# Feature Specification: Consent Management Screen
## E-BRGY SUA v1.1 - Data Privacy & User Consent

**Feature Branch**: `002-consent-screen` | **Created**: 2026-10-30 | **Spec Version**: 1.1.0

### Overview
Add a consent management screen for E-BRGY SUA that addresses audit finding F11 (User consent management). The feature implements first-launch consent collection, Appwrite metadata storage, opt-out functionality, and audit logging in compliance with Philippine DPA 2012.

### User Scenarios & Testing

#### User Story 1 - Onboarding Consent (Priority: P1)
**Description**: New resident encounters consent screen on first app launch before accessing any features.

**Why this priority**: Legal compliance requires explicit consent before collecting or processing resident data per Philippine DPA 2012. Without consent, the app limits non-essential data collection while preserving core functionality.

**Independent Test**: Consent screen displays on first launch; tapping "Accept" enables full features; tapping "Decline" limits data collection and shows feature restrictions.

**Acceptance Scenarios**:
1. **Given** resident has never launched the app, **When** app starts, **Then** consent screen appears modally before dashboard
2. **Given** resident taps "Accept", **When** consent stored, **Then** full app functionality accessible, consent flag set in Appwrite user metadata
3. **Given** resident taps "Decline", **When** consent rejected, **Then** core features work (login, view advisories) but non-essential data collection disabled, consent flag set to declined

**Edge Cases**:
- What if resident rotates screen during consent display? → Consent screen re-displays on next launch if not saved
- What if consent is declined then later accepted? → Settings screen allows re-consent at any time

#### User Story 2 - Settings Re-Consent (Priority: P2)
**Description**: Resident can change consent preference at any time via Settings screen.

**Why this priority**: Residents may change their mind about data collection; opt-out mechanism required for privacy compliance.

**Independent Test**: Resident navigates to Settings → Consent, taps "Change Consent", selects Accept or Decline, confirmation displayed.

**Acceptance Scenarios**:
1. **Given** resident previously declined consent, **When** they change to "Accept" in Settings, **Then** consent flag updated, full features re-enabled, audit log entry created
2. **Given** resident previously accepted consent, **When** they change to "Decline" in Settings, **Then** consent flag updated, non-essential features disabled, audit log entry created

### Functional Requirements

- **FR-001**: System MUST display consent screen on first app launch (if no prior consent record exists)
- **FR-002**: System MUST provide "Accept" button on consent screen
- **FR-003**: System MUST provide "Decline" button on consent screen
- **FR-004**: System MUST store consent status in Appwrite user metadata after acceptance
- **FR-005**: System MUST limit non-essential data collection when consent is declined
- **FR-006**: System MUST provide "Change Consent" option in Settings screen
- **FR-007**: System MUST create audit log entry when consent is given, withdrawn, or changed
- **FR-008**: System MUST remember consent decision across app restarts and configuration changes
- **FR-009**: System MUST display information about what data is collected and why
- **FR-010**: System MUST NOT prevent core functionality (login, view advisories) when consent is declined

### Key Entities

- **ConsentRecord**: Tracks consent status per user
  - `userId`: Appwrite user ID (String, FK)
  - `consentGiven`: Boolean (true = accepted, false = declined, null = not yet presented)
  - `consentDate`: Timestamp of consent decision (String, ISO format)
  - `consentVersion`: Version of consent terms (String, default "1.0.0")
  - `withdrawnAt`: Timestamp if consent later withdrawn (String, ISO format, optional)

- **AuditLog Entry** (existing entity, extended):
  - Action: "CONSENT_ACCEPTED", "CONSENT_DECLINED", "CONSENT_WITHDRAWN"
  - Performed by: Appwrite user ID
  - Target ID: Resident user ID
  - Details: "Consent version {version}, decision: {accept/decline}", timestamp

### Success Criteria

- **SC-001**: 100% of new residents encounter consent screen on first launch (measured over 30-day period)
- **SC-002**: At least 90% of residents accept consent (measured over 30-day period)
- **SC-003**: Residents who decline consent can still access core features (login, advisories)
- **SC-004**: Settings "Change Consent" function updates flag and re-enables/disables features correctly
- **SC-005**: Audit log entry created within 5 seconds of consent decision
- **SC-006**: Consent decision persists across app restarts and device reboots
- **SC-007**: No critical or high-severity security findings in merged code

### Measurable Outcomes

- **MO-001**: Consent screen displays within 3 seconds of app cold start (when no prior consent)
- **MO-002**: Consent screen dismisses within 1 second of button tap
- **MO-003**: App write user metadata updated within 2 seconds of "Accept" tap
- **MO-004**: Audit log entry appears in Appwrite console within 5 seconds of consent decision
- **MO-005**: Settings "Change Consent" responds within 2 seconds

### Assumptions

- Target devices run Android 7.0 (API 24) or higher
- Appwrite cloud project `6ac31e4000390af0f850` remains active with current configuration
- Philippine DPA 2012 compliance requires explicit consent for data processing (standard interpretation for municipal/government apps)
- MIT license terms observed for all UI components and new code
- Existing authentication system (Appwrite email/password) functions prior to consent screen

### Dependencies

- Appwrite Android SDK 25.2.0 (already integrated)
- Room DB 2.7.0 (already integrated - for local consent record caching if needed)
- Jetpack Compose Material 3 (already integrated)
- Kotlin 2.2.10 (already used throughout project)
- AGP 8.13.2 (already configured)

### Notes

- This feature addresses audit finding F11 from audit_report.md (2026-10-13)
- Consent screen should use WCAG AA contrast requirements (Principle VII)
- All string resources should be externalizable for future i18n (supports Principle VII multi-language future work)
- Audit log extension follows existing AuditLog entity pattern (Principle VI: Data Integrity)
- Consent versioning enables future term updates (v1.0.0 → v1.1.0 → etc.)
- This is v1.1 of the constitution/specification; constitution increments to v1.1.0 upon merge

## Done When

- [ ] Specification written to SPEC_FILE and validated against quality checklist
- [ ] Feature spec distinguishes implemented/partial/unknown clearly
- [ ] No [NEEDS CLARIFICATION] markers remain (max 3 limit observed)
- [ ] Success criteria are measurable and technology-agnostic
- [ ] All functional requirements are testable and unambiguous
- [ ] Cross-referenced with audit_report.md finding F11
- [ ] Constitution principle alignment documented (VI, X)
- [ ] Ready for /speckit.plan execution