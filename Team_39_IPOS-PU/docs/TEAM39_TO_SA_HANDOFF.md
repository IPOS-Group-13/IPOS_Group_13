# Team 39 To Team 37 (SA)

This is the exact information Team 39 (`IPOS-PU`) is giving Team 37 (`IPOS-SA`) for **shared MySQL** integration, plus the exact information we need back.

## Important note about DB setup

This does **not** go against Aden's setup.

- We still use `db.properties.local`
- We still use the shared MySQL host / port / db / user
- We still keep passwords out of Git
- `db.init.mode=SHARED` only means: **PU should connect without auto-running schema/seed on the shared DB**

So this is the same connection method as Aden's, just safer for a shared database.

## What Team 39 gives Team 37

### 1. Business flow from PU

- PU allows non-commercial and commercial membership registration.
- For commercial membership, PU validates the input.
- After validation, PU submits a commercial application to SA.
- Today this is still mocked on the PU side; we now need the real shared DB object from SA.

### 2. Commercial application fields PU sends

| Field | Value from PU | Notes |
|------|----------------|-------|
| `company_registration_number` | Example: `UK10003429COMPH` | PU validation supports the PDF sample format. |
| `director_name` | User-entered text | Required in current PU form. |
| `business_type` | User-entered text | Required in current PU form. |
| `address` | User-entered text | Stored as one string in PU. |
| `email` | User-entered email | PU blocks reuse of an email already used by an existing PU member account. |
| `submitted_at` | Current timestamp | PU can set this. |
| `submission_status` | `SUBMITTED_TO_SA` | Current PU default; SA should confirm final status vocabulary. |

### 3. PDF-aligned sample already present on PU side

PU is seeded with the sample commercial application corresponding to PDF `PU0003`:

- Company name in PDF: `Pond Pharmacy`
- Company registration: `UK10003429CompH`
- Email: `pondPharma@example.com`

Important: the current PU schema stores this as a **commercial application row**, not as a full separate commercial member account object with its own `PU0003` account-number column.

### 4. What Team 39 needs Team 37 to provide back

Please fill in and return:

#### A. SA intake object

| Question | Team 37 answer |
|---------|-----------------|
| Table name where PU should insert commercial applications | |
| Are we inserting directly into an SA-owned table, or should SA read from PU's current `commercial_applications` table? | |
| If SA has a different table, what are the exact column names? | |

#### B. Column mapping

| PU field | Team 37 column name |
|---------|----------------------|
| `company_registration_number` | |
| `director_name` | |
| `business_type` | |
| `address` | |
| `email` | |
| `submitted_at` | |
| `submission_status` | |

#### C. Business rules

| Question | Team 37 answer |
|---------|-----------------|
| What should happen on duplicate application email? | |
| What should happen on duplicate company registration number? | |
| What status values does SA expect? | |
| Does SA require a company trading name column as well? | |

#### D. Permissions needed for `pu_user`

| Permission | Needed? | Team 37 answer |
|-----------|---------|----------------|
| `INSERT` on commercial intake object | Yes | |
| `SELECT` on inserted rows for verification | Optional but useful | |
| `UPDATE` by PU | No, unless explicitly required | |

## What Team 37 needs to do

1. Confirm which table or shared object PU should write to.
2. Confirm the exact column names and status values.
3. Grant the minimum required rights to the PU DB user.
4. Confirm whether SA wants PU to keep its own local `commercial_applications` table or whether SA's object becomes the only source of truth.
5. Run one shared integration test with Team 39.

## Definition of done for SA integration

SA integration is complete when all of the below are true:

- A new commercial application submitted from PU appears in the SA-agreed intake object.
- Team 37 can read and process that row.
- Duplicate handling rules are agreed and visible in PU behavior.
- The field set and status values are signed off by both teams.
