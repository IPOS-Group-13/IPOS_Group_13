# IPOS-PU

InfoPharma **public portal** (JavaFX): shop, checkout, tracking, membership, commercial applications, admin campaigns, reports, mail outbox / SMTP.

## Quick start

1. Copy `src/main/resources/db.properties.local.example` → `db.properties.local` and fill in MySQL (and optional mail) settings.
2. Run:

```powershell
.\mvnw.cmd javafx:run
```

## Documentation

Everything that used to be spread across multiple markdown files is consolidated here:

**[docs/guide/MASTER.md](docs/guide/MASTER.md)**  
**[docs/guide/PROJECT_OVERVIEW.md](docs/guide/PROJECT_OVERVIEW.md)** — thorough bullet overview: parts of the system and why they exist.  
**[docs/guide/TECHNOLOGY_ROLES.md](docs/guide/TECHNOLOGY_ROLES.md)** — what Maven, JavaFX, CSS, JDBC, Jakarta Mail, and JUnit are used for.  
**[docs/guide/DEMO_PREP.md](docs/guide/DEMO_PREP.md)** — architecture and likely demo questions.  
**[docs/guide/DEMO_PREP_IMPL_QUESTIONS.md](docs/guide/DEMO_PREP_IMPL_QUESTIONS.md)** — long “how / what we used” question list.

Operational SQL for shared DB setup lives in **`docs/sql/`** (queue, grants, optional triggers).
