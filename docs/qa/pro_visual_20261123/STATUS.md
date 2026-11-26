# KNOUX AI Pro — Visual Reality Mission 20261123 — STATUS (updated 2026-11-26)

## Repository state (current)

- Repo: `K:\KNOUX-AI-Pro` (canonical), branch `master`
- Local HEAD: `0251e129ffd209de2faa8deb295f23908704ecfa` (pushed to `origin/master`)
- Pending uncommitted work: `DeviceReality.kt`, `MoreScreensV2.kt`, `CleanToolsV2.kt`, `DeepScanEngine.kt`, `DuplicateWorkspaceV2.kt` edits + this STATUS.md
- Untracked (NOT staged, disposable Python repair artifacts): `add_imports.py`, `fix_brace.py`, `fix_privacy.py`, `fix_privacy2.py`
- Device: Samsung SM-A025F at `192.168.70.109:43477` (adb `-s` explicit); itel `1334825549012211` present but NOT used
- Installed package: `com.knoux.ai.debug` versionName `1.0.0-debug` (install `-r`, data preserved)

## Last verified build

- `.\gradlew.bat :app:assembleDebug --no-daemon` → BUILD SUCCESSFUL
- APK: `app\build\outputs\apk\debug\app-debug.apk`
- SHA-256: `5e2f298ea5dfe27db4a6477b10004ad7997f642000db0480df0662436f62ff84`
- Installed `-r` on Samsung: INSTALL_EXIT=0

## What this increment fixes (real-data honesty gaps)

All edits are in the existing V2 source — no new files, no redesign, no fake data.

1. `MoreScreensV2.kt` `V2OldBackupsScreen` — removed fixture `oldBackups` (5 invented backups with sizes/dates) and the no-op destructive `V2GradientButton("حذف … نسخ احتياطية", {}, …)`. Replaced with a genuine SAF folder picker (`OpenDocumentTree`) that reads the user-chosen folder's real files via `readScopedFolder`. Shows real names/sizes/dates/mimes, max 500 entries, open-in-system handler, honest empty/unavailable states. No delete button — KNOUX does not delete backups.

2. `MoreScreensV2.kt` `V2SystemLogsScreen` — removed fixture `systemLogs` (8 invented logcat-style entries). Replaced with the app's own journal: Room `scan_sessions` (recent 40) merged with locally saved `ScanHistoryEntry` measurements, deduplicated by timestamp, showing real service name/status/item counts/bytes/timestamps. Honest notice that Android blocks third-party logcat access. Refresh + empty state.

3. `MoreScreensV2.kt` `V2DeviceInfoScreen` — removed hardcoded `"com.knoux.ai.pro"` and stray Korean `여기`. Now reads real `context.packageName`, `PackageInfoCompat.getLongVersionCode`, `Build.MANUFACTURER/MODEL`, `Build.VERSION.RELEASE/SDK_INT`, `SUPPORTED_ABIS`, `Build.VERSION.SECURITY_PATCH`, `Locale.getDefault().toLanguageTag()`. Unavailable values shown honestly.

4. `MoreScreensV2.kt` `V2NetworkScanScreen` — removed hardcoded `"Wi-Fi · آمن"`, `"2.4 GB هذا الشهر"`, `"اليوتيوب · 45%"`, and fixture `NetworkRow`s. Binds to real `ConnectivityManager` (active network transport, VALIDATED, metered, link bandwidth, VPN) plus `readNetworkUsage()` via `NetworkStatsManager` (month-to-date Wi-Fi/cellular bytes + per-app top consumers, gated behind Usage Access). Shows Loading / Permission-required / Query-failed / No-activity / Actual-zero honestly. No division-by-zero or misleading percentages.

5. `MoreScreensV2.kt` `V2SecurityScanScreen` — removed fabricated `"82%"`, `.82f` progress, `"4 فحوصات مكتملة"`, all-green `SecurityRow`s. Binds to real readers: `readLockSecure`, `readVpnActive`, `readDeviceAdminCount`, `overlayDeclaredCount`, `captureRiskCount`, security patch age, biometric strength, undeclared-store app count. Computes a local 0–100 estimate only when `facts.isLive`; otherwise shows UNKNOWN. Each row shows real state + detail. Honest disclaimer that KNOUX is not an antivirus.

6. `MoreScreensV2.kt` `V2NotificationManagerScreen` — removed fabricated `"12 تطبيق"` and per-app unread counts. Uses real `NotificationManagerCompat.areNotificationsEnabled()` for KNOUX itself, real notification channels (Android 8+), and the real installed user-app list; each app row opens its genuine system notification settings via `openAppNotificationSettings`. Honest that KNOUX cannot read other apps' notifications. No fabricated counts.

7. `CleanToolsV2.kt` `V2CleanToolScreen` — added LOADING / FAILED / storage-unread / media-unread states. Failed reads render a `ServiceCapabilityCard` (PERMISSION state) instead of zeros posing as success. Refresh re-reads. Preview note distinguishes real readings from unavailable.

8. `DuplicateWorkspaceV2.kt` — stale-selection CTA fix: result/cleared state now invalidates old selections and keepers; `selectedUris`/`selectedBytes` are computed only over files present in the current result, so the "مراجعة وحذف المحدد" CTA can never act on a previous scan's files.

9. `DeepScanEngine.kt` — each completed/cancelled/failed deep scan now creates (and completes) a real Room `scan_session` (`serviceId="deep_scan"`) via `KnouxRepository`, connected to `saveScanHistory`. The summary/activity log can show the real session instead of claiming none exists.

10. Dead fixtures removed from `MoreScreensV2.kt`: `managedAppsV2`, `appFilters`, `AppManagerSummary`, `storageCategories`, `StorageCategoryRow`, `DrainRow`, `aiActions`, `AiActionRow`, `ManagedAppRow` — all unused after the app manager screen was already real (PackageManager-based). No behavior change to live screens.

## Device verification (Samsung SM-A025F, live, `adb -s 192.168.70.109:43477`)

All six fixed routes launched via `am start -S -n com.knoux.ai.debug/com.knoux.ai.MainActivity --es route <route>`:
- `old-backups` — opens, no crash. Real SAF picker UI.
- `system-logs` — opens, no crash. Journal/Room-backed list.
- `device-info` — opens, no crash. Real Build/package values.
- `network-scan` — opens, no crash. Real connectivity + usage stats (permission-gated).
- `security-scan` — opens, no crash. Real device-state rows, local estimate.
- `notification-manager` — opens, no crash. Real channels + app list.

Authentic runtime screenshots captured for all six routes (D:\Temp\opencode\knoux\final_*.png, mean≈40 std≈29 = rendered content, not blank). No FATAL/crash traceback in logcat for any route.

Note: Samsung dims/locks the screen between wake and screencap; captures show dark cosmic-night theme (expected) but non-blank content. Functional verification (launch + no crash + real data binding) is the primary gate.

## Honesty vocabulary used
Loading / Permission-required / Measurement-unavailable / Query-failed / No-recorded-activity / Actual-zero — never a fake number standing in for a failed read.

## Next
- Commit this increment to `master` and push to `origin/master`.
- Verify `git ls-remote origin refs/heads/master` matches local HEAD.
- Continue with remaining real-data gaps (remaining secondary screens, cleaning tools routes, deep scan, duplicates, reports, cloud cleaner, developer studio subpages, auth gaps) per the execution order.
