# KNOUX AI — Android app (phone-native workspace)

Canonical project root **on the phone**: `/root/workspace/KNOUX-AI`
(app package `com.knoux.ai`, applicationId `com.knoux.ai.debug` for the debug variant).

> The upstream brief mentions `D:\KNOUX-AI` (a Windows path). Per the KNOUX
> phone-first policy that path is treated as a legacy hint: the real, reachable
> workspace is the one above, inside the OpenCode Android app sandbox, and it is
> the only place where this project is built, installed and tested.

## What this project contains

| Path | Content |
|---|---|
| `app/` | Native Kotlin + Jetpack Compose app (Phase 1: UI only) |
| `design/reference/` | The 10 approved reference PNGs (864×1536) received from the owner |
| `design/original_hi_res/` | 11 higher-resolution originals (941×1672) received from the owner |
| `design/original_jpeg/` | The original JPEG bytes of the reference frames |
| `docs/` | Play-policy matrix, fake-closure gate, architecture and research notes |
| `prompts/phase2/` | The ten per-service implementation missions (not started) |
| `provenance/` | What was verified in the supplied bundle vs. what was not |
| `scripts/Verify-Bundle.py` | Offline integrity check for the supplied kit |

## Phase 1 scope (as ordered by the master prompt)

* Ten real Compose screens with working navigation, selection states and
  preview-only interactions.
* Fixture values are the **design reference numbers** from the approved mockups.
  They are always labelled `DESIGN PREVIEW` and must never be presented as
  device measurements (see `docs/testing/FAKE_CLOSURE_PRODUCTION_GATE.md`).
* No scanner, no cleaner, no Room schema, no cloud, no privileged permissions.
  The app requests **no runtime permissions at all** in this phase.

## Build toolchain that runs on the phone

The container had no JDK/Gradle/Android SDK, so a native toolchain was
bootstrapped **on the device**:

| Component | Version | Location |
|---|---|---|
| JDK (Temurin, aarch64) | 17.0.21 | `/opt/jdk17` |
| Gradle | 8.11.1 | `/opt/gradle/gradle-8.11.1` |
| Android SDK (cmdline-tools, platform-tools, build-tools 36.0.0, platform android-36) | — | `/opt/android-sdk` |
| aapt2 (x86_64 binary executed under `qemu-x86_64-static`, because Google ships no linux-arm64 build) | 8.9.3 | `/opt/bin/aapt2` |

`gradle.properties` points AGP at the qemu-wrapped aapt2 through
`android.aapt2FromMavenOverride=/opt/bin/aapt2`.

### Build command

```bash
JAVA_HOME=/opt/jdk17 ANDROID_HOME=/opt/android-sdk \
  /opt/gradle/gradle-8.11.1/bin/gradle --no-daemon --console=plain \
  -p /root/workspace/KNOUX-AI assembleDebug
```

Output APK: `app/build/outputs/apk/debug/app-debug.apk`

## Status vocabulary

`DESIGNED` · `UI_IMPLEMENTED` · `BUILD_VERIFIED` · `RUNTIME_VERIFIED` ·
`VISUAL_APPROVED` · `SERVICE_IMPLEMENTED` · `SERVICE_VERIFIED` · `MERGED` ·
`RELEASED` — plus `PARTIAL`, `UNVERIFIED`, `BLOCKED`, `UNSUPPORTED`.

Current status is recorded in `qa-evidence/build/build-test-lint-results.md`
and `PHASE_1_HANDOFF.md`. Never describe this project as production-ready on
the basis of a build alone.
