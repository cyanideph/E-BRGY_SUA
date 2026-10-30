# E-BRGY SUA Constitution
<!-- E-Barangay Management System - Barangay Sua, San Juan, Southern Leyte -->

## Core Principles

### I. Kotlin-First Android Development
Kotlin is the primary language for all Android development. All new code must be written in Kotlin; existing Java code should be migrated incrementally. Leverage Kotlin coroutines and flow for asynchronous operations, and prefer Kotlin idioms over Java patterns. Use Kotlin 2.2.x language features safely within AGP 8.13.2 constraints.

### II. Maintainable and Secure Architecture
The application must follow a clean architecture pattern with clear separation between UI, domain, and data layers. All architectural decisions must prioritize long-term maintainability, testability, and security. The repository pattern (BarangayRepository) must encapsulate all data sources (Room DB + Appwrite). No business logic may reside in UI components or Compose functions.

### III. Appwrite as the Backend
Appwrite is the exclusive backend platform for authentication, database, storage, and realtime functionality. All backend integrations must use the official Appwrite SDK 25.2.0. Follow Appwrite best practices for security rules, database permissions, and token management. Never hardcode Appwrite project IDs, endpoint URLs, or collection names in source code — access them via BuildConfig or environment variables.

### IV. Environment-Based Secrets and Configuration
All sensitive configuration (Appwrite endpoint, project ID, database ID, bucket IDs, API keys) must be provided exclusively through the Gradle Secrets Plugin via `.env` files. The `.env.example` template must accompany any new `.env` additions. The `.env` file itself must be git-ignored and never committed. Secrets must never be embedded in BuildConfig, BuildConfig fields, or hardcoded in source.

### V. Real Backend Data Instead of Fabricated Data
All UI screens must display real data from the Appwrite backend, not hardcoded or simulated data fixtures. Room DB may cache offline data, but primary data sources must always be Appwrite. Test data used in unit tests must be clearly marked and never committed as production data. Robolectric tests must use real Appwrite SDK calls when testing backend-integrated features.

### VI. Data Integrity and Authentication Security
All data mutations must go through authenticated Appwrite channels. Validate all incoming data at the repository layer before Room DB or Appwrite writes. Enforce proper Appwrite security rules on all database collections and storage buckets. Implement session management that respects Android security guidelines (auto-logout on background state transition, secure token storage). No plaintext data transmission — all Appwrite traffic must be HTTPS (already enforced by `usesCleartextTraffic="false"`).

### VII. Accessibility and Mobile Usability
All Jetpack Compose UI components must meet WCAG 2.1 Level AA contrast requirements. Touch targets must be minimum 48dp × 48dp. Provide contentDescription for all interactive elements. Support screen readers and content scaling. The application must function correctly on devices with minimum SDK 24 (Android 7.0 Nougat) and handle configuration changes (screen rotation) gracefully without data loss or crashes.

### VIII. Testing and Verification Before Merging
All changes must pass the full test suite Robolectric unit tests (100% passing) before merge. New features must include appropriate unit tests. UI changes must not introduce visual regressions. Security-critical changes must pass the audit review checklist (refer to audit_report.md). The CI/CD pipeline (GitHub Actions) must build and test successfully on every PR. No WIP merges that break existing tests.

### IX. Compatibility with Android Devices and Resource-Constrained Environments
The application must optimize for low-resource Android devices common in the Philippines (entry-level smartphones, variable network connectivity). Room DB must support efficient offline caching with proper schema versioning. Appwrite operations must handle network timeouts gracefully with user-friendly fallback messages. Asset resources (images, sounds) must be optimized for mobile bandwidth. The minimum SDK 24 ensures compatibility with devices running Android 7.0 and above.

### X. Documentation and Traceable Requirements
All features, changes, and architectural decisions must be documented in the Specify/Speckit feature specification system. Each feature must have a corresponding spec.md in the specs/ directory with clear user scenarios, functional requirements, success criteria, and acceptance tests. The audit_report.md findings must be tracked through resolution, with each finding (F1-F19) documented as resolved or deferred with justification. No undocumented code changes are permitted.

## Governance

Constitution supersedes all other practices. Amendments require documented approval, version increment, and compliance review.

**Version**: 1.0.0 | **Ratified**: 2026-10-30 | **Last Amended**: 2026-10-30

**Amendment Procedure**:
1. Proposer submits a `/speckit.specify` description proposing constitutional change
2. Constitutional Committee (lead architect) reviews and validates
3. Supermajority approval (2/3 of core maintainers) required for acceptance
4. Version increments according to semantic versioning: MAJOR for backward-incompatible principle removals, MINOR for new principles, PATCH for clarifications
5. Updated constitution must be committed with a `docs: amend constitution` message

**Compliance Review**:
- Each PR must include a constitution compliance checklist
- Violations must be justified with documented rationale
- Unexplained violations block merge

**Last Updated**: 2026-10-30 by lead architect