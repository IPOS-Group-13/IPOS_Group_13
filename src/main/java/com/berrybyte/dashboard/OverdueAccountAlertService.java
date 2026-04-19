package com.berrybyte.dashboard;

import com.berrybyte.ACC.services.MerchantStatusService;
import com.berrybyte.common.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents overdue account alert service.
 */
public class OverdueAccountAlertService {

    private static final String SOURCE_SYSTEM = "SA";
    private static final String PURPOSE_FIRST_REMINDER = "PAYMENT_OVERDUE_REMINDER";
    private static final String PURPOSE_DEFAULT_NOTICE = "PAYMENT_DEFAULT_NOTICE";
    private static final String FALLBACK_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK);

    private final MerchantStatusService merchantStatusService = new MerchantStatusService();
/**
 * Loads alerts and queue reminders.
 * This method coordinates the main operation for this action.
 *
 * @param today today
 * @return result value
 * @throws Exception when the operation fails
 */

    public List<OverdueAccountAlertRow> loadAlertsAndQueueReminders(LocalDate today) throws Exception {
        LocalDate evaluationDate = today == null ? LocalDate.now() : today;
        merchantStatusService.refreshAllMerchantStatuses(evaluationDate);

        String sql = """
                SELECT i.InvoiceId,
                       i.OrderId,
                       i.MerchantId,
                       ma.CompanyName,
                       ma.IPOSAccountNumber,
                       ma.Address,
                       u.Email AS MerchantEmail,
                       u.PhoneNumber,
                       i.InvoiceDate,
                       i.DueDate,
                       COALESCE(i.OutstandingBalance, i.TotalAmount - COALESCE(i.AmountPaid, 0)) AS OutstandingAmount,
                       DATEDIFF(?, i.DueDate) AS DaysOverdue
                FROM Invoices i
                JOIN MerchantAccounts ma ON ma.MerchantId = i.MerchantId
                JOIN Users u ON u.UserId = ma.UserId
                WHERE i.DueDate < ?
                  AND UPPER(COALESCE(i.PaymentStatus, 'PENDING')) IN ('PENDING', 'PARTIAL')
                  AND COALESCE(i.OutstandingBalance, i.TotalAmount - COALESCE(i.AmountPaid, 0)) > 0
                ORDER BY i.MerchantId ASC, i.DueDate ASC
                """;

        Map<Integer, AggregatedMerchantAlert> aggregatedAlerts = new LinkedHashMap<>();

        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(evaluationDate));
            statement.setDate(2, Date.valueOf(evaluationDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    InvoiceOverdueItem item = mapInvoice(resultSet);
                    queueOverdueEmailIfDue(connection, item, evaluationDate);
                    mergeAlert(aggregatedAlerts, item);
                }
            }
        }

        List<OverdueAccountAlertRow> rows = new ArrayList<>();
        for (AggregatedMerchantAlert aggregate : aggregatedAlerts.values()) {
            rows.add(aggregate.toRow());
        }
        return rows;
    }
/**
 * Performs map invoice.
 * This method coordinates the main operation for this action.
 *
 * @param resultSet result set
 * @return result value
 * @throws Exception when the operation fails
 */

    private InvoiceOverdueItem mapInvoice(ResultSet resultSet) throws Exception {
        return new InvoiceOverdueItem(
                resultSet.getInt("InvoiceId"),
                resultSet.getInt("OrderId"),
                resultSet.getInt("MerchantId"),
                safeText(resultSet.getString("CompanyName")),
                safeText(resultSet.getString("IPOSAccountNumber")),
                safeText(resultSet.getString("Address")),
                safeText(resultSet.getString("MerchantEmail")),
                safeText(resultSet.getString("PhoneNumber")),
                resultSet.getDate("InvoiceDate").toLocalDate(),
                resultSet.getDate("DueDate").toLocalDate(),
                normalizeCurrency(resultSet.getBigDecimal("OutstandingAmount")),
                Math.max(resultSet.getInt("DaysOverdue"), 0));
    }
/**
 * Performs queue overdue email if due.
 * This method coordinates the main operation for this action.
 *
 * @param connection connection
 * @param item item
 * @param today today
 * @throws Exception when the operation fails
 */

    private void queueOverdueEmailIfDue(Connection connection, InvoiceOverdueItem item, LocalDate today) throws Exception {
        if (item.daysOverdue() >= 30) {
            String referenceKey = "SA_OVERDUE_DEFAULT_30_INVOICE_" + item.invoiceId();
            if (!emailAlreadyQueued(connection, referenceKey)) {
                insertQueueEmail(connection,
                        resolveRecipient(item.merchantEmail()),
                        "SECOND REMINDER - INVOICE NO.: " + item.invoiceId() + " (ACCOUNT IN DEFAULT)",
                        buildSecondReminderBody(item, today),
                        PURPOSE_DEFAULT_NOTICE,
                        referenceKey);
            }
            return;
        }

        if (item.daysOverdue() >= 15) {
            String referenceKey = "SA_OVERDUE_REMINDER_15_INVOICE_" + item.invoiceId();
            if (!emailAlreadyQueued(connection, referenceKey)) {
                insertQueueEmail(connection,
                        resolveRecipient(item.merchantEmail()),
                        "REMINDER - INVOICE NO.: " + item.invoiceId(),
                        buildFirstReminderBody(item, today),
                        PURPOSE_FIRST_REMINDER,
                        referenceKey);
            }
        }
    }
/**
 * Performs email already queued.
 * This method coordinates the main operation for this action.
 *
 * @param connection connection
 * @param referenceKey reference key
 * @return result value
 * @throws Exception when the operation fails
 */

    private boolean emailAlreadyQueued(Connection connection, String referenceKey) throws Exception {
        String sql = """
                SELECT 1
                FROM ipos_pu.external_comms_queue
                WHERE reference_key = ?
                LIMIT 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, referenceKey);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
/**
 * Performs insert queue email.
 * This method coordinates the main operation for this action.
 *
 * @param connection connection
 * @param recipientEmail recipient email
 * @param subject subject
 * @param body body
 * @param purpose purpose
 * @param referenceKey reference key
 * @throws Exception when the operation fails
 */

    private void insertQueueEmail(Connection connection,
                                  String recipientEmail,
                                  String subject,
                                  String body,
                                  String purpose,
                                  String referenceKey) throws Exception {
        String sql = """
                INSERT INTO ipos_pu.external_comms_queue
                (recipient_email, subject, body, purpose, source_system, reference_key)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, recipientEmail);
            statement.setString(2, subject);
            statement.setString(3, body);
            statement.setString(4, purpose);
            statement.setString(5, SOURCE_SYSTEM);
            statement.setString(6, referenceKey);
            statement.executeUpdate();
        }
    }
/**
 * Performs build first reminder body.
 *
 * @param item item
 * @param today today
 * @return result value
 */

    private String buildFirstReminderBody(InvoiceOverdueItem item, LocalDate today) {
        return """
                Client: %s
                %s
                Phone: %s

                InfoPharma Ltd.,
                19 High St.,
                Ashford,
                Kent

                %s

                Dear Client,

                REMINDER - INVOICE NO.: %d

                IPOS Account: %s        Total Amount: %.2f

                According to our records, it appears that we have not yet received payment of the above invoice, which was raised against %s on %s, for ordering pharmaceutical goods from InfoPharma Ltd, order IP%d.

                We would appreciate payment at your earliest convenience.

                If you have already sent a payment to us recently, please accept our apologies.

                Yours sincerely,

                A. Petite, Director of Operations, InfoPharma Ltd.
                """
                .formatted(
                        item.companyName(),
                        item.address(),
                        fallbackPhone(item.phoneNumber()),
                        today.format(DATE_FORMAT),
                        item.invoiceId(),
                        item.iposAccountNumber(),
                        item.outstandingAmount().doubleValue(),
                        item.companyName(),
                        item.invoiceDate().format(DATE_FORMAT),
                        item.orderId());
    }
/**
 * Performs build second reminder body.
 *
 * @param item item
 * @param today today
 * @return result value
 */

    private String buildSecondReminderBody(InvoiceOverdueItem item, LocalDate today) {
        LocalDate firstReminderDate = item.dueDate().plusDays(15);

        return """
                Client: %s
                %s
                Phone: %s

                InfoPharma Ltd.,
                19 High St.,
                Ashford,
                Kent

                %s

                Dear Client,

                SECOND REMINDER - INVOICE NO.: %d

                IPOS Account: %s        Total Amount: %.2f

                It appears that we still have not yet received payment of the above invoice, which was raised against %s on %s, for ordering pharmaceutical goods from InfoPharma Ltd, order IP%d, despite the reminder sent to you on %s.

                Your account has now been declared in default due to prolonged non-payment.

                We would appreciate it if you would settle this invoice in full by return.

                If you have already sent a payment to us recently, please accept our apologies.

                Yours sincerely,

                A. Petite, Director of Operations, InfoPharma Ltd.
                """
                .formatted(
                        item.companyName(),
                        item.address(),
                        fallbackPhone(item.phoneNumber()),
                        today.format(DATE_FORMAT),
                        item.invoiceId(),
                        item.iposAccountNumber(),
                        item.outstandingAmount().doubleValue(),
                        item.companyName(),
                        item.invoiceDate().format(DATE_FORMAT),
                        item.orderId(),
                        firstReminderDate.format(DATE_FORMAT));
    }
/**
 * Performs merge alert.
 *
 * @param map map
 * @param item item
 */

    private void mergeAlert(Map<Integer, AggregatedMerchantAlert> map, InvoiceOverdueItem item) {
        AggregatedMerchantAlert aggregate = map.computeIfAbsent(item.merchantId(), unused ->
                new AggregatedMerchantAlert(
                        item.merchantId(),
                        item.companyName(),
                        item.iposAccountNumber(),
                        item.dueDate(),
                        item.daysOverdue()));

        aggregate.addOutstanding(item.outstandingAmount());
        aggregate.updateOldestDueDate(item.dueDate());
        aggregate.updateMaxDaysOverdue(item.daysOverdue());
    }
/**
 * Performs resolve recipient.
 *
 * @param merchantEmail merchant email
 * @return result value
 */

    private String resolveRecipient(String merchantEmail) {
        if (merchantEmail != null && !merchantEmail.isBlank()) {
            return merchantEmail.trim();
        }
        return FALLBACK_RECIPIENT_EMAIL;
    }
/**
 * Performs fallback phone.
 *
 * @param phoneNumber phone number
 * @return result value
 */

    private String fallbackPhone(String phoneNumber) {
        return (phoneNumber == null || phoneNumber.isBlank()) ? "N/A" : phoneNumber;
    }
/**
 * Performs safe text.
 *
 * @param value value
 * @return result value
 */

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
/**
 * Performs normalize currency.
 *
 * @param amount amount
 * @return result value
 */

    private BigDecimal normalizeCurrency(BigDecimal amount) {
        return (amount == null ? BigDecimal.ZERO : amount).setScale(2, RoundingMode.HALF_UP);
    }

    private static class AggregatedMerchantAlert {
        private final int merchantId;
        private final String companyName;
        private final String iposAccountNumber;
        private LocalDate oldestDueDate;
        private int maxDaysOverdue;
        private BigDecimal overdueAmount;
/**
 * Performs aggregated merchant alert.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param companyName company name
 * @param iposAccountNumber ipos account number
 * @param dueDate due date
 * @param daysOverdue days overdue
 * @return result value
 */

        private AggregatedMerchantAlert(int merchantId,
                                        String companyName,
                                        String iposAccountNumber,
                                        LocalDate dueDate,
                                        int daysOverdue) {
            this.merchantId = merchantId;
            this.companyName = companyName;
            this.iposAccountNumber = iposAccountNumber;
            this.oldestDueDate = dueDate;
            this.maxDaysOverdue = daysOverdue;
            this.overdueAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
/**
 * Performs add outstanding.
 *
 * @param amount amount
 */

        private void addOutstanding(BigDecimal amount) {
            overdueAmount = overdueAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
        }
/**
 * Executes the update oldest due date workflow.
 * This method coordinates the main operation for this action.
 *
 * @param dueDate due date
 */

        private void updateOldestDueDate(LocalDate dueDate) {
            if (dueDate != null && (oldestDueDate == null || dueDate.isBefore(oldestDueDate))) {
                oldestDueDate = dueDate;
            }
        }
/**
 * Executes the update max days overdue workflow.
 * This method coordinates the main operation for this action.
 *
 * @param daysOverdue days overdue
 */

        private void updateMaxDaysOverdue(int daysOverdue) {
            if (daysOverdue > maxDaysOverdue) {
                maxDaysOverdue = daysOverdue;
            }
        }
/**
 * Performs to row.
 *
 * @return result value
 */

        private OverdueAccountAlertRow toRow() {
            String alertLevel = maxDaysOverdue >= 30 ? "DEFAULT" : (maxDaysOverdue >= 15 ? "REMINDER" : "OVERDUE");
            String accountState = maxDaysOverdue >= 30 ? "IN DEFAULT" : "OVERDUE";
            return new OverdueAccountAlertRow(
                    merchantId,
                    companyName,
                    iposAccountNumber,
                    oldestDueDate,
                    maxDaysOverdue,
                    overdueAmount,
                    accountState,
                    alertLevel);
        }
    }

/**
 * Represents immutable data for invoice overdue item.
 */
    private record InvoiceOverdueItem(int invoiceId,
                                      int orderId,
                                      int merchantId,
                                      String companyName,
                                      String iposAccountNumber,
                                      String address,
                                      String merchantEmail,
                                      String phoneNumber,
                                      LocalDate invoiceDate,
                                      LocalDate dueDate,
                                      BigDecimal outstandingAmount,
                                      int daysOverdue) { }
}
