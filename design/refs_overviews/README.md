# KNOUX AI — Overview Reference Inventory

- Source found in the user's collected assets: `design/original_hi_res/`
- 11 PNG references, all `941 × 1672`
- These cover **Home + all 10 interior tool overviews**
- No detail-screen references (the requested 11–20 set) exist in the collected assets

The high-resolution set is the newest/authoritative overview set. `design/refs20/01–07` are earlier variants of six of these screens; the splash reference `design/refs20/01_Splash_Logo_Smile.png` remains the only splash reference.

## Mapping

| Screen | High-res file | Earlier alias |
|---|---|---|
| Home / AI Smart Clean | `file_00000000a56c8243bae7a2ee9ea9ce46.png` | `refs20/02_Home_Dashboard.png`, `reference/01_AI_Smart_Clean_Home.png` |
| Temporary Files Cleanup | `file_0000000067e4820abd858040a8fe7f92.png` | — (previously missing) |
| Duplicate Files Finder | `file_000000009b9481f486d30a054678c35c.png` | `refs20/04_Duplicate_Files_Finder.png`, `original_jpeg/03_Duplicate_Files_Finder.jpg` |
| Storage Analyzer | `file_000000000d6481f4878c25f891db3c18.png` | `original_jpeg/08_Storage_Analyzer.jpg`, `reference/08_Storage_Analyzer.png` |
| App Residue Cleanup | `file_000000007df481f48bf5952a39394596.png` | `refs20/07_App_Residue_Cleanup.png`, `original_jpeg/06_App_Residue_Cleanup.jpg` |
| Privacy Sweep | `file_00000000a2208210a5048f290a47b5ad.png` | `refs20/03_Privacy_Sweep.png`, `original_jpeg/02_Privacy_Sweep.jpg` |
| AI Optimization | `file_0000000024b882439f77718e6d32e106.png` | `original_jpeg/10_AI_Optimization.jpg` |
| Deep Scan | `file_000000000e24820aa66c80b3a113c5f9.png` | `refs20/05_Deep_Scan.png`, `original_jpeg/04_Deep_Scan.jpg` |
| Battery Saver | `file_0000000008c88243ad9bb32594ec2550.png` | `original_jpeg/09_Battery_Saver.jpg` |
| App Manager | `file_000000003d3481f49a1867d4d95ef508.png` | `original_jpeg/07_App_Manager.jpg` |
| Reports & Insights | `file_000000006aa48210bbc473eba2635170.png` | `refs20/06_Reports_Insights.png`, `original_jpeg/05_Reports_Insights.jpg` |

## Verification

Each high-res overview was matched against the earlier JPEG/PNG aliases by resized RGB RMS difference. The nine known aliases match at RMS ≈ **1.9–2.1**; unrelated screens are ≈ 29–33, so the mapping is unambiguous.

`Temporary Files Cleanup` has **no** earlier alias (its best match is ≈ 29, i.e. not a match). It is an additional reference that was previously missing.

## Gap

- Detail screens 11–20: **no reference images available** in `design/original_hi_res`, `design/reference`, `design/original_jpeg`, or `design/refs20`.
- Do not claim pixel fidelity for detail screens without references. Their layout remains MASTER_PROMPT-derived.
- Runtime captures of the current installed APK are from the pre-LTR build; build14 still needs installation.
