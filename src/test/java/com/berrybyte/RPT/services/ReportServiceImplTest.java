package com.berrybyte.RPT.services;

import com.berrybyte.RPT.model.*;
import com.berrybyte.RPT.repository.ReportRepository;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceImplTest {

    @Test
    void generateLowStockReport_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        LowStockReport report = service.generateLowStockReport();

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getItems());
    }

    @Test
    void generateMerchantOrderSummary_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        MerchantOrderSummaryReport report = service.generateMerchantOrderSummary(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getRows());
    }

    @Test
    void generateInvoiceListing_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        InvoiceListingReport report = service.generateInvoiceListing(
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getRows());
    }

    @Test
    void generateStockTurnover_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        StockTurnoverReport report = service.generateStockTurnover(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getRows());
    }

    @Test
    void generateMerchantActivityReport_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        MerchantActivityReport report = service.generateMerchantActivityReport(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getOrders());
    }

}