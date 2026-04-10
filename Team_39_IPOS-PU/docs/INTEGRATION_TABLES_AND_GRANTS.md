# Integration: tables each team must expose + where GRANTs run

This file complements `GROUP_SHARED_DB_CONTRACT_TEAM39.md` and the handoff docs.  
**Do not treat MySQL object names as final until Team 38 (CA) and Team 37 (SA) fill in their sections** — table names and casing on the server may differ from MySQL Workbench labels.

---

## What Team 38 (CA, schema `ipos_ca`) must give PU

| PU need | Typical CA objects (from your schema explorer) | Purpose |
|--------|--------------------------------------------------|--------|
| Shop catalogue + stock for `getCatalogue()`, `checkStock()`, `reserveOrReject()` | **`Catalogue`**, **`Inventory`** (join as CA defines) | Read product id, name, description, price, quantity available |
| Optional joins | **`AccountHolders`** | Only if CA’s catalogue view requires it — **confirm with CA** |
| Post‑checkout order (after payment in PU) | **Header + line table(s) CA must name** (not visible in every explorer screenshot) | `INSERT` order id, address, lines, optional email — see `TEAM39_TO_CA_HANDOFF.md` |
| Order tracking `getOrderStatus(orderId)` | **Same or related table/view CA names** | `SELECT` status mapped to `RECEIVED` / `DISPATCHED` / `DELIVERED` / `VOID` |

**CA must return:** exact table/view names, column mapping, and whether PU **UPDATE**s `Inventory` for stock or only **INSERT**s orders and CA processes stock internally.

---

## What Team 37 (SA, schema `ipos_sa`) must give PU

| PU need | Candidate objects (from your explorer — **verify**) | Purpose |
|--------|------------------------------------------------------|--------|
| `submitCommercialApplication(...)` | **Intake table SA designates** (may be new, or e.g. linked to **`MerchantAccounts`** — **SA decides**) | `INSERT` company reg, director, business type, address, email, submitted time, status |

**SA must return:** exact intake table name + column list + duplicate rules (see `TEAM39_TO_SA_HANDOFF.md`).

---

## Where the SQL lives (this repo)

| File | Purpose |
|------|--------|
| `docs/sql/pu_integration_grants_TEMPLATE.sql` | **Template** `CREATE USER` / `GRANT` script — edit placeholders, then run on the server |
| `docs/GROUP_SHARED_DB_CONTRACT_TEAM39.md` | Checklists for CA/SA to complete |
| `docs/TEAM39_TO_CA_HANDOFF.md` / `TEAM39_TO_SA_HANDOFF.md` | Payloads PU sends |

**Do not commit** real passwords. The app keeps secrets in `src/main/resources/db.properties.local` (gitignored).

---

## Where you **execute** the GRANT script (not in Java, not in GitHub Actions by default)

1. Connect to the **shared MySQL** as an admin (`root` or another account with `GRANT OPTION`), e.g.:
   - **MySQL Workbench** → same host/port as Railway/shared DB  
   - **Railway** (or host) **MySQL console** if provided  
2. Paste the edited script from `docs/sql/pu_integration_grants_TEMPLATE.sql` and run it once (or re-run after object renames).  
3. Put the **same** username/password PU uses into `db.properties.local` on each developer machine.

PU code then uses **one** JDBC connection (usually default schema `ipos_pu`) and SQL with **fully qualified names**, e.g. `ipos_ca.Inventory`, `ipos_sa.YourIntakeTable`.

---

## Honest limitation

Until CA names **order header/line** tables and SA names the **commercial intake** table, the GRANT file **cannot** be final — only templated. After the meeting, replace placeholders in `pu_integration_grants_TEMPLATE.sql` and re-run `FLUSH PRIVILEGES;`.
