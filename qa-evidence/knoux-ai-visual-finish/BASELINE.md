# KNOUX AI — Visual Phase Baseline

Date: 2026-11-11 (UTC)
Owner: Eng. Sadek Elgazar
Execution host: Android phone (itel S686LN, Android 15 / SDK 35) through the OpenCode proot Ubuntu environment.

## Canonical project

| Item | Value |
|---|---|
| Project root | `/root/workspace/KNOUX-AI` |
| Stack | Kotlin + Jetpack Compose (Material 3), AGP 8.9.3, Kotlin 2.1.0 |
| Package | `com.knoux.ai` (debug suffix `.debug`) |
| Build variant | `debug` |
| compileSdk / targetSdk | 36 / 36 |
| minSdk | 26 |
| Git baseline before this pass | `58d2cac` (compile fixes, first successful APK) |
| Git state at evidence capture | `e344e22` (splash + detail screens) plus the integrated working-tree changes described in `AUDIT_AND_NEXT_PROMPT_AR.md` |
| APK | `app/build/outputs/apk/debug/app-debug.apk` |
| APK size / SHA | 19,130,812 bytes · `e6d929e6377eda1a595ebbb0b2fb2f9f93125d6059c7bd1939dd1fb0d2276a4e` |

## Toolchain reality

The container is the phone's proot Ubuntu environment (`aarch64`), not a PC.
An `adb` binary is available through an x86_64/qemu wrapper, but `adb devices`
lists **no devices**: the phone's wireless-debugging endpoint was not found on
loopback, so package installation, `logcat`, `screencap` and on-device UI
automation are still not available to the agent directly.

Installation is delivered through the project's loopback dev server on
`127.0.0.1:8099`, opened in the phone browser. This works because the container
loopback is shared with the Android device.

- JDK 17 (`/opt/jdk17`), Gradle 8.11.1, Android SDK 36.
- `aapt2` is the x86_64 binary run through `qemu-x86_64-static` (no linux-arm64
  Android build-tools are published).
- Build command:
  `JAVA_HOME=/opt/jdk17 ANDROID_HOME=/opt/android-sdk /opt/gradle/gradle-8.11.1/bin/gradle --no-daemon --console=plain -p /root/workspace/KNOUX-AI assembleDebug`

## Reference inventory and integrity

The `manifest.json` supplied with the 20-reference kit was verified against the
attachments that actually arrived in this session.

| ID | Reference | Status |
|---|---|---|
| 01 | Splash | present · SHA-256 matches manifest |
| 02 | Home Dashboard | present · SHA-256 matches manifest |
| 03 | Privacy Sweep | present · SHA-256 matches manifest |
| 04 | Duplicate Files Finder | present · SHA-256 matches manifest |
| 05 | Deep Scan | present · SHA-256 matches manifest |
| 06 | Reports & Insights | present · SHA-256 matches manifest |
| 07 | App Residue Cleanup | present · SHA-256 matches manifest |
| 08 | Storage Analyzer | older kit copy only · SHA-256 differs from this manifest |
| 09 | Battery Saver | **not attached** |
| 10 | AI Optimization | **not attached** |
| 11–20 | Detail screens | **not attached** |

Files 01–07 are stored in `design/refs20/`; a comparison copy is in
`qa-evidence/knoux-ai-visual-finish/VISUAL_COMPARISONS/references/`.

Consequence: screens 11–20 are implemented from the binding descriptions in
`MASTER_PROMPT.md`, not from pixels. Pixel-fidelity acceptance for 08–20 is
**BLOCKED** until the missing PNGs/ZIP are re-attached.

## What existed before this pass

A Phase-1 Compose UI source baseline existed with: theme tokens, shared neon
components, Home, 10 overview surfaces, bottom navigation and a first splash.
That pass compiled to an APK (`58d2cac`). All work was preserved; no project,
branch or module was recreated.

## Added in this pass

1. Splash rewritten around the DESIGN_TOKENS storyboard: one 3.1 s master clock,
   stroke-ordered K reveal, `knoux ai` write-on, left→right smile with moving
   tip and final sparkle, **one** 70 ms synthesized click at 2440 ms through
   SoundPool, silent/DND/system-sound/app-preference guards, reduced-motion
   static path.
2. Ten interior detail screens (11–20) in the shared design system, with
   honest preview labels, unsupported-capability notices, real Android settings
   handoffs, and a live BatteryManager reading on 19.
3. Routing: every overview detail CTA opens its matching detail; Home primary
   CTA opens Smart Clean Center (11); Deep Scan completion path opens 14;
   App Manager opens 17.
4. Splash→Home handoff through a 380 ms crossfade (splash itself scales/fades
   out from 2720–3100 ms).
5. Debug-only runtime capture: `RuntimeProbe` + `KnouxDebugApp` +
   `RuntimeUploader` use `PixelCopy` to post screenshots of the app window to
   `http://127.0.0.1:8099/upload`. The dev server stores them in
   `qa-evidence/runtime/`. This is a capture harness, **not** evidence until the
   app is installed and the PNGs actually arrive.

## Known limitations (not hidden)

- The APK has not been installed or launched yet; install confirmation is
  pending the user's tap. No runtime screenshot, recording or click-audio sync
  evidence exists yet.
- The screenshot harness requires the user to install the debug APK and open
  screens; it cannot install or drive the app by itself.
- `adb devices` lists no device; there is no reliable adb/screencap path.
- The click WAV is synthesized and packaged; audible timing is unverified.
- No instrumented/unit tests exist yet; there is no CI.
- Reference numbers remain labelled design previews; no telemetry is claimed.
- References 08–20 are missing, so pixel acceptance for them is blocked.
