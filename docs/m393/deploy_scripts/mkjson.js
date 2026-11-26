const fs=require('fs');
const P='C:/פרוייקטים קלוד/orech/app/src/main/assets/patches/patches.json';
const arr=JSON.parse(fs.readFileSync(P,'utf8'));
const rd=n=>fs.readFileSync('C:/orech_build/deploy/methods/'+n,'utf8').replace(/\r/g,'').replace(/^\uFEFF/,'').trim();
const add=[
 { id:'recent_apps_limit_m393', title:'ביטול מגבלת 3 אפליקציות אחרונות (M393)', kind:'edit', device:'M393',
   targetFile:'/system/framework/services.jar', remount:'/', signingKey:'platform',
   expectedSha256:'9aaf16ad0d962f1416d7ee8737bbcd41a4b170cd8c16126f44ae5c1c5012d841',
   edits:[{kind:'method',className:'Lcom/android/server/wm/RecentTasks;',methodName:'checkRecentTask',
     newMethodSmali:'.method private checkRecentTask()V\n    .registers 1\n\n    return-void\n.end method'}],
   note:'אומת על services.jar של M393 (classes2.dex). הערה: services.jar אינו חתום; signingKey נשאר כמו ב-M36. מוצע לבצע יחד עם m393_adb_usb_fix באותה הרצה, כי שניהם עורכים את אותו הקובץ.' },
 { id:'m393_adb_usb_fix', title:'תיקון באג adb שנפתח תמיד ושואל אישור שוב ושוב (M393)', kind:'edit', device:'M393',
   targetFile:'/system/framework/services.jar', remount:'/', signingKey:'platform',
   expectedSha256:'9aaf16ad0d962f1416d7ee8737bbcd41a4b170cd8c16126f44ae5c1c5012d841',
   edits:[
     {kind:'method',className:'Lcom/android/server/usb/UsbDeviceManager$UsbHandler;',methodName:'finishBoot',newMethodSmali:rd('finishBoot.smali')},
     {kind:'method',className:'Lcom/android/server/usb/UsbDeviceManager$UsbHandler;',methodName:'handleMessage',newMethodSmali:rd('handleMessage.smali')}],
   note:'הבאג: ב-finishBoot וב-handleMessage של UsbDeviceManager$UsbHandler מוזרקת קריאה קבועה setAdbEnabled(true) שמאתחלת את adb בכל חיבור/ניתוק ומבקשת אישור RSA מחדש. התיקון מסיר אותה. עורך את אותו services.jar כמו recent_apps_limit_m393, לכן אם רוצים את שניהם יש לשלב לתיקון אחד. לא נבדק עדיין במכשיר.' },
 { id:'keyguard_backdoor_m393', title:'הסרת דלת אחורית בנעילת המסך (M393)', kind:'edit', device:'M393',
   targetFile:'/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk', remount:'/', signingKey:'platform',
   expectedSha256:'99fd67f0dd0920758a419c6668260efe41216b051360f70ec109a326fa41752a',
   edits:[{kind:'method',className:'Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;',methodName:'onPatternDetected',newMethodSmali:rd('onPatternDetected.smali')}],
   note:'מחליף את המחרוזת 9875321 (Z הפוך) שמפעילה את persist.sys.secret במחרוזת שלא תתאים לעולם. חתימה: platform (חובה, sharedUserId=android.uid.systemui). לא נבדק עדיין במכשיר.' }
];
for(const a of add){ const i=arr.findIndex(x=>x.id===a.id); if(i>=0) arr[i]=a; else arr.push(a); }
fs.writeFileSync(P, JSON.stringify(arr,null,2)+'\n','utf8');
const back=JSON.parse(fs.readFileSync(P,'utf8')); console.log('entries:',back.length, back.map(x=>x.id).join(', '));
