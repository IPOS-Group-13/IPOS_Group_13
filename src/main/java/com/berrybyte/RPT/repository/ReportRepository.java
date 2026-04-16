package com.berrybyte.RPT.repository;

import com.berrybyte.RPT.model.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {
    List<LowStockItem> findLowStockItems();

    List<MerchantOrderSummaryRow> findMerchantOrderSummary(int merchantId,
                                                           LocalDate startDate,
                                                           LocalDate endDate);
    List<InvoiceListingRow> findInvoiceListing(Integer merchantId,
                                               LocalDate startDate,
                                               LocalDate endDate);
    List<StockTurnoverRow> findStockTurnover(java.time.LocalDate startDate,
                                             java.time.LocalDate endDate);
    MerchantActivityReport findMerchantActivityReport(int merchantId,
                                                      LocalDate startDate,
                                                      LocalDate endDate);
    List<InfoPharmaTurnoverRow> findInfoPharmaTurnover(LocalDate startDate,
                                                       LocalDate endDate);
    List<MerchantOption> findMerchantOptions();
}
