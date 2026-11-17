"""Read-only check: content safety and copy hash integrity. Does not display matched secret values."""
import pathlib,csv,hashlib,re,collections
root=pathlib.Path(r'K:\KNOUX-AI-Pro\PROJECT_KNOWLEDGE')
repo=root.parent
manifest=list(csv.DictReader((root/'90_INDEX/IMPORT_MANIFEST.local.csv').open(encoding='utf-8-sig',newline='')))
checked=bad=0
for row in manifest:
 if row['Status']!='COPY_SHA_VERIFIED':continue
 p=repo/pathlib.Path(row['Stored'])
 if not p.exists() or hashlib.sha256(p.read_bytes()).hexdigest().lower()!=row['SHA256'].lower():bad+=1
 else:checked+=1
patterns={
 'PRIVATE_KEY':re.compile(r'-----BEGIN [^-]*PRIVATE KEY',re.I),
 'HIGH_CONFIDENCE_TOKEN':re.compile(r'(?:sb_secret_|ghp_|github_pat_|sk-)[a-zA-Z0-9_-]{12,}',re.I),
 'JWT':re.compile(r'\beyJ[a-zA-Z0-9_-]{20,}\.[a-zA-Z0-9_-]{15,}\.[a-zA-Z0-9_-]{8,}\b'),
 'CREDENTIAL_VALUE':re.compile(r'(?:client_secret|access_token|refresh_token|service_role)\s*[=:]\s*["\']?[A-Za-z0-9_.-]{18,}',re.I),
 'PRIVATE_IP':re.compile(r'\b(?:192\.168|10\.0)\.\d{1,3}\.\d{1,3}(?::\d{2,5})?\b'),
 'DEVICE_SERIAL':re.compile(r'\bR[A-Z0-9]{10,14}\b')
}
flags=collections.defaultdict(list)
texts={'.md','.txt','.json','.csv','.ps1','.py','.svg'}
files=0
for p in root.rglob('*'):
 if not p.is_file() or p.suffix.lower() not in texts or p.name.endswith(('.local.csv','.local.json')):continue
 files+=1
 try: s=p.read_text(encoding='utf-8-sig')
 except (UnicodeError,OSError):continue
 for typ,pat in patterns.items():
  if pat.search(s):flags[typ].append(str(p.relative_to(root)))
print('COPY_SHA_VERIFIED',checked,'COPY_HASH_ERRORS',bad,'TEXT_FILES_SCANNED',files)
for k in patterns:
 print(k,'FLAGGED_FILES',len(flags[k]))
 for v in flags[k][:14]: print('  FILE',v)
