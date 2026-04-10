package com.teesolutions.ipospu.mail;

import com.teesolutions.ipospu.utils.DatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public final class MailConfig {
    private final boolean smtpEnabled;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String fromAddress;
    private final boolean startTls;
    private final String sslTrust;

    private MailConfig(
            boolean smtpEnabled,
            String host,
            int port,
            String username,
            String password,
            String fromAddress,
            boolean startTls,
            String sslTrust) {
        this.smtpEnabled = smtpEnabled;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
        this.startTls = startTls;
        this.sslTrust = sslTrust;
    }

    public static MailConfig load() {
        Properties p = loadClasspathMailDefaults();
        Properties merged = DatabaseManager.getLocalConfigurationProperties();
        copyMailKeysFrom(merged, p);
        DatabaseManager.overlayMailKeysFromDiscoveredDbLocalFiles(p);
        boolean enabled = parseBool(firstNonBlank(
                System.getProperty("mail.smtp.enabled"),
                p.getProperty("mail.smtp.enabled"),
                System.getenv("IPOS_MAIL_SMTP_ENABLED")), false);
        String host = firstNonBlank(p.getProperty("mail.smtp.host"), System.getenv("IPOS_MAIL_SMTP_HOST"));
        int port = parsePort(firstNonBlank(p.getProperty("mail.smtp.port"), System.getenv("IPOS_MAIL_SMTP_PORT")), 587);
        String user = firstNonBlank(p.getProperty("mail.smtp.username"), System.getenv("IPOS_MAIL_SMTP_USERNAME"));
        String pass = firstNonBlank(p.getProperty("mail.smtp.password"), System.getenv("IPOS_MAIL_SMTP_PASSWORD"));
        String from = firstNonBlank(p.getProperty("mail.from.address"), System.getenv("IPOS_MAIL_FROM"));
        boolean startTls = parseBool(firstNonBlank(
                p.getProperty("mail.smtp.starttls.enable"),
                System.getenv("IPOS_MAIL_STARTTLS")), true);
        String trust = firstNonBlank(p.getProperty("mail.smtp.ssl.trust"), System.getenv("IPOS_MAIL_SMTP_SSL_TRUST"));
        if (Boolean.getBoolean("ipospu.log.config")) {
            String rawEnabled = p.getProperty("mail.smtp.enabled");
            System.err.println("[config] mail.smtp.enabled raw='" + rawEnabled + "' -> enabled=" + enabled);
        }
        return new MailConfig(enabled, host, port, user, pass, from, startTls, trust);
    }

    private static Properties loadClasspathMailDefaults() {
        Properties props = new Properties();
        try (InputStream in = MailConfig.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load classpath mail.properties", e);
        }
        return props;
    }

    private static void copyMailKeysFrom(Properties source, Properties target) {
        for (String name : source.stringPropertyNames()) {
            if (name.startsWith("mail.")) {
                target.setProperty(name, source.getProperty(name));
            }
        }
    }

    public boolean smtpEnabled() {
        return smtpEnabled;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String fromAddress() {
        return fromAddress;
    }

    public boolean startTls() {
        return startTls;
    }

    public String sslTrust() {
        return sslTrust;
    }

    
    public boolean readyToSend() {
        if (!smtpEnabled) {
            return false;
        }
        if (host == null || host.isBlank() || fromAddress == null || fromAddress.isBlank()) {
            return false;
        }

        if (username != null && !username.isBlank()
                && (password == null || password.isBlank())) {
            return false;
        }
        return true;
    }

    
    public String effectiveSslTrustOrNull() {
        if (sslTrust != null && !sslTrust.isBlank()) {
            return sslTrust;
        }
        if (host == null) {
            return null;
        }
        String h = host.trim().toLowerCase();
        if (h.contains("gmail.com")) {
            return "smtp.gmail.com";
        }
        return null;
    }

    private static String firstNonBlank(String a, String b) {
        return firstNonBlank(a, b, null);
    }

    private static String firstNonBlank(String a, String b, String c) {
        if (a != null && !a.trim().isEmpty()) {
            return a.trim();
        }
        if (b != null && !b.trim().isEmpty()) {
            return b.trim();
        }
        if (c != null && !c.trim().isEmpty()) {
            return c.trim();
        }
        return null;
    }

    private static boolean parseBool(String raw, boolean defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(raw.trim()) || "1".equals(raw.trim()) || "yes".equalsIgnoreCase(raw.trim());
    }

    private static int parsePort(String raw, int defaultPort) {
        if (raw == null || raw.isBlank()) {
            return defaultPort;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return defaultPort;
        }
    }
}
