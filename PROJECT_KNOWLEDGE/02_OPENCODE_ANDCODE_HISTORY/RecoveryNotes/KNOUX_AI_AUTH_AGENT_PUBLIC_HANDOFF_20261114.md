# KNOUX AI — VERIFIED PUBLIC AUTH HANDOFF FOR ANDCODE AGENT
As checked 2026-11-14; user-authorized, nonsecret connection fields only. THIS IS NOT PROOF LOGIN WORKS.

## Verified Supabase backend
PROJECT_NAME=knoux ai
PROJECT_REF=tduofkizwkjtqcbklfah
SUPABASE_URL=https://tduofkizwkjtqcbklfah.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_19JCfWlBOdUng5LHAoMfRg_hlFl7YnC
PUBLISHABLE_KEY_ENABLED=true

## IMPORTANT: same service, different workspaces
The active AndCode agent must inspect its actual current workspace and its own Git status; do not assume the source in Android-local container equals D:\KNOUX-AI. Canonical PC source main HEAD was 78391aa927b3629fcf856de04b10dae23fc2881c when checked. A user screenshot claims new authentication code in AndCode; that source has NOT been compared with GitHub.

## To execute, NOT just plan
1. In AndCode import/read this file and configure the existing Android app's *public* Supabase URL and publishable API key in a single config abstraction. These two values are safe for a client, but are not a service_role or administrative credential. Don't pretend this alone activates Google/Passkeys.
2. Verify actual package ID/build variant and current Auth controller. Current KNOUX-AI public GitHub source at the checked SHA uses com.knoux.ai and contains a real local BiometricPrompt, but Google/Passkeys are explicitly unconfigured placeholders; the active AndCode local source may be different. Preserve unique work from both versions.
3. Google OAuth: In the project Supabase Dashboard > Authentication > Sign In / Providers > Google, validate enabled/provider configuration and correct WEB OAuth Client ID and Client Secret, securely stored in Supabase's server-only auth settings. Never put Google client_secret into Android APK, Git, chat, or this file. Also confirm Android OAuth client ID, package name, release/debug SHA signing fingerprints and redirect URI for the actually selected flow. The only old local client_secret_ JSON currently found on the Windows host belongs to DIFFERENT Google Cloud project knoux-smartorganizer-dev: do not reuse.
4. Email OTP: Inspect Supabase Email provider, email confirmation and real user flow; never invent a successful OTP.
5. Passkeys: Supabase passkeys are experimental. Verify Dashboard Authentication > Passkeys enabled and HTTPS relying-party RP ID/origins. For native Android, verify Android Credential Manager WebAuthn ceremony and https://knoux.store/.well-known/assetlinks.json Digital Asset Links using the ACTUAL APK signing SHA256. Do not change production RP ID blindly; changes invalidate already registered passkeys. Registering a passkey needs an existing confirmed account; sign-in after registered passkey.
6. Biometric: Android BiometricPrompt is local unlock/session reauthentication only; it is not a server-side Supabase OAuth identity by itself. Bind it to a previously established user session using Android Keystore and secure storage; no bypass/guest fake session.
7. RLS/pro: never expose service_role/secret key, Google client secret, Management API access token, or OAuth refresh tokens. Client uses publishable key and Auth session JWT; enforce user-isolated RLS. Verify behavior on real device.
8. Provide proof: actual source path SHA, gradle build/tests, real Google/email OTP/Passkey/auth session on device, redirect handler, and remote Supabase Auth settings. If remote auth configuration cannot be read, report NOT_VERIFIED rather than configured.

This file contains ONLY publicly usable project endpoint + client publishable key; all sensitive credentials remain server-side. Do not request the owner to paste a secret in the chat.
