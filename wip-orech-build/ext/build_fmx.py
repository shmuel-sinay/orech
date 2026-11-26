"""Builds the lite (no video, no install) file manager under a new package name.
Usage: python -I build_fmx.py [--name "<label>"] [--package com.orech.files]
Mirrors the lite logic of the upstream build.ps1 using the local Android SDK + Android Studio JBR."""
import argparse, os, shutil, subprocess, sys, glob

ap = argparse.ArgumentParser()
ap.add_argument('--name', default='מנהל קבצים')
ap.add_argument('--package', default='com.orech.files')
args = ap.parse_args()

ROOT = r'C:\orech_build\ext'
SRC_MAIN = os.path.join(ROOT, 'fmx', 'app', 'src', 'main')
OUT = os.path.join(ROOT, 'fmx_out')
SDK = r'C:\Users\user1\AppData\Local\Android\Sdk'
BT = os.path.join(SDK, 'build-tools', '37.0.0')
AJAR = os.path.join(SDK, 'platforms', 'android-34', 'android.jar')
JDK = r'C:\Program Files\Android\Android Studio\jbr\bin'
KS = os.path.join(ROOT, 'keys', 'fmx_release.keystore')
PASS = open(os.path.join(ROOT, 'keys', 'fmx.pass'), encoding='ascii').read().strip()
assert os.path.exists(KS), 'signing key missing: ' + KS

def run(cmd):
    r = subprocess.run(cmd, capture_output=True, text=True, encoding='utf-8', errors='replace')
    if r.returncode != 0:
        print(r.stdout, r.stderr)
        sys.exit('FAILED: ' + os.path.basename(cmd[0]))
    return r

if os.path.exists(OUT):
    shutil.rmtree(OUT)
os.makedirs(os.path.join(OUT, 'gen'))
os.makedirs(os.path.join(OUT, 'classes'))
os.makedirs(os.path.join(OUT, 'dex'))
src = os.path.join(OUT, 'src')
shutil.copytree(SRC_MAIN, src)

def rw(path, fn):
    s = open(path, encoding='utf-8', newline='').read()
    s2 = fn(s)
    assert s2 != s, 'no change: ' + path
    open(path, 'w', encoding='utf-8', newline='').write(s2)

flavor = os.path.join(src, 'java', 'com', 'custom', 'files', 'Flavor.java')
rw(flavor, lambda s: s.replace('NO_VIDEO = false', 'NO_VIDEO = true').replace('NO_INSTALL = false', 'NO_INSTALL = true')
   .replace('"com.custom.files.provider"', '"%s.provider"' % args.package)
   .replace('NAME = "מנהל הקבצים X"', 'NAME = "%s"' % args.name))
man = os.path.join(src, 'AndroidManifest.xml')
def fix_manifest(s):
    s = s.replace('com.custom.files.provider', args.package + '.provider')
    lines = [l for l in s.split('\n') if 'REQUEST_INSTALL_PACKAGES' not in l]
    return '\n'.join(lines)
rw(man, fix_manifest)
rw(os.path.join(src, 'res', 'values', 'values.xml'), lambda s: s.replace('>מנהל הקבצים X<', '>%s<' % args.name))
# the provider comment/other string references to the old authority are only documentation

aapt2 = os.path.join(BT, 'aapt2.exe')
run([aapt2, 'compile', '--dir', os.path.join(src, 'res'), '-o', os.path.join(OUT, 'res.zip')])
run([aapt2, 'link', '-I', AJAR, '--manifest', man, '--min-sdk-version', '26', '--target-sdk-version', '33',
     '--java', os.path.join(OUT, 'gen'), '--rename-manifest-package', args.package,
     '-o', os.path.join(OUT, 'base.apk'), os.path.join(OUT, 'res.zip')])
javas = glob.glob(os.path.join(src, 'java', '**', '*.java'), recursive=True) + glob.glob(os.path.join(OUT, 'gen', '**', '*.java'), recursive=True)
run([os.path.join(JDK, 'javac.exe'), '-encoding', 'UTF-8', '-Xlint:-options', '-source', '17', '-target', '17',
     '-cp', AJAR, '-d', os.path.join(OUT, 'classes')] + javas)
classes = glob.glob(os.path.join(OUT, 'classes', '**', '*.class'), recursive=True)
run([os.path.join(JDK, 'java.exe'), '-cp', os.path.join(BT, 'lib', 'd8.jar'), 'com.android.tools.r8.D8', '--release',
     '--min-api', '26', '--lib', AJAR, '--output', os.path.join(OUT, 'dex')] + classes)
subprocess.run([os.path.join(JDK, 'jar.exe'), '-uf', os.path.join(OUT, 'base.apk'), '-C', os.path.join(OUT, 'dex'), 'classes.dex'], check=True)
run([os.path.join(BT, 'zipalign.exe'), '-f', '-p', '4', os.path.join(OUT, 'base.apk'), os.path.join(OUT, 'aligned.apk')])
if not os.path.exists(KS):
    run([os.path.join(JDK, 'keytool.exe'), '-genkeypair', '-keystore', KS, '-alias', 'fmx', '-keyalg', 'RSA', '-keysize', '2048',
         '-validity', '10000', '-storepass', PASS, '-keypass', PASS, '-dname', 'CN=Files'])
final = os.path.join(OUT, 'FileManager-Lite.apk')
run([os.path.join(JDK, 'java.exe'), '-jar', os.path.join(BT, 'lib', 'apksigner.jar'), 'sign', '--ks', KS,
     '--ks-pass', 'pass:' + PASS, '--out', final, os.path.join(OUT, 'aligned.apk')])
print('OK', final, os.path.getsize(final))
