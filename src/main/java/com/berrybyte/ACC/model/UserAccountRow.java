package com.berrybyte.ACC.model;

/**
 * Represents user account row.
 */
public class UserAccountRow {
    private final int userId;
    private final String name;
    private final String username;
    private final String role;
/**
 * Creates a new UserAccountRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @param name name
 * @param username username
 * @param role role
 */

/**
 * Creates a new UserAccountRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @param name name
 * @param username username
 * @param role role
 */
    public UserAccountRow(int userId, String name, String username, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
    }
/**
 * Returns user id.
 *
 * @return result value
 */

/**
 * Returns user id.
 *
 * @return result value
 */
    public int getUserId() {
        return userId;
    }
/**
 * Returns name.
 *
 * @return result value
 */

/**
 * Returns name.
 *
 * @return result value
 */
    public String getName() {
        return name;
    }
/**
 * Returns username.
 *
 * @return result value
 */

/**
 * Returns username.
 *
 * @return result value
 */
    public String getUsername() {
        return username;
    }
/**
 * Returns role.
 *
 * @return result value
 */

/**
 * Returns role.
 *
 * @return result value
 */
    public String getRole() {
        return role;
    }
}
