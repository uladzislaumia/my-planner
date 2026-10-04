# My Planner

Modern Android task management application built with industry-standard technologies, Clean Architecture, and an **Offline-First** data strategy.

## 🛠 Tech Stack

*   **UI:** [Jetpack Compose](class://androidx.compose.ui.Modifier) with Material 3.
*   **Navigation:** [Jetpack Navigation Compose](class://androidx.navigation.compose.NavHost) with Type-Safe Routes ([kotlinx.serialization](class://com.uladzislaumia.myplanner.ui.navigation.Route)).
*   **Dependency Injection:** [Hilt](class://dagger.hilt.android.HiltAndroidApp) (Dagger-based, compile-time safety).
*   **Local Database:** [Room](class://androidx.room.Database) with KSP and reactive Flow support.
*   **Remote Database:** [Firebase Firestore](class://com.google.firebase.firestore.FirebaseFirestore) (Offline-First cloud synchronization).
*   **Authentication:** Firebase Auth.
*   **Push Notifications:** Firebase Cloud Messaging (FCM) **[TBD]**.
*   **Monitoring & Analytics:** [Sentry](symbol://sentry) & Google Analytics **[TBD]**.
*   **Networking:** GraphQL via [Apollo Kotlin](symbol://apollo) **[TBD]**.
*   **Asynchrony:** Kotlin Coroutines & Flow.
*   **Build System:** Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`).
*   **Quality Assurance:** [KtLint](symbol://ktlint) (style formatting) & [Detekt](symbol://detekt) (static analysis).
*   **CI/CD:** GitHub Actions **[TBD]**.

## 🏗 Architecture

The project follows **Clean Architecture** and **Single Activity** principles with an **Offline-First** strategy:
*   **Domain Layer:** Pure Kotlin business logic, [Use Cases](class://com.uladzislaumia.myplanner.domain.usecase.LoginUseCase) (utilizing `operator invoke`), and domain models.
*   **Data Layer:** [Repository pattern](class://com.uladzislaumia.myplanner.domain.repository.AuthRepository) implementation with [OfflineFirstPlannerRepositoryImpl](class://com.uladzislaumia.myplanner.data.repository.OfflineFirstPlannerRepositoryImpl) (Room as Single Source of Truth + Firestore background sync & 5-item demo seeding for new accounts).
*   **UI Layer:** [MVI/MVVM](class://com.uladzislaumia.myplanner.ui.viewmodel.MainViewModel) with reactive StateFlow handling and state-driven navigation via [AppNavGraph](class://com.uladzislaumia.myplanner.ui.navigation.AppNavGraph).

## 📸 Screenshots [TBD]

| Login | Registration | Planner Grid |
|:---:|:---:|:---:|
| _Coming Soon_ | _Coming Soon_ | _Coming Soon_ |

## 🚀 Progress & Roadmap

- [x] Basic Clean Architecture & Single Activity structure.
- [x] Room Persistence with reactive updates.
- [x] Firebase Authentication Integration.
- [x] **Cloud Sync & Offline-First Strategy** (Room + Firebase Firestore).
- [x] **Type-Safe Jetpack Navigation Compose** (`@Serializable` routes).
- [x] **Automatic Demo Data Seeding** (5 initial tasks for new users).
- [x] **Google Play Services Availability Checks & Package Visibility**.
- [x] Dagger Hilt automation.
- [x] **Static Analysis** (KtLint & Detekt).
- [ ] **CI/CD Pipeline** (GitHub Actions for build & lint) — **[TBD]**.
- [ ] **Testing Suite** (Unit tests with MockK & Turbine, UI Tests) — **[TBD]**.
- [ ] **GraphQL Integration** (Apollo Client) — **[TBD]**.
- [ ] **Messaging** (Push via FCM) — **[TBD]**.
- [ ] **Observability** (Sentry & Analytics) — **[TBD]**.
- [ ] **Multimodularity** (Refactoring into `:core` and `:feature` modules) — **[TBD]**.

## ⚙️ Development Requirements

*   Kotlin 2.4.20.
*   **Secrets:** Real Firebase `google-services.json` is required in the `app/` directory (excluded from VCS for security).
*   **Linting:** 
    *   Run `./gradlew ktlintCheck` to check style.
    *   Run `./gradlew ktlintFormat` to fix style issues automatically.
    *   Run `./gradlew detekt` for static analysis.
