import sqlite3, json, collections
p=r'C:\Users\day night\.local\share\opencode\opencode.db'
cn=sqlite3.connect('file:'+p.replace('\\','/')+'?mode=ro',uri=True)
sids=[r[0] for r in cn.execute("SELECT id FROM session_v2 WHERE replace(directory,'\\','/') LIKE 'K:/KNOUX-AI-Pro%'")]
print('PRO_SESSIONS',len(sids))
for sid in sids:
 rows=cn.execute('SELECT type,seq,data FROM session_message WHERE session_id=? ORDER BY seq LIMIT 6',(sid,)).fetchall()
 n=cn.execute('SELECT count(*) FROM session_message WHERE session_id=?',(sid,)).fetchone()[0]
 print('SESSION',sid,'TOTAL_EVENTS',n)
 for t,seq,data in rows:
  try:
   j=json.loads(data)
   print('EVENT_TYPE',t,'SEQ',seq,'KEYS',list(j.keys())[:15], 'CHILD_TYPES', {k:type(v).__name__ for k,v in j.items() if k in ('info','message','parts','content','role','text','data','type')})
  except Exception as e: print('EVENT_INVALID',t,type(e).__name__)
