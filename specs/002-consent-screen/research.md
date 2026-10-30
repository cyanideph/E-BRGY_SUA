# Research Findings: Consent Management Screen - E-BRGY SUA
## Phase 0: Research & Clarification

### Clarified Unknowns

| # | Unknown | Informed Resolution | Source |
|---|---------|--------------------|--------|
| 1 | **F11 - User consent management**: Exact scope and implementation approach | Informed guess: First-launch consent screen with Accept/Decline; store in Appwrite user metadata; opt-out in Settings; audit log entries; limit non-essential data when declined | audit_report.md F11 + Philippine DPA 2012 best practices |
| 2 | **Consent versioning strategy**: How to handle future consent term updates | Informed guess: Consent version field in Appwrite user metadata (String, default "1.0.0"); v1.0.0 initial; future updates as v1.1.0, v2.0.0 etc.; version displayed in Settings | Government app consent pattern standard |
| 3 | **Data collection boundaries**: What counts as "essential" vs "non-essential" data | Informed guess: Essential = login, advisories, emergency reports; Non-essential = usage analytics, optional profiling, push notification preferences | Legal compliance minimum + app functionality mapping |
| 4 | **Audit log format**: What details to include in consent audit entries | Informed guess: Action type (CONSENT_ACCEPTED/CONSENT_DECLINED/CONSENT_WITHDRAWN), consent version, decision (accept/decline), timestamp, user ID | Existing AuditLog entity pattern + compliance requirements |

### Resolved Unknowns (from codebase inspection)

| # | Resolved Question | Answer | Source |
|---|------------------|--------|--------|
| A | Appwrite SDK Version | 25.2.0 | app/build.gradle.kts; libs.versions.toml |
| B | minSdk Version | 24 (Android 7.0) | AndroidManifest.xml; build.gradle.kts |
| C | Kotlin Version | 2.2.10 | libs.versions.toml |
| D | Constitution v1.0.0 Principles | 10 principles documented | .specify/memory/constitution.md |
| E | Audit findings status | F1-F19 mapped; F11 unknown (this feature) | audit_report.md; git history |

### Research Tasks Completed

1. **Consent screen pattern research**: Standard iOS/Android consent UX patterns; modal dialog on first launch; Accept/Decline buttons; Settings re-consent; data collection gating
2. **Appwrite user metadata research**: How to store custom key-value pairs in Appwrite user profile; `updateAccount` method; `getAccount` retrieval; security rules for user metadata
3. **Audit log integration research**: How to create audit log entries via Appwrite SDK; which action strings to use; target ID format (Appwrite user ID); existing AuditLog entity fields
4. **Philippine DPA 2012 research**: Consent requirements for government/municipal apps; implied vs explicit consent; withdrawal rights; record-keeping requirements
5. **Room DB migration research**: Schema version bump strategy for new ConsentRecord entity (if using Room for local caching); Migration subclass pattern; current schema version tracking

### Dependencies & Best Practices Research

- **Appwrite user metadata limits**: 4KB per user; individual values must be strings, numbers, booleans, or null; arrays/maps not supported directly (must serialize)
- **Consent screen UX patterns**: Modal dialog recommended; full-screen overlay avoided; "Don't show again" checkbox pattern; settings-based re-consent
- **Audit log compliance**: Must track who, what, when; action strings should be consistent and enumerated; immutable log entries
- **Android lifecycle**: Consent must persist across configuration changes (use ViewModel or Application-level storage)

### Clarification Decisions (Informed Guesses)

Where the codebase lacks specification but industry standards apply:

1. **Consent screen timing**: Display on app cold start when no prior consent record exists in Appwrite user metadata. If record exists (accepted/declined), skip consent screen and proceed to dashboard.

2. **Essential vs non-essential data**: 
   - Essential (always collected, even without consent): Login credentials (Appwrite auth), advisory read tracking, emergency report submissions (core functionality)
   - Non-essential (disabled when consent declined): Usage analytics, optional feature usage stats, push notification preferences, profile image loading statistics

3. **Consent withdrawal**: If resident previously accepted and later declines via Settings, previously collected data remains (per DPA 2012 retention rules), but future non-essential data collection stops. Audit log entry created for withdrawal.

4. **Appwrite user metadata storage**: Store consent data as simple string key-value pairs:
   - `consent_status`: "accepted" | "declined" | "pending"
   - `consent_version`: "1.0.0"
   - `consent_date`: ISO timestamp

5. **Audit log action strings**: Use consistent format matching existing AuditLog entity:
   - "CONSENT_ACCEPTED"
   - "CONSENT_DECLINED" 
   - "CONSENT_WITHDRAWN" (if previously accepted, later declined)

### Unresolved Questions (Future Clarification)

| # | Question | Current Resolution | Required Decision |
|---|----------|-------------------|-------------------|
| Q1 | **Consent for children**: Minors (under 18) consent requirements | Treat as adults for Barangay context (all residents are adults in Philippines) | Document as assumption |
| Q2 | **Consent withdrawal data retention**: What happens to previously collected non-essential data | Per DPA 2012: retain per original consent period, then purge | Document in privacy policy |
| Q3 | **Consent refresh period**: How often must consent be re-requested | Every 2 years, or when terms change, or on major app version update | Set as v1.0.0 with versioning |

---

**Research Status**: Complete - All clarified unknowns have informed resolutions. Ready for Phase 1: Design & Contracts.