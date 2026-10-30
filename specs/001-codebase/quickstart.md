# Quickstart Validation Guide: E-BRGY SUA Feature Validation
## Phase 1: Design & Contracts

This guide provides runnable validation scenarios to prove the feature works end-to-end. Each scenario includes prerequisites, setup commands, test commands, and expected outcomes.

### 1. Prerequisites

- Android Studio Ladybug (or newer) with Kotlin plugin
- JDK 17 or compatible version
- Android SDK 36 (target) / 24 (min) installed
- Gradle 9.3.1 (wrapper included in project)
- Appwrite cloud project `6ac31e4000390af0f850` accessible
- `.env` file configured with Appwrite credentials (see `.env.example`)

### 2. Environment Setup

```bash
# 1. Copy .env.example to .env and fill in values
cp .env.example .env

# 2. Verify .env values (never commit .env!)
# Required variables:
#   APPWRITE_DATABASE_ID=ebarangay-sua-db
#   APPWRITE_ENDPOINT=https://sgp.cloud.appwrite.io/v1
#   APPWRITE_PROJECT_ID=6ac31e4000390af0f850
#   APPWRITE_RESIDENT_FILES_BUCKET_ID=resident-files

# 3. Start Android emulator or connect physical device
#   - Minimum SDK 24 (Android 7.0)
#   - Recommended: API 36 emulator with x86_64 architecture

# 4. Sync Gradle files in Android Studio
#   - File → Sync Project with Gradle Files

# 5. Build and install debug APK
#   - gradle :app:installDebug
#   OR via Android Studio: Run → 'app' debug configuration
```

### 3. Validation Scenarios

#### Scenario S1: Resident Login
**Purpose**: Verify Appwrite authentication works end-to-end

**Prerequisites**:
- Resident account exists in Appwrite project `6ac31e4000390af0f850`
- `.env` configured with correct project ID and endpoint

**Steps**:
1. Launch the app on emulator/device
2. Navigate to the login screen
3. Enter registered resident email and password
4. Tap "Login" button

**Expected Outcome**:
- Appwrite SDK authenticates the user successfully
- User is redirected to Home Dashboard
- Resident profile data loaded from Appwrite and displayed
- Local Room DB cache populated with resident data
- **Pass criteria**: Login completes within 5 seconds, no crash on valid credentials

**Robolectric Test**:
```kotlin
@Test fun `resident can login with valid credentials`() {
    // Given: Resident account exists in mocked Appwrite
    // When: Login attempted with valid credentials
    // Then: Authentication succeeds and user session established
}
```

#### Scenario S2: View Advisories
**Purpose**: Verify Appwrite database read and Compose UI display

**Prerequisites**:
- Announcements collection has entries in Appwrite
- Appwrite security rules allow read access for authenticated users

**Steps**:
1. Log in as a resident (see S1)
2. Navigate to "Advisories" screen from bottom navigation
3. Wait for list to load

**Expected Outcome**:
- List of advisories fetched from Appwrite `announcements` collection
- Each announcement displays: title, priority badge, expiration date (if applicable)
- Content formatted with Material 3 typography
- Pull-to-refresh triggers fresh fetch from Appwrite
- **Pass criteria**: At least 3 advisories displayed, each with valid title, no empty states for populated data

**Robolectric Test**:
```kotlin
@Test fun `resident can view advisories list`() {
    // Given: Announcements exist in mocked Appwrite
    // When: Resident navigates to Advisories screen
    // Then: List displays with correct item count and content
}
```

#### Scenario S3: Submit Service Request
**Purpose**: Verify Appwrite write, Room DB caching, and notification

**Prerequisites**:
- Resident is logged in (see S1)
- Service request type selected from available options

**Steps**:
1. From Home Dashboard, tap "Services" → select service type (e.g., "Certification")
2. Fill required fields (full name, address, contact)
3. Tap "Submit Request" button
4. Observe submission confirmation

**Expected Outcome**:
- Service request written to Appwrite `service_requests` collection
- Request status set to PENDING
- Local Room DB (`DocumentRequestEntity`) updated with new entry
- Notification triggered for Barangay Officials (via realtime)
- Submission confirmation snackbar displayed
- **Pass criteria**: 
  - Entry appears in Appwrite console within 3 seconds
  - Room DB query returns newly created entity
  - Resident receives on-screen confirmation
  - No validation errors for required fields

**Robolectric Test**:
```kotlin
@Test fun `resident can submit service request`() {
    // Given: Resident logged in, service type selected
    // When: Submit request with valid data
    // Then: Appwrite write succeeds, Room DB cached, confirmation shown
}
```

#### Scenario S4: Report Emergency
**Purpose**: Verify realtime emergency report and official notification

**Prerequisites**:
- Resident is logged in (see S1)
- Appwrite realtime configured and active

**Steps**:
1. From Home Dashboard, tap "Emergency" 
2. Select emergency type (e.g., "Medical")
3. Enter location description and description
4. Tap "Report Emergency" button
5. Observe realtime notification

**Expected Outcome**:
- Emergency report written to Appwrite `emergency_reports` collection
- Status set to ACTIVE
- Realtime subscription delivers notification to on-duty officials
- Local Room DB (`EmergencyReportEntity`) caches the report
- Emergency pin appears on map/dashboard with correct color-coding by severity
- **Pass criteria**:
  - Report appears in Appwrite within 2 seconds
  - Real-time notification received by mocked official client
  - Room DB entity created with correct fields
  - Severity-based visual indicator displayed (e.g., red for CRITICAL)

**Robolectric Test**:
```kotlin
@Test fun `resident can report emergency with realtime notification`() {
    // Given: Resident logged in, emergency details entered
    // When: Report emergency submitted
    // Then: Appwrite write + realtime notification dispatched
}
```

#### Scenario S5: Admin Approves Service Request
**Purpose**: Verify admin flow, status change, and resident notification

**Prerequisites**:
- User authenticated as Barangay Official
- Existing service request in PENDING status

**Steps**:
1. Log in as Barangay Official (different auth credentials)
2. Navigate to "Admin Dashboard" → "Service Requests"
3. Find pending request, tap "Approve" or "Reject"
4. Add resolution notes (if rejecting)
5. Confirm action

**Expected Outcome**:
- Appwrite `service_requests` collection updated: status changed to APPROVED/REJECTED
- `completedDate` set if approved
- Resident receives push notification of decision
- Official's dashboard reflects updated status
- **Pass criteria**:
  - Appwrite DB entry updated within 2 seconds
  - Resident sees status change on their screen
  - Official sees updated list reflecting their action
  - Audit log entry created for the approval/rejection action

**Robolectric Test**:
```kotlin
@Test fun `official can approve service request`() {
    // Given: Official logged in, pending request exists
    // When: Official approves request with notes
    // Then: Appwrite updated, resident notified, audit log created
}
```

### 4. CI/CD Validation

The GitHub Actions workflows provide automated validation:

- **Build APK**: `gradle :app:assembleDebug` (workflow: `.github/workflows/build-apk.yml`)
- **Run RoboTests**: `gradle :app:testDeviceDebugUnitTest` (workflow: `.github/workflows/pr-tests.yml`)
- **Verify PR**: `gradle validate` checks (workflow: `.github/workflows/pr-verify.yml`)

**Pass Criteria for Merge**:
- All Robolectric tests pass (100%)
- APK assembles without errors
- No constitution compliance violations
- Security audit findings addressed or deferred with justification

### 5. Troubleshooting

| Issue | Cause | Resolution |
|-------|-------|------------|
| Login fails | Invalid `.env` credentials or Appwrite downtime | Verify `.env` values; check internet connectivity |
| Advisories empty | Security rules restrict read access | Update Appwrite security rules; verify authenticated user |
| App crash on submit | Room DB schema mismatch | Run `gradle :app:schemamigrate`; check migration logic |
| Real-time not firing | Realtime not enabled in Appwrite console | Enable realtime in Appwrite project settings |
| Build fails | Dependency version conflict | Run `gradle clean build`; check `libs.versions.toml` |

---

**Validation Checklist**:

- [ ] S1: Resident login succeeds (5s timeout)
- [ ] S2: Advisories list displays (>0 items)
- [ ] S3: Service request submits successfully
- [ ] S4: Emergency report triggers realtime notification
- [ ] S5: Admin approves/rejects request with audit trail
- [ ] CI: `gradlew assembleDebug` succeeds
- [ ] CI: `gradlew testDeviceDebugUnitTest` passes (100%)
- [ ] No constitution compliance violations