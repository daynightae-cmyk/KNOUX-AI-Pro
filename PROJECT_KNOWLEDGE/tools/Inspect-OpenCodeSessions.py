"""Read-only OpenCode session schema inspection; print counts and column names, not user message content."""
import sqlite3, os
p=r'C:\Users\day night\.local\share\opencode\opencode.db'
if not os.path.isfile(p):
    print('DB_NOT_FOUND')
    raise SystemExit(0)
cn=sqlite3.connect('file:'+p.replace('\\','/')+'?mode=ro',uri=True,timeout=2)
print('DB_BYTES',os.path.getsize(p))
for (table,) in cn.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"):
    if table.startswith('sqlite_'): continue
    col=[x[1] for x in cn.execute('PRAGMA table_info("'+table+'")')]
    try: n=cn.execute('SELECT count(*) FROM "'+table+'"').fetchone()[0]
    except sqlite3.Error: n='unknown'
    print('TABLE',table,'ROWS',n,'COLS',','.join(col[:30]))
cn.close()
