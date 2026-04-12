# IPOS-PU — What each major technology is for

Short reference for reports and vivas: **why Maven, JavaFX, CSS, JDBC, Jakarta Mail, and JUnit** are part of this project, and what role each plays in what you actually run and ship.

---

## Maven

- **What it is:** A **build and dependency tool** for Java. This project is described by a **`pom.xml`** file that lists **libraries** (JavaFX, MySQL driver, mail API, JUnit, plugins) and **how to compile, test, and run** the application.
- **What we use it for:**
  - **Downloading dependencies** automatically (so teammates do not manually install JARs).
  - **Compiling** `src/main/java` and **running tests** in `src/test/java` with the same settings everywhere (**Java 17**, encoding, etc.).
  - **Running the JavaFX app** via the **javafx-maven-plugin** (`javafx:run`) and a declared **main class** / module name.
- **Maven Wrapper (`mvnw`, `mvnw.cmd`):** pins a **consistent Maven version** so “clone and run” works even if someone has not installed Maven globally.

---

## JavaFX

- **What it is:** Java’s **rich-client UI toolkit**: windows, scenes, controls (buttons, tables, tabs, dialogs, text fields), layout panes, and lifecycle hooks for a **desktop application**.
- **What we use it for:**
  - The **entire user interface** of IPOS-PU: storefront, cart, checkout, authentication panes, admin campaign tools, reports, alerts, and optional **printing**.
  - **FXML** (`MainView.fxml`, `LoginView.fxml`): **declarative** layout (structure of panes and controls) loaded by **`FXMLLoader`** and wired to **`PortalController`** (and a minimal **`LoginController`**).
  - **Observable collections** where the UI must **update automatically** when data changes (for example cart and table contents).
  - **Background work** (`javafx.concurrent.Task`, worker threads) so **database calls** do not block **drawing and input** on the UI thread.
- **`module-info.java`** **opens** the controller packages to **`javafx.fxml`** so the framework can **inject** `@FXML` fields and event handlers by reflection—required on the Java Platform Module System.

---

## CSS (JavaFX CSS)

- **What it is:** **Stylesheets** applied to the JavaFX scene graph—similar in spirit to web CSS, but using **JavaFX-specific** properties and selectors.
- **What we use it for:**
  - **`app.css`** (and any additional stylesheets) define **colours, fonts, spacing, button appearance, table styling**, and **style classes** referenced from FXML or from code (`getStyleClass().add(...)`).
  - **Separation of concerns:** visual design is **not** hard-coded in every control; changing the look does not require rewriting business logic.
- **Why not only inline styles:** maintainability and a **consistent** look across tabs and dialogs.

---

## JDBC

- **What it is:** The **Java Database Connectivity** API (`java.sql`): **Connection**, **PreparedStatement**, **ResultSet**, **transactions**—the standard way Java talks to **relational databases** through a **driver**.
- **What we use it for:**
  - **All persistence** for IPOS-PU: users, products, orders, campaigns, payments, email outbox, external comms queue, app configuration, and **integration reads/writes** to **IPOS-CA** and **IPOS-SA** schemas when configured.
  - **Parameterised SQL** (`?` placeholders) to avoid **SQL injection** and to keep values separate from the query text.
  - **Transactions** (`setAutoCommit(false)`, `commit` / `rollback`) where multiple statements must **succeed or fail together** (for example stock deduction plus order line inserts).
- **Database product for this project:** **MySQL** — typical URLs are `jdbc:mysql://...`, and **`db.properties.local.example`** is oriented toward a MySQL server (local, Railway, or shared team host). That is the **intended** runtime for coursework and integration.

---

## Jakarta Mail

- **What it is:** The **standard Java mail API** (namespace **Jakarta Mail** after the EE/Jakarta transition). The project uses an implementation provided by the **Angus Mail** artifact on the classpath.
- **What we use it for:**
  - **Optional real SMTP:** after an email row is written to **`email_outbox`**, **`SmtpOutboxDispatcher`** can open an SMTP **Session**, build a **MimeMessage**, and **send** it—if **`mail.smtp.enabled`** and host/credentials are set in **`mail.properties`** / **`db.properties.local`** / environment variables.
  - When SMTP is **disabled**, the application **still inserts** into **`email_outbox`**; Jakarta Mail is simply **not** invoked. The **database remains the source of truth** for “what email was requested.”
- **Why Jakarta Mail specifically:** mature, standard API for SMTP with TLS/STARTTLS options suitable for coursework providers (Gmail app passwords, Mailtrap, university SMTP, etc.).

---

## JUnit

- **What it is:** A **unit testing framework** for Java. This project uses **JUnit 5** (Jupiter API and engine).
- **What we use it for:**
  - **Automated checks** of **service and utility behaviour**: authentication rules, checkout edge cases, payment validation, pricing helpers, date-range validation, model rules—see **`src/test/java`**.
  - **Regression safety** when integration or validation logic changes: tests run with **`mvn test`** (or the wrapper equivalent).
- **What we do *not* use it for:** full **end-to-end UI** automation of JavaFX screens; those flows are validated by **running the application** manually or in demos.

---

## Related documentation

- Broader architecture: [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)  
- Setup and config: [MASTER.md](MASTER.md)

---

### Optional note (only if an examiner opens `pom.xml`)

The **`pom.xml`** may still list a **SQLite JDBC** dependency and **`DatabaseManager`** contains a branch for **`jdbc:sqlite:`** URLs. For **this module’s configured workflow**, the team uses **MySQL**; SQLite is **not** part of the described deployment or integration story unless someone explicitly points `db.url` at a SQLite file. You do not need to emphasise SQLite when explaining the project unless asked.
