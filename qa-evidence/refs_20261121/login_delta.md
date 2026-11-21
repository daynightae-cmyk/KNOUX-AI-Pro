# Login Screen Delta — Current Device vs Reference

**Device screenshot:** `D:\Temp\opencode\auth_before_20261121.png` (720x1600 portrait)
**References:**
- `spec_batch_A.md` — Image 3 `download (3).png` (authentication / sign-in screen)
- `spec_batch_B.md` — Image 3 `download (7).png` (login screen)

Note: the two references conflict on a few details (icon side, bottom area, header chrome). Where they conflict, both are noted. Items are ordered by visual impact. "BROKEN" flags overlap/clipping/unreadable/dead-end UI.

---

## 1. Google button fill is wrong — white instead of gold gradient / glass neon  [BROKEN — primary CTA]

- **Reference (batch A):** Button 1 Google = gold/amber gradient fill (`#f59e0b` → `#d97706`) with orange rim glow, white text, Google `G` icon on the right.
- **Reference (batch B):** Google button = translucent glass pill, purple neon border glow (`#8E2DE2`), inner cyan highlight, left Google `G` (multicolor), label `المتابعة باستخدام جوجل / Continue with Google`, right chevron `>`.
- **Device currently shows:** A SOLID WHITE filled pill button with text `تسجيل الدخول باستخدام Google` and the Google `G` icon on the right. No gradient, no glow, no English sub-label, no chevron.
- **Impact:** This is the primary sign-in action. A white button on a dark panel has the wrong visual weight and looks like a different component entirely — it is the most visibly wrong element on screen.
- **Exact fix:** Make the Google button the primary styled button. Per batch A: gold/amber gradient fill with orange rim glow, white text, right-side `G`. OR per batch B: translucent glass pill with purple neon border, left-side `G`, right-side chevron `>`, plus English sub-label "Continue with Google". Pick one spec and apply consistently (batch B is the more complete "login screen" spec).

---

## 2. Button icons on wrong side and missing right-chevrons  [batch B conflict]

- **Reference (batch B):** All action buttons have the icon on the LEFT (Google `G`, passkey key in purple square, envelope) and a right-side chevron `>`.
- **Reference (batch A):** Icons on the right side of each button (no chevrons mentioned).
- **Device currently shows:** Google `G`, passkey key, and envelope icons are all on the RIGHT side of their buttons, and there are NO chevrons `>` anywhere.
- **Impact:** Inconsistent with batch B (the login-screen spec). Buttons look like plain labels with a trailing icon rather than actionable rows with leading icon + trailing chevron.
- **Exact fix:** Move each leading icon (Google `G`, passkey key, envelope) to the LEFT of the button text and add a right-side chevron `>` to every action button (Google, Passkey, Email/OTP).

---

## 3. Missing English sub-labels / secondary button text

- **Reference (batch B):** Each button has an Arabic primary label AND an English sub-label underneath (e.g. `Continue with Google`, `Continue with Passkeys`, `Login with Biometrics`).
- **Reference (batch A):** Button labels = Arabic primary line (white, weight ~600) + English secondary line (slightly smaller, white, weight ~500).
- **Device currently shows:** Only Arabic text is visible on the buttons (e.g. `تسجيل الدخول باستخدام Google`, `تسجيل الدخول باستخدام Passkeys`). No English secondary/sub-label line anywhere.
- **Impact:** Breaks the bilingual label hierarchy defined in both references; buttons read as single-line Arabic only.
- **Exact fix:** Add a smaller English sub-label line under each button's Arabic label: "Continue with Google", "Continue with Passkeys", "Email / OTP Login", "Login with Biometrics".

---

## 4. No bottom account-creation path (batch A) AND no feature pillars (batch B)  [BROKEN — dead-end screen]

- **Reference (batch A):** Bottom link at ~91–94%: `ليس لديك حساب؟ انشا حساب جديد` ("Don't have an account? Create new account") with `انشا حساب جديد` underlined and a leading chevron `>` (RTL).
- **Reference (batch B):** Bottom row of three feature pillars (~86–94%): shield `آمن ومحمي / Secure & Protected`, lightning `سريع وفعال / Fast & Efficient`, document stack `بياناتك خاصة / Your Data is Private`.
- **Device currently shows:** The glass panel ends right after the Biometric label (`تسجيل الدخول باستخدام البصمة`). There is NO bottom link and NO feature pillars — the screen is a dead end with no way to create an account and no trust/feature callouts.
- **Impact:** Functionally broken: a login screen with no "create account" escape hatch and no bottom content leaves the panel feeling cut off.
- **Exact fix:** Add either the batch A bottom "create new account" link (underlined, leading chevron) OR the batch B three feature pillars at the bottom of the panel. Choose one spec; at minimum add the account-creation link.

---

## 5. Missing top-right language pills (AR | EN + globe) and top-left info "i" button  [batch B]

- **Reference (batch B):** Header chrome includes top-left `i` info button (dark circle, white `i`), top-right globe icon + `AR` pill (gold text) + `|` separator + `EN` pill (white text), small gold crown above the header wordmark.
- **Device currently shows:** None of this header chrome exists. The top of the screen below the status bar goes straight to the `knoux ai` + `Pro` logo with no crown above it, no info button, no language pills.
- **Impact:** Missing region/language switching and info affordances defined in the login-screen spec; header looks incomplete.
- **Exact fix:** Add top-left `i` info button, and top-right globe icon + `AR` (gold) / `|` / `EN` (white) pills. Add a small gold crown floating above the header wordmark.

---

## 6. Crown is in the wrong place

- **Reference (batch B):** Small gold crown centered ABOVE the header `knoux ai` wordmark (~3–6% height).
- **Reference (batch A):** Small gold crown between the K emblem and the wordmark (~37–39% height).
- **Device currently shows:** A small gold crown sits BELOW the large gazelle illustration, just above the glass panel — not above the header wordmark and not between emblem and wordmark.
- **Impact:** Crown placement contradicts both references; it reads as a stray decoration rather than part of the header/emblem hierarchy.
- **Exact fix:** Move the crown to above the header `knoux ai` wordmark (per batch B) — or remove it from below the gazelle and place it between the emblem and wordmark (per batch A). Remove the crown currently below the gazelle.

---

## 7. Missing welcome pill `مرحبا بک في KNOUX AI Pro`  [batch A]

- **Reference (batch A):** A wide centered welcome pill at ~44–47%: `مرحبا بک في KNOUX AI Pro` (Arabic + inline wordmark), translucent dark fill with gold glow border at top edge.
- **Device currently shows:** No welcome pill. The screen goes from the Arabic subtitle straight into the glass panel heading `تسجيل الدخول إلى`.
- **Impact:** The welcome/hierarchy step defined in batch A is absent; the panel heading does double duty.
- **Exact fix:** Insert a wide centered welcome pill `مرحبا بک في KNOUX AI Pro` between the subheading and the glass container (translucent dark fill, gold top-edge glow border).

---

## 8. Arabic heading text is wrong / mixed

- **Reference (batch B):** Arabic heading `تسجيل الدخول` (Login) then English sub-heading `Login to KNOUX AI Pro`, with a small golden divider line below.
- **Reference (batch A):** Welcome pill `مرحبا بک في KNOUX AI Pro` then Arabic subheading `سجل الدخول للتحول إلى رحلة لا تُضاهى من الصلاحيات المطلقة`.
- **Device currently shows:** Heading reads `تسجيل الدخول إلى` (Login to) with the `KNOUX AI Pro` wordmark on the next line inside side brackets — neither reference's heading exactly; the `إلى` (to) is attached to the Arabic heading and there's no English `Login to KNOUX AI Pro` line.
- **Impact:** Heading copy doesn't match either spec; mixes Arabic "Login to" with a separate wordmark line.
- **Exact fix:** Set Arabic heading to `تسجيل الدخول` and add English sub-heading `Login to KNOUX AI Pro` beneath it, with a small golden divider line below (per batch B).

---

## 9. Missing English tagline under Arabic subtitle  [batch B]

- **Reference (batch B):** English tagline `Smart Management & AI Cleaning` (~17–19% height, light gray/white `#E0E0E0`) below the Arabic subtitle.
- **Device currently shows:** Only the Arabic subtitle appears below the logo; no English tagline.
- **Impact:** Missing secondary English descriptor from the login-screen spec.
- **Exact fix:** Add English tagline `Smart Management & AI Cleaning` below the Arabic subtitle, smaller, light gray/white.

---

## 10. Status bar values differ (cosmetic)

- **Reference (batch B):** Status bar `12:51` | Wi-Fi | signal | `93%` battery with lightning bolt (charging).
- **Device currently shows:** Status bar `99%` (cut off at left edge) | Wi-Fi | signal | `1:08` time top-right; no charging bolt visible.
- **Impact:** Low — runtime status bar values, not a UI defect. Noting for completeness only.
- **Exact fix:** None required (device status bar). No action.

---

## Summary (top priority first)

1. Google button: white fill → gold gradient (batch A) or glass neon (batch B). [BROKEN — primary CTA]
2. Button icons: right side → left side; add missing right chevrons `>`. [batch B]
3. Missing English sub-labels on all buttons.
4. No bottom account-creation link (batch A) / no feature pillars (batch B) → dead-end screen. [BROKEN]
5. Missing top-left `i` info button and top-right `AR | EN` + globe pills; missing crown above header.
6. Crown in wrong place (below gazelle instead of above header / between emblem and wordmark).
7. Missing welcome pill `مرحبا بک في KNOUX AI Pro`. [batch A]
8. Arabic heading `تسجيل الدخول إلى` wrong; should be `تسجيل الدخول` + English `Login to KNOUX AI Pro`. [batch B]
9. Missing English tagline `Smart Management & AI Cleaning`. [batch B]

Spec conflict note: batch A puts button icons on the RIGHT and has a welcome pill + bottom account link; batch B puts icons on the LEFT + right chevrons, English sub-labels, header chrome (crown, `i`, `AR|EN`), and bottom feature pillars. The current device screen mixes elements but matches neither fully. Recommend adopting batch B (download (7).png) as the canonical login-screen spec since it is the most complete, then reconcile batch A's gold-gradient primary Google button if the brand wants Google to be the emphasized primary action.
