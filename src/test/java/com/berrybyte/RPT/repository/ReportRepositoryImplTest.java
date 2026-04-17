package com.berrybyte.RPT.repository;

import com.berrybyte.RPT.ReportIntegrationTestSupport;
import com.berrybyte.RPT.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportRepositoryImplTest {

    @Test
    void findLowStockItems_returnsListSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();

        List<LowStockItem> items = repository.findLowStockItems();

        assertNotNull(items);

        for (LowStockItem item : items) {
            assertTrue(item.getAvailabilityPacks() < item.getStockLimitPacks());
            assertNotNull(item.getDescription());
        }
    }

    @Test
    void findMerchantOrderSummary_returnsListSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        MerchantOption merchant = ReportIntegrationTestSupport.requireActiveMerchant(repository);

        List<MerchantOrderSummaryRow> rows = repository.findMerchantOrderSummary(
                merchant.getMerchantId(),
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(rows);

        for (MerchantOrderSummaryRow row : rows) {
            assertTrue(row.getOrderId() > 0);
            assertNotNull(row.getOrderDate());
            assertNotNull(row.getTotalAmount());
            assertNotNull(row.getOrderStatus());
        }
    }
    @Test
    void findInvoiceListing_returnsListSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();

        List<InvoiceListingRow> rows = repository.findInvoiceListing(
                null,
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(rows);

        for (InvoiceListingRow row : rows) {
            assertTrue(row.getInvoiceId() > 0);
            assertNotNull(row.getInvoiceDate());
            assertNotNull(row.getPaymentStatus());
        }
    }

    @Test
    void findStockTurnover_returnsListSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();

        List<StockTurnoverRow> rows = repository.findStockTurnover(
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(rows);

        for (StockTurnoverRow row : rows) {
            assertTrue(row.getItemId() > 0);
            assertNotNull(row.getDescription());
            assertTrue(row.getQuantitySold() >= 0);
            assertNotNull(row.getSalesValue());
        }
    }

    @Test
    void findMerchantActivityReport_returnsReportSuccessfully() {
        ReportRepository repository = new ReportRepositoryImpl();
        MerchantOption merchant = ReportIntegrationTestSupport.requireActiveMerchant(repository);

        MerchantActivityReport report = repository.findMerchantActivityReport(
                merchant.getMerchantId(),
                ReportIntegrationTestSupport.startDate(),
                ReportIntegrationTestSupport.endDate()
        );

        assertNotNull(report);
        assertNotNull(report.getTitle());
        assertNotNull(report.getCompanyName());
        assertNotNull(report.getIposAccountNumber());
        assertNotNull(report.getOrders());
        assertEquals(merchant.getMerchantId(), report.getMerchantId());
        assertEquals(merchant.getCompanyName(), report.getCompanyName());
    }

}
