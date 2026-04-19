package com.berrybyte.RPT.services;

import com.berrybyte.RPT.model.*;
import com.berrybyte.RPT.repository.ReportRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents report service impl.
 */
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
/**
 * Creates a new ReportServiceImpl instance.
 *
 * @param reportRepository report repository
 */

    public ReportServiceImpl(ReportRepository reportRepository) {
        if (reportRepository == null) {
            throw new IllegalArgumentException("ReportRepository must not be null.");
        }
        this.reportRepository = reportRepository;
    }

/**
 * Executes the generate low stock report workflow.
 * This method coordinates the main operation for this action.
 *
 * @return result value
 */
    @Override
    public LowStockReport generateLowStockReport() {
        List<LowStockItem> items = reportRepository.findLowStockItems();

        return new LowStockReport(
                "Low Stock Report",
                LocalDateTime.now(),
                items
        );
    }

/**
 * Executes the generate merchant order summary workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public MerchantOrderSummaryReport generateMerchantOrderSummary(int merchantId,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate) {
        validateMerchantId(merchantId);
        validateDateRange(startDate, endDate);

        List<MerchantOrderSummaryRow> rows =
                reportRepository.findMerchantOrderSummary(merchantId, startDate, endDate);

        BigDecimal totalOrderValue = rows.stream()
                .map(MerchantOrderSummaryRow::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MerchantOrderSummaryReport(
                "Merchant Orders Summary",
                merchantId,
                startDate,
                endDate,
                LocalDateTime.now(),
                rows,
                rows.size(),
                totalOrderValue
        );
    }

/**
 * Executes the generate invoice listing workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public InvoiceListingReport generateInvoiceListing(Integer merchantId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) {
        if (merchantId != null) {
            validateMerchantId(merchantId);
        }
        validateDateRange(startDate, endDate);

        List<InvoiceListingRow> rows =
                reportRepository.findInvoiceListing(merchantId, startDate, endDate);

        BigDecimal totalInvoiced = rows.stream()
                .map(InvoiceListingRow::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = rows.stream()
                .map(InvoiceListingRow::getOutstandingBalance)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InvoiceListingReport(
                "Invoice Listing Report",
                merchantId,
                startDate,
                endDate,
                LocalDateTime.now(),
                rows,
                rows.size(),
                totalInvoiced,
                totalOutstanding
        );
    }

/**
 * Executes the generate stock turnover workflow.
 * This method coordinates the main operation for this action.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public StockTurnoverReport generateStockTurnover(LocalDate startDate,
                                                     LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<StockTurnoverRow> rows = reportRepository.findStockTurnover(startDate, endDate);

        int totalQuantitySold = rows.stream()
                .mapToInt(StockTurnoverRow::getQuantitySold)
                .sum();

        BigDecimal totalSalesValue = rows.stream()
                .map(StockTurnoverRow::getSalesValue)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new StockTurnoverReport(
                "Stock Turnover Report",
                startDate,
                endDate,
                LocalDateTime.now(),
                rows,
                totalQuantitySold,
                totalSalesValue
        );
    }

/**
 * Executes the generate merchant activity report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public MerchantActivityReport generateMerchantActivityReport(int merchantId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        validateMerchantId(merchantId);
        validateDateRange(startDate, endDate);

        MerchantActivityReport baseReport =
                reportRepository.findMerchantActivityReport(merchantId, startDate, endDate);

        int totalOrders = baseReport.getOrders().size();

        BigDecimal totalOrderValue = baseReport.getOrders().stream()
                .map(MerchantActivityOrderSection::getOrderTotal)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MerchantActivityReport(
                baseReport.getTitle(),
                baseReport.getMerchantId(),
                baseReport.getCompanyName(),
                baseReport.getIposAccountNumber(),
                baseReport.getAddress(),
                baseReport.getStartDate(),
                baseReport.getEndDate(),
                baseReport.getGeneratedAt(),
                baseReport.getOrders(),
                totalOrders,
                totalOrderValue
        );
    }

/**
 * Executes the generate info pharma turnover report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public InfoPharmaTurnoverReport generateInfoPharmaTurnoverReport(LocalDate startDate,
                                                                     LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<InfoPharmaTurnoverRow> rows = reportRepository.findInfoPharmaTurnover(startDate, endDate);

        BigDecimal totalInvoicedAmount = rows.stream()
                .map(InfoPharmaTurnoverRow::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaidAmount = rows.stream()
                .map(InfoPharmaTurnoverRow::getAmountPaid)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstandingAmount = rows.stream()
                .map(InfoPharmaTurnoverRow::getOutstandingBalance)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int paidInvoiceCount = (int) rows.stream()
                .filter(row -> row.getPaymentStatus() != null && row.getPaymentStatus().equalsIgnoreCase("PAID"))
                .count();

        int unpaidInvoiceCount = (int) rows.stream()
                .filter(row -> row.getPaymentStatus() != null && row.getPaymentStatus().equalsIgnoreCase("UNPAID"))
                .count();

        int partialInvoiceCount = (int) rows.stream()
                .filter(row -> row.getPaymentStatus() != null && row.getPaymentStatus().equalsIgnoreCase("PARTIAL"))
                .count();

        return new InfoPharmaTurnoverReport(
                "InfoPharma Turnover Report",
                startDate,
                endDate,
                LocalDateTime.now(),
                rows,
                rows.size(),
                totalInvoicedAmount,
                totalPaidAmount,
                totalOutstandingAmount,
                paidInvoiceCount,
                unpaidInvoiceCount,
                partialInvoiceCount
        );
    }
/**
 * Executes the validate merchant id workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 */

    private void validateMerchantId(int merchantId) {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Merchant ID must be greater than zero.");
        }
    }
/**
 * Executes the validate date range workflow.
 * This method coordinates the main operation for this action.
 *
 * @param startDate start date
 * @param endDate end date
 */

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }
    }
/**
 * Executes the generate overdue balance report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param merchantName merchant name
 * @return result value
 */
    @Override
    public OverdueBalanceReport generateOverdueBalanceReport(Integer merchantId, String merchantName) {
        if (merchantId != null) {
            validateMerchantId(merchantId);
        }

        List<OverdueBalanceRow> rows = reportRepository.findOverdueBalanceReport(merchantId);

        BigDecimal totalOverdueAmount = rows.stream()
                .map(OverdueBalanceRow::getTotalOverdueAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String title = merchantId == null
                ? "Overdue Balance Report"
                : "Overdue Balance Report - " + (merchantName == null || merchantName.isBlank() ? "Selected Merchant" : merchantName);

        return new OverdueBalanceReport(
                title,
                merchantId,
                merchantName,
                LocalDateTime.now(),
                rows,
                rows.size(),
                totalOverdueAmount
        );
    }
}
