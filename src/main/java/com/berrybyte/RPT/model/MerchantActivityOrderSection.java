package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MerchantActivityOrderSection {
    private final int orderId;
    private final LocalDate orderDate;
    private final BigDecimal orderTotal;
    private final String paymentStatus;
    private final BigDecimal outstandingBalance;
    private final List<MerchantActivityItemRow> items;

    public MerchantActivityOrderSection(int orderId,
                                        LocalDate orderDate,
                                        BigDecimal orderTotal,
                                        String paymentStatus,
                                        BigDecimal outstandingBalance,
                                        List<MerchantActivityItemRow> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.paymentStatus = paymentStatus;
        this.outstandingBalance = outstandingBalance;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public List<MerchantActivityItemRow> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "MerchantActivityOrderSection{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", orderTotal=" + orderTotal +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", outstandingBalance=" + outstandingBalance +
                ", items=" + items +
                '}';
    }
}
