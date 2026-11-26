import sys, re
import sys, os; sys.path.insert(0, "/home/user/android/tools"); import ag_compat
from loguru import logger; logger.remove()
from androguard.misc import AnalyzeDex
# analyze both dex files together so cross-dex references are found
from androguard.core.analysis.analysis import Analysis
from androguard.core.dex import DEX
dx = Analysis()
for f in sys.argv[1].split(','):
    dx.add(DEX(open(f,'rb').read()))
dx.create_xref()
cls = sys.argv[2]
for fname in sys.argv[3:]:
    for fa in dx.find_fields(classname=re.escape(cls), fieldname="^"+fname+"$"):
        print("##", fname)
        for kind, xs in (("READ", fa.get_xref_read()), ("WRITE", fa.get_xref_write())):
            seen=set()
            for x in xs:
                m = x[1]
                m = m.get_method() if hasattr(m, "get_method") else m; k=(m.get_class_name(), m.get_name())
                if k in seen: continue
                seen.add(k); print(f"   {kind:5} {m.get_class_name()[1:-1].replace('/','.')}.{m.get_name()}")
