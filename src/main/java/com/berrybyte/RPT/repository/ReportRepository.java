package com.berrybyte.RPT.repository;

import com.berrybyte.RPT.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Defines the contract for report repository.
 */
public interface ReportRepository {
/**
 * Executes the find low stock items workflow.
 *
 * @return result value
 */
    List<LowStockItem> findLowStockItems();
/**
 * Executes the find merchant order summary workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */

    List<MerchantOrderSummaryRow> findMerchantOrderSummary(int merchantId,
                                                           LocalDate startDate,
                                                           LocalDate endDate);
/**
 * Executes the find invoice listing workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    List<InvoiceListingRow> findInvoiceListing(Integer merchantId,
                                               LocalDate startDate,
                                               LocalDate endDate);
/**
 * Executes the find stock turnover workflow.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    List<StockTurnoverRow> findStockTurnover(java.time.LocalDate startDate,
                                             java.time.LocalDate endDate);
/**
 * Executes the find merchant activity report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    MerchantActivityReport findMerchantActivityReport(int merchantId,
                                                      LocalDate startDate,
                                                      LocalDate endDate);
/**
 * Executes the find info pharma turnover workflow.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    List<InfoPharmaTurnoverRow> findInfoPharmaTurnover(LocalDate startDate,
                                                       LocalDate endDate);
/**
 * Executes the find merchant options workflow.
 *
 * @return result value
 */
    List<MerchantOption> findMerchantOptions();
/**
 * Executes the find overdue balance report workflow.
 *
 * @param merchantId merchant id
 * @return result value
 */

    List<OverdueBalanceRow> findOverdueBalanceReport(Integer merchantId);
}
