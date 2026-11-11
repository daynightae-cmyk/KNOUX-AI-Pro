# Runtime capture (on-device evidence)

Screenshots in this folder are **not** mockups and not rendered previews: they
are captured by the KNOUX AI **debug** app on the phone itself.

## How it works

| Piece | Where | What it does |
|---|---|---|
| `RuntimeProbe` | `app/src/main/java/com/knoux/ai/ui/debug/RuntimeProbe.kt` | No-op hook in the main source set (the release variant must not know about debug code). |
| `KnouxDebugApp` | `app/src/debug/java/com/knoux/ai/debug/KnouxDebugApp.kt` | Debug-variant `Application`; installs the hook at process start. |
| `RuntimeUploader` | `app/src/debug/java/com/knoux/ai/debug/RuntimeUploader.kt` | `PixelCopy` of the app's own window → PNG → `POST http://127.0.0.1:8099/upload`. |
| `knoux_dev_server.py` | `scripts/knoux_dev_server.py` | Serves `release/` on `0.0.0.0:8099` and stores posted PNGs here. |
| Trigger | `MainActivity.kt` | Captures the splash (~2.6 s) and every navigation destination (~0.9 s after the route changes). |

## Boundaries

* Debug variant only: `app/src/debug/**` is not compiled into the release APK,
  and `INTERNET` + `usesCleartextTraffic` are declared in the **debug**
  manifest only. The release APK requests no permissions at all.
* The upload target is the phone's own loopback. Nothing leaves the device.
* File names: `<route>-<HHMMSS>.png` (e.g. `home-140233.png`, `00_splash-140230.png`).

## How to reproduce

```bash
# 1. server (serves release/ and accepts uploads)
python3 /root/workspace/KNOUX-AI/scripts/knoux_dev_server.py

# 2. build + publish
JAVA_HOME=/opt/jdk17 ANDROID_HOME=/opt/android-sdk \
  /opt/gradle/gradle-8.11.1/bin/gradle --no-daemon --console=plain \
  -p /root/workspace/KNOUX-AI assembleDebug
cp /root/workspace/KNOUX-AI/app/build/outputs/apk/debug/app-debug.apk \
   /root/workspace/KNOUX-AI/release/app-debug.apk

# 3. install on the phone from http://127.0.0.1:8099/install.html
# 4. tap through the screens -> PNGs land in this folder
```
