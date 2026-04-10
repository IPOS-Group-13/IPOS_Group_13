package com.teesolutions.ipospu.config;

import com.teesolutions.ipospu.api.I_InventoryAPI;
import com.teesolutions.ipospu.integrations.CaJdbcInventoryApiClient;
import com.teesolutions.ipospu.integrations.MockInventoryApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public final class InventoryApiFactory {

    private static final String RESOURCE = "db.properties.local";

    private InventoryApiFactory() {
    }

    
    public static boolean isCaInventoryEnabled() {
        return isCaInventory(loadLocalProperties());
    }

    public static I_InventoryAPI create() {
        return isCaInventory(loadLocalProperties())
                ? new CaJdbcInventoryApiClient()
                : new MockInventoryApiClient();
    }

    private static boolean isCaInventory(Properties p) {
        String api = trimUpper(firstNonBlank(
                p.getProperty("db.inventory.api"),
                System.getenv("IPOS_INVENTORY_API")
        ));
        return "CA".equals(api);
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.trim().isEmpty()) {
            return a.trim();
        }
        if (b != null && !b.trim().isEmpty()) {
            return b.trim();
        }
        return "";
    }

    private static String trimUpper(String s) {
        return s == null || s.isEmpty() ? "" : s.trim().toUpperCase();
    }

    private static Properties loadLocalProperties() {
        Properties properties = new Properties();
        try (InputStream in = InventoryApiFactory.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (in != null) {
                properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException ignored) {

        }
        return properties;
    }
}
