package com.teesolutions.ipospu.api;

import com.teesolutions.ipospu.dto.CartLineDto;
import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.dto.OnlineOrderRequest;
import com.teesolutions.ipospu.dto.OnlineOrderResult;
import com.teesolutions.ipospu.dto.OrderStatusDto;
import com.teesolutions.ipospu.dto.StockReservationResult;

import java.util.List;

public interface I_InventoryAPI {
    List<InventoryItemDto> getCatalogue();

    boolean checkStock(String productId, int requestedQty);

    StockReservationResult reserveOrReject(List<CartLineDto> lines);

    OnlineOrderResult submitOnlineOrder(OnlineOrderRequest request);

    OrderStatusDto getOrderStatus(String orderId);
}
