# KNOUX AI Pro — Core Engineering Integration Manifest

Isolated worktree: `K:\KNOUX-AI-Pro-CORE-ISOLATED` (detached HEAD, intentional).
Original repository `K:\KNOUX-AI-Pro` is owned by the Visual Agent and is never modified.

SOURCE_BASE_SHA: `230d30db9dbd83ddb4f857bea05ad0697d2fd660`

## Live external verification (2026-11-23, read-only, publishable key only)

- Supabase project `tduofkizwkjtqcbklfah`:
  - `GET /auth/v1/health` → HTTP 200, GoTrue v2.198.0 (Auth service alive).
  - `GET /rest/v1/ka_profiles` → HTTP 404 `PGRST205` (table not in schema cache).
  - `GET /rest/v1/scan_sessions` → HTTP 404 `PGRST205`.
  - Conclusion: public schema still has no application tables. Supabase Auth is the
    real session authority; cloud data tables remain CONFIGURATION_REQUIRED.
- No service-role key used or stored anywhere. No writes to the remote project.

## Capability classification (evidence-based, refreshed at base SHA)

| Capability | Status | Evidence |
|---|---|---|
| Room journal (9 entities, v2, exported schemas, MIGRATION_1_2) | IMPLEMENTED_AND_TESTED (SQL-contract JVM test; device migration test exists in CI history) | `database/AppDatabase.kt`, `AppDatabaseMigrationJvmTest` |
| Repository write journaling | PARTIALLY_CONNECTED → repaired in Slice 1 (non-atomic multi-write paths) | see Slice 1 |
| Storage analyzer scan→consent→act→verify flow | IMPLEMENTED_NOT_TESTED (device-dependent; UI-owned ViewModel) | `StorageAnalyzerViewModel.kt` |
| Duplicate scan engine / action safety | IMPLEMENTED_AND_TESTED (JVM) | `DuplicateScanEngineTest`, `DuplicateActionSafetyTest` |
| Device Reality facts / privacy / AI aggregates | IMPLEMENTED_AND_TESTED (JVM aggregates) | `DeviceRealityTest` etc. |
| Supabase email/OTP/password auth client | IMPLEMENTED_NOT_TESTED → contract-tested in Slice 2 | `KnouxAuthApi.kt` |
| Session store (Keystore AES/GCM) | IMPLEMENTED_NOT_TESTED (device Keystore; JVM cannot execute) | `KnouxSessionStore.kt` |
| Google Sign-In (Credential Manager) | CONFIGURATION_REQUIRED (web client ID present; interactive consent untested on device) | `KnouxGoogleCredential.kt` |
| Passkeys (Supabase experimental routes) | CONFIGURATION_REQUIRED (server feature must be enabled; client implemented) | `KnouxPasskeyCredential.kt` |
| Biometric local unlock | IMPLEMENTED_NOT_TESTED (correctly separate from server auth) | `KnouxBiometricUnlock.kt` |
| OAuth redirect relay | IMPLEMENTED_NOT_TESTED → JVM-tested in Slice 2 | `OAuthRedirectRelay.kt` |
| Google/Microsoft connectors | IMPLEMENTED_AND_TESTED (JVM contract tests) in Slice 3: structural parsing, pagination, explicit failure states, consent-safe mutations | `google/`, `microsoft/`, `CloudConnectorContractTest` (12 tests) |
| Supabase app tables + RLS | IMPLEMENTED_AND_TESTED (source-contract JVM tests, 9 tests) in Slice 4; live DEPLOYMENT = CONFIGURATION_REQUIRED (no privileged access; files now exist at the paths README references) | `database/supabase/001_core.sql`, `002_rls.sql`, `003_account_erasure.sql`, `tests/001_rls_structure.sql`, `functions/report-summary/` |
| Developer Studio providers + vault | IMPLEMENTED_AND_TESTED (JVM contract tests) in Slice 5: vault Keystore AES-GCM verified by source contract; adapters hardened | `developer/`, `DeveloperStudioContractTest` (11 tests) |
| secureRequest HTTP-status retry | PARTIALLY_CONNECTED → repaired in Slice 2 (429/5xx never retried) | `security/ProductionSecurity.kt` |

## Slices

### Slice 1 — Atomic Room journaling (P2)

DOMAIN: persistence / repository integrity
BASE SHA: `230d30db9dbd83ddb4f857bea05ad0697d2fd660`
COMMIT SHA: `8d3128726c2561ba5e68265c546acc4943e2e54f`
FILES_CHANGED:
- `app/src/main/java/com/knoux/ai/database/AppDatabase.kt`
- `app/src/main/java/com/knoux/ai/ui/v2/StorageAnalyzerViewModel.kt` (data-call swap only)
- `app/src/main/java/com/knoux/ai/ui/v2/SectionServiceViewModel.kt` (data-call swap only)
- `app/src/main/java/com/knoux/ai/ui/v2/DuplicateWorkspaceViewModel.kt` (data-call swap only)
- `app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt` (data-call swap only)
- `app/src/main/java/com/knoux/ai/ui/v2/MoreScreensV2.kt` (data-call swap only)
- `app/src/test/java/com/knoux/ai/JournalAtomicityJvmTest.kt` (new, 10 tests)

IMPLEMENTATION:
- `ScanSessionDao.completeWithItems` @Transaction: observed items + session summary commit together; chunked at 500 rows (SQLite bind-variable limit).
- `OperationItemOutcomeDao.insertAllWithStatus` @Transaction: outcome evidence + final operation status commit together; aborts before any write if the operation parent is missing.
- `KnouxRepository.completeScanSession(..., items)` overload and `recordItemOutcomes(..., finalStatus)` route through the transactions. Old signatures preserved.
- All five real operation pipelines now write status+evidence in one call. No separate `updateOperationStatus` after `recordItemOutcomes` remains in the pipelines.
- No schema change: Room version stays 2, exported schemas untouched, no migration needed.

TESTS_EXECUTED:
- `.\gradlew.bat :app:testDebugUnitTest --tests com.knoux.ai.JournalAtomicityJvmTest --tests com.knoux.ai.OperationRequestParentJvmTest --tests com.knoux.ai.AppDatabaseMigrationJvmTest` → EXIT 0 (16 tests, 0 failures)
- Full suite + `:app:assembleDebug` → EXIT 0 (see combined run in Slice 2)

UI_HANDOFF_REQUIRED: none — repository call signatures are backward compatible; Visual Agent layouts untouched. The five UI-file edits are strictly inside existing IO blocks (journal calls), zero layout/visual change. Integration gate should re-apply these small call-site swaps if the Visual Agent rewrote those blocks.
MIGRATIONS: none (schema v2 unchanged).
REAL DATA SOURCE: unchanged — MediaStore/StorageManager measurements; this slice only makes their journaling atomic.
EXTERNAL VERIFICATION: none required.
UNRESOLVED BLOCKERS: device-level SQLite transaction behavior verified only via JVM fakes + Room's generated wrapper; instrumented run deferred (Visual Agent owns the phone).

### Slice 2 — Auth session correctness + HTTP retry policy (P1)

DOMAIN: authentication / network safety
BASE SHA: `8d3128726c2561ba5e68265c546acc4943e2e54f` (Slice 1 commit)
COMMIT SHA: `658c32007892e5a0aaf24e5838b0ed2f9028318f`
FILES_CHANGED:
- `app/src/main/java/com/knoux/ai/auth/SessionResponseParser.kt` (new, pure JVM policy)
- `app/src/main/java/com/knoux/ai/auth/KnouxAuthApi.kt`
- `app/src/main/java/com/knoux/ai/security/ProductionSecurity.kt`
- `app/build.gradle.kts` (test-only dependency `org.json:json:20240303`)
- `app/src/test/java/com/knoux/ai/auth/AuthSessionContractTest.kt` (new, 14 tests)

IMPLEMENTATION:
- Session acceptance extracted to `SessionResponseParser` (tokens required, expires_in clamped 1..86400, 60s refresh skew) — now executed by JVM tests against real org.json.
- `verifiedToken()` serialized behind `refreshMutex`: concurrent callers refresh once; rotating refresh tokens can't be spent twice.
- Permanent refresh rejection (4xx `KnouxAuthException`) clears the dead local session → UI returns to sign-in instead of looping.
- New `HttpStatusException(status)` in security layer; `secureRequest` retries 408/425/429/5xx only for idempotent calls. Previously **no HTTP status was ever retried** despite the documented contract.
- `KnouxAuthApi.request`: GET and refresh are idempotent (retryable); sign-in/OTP/signup POSTs execute exactly once (single-use codes must not be replayed).
- Email validation moved from `android.util.Patterns` (untestable on JVM) to a conservative JVM-executable regex in `ProductionSecurity.isValidEmail`.

TESTS_EXECUTED:
- `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` → **EXIT 0**, 168 tests / 16 suites / 0 failures (includes all 10 Slice-1 tests, 14 new auth tests, existing security/migration/engine suites). Debug APK assembled.

PUBLIC_INTERFACE_CHANGES: additive only (`HttpStatusException`, `SessionResponseParser` internal, request() private param). No UI contract change.
MIGRATIONS: none.
REAL DATA SOURCE: Supabase GoTrue (live health verified 200); no fabricated sessions anywhere.
LOCAL VERIFICATION: full JVM suite + assembleDebug, exit 0.
EXTERNAL VERIFICATION: GoTrue `/auth/v1/health` 200 (read-only). Actual sign-in against live project NOT executed (would create a real account; owner decision).
UNRESOLVED BLOCKERS: Google interactive consent (CONFIGURATION_REQUIRED), passkey server feature enablement (CONFIGURATION_REQUIRED), device Keystore/biometric paths (IMPLEMENTED_NOT_TESTED — instrumented run deferred while Visual Agent owns the phone).
GRADLE HANDOFF: `app/build.gradle.kts` adds one testImplementation line (`org.json:json:20240303`). Trivial merge; required for JVM JSON contract tests.

### Slice 3 — Google/Microsoft connector data integrity (P4)

DOMAIN: cloud connectors (Drive, Gmail, Outlook) + OAuth token parsing
BASE SHA: `658c32007892e5a0aaf24e5838b0ed2f9028318f`
COMMIT SHA: `b0502c09bd71b9c719c5589dc481528391d7b28d`
FILES_CHANGED:
- `app/src/main/java/com/knoux/ai/cloud/CloudHttp.kt` (new: shared secure GET/mutate/paginate + structural parsers)
- `app/src/main/java/com/knoux/ai/google/auth/GoogleTokenParser.kt` (new, pure)
- `app/src/main/java/com/knoux/ai/microsoft/auth/MicrosoftTokenParser.kt` (new, pure)
- `app/src/main/java/com/knoux/ai/google/auth/GoogleAuthProvider.kt`
- `app/src/main/java/com/knoux/ai/microsoft/auth/MicrosoftAuthProvider.kt`
- `app/src/main/java/com/knoux/ai/google/drive/GoogleDriveConnector.kt`
- `app/src/main/java/com/knoux/ai/google/gmail/GmailConnector.kt`
- `app/src/main/java/com/knoux/ai/microsoft/outlook/OutlookConnector.kt`
- `app/src/test/java/com/knoux/ai/cloud/CloudConnectorContractTest.kt` (new, 12 tests)

IMPLEMENTATION (real defects repaired):
- Regex field-zip parsing replaced by structural org.json parsing — the old
  code mis-paired id/size/md5Checksum whenever a record omitted an optional
  field (e.g. Google Docs editors without size), silently attributing another
  file's checksum to a file. Duplicates are now claimed only on verified MD5.
- Silent `catch → emptyList()` on list calls replaced by `CloudApiException`:
  a failed provider call now fails the scan explicitly (ScanStatus.Failed +
  redacted provider message) instead of reporting an empty "Completed" scan.
- Pagination: Drive/Gmail listings follow pageToken (bounded 10 pages);
  Gmail now fetches real sizeEstimate/Subject via messages.get metadata
  (bounded 50) instead of reporting sizeEstimate=0 for every message.
- Mutating calls (trash/untrash/move) execute exactly once and count success
  only from the provider's confirmed response body (trashed flag / TRASH
  label / parentFolderId), not from a bare 2xx.
- Candidate dedup by providerItemId: one real item matched by two queries is
  counted once in itemsFound/bytesFound.
- Outlook "old emails" cutoff computed from scan time (was hardcoded
  2025-01-01); junk query resolves the real junkemail folder id; token
  exchange bodies no longer echo raw provider responses (PII leak); scopes
  deduped; token bodies URL-encoded.
- OAuth token responses parse via pure JVM-tested parsers; missing
  access_token is rejected; AAD string-or-number expires_in handled.

TESTS_EXECUTED: `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` → **EXIT 0**
(full suite: **180 tests / 17 suites / 0 failures**, includes the 12 new
CloudConnectorContractTest tests; `app-debug.apk` assembled. An earlier
background run failed only because build outputs were deleted mid-run —
re-run from a clean state passed.)
PUBLIC_INTERFACE_CHANGES: additive only; connector public method signatures
unchanged (`scanWithCandidates`, `trashFiles`, `isTrashed`, etc.).
MIGRATIONS: none.
REAL DATA SOURCE: provider APIs at call time; fixtures in tests are synthetic.
EXTERNAL VERIFICATION: none performed against live Google/Microsoft accounts
(interactive consent + registered redirect URIs are owner-controlled) —
labeled BLOCKED, not claimed.
UI_HANDOFF_REQUIRED: none (backend-only). ScanResult.error semantics improved:
UI already displays `result.error`.
UNRESOLVED BLOCKERS: Google Android client ID not configured at build time
(`knoux.googleAndroidClientId` unset → `knoux.oauth.unconfigured` scheme),
Microsoft client ID unset, consent screens unverified on device.

### Slice 4 — Supabase schema prep: core tables + RLS + erasure + tests (P5)

DOMAIN: `database/supabase/` (files referenced by README_DEPLOYMENT.md that did not exist)
BASE SHA: `b26710a97ebf312fd54086da5b98b6db5bf5cee8`
COMMIT SHA: `76ffe182a1dc672279f7b68caaf64980c6374f23`
FILES_CHANGED:
- `database/supabase/001_core.sql` (new): 7 user-owned tables mirroring Room
  (ka_profiles, ka_devices, ka_scan_sessions, ka_operation_requests,
  ka_operation_item_outcomes, ka_storage_snapshots, ka_battery_samples);
  all FK to auth.users(id) ON DELETE CASCADE; consent CHECK constraint;
  idempotent (create-if-not-exists), updated_at trigger.
- `database/supabase/002_rls.sql` (new): RLS enabled on all tables; owner-only
  policies `auth.uid() = user_id` for select/insert/update/delete; ka_operation_requests
  INSERT exists ONLY as consent policy (consent_given AND consent_at);
  item-outcomes owned via parent operation; no anon grants.
- `database/supabase/003_account_erasure.sql` (new): SECURITY INVOKER
  `ka_erase_my_account()` returning per-table audit counts; deny-by-default
  `ka_erasure_audit` table (RLS on, zero policies); executable only by
  `authenticated`; auth.users deletion documented as server-side service-role step.
- `database/supabase/tests/001_rls_structure.sql` (new): Part A structural checks
  (pg_tables.relrowsecurity, pg_policies, FK cascades, consent policy, audit
  deny-by-default, no anon policies) + Part B two-real-user behavioral script
  (B1–B8: cross-owner reads/writes denied, cross-device references rejected,
  no-consent insert rejected, erasure receipt, expired token 401).
- `database/supabase/functions/report-summary/index.ts` (new, optional Edge Fn):
  JWT verified via auth.getUser(jwt) → 401 otherwise; all reads through a
  client bound to the USER's JWT (RLS applies); aggregates only, never raw files.
- `app/src/test/java/com/knoux/ai/cloud/SupabaseSchemaContractTest.kt` (new, 9 tests):
  pins every security property above at source level + asserts zero secrets
  (no JWT-shaped strings, no sb_* keys, no hardcoded credential assignments).

TESTS_EXECUTED: `.\gradlew.bat :app:testDebugUnitTest --tests com.knoux.ai.cloud.SupabaseSchemaContractTest`
→ EXIT 0, 9 tests / 0 failures (one whitespace-tolerant fix after first run).
Full suite `.\gradlew.bat :app:testDebugUnitTest` → **EXIT 0: 189 tests / 18 suites / 0 failures / 0 errors**.
PUBLIC_INTERFACE_CHANGES: none (new files only; README paths now satisfied).
MIGRATIONS: these ARE the migrations (001→002→003 order), apply owner-side on
staging first per README_DEPLOYMENT.md.
REAL DATA SOURCE: none — no database was connected (no local Postgres/Docker in
this environment, verified); remote project untouched (read-only probes only).
EXTERNAL VERIFICATION: none — SQL never executed against any database.
Classification: files IMPLEMENTED_AND_TESTED (JVM source-contract tests);
live deployment + two-real-user RLS run = CONFIGURATION_REQUIRED / BLOCKED
(no privileged credentials; must run owner-side on staging).
UI_HANDOFF_REQUIRED: none.
UNRESOLVED BLOCKERS: apply migrations to `tduofkizwkjtqcbklfah` requires owner
SQL editor / CLI access; supabase-kt client wiring in the app is a follow-up.

### Slice 5 — Developer Studio security hardening (P6)

DOMAIN: `developer/provider/*` adapters + `developer/vault/CredentialVault.kt` + `cloud/CloudHttp.kt`
BASE SHA: `83681554be5cb160f5206ab8c19f676a7d9b1917`
COMMIT SHA: `372a3e3ca1aef08ddc67ad92a57e254b75655c13`
FILES_CHANGED:
- `app/src/main/java/com/knoux/ai/cloud/CloudHttp.kt`: `CloudHttp` made public;
  `getJson` gained `headerName` param (Bearer for Authorization, raw value for
  e.g. x-goog-api-key) and skips the auth header entirely for empty credentials
  (local providers). `paginate` forwards the header param.
- All 5 adapters (OpenRouter, OpenAI, Nvidia, Gemini, Ollama): raw
  HttpURLConnection blocks replaced with `CloudHttp.getJson` (https-only base
  URLs, `instanceFollowRedirects = false`, timeouts, secureRequest retry on
  transient statuses). Removed `throw RuntimeException("HTTP $status: $payload")`
  which echoed raw provider bodies (potential key/PII echo) into UI-visible
  error messages — now `CloudApiException` carries a redacted, status-only
  message. `normalizeError` now maps `CloudApiException.status`
  (401/403→InvalidApiKey, 429→RateLimited, 5xx→ProviderUnavailable).
- `GeminiAdapter`: API key moved OUT of the URL query (`?key=` leaks into
  logs/proxies) into the `x-goog-api-key` header.
- `OllamaAdapter` + new pure `OllamaEndpointPolicy`: `setBaseUrl` validates —
  cleartext HTTP allowed ONLY for loopback hosts (localhost/127.x/10.0.2.2/::1),
  remote hosts must be https; malformed URLs rejected.
- `CredentialVault.kt`: masking extracted to pure `CredentialMask` (short keys
  fully hidden; first4****last4 otherwise) — JVM-testable. Vault itself was
  audited clean: Keystore AES-256-GCM with randomized IV, unreadable-entry
  cleanup on decrypt failure, wipeAll recovery, safeLogKey = REDACTED.
- Gmail/Drive `isTrashed`: raw connections replaced with `CloudHttp.getJson`;
  404→true (gone), other provider failures→null (never success).
- `app/src/test/java/com/knoux/ai/cloud/DeveloperStudioContractTest.kt` (new, 11 tests).

TESTS_EXECUTED: `.\gradlew.bat :app:testDebugUnitTest --tests com.knoux.ai.cloud.DeveloperStudioContractTest`
→ EXIT 0, 11 tests / 0 failures (also proves all refactored files compile).
Full `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` → **EXIT 0:
200 tests / 19 suites / 0 failures / 0 errors; app-debug.apk assembled**.
PUBLIC_INTERFACE_CHANGES: additive only (`CloudHttp` visibility, `getJson` default
param, `OllamaEndpointPolicy`, `CredentialMask`); adapter interface unchanged;
`OllamaAdapter.setBaseUrl` now throws on invalid/remote-cleartext URLs (intentional).
MIGRATIONS: none. REAL DATA SOURCE: unchanged (provider APIs at call time).
EXTERNAL VERIFICATION: no live provider calls made from tests (fixtures/synthetic).
UI_HANDOFF_REQUIRED: none.
UNRESOLVED BLOCKERS: device Keystore path still IMPLEMENTED_NOT_TESTED on JVM
(instrumented run deferred — Visual Agent owns the phone); live provider
connectivity CONFIGURATION_REQUIRED (owner supplies keys via vault).

### Slice 6 — Integration contracts (P7)

DOMAIN: cross-layer contracts (build config ↔ manifest ↔ Room ↔ UI call sites ↔ secrets)
BASE SHA: `3f2284a2078b22bf9f8ea496b679a82b934bc6a2`
COMMIT SHA: (recorded after commit)
FILES_CHANGED:
- `app/src/test/java/com/knoux/ai/IntegrationContractJvmTest.kt` (new, 5 tests):
  1. No secrets in any app source/gradle file (sb_secret_*, service-role
     assignments, JWT-shaped strings, hardcoded key literals) — scans
     app/src/main + build.gradle.kts + gradle.properties.
  2. BuildConfig Supabase host == manifest project ref `tduofkizwkjtqcbklfah`;
     publishable key only, never a service-role reference.
  3. Room schema stays v2 with exported `schemas/.../2.json`.
  4. Repository keeps backward-compatible atomic overloads
     (`completeScanSession(..., items=...)`, `recordItemOutcomes(..., finalStatus=...)`).
  5. The five UI journal call-site swaps still in place (4× `finalStatus =`,
     SectionServiceViewModel × atomic `items =` overload).

TESTS_EXECUTED: `.\gradlew.bat :app:testDebugUnitTest --tests com.knoux.ai.IntegrationContractJvmTest`
→ EXIT 0, 5 tests / 0 failures (one assertion corrected to the per-file atomic
marker after first run; re-run clean EXIT 0). Full suite + assembleDebug recorded after run.
PUBLIC_INTERFACE_CHANGES: none (test-only slice).
MIGRATIONS: none. REAL DATA SOURCE: none (static contracts).
EXTERNAL VERIFICATION: none needed. UI_HANDOFF_REQUIRED: none.
UNRESOLVED BLOCKERS: none new.

## Ordered safe integration procedure (for the owner / integration gate)

Master has NOT moved since the base (`230d30d…` verified in the original repo
on 2026-11-23). `git merge-tree` confirms my commits merge CLEANLY onto it.

1. In the ORIGINAL repo (owner only, never an agent): `git fetch` the isolated
   worktree or apply the bundle:
   `git bundle verify K:\KNOUX-AI-Pro-CORE-ISOLATED\core-slices-230d30d.bundle`
   `git merge 372a3e3ca1aef08ddc67ad92a57e254b75655c13` (after re-checking
   master; re-run merge-tree against the then-current master if it moved).
2. Re-run `.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug` in master.
3. Expected result: 200 JVM tests, 0 failures, debug APK builds.
4. UI conflicts: none expected — the Visual Agent's layout files were not
   modified; the only UI-adjacent edits are journal-call swaps inside existing
   IO blocks of 5 ViewModel/screen files (listed per slice).

## Disk-space actions taken (safe, regenerable only)

- Cleaned build outputs ONLY inside the isolated worktree (`app/build`,
  `.gradle`) — ~0.35 GB reclaimed; all regenerable by Gradle. No source,
  asset, schema, or data file was deleted.
- Refused: merging into master / pushing (mission boundary), writing to the
  original repo, and deleting "old non-unique files" without an explicit file
  list + impact assessment (destructive; mission boundary). K: has ~17 GB
  free, D: ~8 GB free after cleanup.
