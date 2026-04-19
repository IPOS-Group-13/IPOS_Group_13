package com.berrybyte.ACC.session;

/**
 * Stores temporary merchant account details while multi-step setup is in progress.
 */
public class MerchantDraftSession {

    private static String previousPage;

    private static String fullName;
    private static String companyName;
    private static String username;
    private static String password;
    private static String phoneNumber;
    private static String email;
    private static String address;
    private static String accountStatus;
    private static String creditLimit;

    /**
     * Saves the current merchant draft values.
     *
     * @param fullName full name
     * @param companyName company name
     * @param username username
     * @param password password
     * @param phoneNumber phone number
     * @param email email
     * @param address address
     * @param accountStatus account status
     * @param creditLimit credit limit
     */
    public static void saveDraft(String fullName,
                                 String companyName,
                                 String username,
                                 String password,
                                 String phoneNumber,
                                 String email,
                                 String address,
                                 String accountStatus,
                                 String creditLimit) {
        MerchantDraftSession.fullName = fullName;
        MerchantDraftSession.companyName = companyName;
        MerchantDraftSession.username = username;
        MerchantDraftSession.password = password;
        MerchantDraftSession.phoneNumber = phoneNumber;
        MerchantDraftSession.email = email;
        MerchantDraftSession.address = address;
        MerchantDraftSession.accountStatus = accountStatus;
        MerchantDraftSession.creditLimit = creditLimit;
    }

    /**
     * Returns full name.
     *
     * @return full name
     */
    public static String getFullName() {
        return fullName;
    }

    /**
     * Returns company name.
     *
     * @return company name
     */
    public static String getCompanyName() {
        return companyName;
    }

    /**
     * Returns username.
     *
     * @return username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Returns password.
     *
     * @return password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * Returns phone number.
     *
     * @return phone number
     */
    public static String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns email.
     *
     * @return email
     */
    public static String getEmail() {
        return email;
    }

    /**
     * Returns address.
     *
     * @return address
     */
    public static String getAddress() {
        return address;
    }

    /**
     * Returns account status.
     *
     * @return account status
     */
    public static String getAccountStatus() {
        return accountStatus;
    }

    /**
     * Returns credit limit.
     *
     * @return credit limit
     */
    public static String getCreditLimit() {
        return creditLimit;
    }

    /**
     * Returns whether a complete draft currently exists.
     *
     * @return {@code true} when all draft fields are populated
     */
    public static boolean hasDraft() {
        return fullName != null
                && companyName != null
                && username != null
                && password != null
                && phoneNumber != null
                && email != null
                && address != null
                && accountStatus != null
                && creditLimit != null;
    }

    /**
     * Stores previous page key used for back navigation.
     *
     * @param page previous page key
     */
    public static void setPreviousPage(String page) {
        previousPage = page;
    }

    /**
     * Returns previous page key.
     *
     * @return previous page key
     */
    public static String getPreviousPage() {
        return previousPage;
    }

    /**
     * Clears all saved draft state.
     */
    public static void clear() {
        previousPage = null;
        fullName = null;
        companyName = null;
        username = null;
        password = null;
        phoneNumber = null;
        email = null;
        address = null;
        accountStatus = null;
        creditLimit = null;
    }
}
