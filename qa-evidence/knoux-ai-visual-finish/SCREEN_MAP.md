# Screen map — references to implementation

| Ref | Surface | Route | Entry point | Detail link |
|---|---|---|---|---|
| 01 | Cinematic splash | (overlay) | Cold start | → Home via 380 ms crossfade |
| 02 | Home dashboard | `home` | Start destination | orb/CTA + 6 service cards |
| 03 | Privacy Sweep overview | `privacy` | Home card · Tools tab | `privacy-risk` (13) |
| 04 | Duplicate Files overview | `duplicates` | Home card · Clean tab | `duplicate-group` (12) |
| 05 | Deep Scan | `deepscan` | Home CTA · Clean tab | `deep-scan-summary` (14) |
| 06 | Reports & Insights | `reports` | Reports tab | `performance-report` (15) |
| 07 | App Residue overview | `residue` | Home card · Clean tab | `residue-item` (16) |
| 08 | Storage Analyzer overview | `storage` | Home card · Tools tab | `storage-breakdown` (18) |
| 09 | Battery Saver overview | `battery` | Tools tab | `battery-diagnostics` (19) |
| 10 | AI Optimization overview | `ai` | Home card · Tools tab | `ai-recommendations` (20) |
| 11 | Smart Clean Center | `smartclean` | Home primary CTA | → `deepscan` |
| 12 | Duplicate Group Review | `duplicate-group` | 04 detail CTA | back |
| 13 | Privacy Risk Inspector | `privacy-risk` | 03 detail CTA | OS app/privacy settings |
| 14 | Deep Scan Summary | `deep-scan-summary` | 05 "View Summary" | → `smartclean` |
| 15 | Performance Report Detail | `performance-report` | 06 "View History" | export status dialog |
| 16 | Residue Item Review | `residue-item` | 07 detail CTA | review dialog |
| 17 | Installed Apps Review | `installed-apps` | App Manager overview CTA | OS app settings |
| 18 | Storage Breakdown | `storage-breakdown` | 08 "View Breakdown" | OS storage settings |
| 19 | Battery Diagnostics | `battery-diagnostics` | 09 "View Details" | live BatteryManager + saver settings |
| 20 | AI Recommendations | `ai-recommendations` | 10 "See Recommendations" | evidence/apply dialogs |
| — | App Manager overview | `appmanager` | Tools tab | `installed-apps` (17) |

## Honesty rules enforced in the UI

- Every screen carries a preview disclosure chip; fixture numbers are labelled
  "sample", "preview", "estimated" or "not measured".
- No screen claims an action succeeded. Destructive actions open an explicit
  explanation dialog and state that deletion/measurement is not wired yet.
- Unsupported Android capabilities are shown as `Unavailable` (charge cycles,
  per-app drain, `/Android/data` access, universal tracker blocking).
- Real OS handoffs are real intents: app settings, privacy settings, internal
  storage settings, battery-saver settings.
- Battery screen reads level/status/temperature/voltage/health/technology live
  from the `ACTION_BATTERY_CHANGED` sticky intent.

## Current verification status

- Compilation and APK packaging: **VERIFIED**.
- Route wiring: **VERIFIED by source inspection**; runtime navigation not yet
  captured.
- Visual match per reference: **PARTIAL** for 01–07 (implementation follows the
  references, no runtime screenshots yet), **BLOCKED** for 08–20 (pixels not
  supplied).
