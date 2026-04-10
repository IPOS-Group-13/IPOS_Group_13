package com.teesolutions.ipospu.models;

public class User {
    private int userId;
    private String email;
    private String memberType;
    private boolean isFirstLogin;
    private int completedOrderCount;

    public User(int userId, String email, String memberType, boolean isFirstLogin, int completedOrderCount) {
        this.userId = userId;
        this.email = email;
        this.memberType = memberType;
        this.isFirstLogin = isFirstLogin;
        this.completedOrderCount = completedOrderCount;
    }


    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getMemberType() { return memberType; }
    public boolean isFirstLogin() { return isFirstLogin; }
    public int getCompletedOrderCount() { return completedOrderCount; }


    public void setFirstLogin(boolean firstLogin) { isFirstLogin = firstLogin; }
    public void setCompletedOrderCount(int completedOrderCount) { this.completedOrderCount = completedOrderCount; }


    public boolean isEligibleForLoyaltyDiscount() {
        if (!"NON_COMMERCIAL".equals(this.memberType)) {
            return false;
        }

        return (this.completedOrderCount + 1) % 10 == 0;
    }
}