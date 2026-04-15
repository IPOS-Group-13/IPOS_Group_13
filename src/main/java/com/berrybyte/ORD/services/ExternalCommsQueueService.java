package com.berrybyte.ORD.services;

import com.berrybyte.ORD.helpers.InvoiceDetails;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ExternalCommsQueueService {

    private static final String PURPOSE_ORDER_ACCEPTED = "ORDER_ACCEPTED";
    private static final String SOURCE_SYSTEM = "SA";

    public String queueOrderAcceptedEmail(InvoiceDetails invoiceDetails, String invoiceUrl) throws Exception {
        if (invoiceDetails == null) {
            throw new IllegalArgumentException("Invoice details are required.");
        }
        if (invoiceUrl == null || invoiceUrl.isBlank()) {
            throw new IllegalArgumentException("Invoice URL is required.");
        }

        String sql = """
                INSERT INTO ipos_pu.external_comms_queue
                (recipient_email, subject, body, purpose, source_system, reference_key)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String recipientEmail = resolveRecipientEmail();
        String subject = buildSubject(invoiceDetails);
        String body = buildBody(invoiceDetails, invoiceUrl);
        String referenceKey = buildReferenceKey(invoiceDetails);

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipientEmail);
            ps.setString(2, subject);
            ps.setString(3, body);
            ps.setString(4, PURPOSE_ORDER_ACCEPTED);
            ps.setString(5, SOURCE_SYSTEM);
            ps.setString(6, referenceKey);
            ps.executeUpdate();
        }

        return recipientEmail;
    }

    private String resolveRecipientEmail() {
        String merchantEmail =  "ipos_commercial@yahoo.com";
        if (merchantEmail == null || merchantEmail.isBlank()) {
            throw new IllegalArgumentException("Merchant email is not available for this invoice.");
        }
        return merchantEmail.trim();
    }

    private String buildSubject(InvoiceDetails invoiceDetails) {
        return "Order Accepted - Invoice Ready for Order #" + invoiceDetails.getOrderId();
    }

    private String buildBody(InvoiceDetails invoiceDetails, String invoiceUrl) {
        return """
                Hello,

                Your order has been accepted by IPOS-SA and an invoice has been generated.

                Order ID: %d
                Invoice ID: %d
                IPOS Account Number: %s
                Merchant Company: %s
                Invoice Date: %s
                Due Date: %s
                Total Amount: GBP %.2f
                Outstanding Balance: GBP %.2f
                Payment Status: %s

                Invoice download link:
                %s
                """
                .formatted(
                        invoiceDetails.getOrderId(),
                        invoiceDetails.getInvoiceId(),
                        safeValue(invoiceDetails.getIposAccountNumber()),
                        safeValue(invoiceDetails.getCompanyName()),
                        safeValue(invoiceDetails.getInvoiceDate()),
                        safeValue(invoiceDetails.getDueDate()),
                        invoiceDetails.getTotalAmount(),
                        invoiceDetails.getOutstandingBalance(),
                        safeValue(invoiceDetails.getPaymentStatus()),
                        invoiceUrl
                );
    }

    private String buildReferenceKey(InvoiceDetails invoiceDetails) {
        return "SA_ORDER_" + invoiceDetails.getOrderId() + "_INVOICE_" + invoiceDetails.getInvoiceId() + "_ACCEPTED";
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }
}
