package com.berrybyte.RPT.services;

import com.berrybyte.RPT.ReportIntegrationTestSupport;
import com.berrybyte.RPT.model.*;
import com.berrybyte.RPT.model.MerchantOption;
import com.berrybyte.RPT.repository.ReportRepository;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import org.junit.jupiter.api.Test;

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
        MerchantOption merchant = ReportIntegrationTestSupport.requireActiveMerchant(repository);

        MerchantOrderSummaryReport report = service.generateMerchantOrderSummary(
                merchant.getMerchantId(),
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getRows());
        assertEquals(merchant.getMerchantId(), report.getMerchantId());
    }

    @Test
    void generateInvoiceListing_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService service = new ReportServiceImpl(repository);

        InvoiceListingReport report = service.generateInvoiceListing(
                null,
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
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
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
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
        MerchantOption merchant = ReportIntegrationTestSupport.requireActiveMerchant(repository);

        MerchantActivityReport report = service.generateMerchantActivityReport(
                merchant.getMerchantId(),
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getGeneratedAt());
        assertNotNull(report.getOrders());
        assertEquals(merchant.getMerchantId(), report.getMerchantId());
        assertEquals(merchant.getCompanyName(), report.getCompanyName());
    }

}
