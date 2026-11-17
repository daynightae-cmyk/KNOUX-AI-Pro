# KNOUX AI / Credential Handoff — what really exists

This package is for TWO independent KNOUX AI Android development agents, not an all-credentials dump.

- `00_KNOUX_AI_VERIFIED_CLIENT_CONFIG.json` — COMPLETE VERIFIED CLIENT-PUBLIC CONFIG: Supabase enabled publishable key, Supabase project URL/ref, Google Web/Android/extra-Web Client IDs, callback, historical Windows debug SHA-1 and honest verification states.
- `01_PC_WINDOWS_ANDROID_AGENT.md` — one-shot instructions for Windows-developed Android code; points to original locally protected server-side OAuth JSON files and owner-only apply script. No raw server credentials copied.
- `02_ANDCODE_PHONE_ANDROID_AGENT.md` — one-shot instructions for AndCode Android/mobile development; needs ONLY public client values.

Private secrets were NOT included intentionally: primary/extra Google Web Client Secrets, Supabase personal management tokens, service_role/secret key, private signing keys, refresh tokens, passkey private material. They must NEVER be present in mobile APK or GitHub.

Source verification on 2026-11-14: original JSONs found on Windows; primary Web OAuth callback verified; connected Supabase project ACTIVE_HEALTHY, publishable key enabled; old Downloads/google-services.json belongs to KX Private VPN; live provider Google enabled state, Passkeys RP, Android actual SHA registrations, end-to-end login remain unverified.

GitHub main SHA at last inspection: `78391aa927b3629fcf856de04b10dae23fc2881c`.
