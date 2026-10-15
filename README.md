<!--suppress HtmlDeprecatedAttribute -->
<p align="center">
  <img src="/docs/app_icon.png" alt="My Planner app icon" width="128">
</p>

<!--suppress HtmlDeprecatedAttribute -->
<h1 align="center">My Planner</h1>

Modern Android task management application built with industry-standard technologies, Clean Architecture, and an **Offline-First** data strategy.

## 💡 App Use Cases & Real-Life Ideas

Here are practical ways **My Planner** helps users organize their daily routine:

* 💳 **Subscription & Bill Reminders:** Track upcoming recurring payments (e.g., Spotify, Netflix, cloud storage, utility bills) before the renewal date.
* 🎟️ **Event & Ticket Bookings:** Reminders to buy football match tickets, register for free concerts or webinars, or claim early-bird passes.
* ✈️ **Travel Planning:** Packing checklists, flight ticket bookings, hotel reservations, and trip itineraries.
* 🏠 **Home & Household Chores:** Routine errands, grocery shopping lists, and home maintenance schedules.
* 🩺 **Health & Medical Appointments:** Scheduling doctor check-ups, routine medical visits, and wellness goals.

## 🛠 Tech Stack

*   **UI:** [Jetpack Compose](class://androidx.compose.ui.Modifier) with Material 3.
*   **Navigation:** [Jetpack Navigation Compose](class://androidx.navigation.compose.NavHost) with Type-Safe Routes ([kotlinx.serialization](class://com.uladzislaumia.myplanner.ui.navigation.Route)).
*   **Dependency Injection:** [Hilt](class://dagger.hilt.android.HiltAndroidApp) (Dagger-based, compile-time safety).
*   **Local Database:** [Room](class://androidx.room.Database) with KSP and reactive Flow support.
*   **Remote Database:** [Firebase Firestore](class://com.google.firebase.firestore.FirebaseFirestore) (Offline-First cloud synchronization).
*   **Authentication:** Firebase Auth.
*   **Notifications:** Local Scheduled Notifications (`AlarmManager` & `NotificationManager`).
*   **Analytics & Crash Reporting:** Firebase Analytics & Firebase Crashlytics (integrated with Timber logging).
*   **Testing:** JUnit 4, [MockK](symbol://mockk), [Turbine](symbol://turbine), kotlinx-coroutines-test.
*   **UI Testing:** Compose UI Testing **[TBD]**.
*   **Security:**
    *   Android Keystore (hardware-backed keys, biometric app lock via `BiometricPrompt` + `CryptoObject`) **[TBD]**.
    *   Window Security (`FLAG_SECURE` for sensitive screens) **[TBD]**.
    *   Root & Tamper Detection (Firebase App Check + Play Integrity API) **[TBD]**.
    *   Anti-fraud SDKs (SEON, Clarity) **[TBD]**.
    *   ProGuard/R8 (code shrinking & obfuscation for release builds) **[TBD]**.
    *   Network Security Config (cleartext traffic disabled, debug-only user CAs) **[TBD]**.
*   **Dynamic Configuration:** Firebase Remote Config **[TBD]**.
*   **Push Notifications:** Firebase Cloud Messaging (FCM) **[TBD]**.
*   **Monitoring:** Sentry **[TBD]**.
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

## 📸 Screenshots

| Login Screen | Registration Screen | Planner Grid |
|:---:|:---:|:---:|
| <img src="/docs/screenshots/login_screen.png" alt="Login Screen" width="250"> | <img src="/docs/screenshots/signup_screen.png" alt="Registration Screen" width="250"> | <img src="/docs/screenshots/home_screen.png" alt="Planner Grid" width="250"> |

| Add Task Sheet | Edit Task Sheet (Autosave) |
|:---:|:---:|
| <img src="/docs/screenshots/add_task_sheet.png" alt="Add Task Sheet" width="250"> | <img src="/docs/screenshots/edit_task_sheet.png" alt="Edit Task Sheet" width="250"> |

## 🚀 Progress & Roadmap

- [x] Basic Clean Architecture & Single Activity structure.
- [x] Room Persistence with reactive updates.
- [x] Firebase Authentication Integration.
- [x] **Cloud Sync & Offline-First Strategy** (Room + Firebase Firestore).
- [x] **Type-Safe Jetpack Navigation Compose** (`@Serializable` routes).
- [x] **Full Task CRUD Operations** (Create, Read, Update, Delete with Priority & Status).
- [x] **Scheduled Local Deadline Notifications** (Exact AlarmManager reminders for task deadlines).
- [x] **Dynamic Categories & Carousel Filtering** (Large category cards, task counts per category, and custom category creation).
- [x] **Unit Testing Suite** (MockK, Turbine Flow Testing, MainDispatcherRule).
- [x] **Automatic Demo Data Seeding** (5 initial tasks for new users).
- [x] **Firebase Analytics & Crashlytics Integration** (with custom Timber `CrashlyticsTree`).
- [x] **Google Play Services Availability Checks & Package Visibility**.
- [x] Dagger Hilt automation.
- [x] **Static Analysis** (KtLint & Detekt).
- [ ] **All Categories Search & BottomSheet** ("View All" sheet with instant search) — **[TBD]**.
- [ ] **Advanced Emoji & Color Picker** (Android Emoji Picker API & Interactive HSV Color Wheel) — **[TBD]**.
- [ ] **Firebase Remote Config** (Dynamic feature flags & configuration) — **[TBD]**.
- [ ] **CI/CD Pipeline** (GitHub Actions for build & lint) — **[TBD]**.
- [ ] **GraphQL Integration** (Apollo Client) — **[TBD]**.
- [ ] **Messaging** (Push via FCM) — **[TBD]**.
- [ ] **Reopen Completed Tasks** (One-tap "Do it again" for tasks you finished before, e.g. "Clean the coffee machine" or "Pay for a subscription": reopen on demand, optionally set a new deadline and reminder, keep the last completion date) — **[TBD]**.
- [ ] **Optimistic UI for Task Edits** (Fix stale data when reopening the edit sheet right after closing it: overlay recent edits in `MainViewModel` until Room emits them, open the sheet by task id) — **[TBD]**.
- [ ] **Real-time Cloud Sync** (Replace one-shot Firestore `get()` with `addSnapshotListener` wrapped in `callbackFlow`: live updates from other devices, remote deletions via `DocumentChange.REMOVED`, fewer billed reads) — **[TBD]**.
- [ ] **Secure Logout Data Wipe** (Cancel pending sync work, clear Room tables and cancel scheduled reminders on logout to prevent data leaking between accounts) — **[TBD]**.
- [ ] **Reminder Restoration** (Reschedule AlarmManager reminders from Room after device reboot, app update and exact alarm permission change via `BOOT_COMPLETED` / `MY_PACKAGE_REPLACED` receiver) — **[TBD]**.
- [ ] **Compose UI Testing** (UI tests for screens and bottom sheets) — **[TBD]**.
- [ ] **App Security Hardening** (Android Keystore, `FLAG_SECURE`, Root & Tamper Detection, Anti-fraud SDKs (SEON, Clarity), ProGuard/R8, Network Security Config) — **[TBD]**.
- [ ] **Multimodularity** (Refactoring into `:core` and `:feature` modules) — **[TBD]**.

## ⚙️ Development Requirements

*   Kotlin 2.4.20.
*   **Secrets:** Real Firebase `google-services.json` is required in the `app/` directory (excluded from VCS for security).
*   **Testing:** Run `./gradlew test` to execute unit tests.
*   **Linting:** 
    *   Run `./gradlew ktlintCheck` to check style.
    *   Run `./gradlew ktlintFormat` to fix style issues automatically.
    *   Run `./gradlew detekt` for static analysis.
