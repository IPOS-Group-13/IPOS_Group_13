package com.berrybyte.RPT.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ReportEmailServiceImpl implements ReportEmailService {

    private final Properties mailProperties = new Properties();

    public ReportEmailServiceImpl() {
        loadMailProperties();
    }

    @Override
    public void sendReportEmail(String recipientEmail,
                                String subject,
                                String body,
                                Path attachmentPath) throws Exception {

        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required.");
        }

        if (attachmentPath == null || !Files.exists(attachmentPath)) {
            throw new IllegalArgumentException("Attachment file does not exist.");
        }

        final String username = mailProperties.getProperty("mail.username");
        final String password = mailProperties.getProperty("mail.password");

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachmentPath.toFile());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private void loadMailProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mail.properties.local")) {
            if (inputStream == null) {
                throw new RuntimeException("mail.properties.local file not found in resources folder.");
            }
            mailProperties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mail properties.", e);
        }
    }
}