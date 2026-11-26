import sys, re
import sys, os; sys.path.insert(0, "/home/user/android/tools"); import ag_compat
from loguru import logger; logger.remove()
from androguard.misc import AnalyzeDex
from androguard.decompiler.decompile import DvMethod
cls = sys.argv[2]; targets = sys.argv[3:]
_, d, dx = AnalyzeDex(sys.argv[1])
for name in targets:
    for ma in dx.find_methods(classname=re.escape(cls), methodname="^"+re.escape(name)+"$"):
        if ma.is_external(): continue
        print("=====", name, ma.get_method().get_descriptor())
        dv = DvMethod(ma); dv.process(); print(dv.get_source())
