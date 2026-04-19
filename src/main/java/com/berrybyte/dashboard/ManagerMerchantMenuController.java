package com.berrybyte.dashboard;

/**
 * Represents manager merchant menu controller.
 */
public class ManagerMerchantMenuController extends MerchantMenuController {

/**
 * Performs can delete merchant accounts.
 *
 * @return result value
 */
    @Override
    protected boolean canDeleteMerchantAccounts() {
        return false;
    }
}
