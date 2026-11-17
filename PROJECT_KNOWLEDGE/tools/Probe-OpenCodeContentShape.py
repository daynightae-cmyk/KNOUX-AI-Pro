import sqlite3,json,collections
p=r'C:\Users\day night\.local\share\opencode\opencode.db'
cn=sqlite3.connect('file:'+p.replace('\\','/')+'?mode=ro',uri=True)
for sid, in cn.execute("SELECT id FROM session_v2 WHERE replace(directory,'\\','/') LIKE 'K:/KNOUX-AI-Pro%'"):
 print('SESSION',sid)
 t=collections.Counter();s=collections.Counter()
 for typ,data in cn.execute('SELECT type,data FROM session_message WHERE session_id=?',(sid,)):
  t[typ]+=1
  if typ=='assistant':
   obj=json.loads(data)
   for part in (obj.get('content') or []):
    if isinstance(part,dict): s[str(part.get('type'))+':'+','.join(part.keys())]+=1
 print('EVENTS',t)
 print('PART_SHAPES',s.most_common(18))
