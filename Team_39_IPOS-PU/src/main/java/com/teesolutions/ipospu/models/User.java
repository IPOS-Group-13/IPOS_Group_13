package com.teesolutions.ipospu.models;

public class User {
    private int userId;
    private String email;
    private String memberType; // e.g., "NON_COMMERCIAL", "COMMERCIAL", "ADMIN", "GUEST"
    private boolean isFirstLogin;
    private int completedOrderCount;

    public User(int userId, String email, String memberType, boolean isFirstLogin, int completedOrderCount) {
        this.userId = userId;
        this.email = email;
        this.memberType = memberType;
        this.isFirstLogin = isFirstLogin;
        this.completedOrderCount = completedOrderCount;
    }

    // --- Getters ---
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getMemberType() { return memberType; }
    public boolean isFirstLogin() { return isFirstLogin; }
    public int getCompletedOrderCount() { return completedOrderCount; }

    // --- Setters ---
    public void setFirstLogin(boolean firstLogin) { isFirstLogin = firstLogin; }
    public void setCompletedOrderCount(int completedOrderCount) { this.completedOrderCount = completedOrderCount; }

    // --- Business Logic ---
    /**
     * US-12: Non-commercial members get a 10% discount on every 10th order.
     * If they have exactly 9 (or 19, 29) completed orders, the CURRENT order gets the discount.
     */
    public boolean isEligibleForLoyaltyDiscount() {
        if (!"NON_COMMERCIAL".equals(this.memberType)) {
            return false;
        }
        // Checks if the next order will be a multiple of 10
        return (this.completedOrderCount + 1) % 10 == 0;
    }
}