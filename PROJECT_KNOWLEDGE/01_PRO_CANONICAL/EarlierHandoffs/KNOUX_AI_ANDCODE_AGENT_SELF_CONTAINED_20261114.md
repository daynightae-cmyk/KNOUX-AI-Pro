# 02 — EXECUTE AUTH ON ANDCODE / PHONE-DEVELOPED ANDROID APP (PHONE WORKER)

# KNOUX AI — Two independently developed Android apps, one verified Auth backend

**Verified 2026-11-14.** This is a real credentials/configuration handoff, not proof that any sign-in has succeeded. Public client credentials are EMBEDDED IN THIS FILE in the section `VERIFIED PUBLIC CLIENT CONFIG`. There is no other required attachment.

### The verified items
- Supabase: `knoux ai` / `tduofkizwkjtqcbklfah` — ACTIVE_HEALTHY.
- Supabase URL / enabled publishable key: in the embedded JSON section below.
- Google Cloud Project: `fiery-script-514604-n6`.
- Google Web primary client: `966864797292-6gqjaebu6n3il0777g3jf0q0mc22jr29.apps.googleusercontent.com` — JSON contains a web client_secret, but secret is server-only.
- Google Android debug client: `966864797292-pbd1b3hh9bpsi8mjhunlrebaug9tmo08.apps.googleusercontent.com` — Android client, no client_secret.
- Google extra Web / server client: `966864797292-tab00fjmpdodheouu77tlm7ffitkmsi1.apps.googleusercontent.com` — separate secret, never substitute for primary.
- Supabase callback: `https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback` (verified in primary Web JSON).
- Verified Windows-repo Android applicationId: `com.knoux.ai`. A historical Windows debug SHA-1 is in the embedded JSON, but re-read signingreport now; AndCode builds may have DIFFERENT signing fingerprint.


## VERIFIED PUBLIC CLIENT CONFIG — EMBEDDED (NO THIRD FILE)

This is the original public-client configuration supplied by the owner. It is included verbatim as data below. The provider-enabled and end-to-end login fields marked `NOT_VERIFIED` remain unverified; do not promote them to PASS. The publishable key is intentionally client-visible; server secrets and refresh tokens must never be added.

```json
{
  "product": "KNOUX AI",
  "checked_at": "2026-11-14",
  "auth_backend": "Supabase Auth",
  "supabase_project_name": "knoux ai",
  "supabase_project_ref": "tduofkizwkjtqcbklfah",
  "supabase_url": "https://tduofkizwkjtqcbklfah.supabase.co",
  "supabase_publishable_key": "sb_publishable_19JCfWlBOdUng5LHAoMfRg_hlFl7YnC",
  "supabase_google_callback": "https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback",
  "google_cloud_project_id": "fiery-script-514604-n6",
  "google_web_primary_client_id": "966864797292-6gqjaebu6n3il0777g3jf0q0mc22jr29.apps.googleusercontent.com",
  "google_android_debug_client_id": "966864797292-pbd1b3hh9bpsi8mjhunlrebaug9tmo08.apps.googleusercontent.com",
  "google_web_extra_server_client_id": "966864797292-tab00fjmpdodheouu77tlm7ffitkmsi1.apps.googleusercontent.com",
  "verified_pc_android_package_id": "com.knoux.ai",
  "pc_historical_debug_signing_sha1": "C0:58:EA:53:09:9B:1D:7F:C5:08:F9:39:83:91:F9:D5:B9:22:24:2C",
  "supabase_project_status_at_inspection": "ACTIVE_HEALTHY",
  "supabase_publishable_key_status_at_inspection": "enabled",
  "oauth_web_primary_callback_file_verified": true,
  "google_provider_remote_live_enabled": "NOT_VERIFIED",
  "google_android_debug_sha1_registered": "NOT_VERIFIED",
  "real_google_sign_in_e2e": "NOT_VERIFIED",
  "passkey_rp_origin_assetlinks": "NOT_VERIFIED",
  "android_release_signing_fingerprints": "NOT_VERIFIED",
  "origin": "owner original Google OAuth JSON files on Windows, connected Supabase project",
  "contains_private_secrets": false,
  "warnings": [
    "Google client IDs and Supabase publishable key are public client configuration only.",
    "Never put Google Web client_secret, Supabase management PAT, service-role/secret API keys, refresh tokens or private signing key in Android/APK/Git/agent chat.",
    "Windows Downloads/google-services.json belongs to KX Private VPN and MUST NOT be used for KNOUX AI."
  ]
}
```

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

## START HERE — NO WINDOWS OR ADMIN KEYS NEEDED
Read the embedded JSON in this same file. It is the owner-provided **client-side** configuration; there is no additional file to fetch. Configure the same verified Supabase project, enabled publishable key, and actual Web Client ID needed for native Google ID-token audience. Preserve ALL original work.

**DO NOT** attempt to load Google Client Secrets / Management PAT / service_role into Android, mobile Downloads, repo, or AndCode chat. Those are server-only and already held privately on Windows/Supabase. Do not demand these secrets before working on the UI, Android auth flow, tests, or client config.

## EXECUTION
1. Identify actual `pwd`, Git remote, branch, HEAD, dirty/untracked files, active agent edits, package and signing key of THIS mobile checkout; don't mistake the independent Windows Git history for a fully synchronized clone.
2. Check native auth implementations (Google, Email/OTP, Passkey, Biometric) and reuse current functional code instead of replacing it. Keep approved Splash, Onboarding, Login/OTP, Home/Pro assets and AR/EN layouts.
3. Wire the public Supabase values into one version-appropriate client config. Native Google Credential Manager asks for an ID token intended for the verified accepted Web Client audience and exchanges it into Supabase Auth. Observe nonce / cancellation / wrong audience handling.
4. Check real Email OTP delivery/session persistence/logout. Passkeys require actual Supabase Dashboard activation + correct RP origin + verified assetlinks signing SHA256; do not claim verified merely from the Android challenge/verify code. Biometric cannot create a Supabase user by itself.
5. Use Android debugging/testing without deleting personal data. Compile/tests/screenshots where authorized. Distinguish remote provider config NOT_VERIFIED from code NOT_IMPLEMENTED.
6. Preserve independent source and Git history. Share only source diffs and evidence, not owner-private secrets. No reset/clean/force-push; never overwrite Windows project.

If the server-side Google secret/provider is not remotely confirmed, output `EXTERNAL_AUTH_CONFIG_UNVERIFIED` and continue locally. The owner already has the primary Google server-only JSON in Windows owner-only folder; asking for its raw secret inside the Android app is a security mistake.
