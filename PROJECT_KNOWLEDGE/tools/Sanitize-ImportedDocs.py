"""Redact device identifiers only in copied knowledge text; preserve original sources, update provenance."""
import pathlib,csv,re,hashlib,collections
root=pathlib.Path(r'K:\KNOUX-AI-Pro\PROJECT_KNOWLEDGE')
repo=root.parent
index=root/'90_INDEX/IMPORT_MANIFEST.local.csv'
with index.open(encoding='utf-8-sig',newline='') as f: items=list(csv.DictReader(f))
count=0
for x in items:
 rel=x.get('Stored','')
 if not rel or x['Status'] not in ('COPY_SHA_VERIFIED','SANITIZED_COPY'):continue
 p=repo/pathlib.Path(rel)
 if not p.is_file():continue
 if p.suffix.lower() not in ('.md','.txt','.json','.csv','.svg'):continue
 try: content=p.read_text(encoding='utf-8-sig')
 except (UnicodeError,OSError):continue
 changed=re.sub(r'\bR[A-Z0-9]{10,14}\b','[REDACTED_DEVICE_SERIAL]',content)
 changed=re.sub(r'\b(?:192\.168|10\.0)\.\d{1,3}\.\d{1,3}(?::\d{2,5})?\b','[REDACTED_NETWORK_ENDPOINT]',changed)
 if changed!=content:
  p.write_text(changed,encoding='utf-8-sig')
  x['Status']='SANITIZED_COPY'
  count+=1
 x['TargetSHA256']=hashlib.sha256(p.read_bytes()).hexdigest()
fields=list(dict.fromkeys([key for x in items for key in x.keys()] + ['TargetSHA256']))
with index.open('w',encoding='utf-8-sig',newline='') as f:
 writer=csv.DictWriter(f,fieldnames=fields);writer.writeheader();writer.writerows(items)
catalog=root/'90_INDEX/KNOWLEDGE_CATALOG.md'
lines=['# KNOUX AI Pro — Knowledge Catalog','',
       '2026-11-15 curated sources. Files with SANITIZED_COPY have source bytes preserved at original path; check local-only manifest for both hashes.','',
       '## Browse','',
       'Open ../00_START_HERE/AGENT_ENTRYPOINT_AR.md first. Existing code/docs/design remain in their original repository folders.','']
groups=collections.defaultdict(list)
for r in items:
 if r.get('Stored'):groups[r['Group']].append(r)
for group,rows in sorted(groups.items()):
 lines.extend(['## '+group+' ('+str(len(rows))+')',''])
 for r in sorted(rows,key=lambda x:x['Stored']):
  rel=r['Stored'].removeprefix('PROJECT_KNOWLEDGE/')
  name=pathlib.Path(rel).name
  href='../'+rel.replace(' ','%20').replace('\\','/')
  lines.append('- ['+name+']('+href+') — '+r['Status']+' — original SHA256 '+r['SHA256'][:12])
 lines.append('')
lines.extend(['## More sources','',
              'See ../04_DESIGN_AND_IMAGES/IMAGE_INDEX.md for 49 in-place reference images; ../06_AGENT_PROMPTS_CONVERSATIONS/CONVERSATIONS_INDEX.md for filtered OpenCode transcripts.',
              'Additional candidates and original absolute paths are local only in discovery_candidates.local.csv and IMPORT_MANIFEST.local.csv.'])
catalog.write_text('\n'.join(lines)+'\n',encoding='utf-8-sig')
print('SANITIZED_DOCUMENTS',count,'CATALOG_ENTRIES',sum(len(x) for x in groups.values()))
