const fs = require('fs');
const reps = [
  ['    new-instance v2, Landroid/app/AlertDialog$Builder;\n    invoke-direct {v2, p0}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V\n',
   '    new-instance v2, Landroid/app/AlertDialog$Builder;\n    const v3, 0x010302d2\n    invoke-direct {v2, p0, v3}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;I)V\n'],
  ['    new-instance v1, Landroid/app/AlertDialog$Builder;\n    invoke-direct {v1, p0}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V\n',
   '    new-instance v1, Landroid/app/AlertDialog$Builder;\n    const v2, 0x010302d2\n    invoke-direct {v1, p0, v2}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;I)V\n'],
  ['const-string v1, "הודעת מערכת"', 'const-string v1, "התקנת אפליקציות"'],
  ['const-string v1, "המכשיר שלך חסום להתקנה"', 'const-string v1, "ההתקנה חסומה במכשיר הזה"']
];
function apply(text, label) {
  let t = text;
  for (const [a, b] of reps) {
    const n = t.split(a).length - 1;
    if (n !== 1) throw new Error(label + ': pattern matched ' + n + ' times: ' + a.slice(0, 60));
    t = t.replace(a, () => b);
  }
  return t;
}
const CR = String.fromCharCode(13);
const rp = 'tools/reference_smali/installstart.smali';
const raw = fs.readFileSync(rp, 'utf8');
const crlf = raw.includes(CR + '\n');
const lf = crlf ? raw.split(CR + '\n').join('\n') : raw;
const out = apply(lf, 'smali');
fs.writeFileSync(rp, crlf ? out.split('\n').join(CR + '\n') : out, 'utf8');
const P = 'app/src/main/assets/patches/patches.json';
const arr = JSON.parse(fs.readFileSync(P, 'utf8'));
const e = arr.find(x => x.id === 'installer_password_lock').edits.find(x => x.className === 'Lcom/android/packageinstaller/InstallStart;');
e.newClassSmali = apply(e.newClassSmali.split(CR + '\n').join('\n'), 'json');
fs.writeFileSync(P, JSON.stringify(arr, null, 2) + '\n', 'utf8');
JSON.parse(fs.readFileSync(P, 'utf8'));
console.log('ok, entries', arr.length, 'crlf-smali=', crlf);
