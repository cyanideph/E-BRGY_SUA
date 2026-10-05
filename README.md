# 🌊 e-Barangay Sua

<p align="center"><strong>Service. Community. Sua.</strong><br/>A modern Android civic-services platform for Barangay Sua, San Juan, Southern Leyte.</p>

<p align="center"><a href="https://github.com/cyanideph/E-BRGY_SUA/actions/workflows/build-apk.yml"><img src="https://github.com/cyanideph/E-BRGY_SUA/actions/workflows/build-apk.yml/badge.svg" alt="Android APK build"/></a> <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/> <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=android&logoColor=white" alt="Jetpack Compose"/> <img src="https://img.shields.io/badge/Min%20SDK-24-3DDC84?logo=android&logoColor=white" alt="Minimum Android SDK"/></p>

---

## ✨ What is e-Barangay Sua?

**e-Barangay Sua** is a native Android civic-services application designed to bring barangay services, community information, document requests, emergency reporting, and resident administration into one mobile-first experience.

The product combines a coastal community identity with **soft skeuomorphism**: rounded surfaces, subtle depth, ocean-inspired colors, warm gold accents, and clear civic actions.

> **Service. Community. Sua.**

## 🧭 Resident experience

- 🏠 **Home** — community overview, shortcuts, announcements and important actions
- 🧾 **Services** — browse barangay services and start document requests
- 🌊 **Sua** — community hub and local information
- 📣 **Alerts** — announcements, events and notifications
- 👤 **Profile** — resident profile, settings and administrative access
- 🚨 **Emergency** — SOS/emergency reporting with optional location data
- 📋 **My Requests** — track submitted service requests and status timelines
- 🤝 **Assistant** — in-app civic assistance surface
- 🔔 **Notifications** — request, announcement and emergency updates

## 🛡️ Administrative portal

The navigation model includes a dedicated administrative experience for request management, residents, households, announcements, events, emergencies, reports, and audit logs.

## 🎨 Design system

e-Barangay Sua uses a **Coastal Community Soft-Skeuomorphism** direction. The interface is intentionally warmer and more tactile than a standard Material-only civic app.

| Element | Direction |
|---|---|
| Mood | Coastal, welcoming, civic |
| Primary | Deep ocean blue |
| Secondary | Sea teal |
| Highlight | Warm gold |
| Emergency | Coral / high-attention treatment |
| Surfaces | Soft rounded cards with subtle depth |
| Navigation | Five-tab mobile bottom navigation |
| Theme | Light, Dark and System modes |

Reusable UI primitives include **SoftSkeuomorphicCard**, **SuaWaveHeader**, **StatusBadges**, **DigitalResidentIdCard**, **DocumentQrCard**, **QuickEmergencyBottomSheet**, and **GlobalOfflineSyncPill**.

## 🏗️ Architecture

The project is a native **Kotlin + Jetpack Compose** Android application.

```text
app/
├── data/
│   ├── BarangayRepository.kt
│   └── DemoData.kt
├── model/
│   ├── Announcement.kt
│   ├── AuditLog.kt
│   ├── Emergency.kt
│   ├── Event.kt
│   ├── Notification.kt
│   ├── Official.kt
│   ├── Service.kt
│   └── User.kt
├── services/
│   └── Appwrite.kt
└── ui/
    ├── components/
    ├── navigation/
    ├── screens/
    └── theme/
```

Navigation is centralized through the **Screen** route model and a lightweight in-memory back stack. Main destinations are **Home, Services, Sua, Alerts, and Profile**.

## 🔌 Appwrite integration

The Android client is prepared for **Appwrite** as its backend platform and exposes:

- Account
- TablesDB
- Storage
- Realtime
- Connectivity health check

The current data model identifiers cover:

- users
- residents
- services
- document requests
- announcements
- events
- emergency reports
- notifications
- audit logs
- resident file storage

The Settings screen includes an **Appwrite Connection** test using the Appwrite ping endpoint. This gives the app a simple mobile-to-backend connectivity check before each feature is moved to live data.

**Security:** no Appwrite API key is stored in the mobile application. Production authorization should be enforced through authenticated sessions, resource permissions, and server-side controls.

## 🧪 Current data strategy

The codebase currently contains a substantial **demo/repository data layer** so resident and administrative flows can be exercised without a fully populated backend.

**BarangayRepository** currently manages in-memory state for the resident session, services, requests, announcements, events, emergency reports, residents, households, officials, notifications, audit logs, online/offline state, and theme mode.

> **Demo data is development/test data and must not be treated as production resident records.**

## 🧰 Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.x |
| UI | Jetpack Compose + Material 3 |
| Design | Custom soft-skeuomorphic components |
| Build | Android Gradle Plugin 9.1.x + Gradle 9.3.1 |
| Java | JDK 17 |
| Minimum Android | API 24 |
| Target Android | API 36 |
| Compile SDK | API 37 |
| Backend | Appwrite Android SDK 28.0.0 |
| Local data | Room |
| Networking | Retrofit + OkHttp + Moshi |
| Async | Kotlin Coroutines |
| Images | Coil |
| Location | Google Play Services Location |
| Testing | JUnit, Robolectric, Compose UI tests, Espresso |
| CI | GitHub Actions |

## 📱 Build locally

Requirements: Android Studio, JDK 17, Git, and the Android SDK required by the current dependency graph.

```bash
git clone https://github.com/cyanideph/E-BRGY_SUA.git
cd E-BRGY_SUA
./gradlew :app:assembleDebug
```

The debug APK is generated under **app/build/outputs/apk/debug/**.

## ⚙️ Environment

The repository includes **.env.example** for development-time configuration.

Never commit real credentials, API keys, signing passwords, or production secrets. Sensitive server credentials must remain server-side.

## 🤖 Continuous integration

GitHub Actions builds the debug APK automatically when changes reach **main** and also supports manual dispatch.

```text
GitHub push
    ↓
Android build
    ↓
APK artifact
    ↓
Android device testing
    ↓
Ramus smoke tests
    ↓
Human review
```

The current workflow installs JDK 17, Gradle 9.3.1, Android SDK tooling, the Android 17 / API 37 preview platform required by the dependency graph, creates a debug keystore, runs the debug APK build, and uploads the APK artifact.

## 🧪 Recommended smoke test

```text
Launch → Splash / onboarding → Home
                    ↓
             Bottom navigation
                    ↓
              Services → Request
                    ↓
            Profile → Settings
                    ↓
         Appwrite Connection → Test
```

Emergency path:

**Home/Sua → Emergency → create SOS report → location/details → status and notification**

## 🔐 Production hardening

- Connect resident authentication to Appwrite
- Enforce role-based permissions server-side
- Replace demo records with live TablesDB data
- Connect resident uploads to Storage
- Connect Realtime notifications
- Complete offline synchronization and conflict handling
- Validate audit logs end-to-end
- Add privacy, retention, backup and recovery policies
- Configure production signing and release validation
- Run accessibility and security audits

## 🗺️ Roadmap

### Foundation

- [x] Native Kotlin + Compose application
- [x] Five-tab resident navigation
- [x] Resident and admin screen structure
- [x] Soft-skeuomorphic civic design system
- [x] Demo repository/data layer
- [x] Appwrite Android SDK integration
- [x] Appwrite connectivity test
- [x] Automated debug APK workflow

### Integration

- [ ] Appwrite resident authentication
- [ ] Live services and requests
- [ ] Resident file storage
- [ ] Realtime notifications
- [ ] Production roles and permissions
- [ ] Offline synchronization
- [ ] End-to-end audit logging

### Production

- [ ] Production signing
- [ ] Full instrumentation suite
- [ ] Automated device testing
- [ ] Accessibility audit
- [ ] Security review
- [ ] Privacy/data-retention documentation
- [ ] Release candidate validation

## 🌴 About Sua

e-Barangay Sua is designed around the community identity of **Barangay Sua, San Juan, Southern Leyte**.

The product direction is intentionally local: practical civic workflows, emergency-first access, community information, and a coastal visual language instead of a generic enterprise dashboard.

## 📂 Project structure

```text
E-BRGY_SUA/
├── .github/workflows/build-apk.yml
├── app/
│   ├── src/main/java/com/example/
│   │   ├── data/
│   │   ├── model/
│   │   ├── services/
│   │   └── ui/
│   ├── src/androidTest/
│   ├── src/test/
│   └── build.gradle.kts
├── gradle/libs.versions.toml
├── .env.example
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🤝 Development principles

**Local first.** Build around the real needs of Barangay Sua residents.

**Useful first.** Prioritize services, requests, alerts, community information, and emergency access.

**Trust first.** Protect resident data, make system state visible, and keep administrative actions auditable.

## 📜 License

No public license is currently declared in the repository. Until a license is added, treat the source as **all rights reserved** and do not redistribute it as an open-source project.

<p align="center"><strong>🌊 e-Barangay Sua</strong><br/><sub>Service. Community. Sua.</sub></p>
