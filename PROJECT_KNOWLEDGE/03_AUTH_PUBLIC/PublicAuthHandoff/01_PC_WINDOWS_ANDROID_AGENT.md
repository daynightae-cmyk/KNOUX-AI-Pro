# 01 — EXECUTE AUTH ON WINDOWS-DEVELOPED ANDROID APP (PC WORKER)

# KNOUX AI — Two independently developed Android apps, one verified Auth backend

**Verified 2026-11-14.** This is a real credentials/configuration handoff, not proof that any sign-in has succeeded. Public client credentials are in `00_KNOUX_AI_VERIFIED_CLIENT_CONFIG.json` and MUST be read from that file rather than guessing.

### The verified items
- Supabase: `knoux ai` / `tduofkizwkjtqcbklfah` — ACTIVE_HEALTHY.
- Supabase URL / enabled publishable key: in the JSON file.
- Google Cloud Project: `fiery-script-514604-n6`.
- Google Web primary client: `966864797292-6gqjaebu6n3il0777g3jf0q0mc22jr29.apps.googleusercontent.com` — JSON contains a web client_secret, but secret is server-only.
- Google Android debug client: `966864797292-pbd1b3hh9bpsi8mjhunlrebaug9tmo08.apps.googleusercontent.com` — Android client, no client_secret.
- Google extra Web / server client: `966864797292-tab00fjmpdodheouu77tlm7ffitkmsi1.apps.googleusercontent.com` — separate secret, never substitute for primary.
- Supabase callback: `https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback` (verified in primary Web JSON).
- Verified Windows-repo Android applicationId: `com.knoux.ai`. A historical Windows debug SHA-1 is in JSON, but re-read signingreport now; AndCode builds may have DIFFERENT signing fingerprint.

### Mandatory separation
- **PC-developed Android**: Windows workspace `D:\KNOUX-AI`, GitHub `https://github.com/daynightae-cmyk/KNOUX-AI.git`, main last independently verified `78391aa927b3629fcf856de04b10dae23fc2881c`. This is an Android app developed on Windows, not necessarily a Windows desktop executable.
- **Phone-developed Android**: current AndCode/OpenCode source workspace must be discovered. Historical `/root/workspace/KNOUX-AI` was independent and was affected by app uninstall. Historical mobile repo / `KNOUX-AI-Pro` should NOT be assumed in sync or populated without a real Git inventory.
- Both clients may share the **same Supabase backend and identity**, but never blindly merge code, Git histories, assets, signing material or app state. Preserve agents' live edits.

### Auth design and proof
- Native Android Google Credential Manager / Google Identity -> real Google ID token with correct **Web Client ID audience** -> server-verifiable Supabase Auth session. Validate JWT audience, nonce, provider config, cancellation, wrong/expired tokens, and logout. Client ID alone does not prove that server-side provider is configured.
- Native Email/OTP uses same Supabase URL and publishable key, real confirmed email and server session. No fabricated completion.
- Passkeys: Supabase passkeys currently experimental; remote Dashboard opt-in, stable RP ID + HTTPS origins, native Android Credential Manager WebAuthn, and `https://knoux.store/.well-known/assetlinks.json` matching **actual APK signing SHA-256**. NEVER swap RP ID or invent signing fingerprints. Existing signed-in confirmed user is required for passkey enrollment.
- BiometricPrompt is **local re-unlocking of an existing valid server session**, not an independent Supabase account sign-in. Use Keystore-backed session protection. Handle no sensor, cancel, lockout, revoked tokens.
- No secrets in APK, code, Git, build logs, mobile Downloads, uploaded prompts, or screenshots. A publishable key is expected to be client-visible, with real RLS ownership authorization on protected data.
- Reports must include exact workspace Git SHA, package, signing fingerprints (not private keys), build + test commands/exit codes, actual provider configs as verified, and real device sign-in evidence. Separate UNVERIFIED from FAILED and PASS.

## REAL CURRENT SOURCES, NOT GUESSES
Owner-private OAuth source folder (verified present):
`C:\Users\day night\Documents\KNOUX_AI_AUTH_PRIVATE_20261113`

Original files:
- `client_secret_966864797292-6gqjaebu6n3il0777g3jf0q0mc22jr29.apps.googleusercontent.com.json` — correct primary Web OAuth client, with server-only client_secret and correct callback.
- `client_secret_966864797292-pbd1b3hh9bpsi8mjhunlrebaug9tmo08.apps.googleusercontent.com.json` — Android OAuth client, no secret.
- `client_secret_966864797292-tab00fjmpdodheouu77tlm7ffitkmsi1.apps.googleusercontent.com.json` — extra Web client with DIFFERENT server-side secret.
- `KNOUX_AI_PUBLIC_OAUTH_HANDOFF.json` — pre-existing original public OAuth inventory.
- `README_SAFE_GOOGLE_AUTH.txt`, `02_APPLY_SUPABASE_GOOGLE.ps1`, `RUN_APPLY_GOOGLE_AUTH.cmd` — owner-local configuration/test tools.

`C:\Users\day night\Downloads\Supabase Access Tokens.txt` **exists**, but its token has NOT been validated or exposed. Never print/read into agent prompts or publish it. If an authorized scoped Management PAT is required, request it through a hidden local prompt, use it only for the authorized server configuration operation, and revoke it afterwards. OAuth client secrets belong in Supabase Auth provider server settings, NOT the Android build.

**WARNING:** `C:\Users\day night\Downloads\google-services.json` was checked: it is for `kx-private-vpn` / `com.knoux.kxprivatevpn`, NOT this app. DO NOT copy it.

## IMPLEMENT NOW
1. Enter actual Android Windows checkout, read HEAD/origin/main/status, inspect active writers, preserve unique work and approved designs.
2. Inspect `AuthScreen.kt`, `app/build.gradle.kts`, `AndroidManifest.xml` and Android sources. At the inspected SHA Windows code had working local BiometricPrompt but Google / Passkey were explicit placeholders. Do not call them working.
3. Configure verified public Supabase URL and publishable key, and correct native Google client ID audience from the JSON. Use one config abstraction, not duplicated hardcodes; no Google Web client_secret, Supabase service_role or Management PAT in app.
4. Verify the remote Supabase Google Provider and primary Web Client Secret (server-side) through permitted dashboard or owner's verified local script. The existing script defaults to VALIDATE_ONLY; the `-Apply` mode actually sends a management API PATCH, so inspect current config and exact change BEFORE applying. Do not blindly overwrite a working provider.
5. Match installed debug/release SHA-1 to the actual Google Android OAuth registration. Use signingReport and installed APK signing evidence. Handle Google ID-token exchange, Email/OTP, real session state, logout; Passkeys only when backed by verified RP setup; Biometric only with existing verified session.
6. Run real tests, build, instrumentation when device available; save evidence and safely commit/push after rechecking main and coordinating any concurrent agent.
7. If remote provider configuration still can't be inspected, continue client-side integration and report the exact external blocker separately; don't stop unrelated implementation.

**Acceptance:** user actually signs in, gets a Supabase Auth user session from the VERIFIED project, loses access after logout/session revoke, and no private credential appears in Git/APK/logcat.
