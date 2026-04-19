package com.berrybyte.ACC.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteAccountServiceTest {

    private final DeleteAccountService service = new DeleteAccountService();

    @Test
    void deleteUserAccount_invalidId() {
        System.out.println("Testing deleteUserAccount with invalid ID...");

        Exception ex = assertThrows(Exception.class, () ->
                service.deleteUserAccount(0)
        );

        System.out.println("Check: exception message contains expected text...");
        assertTrue(ex.getMessage().contains("No user account was deleted"));
        System.out.println("...success");
    }

    @Test
    void searchUsers_returnsResults() throws Exception {
        System.out.println("Testing searchUsers with keyword 'TEST'...");

        var users = service.searchUsers("TEST");

        System.out.println("Check: users list is not null...");
        assertNotNull(users);
        System.out.println("...success");
    }

    @Test
    void searchUsers_withEmptyString_returnsList() throws Exception {
        System.out.println("Testing searchUsers with empty string...");

        var users = service.searchUsers("");

        System.out.println("Check: users list is not null...");
        assertNotNull(users);
        System.out.println("...success");
    }

    @Test
    void searchUsers_withNoMatch_returnsEmptyOrList() throws Exception {
        System.out.println("Testing searchUsers with no match keyword...");

        var users = service.searchUsers("ZZZ_NO_MATCH_123");

        System.out.println("Check: users list is not null...");
        assertNotNull(users);
        System.out.println("...success");
    }

    @Test
    void getUserAccount_invalidId_throwsException() {
        System.out.println("Testing getUserAccount with invalid ID...");

        Exception ex = assertThrows(Exception.class, () ->
                service.getUserAccount(-1)
        );

        System.out.println("Check: exception message contains expected text...");
        assertTrue(ex.getMessage().contains("could not be found"));
        System.out.println("...success");
    }
}