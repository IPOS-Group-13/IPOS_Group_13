package com.berrybyte.RPT.services;

import com.berrybyte.RPT.model.*;

import java.time.LocalDate;

/**
 * Defines the contract for report service.
 */
public interface ReportService {
/**
 * Executes the generate low stock report workflow.
 * This method coordinates the main operation for this action.
 *
 * @return result value
 */
    LowStockReport generateLowStockReport();
/**
 * Executes the generate merchant order summary workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */

    MerchantOrderSummaryReport generateMerchantOrderSummary(int merchantId,
                                                            LocalDate startDate,
                                                            LocalDate endDate);
/**
 * Executes the generate invoice listing workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    InvoiceListingReport generateInvoiceListing(Integer merchantId,
                                                LocalDate startDate,
                                                LocalDate endDate);
/**
 * Executes the generate stock turnover workflow.
 * This method coordinates the main operation for this action.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    StockTurnoverReport generateStockTurnover(LocalDate startDate,
                                              LocalDate endDate);
/**
 * Executes the generate merchant activity report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    MerchantActivityReport generateMerchantActivityReport(int merchantId,
                                                          LocalDate startDate,
                                                          LocalDate endDate);
/**
 * Executes the generate info pharma turnover report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    InfoPharmaTurnoverReport generateInfoPharmaTurnoverReport(LocalDate startDate,
                                                              LocalDate endDate);
/**
 * Executes the generate overdue balance report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param merchantName merchant name
 * @return result value
 */
    OverdueBalanceReport generateOverdueBalanceReport(Integer merchantId, String merchantName);
}
