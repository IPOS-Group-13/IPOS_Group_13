package com.berrybyte.catalogue;

import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteProductService {

    public CatalogueItemRow getProduct(int itemId) throws Exception {
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
                        return new CatalogueItemRow(
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

        throw new Exception("Selected product could not be found.");
    }

    public List<CatalogueItemRow> searchProducts(String searchText) throws Exception {
        List<CatalogueItemRow> products = new ArrayList<>();

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

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection()) {
            ensureIsDeletedColumn(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String keyword = "%" + (searchText == null ? "" : searchText.trim()) + "%";
                ps.setString(1, keyword);
                ps.setString(2, keyword);
                ps.setString(3, keyword);
                ps.setString(4, keyword);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        products.add(new CatalogueItemRow(
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

        return products;
    }

    public void deleteProduct(int itemId) throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();

        String softDeleteSql = "UPDATE Catalogue SET IsDeleted = 1 WHERE ItemId = ?";

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try {
                ensureIsDeletedColumn(conn);

                try (PreparedStatement ps = conn.prepareStatement(softDeleteSql)) {
                    ps.setInt(1, itemId);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new Exception("No product was deleted.");
                    }
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
