package com.teesolutions.ipospu.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.Properties;


public final class SmtpOutboxDispatcher {
    private static final SmtpOutboxDispatcher INSTANCE = new SmtpOutboxDispatcher();

    private SmtpOutboxDispatcher() {
    }

    public static SmtpOutboxDispatcher getInstance() {
        return INSTANCE;
    }

    
    public SmtpDispatchOutcome dispatchIfEnabled(String to, String subject, String body) {
        MailConfig cfg = MailConfig.load();
        if (!cfg.smtpEnabled()) {
            return SmtpDispatchOutcome.disabled();
        }
        if (!cfg.readyToSend()) {
            String hint =
                    "need mail.smtp.host, mail.from.address, and mail.smtp.password when username is set";
            System.err.println("[mail] mail.smtp.enabled=true but send was skipped: " + hint);
            return SmtpDispatchOutcome.skippedBadConfig(hint);
        }
        try {
            send(cfg, to, subject, body);
            System.out.println("[mail] SMTP sent OK to " + to);
            return SmtpDispatchOutcome.success();
        } catch (Exception e) {
            String shortMsg = shortExceptionMessage(e);
            System.err.println("[mail] SMTP send failed (message still in email_outbox): " + shortMsg);
            e.printStackTrace();
            return SmtpDispatchOutcome.failed(shortMsg);
        }
    }

    private static String shortExceptionMessage(Exception e) {
        String m = e.getMessage();
        if (m != null && !m.isBlank()) {
            return m.trim();
        }
        for (Throwable c = e.getCause(); c != null; c = c.getCause()) {
            String cm = c.getMessage();
            if (cm != null && !cm.isBlank()) {
                return cm.trim();
            }
        }
        return e.getClass().getSimpleName();
    }

    private static void send(MailConfig cfg, String to, String subject, String body) throws Exception {
        Properties props = buildSessionProperties(cfg);

        boolean auth = cfg.username() != null && !cfg.username().isBlank();
        Session session;
        if (auth) {
            final String user = cfg.username();
            final String pass = cfg.password() != null ? cfg.password() : "";
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(cfg.fromAddress(), false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        message.setText(body, StandardCharsets.UTF_8.name());

        Transport.send(message);
    }

    private static Properties buildSessionProperties(MailConfig cfg) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", cfg.host());
        props.put("mail.smtp.port", String.valueOf(cfg.port()));
        props.put("mail.smtp.starttls.enable", String.valueOf(cfg.startTls()));
        if (cfg.startTls()) {
            props.put("mail.smtp.starttls.required", "true");
        }
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        String trust = cfg.effectiveSslTrustOrNull();
        if (trust != null && !trust.isBlank()) {
            props.put("mail.smtp.ssl.trust", trust);
        }
        props.put("mail.smtp.connectiontimeout", "20000");
        props.put("mail.smtp.timeout", "20000");
        boolean auth = cfg.username() != null && !cfg.username().isBlank();
        props.put("mail.smtp.auth", String.valueOf(auth));
        return props;
    }
}
