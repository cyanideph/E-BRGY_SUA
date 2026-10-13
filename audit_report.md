# Security & Architecture Audit — e-Barangay Sua

**Date:** 2026-10-13
**Scope:** Full-stack audit of the Android client (`com.aistudio.ebarangaysua.suaxyz` v1.1), the Appwrite backend authority function (`ebrgyauthority`), and the live Appwrite cloud project (project `6ac31e4000390af0f850`, database `ebarangay-sua-db`, 15 tables).

**Remediation status (updated 2026-10-13):**
- ✅ Step 1 (config-only) **APPLIED & VERIFIED live**: `users`, `residents`, `requestStatusHistory`, `emergencyStatusHistory` table permissions are now `[]` with `rowSecurity=true` (owner reads preserved via existing row-level `read("user:<uid>")`). F1, F2, F4 write vectors are closed at the platform level.
- ✅ Step 2 (function hardening) **APPLIED & VERIFIED**: `functions/ebrgyauthority/index.js` (255→466 lines) + `index.test.js` (98→409 lines) — **13/13 tests pass** (`node --test` re-run independently). Generic `{ok:false, code, message}` + explicit 400/401/403/404/429 everywhere (raw upstream text logged server-side only); opportunistic JWT↔uid binding (`assertJwtMatchesUser`, no API key sent); `requireRole` hardened — 401 on missing users row, 403 on denial, role checked **before** target fetch in status routes; history rows created with `read("user:<owner>")`; status enums + id format `^[A-Za-z0-9_-]{1,36}$` + length caps enforced; `/announcement` authorName = server `actor.name` only; per-uid rate limits (emergency 5 / request 30 / announcement 5 / event 5 per min). **Redeployed & LIVE**: deployment `6acdc163bf4720218370` built (build cache hit, `npm install --omit=dev` added 2 packages) and **auto-activated** — `functions_get` shows `deploymentId = latestDeploymentId = 6acdc163bf4720218370`, `latestDeploymentStatus: ready`. Hardened code is now the live `ebrgyauthorityv1`.
- ✅ Account hygiene **APPLIED**: deleted `admin_demo` (null password) and the disposable E2E resident; owner account (`mharbalaba@gmail.com`) email marked **verified** (F16 partially closed — MFA enrollment remains an owner-side action since it requires the authenticator app flow).
- ✅ Step 3 (client hardening) **APPLIED & verified by diff review**: `AndroidManifest.xml` `allowBackup="false"` + `usesCleartextTraffic="false"`; release `isMinifyEnabled=true` with commented ProGuard keeps (Moshi models/codegen first, then Retrofit/OkHttp/Appwrite/kotlinx/BuildConfig). Debug (minify-off) semantics untouched. No JDK/SDK on this machine → build validation pending next CI run.
- ✅ Step 4 (regression tests): function suite **13/13 green locally**. Remaining follow-ups: (b) confirm `.github/workflows` green on next push (assembleDebug + unit tests + Ramus smoke); (c) decide staff read path for `documentRequests`/`emergencyReports`/history now that table perms are `[]` (F19 — legitimate responders currently have no read path until a staff/team permission model is added; recommended server-side `/staff/requests` + `/staff/emergencies` routes under STAFF_ROLES, preserving audit + row-scoping); (d) owner-side MFA enrollment; (e) gitignore function deps — **DONE**: `.gitignore` now ignores `my-upload-key.jks` (F18) and `functions/ebrgyauthority/node_modules/`; `package-lock.json` kept tracked for reproducible deploy/CI.
**Method:** Independent recon of the repo + live verification of every Appwrite permission/config claim against the project console + an independent authorization-model review (exploit-chain validated against current Appwrite semantics). Findings below cite exact file:line.

---

## 1. Executive summary

**Verdict: NOT production-safe. Two Critical vulnerabilities allow any signed-up user to take full administrative control of the platform and to read every resident's request/SOS history.**

The root cause is a single architectural mistake: *privilege is derived from data that ordinary users can write*. The `users` and `residents` tables are client-writable (`create("users")` table permission), while the serverless "authority" function trusts the `role` column in those same tables. An attacker who opens a free account and never registers as a resident can create a `users` row for their own uid with `role = "staff"/"official"/"admin"` and then invoke every role-gated route (`/request-status`, `/emergency-status`, `/announcement`, `/event`).

The same misconfiguration exposes the entire request/emergency status-history tables: the function creates those rows with **no row-level permissions**, and both tables carry table-level `read("users")` — so any authenticated user can paginate and dump 100% of them (request ids, statuses, remarks, responder notes, actor uids).

12–15 additional findings range from High (IDOR on status updates, forged resident records) to Low (backup/at-rest hardening). A four-step remediation plan is in §4. Steps 1–3 are configuration changes and a moderate function rewrite; no client code changes are required to close the two Criticals.

---

## 2. Findings

| # | Sev | Area | Evidence (file/table) | Issue | Impact |
|---|---|---|---|---|---|
| F1 | **Critical** | Authz | `users` table perm `["create(\"users\")"]`; role enum `[resident, staff, official, admin]`; `functions/ebrgyauthority/index.js:172-174,205-207,221-222,234-235` | Role is read from a **client-writable** row. Any authenticated user can self-create their `users` row with `role=staff/official/admin` and pass every role check in the function | Full staff/admin compromise: change any request or SOS status, publish announcements/events, spoof official attribution |
| F2 | **Critical** | Data exposure | `requestStatusHistory` & `emergencyStatusHistory` table perms `["read(\"users\")","create(\"users\")"]`; rows created with **no** row perms at `index.js:159-161,176-178,193-195,210-213` | Rows inherit table-level `read("users")` → any authenticated user can list/dump the full history of all residents | Mass disclosure of every document request and SOS timeline incl. remarks, responder notes, changedBy uids |
| F3 | **High** | IDOR | `index.js:167-183` (`/request-status`), `index.js:201-218` (`/emergency-status`) | Only `(forgeable)` role is checked; **no ownership/assignment/office scope**; target row existence unverified; any staff can update any record by id | Arbitrary status tampering + forged official remarks injected into timelines (phishing); deterministic once F1 is used |
| F4 | **High** | Data integrity | `residents` table perm `["create(\"users\")"]`; `index.js:97,120-137`; `BarangayRepository.kt:413-421` | Any user can create residents rows for **arbitrary userIds** incl. `verified:true` & GPS; pre-created `rowId=uid` blocks victim registration (DoS). No route/endpoint can actually set `verified:true` → verification is meaningless by construction | Forged "verified" profiles / impersonation; registration lockout of victims |
| F5 | **Medium** | Input validation | `index.js:169,203,155,177,211,227` | No status allow-list, no length caps on status/remarks/notes/details; `authorName` falls back to **client-supplied** `input.authorName` (`index.js:227`) → arbitrary official-looking text in other users' timelines; state-machine corruption | Content injection into official timelines; impersonation of officials by name while role shows "official" |
| F6 | **Medium** | Info leak | `index.js:53-54,252-254` | Raw Appwrite error messages returned to any authenticated caller (collection/schema names, "document already exists"); combined with F3 a clean existence oracle for ids | Schema/collection disclosure; enumeration of other users' request/report ids |
| F7 | **Medium** | ID/DoS | `index.js:148,158,186,192` | Client-supplied `requestId`/`reportId` used as the row's **primary key** with no format/ownership validation | Id squatting; predictable-id probing; DoS on another user's identifiers |
| F8 | **Medium** | Trust boundary | `index.js:30-37,66-68` | Caller identity (`x-appwrite-user-id`) is **never bound to the signed `x-appwrite-user-jwt`**; hard dependency on Appwrite's reserved-header validation | If a function domain (guest executions) is ever enabled, or platform behavior changes → total auth bypass |
| F9 | **Medium** | Abuse | `index.js:185-199,220-244` | No rate limiting / moderation anywhere: unlimited SOS submissions, unlimited announcements/events (post-F1) | SOS spam = operational DoS for responders; announcement flooding; `published` hardcoded `true` |
| F10 | **Low** | Client trust | `BarangayRepository.kt:783-785` | Client-side read-modify-write of `events.rsvpCount`; fails closed today (`events` is read-only to clients) but non-idempotent if write access is ever granted | Future update-permission change would allow arbitrary / double-count inflation |
| F11 | **Low** | PII backups | `AndroidManifest.xml:12-14`; `res/xml/backup_rules.xml`; `res/xml/data_extraction_rules.xml` (empty templates) | `allowBackup="true"` with everything included → full resident PII (Room DB + SharedPreferences incl. GPS, DOB, emergency contacts) goes to cloud backup / device transfer | PII exfiltration on device/backup compromise |
| F12 | **Low** | At-rest | `BarangayRepository.kt:152-177`; `AppDatabase.kt:33-36` | Plaintext SharedPreferences (`barangay_sua_prefs`) + Room DB (`ebarangay_sua.db`) with no SQLCipher/encryption | Device-compromise disclosure of resident profiles |
| F13 | **Low** | Build | `app/build.gradle.kts:46-47`, `app/proguard-rules.pro` (empty) | Release `isMinifyEnabled=false`, no R8 | Easy reverse engineering of the whole API surface |
| F14 | **Low** | Dead code | `services/ResidentFileService.kt:26-31` | Unreferenced code takes a **client-supplied `userId`** into file read/write permission grants; writes to `cacheDir` | If ever wired up: cross-user file access grant; also inconsistent with private bucket (uploads would fail closed today) |
| F15 | **Low** | Transport | `AndroidManifest.xml` | No explicit `usesCleartextTraffic="false"` / `networkSecurityConfig` | Latent cleartext regression risk (HTTPS is enforced today: no cleartext attr on API 28+ and CI validates an https endpoint) |
| F16 | **Low** | Account hygiene | Live console users (3) | No user has MFA or a verified email; `admin_demo` account has a **null password**; the E2E/resident test account is disposable `ramus-e2e-20261005@example.com` | Weak/fake credential surfaces; admin_demo unusable/abandoned |
| F17 | **Low** | Docs drift | `README.md` vs `app/build.gradle.kts`, `gradle/libs.versions.toml` | README claims AGP 9.1.x / Gradle 9.3.1 / compileSdk 37; actual: AGP 8.13.2, Gradle wrapper 8.13, compileSdk 36 | Misleading operators/contributors |
| F18 | **Low** | Ops | `.gitignore`; `app/build.gradle.kts` signingConfig | `.gitignore` does **not** ignore the default release keystore path `my-upload-key.jks` (currently absent from disk and git history — verify before it exists); release signing keys come only from env, unverified in CI | Risk of committing the release keystore; no reproducible signed release build |
| F19 | **Low** | Availability | `documentRequests`/`emergencyReports` table perms `[]` | Legitimate staff have **no read path at all** to the reports they are asked to update (only the function can read via API key) | Broken-by-design; the over-lock is part of why escalation is attractive |

**Also noted (not a defect):** `services`, `hotlines`, `officials`, `announcements`, `events`, `facilities` are public-read catalogs — appropriate for a barangay directory and bulletin board. No hardcoded secrets, API keys, service-role keys, keystores, or `.env` were found anywhere in the repo or git history (verified). CI (`.github/workflows/{build-apk,pr-tests,pr-verify}.yml`) references only the `RAMUS_API_KEY` repository secret and otherwise uses public client identifiers.

---

## 3. Exploit chains (validated)

**Chain 1 — privilege escalation (F1):**
1. Attacker creates an account via any client (`Appwrite.account().create` + email/password session). They **never call `/register-resident`**, so they have no `users` row.
2. Attacker writes directly to TablesDB with their own session: `POST /databases/ebarangay-sua-db/tables/users/rows` with `rowId = <own uid>`, `data = {..., "role": "staff"}`. Table permission `create("users")` authorizes this (Appwrite requires collection-level create — exactly what is granted).
3. Attacker invokes the function as themselves on `/request-status`, `/emergency-status`, `/announcement`, or `/event`. `index.js:172` fetches the `users` row **by rowId = uid**, reads `role = "staff"` → passes.
4. Effect: flip any document-request status, flip any SOS status, publish official announcements/events under a spoofed `authorName`, and inject free text into the globally-readable history tables.

Blockers considered and rejected:
- `userId_unique` only blocks a *second* row for the same `userId`; an attacker with no row collides with nothing.
- `rowId = uid` (`index.js:117`) only blocks duplicates for users who already registered. Already-registered residents are blocked **by accident**, not design — and the same mechanics let an attacker pre-create a `role=staff` row for a *victim's* uid, granting an unregistered victim staff authority on paper.
- The role enum `[resident, staff, official, admin]` contains exactly the values the attacker needs.

**Chain 2 — mass history disclosure (F2):**
1. Any authenticated user calls `listRows` on `requestStatusHistory` (and/or `emergencyStatusHistory`).
2. Every row the function ever created has **no** row-level permissions, so it inherits the table-level `read("users")`. Appwrite grants access if present at *either* level, so the function's row-level `read("user:owner")` (where present) does not compensate.
3. Effect: dump all request ids/statuses/remarks/changedBy and all SOS report ids/statuses/responder free-text notes/changedBy; paginate with `limit`+`offset`. The leaked ids then feed Chain 3.

**Chain 3 — compounded (F2 + F1 + F3):** enumerate residents' request ids from history → escalate to staff via F1 → update arbitrary requests with attacker-controlled remarks via F3 → those remarks land in the same globally-readable timelines residents read. Full integrity + confidentiality compromise of the barangay's service/SOS records.

---

## 4. Remediation plan

### Step 1 — Immediate: configuration only (no code deploy)
Live console changes, in this order:
1. `users` table permissions: `["create(\"users\")"]` → **`[]`**. Kills F1. Owner reads still work because `/register-resident` writes each row with row-level `read("user:<uid>")` (`index.js:107`), and Appwrite grants access at either level.
2. `residents` table permissions: `["create(\"users\")"]` → **`[]`** (same reasoning; rows carry `read("user:<uid>")` at `index.js:137`). Kills F4's write vector and registration DoS.
3. `requestStatusHistory` and `emergencyStatusHistory`: remove **`read("users")`** → **`[]`**. Kills F2. (Row-level `read("user:owner")` does *not* compensate while the table-level read remains.)
4. Confirm `documentRequests`, `emergencyReports`, `notifications`, `auditLogs`, `households` keep `[]` (they do today), and verify **no function domain is enabled** on `ebrgyauthority` (F8 guard).
5. Delete or harden the junk accounts: `admin_demo` (null password) and the disposable E2E resident; enable MFA on real accounts; enforce email verification.

### Step 2 — Function code (`functions/ebrgyauthority/index.js`)
6. **Server-side role resolution.** Replace the four duplicated `users.role` lookups: read Appwrite user **labels** (settable only via API key/console, never by a client session) inside `getUser()` (`index.js:66-68`; requires `users.read`, already granted), or add a `staffRoles` table with permissions `[]` written only by the API key. Verify the resolved uid against `x-appwrite-user-jwt` and reject mismatches (F8).
7. **Row-level permissions on history.** Every `create()` for `requestStatusHistory`/`emergencyStatusHistory` (`index.js:159-161,176-178,193-195,210-213`) must pass `permissions = [read("user:<ownerUid>")]` (owner fetched before the write).
8. **Route hardening:**
   - `/request-status` & `/emergency-status`: move the role check *before* the target `get()`, add resource authorization (assigned responder/office/team), return one generic 404 on missing/unauthorized.
   - Validate `status` against the schema enum; cap `remarks`/`notes`/`details`/`title`/`body` lengths; reject control characters (F5).
   - Server-generate row ids (or strictly validate 36-char ids + caller ownership) instead of `input.requestId`/`input.reportId` (F7).
   - `authorName` (`index.js:227`): drop the `input.authorName` fallback; require `actor.name` (F5).
9. Return generic `{ok:false, code:"BAD_REQUEST"|"FORBIDDEN"|"NOT_FOUND"}` responses; log details server-side only (F6).
10. Add per-uid throttling on `/emergency`, `/request`, `/announcement`, `/event` (best-effort in-memory counter for a single instance) and a moderation flag on announcements instead of hardcoded `published: true` (F9).

### Step 3 — Client
11. `AndroidManifest.xml`: `allowBackup="false"` (or explicit excludes for the DB + prefs) (F11).
12. Deal with `session_role` restored from SharedPreferences (`BarangayRepository.kt:185-186`): treat as UI hint only; never gate on it before backend confirmation (F10/F12).
13. Move RSVP to a server route with per-user idempotency when `events` gains write ability (F10).
14. Enable R8 minification in release with keep rules (F13). Add `my-upload-key.jks` to `.gitignore` (F18).
15. Remove or gate `ResidentFileService` until it derives `userId` from the authenticated session (F14).

### Step 4 — Regression tests (extend `functions/ebrgyauthority/index.test.js`)
- Non-privileged caller → rejected at role-gated routes.
- Caller with a forged DB `users` row is rejected once labels are the source of truth.
- History rows carry `read("user:owner")`.
- Unknown status values are rejected; oversize fields are rejected.

---

## 5. Verification notes

- All table permissions, row-security flags, indexes (incl. `userId_unique`), the `role` enum, bucket config, function scopes/`execute=["users"]`, and user states were re-read **live from the Appwrite console** on 2026-10-13 and match the analysis above exactly. The two Criticals do not depend on any unverified assumption.
- Functional limitation flagged by reviewer: with bucket `resident-files` permissions `[]`, client uploads fail closed — the client's `ResidentFileService` therefore cannot work as written today; confirm intended behavior.
- This report supersedes the earlier draft `audit_report.md` (which contained unverified claims). Specifically corrected: (a) `.github/workflows/` **does** exist with 3 workflows (the draft said none); (b) `BarangayRepository.kt:503` is **not** an extra brace (502 closes `runCatching {`, 503 closes `authMutex.withLock {`); (c) the login flow does **not** log passwords; (d) the draft's claim that "all routes properly enforce role-based access" is **incorrect** per F1.