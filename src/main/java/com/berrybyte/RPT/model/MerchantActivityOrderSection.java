package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents merchant activity order section.
 */
public class MerchantActivityOrderSection {
    private final int orderId;
    private final LocalDate orderDate;
    private final BigDecimal orderTotal;
    private final String paymentStatus;
    private final BigDecimal outstandingBalance;
    private final List<MerchantActivityItemRow> items;
/**
 * Creates a new MerchantActivityOrderSection instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param orderDate order date
 * @param orderTotal order total
 * @param paymentStatus payment status
 * @param outstandingBalance outstanding balance
 * @param items items
 */

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
/**
 * Returns order id.
 *
 * @return result value
 */

    public int getOrderId() {
        return orderId;
    }
/**
 * Returns order date.
 *
 * @return result value
 */

    public LocalDate getOrderDate() {
        return orderDate;
    }
/**
 * Returns order total.
 *
 * @return result value
 */

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }
/**
 * Returns payment status.
 *
 * @return result value
 */

    public String getPaymentStatus() {
        return paymentStatus;
    }
/**
 * Returns outstanding balance.
 *
 * @return result value
 */

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }
/**
 * Returns items.
 *
 * @return result value
 */

    public List<MerchantActivityItemRow> getItems() {
        return items;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
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
