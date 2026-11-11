#!/usr/bin/env python3
"""Offline integrity test: reference dimensions + all required assets + SHA256 manifest."""
from pathlib import Path
from hashlib import sha256
import json, struct, sys

root = Path(__file__).resolve().parent.parent
images = sorted((root/'design'/'reference').glob('*.png'))
assert len(images) == 10, f'Expected 10 images, found {len(images)}'
for image in images:
    data = image.read_bytes()
    assert data[:8] == b'\x89PNG\r\n\x1a\n', f'Invalid PNG: {image.name}'
    dims = struct.unpack('>II', data[16:24])
    assert dims == (864, 1536), (image.name, dims)
assert (root/'MASTER_PROMPT_CODEX_PHASE_1_UI.md').is_file()
assert (root/'database/supabase/migrations/002_rls.sql').is_file()
assert len(list((root/'prompts/phase2').glob('*.md'))) == 10
manifest = {}
for f in sorted(root.rglob('*')):
    if f.is_file() and not f.name.startswith('MANIFEST_SHA256'):
        manifest[f.relative_to(root).as_posix()] = sha256(f.read_bytes()).hexdigest()
if '--write' in sys.argv:
    (root/'provenance/MANIFEST_SHA256.json').write_text(json.dumps(manifest,indent=2,ensure_ascii=False)+'\n',encoding='utf-8')
else:
    stored = json.loads((root/'provenance/MANIFEST_SHA256.json').read_text(encoding='utf-8'))
    assert stored == manifest, 'Checksum manifest mismatch'
print(f'PASS: {len(images)} reference PNGs, {len(manifest)} source files checksummed')
