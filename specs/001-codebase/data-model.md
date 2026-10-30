# Data Model: E-BRGY SUA Domain Models
## Phase 1: Design & Contracts

### 1.1 Core Entities

#### Resident
- **Purpose**: Represents a registered community member and primary user of the system
- **Attributes**:
  - `fullName`: Complete name of the resident (String)
  - `address`: Barangay address including street, zone (String)
  - `birthDate`: Date of birth (String, format: YYYY-MM-DD)
  - `age`: Computed age from birth date (Integer, read-only)
  - `profileImageId`: Appwrite file ID for profile picture (String, optional)
  - `status`: Account status (ACTIVE, INACTIVE, PENDING_VERIFICATION) (String)
  - `dateCreated`: Timestamp of account creation (String, ISO format)
  - `lastLogin`: Timestamp of last app login (String, ISO format, optional)

- **Relationships**:
  - One-to-Many: `serviceRequests`
  - One-to-Many: `emergencyReports`
  - One-to-Many: `auditLogs`
  - One-to-Many: `notifications`

#### ServiceRequest
- **Purpose**: Represents a resident's request for a Barangay service (certification, clearance, etc.)
- **Attributes**:
  - `type`: Category of service (CERTIFICATION, CLEARANCE, PERMIT, OTHER) (String, enum)
  - `status`: Current status (PENDING, UNDER_REVIEW, APPROVED, REJECTED, COMPLETED) (String, enum)
  - `requestedBy`: Resident ID who submitted the request (String, FK → Resident)
  - `description`: Details of the requested service (String)
  - `requiredDocuments`: List of documents needed for the request (List<String>)
  - `requestDate`: When the request was submitted (String, ISO format)
  - `targetDate`: Expected completion date (String, ISO format, optional)
  - `completedDate`: When the request was fulfilled (String, ISO format, optional)
  - `assignedOfficial`: Barangay official handling the request (String, optional)
  - `resolutionNotes`: Official's notes on the decision (String, optional)

- **Relationships**:
  - Many-to-One: `requestedBy` → Resident
  - One-to-Many: `timelineEvents`

#### EmergencyReport
- **Purpose**: Represents an emergency incident reported by a resident
- **Attributes**:
  - `type`: Type of emergency (FIRE, MEDICAL, DISASTER, ACCIDENT, CRIME, OTHER) (String, enum)
  - `severity`: Level of severity (LOW, MEDIUM, HIGH, CRITICAL) (String, enum)
  - `reportedBy`: Resident who reported the emergency (String, FK → Resident)
  - `location`: Geolocation or description of incident location (String)
  - `description`: Detailed description of the emergency (String)
  - `reportedAt`: Timestamp of report submission (String, ISO format)
  - `updatedAt`: Last status update timestamp (String, ISO format)
  - `status`: Current status (ACTIVE, IN_PROGRESS, RESOLVED, CLOSED) (String, enum)
  - `resolvedAt`: Timestamp when resolved (String, ISO format, optional)
  - `resolvedBy`: Official who resolved it (String, optional)
  - `photos`: List of Appwrite file IDs for incident photos (List<String>, optional)

- **Relationships**:
  - Many-to-One: `reportedBy` → Resident
  - One-to-Many: `auditLogs`

#### Announcement
- **Purpose**: Community advisories and official notices broadcast to all residents
- **Attributes**:
  - `title`: Short headline for the announcement (String)
  - `content`: Full announcement text (String)
  - `author`: Official who created the announcement (String)
  - `priority`: Priority level (LOW, MEDIUM, HIGH, URGENT) (String, enum)
  - `expiresAt`: When the announcement expires (String, ISO format, optional)
  - `createdAt`: Timestamp of creation (String, ISO format)
  - `isPinned`: Whether this is pinned to the top of the feed (Boolean, default false)

- **Relationships**:
  - Many-to-One: `author` → Resident/Official (simplified: points to creating user)

#### BarangayOfficial
- **Purpose**: Represents an elected or appointed Barangay official
- **Attributes**:
  - `fullName`: Official's full name (String)
  - `position`: Role/position (CAPTAIN, VICE_CAPTAIN, CAPTAIN_COMMITTEE, TREASURER, SECRETARY, OTHER) (String, enum)
  - `contactPhone`: Primary contact number (String)
  - `contactEmail`: Email address (String, optional)
  - `isOnDuty`: Whether the official is currently on duty (Boolean, default false)
  - `dateAppointed`: When they took office (String, ISO format, optional)
  - `profileImageId`: Appwrite file ID for official photo (String, optional)

- **Relationships**:
  - One-to-Many: `serviceRequests` (requests assigned to this official)

### 1.2 Entity Relationship Diagram

```
┌─────────────┐       ┌─────────────────────┐       ┌─────────────────────┐
│   Resident   │──────▶│    ServiceRequest   │──────▶│   TimelineEvent     │
│ (one)        │       │ (many)              │       │ (many)              │
└─────────────┘       └─────────────────────┘       └─────────────────────┘
       │                       │                                   │
       │                       │                                   │
       ▼                       ▼                                   ▼
│   EmergencyReport   │     │     Announcement              │
│ (many)              │     │ (many, one author)            │
└─────────────────────┘     └───────────────────────────────┘
          │
          │
          ▼
      │
      ▼
  BarangayOfficial
  (one)
```

### 1.3 Data Converters (Room ↔ Appwrite)

| Room Type | Appwrite Type | Converter |
|-----------|--------------|-----------|
| `String` | `String` | Default |
| `List<String>` | `List<String>` | RoomTypeConverters.listString() |
| `RequestStatus` (enum) | `String` (Appwrite stores as string) | RoomTypeConverters.requestStatus() |
| `EmergencyType` (enum) | `String` (Appwrite stores as string) | RoomTypeConverters.emergencyType() |
| `EmergencyStatus` (enum) | `String` (Appwrite stores as string) | RoomTypeConverters.emergencyStatus() |
| `Boolean` | `Boolean` | Default |
| `Long` (timestamp) | `String` (ISO) | Custom converter for Date → String |

### 1.4 Validation Rules

| Entity | Field | Rule | Error Message |
|--------|-------|------|---------------|
| Resident | `fullName` | Required, min 2 chars, max 100 | "Full name is required (2-100 characters)" |
| Resident | `address` | Required | "Barangay address is required" |
| Resident | `birthDate` | Required, valid date, must be < 100 years ago | "Valid birth date required (under 100 years)" |
| Resident | `profileImageId` | Must be valid Appwrite file ID format if provided | "Invalid profile image reference" |
| ServiceRequest | `type` | Required, must be valid enum value | "Service type is required" |
| ServiceRequest | `status` | Required, must be valid enum value | "Request status is required" |
| ServiceRequest | `requestedBy` | Must reference existing Resident | "Invalid resident reference" |
| ServiceRequest | `requiredDocuments` | Optional, if provided must be List<String> | "Required documents must be a list" |
| EmergencyReport | `type` | Required, must be valid enum value | "Emergency type is required" |
| EmergencyReport | `severity` | Required, must be valid enum value | "Severity level is required" |
| EmergencyReport | `reportedBy` | Must reference existing Resident | "Invalid resident reference" |
| EmergencyReport | `location` | Required, min 5 chars | "Location description is required (min 5 characters)" |
| EmergencyReport | `reportedAt` | Must be valid ISO timestamp | "Invalid report timestamp" |
| Announcement | `title` | Required, min 3 chars, max 200 | "Announcement title is required (3-200 characters)" |
| Announcement | `content` | Required | "Announcement content is required" |
| Announcement | `priority` | Optional, if provided must be valid enum | "Invalid priority level" |
| BarangayOfficial | `fullName` | Required, min 2 chars | "Official name is required (2+ characters)" |
| BarangayOfficial | `position` | Required, must be valid enum | "Position is required (valid role)" |
| BarangayOfficial | `contactPhone` | Required, must match PH phone format | "Valid Philippine phone number required" |

### 1.5 State Transitions

#### ServiceRequest States
```
PENDING → UNDER_REVIEW → (APPROVED | REJECTED) → COMPLETED
     ↘
      → CANCELLED (by resident or system)
```

#### EmergencyReport States
```
ACTIVE → IN_PROGRESS → RESOLVED → CLOSED
```
- ACTIVE: Initial state, awaiting official assignment
- IN_PROGRESS: Official is responding/acting on the report
- RESOLVED: Issue has been addressed
- CLOSED: Formal closure after verification

#### Resident Account States
```
PENDING_VERIFICATION → ACTIVE → (DEACTIVATED | ARCHIVED)
```
- PENDING_VERIFICATION: New account, awaiting admin approval
- ACTIVE: Fully operational resident
- DEACTIVATED: Account suspended (e.g., ToS violation)
- ARCHIVED: Inactive for extended period (retention policy)

---

**Design Notes**:
- All enums stored as Strings in both Room and Appwrite for compatibility
- Timestamps use ISO 8601 format for cross-system compatibility
- Foreign keys reference Appwrite user IDs and Resident document IDs
- Soft deletes used where applicable (no hard deletes on resident data per audit F17)
- Audit logs track all state changes with before/after snapshots