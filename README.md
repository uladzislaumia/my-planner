# My Planner

Modern Android task management application built with industry-standard technologies and architecture.

## 🛠 Tech Stack

*   **UI:** [Jetpack Compose](class://androidx.compose.ui.Modifier) with Material 3.
*   **Dependency Injection:** [Hilt](class://dagger.hilt.android.HiltAndroidApp) (Dagger-based, compile-time safety).
*   **Local Database:** [Room](class://androidx.room.Database) with KSP (re-active Flow support).
*   **Remote Database:** Firebase Firestore **[TBD]**.
*   **Authentication:** Firebase Auth.
*   **Push Notifications:** Firebase Cloud Messaging (FCM) **[TBD]**.
*   **Monitoring & Analytics:** [Sentry](symbol://sentry) & Google Analytics **[TBD]**.
*   **Networking:** GraphQL via [Apollo Kotlin](symbol://apollo) **[TBD]**.
*   **Asynchrony:** Kotlin Coroutines & Flow.
*   **Build System:** Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`).
*   **Quality Assurance:** [KtLint](symbol://ktlint) (style formatting) & [Detekt](symbol://detekt) (static analysis).
*   **CI/CD:** GitHub Actions **[TBD]**.

## 🏗 Architecture

The project follows **Clean Architecture** principles:
*   **Domain Layer:** Business logic, [Use Cases](class://com.uladzislaumia.myplanner.domain.usecase.LoginUseCase) (utilizing `operator invoke`), and pure models.
*   **Data Layer:** [Repository pattern](class://com.uladzislaumia.myplanner.domain.repository.AuthRepository) implementation, Local ([Room](class://com.uladzislaumia.myplanner.data.local.database.AppDatabase)) and Remote (Firebase) sources.
*   **UI Layer:** [MVI/MVVM](class://com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel) with reactive state handling.

## 📸 Screenshots [TBD]

| Login | Registration | Planner Grid |
|:---:|:---:|:---:|
| _Coming Soon_ | _Coming Soon_ | _Coming Soon_ |

## 🚀 Progress & Roadmap

- [x] Basic Clean Architecture structure.
- [x] Room Persistence with reactive updates.
- [x] Firebase Authentication Integration.
- [x] Dagger Hilt automation.
- [x] **Static Analysis** (KtLint & Detekt).
- [ ] **CI/CD Pipeline** (GitHub Actions for build & lint) — **[TBD]**.
- [ ] **Testing Suite** (Unit tests with MockK & Turbine, UI Tests) — **[TBD]**.
- [ ] **GraphQL Integration** (Apollo Client) — **[TBD]**.
- [ ] **Cloud Sync** (Firebase Firestore) — **[TBD]**.
- [ ] **Messaging** (Push via FCM) — **[TBD]**.
- [ ] **Observability** (Sentry & Analytics) — **[TBD]**.
- [ ] **Jetpack Navigation 3** (Type-safe navigation) — **[TBD]**.
- [ ] **Multimodularity** (Refactoring into `:core` and `:feature` modules) — **[TBD]**.

## ⚙️ Development Requirements

*   Kotlin 2.4.20.
*   **Secrets:** Real Firebase `google-services.json` is required in the `app/` directory (excluded from VCS for security).
*   **Linting:** 
    *   Run `./gradlew ktlintCheck` to check style.
    *   Run `./gradlew ktlintFormat` to fix style issues automatically.
    *   Run `./gradlew detekt` for static analysis.
