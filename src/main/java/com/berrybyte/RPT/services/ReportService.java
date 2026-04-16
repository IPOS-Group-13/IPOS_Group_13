package com.berrybyte.RPT.services;

import com.berrybyte.RPT.model.*;

import java.time.LocalDate;

public interface ReportService {
    LowStockReport generateLowStockReport();

    MerchantOrderSummaryReport generateMerchantOrderSummary(int merchantId,
                                                            LocalDate startDate,
                                                            LocalDate endDate);
    InvoiceListingReport generateInvoiceListing(Integer merchantId,
                                                LocalDate startDate,
                                                LocalDate endDate);
    StockTurnoverReport generateStockTurnover(LocalDate startDate,
                                              LocalDate endDate);
    MerchantActivityReport generateMerchantActivityReport(int merchantId,
                                                          LocalDate startDate,
                                                          LocalDate endDate);
    InfoPharmaTurnoverReport generateInfoPharmaTurnoverReport(LocalDate startDate,
                                                              LocalDate endDate);
}