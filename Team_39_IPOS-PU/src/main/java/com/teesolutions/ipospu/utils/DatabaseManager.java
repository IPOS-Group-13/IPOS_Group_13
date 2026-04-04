package com.teesolutions.ipospu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {
    private enum InitMode {
        LOCAL,
        SHARED
    }

    private static final String DB_LOCAL_PROPERTIES_RESOURCE = "db.properties.local";
    private static final String DB_URL_DEFAULT =
            "jdbc:mysql://localhost:3306/ipos_pu?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String DB_USERNAME_DEFAULT = "root";
    private static final String DB_PASSWORD_DEFAULT = "root";

    /**
     * Schema/bootstrap runs once per JVM. After that, each {@link #getConnection()} opens its own
     * {@link Connection} so background threads and the JavaFX thread never share one connection
     * (MySQL would close the other thread's {@link java.sql.ResultSet}).
     */
    private static final Object INIT_LOCK = new Object();
    private static boolean initialized = false;

    private DatabaseManager() {
    }

    public static Connection getConnection() {
        try {
            DbConfig dbConfig = resolveDbConfig();
            String dbUrl = dbConfig.url();
            Connection conn;
            if (dbUrl.startsWith("jdbc:sqlite:")) {
                conn = DriverManager.getConnection(dbUrl);
            } else {
                conn = DriverManager.getConnection(dbUrl, dbConfig.username(), dbConfig.password());
            }

            synchronized (INIT_LOCK) {
                if (!initialized) {
                    System.out.println("Database connection established: " + dbUrl);
                    initializeDatabaseOnce(conn, dbConfig);
                    initialized = true;
                }
            }
            return conn;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to database", e);
        }
    }

    private static void initializeDatabaseOnce(Connection conn, DbConfig dbConfig) throws SQLException {
        if (dbConfig.initMode() == InitMode.SHARED) {
            System.out.println("Database init mode: SHARED (schema/seed skipped).");
            return;
        }
        runSqlScript(conn, "db/schema.sql");
        ensureLoginAliasColumn(conn);
        runSqlScript(conn, "db/seed.sql");
        System.out.println("Database init mode: LOCAL (schema/seed applied).");
    }

    /**
     * Older databases created before IPOS_SampleData_2026 alignment may lack {@code login_alias}.
     * Safe to run repeatedly (ignored if column already exists).
     */
    private static void ensureLoginAliasColumn(Connection conn) {
        try (Statement statement = conn.createStatement()) {
            statement.execute("ALTER TABLE users ADD COLUMN login_alias VARCHAR(64) NULL UNIQUE");
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (e.getErrorCode() == 1060) {
                return;
            }
            if (msg != null && (msg.contains("Duplicate column")
                    || msg.contains("duplicate column name")
                    || msg.contains("already exists"))) {
                return;
            }
            throw new IllegalStateException("Failed to ensure users.login_alias column", e);
        }
    }

    private static void runSqlScript(Connection conn, String resourcePath) {
        try (InputStream inputStream = getResourceStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("SQL resource not found: " + resourcePath);
            }
            String sql = readAll(inputStream);
            List<String> statements = splitSqlStatements(sql);
            try (Statement statement = conn.createStatement()) {
                for (String sqlStatement : statements) {
                    String trimmed = sqlStatement.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Failed to execute SQL script: " + resourcePath, e);
        }
    }

    private static String readAll(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private static List<String> splitSqlStatements(String sql) {
        String[] parts = sql.split(";");
        List<String> statements = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                statements.add(trimmed);
            }
        }
        return statements;
    }

    /**
     * Repositories close connections via try-with-resources. Nothing global remains open.
     */
    public static void closeConnection() {
        // Intentionally empty: each getConnection() is paired with try-with-resources in callers.
    }

    private static DbConfig resolveDbConfig() {
        Properties localProperties = loadLocalDbProperties();

        // Prefer host / port / name (same fields you type in MySQL Workbench). Use db.url only if those are not set.
        String url = buildJdbcUrlFromHostPortName(localProperties);
        if (url == null || url.isBlank()) {
            url = resolveValueAllowEmpty(localProperties, "db.url", "IPOS_DB_URL", null);
        }
        if (url == null || url.isBlank()) {
            url = DB_URL_DEFAULT;
        }
        String username = resolveValue(localProperties, "db.username", "IPOS_DB_USERNAME", DB_USERNAME_DEFAULT);
        String password = resolveValue(localProperties, "db.password", "IPOS_DB_PASSWORD", DB_PASSWORD_DEFAULT);
        InitMode initMode = resolveInitMode(localProperties, url);
        return new DbConfig(url, username, password, initMode);
    }

    private static InitMode resolveInitMode(Properties properties, String url) {
        String configured = resolveValueAllowEmpty(properties, "db.init.mode", "IPOS_DB_INIT_MODE", null);
        if (configured != null) {
            String normalized = configured.trim().toUpperCase();
            if ("LOCAL".equals(normalized)) {
                return InitMode.LOCAL;
            }
            if ("SHARED".equals(normalized)) {
                return InitMode.SHARED;
            }
            throw new IllegalStateException("Unsupported db.init.mode: " + configured + " (expected LOCAL or SHARED)");
        }
        return isLocalUrl(url) ? InitMode.LOCAL : InitMode.SHARED;
    }

    private static boolean isLocalUrl(String url) {
        if (url == null) {
            return true;
        }
        String normalized = url.trim().toLowerCase();
        return normalized.startsWith("jdbc:sqlite:")
                || normalized.contains("://localhost")
                || normalized.contains("://127.0.0.1")
                || normalized.contains("://0.0.0.0")
                || normalized.contains("://[::1]");
    }

    /**
     * Builds JDBC URL from db.host, db.port, db.name (and optional db.url.params), matching MySQL Workbench.
     * Environment fallbacks: IPOS_DB_HOST, IPOS_DB_PORT, IPOS_DB_NAME, IPOS_DB_URL_PARAMS (semicolons are turned into ampersands for the query string).
     */
    private static String buildJdbcUrlFromHostPortName(Properties properties) {
        String host = firstNonBlank(trimToNull(properties.getProperty("db.host")), trimToNull(System.getenv("IPOS_DB_HOST")));
        String port = firstNonBlank(trimToNull(properties.getProperty("db.port")), trimToNull(System.getenv("IPOS_DB_PORT")));
        String name = firstNonBlank(trimToNull(properties.getProperty("db.name")), trimToNull(System.getenv("IPOS_DB_NAME")));
        if (host == null || port == null || name == null) {
            return null;
        }
        String params = firstNonBlank(trimToNull(properties.getProperty("db.url.params")),
                trimToNull(System.getenv("IPOS_DB_URL_PARAMS")));
        if (params == null) {
            params = "allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        } else {
            params = params.replace(';', '&');
        }
        return "jdbc:mysql://" + host + ":" + port + "/" + name + "?" + params;
    }

    private static String firstNonBlank(String a, String b) {
        return a != null ? a : b;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String resolveValueAllowEmpty(Properties properties, String propertyKey, String envKey, String fallback) {
        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return fallback;
    }

    private static Properties loadLocalDbProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getResourceStream(DB_LOCAL_PROPERTIES_RESOURCE)) {
            if (inputStream == null) {
                return properties;
            }
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load resource: " + DB_LOCAL_PROPERTIES_RESOURCE, e);
        }
    }

    private static String resolveValue(Properties properties, String propertyKey, String envKey, String fallback) {
        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return fallback;
    }

    private static InputStream getResourceStream(String resourcePath) {
        InputStream inputStream = DatabaseManager.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream != null) {
            return inputStream;
        }
        return DatabaseManager.class.getResourceAsStream("/" + resourcePath);
    }

    private record DbConfig(String url, String username, String password, InitMode initMode) {
    }
}
