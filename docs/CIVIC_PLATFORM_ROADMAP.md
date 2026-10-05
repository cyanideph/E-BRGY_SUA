# e-Barangay Sua — Civic Platform Phase

## Scope
This PR keeps all new work isolated from main and establishes the resident-facing civic platform foundations.

### Data flows
- Document request: resident → Appwrite documentRequests → status history → notification → audit log.
- Emergency: resident → emergencyReports → official acknowledgement/response → notification → audit log.
- Facilities: one canonical backend dataset consumed by map and directory UI.
- GIS: verified Sua administrative identity remains distinct from optional authenticated boundary geometry.

## Backend tables
Existing tables should remain the source of truth:
- services
- documentRequests
- emergencyReports
- notifications
- auditLogs
- hotlines
- officials

Recommended additions:
- facilities
- requestStatusHistory
- emergencyStatusHistory
- gisBoundaries

## Security
- Never place resident PII or mutable resident attributes inside QR payloads.
- Prefer opaque IDs / short-lived verification tokens.
- Resident reads must be scoped to the authenticated user.
- Officials/admins are the only actors allowed to change request/emergency statuses.
- Every privileged mutation should generate an audit log.
- Location data should be collected only when required for the emergency/GIS feature.

## UI
The resident app should expose:
1. Sua Community Intelligence
2. Civic Map
3. Services
4. Requests + tracking
5. Emergency / disaster readiness
6. Notifications
7. Resident ID verification
8. Profile/settings

## Acceptance criteria
- No new feature relies on hardcoded facility records.
- Requests and emergencies have durable server-side lifecycle state.
- UI clearly indicates offline vs synchronized data.
- GIS boundary is never presented as verified until authenticated geometry is available.
- PR can be tested independently before merge to main.
