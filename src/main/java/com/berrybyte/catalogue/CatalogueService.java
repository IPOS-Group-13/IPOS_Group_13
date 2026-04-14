package com.berrybyte.catalogue;

import com.berrybyte.API.ICatalogueAPI;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogueService implements ICatalogueAPI {

    private static final String DESCRIPTION_REGEX = "^[A-Za-z0-9 ,.&()'/-]{2,100}$";
    private static final String PACKAGE_TYPE_REGEX = "^[A-Za-z0-9 .&()'/-]{2,50}$";
    private static final String UNIT_REGEX = "^[A-Za-z]{1,20}$";

    public void createProduct(String description,
                              String packageType,
                              String unit,
                              int unitsInPack,
                              double packageCost,
                              int availabilityPacks,
                              int stockLimitPacks) throws Exception {

        validateProductDetails(
                description,
                packageType,
                unit,
                unitsInPack,
                packageCost,
                availabilityPacks,
                stockLimitPacks
        );

        String nextIdSql = "SELECT COALESCE(MAX(ItemId), 10000000) + 1 AS NextItemId FROM Catalogue";

        String insertSql = """
                INSERT INTO Catalogue
                (ItemId, Description, PackageType, Unit, UnitsInPack, PackageCost, AvailabilityPacks, StockLimitPacks)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            int nextItemId = 10000001;

            try (PreparedStatement nextIdPs = conn.prepareStatement(nextIdSql);
                 ResultSet rs = nextIdPs.executeQuery()) {
                if (rs.next()) {
                    nextItemId = rs.getInt("NextItemId");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, nextItemId);
                ps.setString(2, description.trim());
                ps.setString(3, packageType.trim());
                ps.setString(4, unit.trim());
                ps.setInt(5, unitsInPack);
                ps.setDouble(6, packageCost);
                ps.setInt(7, availabilityPacks);
                ps.setInt(8, stockLimitPacks);
                ps.executeUpdate();
            }
        }
    }

    public List<CatalogueItemRow> searchCatalogueItems(String keyword) throws Exception {
        List<CatalogueItemRow> items = new ArrayList<>();

        String sql = """
                SELECT ItemId, Description, PackageType, Unit, UnitsInPack, PackageCost, AvailabilityPacks, StockLimitPacks
                FROM Catalogue
                WHERE IsDeleted = 0
                  AND (
                      CAST(ItemId AS CHAR) LIKE ?
                   OR Description LIKE ?
                   OR PackageType LIKE ?
                   OR Unit LIKE ?
                  )
                ORDER BY ItemId
                """;

        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new CatalogueItemRow(
                                rs.getInt("ItemId"),
                                rs.getString("Description"),
                                rs.getString("PackageType"),
                                rs.getString("Unit"),
                                rs.getInt("UnitsInPack"),
                                rs.getDouble("PackageCost"),
                                rs.getInt("AvailabilityPacks"),
                                rs.getInt("StockLimitPacks")
                        ));
                    }
                }
            }
        }

        return items;
    }

    public void updateProductDetails(int itemId,
                                     String description,
                                     String packageType,
                                     String unit,
                                     int unitsInPack,
                                     double packageCost,
                                     int availabilityPacks,
                                     int stockLimitPacks) throws Exception {

        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item ID.");
        }


        validateProductDetails(
                description,
                packageType,
                unit,
                unitsInPack,
                packageCost,
                availabilityPacks,
                stockLimitPacks
        );

        String updateSql = """
            UPDATE Catalogue
            SET Description = ?,
                PackageType = ?,
                Unit = ?,
                UnitsInPack = ?,
                PackageCost = ?,
                AvailabilityPacks = ?,
                StockLimitPacks = ?
            WHERE ItemId = ?
              AND IsDeleted = 0
            """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {

                ps.setString(1, description.trim());
                ps.setString(2, packageType.trim());
                ps.setString(3, unit.trim());
                ps.setInt(4, unitsInPack);
                ps.setDouble(5, packageCost);
                ps.setInt(6, availabilityPacks);
                ps.setInt(7, stockLimitPacks);
                ps.setInt(8, itemId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new SQLException("No product was updated.");
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }




    @Override
    public List<String> getCatalogue(String keyword) throws Exception {
        List<String> rows = new ArrayList<>();
        for (CatalogueItemRow item : searchCatalogueItems(keyword)) {
            rows.add(item.getItemId() + " - " + item.getDescription());
        }
        return rows;
    }

    @Override
    public String getProductDetails(int itemId) throws Exception {
        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item id.");
        }

        String sql = """
                SELECT ItemId, Description, PackageType, Unit, UnitsInPack, PackageCost, AvailabilityPacks, StockLimitPacks
                FROM Catalogue
                WHERE ItemId = ?
                  AND IsDeleted = 0
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, itemId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return String.format(
                                "ItemId: %d, Description: %s, PackageType: %s, Unit: %s, UnitsInPack: %d, PackageCost: %.2f, AvailabilityPacks: %d, StockLimitPacks: %d",
                                rs.getInt("ItemId"),
                                rs.getString("Description"),
                                rs.getString("PackageType"),
                                rs.getString("Unit"),
                                rs.getInt("UnitsInPack"),
                                rs.getDouble("PackageCost"),
                                rs.getInt("AvailabilityPacks"),
                                rs.getInt("StockLimitPacks")
                        );
                    }
                }
            }
        }

        return null;
    }

    @Override
    public int checkStock(int itemId) throws Exception {
        if (itemId <= 0) {
            throw new IllegalArgumentException("Invalid item id.");
        }

        String sql = "SELECT AvailabilityPacks FROM Catalogue WHERE ItemId = ? AND IsDeleted = 0";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, itemId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("AvailabilityPacks");
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public boolean addItem(String itemDetails) throws Exception {
        if (itemDetails == null || itemDetails.isBlank()) {
            throw new IllegalArgumentException("Item details are required.");
        }

        String[] parts = itemDetails.split("\\|");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Item details must be in the format: Description|PackageType|Unit|UnitsInPack|PackageCost|AvailabilityPacks|StockLimitPacks");
        }

        createProduct(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                Integer.parseInt(parts[3].trim()),
                Double.parseDouble(parts[4].trim()),
                Integer.parseInt(parts[5].trim()),
                Integer.parseInt(parts[6].trim())
        );

        return true;
    }

    @Override
    public List<String> generateLowStockReport() throws Exception {
        List<String> report = new ArrayList<>();

        String sql = """
                SELECT ItemId, Description, AvailabilityPacks, StockLimitPacks
                FROM Catalogue
                WHERE IsDeleted = 0
                  AND AvailabilityPacks <= StockLimitPacks
                ORDER BY AvailabilityPacks ASC, ItemId ASC
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    report.add(String.format(
                            "Item %d (%s): %d packs available, stock limit %d",
                            rs.getInt("ItemId"),
                            rs.getString("Description"),
                            rs.getInt("AvailabilityPacks"),
                            rs.getInt("StockLimitPacks")
                    ));
                }
            }
        }

        return report;
    }

    private void validateProductDetails(String description,
                                        String packageType,
                                        String unit,
                                        int unitsInPack,
                                        double packageCost,
                                        int availabilityPacks,
                                        int stockLimitPacks) {

        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required.");
        if (packageType == null || packageType.isBlank()) throw new IllegalArgumentException("Package type is required.");
        if (unit == null || unit.isBlank()) throw new IllegalArgumentException("Unit is required.");
        if (unitsInPack <= 0) throw new IllegalArgumentException("Units in a pack must be greater than zero.");
        if (packageCost < 0) throw new IllegalArgumentException("Package cost cannot be negative.");
        if (availabilityPacks < 0) throw new IllegalArgumentException("Availability cannot be negative.");
        if (stockLimitPacks < 0) throw new IllegalArgumentException("Stock limit cannot be negative.");

        if (!description.matches(DESCRIPTION_REGEX)) {
            throw new IllegalArgumentException("Description contains invalid characters.");
        }
        if (!packageType.matches(PACKAGE_TYPE_REGEX)) {
            throw new IllegalArgumentException("Package type contains invalid characters.");
        }
        if (!unit.matches(UNIT_REGEX)) {
            throw new IllegalArgumentException("Unit must contain letters only.");
        }
    }

    public List<LowStockItemRow> getLowStockItems() throws Exception {
        String sql = """
                SELECT ItemId, Description, AvailabilityPacks, StockLimitPacks
                FROM Catalogue
                WHERE AvailabilityPacks <= StockLimitPacks
                  AND IsDeleted = 0
                ORDER BY ItemId
                """;

        List<LowStockItemRow> items = new ArrayList<>();
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new LowStockItemRow(
                            rs.getInt("ItemId"),
                            rs.getString("Description"),
                            rs.getInt("AvailabilityPacks"),
                            rs.getInt("StockLimitPacks")
                    ));
                }
            }
        }

        return items;
    }

    private void ensureIsDeletedColumn(Connection conn) throws Exception {
        String alterSql = "ALTER TABLE Catalogue ADD COLUMN IsDeleted TINYINT(1) NOT NULL DEFAULT 0";

        try (PreparedStatement ps = conn.prepareStatement(alterSql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (!message.contains("duplicate column")) {
                throw e;
            }
        }
    }
}
