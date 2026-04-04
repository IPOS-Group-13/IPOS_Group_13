# UI Integration Decision

`Project UI.zip` was extracted and inspected.

## Findings

- The zip contains JavaFX Java classes:
  - `PharmacyLoginUI.java`
  - `PharmacyDashboardUI.java`
  - `SellerDashboardUI.java`
  - `ProfileUI.java`
- It does not contain FXML views or MVC controllers wired to application services.
- The screens are visual prototypes with hardcoded data and direct node construction.

## Decision

- Use the teammate UI as visual/style reference only.
- Implement integrated screens inside `IPOS-PU` using the existing project package structure:
  - FXML views under `resources/com/teesolutions/ipospu/views`
  - Controllers under `com.teesolutions.ipospu.controllers`
  - Services/repositories for real business logic and MySQL persistence.

## Rationale

- This allows fast integration with the current Maven JavaFX app and keeps all required requirements logic in one maintainable codebase.
- It avoids duplicating standalone `Application` classes with disconnected state.
