# Splash and walkthrough video — not captured

A real screen recording with audio is required to prove the click lands on the
smile's final tip. This environment cannot record the device screen:

- no `adb` binary in the proot Ubuntu container;
- no `/system/bin/screencap` or `/system/bin/input`;
- the Android capability bridge exposes notifications, clipboard, URL opening
  and file transfer only, not screen capture.

Required capture: one cold-start recording including system audio, then a
walkthrough Home → overview → detail → back. Store files here and reference them
from `SPLASH_STORYBOARD.md`.
