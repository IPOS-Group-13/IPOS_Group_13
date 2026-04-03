package com.berrybyte.account;

public class UserAccountRow {
    private final int userId;
    private final String name;
    private final String username;
    private final String role;

    public UserAccountRow(int userId, String name, String username, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}