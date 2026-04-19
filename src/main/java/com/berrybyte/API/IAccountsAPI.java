package com.berrybyte.API;

/**
 * Defines the contract for i accounts api.
 */
public interface IAccountsAPI {
/**
 * Performs get outstanding balance.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

    double getOutstandingBalance(int merchantId) throws Exception;
/**
 * Executes the update merchant details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param details details
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean updateMerchantDetails(int merchantId, String details) throws Exception;
/**
 * Performs get account status.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

    String getAccountStatus(int merchantId) throws Exception;
/**
 * Performs set account status.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param newStatus new status
 * @param authorisedByUserId authorised by user id
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean setAccountStatus(int merchantId, String newStatus, int authorisedByUserId) throws Exception;
/**
 * Executes the record payment workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param paymentDetails payment details
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean recordPayment(int merchantId, String paymentDetails) throws Exception;
}
