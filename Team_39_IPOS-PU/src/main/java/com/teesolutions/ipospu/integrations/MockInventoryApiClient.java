package com.teesolutions.ipospu.integrations;

import com.teesolutions.ipospu.api.I_InventoryAPI;
import com.teesolutions.ipospu.dto.CartLineDto;
import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.dto.OnlineOrderRequest;
import com.teesolutions.ipospu.dto.OnlineOrderResult;
import com.teesolutions.ipospu.dto.OrderStatusDto;
import com.teesolutions.ipospu.dto.StockReservationResult;
import com.teesolutions.ipospu.repositories.ProductRepository;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MockInventoryApiClient implements I_InventoryAPI {
    private final ProductRepository productRepository = new ProductRepository();

    @Override
    public List<InventoryItemDto> getCatalogue() {
        return productRepository.findActiveProducts("");
    }

    @Override
    public boolean checkStock(String productId, int requestedQty) {
        if (requestedQty <= 0) {
            return false;
        }
        return productRepository.hasStock(productId, requestedQty);
    }

    @Override
    public StockReservationResult reserveOrReject(List<CartLineDto> lines) {
        List<String> unavailable = new ArrayList<>();
        for (CartLineDto line : lines) {
            if (!checkStock(line.getProductId(), line.getQuantity())) {
                unavailable.add(line.getProductId());
            }
        }
        return new StockReservationResult(unavailable.isEmpty(), unavailable);
    }

    @Override
    public OnlineOrderResult submitOnlineOrder(OnlineOrderRequest request) {
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (CartLineDto line : request.getLines()) {
                    boolean updated = productRepository.deductStockAtomically(connection, line.getProductId(), line.getQuantity());
                    if (!updated) {
                        connection.rollback();
                        return new OnlineOrderResult(false, "Stock changed during checkout for " + line.getProductId());
                    }
                }
                connection.commit();
                return new OnlineOrderResult(true, "Order propagated to CA stock successfully");
            } catch (SQLException ex) {
                connection.rollback();
                return new OnlineOrderResult(false, "Inventory update failed: " + ex.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return new OnlineOrderResult(false, "Inventory connection failure: " + e.getMessage());
        }
    }

    @Override
    public OrderStatusDto getOrderStatus(String orderId) {
        return new OrderStatusDto(orderId, "RECEIVED");
    }
}
