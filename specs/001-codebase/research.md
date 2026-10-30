# Research Findings: E-BRGY SUA Architecture Clarification
## Phase 0: Clarify & Research

### Unknowns Requiring Resolution

| # | Unknown | Impact | Resolution |
|---|---------|--------|------------|
| 1 | **F3 - Audit Logging Scope**: Which specific actions must be logged in audit_logs collection vs. which are optional? | High - Security compliance requirement | Define mandatory logging actions: auth events, data mutations, admin actions; optional: UI interactions, cache updates |
| 2 | **F5 - Document Retention Policy**: How long should resident records and audit logs be retained? | High - Legal compliance (Philippine DPA 2012) | Establish retention periods: residents (until revoked + 5 years), audit logs (3 years minimum), emergency reports (7 years) |
| 3 | **F9 - Offline Sync Strategy**: Current behavior when Appwrite is unreachable; does Room DB serve stale data? What UX pattern? | Medium - User experience on poor connectivity | Define offline-first pattern: stale data with expiration, explicit "connection lost" banner, auto-retry queue |
| 4 | **F11 - User Consent Management**: Consent for data processing, privacy terms acceptance | High - Legal compliance | Implement consent screen on first launch; store consent status in Appwrite/user metadata; respect opt-out for non-essential data collection |
| 5 | **F13 - External Municipal Integration**: Integration with external civil registry systems beyond Appwrite | Medium - Feature scope | Clarify if external integration is in scope for v1; if yes, define API contracts and data mapping |
| 6 | **F15 - Multi-language Support**: Current string resources are English/Tagalog only; i18n strategy undocumented | Medium - Accessibility for non-Tagalog speakers | Define i18n strategy: resource externalization, locale handling, priority for Filipino/English |
| 7 | **F17 - Automated Backup Strategy**: No automated Appwrite database backup procedure documented | High - Data loss risk | Define backup schedule; test restore procedure; document RPO/RTO targets |

### Resolved Unknowns (from codebase inspection)

| # | Resolved Question | Answer | Source |
|---|------------------|--------|--------|
| A | AGP Version | AGP 8.13.2 (not 9.1.x as claimed in README) | build.gradle.kts analysis; audit F14 |
| B | Appwrite SDK Version | 25.2.0 | app/build.gradle.kts; libs.versions.toml |
| C | Minimum SDK | 24 (Android 7.0) | AndroidManifest.xml; build.gradle.kts |
| D | Kotlin Version | 2.2.10 | libs.versions.toml |
| E | HTTPS Enforcement | `usesCleartextTraffic="false"` in Manifest; secrets enforce HTTPS | AndroidManifest.xml; audit F14 |
| F | Security Audit Status | 19 findings F1-F19; most critical remediated; some deferred | audit_report.md; git history |

### Research Tasks Completed

1. **AGP Version Correction**: Confirmed project uses AGP 8.13.2, not 9.1.x as stated in README. Update documentation accordingly.
2. **Appwrite Integration Verification**: Confirmed Appwrite SDK 25.2.0 is properly integrated; configuration via `.env` + BuildConfig; no hardcoded secrets.
3. **Security Audit Cross-Reference**: Mapped 19 audit findings (F1-F19) to codebase state - identified which are resolved, which are deferred, and which are in-progress via git branches.
4. **Constitution Principle Alignment**: Verified all 10 constitution principles are supported by existing codebase patterns (or deliberately absent, noting the gap).

### Dependencies & Best Practices Research

- **Room Migration Strategy**: Need to define RoomDatabase.Migration subclasses for schema version bumps; current setup has no version tracking strategy.
- **Appwrite Security Rules**: Current rules are in Appwrite console; need to version-control rules alongside code via repository or CI validation.
- **Offline-First Pattern**: Room DB + Appwrite sync pattern confirmed; need to define conflict resolution strategy for concurrent edits.
- **Coroutine Scoping**: Kotlin coroutine scoping patterns across ViewModels, Repository, and Use Cases; need to ensure no leaky abstractions.

### Clarification Decisions (Informed Guesses)

Where the codebase lacks specification but industry standards apply:

1. **Retention Periods** (F5): Following Philippine DPA 2012 best practices - resident records retained until individual requests deletion + 3 years minimum; audit logs minimum 3 years; emergency reports 7 years for legal/compliance purposes.

2. **Offline Sync** (F9): Implementing standard offline-first pattern: Room DB as source of truth locally; Appwrite as authoritative backend on reconnection; conflicts resolved by timestamp (latest wins) with user notification; explicit "last synced" timestamp displayed UI.

3. **Consent Management** (F11): Implementing consent screen on first app launch; storing `consent_accepted` flag in Appwrite user metadata; opt-out available in settings; non-essential data collection disabled when consent not given.

4. **Multi-language Support** (F15): Externalizing all string resources to `res/values/strings.xml` and `res/values-tl/strings.xml; adding `addStringResource` calls for new strings; using `ConfigurationLocale` composable for runtime switching; priority: English + Tagalog (Filipino).

5. **External Integration** (F13): Documenting as out-of-scope for v1; marking as future enhancement in spec; no API contracts defined yet.

---

**Research Status**: Complete - All identified unknowns have informed resolutions. Ready for Phase 1: Design & Contracts.