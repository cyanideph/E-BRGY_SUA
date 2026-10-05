# 🌊 e-Barangay Sua

<p align="center"><strong>Service. Community. Sua.</strong><br/>A modern Android civic-services and digital governance platform for Barangay Sua, San Juan, Southern Leyte.</p>

<p align="center">
  <a href="https://github.com/cyanideph/E-BRGY_SUA/actions/workflows/build-apk.yml"><img src="https://github.com/cyanideph/E-BRGY_SUA/actions/workflows/build-apk.yml/badge.svg" alt="Android APK build"/></a>
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=android&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Target%20SDK-36-3DDC84?logo=android&logoColor=white" alt="Target Android SDK"/>
  <img src="https://img.shields.io/badge/Compile%20SDK-37-34A853?logo=android&logoColor=white" alt="Compile SDK"/>
  <img src="https://img.shields.io/badge/Backend-Appwrite%20Cloud-FD366E?logo=appwrite&logoColor=white" alt="Appwrite"/>
  <img src="https://img.shields.io/badge/Database-Room%20(Offline%20First)-0078D4?logo=sqlite&logoColor=white" alt="Room SQLite"/>
  <img src="https://img.shields.io/badge/Tests-Robolectric%20100%25%20Passing-brightgreen?logo=junit5&logoColor=white" alt="Unit Tests"/>
</p>

---

## ✨ Overview

**e-Barangay Sua** is an official-grade, native Android civic-services application designed to bring barangay services, community advisories, digital document applications, emergency dispatch, and local government administration into one seamless, mobile-first experience.

Tailored specifically for the coastal municipality of **Barangay Sua, San Juan, Southern Leyte (Region VIII)**, the app blends modern Material 3 design with a **coastal soft-skeuomorphic aesthetic**: ocean-inspired colors, tactile depth, frosted surfaces, and emergency-first ergonomics.

---

## 📱 Core Resident Experience

- 🏠 **Home Dashboard** — Quick civic services, emergency shortcuts, live weather & storm advisory widgets, community spotlight, and latest news.
- 🧾 **Barangay Services** — Digital catalog and application workflow for essential barangay certifications:
  - Barangay Clearance (with First-Time Jobseekers Act RA 11261 waiver support)
  - Certificate of Residency
  - Certificate of Indigency (Social Welfare & Medical Assistance)
  - General Barangay Certificate
  - Certificate of Good Moral Character
  - Business Clearance & Commercial Permit
  - Other Administrative Services
- 📋 **My Requests & Live Tracking** — Track applications across each stage (`Submitted` → `Under Review` → `Processing` → `Ready for Release` → `Completed`) with dynamic QR verification cards and full milestone audit timelines.
- 🚨 **Emergency SOS Dispatch** — One-tap emergency dispatch with GPS coordinate transmission and telemetry for Tanod Immediate Dispatch, San Juan MDRRMO, Police, and Rural Health Units.
- 🌊 **Sua Community Hub** — Purok demographic directory, barangay officials directory, local facilities, and coastal marine sanctuary info.
- 📣 **Advisories & Weather Alerts** — High-priority weather warnings, community assembly notices, and public health missions.
- 👤 **Digital Resident ID & Profile** — Offline-accessible digital barangay identification card, household affiliation, and settings.
- 🔔 **In-App Notification Center** — Status updates, advisory bulletins, and emergency broadcasts.

---

## 🛡️ Administrative Portal

The app includes role-based administration features for Barangay Officials and staff:

- **Request Management** — Review incoming applications, verify uploaded requirements, attach official remarks, and update status.
- **Emergency SOS Dispatch Console** — Live triage of incoming emergencies, assign field responders (Tanod/MDRRMO), and record resolution logs.
- **Announcement Management** — Draft and broadcast emergency alerts, health mission notices, and council advisories.
- **Barangay Event Scheduling** — Create community events and monitor resident RSVPs.
- **Civic Reports & Demographic Analytics** — Application volume analytics, Purok distribution stats, and processing turnaround metrics.
- **System Audit Trail (`AdminAuditLogsScreen`)** — Immutable chronological audit logging for governance and transparency.

---

## ☁️ Backend & Data Architecture

e-Barangay Sua uses an **Offline-First Architecture** combining local SQLite persistence via **Android Room** with remote synchronization via **Appwrite Cloud**.

```text
[ Resident / Admin UI ]
           │
           ▼
 [ BarangayRepository ]
     │              │
     ▼              ▼
[ Room Database ]  [ CivicSyncService ]
(Offline Cache)            │
                           ▼
                  [ Appwrite Cloud v2.3+ ]
                   • TablesDB (Collections)
                   • Storage (Resident Files)
                   • Realtime & Health
```

### Database Collections (`ebarangay-sua-db`)

| Collection / Table | Purpose | Permissions |
|---|---|---|
| `services` | Barangay certification catalog and requirements | Public Read (`read("any")`) |
| `announcements` | Official public advisories and storm warnings | Public Read (`read("any")`) |
| `documentRequests` | Resident applications and processing states | Resident Read/Write + Admin Management |
| `requestStatusHistory` | Lifecycle transition history and timestamps | Audit tracking |
| `emergencyReports` | Active SOS telemetry and GPS coordinates | Emergency dispatch |
| `emergencyStatusHistory`| Responder notes and dispatch timelines | Dispatch history |
| `notifications` | Resident-specific in-app notifications | User-scoped |
| `auditLogs` | System-wide administrative action logs | LGU governance & compliance |
| `facilities` | Community halls, evacuation centers, and sports hubs | Public Read |

---

## 🔒 Comprehensive System Audit Trail

All critical operations in e-Barangay Sua automatically emit auditable logs:

- `CREATE_REQUEST`: Logged upon document submission with reference number.
- `UPDATE_REQUEST_STATUS`: Captures official actor, previous state, and new state (`Submitted` → `Ready for Release`).
- `EMERGENCY_SOS`: Records emergency caller details, incident type, and GPS coordinates.
- `UPDATE_EMERGENCY_STATUS`: Records dispatch response, assigned tanod/responder, and resolution notes.
- `PUBLISH_ANNOUNCEMENT`: Records title, author role, and emergency flags.
- `CREATE_EVENT`: Records community event scheduling and logistics.

Logs are dual-persisted locally in Room's `audit_logs` table and synchronized to Appwrite's `auditLogs` collection.

---

## 🧰 Technology Stack

| Layer | Component | Details |
|---|---|---|
| **Language** | Kotlin | 2.2.x (100% Kotlin DSL) |
| **UI Framework** | Jetpack Compose | Material 3 + Custom Skeuomorphic Surfaces |
| **Android SDK** | Modern Platform | `minSdk 24`, `targetSdk 36`, `compileSdk 37` (Android 16 Ready) |
| **Build System** | Gradle | AGP 9.1.x + Gradle 9.3.1 |
| **Local Storage** | Room Database | SQLite with TypeConverters & DAOs |
| **Remote Backend** | Appwrite Cloud | SDK 28.0.0 (Account, TablesDB, Storage, Realtime) |
| **Networking** | Retrofit + OkHttp | Moshi JSON serialization |
| **Testing** | Robolectric & JUnit 4 | Local JVM tests for CUJs, Room, and audit flows |
| **Image Loading** | Coil Compose | Asynchronous image rendering |
| **Location** | Google Play Services | GPS location capture for SOS dispatch |
| **CI/CD** | GitHub Actions | Automated build, verification, and APK artifact upload |

---

## ⚙️ Configuration & Environment

The project uses the **Secrets Gradle Plugin** with `.env` / `.env.example` for secure build configuration:

```properties
APPWRITE_DATABASE_ID="ebarangay-sua-db"
APPWRITE_ENDPOINT="https://sgp.cloud.appwrite.io/v1"
APPWRITE_PROJECT_ID="6ac31e4000390af0f850"
APPWRITE_RESIDENT_FILES_BUCKET_ID="resident-files"
```

> **Security Note**: Never commit service-role keys or private credentials to the client repository. The Android app connects securely using client-level publishable Project IDs and user authentication sessions.

---

## 🧪 Testing & Verification

The project includes an automated **Robolectric unit test suite** (`app/src/test/`):

To run all unit and Robolectric tests locally:

```bash
gradle :app:testDebugUnitTest
```

### Verified Test Cases:
- ✔ Android 16 runtime and application context initialization
- ✔ Civic backend contract and table name integrity
- ✔ Default dataset seeding (services, announcements, emergency hotlines)
- ✔ Room local database and DAO CRUD operations
- ✔ Document request submission and status transition audit generation
- ✔ Emergency SOS broadcast and responder dispatch audit generation
- ✔ Announcement publishing and community event scheduling audit generation
- ✔ Session authentication and role switching stability

---

## 🔨 Building the App

### Requirements
- **JDK 17** or higher
- **Android SDK** with Platform Tools API 36/37
- **Gradle 9.3.1** (or run via the Gradle toolchain)

### Assemble Debug APK
```bash
gradle :app:assembleDebug
```
The output APK is generated at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌴 About Barangay Sua

**Barangay Sua** is a vibrant coastal community in the municipality of **San Juan, Southern Leyte, Philippines**. Known for its coastal marine sanctuary, beachfront puroks, and agricultural areas, this platform was built to bridge modern digital governance with the day-to-day needs of its residents and barangay officials.

---

## 📜 License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

<p align="center">
  <strong>🌊 e-Barangay Sua</strong><br/>
  <sub>Service. Community. Sua.</sub>
</p>
