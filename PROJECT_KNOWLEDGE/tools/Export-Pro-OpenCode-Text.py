"""Read-only export of USER and ASSISTANT text from K:/KNOUX-AI-Pro OpenCode sessions.
Exclude hidden reasoning, tool logs, system instructions, credentials and all other projects.
"""
import sqlite3, json, re, datetime, pathlib, hashlib
source = pathlib.Path(r'C:\Users\day night\.local\share\opencode\opencode.db')
out = pathlib.Path(r'K:\KNOUX-AI-Pro\PROJECT_KNOWLEDGE\06_AGENT_PROMPTS_CONVERSATIONS\OpenCode_Pro_Sessions')
out.mkdir(parents=True,exist_ok=True)
conn = sqlite3.connect('file:'+source.as_posix()+'?mode=ro',uri=True,timeout=5)
patterns = [
 (r'-----BEGIN[\s\S]*?PRIVATE KEY-----[\s\S]*?-----END[\s\S]*?PRIVATE KEY-----', '[REDACTED_PRIVATE_KEY]'),
 (r'(?i)(?:sb_secret_|ghp_|github_pat_|sk-)[A-Za-z0-9_-]{12,}', '[REDACTED_SECRET]'),
 (r'(?i)\beyJ[A-Za-z0-9_-]{20,}\.[A-Za-z0-9_-]{15,}\.[A-Za-z0-9_-]{8,}\b', '[REDACTED_JWT]'),
 (r'(?i)(?:client_secret|access_token|refresh_token|service_role|management_token|password|api_key)\s*[=:]\s*\S{8,}', '[REDACTED_CREDENTIAL_VALUE]'),
 (r'(?i)Authorization:\s*Bearer\s+\S+', '[REDACTED_AUTHORIZATION_HEADER]'),
 (r'(?i)https?://[^/\s@:]+:[^/\s@]+@', '[REDACTED_URL_CREDENTIALS]'),
 (r'(?i)(\bserial(?:no)?\s*[:=]\s*)[A-Z0-9]{8,24}', r'\1[REDACTED]'),
 (r'\b(?:192\.168|10\.0)\.\d{1,3}\.\d{1,3}(?::\d{2,5})?\b','[REDACTED_NETWORK_ENDPOINT]'),
]
compiled=[(re.compile(p),v) for p,v in patterns]
def safe_text(x):
    if not isinstance(x,str): return ''
    for p,v in compiled: x=p.sub(v,x)
    return re.sub(r'\bR[A-Z0-9]{10,14}\b','[REDACTED_DEVICE_SERIAL]',x).replace('\x00','')
def utc(ms):
    try: return datetime.datetime.fromtimestamp(int(ms)/1000,datetime.timezone.utc).isoformat()
    except Exception: return 'unknown'
query = "SELECT id,directory,title,time_created,time_updated FROM session_v2 WHERE lower(replace(directory, char(92), '/')) LIKE ? ORDER BY time_updated DESC"
sessions=conn.execute(query,('k:/knoux-ai-pro%',)).fetchall()
for sid,where,title,t0,t1 in sessions:
    doc=['# OpenCode — '+safe_text(title or 'Untitled').replace('\n',' ')[:150],
         '', '- Project: K:/KNOUX-AI-Pro', '- Session: '+sid,
         '- Created UTC: '+utc(t0), '- Last updated UTC: '+utc(t1),'',
         '> Historical conversation. User and assistant visible text only; no tools, hidden reasoning or credential database.','']
    users=assistants=0
    for typ,data in conn.execute('SELECT type,data FROM session_message WHERE session_id=? ORDER BY seq',(sid,)):
        if typ not in ('user','assistant'): continue
        try: j=json.loads(data)
        except Exception: continue
        texts=[j.get('text','')] if typ=='user' else [
            x.get('text','') for x in j.get('content',[]) if isinstance(x,dict) and x.get('type')=='text']
        for s in texts:
            if not isinstance(s,str) or not s.strip(): continue
            s=safe_text(s)
            if len(s)>100000: s=s[:100000]+'\n[TRUNCATED_LONG_MESSAGE]'
            if typ=='user': users+=1
            else: assistants+=1
            doc.extend(['## '+('User' if typ=='user' else 'Assistant')+' — '+utc(j.get('time')),'',s,''])
    path=out/(sid+'.md')
    path.write_text('\n'.join(doc),encoding='utf-8-sig')
    print('EXPORTED',sid,'USER',users,'ASSISTANT',assistants,'BYTES',path.stat().st_size,
          'SHA256_12',hashlib.sha256(path.read_bytes()).hexdigest()[:12])
print('TOTAL_SESSIONS',len(sessions))
conn.close()
