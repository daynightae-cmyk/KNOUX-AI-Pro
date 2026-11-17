# KNOUX AI (MOBILE OPENCODE) — SOURCE READINESS AUDIT
Date: 2026-11-11 (Asia/Dubai)
Scope: source preserved in the mobile OpenCode ZIP only. PC project D:\KNOUX-AI was NOT accessed or changed by this audit.

## Evidence and integrity
- Source: D:\KNOUX-AI-Phone-Exports\KNOUX-AI-phone-snapshot-210018.zip
- Bytes: 2,470,499,860; archive SHA-256: cef861bc756c6aeda6a85b36c07d12f798280e9fd368d5d545b332298fbf2e88
- SHA-256 matched the Android source ZIP byte-for-byte; all 3,188 ZIP entries passed Python CRC verification.
- Snapshot archive HEAD: master -> cffe7b4f7cc1de4beb5539202dbcf067de19741e. This HEAD does not cover uncommitted work.
- The OpenCode Git UI separately showed 104 changed files. Current live phone working-tree status AFTER snapshot was not reverified.
- Archived source includes 32 main Kotlin files, 3 debug Kotlin files, 20 original references in design/refs20_complete, and a separate 11 high-resolution image directory.
- No app/src/test or app/src/androidTest source was found in this snapshot. Older QA screenshots are not a substitute for tests of current changes.
- The newly approved final ten-image visual package is NOT identifiable as a separate mapped set in the archive.

## What is implemented
1. Functional Compose navigation structure: splash, welcome, Home/Clean/Tools/Reports, Settings, main service screens, detail routes.
2. Distinct visual assets, animations, service-specific screens, preview interactions and user settings.
3. AuthGate.kt, AuthVisual.kt, FingerprintButton.kt, WelcomeScreen.kt present.
4. Android BiometricPrompt integrated and local biometric capability queried.
5. Credential Manager and Google ID sign-in UI plumbing present; real provider credentials and server verification NOT proven.
6. Battery Diagnostics reads real ACTION_BATTERY_CHANGED / BatteryManager fields in DetailScreens2.kt.
7. Settings has a real local-app-cache deletion action confined to context.cacheDir; not global device cleanup. It opens Android storage settings.
8. One canonical KnouxAppHeader() composition call found in MainActivity. Visual before/after not newly tested.

## Gaps and blockers
- Fixtures.kt explicitly declares Phase 1 UI-only fixture layer: not device telemetry. Most cleanup/storage/duplicate/privacy/AI claims remain preview.
- Main AndroidManifest.xml contains no uses-permission entries; runtime permission/capability orchestration still missing for real services.
- No Room/SQLite repository, worker/orchestrator, Retrofit/OkHttp backend client, or full lifecycle service layer evident in main Kotlin/dependencies.
- AuthGate.kt: runGoogleSignIn calls getCredential and returns AuthOutcome(success=true) without server-side Google ID token verification. Must NOT enable production login as-is.
- AuthGate.kt: runPasskeySignIn passes passkeyRpId directly into GetPublicKeyCredentialOption. Proper sign-in requires requestJson including a server-issued challenge, and server-side assertion verification.
- MainActivity.kt: biometric or Google/passkey success calls onContinueAsGuest(); it is an app entry gate, not an authenticated user session/account.
- MainActivity.kt: welcomeDone rememberSaveable is not a durable authentication/session trust boundary.
- Android manifest android:allowBackup=true: revisit backup/data-extraction rules before storing sensitive user content.
- Zero archived test source: implementation and security regression tests must be created.
- Build and instrumentation of THIS archived, uncommitted source were not executed during this static audit.
- Last ten approved reference images are not demonstrably integrated; do not claim final visual acceptance.
- APK installed at ~18:08 may predate archive contents recorded at ~21:03; rebuild/install/compare are needed after changes.
- Android 11+ restricts Android/data and Android/obb access even under broad storage access, and Google Play reviews MANAGE_EXTERNAL_STORAGE. Do not promise inaccessible private app-cache cleaning.

## Engineering decision
READY TO BEGIN END-TO-END ENGINEERING: YES — existing UI/navigation/design system is a usable starting foundation.
READY FOR PRODUCTION OR REAL DEVICE CLEANER RELEASE: NO — critical service, permission, test and auth gaps remain.

## Recommended controlled execution order (on MOBILE workspace only)
0. Preserve full mobile ZIP and independent latest Git state; do not merge with the Windows application.
1. Land and verify the ten owner-approved visual references; close Auth/header/splash parity before visual baseline lock.
2. Introduce real typed service contracts, explicit provenance (PREVIEW vs LIVE), permissions/capability matrix, and error/empty states.
3. Implement read-only device inventory: Storage/MediaStore/SAF, PackageManager with package visibility constraints, real battery telemetry; test on device and emulator.
4. Implement duplicate detection using content hash, candidate review, scoped user confirmation, and reversible safe actions.
5. Implement cleanup only for supported user-accessible files via MediaStore/SAF/user-granted permissions. No silent or indiscriminate deletion; never claim cross-app cache cleanup.
6. Implement app manager, privacy signals, settings hops, scan orchestration, and reports based only on evidence from real adapters.
7. Implement AI recommendation engine with explainable real data and appropriate limits; separate recommendations from execution.
8. Build comprehensive unit, instrumentation, privacy, performance and device compatibility tests; collect actual runtime screenshots and logs.
9. Final pre-production identity provider configuration: Google server ID token verification, Passkey WebAuthn challenge/assertion server verification, secure sessions, biometric binding, secret management, server auth and account deletion/privacy.
10. Run CI/release signing/Google Play compliance and physical-device verification; do not call PASS without reports/SHAs/runtime evidence.

## Caution
This was a read-only forensic audit of an archived mobile workspace. The full 2.47GB archive was NOT extracted onto the phone or into the PC project's repository. Only 49 relevant source/config/document files totaling 458,344 bytes were selectively extracted to:
D:\KNOUX-AI-Phone-Exports\mobile_archive_audit\inspectable_source
This is an inspection-only copy, not a separate canonical project and not a merge target.

Android references:
https://developer.android.com/identity/passkeys/sign-in-with-passkeys
https://developer.android.com/training/data-storage/manage-all-files
https://support.google.com/googleplay/android-developer/answer/10467955
