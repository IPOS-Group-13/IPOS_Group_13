package com.berrybyte.ORD.services;

import com.berrybyte.ORD.helpers.PaymentRequestRow;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentRequestService {

    public List<PaymentRequestRow> getPaymentRequests() throws Exception {
        String sql = """
                SELECT id,
                       payee_details,
                       amount,
                       card_type,
                       status
                FROM ipos_pu.ca_payment_requests
                ORDER BY created_at DESC, id DESC
                """;

        List<PaymentRequestRow> rows = new ArrayList<>();
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new PaymentRequestRow(
                        rs.getLong("id"),
                        rs.getString("payee_details"),
                        rs.getBigDecimal("amount"),
                        rs.getString("card_type"),
                        rs.getString("status")
                ));
            }
        }
        return rows;
    }
}
