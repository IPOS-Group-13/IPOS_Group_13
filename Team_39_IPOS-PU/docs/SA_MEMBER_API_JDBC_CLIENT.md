# SA member API — JDBC client (replaces `MockMemberApiClient`)

This document holds the **intended** production-style implementation of **`I_MemberAPI`** using **shared MySQL** and **`GRANT`**ed access to **Team 37 (SA)** objects.

> **Naming correction:** Commercial applications go to **SA** (`ipos_sa`), **not** CA (`ipos_ca`). CA owns inventory/catalogue; SA owns commercial membership intake per `docs/GROUP_API_CONTRACT_TEAM37_38_39.md`.

---

## 1. What you do first (permissions)

Ask Team 37 for the **exact** intake table name and column names, then run (as MySQL `root` / admin) something like:

```sql
-- After replacing <<SA_INTAKE_TABLE>> and <<PU_MYSQL_USER>> / host:
GRANT INSERT, SELECT ON ipos_sa.<<SA_INTAKE_TABLE>> TO '<<PU_MYSQL_USER>>'@'%';
FLUSH PRIVILEGES;
```

`SELECT` is useful for duplicate checks if SA asks for it. Adjust to least privilege.

PU’s JDBC user (in `db.properties.local`) must be the same user you grant.

Full template: `docs/sql/pu_integration_grants_TEMPLATE.sql`.

---

## 2. Placeholder map (fill when SA replies)

| `CommercialApplicationDto` getter | SA column (placeholder) | Notes |
|----------------------------------|-------------------------|--------|
| `getCompanyRegistrationNumber()` | `<<COL_COMPANY_REG>>` | |
| `getDirectorName()` | `<<COL_DIRECTOR>>` | |
| `getBusinessType()` | `<<COL_BUSINESS_TYPE>>` | |
| `getAddress()` | `<<COL_ADDRESS>>` | |
| `getEmail()` | `<<COL_EMAIL>>` | |
| *(optional, not in DTO today)* | `<<COL_SUBMITTED_AT>>` | e.g. `submitted_at` — set in Java |
| *(optional)* | `<<COL_STATUS>>` | e.g. `submission_status` — agree value with SA |

**Table:** `<<SA_SCHEMA>>`.`<<SA_INTAKE_TABLE>>` — usually `ipos_sa` and a table SA names.

If SA uses **different** column order or extra `NOT NULL` columns, extend the `INSERT` and this table.

---

## 3. New class — copy into `src/main/java/com/teesolutions/ipospu/integrations/`

Create file: **`SaJdbcMemberApiClient.java`**

```java
package com.teesolutions.ipospu.integrations;

import com.teesolutions.ipospu.api.I_MemberAPI;
import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Team 37 (SA) commercial application intake via shared MySQL.
 * Replace ALL {@code <<PLACEHOLDER>>} strings below after SA confirms DDL + GRANTs.
 * <p>
 * Contract: {@link com.teesolutions.ipospu.api.I_MemberAPI}
 */
public class SaJdbcMemberApiClient implements I_MemberAPI {

    // TODO (Team 37): confirm schema and table identifier (match server case sensitivity).
    private static final String SA_SCHEMA = "<<SA_SCHEMA>>"; // e.g. ipos_sa
    private static final String SA_INTAKE_TABLE = "<<SA_INTAKE_TABLE>>"; // e.g. PuCommercialApplicationIntake

    // TODO (Team 37): column names — must match SA table exactly.
    private static final String COL_COMPANY_REG = "<<COL_COMPANY_REG>>";
    private static final String COL_DIRECTOR = "<<COL_DIRECTOR>>";
    private static final String COL_BUSINESS_TYPE = "<<COL_BUSINESS_TYPE>>";
    private static final String COL_ADDRESS = "<<COL_ADDRESS>>";
    private static final String COL_EMAIL = "<<COL_EMAIL>>";

    // Set to null after SA says these columns do not exist (code will omit them).
    private static final String COL_SUBMITTED_AT = "<<COL_SUBMITTED_AT>>"; // or null
    private static final String COL_STATUS = "<<COL_STATUS>>"; // or null

    // TODO: agree initial status literal with SA (e.g. SUBMITTED, SUBMITTED_TO_SA, PENDING).
    private static final String INITIAL_STATUS_VALUE = "<<INITIAL_STATUS_VALUE>>";

    @Override
    public boolean submitCommercialApplication(CommercialApplicationDto application) {
        if (application == null) {
            return false;
        }

        // Fully-qualified table: works when JDBC URL default schema is ipos_pu but user has GRANT on ipos_sa.*.
        String fqTable = quoteIdentifier(SA_SCHEMA) + "." + quoteIdentifier(SA_INTAKE_TABLE);

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(fqTable).append(" (");
        StringBuilder vals = new StringBuilder(") VALUES (");

        appendColumn(sql, vals, COL_COMPANY_REG);
        appendColumn(sql, vals, COL_DIRECTOR);
        appendColumn(sql, vals, COL_BUSINESS_TYPE);
        appendColumn(sql, vals, COL_ADDRESS);
        appendColumn(sql, vals, COL_EMAIL);
        appendColumn(sql, vals, COL_SUBMITTED_AT);
        appendColumn(sql, vals, COL_STATUS);

        // Remove trailing comma from "col1, col2, " before closing paren
        trimTrailingComma(sql);
        trimTrailingComma(vals);
        sql.append(vals).append(")");

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setString(i++, application.getCompanyRegistrationNumber());
            ps.setString(i++, application.getDirectorName());
            ps.setString(i++, application.getBusinessType());
            ps.setString(i++, application.getAddress());
            ps.setString(i++, application.getEmail());

            if (isRealColumn(COL_SUBMITTED_AT)) {
                ps.setTimestamp(i++, Timestamp.from(Instant.now()));
            }
            if (isRealColumn(COL_STATUS)) {
                ps.setString(i++, INITIAL_STATUS_VALUE);
            }

            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            // TODO: log properly; avoid leaking SQL details to end users in production.
            System.err.println("SA commercial intake insert failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean isRealColumn(String constant) {
        return constant != null && !constant.isBlank() && !constant.startsWith("<<");
    }

    private static void appendColumn(StringBuilder cols, StringBuilder vals, String colConst) {
        if (!isRealColumn(colConst)) {
            return;
        }
        cols.append(quoteIdentifier(colConst)).append(", ");
        vals.append("?, ");
    }

    private static void trimTrailingComma(StringBuilder sb) {
        int len = sb.length();
        if (len >= 2 && sb.substring(len - 2).equals(", ")) {
            sb.setLength(len - 2);
        }
    }

    private static String quoteIdentifier(String raw) {
        if (raw == null || raw.isBlank() || raw.startsWith("<<")) {
            throw new IllegalStateException("Replace all <<PLACEHOLDER>> constants in SaJdbcMemberApiClient before running: " + raw);
        }
        return "`" + raw.replace("`", "``") + "`";
    }
}
```

---

## 4. Wire it in PU (when placeholders are real)

In **`PortalController`**, replace:

```java
private final MockMemberApiClient memberApiClient = new MockMemberApiClient();
```

with:

```java
private final I_MemberAPI memberApiClient = new SaJdbcMemberApiClient();
```

(add `import com.teesolutions.ipospu.api.I_MemberAPI;`)

Keep **`MockMemberApiClient`** until integration is tested; you can switch with a **config flag** later if you want local demo without SA tables.

---

## 5. Optional: keep a copy in PU’s `commercial_applications`

Today PU also persists commercial rows locally for UI/history. **`I_MemberAPI`** only requires SA intake. If product owners want **both**, call `CommercialApplicationRepository.save(...)` **and** `SaJdbcMemberApiClient` (or compose in a small facade). That is a **group** decision — not required for the interface itself.

---

## Related docs

- `docs/GROUP_API_CONTRACT_TEAM37_38_39.md` — `IMemberAPI` semantics  
- `docs/TEAM39_TO_SA_HANDOFF.md` — fields SA must map  
- `docs/sql/pu_integration_grants_TEMPLATE.sql` — `GRANT` template  
- `docs/INTEGRATION_TABLES_AND_GRANTS.md` — who owns which schema  
