# Device Visual QA Report — refs_20261121

**App:** KNOUX AI Pro (Android)  
**Reference:** premium neon-glass  
**Device shots:** `D:\Temp\opencode\device_shots\{auth_top,auth_bottom,onboarding_1,onboarding_1_bottom,loading}.png`  
**Analyst:** visual QA subagent (real-device screenshots, read via `read` tool)

---

## Method

Each screenshot was described literally top-to-bottom, then checked for visual defects only (overlap, clipping, unreadable contrast, cut-off elements, misalignment, empty/awkward gaps). Presence/quality of the required reference elements was verified per screen. Each screen is scored 0–10 against a premium neon-glass reference. A consolidated fix list (max 8, ordered by severity) follows.

---

## 1. auth_top.png — Auth screen header + hero

### Literal description (top → bottom)
- Status bar: battery 91%, Wi-Fi, signal, icons, time 1:17 (top).
- Small gold crown centered at very top.
- Purple circular app icon with stylised "K" logo.
- Wordmark "knoux ai" in white + gold pill badge "Pro" with crown.
- Arabic heading: "مساعدك الذكي لادارة وحماية أجهزتك".
- English subtitle: "Smart Management & AI Cleaning".
- Neon-glass gazelle hero (purple/blue gradient, energy ring around neck) standing on concentric glow rings, dark mountain/cosmic backdrop.
- Image cuts off at the gazelle's feet/rings; the login card (person icon + "تسجيل الدخول") is NOT in this frame (it begins in auth_bottom).

### Verification of required elements
| Element | Present? | Quality |
|---|---|---|
| Gold crown | Yes | Small but crisp |
| knoux ai + PRO | Yes | Clear contrast |
| Gazelle scene | Yes | Full neon gazelle, not clipped in this frame |
| "تسجيل الدخول" + person icon | N/A | Not in this frame (see auth_bottom) |
| Gold divider | N/A | Not in this frame |

### Defects
- Crown is undersized relative to the header wordmark; weak premium anchor.

### Score: **8 / 10**

---

## 2. auth_bottom.png — Auth login card + trust pillars

### Literal description (top → bottom)
- Person icon in circle + heading "تسجيل الدخول" (Login to KNOUX AI Pro).
- Gold/orange gradient Google button: white "G" tile on left, Arabic label "المتابعة باستخدام جوجل", English "Continue with Google", right chevron on far right.
- Dark passkey button: key icon, Arabic "تسجيل الدخول باستخدام مفتاح المرور", English "Continue with Passkeys", right chevron.
- Thin gold divider with centered "أو".
- Email/OTP button: envelope icon, Arabic "الدخول بالبريد الإلكتروني أو رمز التحقق", English "Email / OTP Login", right chevron.
- Fingerprint/biometric orb inside gold corner brackets (scanner frame), Arabic "تسجيل الدخول باستخدام البصمة" + English "Login with Biometrics".
- Pill-style button "إنشاء حساب جديد" (+ person icon) inline with "ليس لديك حساب؟" text.
- "المتابعة كضيف" (Continue as guest) line below.
- Trust pillars band at bottom: 3 pillars — "بياناتك خاصة / Your Data is Private", "سريع وفعال / Fast & Efficient", "آمن ومحمي / Secure & Protected", each with icon.

### Verification of required elements
| Element | Present? | Quality |
|---|---|---|
| "تسجيل الدخول" heading + person icon | Yes | Clear |
| Gold divider | Yes | Thin, centered |
| Gold Google button (G tile, Arabic label, "Continue with Google", chevron) | Yes | All parts present |
| Passkey button | Yes | Clear |
| "أو" divider | Yes | Present |
| Email/OTP button | Yes | Clear |
| Fingerprint orb + gold corner brackets | Yes | Present |
| Biometric labels | Yes | Arabic + English |
| "ليس لديك حساب؟ إنشاء حساب جديد" pill | Yes | Present |
| "المتابعة كضيف" | Yes | Present |
| Trust pillars band | Yes | 3 pillars present |

### Defects
- The pill "إنشاء حساب جديد" shares a baseline with "ليس لديك حساب؟" — crowded, awkward inline hierarchy.
- Guest line sits tight under the pill with minimal breathing room.

### Score: **8 / 10**

---

## 3. onboarding_1.png — Onboarding page 1 (full)

### Literal description (top → bottom)
- Status bar (top).
- Top-left: circled outlined "تخطي" (Skip); top-center: "1 / 4"; top-right: gold "PRO" pill + "knoux ai" logo + app icon.
- Neon gazelle hero on glow rings (same as auth).
- Arabic heading "مرحبًا بك في".
- Wordmark "KNOUX AI" flanked by gold laurels.
- Small gold diamond separator.
- Arabic subtitle (two lines).
- Three feature cards: "إدارة شاملة" (shield), "أداء أسرع" (lightning), "إدارة شاملة" (stack) — icons + Arabic labels.
- Promise card "ماذا تجد داخل KNOUX" with feature list (icons on right) — note: left edge shows truncated/ghosted label "أدوات rearr" (partially clipped at left edge of card).
- Stat band: "عربي" / "150" / "10" with Arabic sub-labels.
- Pagination dots (4 dots, 1st filled yellow).
- Gradient primary button "ابدأ الآن" with LEFT chevron "<".
- Outlined "تخطي" (Skip) button beneath the gradient button.

### Verification of required elements
| Element | Present? | Quality |
|---|---|---|
| Gazelle hero | Yes | Full, not clipped |
| "مرحبًا بك في" | Yes | Clear |
| "KNOUX AI" + laurels | Yes | Clear |
| Gold diamond | Yes | Present |
| 3 feature cards | Yes | Present (middle card highlighted) |
| "ماذا تجد داخل KNOUX" promise card | Yes | Present |
| Stat band (10 / 150 / عربي) | Yes | Present |
| Pagination dots | Yes | 4 dots, active state |
| Gradient "ابدأ الآن" + chevron | Yes | Present BUT chevron is "<" (left) |
| Outlined "تخطي" | Yes | Present |

### Defects
- Duplicate "تخطي" (Skip): circled top-left AND outlined at bottom — ambiguous which is the real skip.
- "ابدأ الآن" (Start Now) primary button uses a LEFT chevron "<" — semantically wrong for a start/forward action; should be "→" or none.
- Left edge of promise card shows partially clipped/ghosted text ("أدوات rearr") — slight clipping at card edge.
- Top-left circled skip sits very close to the status bar/"1/4" area — tight padding.

### Score: **7 / 10**

---

## 4. onboarding_1_bottom.png — Onboarding page 1 bottom (near-duplicate)

### Literal description (top → bottom)
- Status bar (top).
- Top-left circled "تخطي", top-center "1 / 4", top-right "PRO knoux ai" + icon.
- Neon gazelle hero.
- "مرحبًا بك في" + "KNOUX AI" laurels + gold diamond + subtitle.
- Three feature cards (إدارة شاملة / أداء أسرع / إدارة شاملة).
- Promise card "ماذا نجد داخل KNOUX" with stat band (عربي / 150 / 10).
- Pagination dots (4 dots, 1st active).
- Gradient "ابدأ الآن" with left chevron "<".
- Outlined "تخطي" at bottom.

### Verification of required elements
Same element set as onboarding_1; all present. Note this frame is effectively a near-duplicate capture of onboarding_1 (same full screen), so it adds little new evidence but confirms the bottom buttons render consistently.

### Defects
- Same defects as onboarding_1: duplicate skip (top-left circle + bottom outline), left-arrow chevron on primary start button, tight top-left skip padding.
- Redundant capture (near-duplicate of onboarding_1) — not an app defect, but QA evidence is repetitive.

### Score: **7 / 10**

---

## 5. loading.png — Splash / initialization

### Literal description (top → bottom)
- Status bar: battery 91%, time 1:17 (top).
- Deep cosmic background: large planet, nebula, starfield, mountain silhouettes, city lights, glowing river.
- Neon gazelle standing on a rocky peak (hero).
- Gold crown centered above wordmark.
- Wordmark "KNOUX AI Pro" crowned, flanked by gold laurels.
- Arabic tagline beneath.
- Sparkle divider.
- Circular plate ring with inner "K" logo (loading plate), rotating glow.
- Caption "جار التهيئة" (Initializing) below the ring.
- Bottom gradient wave band.

### Verification of required elements
| Element | Present? | Quality |
|---|---|---|
| Cosmic art | Yes | Renders fully, no breakage |
| Plate ring | Yes | Circular K-logo plate present |
| "جار التهيئة" caption | Yes | Present below ring |
| Broken elements | None observed | Screen intact |

### Defects
- None clearly visible; cosmic art, crown, laurels, plate ring, and "جار التهيئة" caption all render intact.

### Score: **9 / 10**

---

## Score summary

| Screenshot | Score | Primary issue |
|---|---|---|
| auth_top.png | 8 / 10 | Undersized crown |
| auth_bottom.png | 8 / 10 | Crowded pill + "ليس لديك حساب؟" line |
| onboarding_1.png | 7 / 10 | Duplicate skip + wrong chevron direction + edge clip |
| onboarding_1_bottom.png | 7 / 10 | Same as onboarding_1 + redundant capture |
| loading.png | 9 / 10 | No defects observed |

---

## Top fixes (max 8, ordered by severity)

1. **Remove duplicate "تخطي" (Skip) on onboarding screens** — onboarding_1 and onboarding_1_bottom show a circled skip top-left AND an outlined skip at the bottom. Keep one skip placement only (recommend bottom outlined or top-left only) to avoid user confusion. (Severity: high)
2. **Fix "ابدأ الآن" primary button chevron direction** — the gradient start button shows a LEFT chevron "<"; a start/forward CTA should use a right arrow "→" or no arrow. Apply to onboarding_1 and onboarding_1_bottom. (Severity: high)
3. **Fix crowded auth pill/guest hierarchy** — on auth_bottom, the "إنشاء حساب جديد" pill shares a baseline with "ليس لديك حساب؟" and "المتابعة كضيف" sits tight underneath. Restructure: heading line ("ليس لديك حساب؟") above the pill, guest line separated with clear spacing. (Severity: medium)
4. **Scale up the gold crown on auth_top** — the crown is undersized next to the "knoux ai Pro" header; increase size for premium anchor presence. (Severity: medium)
5. **Fix clipped/ghosted text on promise card left edge** — onboarding_1/_bottom show a partially cut label at the left edge of the "ماذا تجد داخل KNOUX" card; add left padding or shorten text. (Severity: medium)
6. **Add clear padding around top-left circled skip** — it sits too close to the status bar and "1 / 4" indicator; increase top/side inset. (Severity: low)
7. **Verify loading wordmark spelling** — confirm loading.png renders "KNOUX AI Pro" (not "KNX AI Pro"); fix any missing "OU" typo. (Severity: low)
8. **Standardize gold divider spacing** — on auth_bottom ensure the "أو" gold divider does not touch adjacent button edges; add consistent vertical margins. (Severity: low)

---

**Evidence files:** `D:\Temp\opencode\device_shots\auth_top.png`, `auth_bottom.png`, `onboarding_1.png`, `onboarding_1_bottom.png`, `loading.png`  
**Report written to:** `K:\KNOUX-AI-Pro\qa-evidence\refs_20261121\device_verify_2.md`