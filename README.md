# My Planner

Modern Android task management application built with industry-standard technologies and architecture.

## 🛠 Tech Stack

*   **UI:** [Jetpack Compose](class://androidx.compose.ui.Modifier) with Material 3.
*   **Dependency Injection:** [Hilt](class://dagger.hilt.android.HiltAndroidApp) (Dagger-based, compile-time safety).
*   **Local Database:** [Room](class://androidx.room.Database) with KSP (re-active Flow support).
*   **Authentication:** Firebase Auth.
*   **Networking:** GraphQL via [Apollo Kotlin](symbol://apollo) **[TBD]**.
*   **Asynchrony:** Kotlin Coroutines & Flow.
*   **Build System:** Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`).

## 🏗 Architecture

The project follows **Clean Architecture** principles:
*   **Domain Layer:** Business logic, [Use Cases](class://com.uladzislaumia.myplanner.domain.usecase.LoginUseCase) (utilizing `operator invoke`), and pure models.
*   **Data Layer:** [Repository pattern](class://com.uladzislaumia.myplanner.domain.repository.AuthRepository) implementation, Local ([Room](class://com.uladzislaumia.myplanner.data.local.database.AppDatabase)) and Remote (Firebase) sources.
*   **UI Layer:** [MVI/MVVM](class://com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel) with reactive state handling.

## 🚀 Progress & Roadmap

- [x] Basic Clean Architecture structure.
- [x] Room Persistence with reactive updates.
- [x] Firebase Authentication Integration.
- [x] Dagger Hilt automation.
- [ ] **GraphQL Integration** (Apollo Client) — **[TBD]**.
- [ ] **Jetpack Navigation 3** (Type-safe navigation) — **[TBD]**.
- [ ] **Multimodularity** (Refactoring into `:core` and `:feature` modules) — **[TBD]**.
- [ ] **Testing Suite** (Unit tests for Use Cases, UI Tests) — **[TBD]**.

## ⚙️ Development Requirements

*   Kotlin 2.4.20.
*   Firebase `google-services.json` (Local only, excluded from VCS).
