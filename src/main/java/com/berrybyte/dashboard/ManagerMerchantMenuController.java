package com.berrybyte.dashboard;

public class ManagerMerchantMenuController extends MerchantMenuController {

    @Override
    protected boolean canDeleteMerchantAccounts() {
        return false;
    }
}
