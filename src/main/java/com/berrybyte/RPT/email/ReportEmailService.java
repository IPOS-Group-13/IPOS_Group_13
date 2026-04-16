package com.berrybyte.RPT.email;

import java.nio.file.Path;

public interface ReportEmailService {
    void sendReportEmail(String recipientEmail,
                         String subject,
                         String body,
                         Path attachmentPath) throws Exception;
}