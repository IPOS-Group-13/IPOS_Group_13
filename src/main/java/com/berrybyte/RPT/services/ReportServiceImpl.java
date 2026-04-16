package com.berrybyte.RPT.services;

import com.berrybyte.RPT.model.*;
import com.berrybyte.RPT.repository.ReportRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        if (reportRepository == null) {
            throw new IllegalArgumentException("ReportRepository must not be null.");
        }
        this.reportRepository = reportRepository;
    }

    @Override
    public LowStockReport generateLowStockReport() {
        List<LowStockItem> items = reportRepository.findLowStockItems();

        return new LowStockReport(
                "Low Stock Report",
                LocalDateTime.now(),
                items
        );
    }

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

    private void validateMerchantId(int merchantId) {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Merchant ID must be greater than zero.");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }
    }
}