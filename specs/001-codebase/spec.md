# Project Specification: E-BRGY SUA v1.0
## Barangay Management System - Barangay Sua, San Juan, Southern Leyte

**Created**: 2026-10-30 | **Status**: Initial Documentation | **Spec Version**: 1.0

***

## 1. Overview

E-BRGY SUA is a Barangay Management System for civic services and digital governance in Barangay Sua, San Juan, Southern Leyte, Philippines. The application serves both residents andBarangay officials, providing a digital platform for resident registration, service requests, emergency reports, community advisories, and administrative management.

The system is built as a offline-first Android application using Jetpack Compose, Room database for local caching, and Appwrite as the cloud backend backend. It balances modern UI capabilities with compatibility for entry-level Android devices common in the Philippines.

**Target Audience**: Residents of Barangay Sua (service requests, digital ID, advisory viewing) and Barangay officials/administrators (resident management, service approvals, reports).

***

## 2. Existing Users and Roles

| User Type | Description | Key Capabilities |
|-----------|-------------|------------------|
| **Resident** | Registered community member | View advisories, submit service requests, report emergencies, access digital ID, receive notifications |
| **Barangay Official** | Authorized community leader | Approve/reject service requests, view/reply to emergency reports, manage resident profiles, publish advisories, view analytics |
| **System Administrator** | Super-user for backend management | User management, security configuration, Appwrite rule management, database maintenance |

**Authentication**: Appwrite-based authentication with email/password. Social login not currently implemented. Session tokens managed via Appwrite SDK with Android-secure storage.

***

## 3. Existing Modules and Workflows

### 3.1 Resident-Facing Modules

- **Home Dashboard**: Overview of advisories, pending requests, and quick actions
- **Services**: Submit and track service requests (certification, clearance, etc.)
- **Emergency Reports**: Submit incident reports (fire, medical, disaster) with geolocation
- **Digital ID**: View resident profile and ID information
- **Advisories**: Read community announcements and official notices
- **Community Hub**: Participate in community discussions and polls

### 3.2 Administrative Modules

- **Resident Management**: View, create, update resident profiles and records
- **Service Request Review**: Approve, reject, or request revisions to service requests
- **Emergency Response**: Prioritize and respond to emergency reports; coordinate with relevant offices
- **Officials Directory**: View and manage Barangay official information and contact details
- **Announcements**: Publish advisories, announcements, and official communications
- **Reporting**: Generate summaries of requests, emergencies, and resident data

### 3.3 Core Workflows

1. **Resident Onboarding**: New resident registers → Appwrite auth → Room DB local cache → Admin approval → Full access
2. **Service Request**: Resident submits request → Appwrite database write → Notification to officials → Official reviews → Approval/rejection → Resident notified
3. **Emergency Report**: User submits incident → Immediate Appwrite write → Real-time notification to on-duty officials → Response tracking
4. **Admin Broadcast**: Official composes advisory → Appwrite broadcast → All residents receive push/notification

***

## 4. Existing Appwrite Integration

### 4.1 Appwrite Configuration

- **Project ID**: `6ac31e4000390af0f850`
- **Endpoint**: `https://sgp.cloud.appwrite.io/v1`
- **Database ID**: `ebarangay-sua-db`
- **Resident Files Bucket ID**: `resident-files`
- **SDK Version**: 25.2.0 (official Appwrite Android SDK)

### 4.2 Collections and Databases

The Appwrite database `ebarangay-sua-db` contains the following collections:

| Collection | Purpose | Key Fields |
|------------|---------|------------|
| `residents` | Resident profiles and authentication | `full_name`, `address`, `birthdate`, `profile_image_id`, `status` |
| `service_requests` | Service request submissions | `type`, `status`, `requested_by`, `created_at`, `updated_at` |
| `emergency_reports` | Emergency incident reports | `type`, `severity`, `location`, `reported_by`, `status`, `resolved_at` |
| `audit_logs` | Security audit trail | `action`, `performed_by`, `target_id`, `timestamp`, `details` |
| `announcements` | Community advisories and notices | `title`, `content`, `author`, `expires_at`, `priority` |
| `officials` | Barangay official records | `name`, `position`, `contact`, `is_on_duty` |

### 4.3 Storage

- **Bucket ID**: `resident-files`
- **Purpose**: Store resident profile pictures, supporting documents for service requests, emergency photos
- **Access**: Controlled via Appwrite security rules; residents can upload/retrieve own files; officials can access assigned files

### 4.4 Realtime

- Realtime subscription used for emergency report notifications and service request status updates
- Configulated via Appwrite SDK with proper authentication token handling

### 4.5 Security Rules (Current State)

Security rules are actively managed and were recently hardened per audit findings F1, F2, F08, and F14. Key provisions include:
- Resident collections: Read access limited to authenticated residents and admins; write limited to authenticated residents during onboarding
- Service requests: Read access for request originator and admins; write via API only (no direct user write to certain fields)
- Emergency reports: Broad read access for authenticated users; write limited to authenticated users with rate limiting
- Files bucket: Read access restricted; write requires proper authorization

Full security rules are documented in the Appwrite console and were last reviewed in audit_report.md (2026-10-13).

***

## 5. Existing Data Models and Relationships

### 5.1 Room Database Entities

The local Room database (`ebarangay_sua.db`) caches the following entities for offline functionality:

| Entity | Key Fields | Relationships |
|--------|------------|---------------|
| `DocumentRequestEntity` | `id`, `type`, `status`, `requested_by`, `created_at` | Linked to `residents` via requested_by |
| `EmergencyReportEntity` | `id`, `type`, `severity`, `status`, `reported_by`, `created_at` | Linked to `residents` via reported_by |
| `NotificationEntity` | `id`, `title`, `body`, `type`, `read`, `created_at` | — |
| `AuditLogEntity` | `id`, `action`, `performed_by`, `timestamp`, `details` | — |

### 5.2 Room TypeConverters

Custom converters handle Room <-> Appwrite type mapping for:
- `List<String>` (e.g., resident documents, service tags)
- `RequestStatus` enum (PENDING, APPROVED, REJECTED)
- `EmergencyType` enum (FIRE, MEDICAL, DISASTER, etc.)
- `EmergencyStatus` enum (ACTIVE, RESOLVED, CLOSED)
- `List<RequestTimelineEvent>` for service request progress tracking

### 5.3 Model Relationships (Appwrite ↔ Room)

- **One-to-Many**: One resident can have multiple service requests, emergency reports, and audit log entries
- **Many-to-One**: Each service request / emergency report belongs to one resident
- **Converters**: Room entities are populated asynchronously from Appwrite SDK calls; Room serves as local cache and offline fallback

### 5.4 Key Kotlin Data Models (selected)

- `User` (model/) - Resident user data, authentication state
- `ResidentProfile` (model/) - Full profile with address, contact, documents
- `ServiceRequest` (model/) - Service type, status, requester, timeline
- `EmergencyReport` (model/) - Incident type, severity, location, status
- `Announcement` (model/) - Title, content, author, expiration
- `BarangayOfficial` (model/) - Name, position, contact, duty status

***

## 6. Current Limitations and Unfinished Requirements

### 6.1 Known Limitations (from audit_report.md)

- **F1 (Critical)**: SSL pinning not implemented — network traffic vulnerable to MITM (partially mitigated by `usesCleartextTraffic="false"`)
- **F2 (Critical)**: No brute-force protection on Appwrite auth — repeated login attempts possible
- **F8 (High)**: Room DB schema not versioned for incremental updates — migration strategy needs definition
- **F12 (Medium)**: No ContentDescription on several Compose interactive elements — accessibility non-compliant
- **F14 (Medium)**: Mixed content risk if Appwrite endpoint HTTP fallback added — currently HTTPS-only enforced
- **F18 (Medium)**: Keystore paths may be exposed if .env committed to repo

### 6.2 Partially Implemented Functionality

- **Social Login**: Appwrite OAuth/google login configured but not fully integrated into login screen
- **Push Notifications**: Appwrite realtime used but push notification setup incomplete
- **Advanced Search**: Resident search limited to name exact match; no fuzzy or filtered search
- **Report Generation**: Administrative reports exist but are manual CSV exports; no automated scheduling

### 6.3 Planned Functionality (in Git branches, not yet merged)

- **feat/complete-resident-registration**: Enhanced resident onboarding with document upload
- **feat/appwrite-backend-authority**: Backend function for authority-based access control
- **fix/security: harden-appwrite-data-access**: Additional Appwrite security rule hardening

### 6.4 Unknown Functionality Requiring Clarification

- **F3**: Exact scope of audit logging — which specific actions must be logged vs. which are optional
- **F5**: Document retention policy for resident records and audit logs
- **F9**: Offline sync strategy when Appwrite is unreachable — current behavior on network failure
- **F11**: User consent management for data processing and privacy terms
- **F13**: Integration with municipal/civil registry systems external to Appwrite
- **F15**: Multi-language support (currently only English/Tagalog strings)
- **F17**: Automated backup strategy for Appwrite database

***

## 7. Expected Behavior and Acceptance Criteria

### 7.1 Functional Acceptance Criteria

- **AC-001**: A registered resident can log in using email/password via Appwrite SDK within 5 seconds
- **AC-002**: Home dashboard displays at least 3 latest advisories from Appwrite database within 3 seconds of launch
- **AC-003**: Submitting a service request creates an entry in Appwrite `service_requests` collection and local Room DB cache
- **AC-004**: An emergency report triggers immediate notification to on-duty Barangay Officials via Appwrite realtime
- **AC-005**: Admin can approve or reject a service request; resident receives status notification
- **AC-006**: Application functions on minSdk 24 devices (Android 7.0) with at least 1GB RAM
- **AC-007**: Screen rotation and configuration changes do not cause data loss or UI crash
- **AC-008**: All network traffic to Appwrite uses HTTPS; cleartext traffic is prohibited

### 7.2 Non-Functional Acceptance Criteria

- **AC-009**: App cold start time under 2 seconds on target devices
- **AC-010**: Room DB query response under 100ms for cached data
- **AC-011**: Appwrite API responses parsed and displayed within 2 seconds on 3G network conditions
- **AC-012**: Minimum 48dp touch targets on all interactive Compose elements
- **AC-013**: Contrast ratio of at least 4.5:1 for normal text, 3:1 for large text (WCAG AA)
- **AC-014**: Robolectric unit tests achieve 100% passing on CI pipeline
- **AC-015**: No hardcoded API keys, service tokens, or secrets in source code or git history

### 7.3 Success Criteria (Project-Level)

- **SC-01**: 100% of audit findings from audit_report.md either resolved or documented with justified deferral
- **SC-02**: New feature specs must be created via Specify/Speckit workflow before implementation
- **SC-03**: All PRs must pass CI build, Robolectric tests, and constitution compliance checklist
- **SC-04**: Zero critical or high-severity security findings in merged code

***

## 8. Assumptions

- Target devices run Android 7.0 (API 24) or higher with minimum 1GB RAM
- Internet connectivity is available but may be intermittent; application must handle offline gracefully
- Appwrite cloud project `6ac31e4000390af0f850` will remain active with current configuration
- Resident data privacy laws applicable to the Philippines (DPA 2012) are followed
- MIT license terms are observed for all open-source components

## 9. Dependencies

- Appwrite Android SDK 25.2.0
- Room 2.7.0 (local caching)
- Kotlin 2.2.10
- Jetpack Compose Material 3
- AGP 8.13.2

***

**Documentation Notes**:
- This specification documents the EXISTING system state as of 2026-10-30
- Implemented functionality is verified against codebase and audit_report.md
- Partially implemented items are marked as such with known completion status
- Planned functionality exists in git branches but is not yet merged
- Unknown items (F1-F19 from audit_report.md) require clarification before feature expansion
- No requirements are invented or claimed verified beyond what exists in the codebase