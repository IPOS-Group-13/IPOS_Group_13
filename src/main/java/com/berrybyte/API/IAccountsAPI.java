package com.berrybyte.API;

public interface IAccountsAPI {

    double getOutstandingBalance(int merchantId) throws Exception;

    boolean updateMerchantDetails(int merchantId, String details) throws Exception;

    String getAccountStatus(int merchantId) throws Exception;

    boolean setAccountStatus(int merchantId, String newStatus, int authorisedByUserId) throws Exception;

    boolean recordPayment(int merchantId, String paymentDetails) throws Exception;
}