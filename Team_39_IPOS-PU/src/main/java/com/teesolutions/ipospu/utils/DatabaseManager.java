package com.teesolutions.ipospu.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicBoolean;

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
            ensureExternalCommsQueueTable(conn, dbConfig.url());
            runSqlScript(conn, "db/shared_align_sample_data.sql");
            System.out.println("Database init mode: SHARED (sample email/password alignment applied).");
            return;
        }
        runSqlScript(conn, "db/schema.sql");
        ensureLoginAliasColumn(conn);
        runSqlScript(conn, "db/seed.sql");
        System.out.println("Database init mode: LOCAL (schema/seed applied).");
    }

    
    private static final AtomicBoolean LOGGED_EXT_COMMS_ENSURE_FAIL = new AtomicBoolean();
    private static final AtomicBoolean LOGGED_CODE_SOURCE_CONFIG_FAIL = new AtomicBoolean();

    
    private static void ensureExternalCommsQueueTable(Connection conn, String jdbcUrl) {
        if (jdbcUrl != null && jdbcUrl.trim().toLowerCase().startsWith("jdbc:sqlite:")) {
            return;
        }
        String ddl = "CREATE TABLE IF NOT EXISTS external_comms_queue ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "recipient_email VARCHAR(255) NOT NULL,"
                + "subject VARCHAR(255) NOT NULL,"
                + "body MEDIUMTEXT NOT NULL,"
                + "purpose VARCHAR(64) NOT NULL,"
                + "source_system VARCHAR(16) NOT NULL,"
                + "reference_key VARCHAR(128) NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "consumed_at TIMESTAMP NULL DEFAULT NULL,"
                + "KEY idx_ext_comms_pending (consumed_at),"
                + "KEY idx_ext_comms_source_ref (source_system, reference_key)"
                + ")";
        try (Statement st = conn.createStatement()) {
            st.execute(ddl);
            System.out.println("Ensured table external_comms_queue exists.");
        } catch (SQLException e) {
            if (LOGGED_EXT_COMMS_ENSURE_FAIL.compareAndSet(false, true)) {
                System.err.println(
                        "[PU] Could not auto-create external_comms_queue: " + e.getMessage()
                                + " — run docs/sql/pu_external_comms_queue.sql as DBA if the queue is required.");
            }
        }
    }

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

    
    public static void closeConnection() {

    }

    private static DbConfig resolveDbConfig() {
        Properties localProperties = loadLocalDbProperties();


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
        return getLocalConfigurationProperties();
    }

    
    public static Properties getLocalConfigurationProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getResourceStream(DB_LOCAL_PROPERTIES_RESOURCE)) {
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load resource: " + DB_LOCAL_PROPERTIES_RESOURCE, e);
        }
        mergeDiscoveredLocalConfigFiles(properties);
        return properties;
    }

    
    public static void overlayMailKeysFromDiscoveredDbLocalFiles(Properties properties) {
        for (Path path : orderedDbPropertiesLocalFilePaths()) {
            overlayMailKeysFromPath(properties, path);
        }
    }

    
    private static void mergeDiscoveredLocalConfigFiles(Properties properties) {
        for (Path path : orderedDbPropertiesLocalFilePaths()) {
            mergePropertiesFromPath(properties, path);
        }
    }

    
    private static List<Path> orderedDbPropertiesLocalFilePaths() {
        List<Path> paths = new ArrayList<>();
        Path cur = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        List<Path> fromCwdToRoot = new ArrayList<>();
        for (int depth = 0; depth < 16 && cur != null; depth++) {
            fromCwdToRoot.add(cur);
            cur = cur.getParent();
        }
        List<Path> rootToCwd = new ArrayList<>(fromCwdToRoot);
        Collections.reverse(rootToCwd);
        for (Path base : rootToCwd) {
            paths.add(base.resolve("db.properties.local"));
            paths.add(base.resolve("src/main/resources/db.properties.local"));
            paths.add(base.resolve("IPOS-PU/db.properties.local"));
            paths.add(base.resolve("IPOS-PU/src/main/resources/db.properties.local"));
        }
        paths.addAll(codeSourceAncestorDbLocalPaths());
        String envDir = System.getenv("IPOS_PU_CONFIG_DIR");
        if (envDir != null && !envDir.isBlank()) {
            paths.add(Paths.get(envDir.trim()).resolve("db.properties.local"));
        }
        String sysDir = System.getProperty("ipospu.config.dir");
        if (sysDir != null && !sysDir.isBlank()) {
            paths.add(Paths.get(sysDir.trim()).resolve("db.properties.local"));
        }
        paths.addAll(pathsFromPackagedDbPropertiesInfoMarker());
        addClasspathExplodedOutputDirPropertyPaths(paths);
        return paths;
    }

    
    private static void addClasspathExplodedOutputDirPropertyPaths(List<Path> paths) {
        String cp = System.getProperty("java.class.path");
        if (cp == null || cp.isBlank()) {
            return;
        }
        for (String entry : cp.split(Pattern.quote(File.pathSeparator))) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            Path dir = Paths.get(entry).toAbsolutePath().normalize();
            if (!Files.isDirectory(dir)) {
                continue;
            }
            Path beside = dir.resolve("db.properties.local").normalize();
            if (Files.isRegularFile(beside)) {
                paths.add(beside);
            }
            Path walk = dir;
            for (int i = 0; i < 14 && walk != null; i++) {
                Path candidate = walk.resolve("src/main/resources/db.properties.local").normalize();
                if (Files.isRegularFile(candidate)) {
                    paths.add(candidate);
                    break;
                }
                walk = walk.getParent();
            }
        }
    }

    
    private static List<Path> pathsFromPackagedDbPropertiesInfoMarker() {
        List<Path> list = new ArrayList<>();
        URL u = DatabaseManager.class.getClassLoader().getResource("db.properties.info");
        if (u == null) {
            u = DatabaseManager.class.getResource("/db.properties.info");
        }
        Path infoPath = fileUrlToPath(u);
        if (infoPath == null || !Files.isRegularFile(infoPath)) {
            return list;
        }
        Path outputDir = infoPath.getParent();
        if (outputDir == null) {
            return list;
        }
        Path inOutputTree = outputDir.resolve("db.properties.local").normalize();
        list.add(inOutputTree);
        Path walk = outputDir;
        for (int i = 0; i < 12 && walk != null; i++) {
            Path candidate = walk.resolve("src/main/resources/db.properties.local").normalize();
            if (Files.isRegularFile(candidate)) {
                list.add(candidate);
                break;
            }
            walk = walk.getParent();
        }
        return list;
    }

    private static List<Path> codeSourceAncestorDbLocalPaths() {
        List<Path> paths = new ArrayList<>();
        Path codePath = resolveDatabaseManagerCodePathOrNull();
        if (codePath == null || !Files.exists(codePath)) {
            return paths;
        }
        if (Files.isRegularFile(codePath)) {
            Path jarDir = codePath.getParent();
            if (jarDir != null) {
                paths.add(jarDir.resolve("db.properties.local"));
            }
            return paths;
        }
        List<Path> fromCodeDirToRoot = new ArrayList<>();
        Path c = codePath.toAbsolutePath().normalize();
        for (int depth = 0; depth < 16 && c != null; depth++) {
            fromCodeDirToRoot.add(c);
            c = c.getParent();
        }
        List<Path> rootToCodeDir = new ArrayList<>(fromCodeDirToRoot);
        Collections.reverse(rootToCodeDir);
        for (Path base : rootToCodeDir) {
            paths.add(base.resolve("src/main/resources/db.properties.local"));
            paths.add(base.resolve("db.properties.local"));
        }
        return paths;
    }

    private static Path resolveDatabaseManagerCodePathOrNull() {
        try {
            CodeSource cs = DatabaseManager.class.getProtectionDomain().getCodeSource();
            if (cs == null || cs.getLocation() == null) {
                return null;
            }
            return fileUrlToPath(cs.getLocation());
        } catch (SecurityException e) {
            if (LOGGED_CODE_SOURCE_CONFIG_FAIL.compareAndSet(false, true)) {
                System.err.println("[config] CodeSource blocked for db.properties.local discovery: " + e.getMessage());
            }
            return null;
        }
    }

    
    private static Path fileUrlToPath(URL loc) {
        if (loc == null || !"file".equalsIgnoreCase(loc.getProtocol())) {
            return null;
        }
        try {
            URI uri = loc.toURI();
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                Path p = Paths.get(uri);
                if (Files.exists(p)) {
                    return p;
                }
            }
        } catch (URISyntaxException | IllegalArgumentException ignored) {

        }
        try {
            String p = loc.getPath();
            if (p == null || p.isEmpty()) {
                return null;
            }
            p = URLDecoder.decode(p, StandardCharsets.UTF_8);
            if (p.startsWith("/") && p.length() >= 3 && p.charAt(2) == ':') {
                p = p.substring(1);
            }
            Path path = Paths.get(p);
            return Files.exists(path) ? path : null;
        } catch (Exception e) {
            if (LOGGED_CODE_SOURCE_CONFIG_FAIL.compareAndSet(false, true)) {
                System.err.println("[config] Could not resolve CodeSource URL for config: " + loc + " — " + e.getMessage());
            }
            return null;
        }
    }

    private static void overlayMailKeysFromPath(Properties target, Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
                text = text.substring(1);
            }
            Properties chunk = new Properties();
            try (StringReader reader = new StringReader(text)) {
                chunk.load(reader);
            }
            for (String name : chunk.stringPropertyNames()) {
                if (name != null && name.startsWith("mail.")) {
                    target.setProperty(name, chunk.getProperty(name));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load mail overlay from " + path.toAbsolutePath(), e);
        }
    }

    private static void mergePropertiesFromPath(Properties properties, Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
                text = text.substring(1);
            }
            try (StringReader reader = new StringReader(text)) {
                properties.load(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + path.toAbsolutePath(), e);
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
