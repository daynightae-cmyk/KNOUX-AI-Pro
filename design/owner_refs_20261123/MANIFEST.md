# KNOUX AI Pro — Owner References Intake Manifest (2026-11-23)

## Source archive (verified)

- Local path: `D:\Temp\opencode\uploads\cb72d6d0-5032-4393-b6d0-e3e2846eba51\Knoux ai pro.zip`
- Archive SHA-256: `1ca30c9338cb1a0d31c2926443f656c9910a69408bc190a5e181b53ee526a05f` — **matches the owner-declared fingerprint** `1ca30c93…526a05f` ✅
- Entries: 22 visual entries, 20 unique SHA-256 (index 05 is a byte-duplicate of 02; index 15 is a byte-duplicate of 14)
- Destination: `K:\KNOUX-AI-Pro\design\owner_refs_20261123\`
- ZIP internal order used as index, starting at 0. Originals preserved in the uploads folder; nothing was overwritten.

## Duplicate check against project-owned assets

- All 22 extracted files were hashed and compared against every `*.png/jpg/jpeg/webp` under `design/`, `app/src/main/res/`, `_references/` (387 project images hashed).
- **Only index 00 matched an existing project asset**: `_references/annotated_20261117/assets/STAG_MASCOT_TRANSPARENT.png` (identical SHA-256). Per the no-duplicate-import rule, it was **not** re-copied; the canonical project copy stays in place and is referenced below.
- No other archive image exists anywhere in the project → all other unique images were imported once.

## Rows

| idx | original name in ZIP | organized name | bytes | SHA-256 | target route | decision |
|---|---|---|---|---|---|---|
| 00 | file_0000000021f0821085f36cfca1bd1703.png | (not re-imported) `_references/annotated_20261117/assets/STAG_MASCOT_TRANSPARENT.png` | 1225312 | FDDBFBC1E707CF25DB100343FF44522B035B3C83D554F5B29AC1B8953A82FC4F | brand asset (header mascot, all shell screens) | Exact Duplicate of project-owned asset — canonical copy already in repo |
| 01 | file_0000000067a0821190c7c639b234c201.png | `auth/identity_verification.png` | 2411169 | 2AB57D2D60642331EF44D4ABBDFC7A932EE1747BA90AEB6D158D99FDB6834247 | `auth` (OTP / verify identity state) | Canonical |
| 02 | file_00000000d17882119b3cb870380bdd8b.png | `auth/welcome_founder.png` | 2591694 | BB2ECD1E6A8A5F88B90E43D54B7D56ED90F60B5138E7EA61663B5B9F5E3B5D92 | `auth` (Welcome / Founder entry) | Canonical |
| 03 | file_000000005b288210aea641bc6779bdd8.jpg | `auth/biometric_success_variant_a.jpg` | 229876 | 168645E37F55300262494B30C2C5ECD1162AFCE9E836D9C70C340960A4B52D8F | `auth` biometric success state | Variant (kept as comparison; one success state only in app) |
| 04 | file_00000000103c81f48390d197bc75ff95.jpg | `auth/pro_login_methods.jpg` | 264608 | 11FDA6647C01D644B329FB92BBC5561CA3981D52078365F1F8071DE387039280 | `auth` (Google / Passkey / Biometric / OTP login methods) | Canonical |
| 05 | file_00000000cb78821099494a9ef2235306.png | — | 2591694 | BB2ECD1E6A8A5F88B90E43D54B7D56ED90F60B5138E7EA61663B5B9F5E3B5D92 | — | Exact Duplicate of idx 02 — not imported |
| 06 | file_00000000b79c821094d6904b58199e3b.jpg | `auth/biometric_success_variant_b.jpg` | 221912 | 87BB71723F592257D467E9DFFDF75B446817B09B87F37599AADEDA02C6F19BB8 | `auth` biometric success state | Variant (kept as comparison; one success state only in app) |
| 07 | file_000000002b1481fbbade463b1ae0a789.png | `home/pro_home_dashboard.png` | 2084841 | DE4584114D2D5E662689D438727638EF378EB68C8EC711DDFF877AA306B4AA99 | `home` | Canonical |
| 08 | file_00000000dec481f5aaa153a35a42198b.png | `developer/developer_hub.png` | 2019146 | B9FD336147AEA8A330FC2AEAFBC32C1F00AA152343F7E77833DEDB9EACD2661B | developer hub entry (from home/extra hubs) | Canonical |
| 09 | file_00000000096881fbb023cfa0d662524c.png | `developer/developer_studio_variant_01.png` | 2061910 | 2438E523AC2B0AD72FFF6EDC64FC1FBC762EB027DD85E86E6F7D6726706146F3 | `developer-studio` | Variant — bind to a real state (see ledger) or comparison-only |
| 10 | file_000000008b6481f588b79c388a79054d.png | `developer/developer_studio_variant_02.png` | 2009658 | 3947E16A3D9BE55A44164B781233F6C65C16E7F59E7E8504BDBF8A4142CA882C | `developer-studio` | Variant — same rule |
| 11 | file_0000000071c881f5837f02d536327d94.png | `developer/developer_studio_variant_03.png` | 2056639 | A92B362C2F25CFBD31ADD35421F1B57C532B52151861832A72729E51C9140122 | `developer-studio` | Variant — same rule |
| 12 | file_00000000df7081fbb6ff0fdbba1cf76a.png | `developer/developer_studio_variant_04.png` | 2045362 | D2D1CF328C131932B846041EAB064B35FFBEE19B4F802EB350FBCE1244A1C311 | `developer-studio` | Variant — same rule |
| 13 | file_000000000f4881f5b567f41c844ef8c6.png | `boards/ten_services_visual_storyboard.png` | 2290313 | D7BF0A9E7194D0A1D2E9C248D6B651CA6B5B0773D8B48876E06A57850D3EEE24 | layout reference only (10 services) | Reference-only — never used as a runtime screen |
| 14 | IMG_20261123_034609.jpg | `services/smart_clean.jpg` | 405741 | 470EC342AD8491BF6BF90E96F60E95529043EE83855DE65FD1B0B43CF5E050D4 | `smartclean` (+ clean tools) | Canonical |
| 15 | IMG_20261123_034610.jpg | — | 405741 | 470EC342AD8491BF6BF90E96F60E95529043EE83855DE65FD1B0B43CF5E050D4 | — | Exact Duplicate of idx 14 — not imported |
| 16 | IMG_20261123_034611.jpg | `services/ai_recommendations.jpg` | 440183 | FAD119E8C2B966B146AAE3343FA8730CEFDA0776CA4F2473C1F6D40AFB9738BB | `ai` / `ai-recommendations` | Canonical |
| 17 | IMG_20261123_034613.jpg | `services/battery_performance.jpg` | 402531 | 135C5AAC4A579A6CB8E1B9780EB09B26F8C9217B2295C8A5772C72B62B39579D | `battery` / `battery-diagnostics` | Canonical |
| 18 | IMG_20261123_034614.jpg | `services/privacy_security.jpg` | 416729 | EF01F278865AB71C6CCCB64F36C3A196B035969A74280F5267587668E10EE6C9 | `privacy` / `privacy-risk` | Canonical |
| 19 | IMG_20261123_034615.jpg | `services/app_management.jpg` | 429252 | C738B7CB779748E3A61AF1A0E7E60173BB303BD34BA07B9DC5A78B25507AF36F | `appmanager` / `installed-apps` / `app-detail` | Canonical |
| 20 | IMG_20261123_034617.jpg | `services/storage_analysis.jpg` | 422589 | 2B2818D6837679701068CD53DFB7D67BC22D9610C51E4C1EBA4325048EC2CA055 | `storage` / `storage-breakdown` | Canonical |
| 21 | IMG_20261123_034618.jpg | `services/duplicate_finder.jpg` | 419888 | 0D468D8A3ABF7554BEF7FF21E40BD7A747B0F24C9CE368088A8D8A37E4E8944A | `duplicates` / `duplicate-group` | Canonical |

## Notes / rules applied

- 03 vs 06 are two different biometric-success visuals (not byte duplicates): one becomes the single in-app success state; the other stays as comparison reference only. No duplicated success screen will be added.
- 09–12 are one Developer Studio family: the best single composition becomes the Canonical `developer-studio` reference; each remaining difference is bound to a real state (e.g., providers / models / agents / tools tabs already routed under `dev-*`) — otherwise Reference-only. No decorative duplicate sections.
- 13 is a composite board of ten services: planning/comparison only, never rendered as a screen.
- No image was placed in `app/src/main/res` by this intake; runtime use goes through approved assets only.
- Numbers visible inside these references (percentages, sizes, scores) are treated as illustrative, not as live data, per the mission file.
