# Device Verification Report — KNOUX AI Pro (Real Device)
**Date:** 2026-11-22 | **Screenshots:** auth_top, auth_bottom, onboarding_1, onboarding_1_bottom

---

## 1. auth_top.png — Login screen (upper half)

### Visible content top-to-bottom
- System status bar (battery 91%, Wi-Fi, signal, clock 11:27)
- Small gold crown centered at top
- Circular app logo (purple stylized "K")
- `knoux ai` wordmark + gold outlined `Pro` pill badge
- Arabic subtitle: `مساعد الذكاء الاصطناعي لدينا يساعدك في المهام`
- English: `Smart Management & AI Cleaning`
- Gazelle illustration (neon purple/blue, full body, standing on circular platform)
- Login card with person icon
- Heading: `تسجيل الدخول` + `Login to KNOUX AI Pro`
- Gold Google button: white G tile + Arabic label `المتابعة باستخدام جوجل` + `Continue with Google` + right chevron
- Passkey button: key icon + Arabic label `تسجيل الدخول باستخدام مفتاح المرور` + `Continue with Passkeys` + right chevron
- `أو` divider
- Email/OTP button: envelope icon + Arabic label `الدخول بالبريد الإلكتروني أو رمز التحقق` + `Email / OTP Login` + right chevron
- Fingerprint orb with corner brackets — **partially clipped at the bottom edge** (only corner brackets + top of orb visible; circle cut off by frame)

### Elements present check
| Element | Present? |
|---|---|
| Gold crown above wordmark | Yes |
| knoux ai wordmark + PRO | Yes |
| Gazelle illustration | Yes |
| تسجيل الدخول heading + person icon | Yes |
| Gold divider (الأو line) | Yes |
| Gold Google button + G tile + sub-label + chevron | Yes |
| Passkey button + Continue with Passkeys | Yes |
| أو divider | Yes |
| Email/OTP button + Email / OTP Login | Yes |
| Fingerprint orb + corner brackets + biometrics labels | Partially (clipped at bottom edge) |

### Visual bugs
- **Fingerprint orb clipped at bottom frame edge** — the corner brackets and top of the orb are visible but the circular orb is cut off by the screenshot boundary. (Note: this boundary is an artifact of splitting the screen into top/bottom captures; in auth_bottom.png the orb is fully shown.)

---

## 2. auth_bottom.png — Login screen (lower half)

### Visible content top-to-bottom
- Gazelle hooves/platform — clipped at top edge (continuation from auth_top)
- Same login card content continued
- `أو` divider + thin gold rule line
- Email/OTP button (full)
- Fingerprint biometric section: fingerprint orb inside corner brackets (fully visible here)
- Label: `تسجيل الدخول باستخدام البصمة` + `Login with Biometrics`
- Pill: `ليس لديك حساب؟ إنشاء حساب جديد` (person-plus icon)
- Link: `المتابعة كضيف` (Continue as Guest)
- Subtle text: `المتابعة كضيف` below pill area
- Three trust pillars row:
  1. Left: diamond icon + `يمان بياناتك خاصة` / `Your Data Is Private`
  2. Middle: lightning icon + `سريع وفعال` / `Fast & Efficient`
  3. Right: shield icon + `آمن ومحمي` / `Secure & Protected`

### Elements present check
| Element | Present? |
|---|---|
| Fingerprint orb + corner brackets | Yes (full) |
| تسجيل الدخول باستخدام البصمة | Yes |
| Login with Biometrics | Yes |
| ليس لديك حساب؟ إنشاء حساب جديد pill | Yes |
| المتابعة كضيف | Yes |
| Three trust pillars (آمن ومحمي / سريع وفعال / بياناتك خاصة) | Yes (all three) |

### Visual bugs
- **Trust pillar 1 Arabic text garbled / possible RTL rendering issue:** left pillar reads `يمان بياناتك خاصة` — the leading word `يمان` does not belong to `بياناتك خاصة` (Your Data Is Private). Suspect font/RTL shaping artifact or wrong string. Also uses a diamond icon for "data private" which is semantically mismatched (pillar 3 already uses shield for "Secure & Protected").
- Gazelle hooves clipped at top edge (continuation artifact of the top/bottom split).

---

## 3. onboarding_1.png — Onboarding screen 1 (full view)

### Visible content top-to-bottom
- System status bar
- Top-left: outlined circle `تخطي` (Skip)
- Top-center: `1 / 4` pagination
- Top-right: `PRO` pill + `knoux ai` wordmark + circular logo
- Gazelle hero (large, centered, full body, neon purple/blue)
- `مرحبا بك في`
- `KNOUX AI` title flanked by gold laurels (wreath)
- Gold diamond separator
- Arabic description below title
- Three feature cards:
  1. Left: layers icon + `إدارة شاملة`
  2. Middle: lightning icon + `أداء أسرع`
  3. Right: shield icon + `حماية أجهزتك`
- Promise/details card: left chip `أدوات دازل`, right title `KNOUX حالةجز دازل`, then 3 Arabic feature lines each with a colored icon (hand/tool, lock, bar-chart)
- Stat band: three pills — `عقاري` / `150` / `10` with smaller Arabic labels underneath
- Pagination dots (4 dots, 1st active yellow)
- Gradient `ابدأ الآن` button (pink→blue) with left chevron `<`
- Bottom: outlined `تخطي` button

### Elements present check
| Element | Present? |
|---|---|
| Gazelle hero | Yes |
| مرحبًا بك في | Yes |
| KNOUX AI title with laurels | Yes |
| Gold diamond | Yes |
| 3 feature cards (حماية أجهزتك / أداء أسرع / إدارة شاملة) | Yes |
| Promise card | Yes (text garbled — see bugs) |
| Stat band | Yes |
| Pagination dots | Yes |
| Gradient ابدأ الآن button + chevron | Yes |
| Outlined تخطي button | Yes |

### Visual bugs
- **Promise card bilingual text garbled / RTL-LTR mixing issue:** right side reads `KNOUX حالةجز دازل` and left chip reads `أدوات دازل`. `حالةجز دازل` is not valid Arabic and the English `KNOUX` is embedded mid-Arabic line, indicating a RTL/bilingual layout or string-encoding problem.
- **Stat band values appear to be demo/placeholder:** `150` and `10` with vague Arabic labels (`حماية قاعدة البيانات`, `أنظمة حماية`). Verify these are real, final metrics before ship.

---

## 4. onboarding_1_bottom.png — Onboarding screen 1 (bottom repeat)

Confirms the same content as screenshot 3 (gazelle, cards, promise card, stat band, pagination dots, gradient `ابدأ الآن` button, outlined `تخطي` button). No additional elements or new bugs beyond those noted in #3.

---

## Issue Ranking by Severity

| # | Severity | Issue | Exact Fix Needed (file/area) |
|---|----------|-------|------------------------------|
| 1 | High | Arabic RTL text garbled in trust pillar 1 (`يمان بياناتك خاصة`) — hurts readability and trust | Fix Arabic string/RTL shaping in login-card trust pillar row (left pillar), `auth_bottom` / trust-pillars area; consider swapping diamond icon to lock to match "Your Data Is Private" |
| 2 | High | Onboarding promise card bilingual text garbled (`KNOUX حالةجز دازل` / `أدوات دازل`) — invalid Arabic, English embedded mid-line | Fix RTL/bilingual layout + string encoding in onboarding promise card, `onboarding_1` / promise-details card |
| 3 | Medium | Fingerprint biometric orb clipped at frame boundary between `auth_top` and `auth_bottom` | Add bottom padding to login card / ensure scroll container reveals the full biometric orb, `auth` screen bottom area |
| 4 | Low | Trust pillar 1 uses a diamond icon for "Your Data Is Private" (semantically off; shield used in pillar 3) | Change left-pillar icon to a lock/shield to match label, `auth_bottom` trust-pillars row |
| 5 | Low | Onboarding stat band shows placeholder/demo values (`150`, `10`) | Replace with real metrics or remove demo labels, `onboarding_1` stat band |

---

## Summary

**What works:** All required elements render across both screens — crown, wordmark + PRO, gazelle, login heading, Google/passkey/email biometric buttons, `أو` divider, sign-up pill, guest link, all three trust pillars, onboarding gazelle hero, laurels title, feature cards, stat band, pagination dots, gradient `ابدأ الآن` button, and outlined `تخطي` skip button. Contrast is good (light text on dark), nothing is clipped at the left/right edges, and alignment is generally clean.

**What is broken:** Two Arabic rendering/localization defects (trust pillar 1 garbled text + mismatched icon; onboarding promise card garbled bilingual title), plus a fingerprint orb that gets cut at the capture boundary (needs bottom padding/reveal) and placeholder stat-band values.

**Top 5 fixes:** (1) Fix pillar-1 Arabic text/icon, (2) fix onboarding promise-card RTL text, (3) add bottom padding so biometric orb fully reveals, (4) correct pillar-1 icon to lock/shield, (5) replace demo stat-band values with real metrics.

Full report written to: `K:\KNOUX-AI-Pro\qa-evidence\refs_20261121\device_verify_1.md`