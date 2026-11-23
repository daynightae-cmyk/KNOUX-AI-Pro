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
| Google/Microsoft connectors | under audit (P4) | `google/`, `microsoft/` |
| Supabase app tables + RLS | MISSING remotely → migrations prepared in later slice (P5); README references `001_core.sql` etc. which do NOT exist in repo | `database/supabase/` |
| Developer Studio providers + vault | under audit (P6) | `developer/` |
| secureRequest HTTP-status retry | PARTIALLY_CONNECTED → repaired in Slice 2 (429/5xx never retried) | `security/ProductionSecurity.kt` |

## Slices

### Slice 1 — Atomic Room journaling (P2)

DOMAIN: persistence / repository integrity
BASE SHA: `230d30db9dbd83ddb4f857bea05ad0697d2fd660`
COMMIT SHA: (recorded below after commit)
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
BASE SHA: `230d30db…` (Slice 1 commit)
COMMIT SHA: (recorded below after commit)
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
