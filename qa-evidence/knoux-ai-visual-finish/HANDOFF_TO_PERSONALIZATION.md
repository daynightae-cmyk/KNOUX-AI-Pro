# Handoff to the personalization phase

**Do not start service personalization yet.** The visual phase is implemented
and compiles, but it is not visually accepted on the device.

## Completed in this phase

- Cinematic splash per DESIGN_TOKENS: 3.1 s, stroke-ordered authentic K,
  progressive `knoux ai`, drawn smile, single 70 ms click at 2440 ms, sparkle,
  scale/fade settlement and 380 ms crossfade into Home.
- Silent/DND/system/app-preference sound guards and reduced-motion path.
- 10 interior detail surfaces (11–20) + App Manager detail routing.
- Every overview detail CTA now navigates; no detail CTA is inert.
- Honest preview labelling and unsupported-capability notices across details.
- Real Android settings handoffs and a live battery reading.

## Required before visual sign-off

1. **Install and launch on the phone.** Open `http://127.0.0.1:8099/install.html`
   on the device, download, allow unknown-source install for the browser, install
   `com.knoux.ai.debug`, then open KNOUX AI.
2. **Capture evidence** (screen recording + screenshots) for:
   - the whole splash with audio;
   - Home, all 9 overviews, App Manager overview;
   - each detail 11–20 and the back path.
3. **Re-attach the missing references (08–20).** Only 01–07 arrived in this
   session; 08 is an older kit copy with a different hash. Pixel comparison for
   08–20 cannot be claimed until the correct PNGs are present.
4. Fix any visual deltas found by side-by-side comparison (spacing, bloom,
   hero proportions, typography).
5. Verify the click once and only once, at the smile tip, with mute/DND off;
   then repeat with silent mode and with animator scale 0.

## Known implementation debt for personalization

- All numbers remain `Fixtures` previews. No scanner, MediaStore query,
  UsageStats, PackageManager inventory or persistence layer is connected yet.
- Deletion/cleanup paths are deliberately not implemented; confirmation dialogs
  state this.
- Report export is a status dialog, not a PDF pipeline.
- Sound setting has no UI yet; the preference key is `knoux_prefs/sound_enabled`.
- No unit/instrumented tests exist.

## Safe next step after sign-off

Build the service layer in this order: storage/MediaStore measurement →
duplicate hashing with keep-one invariant → residue inventory with SAF/
MediaStore scoping → privacy permission inventory via PackageManager → battery
and usage signals via documented APIs → persistence (Room/DataStore) → reports
from stored events. Replace each preview label only when its value is genuinely
measured.
