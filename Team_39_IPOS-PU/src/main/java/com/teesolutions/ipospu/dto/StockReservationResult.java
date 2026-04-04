package com.teesolutions.ipospu.dto;

import java.util.Collections;
import java.util.List;

public class StockReservationResult {
    private final boolean success;
    private final List<String> unavailableProductIds;

    public StockReservationResult(boolean success, List<String> unavailableProductIds) {
        this.success = success;
        this.unavailableProductIds = unavailableProductIds == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(unavailableProductIds);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getUnavailableProductIds() {
        return unavailableProductIds;
    }
}
