# KNOUX AI MOBILE — OPENCODE FINAL IN-PLACE IMPLEMENTATION
## Two approved onboarding screens + licensed Salawat audio + relevant KNOUX corporate identity

**Date:** 2026-11-13  
**Sole target repository:** `/root/workspace/KNOUX-AI` (the independent **PHONE** Android project).  
**Scope:** only **TWO supplied onboarding reference images**, their existing navigation and one audio cue after the existing Splash smile finishes. **Do not work on the Windows project** `D:\KNOUX-AI`.  
**Execution:** inspect the real mobile repo → implement in place → compile/test → compare actual screenshots → safely integrate → report real evidence. Do not stop at planning or ask A/B/C.

> **OWNER COMMAND:** All Login, OTP verification, fingerprint, Google Sign-In, Passkeys and existing auth screens are ALREADY IMPLEMENTED. **ABSOLUTELY NO redesign, no new login/OTP page, no new authentication mockup and no rebuild from scratch.** Preserve all existing backend integration and full functionality. Work ONLY on the two specified onboarding chapters and adding the real audio at the existing smile completion event.

---

## 0. Materials — no ZIP / no extraction

The owner sends **this ONE Markdown file as plain text** and these **TWO independent PNG files** (attach separately in the same OpenCode message if needed):

1. `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png` — immutable screenshot, **841×1870**, Arabic middle chapter.
2. `02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png` — immutable screenshot, **841×1870**, Arabic final chapter.

Do not ask for a ZIP. Do not assume you have access to the Windows `D:` partition or to `/mnt/data` of ChatGPT. Use the PNG files actually supplied to your OpenCode session. If they are not available in the current environment, first check current attachments and existing project's approved references; report image transfer as an explicit blocker instead of pretending visual comparison passed.

### Owner-supplied approved Salawat audio URL

```text
https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3
```

The owner explicitly states **the site owner is his friend and has granted permission to use this recording in the KNOUX application**. Document this as the owner's permission statement and the exact source URL in `docs/media/INTRO_SALAWAT_PROVENANCE.md` (or the existing asset provenance ledger). Do not fabricate an email/license certificate or claim that the file's bytes/duration were previously audited. Check whether additional performer/recording rights matter before public distribution if such rights are not covered by the owner's permission.

**Important verification note:** ChatGPT's environment could not retrieve this URL on 2026-11-13, so the MP3's bytes, exact sung words, duration, codec, hash and audio quality are **NOT independently confirmed**. Your connected build environment should attempt the real download, inspect the file, listen to it and ensure the audible phrase matches the owner's intended rendition. If download fails, preserve functionality and mark exact AUDIO_ASSET_DOWNLOAD_BLOCKED; ask for the MP3 itself only as a last resort, without holding up the two UI chapters.

---

## 1. PRE-FLIGHT: existing project and unique-work preservation

1. `pwd`, `git rev-parse --show-toplevel`, current HEAD/branch, true remote SHA, `git status --porcelain -uall`, worktrees, stash, reflog, active coding agents, untracked PNG/audio assets and existing onboarding routes. Do **not** copy another repo, force-push, reset, clean, delete, restore unknown files, or overwrite concurrent agent work.
2. Locate the actual KNOUX AI splash smile animation, its completion event, audio engine, DataStore/preferences and existing first-launch onboarding sequence. Identify what chapters already exist and reuse them.
3. Identify exact existing Logo/brand resources. Preserve the official corporate KNOUX logo and approved KNOUX AI gradient K + lowercase `knoux ai` wordmark. **Never invent/retrace/replace the official logo or paste a full screenshot as an unresponsive fake UI.** Compose interactive responsive layouts, using exact official decorative assets where already available.
4. Preserve existing Login, OTP, Google, Passkey, Biometrics, Pro, Home, settings, real services, auth providers and all user data. Do not substitute placeholder auth or create duplicated routes. The desktop project only supplied **identity/wording guidance**; NO desktop files or code may be accessed or merged.
5. Preserve existing project E2E Workflow, Master and execution ledger. This limited visual/audio request must not cancel, overwrite, or incorrectly mark any pre-existing service implementation as VERIFIED.

---

## 2. Corporate identity (reconciled from the computer's Founder Edition V2 — content only)

**Parent corporate brand:** `KNOUX`  
**Current product:** `KNOUX AI` — one KNOUX software product  
**Founder:** `Eng. Sadek Elgazar` / `م. صادق الجزار`  
**Position:** `Founder & Software Engineer` / `المؤسس ومهندس البرمجيات`  
**Founder designation:** `Founder of KNOUX` / `مؤسس KNOUX`  
**Official site:** `https://knoux.store/`  
**GitHub identity, only if existing app's brand section needs its already approved link:** `https://github.com/daynightae-cmyk`

Use the exact approved wording; do not invent awards, degrees, adoption figures, warranties, 24/7 support, subscriptions, claims of released services or unrelated personal data.

The computer's larger corporate prompt ALSO describes a separate `KNOUX Universe` with a 16-project portfolio and two Edge extensions. **DO NOT BUILD OR REBUILD THIS PORTFOLIO IN THIS TASK.** It is outside the two onboarding screens. If an existing corporate route is already present, preserve it. Product catalog status must remain truthful; the visual representation of ten service categories does not mean all ten engines are already operational.

### Three-chapter flow, only two chapters are being implemented now

- **Chapter I (existing):** the current Welcome/Awakening chapter and Splash K/wordmark/smile: keep exactly as already implemented.
- **Chapter II (supplied PNG 01):** Intelligence Ecosystem — implement or refine to match image.
- **Chapter III (supplied PNG 02):** Creator's Signature — implement or refine to match image.

On first launch: **existing Splash → existing Awakening (if present) → Ecosystem → Founder → existing Login/Auth → Home after genuine auth**.  
On later launches: use existing persistence; **never show an unnecessary repeat onboarding**. A replay action if already implemented must not alter auth.

---

## 3. IMAGE 01 — exact Ecosystem visual contract

**Reference:** `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png`

The image is the binding visual reference. Pixel composition, glow balance, spacing, typography, iconography, responsiveness and subtle motion must closely match. Key visual constraints:

- Deep indigo/violet near-black stage with cinematic **magenta/purple energy ribbons** upper-left and **cyan/blue ribbons** upper-right and lower edges; subtle dotted constellations; no generic white Material background.
- Top left Arabic `تخطي`, top right `رجوع →` at safe system insets; center compact authentic K + italic lowercase `knoux ai` wordmark.
- Heading exactly **`منظومة ذكية واحدة.`** and subtitle exactly **`10 خدمات ذكية، منصة واحدة متكاملة.`**
- Central floating luminous K intelligence **core** with two-tone violet/cyan ring, particles/orbits and the badge **`نواة الذكاء`**; connecting slim curved neon strokes behind all ten surrounding service cards. Each card has its own glowing colored edge/icon.
- Exactly the ten names from the screenshot, keep Arabic order and meaning:
  - `مراجعة الخصوصية`
  - `اكتشاف الملفات المكررة`
  - `مركز الجهاز الذكي`
  - `توفير البطارية`
  - `تنظيف بقايا التطبيقات`
  - `تحليل التخزين`
  - `التقارير والرؤى`
  - `إدارة التطبيقات`
  - `الفحص العميق`
  - `التحسين الذكي`
- Distinct service pictograms (shield, duplicate documents, heartbeat phone, battery/lightning, trash bin, storage disks, bars, app grid, magnifying glass, AI chip). Not a repeated stock icon.
- Three progress dots integrated with the *existing* onboarding, with **middle dot** cyan and other dots muted.
- Bottom large magenta → purple → cyan glowing gradient **`التالي`** CTA, actually navigates to the founder chapter. `تخطي` navigates to existing auth without authenticating, and `رجوع` / system back works. Every touch target comfortable (≥48dp).
- Use Compose `Canvas`/drawables or approved image assets only for complex decoration; implement all text/buttons/cards as independent responsive elements; dynamic scaling for small screens, RTL support, reduced-motion and no blocked gestures or CTA under navbar.

**Service truth:** do not advertise a category as tested and fully functional just because the visual onboarding shows its card. Do not start implementing its backend as part of this focused visual mission.

---

## 4. IMAGE 02 — exact Founder / KNOUX corporate identity visual contract

**Reference:** `02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png`

- Same identical dark-violet cinematic backgrounds, bright magenta/cyan flowing energy ribbons, orbit particles, compact top KNOUX AI wordmark, safe inset `تخطي` and `رجوع →` controls.
- Main large iridescent **pearl-white orbital sphere** with the original distinctive approved purple corporate mark shown inside (NOT a fingerprint icon, NOT a generic unlock). Render or reuse the correct approved corporate asset, not a different arbitrary corporate icon. Thin cyan/magenta orbital ring with localized highlights and a small glowing elliptical ground halo.
- Exact divider text: **`أحد منتجات KNOUX`**.
- Main display title: **`KNOUX AI`** using refined white/lilac/pink gradient typography.
- Main statement exactly: **`رؤية هندسية. تجربة صُممت من أجلك.`**
- Supporting line exactly: **`اكتشف الابتكار الذي يقف خلف KNOUX AI.`**
- Elegant two-column glass founder card with lavender/gold/champagne light strokes, left English founder details and right Arabic, preserving authentic bidi:
  - `Eng. Sadek Elgazar`
  - `Founder & Software Engineer`
  - `م. صادق الجزار`
  - `المؤسس ومهندس البرمجيات`
  - `مؤسس KNOUX`
  - `knoux.store` (link to https://knoux.store/)
- **Third** progress dot illuminated cyan.
- Bottom large rounded gradient CTA exactly **`ابدأ رحلتك مع KNOUX AI`**. It persists first-launch completion and takes the user to the EXISTING genuine sign-in flow, or the established authenticated route if already logged in. It never grants a fake login success.
- Layout responsive for 841×1870 reference ratio and real Android phones, status/navigation bar safe, 48dp touch targets, no text overlap, preserve AR/EN localization (English text should not appear reversed in RTL).
- High fidelity matters; use actual screenshots at comparable size to detect visual regression, and distinguish approved exact logo from placeholder.

---

## 5. MP3 ASSET: download once, verify, bundle OFFLINE in the APK

**Approved source from owner:**

`https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`

The audio must be a **local packaged resource**, NOT streamed from that URL each time an app user opens the Splash. It must work offline, avoid runtime dependence on this host, and not request network permission just to play the intro.

First inspect if this EXACT media already exists in mobile resources, and avoid duplicate copies. Otherwise, from the MOBILE repository root, download it carefully. Example Linux commands (adjust only to actual project structure):

```bash
set -euo pipefail
URL='https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3'
TMP="$(mktemp --suffix=.mp3)"
trap 'rm -f "$TMP"' EXIT
curl --fail --location --retry 2 --max-time 60 --connect-timeout 12 --silent --show-error \
     "$URL" --output "$TMP"
test -s "$TMP"
file "$TMP"
ffprobe -v error -show_entries format=duration,format_name,size -of default=noprint_wrappers=1 "$TMP"
sha256sum "$TMP"
# Only after verified audio (not HTML/error/redirect) and listening to expected Salawat:
mkdir -p app/src/main/res/raw
cp "$TMP" app/src/main/res/raw/knoux_salawat_intro.mp3
```

If `ffprobe` is not installed, inspect with the tools already available (`file`, MP3 parser, Android MediaExtractor or similar). Do not install a bulky SDK for this. Check real audio duration and the complete desired phrase. No arbitrary cutting of a friend's recording without owner's approval; don't silently replace this MP3 with generated music, beeps, TTS or other songs. Store `source URL`, `owner-stated permission`, `SHA-256`, `duration`, `codec` and `date` in the provenance file after actually measured. If the URL fails from the agent machine, stop the **audio-download subtask only** with an exact blocker and still complete the two chapters.

### Real Android audio behavior (hard requirements)

- Existing splash draws the brand K and the `knoux ai` wordmark; the curved neon **smile** line finishes last.
- **Only after the actual stroke reaches its end** and the final smile highlight completes, emit a semantic `SmileStrokeCompleted` event. Do not approximate it with a made-up timeout or trigger at `onCreate`/initial `LaunchedEffect`.
- Play the bundled `R.raw.knoux_salawat_intro` **once** on qualifying first-launch onboarding if user sound setting permits, without looping. Do not replay on Compose recomposition, returning from background or subsequent splash by default. A deliberately initiated onboarding replay can have a separate explicit replay option.
- The audio says **«صلّ على محمد»** as the actual supplied recording. Preserve original voice/melody. No substitute generic chime/TTS.
- Respect user's media volume, silent mode / Do Not Disturb policy, Audio Focus, other audio, phone calls, app pause, background and skip. Never autoplay when app is not in foreground. Give the user a visible mute/sound control (reuse existing settings/preferences). Don't block navigation while sound is playing.
- Release `MediaPlayer`, Audio Focus and callbacks on completion/error/skip/background/disposal; no memory or Activity leak.
- No special broad storage permission is needed to play a `res/raw` asset. Request no microphone permission.

### Kotlin reference implementation — adapt package, existing player and lifecycle to actual mobile repo

This is a **reference class for a local, already downloaded** `res/raw/knoux_salawat_intro.mp3`. Use an existing audio engine if one exists rather than duplicating it. Inspect minSdk/build setup before using the code. **Do not paste blindly without compiling.**

```kotlin
// Add under the existing Android package, e.g. onboarding/SalawatIntroPlayer.kt.
// The source file's package declaration must match the real project package.
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build

class SalawatIntroPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var focusRequest: AudioFocusRequest? = null
    private var legacyFocusGranted = false
    private var attempted = false

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        if (change <= AudioManager.AUDIOFOCUS_LOSS) stop()
    }

    /** Called only from the existing smile animation's true completion callback. */
    fun playOnceAfterSmile(enabled: Boolean, appInForeground: Boolean): Boolean {
        if (attempted || !enabled || !appInForeground) return false
        attempted = true // guard BEFORE starting; recomposition cannot start twice
        if (audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) return false
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) return false

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val focusGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener(focusListener)
                .build()
            if (audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                focusRequest = request
                true
            } else false
        } else {
            val result = audioManager.requestAudioFocus(
                focusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            legacyFocusGranted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            legacyFocusGranted
        }
        if (!focusGranted) return false

        return try {
            val player = MediaPlayer.create(
                appContext, R.raw.knoux_salawat_intro, attributes, 0
            ) ?: run { stop(); return false }
            mediaPlayer = player
            player.isLooping = false
            player.setOnCompletionListener { stop() }
            player.setOnErrorListener { _, _, _ -> stop(); true }
            player.start()
            true
        } catch (e: Exception) {
            stop() // production code should log a redacted diagnostic
            false
        }
    }

    fun stop() {
        val current = mediaPlayer
        mediaPlayer = null
        current?.let {
            runCatching { it.stop() }
            runCatching { it.release() }
        }
        val req = focusRequest
        focusRequest = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && req != null) {
            audioManager.abandonAudioFocusRequest(req)
        } else if (legacyFocusGranted) {
            audioManager.abandonAudioFocus(focusListener)
            legacyFocusGranted = false
        }
    }
}
```

**Important integration details:**

1. There is **no new splash screen**. Add a small explicit callback such as `onSmileStrokeCompleted: () -> Unit` at the existing Compose smile animation's real `finishedListener` / transition completion event. If a completion hook already exists, reuse it.
2. Instantiate one `SalawatIntroPlayer` per true splash lifecycle, not per recomposition. Guard with existing `rememberSaveable`/ViewModel one-shot state + optionally persisted onboarding audio preferences. Wire `onSmileStrokeCompleted` to `playOnceAfterSmile(...)`; call `stop()` from the existing lifecycle `ON_STOP`, `onDispose`, navigation/Skip. Use the real current foreground lifecycle, not a permanent `true`.
3. Some players use asynchronous prepare; either preload safely without starting, or keep this short local resource and optimize only if measured UI jank. Never block the UI on downloading audio.
4. The example uses `AudioFocusRequest` on API 26+, and the legacy audio focus path before API 26. If actual minSdk or audio architecture differs, adapt while preserving semantics. Do not use ringtone/notification priority to bypass silent mode.
5. If another brand chime/SFX currently fires after the smile, avoid collision: maintain an elegant one-shot transition and apply the new approved Salawat cue as the primary event, with the original chime suppressed or gently faded only if it would conflict; **no unapproved changes to smile art/animation**.

**Audio-finish gate:** run real device playback and capture proof of phrase, timing, one-shot semantics, source file hash and focus handling. Audio resource existence is NOT proof it was played at the right time.

---

## 6. Acceptance & checks

**Screens:**
- Screenshot compare against BOTH original user images, not against desktop UI; exact Arabic words, placement, energy trails, 10 colored cards, founder plaque/orb, bottom CTA and progress dots. Do not claim perfect pixel match without side-by-side measured runtime screenshot evidence.
- Interaction: first launch vs return, Next → Founder → existing Login/Auth, back/skip, no duplicate onboarding, persisted completion, Arabic RTL, English LTR, device insets, narrow phones, screen rotation/recreation, reduced motion, 48dp hit targets.

**Audio:**
- Source audio is playable, actually has expected spoken/sung Salawat, does not contain an HTML response; verify file hash/format/length. Audible exactly once only after animation's smile completion, not at logo reveal, and no replay on recomposition/foreground resume.
- Mute setting, device silent and volume zero produce silence, focus failure does not crash, playback stops on background/skip; no unauthorised network playback or release of multiple competing players.

**Engineering and preservation:**
- No Login, OTP, Google, Passkey, biometric or Pro code changes except an unavoidable minimal navigation integration already covered by owner-approved existing architecture. No feature-service code initiated.
- Tests and real Android runtime evidence, exact build commands/exit, screenshot comparisons, test evidence, actual source SHA. No `PASS` from pretty mockup or build alone.
- Preserve all existing unique work, commit only task-owned verified changes to the same main branch or actual approved Git process; push safely after remote comparison, no force-push, no unrelated project merges or deletion.

## 7. Required delivery from OpenCode (not a plan)

Report: **REALITY → RISKS → UNIQUE WORK → GAPS → CHANGED FILES → VISUAL BEFORE/AFTER → AUDIO ACTUAL SOURCE+SHA+DURATION+CALLBACK → TESTS+EXIT → DEVICE RUNTIME → GIT LOCAL/REMOTE SHA → ONLY REAL REMAINING BLOCKERS**.

**BEGIN EXECUTION NOW.** Work only in `/root/workspace/KNOUX-AI`. Do not request a ZIP or ask to recreate the app. Implement the two reference chapters and the provided licensed Salawat audio following the actual smile completion; preserve every approved authentication screen and all existing services.
