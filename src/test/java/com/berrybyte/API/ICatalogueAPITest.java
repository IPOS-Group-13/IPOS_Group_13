package com.berrybyte.API;

import com.berrybyte.catalogue.CatalogueService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ICatalogueAPITest {

    private final ICatalogueAPI catalogueAPI = new CatalogueService();

    @Test
    void getCatalogue_viaProvidedInterface_returnsItems() throws Exception {

        System.out.println("Testing getCatalogue()...");

        var items = catalogueAPI.getCatalogue("");

        System.out.println("Check 1: catalogue is not null...");
        assertNotNull(items);
        System.out.println("...success");

        System.out.println("Check 2: catalogue is not empty...");
        assertFalse(items.isEmpty());
        System.out.println("...success");
    }

    @Test
    void checkStock_viaProvidedInterface_returnsStockLevel() throws Exception {

        System.out.println("Testing checkStock()...");

        int stock = catalogueAPI.checkStock(10000001);

        System.out.println("Check 1: stock is non-negative...");
        assertTrue(stock >= 0);
        System.out.println("...success");
    }
}