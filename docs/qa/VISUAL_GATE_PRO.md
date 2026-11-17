# KNOUX AI Pro — Visual gate (definition of done for the visual layer)

Purpose: stop reviewing screens by eye, one at a time. Every route is captured by a
script, scored against a fixed checklist, and only then does service coding start.

## The loop (one route at a time, ~3 minutes)

1. `tools/qa/Capture-RouteMatrix.ps1 -Routes <route>` — cold start, settle, capture, hash.
2. Open the single PNG. Score it against the checklist below.
3. Fix only what the checklist fails. Rebuild. Re-capture that one route.
4. Record the row in `qa-evidence/visual/VISUAL_GATE_<date>.md`.

A route that captures at a different byte size than the previous run is a route that
changed — the index.csv diff tells you which ones still need review.

## Checklist (a route passes only if all six are true)

| # | Check | Fails when |
| - | ----- | ---------- |
| 1 | **Instrument present** — one signature visual carries the screen's meaning | screen is only text, lists, or plain cards |
| 2 | **Bound to real state** — geometry/motion encode what the code actually knows | animation runs while idle, progress fills without a measurement |
| 3 | **Unknown drawn as unknown** — fog / empty / indeterminate, never a fake number | 0%, "all clean", a gauge at rest poses as a measurement |
| 4 | **Text budget** — title ≤ 2 words, state caption ≤ 4 words, no sentence > 8 words | paragraph inside a visual surface |
| 5 | **RTL intact** — mirrored layout, LTR wordmark, no clipped Arabic | icon on the wrong side, truncated label, bidi-broken number |
| 6 | **Control reachable** — confirm / cancel / undo present and ≥ 44 dp | the only exit is the system back gesture |

## Gate order for the 25 routes

1. Shell: `home`, `clean`, `tools`, `reports`, `pro`, `settings` (bottom bar, header, Pro pill).
2. Real-data surfaces: `storage`, `battery`, `appmanager`, `installed-apps`, `ai`.
3. Service instruments: `privacy`, `duplicates`, `deepscan`, `residue`, `smartclean`.
4. Detail/report surfaces: `app-detail`, `privacy-risk`, `duplicate-group`,
   `deep-scan-summary`, `performance-report`, `residue-item`, `storage-breakdown`,
   `battery-diagnostics`, `ai-recommendations`.
5. Identity: the login gate (`ui/auth`) — captured last, because it must reuse the same
   palette and motion language as the routes behind it.

## Non-negotiable honesty rules

- A tap that only changes a label is not a scan. If the service cannot run yet, the
  screen says so and offers the real entry instead.
- Pro state is a presentation flag today. No screen may imply a purchase, a licence,
  or a subscription that does not exist.
- Measured values come from the device. Everything else is drawn as indeterminate.
