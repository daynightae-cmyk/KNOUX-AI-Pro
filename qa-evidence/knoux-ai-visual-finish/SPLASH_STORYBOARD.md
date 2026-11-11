# Splash storyboard — implementation record

Source of truth: `design/kit20/DESIGN_TOKENS.json` + `MASTER_PROMPT.md` §3.
Implementation: `app/src/main/java/com/knoux/ai/splash/KnouxSplash.kt`.
Sound asset: `app/src/main/res/raw/knoux_click.wav` (70 ms, mono 44.1 kHz, 6,218 bytes,
synthesized in-repo — no third-party sample).

## Timing (single master clock, 3100 ms, LinearEasing)

| Window | ms | What is rendered |
|---|---|---|
| bloom | 0–180 | dark indigo stage + violet radial bloom |
| K wedge | 180–620 | top band of the authentic K raster, magenta leading edge |
| K diagonal | 520–980 | middle band, violet leading edge |
| K lower leg | 760–1120 | lower band, cyan completion |
| wordmark | 1030–1680 | `knoux ai` raster wipe left→right + white writing tip |
| smile | 1600–2420 | U-shaped shallow arc drawn left→right, magenta→violet→cyan, glowing tip |
| sparkle | 2420–2780 | one 4-ray star burst at the final (right) smile tip |
| click | 2440 | one SoundPool one-shot, 20 ms after the smile completes |
| settle + exit | 2420–3100 | lockup holds, then alpha 1→0.1 and scale 1→0.965 |
| handoff | 3100 + 380 | Crossfade in `MainActivity` to the real Home dashboard |

Total in-app reveal: 3.10 s — inside the required 2.7–3.3 s band.

## Why the click is synchronous and singular

- The master `Animatable` is observed with `snapshotFlow { clock.value }` and the
  click fires on the first frame where `t >= 2440 ms`. This ties the sound to the
  rendered frame, not to a separate timer.
- `SplashClickPlayer` owns one `SoundPool` (max 1 stream), preloaded in
  `DisposableEffect`, released on dispose. There is exactly one call site.
- Guards, in order: app preference `knoux_prefs/sound_enabled` (default true),
  system `SOUND_EFFECTS_ENABLED`, ringer mode must be `NORMAL` (silent/vibrate
  skipped), interruption filter must be `ALL` (DND skipped).
- The reveal runs only when `splashDone == false` in a fresh composition; it is
  not replayed on `onResume`. `rememberSaveable` does not replay it on rotation.

## Reduced motion

`Settings.Global.ANIMATOR_DURATION_SCALE == 0` snaps the clock to the final
lockup, waits 700 ms, and skips both the click and the sparkle. No animation
blocks access to Home.

## Brand fidelity

The K and the wordmark are the approved transparent rasters
(`knoux_symbol_alpha.png`, `knoux_wordmark_alpha.png`), not invented vectors.
The K is revealed in three stroke-ordered bands with luminous leading edges and
a soft radial bloom, on a stage with no painted phone frame, camera cutout or
status bar.

## Evidence status

- Source-level timing and guard logic: **VERIFIED** (code inspection + compiled).
- Click asset packaged: **VERIFIED** (`res/raw/knoux_click.wav` present in APK).
- Audible sync / recorded reveal: **NOT CAPTURED** — the environment has no
  `adb`, no `screencap`, no audio capture. A real screen recording with audio is
  required before this can be marked visually accepted.
