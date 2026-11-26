import sys, re
import sys, os; sys.path.insert(0, "/home/user/android/tools"); import ag_compat
from loguru import logger; logger.remove()
from androguard.core.analysis.analysis import Analysis
from androguard.core.dex import DEX
dx = Analysis()
for f in sys.argv[1].split(','): dx.add(DEX(open(f,'rb').read()))
dx.create_xref()
for spec in sys.argv[2:]:
    cls, name = spec.rsplit('.',1)
    for ma in dx.find_methods(classname=re.escape(cls), methodname="^"+name+"$"):
        print("##", cls, name)
        seen=set()
        for _c, m, _o in ma.get_xref_from():
            m = m.get_method() if hasattr(m,'get_method') else m
            k=f"{m.get_class_name()[1:-1].replace('/','.')}.{m.get_name()}"
            if k not in seen: seen.add(k); print("   called by", k)
