package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.dto.PaymentResult;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PaymentRepository {
    public void savePayment(String orderId, PaymentRequest request, PaymentResult result) {
        String sql = "INSERT INTO payments(order_id, payee_details, amount, card_type, first4, last4, expiry_date, status, message, transaction_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, request.getPayeeDetails());
            ps.setDouble(3, request.getAmount());
            ps.setString(4, request.getCardType());
            ps.setString(5, request.getFirst4());
            ps.setString(6, request.getLast4());
            ps.setString(7, request.getExpiryDate());
            ps.setString(8, result.isSuccess() ? "SUCCESS" : "FAILED");
            ps.setString(9, result.getMessage());
            ps.setString(10, result.getTransactionId());
            ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save payment record", e);
        }
    }
}
