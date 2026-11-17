# KNOUX AI PRO — SECURE DIRECT GITHUB PUSH
Original OpenCode container only. DO NOT use independent Windows D:\KNOUX-AI.
Repository ALREADY CREATED and PRIVATE: https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git
Expected source /root/workspace/KNOUX-AI, master, HEAD 64aeb002b1ab5d1da42ac7f0787bbd2318192a4b. Verify actual values.

1. cd /root/workspace/KNOUX-AI; verify git root/HEAD/status -uall/worktrees/stashes/remotes and no active conflicting writer.
2. Audit tracked Git history for secrets/keys/private materials and files >100 MiB. Clean working tree does not prove secret-free commits. Stop if any unsafe material found; never rewrite history without owner authorization.
3. Ensure remote knoux-pro points EXACTLY to https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git. Do not modify other remotes.
4. Test gh --version. If unavailable, install the official GitHub CLI only through supported authorized package management (e.g. Alpine apk add github-cli), preserving project files. If unavailable, ask owner to add a fresh repo-scoped SSH deploy PUBLIC key generated in this container; never reveal private key.
5. PREFERRED AUTH: gh auth login --hostname github.com --git-protocol https --web
6. Show the owner the GitHub device verification URL https://github.com/login/device and the one-time device code, and pause only for OWNER approval. Do not ask for a Personal Access Token, password, OTP, cookies or credential screenshots. gh may store credentials in config when no secure keychain; keep permissions private and NEVER commit that config.
7. After approved auth: gh auth status (redact secrets); gh auth setup-git; git ls-remote knoux-pro refs/heads/main. Target expected empty.
8. Push exactly: git push -u knoux-pro HEAD:refs/heads/main. Do NOT force. If remote unexpectedly nonempty, stop and reconcile.
9. Confirm git rev-parse HEAD equals git ls-remote knoux-pro refs/heads/main. Verify repository Private and actual source files. Provide push exit, exact hashes, source inventory and any blocked unique files.
10. Do not commit 496 MB / 480 MB export archives, build directories, credentials or unrelated Android personal data. Preserve all unique files locally. Do not touch Windows D:\KNOUX-AI or old OpenCode app data.

IMPORTANT: Windows gh is already authenticated but it does not have the original container source. Running git push in the Windows repo would upload the WRONG project.
Do actual push after user approves official device login. No fake closure.