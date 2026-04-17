package com.berrybyte.ACC.session;

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

    public static void saveDraft(String fullName, String companyName, String username, String password, String phoneNumber, String email, String address, String accountStatus, String creditLimit) {
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

    public static String getFullName() { return fullName; }
    public static String getCompanyName() { return companyName; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
    public static String getPhoneNumber() { return phoneNumber; }
    public static String getEmail() { return email; }
    public static String getAddress() { return address; }
    public static String getAccountStatus() { return accountStatus; }
    public static String getCreditLimit() { return creditLimit; }

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

    public static void setPreviousPage(String page) { previousPage = page; }
    public static String getPreviousPage() { return previousPage; }

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
