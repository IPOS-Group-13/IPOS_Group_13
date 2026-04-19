package com.berrybyte.RPT.email;

import java.nio.file.Path;

/**
 * Defines the contract for report email service.
 */
public interface ReportEmailService {
/**
 * Executes the send report email workflow.
 * This method coordinates the main operation for this action.
 *
 * @param recipientEmail recipient email
 * @param subject subject
 * @param body body
 * @param attachmentPath attachment path
 * @throws Exception when the operation fails
 */
    void sendReportEmail(String recipientEmail,
                         String subject,
                         String body,
                         Path attachmentPath) throws Exception;
}
