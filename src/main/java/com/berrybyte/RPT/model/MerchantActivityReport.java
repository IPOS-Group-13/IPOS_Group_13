package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public String getTitle() {
        return title;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<MerchantActivityOrderSection> getOrders() {
        return orders;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

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