# Test results — final visual-milestone build

Date: 2026-11-11 (UTC)
Project: `/root/workspace/KNOUX-AI`
Variant: `debug` (`com.knoux.ai.debug`)

## Build

| Attempt | Result |
|---|---|
| build2 `assembleDebug` | FAILED after 42m05s — 39 Kotlin errors (clipRect, drawCircle pathEffect, BasicText builder, trailing-lambda order, Trophy icon, lazy items, composable scaffolding) |
| build3 `assembleDebug` | FAILED — remaining SelectAllPill order + drawPath pathEffect |
| build4 `assembleDebug` | **SUCCESS** 4m37s — first APK after fixes (18,873,516 bytes) |
| build5 `assembleDebug` | **SUCCESS** 3m14s — cinematic splash compiled |
| build7 `assembleDebug` | **SUCCESS** 4m02s — detail screens 11–20 and routing compiled |
| build8 `assembleDebug` | **SUCCESS** 4m08s — splash background/smile refinements |
| build10 `assembleDebug` | **SUCCESS** 3m18s — Home remap, menu, settings intents |
| build11 `assembleDebug` | FAILED — corrupted incremental dex cache (`desugar_graph/.../graph.bin` missing) |
| build12 `assembleDebug` | FAILED — stale Kotlin incremental-cache error at `Neon.kt` |
| build13 `assembleDebug` | **SUCCESS** `GRADLE_EXIT=0` — after clearing only Kotlin/dex build caches. Final APK produced. |
| build_final `assembleDebug` | **SUCCESS** 3m41s — `35 actionable tasks: 35 up-to-date`, `GRADLE_EXIT=0` |
| verify_build `assembleDebug` | **SUCCESS** 1m34s — independent re-run, same result; APK hash re-checked |
| build14 `assembleDebug` | **SUCCESS** 3m38s — LTR layout fix, splash seam fix, global preview-disclosure, Home density pass |

Command used:

    JAVA_HOME=/opt/jdk17 ANDROID_HOME=/opt/android-sdk \
    /opt/gradle/gradle-8.11.1/bin/gradle --no-daemon --console=plain \
    -p /root/workspace/KNOUX-AI assembleDebug

Raw build logs: `logs/build14.txt`, `logs/build_final.txt`, `logs/verify_build.txt`.
No unit or instrumented test task exists in the project yet.

## Artifact

- Path: `app/build/outputs/apk/debug/app-debug.apk`
- Size: **19,130,812 bytes**
- SHA-256: **`e6d929e6377eda1a595ebbb0b2fb2f9f93125d6059c7bd1939dd1fb0d2276a4e`**
- Package: `com.knoux.ai.debug`, versionCode 1, versionName `1.0.0-debug`
- minSdk 26, targetSdk 36, compileSdk 36
- Resources present inside the APK: `res/raw/knoux_click.wav`,
  `res/drawable-nodpi-v4/knoux_symbol_alpha.png`,
  `res/drawable-nodpi-v4/knoux_wordmark_alpha.png`.
- Debug-only runtime-capture code is present in the APK:
  `com.knoux.ai.debug.KnouxDebugApp`, `RuntimeUploader`, and the merged
  manifest application name `com.knoux.ai.debug.KnouxDebugApp`.
- The APK is a debug build; it is not a release/signed production artifact.

## Runtime capture harness (integrated, not yet exercised)

The debug APK contains an on-device screenshot path so that runtime evidence no
longer depends on the agent having `screencap`:

- `RuntimeProbe` (main source set) is a no-op hook.
- `KnouxDebugApp` (debug source set) installs the hook.
- `RuntimeUploader` uses `PixelCopy` on the app window and POSTs the PNG to
  `http://127.0.0.1:8099/upload`.
- `MainActivity` triggers a capture at ~2.6 s (splash) and ~0.9 s after each
  navigation destination change.
- `scripts/knoux_dev_server.py` serves `release/` and stores uploads in
  `qa-evidence/runtime/`.

Local server verification: `GET /install.html` → 200, `GET /app-debug.apk` →
200, `POST /upload?name=selftest` → 200 and wrote a real PNG (test file removed
after verification). No app screenshot has been received yet because the APK is
not installed.

## Installation

- The final dev server is running on `0.0.0.0:8099` from
  `scripts/knoux_dev_server.py`; it serves `release/install.html` and
  `release/app-debug.apk` (same SHA-256 as the build output).
- `http://127.0.0.1:8099/install.html` was opened on the phone through the
  device bridge (`success: true`), and an install notification was posted
  (`success: true`).
- Install confirmation and first launch: **PENDING USER TAP**. The phone still
  requires the user to download the APK, allow installation from the browser,
  install, and open KNOUX AI.
- `adb devices` returns an empty list through the qemu-wrapped platform-tools
  binary; no wireless-debugging adbd endpoint was found on the phone loopback.
  The agent therefore cannot install, launch, read logcat or capture the screen
  through adb.

## Static checks performed

- All Kotlin sources compile with AGP 8.9.3 / Kotlin 2.1.0.
- Debug manifest merge verified: INTERNET + cleartext are debug-only; debug
  Application class resolves to `com.knoux.ai.debug.KnouxDebugApp`.
- APK contains 11 dex files, including `classes10.dex` with `RuntimeUploader`.
- Detail values remain the existing preview fixtures, labelled
  sample/estimated/design preview in the UI; no fake telemetry source exists.
- Battery screen reads `ACTION_BATTERY_CHANGED` at runtime.
- Settings handoffs use documented Android actions.
- No empty click lambdas remain in `app/src/main/java` (verified by search);
  overview CTAs and rows open real detail routes.

## Not run / blocked

| Check | Status | Reason |
|---|---|---|
| Install via adb | BLOCKED | `adb devices` empty; no adbd endpoint found |
| Launch and navigate on device | PENDING | requires user tap on the install page |
| Screenshot per screen | PARTIAL | PixelCopy uploader is integrated and server verified; no app capture received yet |
| Splash recording + audio sync | BLOCKED | no screen/audio recorder in the agent bridge; splash click is code-verified only |
| Frame jank measurement | BLOCKED | no atrace/gfxinfo access |
| Unit / instrumented tests | NOT PRESENT | no test sources in project |
| Pixel fidelity for refs 08–20 | BLOCKED | refs 08 (hash mismatch) and 09–20 (missing) not supplied |
