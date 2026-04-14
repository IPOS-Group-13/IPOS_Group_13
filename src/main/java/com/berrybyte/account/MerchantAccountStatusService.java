package com.berrybyte.account;

public class MerchantAccountStatusService {

    public String calculateStatus(int daysOverdue) {
        if (daysOverdue <= 0) {
            return "NORMAL";
        } else if (daysOverdue <= 15) {
            return "NORMAL";
        } else if (daysOverdue <= 30) {
            return "SUSPENDED";
        } else {
            return "IN_DEFAULT";
        }
    }

    public boolean canPlaceOrder(String accountStatus) {
        return accountStatus.equals("NORMAL");
    }

    public boolean canRestoreToNormal(String accountStatus) {
        return accountStatus.equals("SUSPENDED");
    }
}