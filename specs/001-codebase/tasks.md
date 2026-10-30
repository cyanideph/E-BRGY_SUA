# tasks.md - E-BRGY SUA Feature Implementation

**Feature**: E-BRGY SUA - Barangay Management System  
**Version**: 1.0  
**Generated**: 2026-10-30  
**Source**: plan.md, spec.md, data-model.md, research.md, quickstart.md

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Project initialization and basic structure

- [ ] T001 Initialize Android Studio project with AGP 8.13.2 and Kotlin 2.2.10
- [ ] T002 Configure Appwrite SDK 25.2.0 dependency and `.env` file setup
- [ ] T003 Setup Room DB 2.7.0 with migration strategy for schema versioning
- [ ] T004 Add Git ignore for `.env` and `.gradle` directories
- [ ] T005 Initialize Git repository with initial commit structure

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T006 Create BarangayRepository interface encapsulating Appwrite + Room data sources
- [ ] T007 Implement Resident Room entity with RoomTypeConverters for String lists and enums
- [ ] T008 Implement ServiceRequest Room entity with RequestStatus enum converter
- [ ] T009 Implement EmergencyReport Room entity with EmergencyStatus enum converter
- [ ] T010 Implement Announcement Room entity with priority enum converter
- [ ] T011 Implement BarangayOfficial Room entity with position enum converter
- [ ] T012 Create Appwrite Resident data source with CRUD operations
- [ ] T013 Create Appwrite ServiceRequest data source with status transition methods
- [ ] T014 Create Appwrite EmergencyReport data source with realtime event methods
- [ ] T015 Create Appwrite Announcement data source with realtime subscription
- [ ] T016 Create Appwrite BarangayOfficial data source with retrieval methods
- [ ] T017 Setup Authentication repository with Appwrite auth state management
- [ ] T018 Configure error handling and logging infrastructure (per constitution VII)
- [ ] T019 Setup environment configuration management via `.env` and BuildConfig
- [ ] T020 Add Constance compliance checklist to CI/CD pipeline

---

## Phase 3: User Story 1 - Resident Login & Onboarding (Priority: P1) 🎯 MVP

**Goal**: Resident can log in using email/password via Appwrite SDK within 5 seconds (AC-001)  
**Independent Test**: Appwrite authentication succeeds, user session established, Room DB cache populated (verify within 5 seconds, no crash on valid credentials)

### Tests for User Story 1

- [ ] T021 [P] [US1] Write unit test for Appwrite auth repository login flow (set up before implementation, expect FAIL)
- [ ] T022 [P] [US1] Write Robolectric test for resident login UI flow

### Implementation for User Story 1

- [ ] T023 [P] [US1] Create AuthRepository class in `src/main/java/com/ebrgy/sua/domain/auth/AuthRepository.kt`
- [ ] T024 [P] [US1] Implement AppwriteAuthProvider in `src/main/java/com/ebrgy/sua/data/provider/appwrite/AuthProvider.kt`
- [ ] T025 [P] [US1] Create LoginScreen composable in `ui/resident/login/LoginScreen.kt`
- [ ] T026 [P] [US1] Implement login form validation in `ui/resident/login/LoginFormValidator.kt`
- [ ] T027 [US1] Connect AuthRepository to LoginScreen via ViewModel in `ui/resident/login/LoginViewModel.kt`
- [ ] T028 [US1] Add navigation from LoginScreen to HomeDashboard on successful auth
- [ ] T029 [US1] Verify Room DB cache populated with resident data after login

---

## Phase 4: User Story 2 - View Advisories (Priority: P1)

**Goal**: Home dashboard displays at least 3 latest advisories from Appwrite database within 3 seconds (AC-002)  
**Independent Test**: List of advisories fetched from Appwrite, each displays correct title/priority, no empty states for populated data (verify within 3 seconds)

### Tests for User Story 2

- [ ] T030 [P] [US2] Write unit test for Announcement data fetching from Appwrite
- [ ] T031 [P] [US2] Write Robolectric test for AdvisoriesScreen UI display

### Implementation for User Story 2

- [ ] T032 [P] [US2] Create Announcement Room entity extension for Appwrite mapping
- [ ] T033 [P] [US2] Implement AppwriteAnnouncementProvider in `src/main/java/com/ebrgy/sua/data/provider/appwrite/AnnouncementProvider.kt`
- [ ] T034 [P] [US2] Create AdvisoriesScreen composable in `ui/resident/advisories/AdvisoriesScreen.kt`
- [ ] T035 [P] [US2] Implement advisory list adapter with Material 3 typography in `ui/resident/advisories/AdvisoryAdapter.kt`
- [ ] T036 [P] [US2] Implement pull-to-refresh mechanism in AdvisoriesScreen
- [ ] T037 [US2] Connect Announcement data source to AdvisoriesScreen via ViewModel
- [ ] T038 [US2] Display badge with priority level (LOW/MEDIUM/HIGH/URGENT) per announcement

---

## Phase 5: User Story 3 - Submit Service Request (Priority: P1)

**Goal**: Resident can submit a service request (AC-003) - creates entry in Appwrite `service_requests` and local Room DB cache  
**Independent Test**: Service request written to Appwrite within 3 seconds, Room DB updated, confirmation shown (no validation errors for required fields)

### Tests for User Story 3

- [ ] T039 [P] [US3] Write unit test for service request submission flow
- [ ] T040 [P] [US3] Write Robolectric test for service request submit UI flow

### Implementation for User Story 3

- [ ] T041 [P] [US3] Create ServiceRequest Room entity mapping in data model
- [ ] T042 [P] [US3] Implement AppwriteServiceRequestProvider in `src/main/java/com/ebrgy/sua/data/provider/appwrite/ServiceRequestProvider.kt`
- [ ] T043 [P] [US3] Create ServiceRequest form composable in `ui/resident/services/ServiceRequestForm.kt`
- [ ] T044 [P] [US3] Implement form validation for service type, resident info in `ui/resident/services/ServiceFormValidator.kt`
- [ ] T045 [P] [US3] Implement ServiceRequest submission handler in `ui/resident/services/ServiceRequestHandler.kt`
- [ ] T046 [US3] Connect ServiceRequest data source to form via ViewModel in `ui/resident/services/ServiceRequestViewModel.kt`
- [ ] T047 [US3] Display submission confirmation snackbar on success
- [ ] T048 [US3] Verify Room DB `DocumentRequestEntity` cached after submission

---

## Phase 6: User Story 4 - Report Emergency (Priority: P1)

**Goal**: Resident can report an emergency with realtime notification to officials (AC-004)  
**Independent Test**: Emergency report written to Appwrite within 2 seconds, realtime notification dispatched, Room DB entity created with correct fields (severity-based visual indicator displayed)

### Tests for User Story 4

- [ ] T049 [P] [US4] Write unit test for emergency report submission flow
- [ ] T050 [P] [US4] Write Robolectric test for emergency report UI flow

### Implementation for User Story 4

- [ ] T051 [P] [US4] Create EmergencyReport Room entity mapping in data model
- [ ] T052 [P] [US4] Implement AppwriteEmergencyReportProvider in `src/main/java/com/ebrgy/sua/data/provider/appwrite/EmergencyReportProvider.kt`
- [ ] T053 [P] [US4] Create EmergencyReportScreen composable in `ui/resident/emergency/EmergencyReportScreen.kt`
- [ ] T054 [P] [US4] Implement emergency type and severity enum selectors in `ui/resident/emergency/EmergencyForm.kt`
- [ ] T055 [P] [US4] Implement realtime subscription for emergency notifications in `ui/resident/emergency/ EmergencyRealtimeListener.kt`
- [ ] T056 [P] [US4] Implement location input with geolocation support
- [ ] T057 [US4] Connect EmergencyReport data source to screen via ViewModel
- [ ] T058 [US4] Display severity-based color-coding (CRITICAL=red, HIGH=orange, etc.)

---

## Phase 7: User Story 5 - Admin Approves Service Request (Priority: P2)

**Goal**: Barangay Official can approve/reject a service request; resident receives status notification (AC-005)  
**Independent Test**: Appwrite DB entry updated within 2 seconds, resident sees status change, audit log entry created (verify audit trail)

### Tests for User Story 5

- [ ] T059 [P] [US5] Write unit test for admin approval/rejection flow
- [ ] T060 [P] [US5] Write Robolectric test for admin dashboard approval action

### Implementation for User Story 5

- [ ] T061 [P] [US5] Create admin dashboard composable in `ui/admin/dashboard/AdminDashboard.kt`
- [ ] T062 [P] [US5] Implement service request list for admin with status badges in `ui/admin/services/AdminServiceRequestList.kt`
- [ ] T063 [P] [US5] Implement approve action handler in `ui/admin/services/AdminRequestHandler.kt`
- [ ] T064 [P] [US5] Implement reject action handler with resolution notes in `ui/admin/services/AdminRequestReject.kt`
- [ ] T065 [P] [US5] Implement resident notification on status change in `ui/resident/notifications/StatusNotificationHandler.kt`
- [ ] T066 [P] [US5] Create audit log entry on approval/rejection in `src/main/java/com/ebrgy/sua/data/AuditLogManager.kt`
- [ ] T067 [US5] Add status change confirmation UI feedback

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T068 [P] Update documentation across all UI screens per constitution III (real backend data)
- [ ] T069 [P] Code cleanup and refactoring - ensure clean architecture layer separation
- [ ] T070 [P] Performance optimization across all screens (cold start under 2s, AC-009)
- [ ] T071 [P] Additional unit tests for remaining components (if requested)
- [ ] T072 [P] Security hardening - implement SSL pinning (addresses F1 from audit)
- [ ] T073 [P] Add ContentDescription for all Compose interactive elements (addresses F12)
- [ ] T074 [P] Implement brute-force protection on Appwrite auth (addresses F2 from audit)
- [ ] T075 [P] Run quickstart.md validation scenarios S1-S5
- [ ] T076 [P] Constitution compliance checklist review for all merged changes
- [ ] T077 [P] Address remaining audit findings (F3, F5, F9, F11, F13, F15, F17 as deferred with justification)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2)
- **Polish (Phase 8)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1) - Resident Login**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1) - View Advisories**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 3 (P1) - Submit Service Request**: Can start after Foundational (Phase 2) - Requires Resident model from US1
- **User Story 4 (P1) - Report Emergency**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 5 (P2) - Admin Approves**: Can start after Foundational (Phase 2) - Requires ServiceRequest model from US3

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: Foundational Phase

```bash
# Launch all data source implementations in parallel (different files, no dependencies):
Task: "Create Resident Room entity in src/main/java/com/ebrgy/sua/data/model/ResidentEntity.kt"
Task: "Create ServiceRequest Room entity in src/main/java/com/ebrgy/sua/data/model/ServiceRequestEntity.kt"
Task: "Create EmergencyReport Room entity in src/main/java/com/ebrgy/sua/data/model/EmergencyReportEntity.kt"
Task: "Create Announcement Room entity in src/main/java/com/ebrgy/sua/data/model/AnnouncementEntity.kt"
Task: "Create BarangayOfficial Room entity in src/main/java/com/ebrgy/sua/data/model/BarangayOfficialEntity.kt"

# Launch all Appwrite data source setups in parallel:
Task: "Create Appwrite Resident data source in src/main/java/com/ebrgy/sua/data/provider/appwrite/ResidentDataSource.kt"
Task: "Create Appwrite ServiceRequest data source in src/main/java/com/ebrgy/sua/data/provider/appwrite/ServiceRequestDataSource.kt"
Task: "Create Appwrite EmergencyReport data source in src/main/java/com/ebrgy/sua/data/provider/appwrite/EmergencyReportDataSource.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Resident Login)
4. **STOP and VALIDATE**: Test User Story 1 independently - verify login succeeds within 5s, Room DB cached
5. Deploy/demo if ready - this is the MVP

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 (Resident Login) → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 (View Advisories) → Test independently → Deploy/Demo
4. Add User Story 3 (Submit Service Request) → Test independently → Deploy/Demo
5. Add User Story 4 (Report Emergency) → Test independently → Deploy/Demo
6. Add User Story 5 (Admin Approves) → Test independently → Deploy/Demo
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Resident Login)
   - Developer B: User Story 2 (View Advisories)
   - Developer C: User Story 3 (Submit Service Request)
   - Developer D: User Story 4 (Report Emergency)
   - Developer E: User Story 5 (Admin Approves)
3. Stories complete and integrate independently
4. Final integration and polish in Phase 8

---

## Notes

- [P] tasks = different files, no dependencies within the same phase
- [USx] label maps task to specific user story for traceability per task format rules
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence

**Constitution References**: All tasks follow constitution principles (Kotlin-first, clean architecture, Appwrite as exclusive backend, environment-based secrets, real backend data, data integrity, accessibility, testing, compatibility, documentation).