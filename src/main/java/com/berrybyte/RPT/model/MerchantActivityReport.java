package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents merchant activity report.
 */
public class MerchantActivityReport {
    private final String title;
    private final int merchantId;
    private final String companyName;
    private final String iposAccountNumber;
    private final String address;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<MerchantActivityOrderSection> orders;
    private final int totalOrders;
    private final BigDecimal totalOrderValue;
/**
 * Creates a new MerchantActivityReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param merchantId merchant id
 * @param companyName company name
 * @param iposAccountNumber ipos account number
 * @param address address
 * @param startDate start date
 * @param endDate end date
 * @param generatedAt generated at
 * @param orders orders
 * @param totalOrders total orders
 * @param totalOrderValue total order value
 */

    public MerchantActivityReport(String title,
                                  int merchantId,
                                  String companyName,
                                  String iposAccountNumber,
                                  String address,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  LocalDateTime generatedAt,
                                  List<MerchantActivityOrderSection> orders,
                                  int totalOrders,
                                  BigDecimal totalOrderValue) {
        this.title = title;
        this.merchantId = merchantId;
        this.companyName = companyName;
        this.iposAccountNumber = iposAccountNumber;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.orders = orders;
        this.totalOrders = totalOrders;
        this.totalOrderValue = totalOrderValue;
    }
/**
 * Returns title.
 *
 * @return result value
 */

    public String getTitle() {
        return title;
    }
/**
 * Returns merchant id.
 *
 * @return result value
 */

    public int getMerchantId() {
        return merchantId;
    }
/**
 * Returns company name.
 *
 * @return result value
 */

    public String getCompanyName() {
        return companyName;
    }
/**
 * Returns ipos account number.
 *
 * @return result value
 */

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }
/**
 * Returns address.
 *
 * @return result value
 */

    public String getAddress() {
        return address;
    }
/**
 * Returns start date.
 *
 * @return result value
 */

    public LocalDate getStartDate() {
        return startDate;
    }
/**
 * Returns end date.
 *
 * @return result value
 */

    public LocalDate getEndDate() {
        return endDate;
    }
/**
 * Returns generated at.
 *
 * @return result value
 */

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
/**
 * Returns orders.
 *
 * @return result value
 */

    public List<MerchantActivityOrderSection> getOrders() {
        return orders;
    }
/**
 * Returns total orders.
 *
 * @return result value
 */

    public int getTotalOrders() {
        return totalOrders;
    }
/**
 * Returns total order value.
 *
 * @return result value
 */

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return "MerchantActivityReport{" +
                "title='" + title + '\'' +
                ", merchantId=" + merchantId +
                ", companyName='" + companyName + '\'' +
                ", iposAccountNumber='" + iposAccountNumber + '\'' +
                ", address='" + address + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", orders=" + orders +
                ", totalOrders=" + totalOrders +
                ", totalOrderValue=" + totalOrderValue +
                '}';
    }
}
