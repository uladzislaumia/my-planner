<!--suppress HtmlDeprecatedAttribute -->
<p align="center">
  <img src="/docs/app_icon.png" alt="My Planner app icon" width="128">
</p>

<!--suppress HtmlDeprecatedAttribute -->
<h1 align="center">My Planner</h1>

<!--suppress HtmlDeprecatedAttribute -->
<p align="center">
  <a href="https://github.com/uladzislaumia/my-planner/actions/workflows/ci.yml"><img src="https://github.com/uladzislaumia/my-planner/actions/workflows/ci.yml/badge.svg" alt="CI"></a>
  <img src="https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.4.20">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose, Material 3">
  <img src="https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white" alt="minSdk 24">
  <img src="https://img.shields.io/badge/targetSdk-37-3DDC84?logo=android&logoColor=white" alt="targetSdk 37">
  <img src="https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-FFCA28?logo=firebase&logoColor=black" alt="Firebase Auth and Firestore">
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2B%20Offline--First-6750A4" alt="Clean Architecture, Offline-First">
</p>

Modern Android task management application built with industry-standard technologies, Clean Architecture, and an **Offline-First** data strategy.

## 💡 App Use Cases & Real-Life Ideas

Here are practical ways **My Planner** helps users organize their daily routine:

* 💳 **Subscription & Bill Reminders:** Track upcoming recurring payments (e.g., Spotify, Netflix, cloud storage, utility bills) before the renewal date.
* 🎟️ **Event & Ticket Bookings:** Reminders to buy football match tickets, register for free concerts or webinars, or claim early-bird passes.
* ✈️ **Travel Planning:** Packing checklists, flight ticket bookings, hotel reservations, and trip itineraries.
* 🏠 **Home & Household Chores:** Routine errands, grocery shopping lists, and home maintenance schedules.
* 🩺 **Health & Medical Appointments:** Scheduling doctor check-ups, routine medical visits, and wellness goals.

## ✨ Features

*   **Task Management:** Create, edit and delete tasks with priority, category, status and deadline. Editing happens in a bottom sheet with **debounced autosave** — no Save button, unchanged drafts are never written.
*   **Calendar Tab:** Upcoming tasks grouped by day under sticky headers (Today, Tomorrow, then each day with tasks), sorted by time. Today and Tomorrow are always shown, past days and tasks without a deadline are left out.
*   **Categories Tab:** A grid of category tiles with task counts — All Tasks, every category and No Category. Tapping a tile opens a separate screen with that category's tasks, where a new task gets the category preselected.
*   **Custom Categories:** Add your own categories with an emoji and a color.
*   **Task Cards:** Priority dot, deadline chip (time, or date and time outside the calendar, in the device's 12/24-hour format) and category chip; completed tasks are faded and struck through.
*   **Warm Design:** Cream and cocoa color scheme for light and dark themes, a background with sandy hills, and hand-drawn icons for the bottom tabs and built-in categories. All colors live in color resources with night variants; wallpaper-based dynamic color is intentionally off.
*   **Deadline Reminders:** Material 3 date & time picker and scheduled local notifications via `AlarmManager` (exact alarms when allowed, graceful fallback otherwise).
*   **Offline-First Cloud Sync:** Room is the single source of truth; changes are pushed to Firestore in the background by **WorkManager** — batched writes, network constraints, retries with backoff. Works offline, survives app restarts, and tracks deletions with tombstones so nothing comes back.
*   **Accounts:** Email & password sign-up and login with Firebase Auth; each user's data is stored in their own Firestore collection.
*   **Demo Data:** New accounts start with 5 sample tasks.
*   **Localization:** English, Spanish and Russian, with per-app language selection on Android 13+.
*   **Polished App Icon:** Adaptive launcher icon with a monochrome layer for Android 13+ themed icons and a dedicated status bar notification icon.
*   **Stability & Insights:** Firebase Crashlytics & Analytics with a custom Timber `CrashlyticsTree`, Google Play Services availability check.
*   **Tested:** Unit tests for use cases and ViewModels (MockK, Turbine, coroutines-test).

## 🛠 Tech Stack

*   **UI:** Jetpack Compose with Material 3
*   **Navigation:** Jetpack Navigation Compose with Type-Safe Routes (kotlinx.serialization)
*   **Dependency Injection:** Hilt
*   **Local Database:** Room with KSP and reactive Flow support
*   **Remote Database:** Firebase Firestore (Offline-First cloud synchronization)
*   **Authentication:** Firebase Auth
*   **Background Work:** WorkManager
*   **Notifications:** Local Scheduled Notifications (`AlarmManager` & `NotificationManager`)
*   **Analytics & Crash Reporting:** Firebase Analytics & Firebase Crashlytics (integrated with Timber logging)
*   **Testing:** JUnit 4, MockK, Turbine, kotlinx-coroutines-test
*   **UI Testing:** Compose UI Testing **[TBD]**
*   **Security:**
    *   Android Keystore (hardware-backed keys, biometric app lock via `BiometricPrompt` + `CryptoObject`) **[TBD]**
    *   Window Security (`FLAG_SECURE` for sensitive screens) **[TBD]**
    *   Root & Tamper Detection (Firebase App Check + Play Integrity API) **[TBD]**
    *   ProGuard/R8 (code shrinking & obfuscation for release builds) **[TBD]**
    *   Network Security Config (cleartext traffic disabled, debug-only user CAs) **[TBD]**
*   **Dynamic Configuration:** Firebase Remote Config **[TBD]**
*   **Push Notifications:** Firebase Cloud Messaging (FCM) **[TBD]**
*   **Monitoring:** Sentry **[TBD]**
*   **Networking:** GraphQL via Apollo Kotlin **[TBD]**
*   **Asynchrony:** Kotlin Coroutines & Flow
*   **Build System:** Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`)
*   **Quality Assurance:** KtLint (style formatting) & Detekt (static analysis), enforced in CI
*   **CI/CD:** GitHub Actions (ktlint, detekt, Android Lint, unit tests, debug APK)

## 🏗 Architecture

The project follows **Clean Architecture** and **Single Activity** principles with an **Offline-First** strategy:
*   **Domain Layer:** Pure Kotlin business logic, [Use Cases](/app/src/main/java/com/uladzislaumia/myplanner/domain/usecase) (utilizing `operator invoke`), and domain models.
*   **Data Layer:** [Repository pattern](/app/src/main/java/com/uladzislaumia/myplanner/domain/repository) implementation with [OfflineFirstPlannerRepositoryImpl](/app/src/main/java/com/uladzislaumia/myplanner/data/repository/OfflineFirstPlannerRepositoryImpl.kt) (Room as Single Source of Truth + Firestore background sync via WorkManager & 5-item demo seeding for new accounts).
*   **UI Layer:** [MVI/MVVM](/app/src/main/java/com/uladzislaumia/myplanner/ui/viewmodel/MainViewModel.kt) with reactive StateFlow handling and state-driven navigation via [AppNavGraph](/app/src/main/java/com/uladzislaumia/myplanner/ui/navigation/AppNavGraph.kt).

## 📸 Screenshots

| Login Screen | Registration Screen |
|:---:|:---:|
| <img src="/docs/screenshots/login_screen.png" alt="Login Screen" width="250"> | <img src="/docs/screenshots/signup_screen.png" alt="Registration Screen" width="250"> |

| Calendar Tab | Categories Tab | Category Screen |
|:---:|:---:|:---:|
| <img src="/docs/screenshots/calendar_tab.png" alt="Calendar Tab" width="250"> | <img src="/docs/screenshots/categories_tab.png" alt="Categories Tab" width="250"> | <img src="/docs/screenshots/category_screen.png" alt="Category Screen" width="250"> |

| Add Task Sheet | Edit Task Sheet (Autosave) |
|:---:|:---:|
| <img src="/docs/screenshots/add_task_sheet.png" alt="Add Task Sheet" width="250"> | <img src="/docs/screenshots/edit_task_sheet.png" alt="Edit Task Sheet" width="250"> |

## 🚀 Roadmap

- [ ] **All Categories Search & BottomSheet** ("View All" sheet with instant search).
- [ ] **Advanced Emoji & Color Picker** (Android Emoji Picker API & Interactive HSV Color Wheel).
- [ ] **Firebase Remote Config** (Dynamic feature flags & configuration).
- [ ] **GraphQL Integration** (Apollo Client).
- [ ] **Messaging** (Push via FCM).
- [ ] **Reopen Completed Tasks** (One-tap "Do it again" for tasks you finished before, e.g. "Clean the coffee machine" or "Pay for a subscription": reopen on demand, optionally set a new deadline and reminder, keep the last completion date).
- [ ] **Specific Exception Handling** (Replace generic `catch (e: Exception)` in repositories, `SyncWorker`, `NotificationScheduler` and `PlannerRemoteDataSource` with specific Firebase / Firestore / network exceptions, rethrow `CancellationException`; fixes detekt `TooGenericExceptionCaught`).
- [ ] **Localized Auth Errors** (Map Firebase Auth exceptions — network, weak password, email in use, invalid credentials — to own localized strings instead of showing the raw English `localizedMessage`; use one message for wrong password / unknown user to avoid account enumeration).
- [ ] **Custom Category Chip on Task Cards** (Fix: `PlannerCard` resolves categories via `Category.findById`, which only knows built-in ones, so tasks with a custom category show no chip; look them up in the categories list from `MainViewModel` instead).
- [ ] **Optimistic UI for Task Edits** (Fix stale data when reopening the edit sheet right after closing it: overlay recent edits in `MainViewModel` until Room emits them, open the sheet by task id).
- [ ] **Real-time Cloud Sync** (Replace one-shot Firestore `get()` with `addSnapshotListener` wrapped in `callbackFlow`: live updates from other devices, remote deletions via `DocumentChange.REMOVED`, fewer billed reads).
- [ ] **Secure Logout Data Wipe** (Cancel pending sync work, clear Room tables and cancel scheduled reminders on logout to prevent data leaking between accounts).
- [ ] **Reminder Restoration** (Reschedule AlarmManager reminders from Room after device reboot, app update and exact alarm permission change via `BOOT_COMPLETED` / `MY_PACKAGE_REPLACED` receiver).
- [ ] **Compose UI Testing** (UI tests for screens and bottom sheets).
- [ ] **App Security Hardening** (Android Keystore, `FLAG_SECURE`, Root & Tamper Detection, Anti-fraud SDKs (SEON, Clarity), ProGuard/R8, Network Security Config).
- [ ] **License** (Choose an open-source license, e.g. MIT, add a `LICENSE` file and a license badge).
- [ ] **Multimodularity** (Refactoring into `:core` and `:feature` modules).

## ⚙️ Development Requirements

*   Kotlin 2.4.20.
*   **Secrets:** Real Firebase `google-services.json` is required in the `app/` directory (excluded from VCS for security).
*   **Testing:** Run `./gradlew test` to execute unit tests.
*   **Linting:** 
    *   Run `./gradlew ktlintCheck` to check style.
    *   Run `./gradlew ktlintFormat` to fix style issues automatically.
    *   Run `./gradlew detekt` for static analysis.
