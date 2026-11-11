# Visual comparisons

Runtime screenshots are not yet available (no adb/screencap). This folder
therefore contains the verified reference PNGs (01–07) for the next capture
pass, not a claimed pixel comparison.

## Available references

| Ref | File | Integrity |
|---|---|---|
| 01 | references/01_Splash_Logo_Smile.png | matches manifest SHA-256 |
| 02 | references/02_Home_Dashboard.png | matches manifest SHA-256 |
| 03 | references/03_Privacy_Sweep.png | matches manifest SHA-256 |
| 04 | references/04_Duplicate_Files_Finder.png | matches manifest SHA-256 |
| 05 | references/05_Deep_Scan.png | matches manifest SHA-256 |
| 06 | references/06_Reports_Insights.png | matches manifest SHA-256 |
| 07 | references/07_App_Residue_Cleanup.png | matches manifest SHA-256 |

## What the implementation targets (source-level)

- 01: authentic K raster, three-band stroke reveal with magenta/violet/cyan
  leading edges, white-to-pink wordmark write-on, shallow smile drawn
  left→right with a star at the final tip, no device frame.
- 02: hero orb + K symbol, device summary donut, six service cards, bottom nav.
- 03–07: hero + italic gradient title + stat strip + review rows + sticky
  two-button action bar, matching the existing shared design system.

## Blocked

- 08: only an older kit copy is present; its SHA-256 differs from the new
  manifest, so it will not be used for comparison.
- 09–20: files did not arrive with the message (only 01–07 and the docs did).
  Re-attach the missing PNGs or the original ZIP to unblock.
