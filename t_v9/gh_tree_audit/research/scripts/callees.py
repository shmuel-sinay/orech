import sys, re
sys.path.insert(0, "/home/user/android/tools"); import ag_compat
from androguard.core.analysis.analysis import Analysis
from androguard.core.dex import DEX
dx = Analysis()
for f in sys.argv[1].split(','): dx.add(DEX(open(f,'rb').read()))
dx.create_xref()
cls, meth, pat = sys.argv[2], sys.argv[3], re.compile(sys.argv[4])
for ma in dx.find_methods(classname=re.escape(cls), methodname="^"+re.escape(meth)+"$"):
    if ma.is_external(): continue
    for i, ins in enumerate(ma.get_method().get_instructions()):
        out = ins.get_output()
        if ins.get_name().startswith(("invoke","iput","sput","if-")) and pat.search(out):
            print(f"{i:5} {ins.get_name():18} {out[-140:]}")
