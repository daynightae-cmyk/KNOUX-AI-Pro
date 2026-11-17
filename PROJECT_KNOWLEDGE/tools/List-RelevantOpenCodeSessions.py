"""Read-only metadata-only query, never print credential or message text."""
import sqlite3, re
p=r'C:\Users\day night\.local\share\opencode\opencode.db'
cn=sqlite3.connect('file:'+p.replace('\\','/')+'?mode=ro',uri=True,timeout=3)
rows=cn.execute('SELECT id, project_id, directory, path, title, time_created, time_updated FROM session_v2 ORDER BY time_updated DESC').fetchall()
for ix,r in enumerate(rows):
    s=' '.join(str(x or '') for x in r[1:5]);
    if re.search(r'knoux.?ai|knoux.?pro|andcode|mobile|android', s, re.I):
        print('MATCH',r[0], 'project',r[1], 'dir',str(r[2])[:100], 'path',str(r[3])[:100], 'title',str(r[4])[:115], 'updated',r[6])
print('TOTAL_SESSIONS',len(rows))
cn.close()
