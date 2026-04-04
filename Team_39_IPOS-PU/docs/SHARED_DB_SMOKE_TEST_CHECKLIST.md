# Shared DB Smoke Test Checklist (Day 1 With Aden Credentials)

Use this in order. Stop at first failure and fix before proceeding.

## 0) Inputs You Must Have

- `host`
- `port` (usually `3306`)
- `dbName` (Team 39 schema)
- `username`
- `password`
- SSL requirement (`required` or `not required`)

## 1) CLI Connectivity

```powershell
mysql -h <HOST> -P <PORT> -u <USERNAME> -p
```

Then run:

```sql
SHOW DATABASES;
USE <DB_NAME>;
SHOW TABLES;
```

Pass criteria:
- login succeeds
- Team 39 schema is visible
- expected tables are visible

## 2) App DB Wiring

Preferred: create `src/main/resources/db.properties.local`:

```properties
db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.username=<USERNAME>
db.password=<PASSWORD>
```

If SSL required:

```properties
db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC
db.username=<USERNAME>
db.password=<PASSWORD>
```

Fallback (if not using local properties): set env vars in PowerShell session:

```powershell
$env:IPOS_DB_URL="jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
$env:IPOS_DB_USERNAME="<USERNAME>"
$env:IPOS_DB_PASSWORD="<PASSWORD>"
```

If SSL required:

```powershell
$env:IPOS_DB_URL="jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC"
```

## 3) Build + Startup

From `IPOS-PU`:

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd javafx:run
```

Pass criteria:
- app starts
- no DB connection exception at startup
- startup log prints the shared host URL (not localhost)

## 4) Write/Read Permission Smoke

Execute these flows:

1. Non-commercial registration (writes `users`, `email_outbox`)
2. Commercial registration (writes `commercial_applications`)
3. Add-to-cart + checkout (writes `orders`, `order_items`, `payments`)
4. Promotions page open and add item (updates `campaign_metrics`)
5. Generate a report (reads aggregation queries)

Pass criteria:
- each operation succeeds
- expected rows/counters are visible in DB

## 5) Cross-Team Integration Smoke

- Confirm Team 37 can read commercial handoff object
- Confirm Team 38 stock-facing object(s) are readable/writable as agreed
- Confirm no permission-denied errors in agreed integration paths

## 6) Failure Cases To Intentionally Test

- wrong password -> authentication failure
- wrong DB name -> schema not found
- SSL mismatch -> connection failure
- missing permission -> operation denied

Capture one screenshot/log per failure and fix.

## 7) Exit Criteria

- All critical flows pass against shared DB
- Schema mapping document updated
- Known issues logged with owners + deadlines
