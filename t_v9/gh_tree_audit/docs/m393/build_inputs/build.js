const fs=require('fs'), zlib=require('zlib'), os=require('os'), path=require('path');
const src=process.argv[2], out=process.argv[3];
const img=fs.readFileSync(src);
const u32=o=>img.readUInt32LE(o);
const kernel=u32(8), ramdisk=u32(16), second=u32(24), page=u32(36), ver=u32(40), rdtbo=u32(1632), dtb=u32(1648);
if(ver!==2||second!==0||rdtbo!==0) throw new Error('unexpected header');
const al=n=>Math.ceil(n/page)*page;
const kOff=page, rdOff=page+al(kernel);
const rdGz=img.slice(rdOff,rdOff+ramdisk);
const cpio=zlib.gunzipSync(rdGz);
const dtbOff=rdOff+al(ramdisk);
const dtbData=img.slice(dtbOff,dtbOff+dtb);
// --- ramdisk modification
const key=fs.readFileSync(path.join(os.homedir(),'.android','adbkey.pub'),'utf8').trim();
if(/['"\$]/.test(key)) throw new Error('key has special chars');
const rc=`# one-shot recovery override (orech): finish the setup wizard and turn adb on with the PC key, once.
on property:sys.boot_completed=1
    exec_background u:r:magisk:s0 root root -- /system/bin/sh -c "if [ ! -e /data/misc/orech_unlock_done ]; then mkdir -p /data/misc/adb; echo '${key}' >> /data/misc/adb/adb_keys; chown system:shell /data/misc/adb/adb_keys; chmod 640 /data/misc/adb/adb_keys; settings put global device_provisioned 1; settings put secure user_setup_complete 1; settings put global adb_enabled 1; am force-stop com.google.android.setupwizard; touch /data/misc/orech_unlock_done; fi"
`;
function entry(name,mode,data){
  const nb=Buffer.from(name+'\0');
  const hex=n=>n.toString(16).padStart(8,'0');
  const hdr=Buffer.from('070701'+[0x7f0000+Math.floor(Math.random()*9999),mode,0,0,1,0,data.length,0,0,0,0,nb.length,0].map(hex).join(''));
  const pad=b=>Buffer.alloc((4-(b%4))%4);
  return Buffer.concat([hdr,nb,pad(110+nb.length),data,pad(data.length)]);
}
// find trailer
let o=0,tOff=-1;
while(true){ if(cpio.slice(o,o+6).toString()!=="070701") throw new Error("bad cpio at "+o); const h=n=>parseInt(cpio.slice(o+6+n*8,o+14+n*8).toString(),16);
 const fsz=h(6),nl=h(11); const name=cpio.slice(o+110,o+110+nl-1).toString(); if(name==="TRAILER!!!"){tOff=o;break;} let d=(o+110+nl+3)&~3; o=(d+fsz+3)&~3; }
const head=cpio.slice(0,tOff), trailer=cpio.slice(tOff);
const newCpio=Buffer.concat([head,entry('overlay.d/orech_setup.rc',0o100750,Buffer.from(rc)),trailer]);
// pad cpio to 256
const pad256=Buffer.alloc((256-(newCpio.length%256))%256);
const newGz=zlib.gzipSync(Buffer.concat([newCpio,pad256]),{level:9});
// --- rebuild image
const hdr=Buffer.from(img.slice(0,page));
hdr.writeUInt32LE(newGz.length,16);
const padTo=(buf)=>Buffer.concat([buf,Buffer.alloc(al(buf.length)-buf.length)]);
const body=Buffer.concat([hdr,padTo(img.slice(kOff,kOff+kernel)),padTo(newGz),padTo(dtbData)]);
if(body.length>33554432) throw new Error('too big');
const res=Buffer.concat([body,Buffer.alloc(33554432-body.length)]);
fs.writeFileSync(out,res);
console.log('ok',{kernel,oldRamdisk:ramdisk,newRamdisk:newGz.length,total:res.length});
