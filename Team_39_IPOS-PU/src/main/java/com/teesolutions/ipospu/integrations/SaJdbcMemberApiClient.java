package com.teesolutions.ipospu.integrations;

import com.teesolutions.ipospu.api.I_MemberAPI;
import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;


public class SaJdbcMemberApiClient implements I_MemberAPI {

    private static final String SA_SCHEMA = "ipos_sa";
    private static final String T_INTAKE = "pu_commercial_application_intake";

    private static final String COL_COMPANY_REG = "company_registration_number";
    private static final String COL_DIRECTOR = "director_name";
    private static final String COL_BUSINESS_TYPE = "business_type";
    private static final String COL_ADDRESS = "address";
    private static final String COL_EMAIL = "email";
    private static final String COL_SUBMITTED_AT = "submitted_at";
    private static final String COL_SUBMISSION_STATUS = "submission_status";

    private static final String INITIAL_STATUS = "SUBMITTED_TO_SA";

    private static String q(String ident) {
        return "`" + ident.replace("`", "``") + "`";
    }

    private static String fq(String schema, String table) {
        return q(schema) + "." + q(table);
    }

    @Override
    public boolean submitCommercialApplication(CommercialApplicationDto application) {
        if (application == null) {
            return false;
        }
        String sql = "INSERT INTO " + fq(SA_SCHEMA, T_INTAKE) + " ("
                + q(COL_COMPANY_REG) + ", "
                + q(COL_DIRECTOR) + ", "
                + q(COL_BUSINESS_TYPE) + ", "
                + q(COL_ADDRESS) + ", "
                + q(COL_EMAIL) + ", "
                + q(COL_SUBMITTED_AT) + ", "
                + q(COL_SUBMISSION_STATUS)
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, application.getCompanyRegistrationNumber());
            ps.setString(2, application.getDirectorName());
            ps.setString(3, application.getBusinessType());
            ps.setString(4, application.getAddress());
            ps.setString(5, application.getEmail());
            ps.setTimestamp(6, Timestamp.from(Instant.now()));
            ps.setString(7, INITIAL_STATUS);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("[SA] commercial intake insert failed: " + e.getMessage());
            return false;
        }
    }
}
