const fs=require('fs'), zlib=require('zlib');
const img=fs.readFileSync(process.argv[2]);
const u32=o=>img.readUInt32LE(o);
const kernel=u32(8), ramdisk=u32(16), second=u32(24), page=u32(36), ver=u32(40);
const al=n=>Math.ceil(n/page)*page;
console.log({kernel,ramdisk,second,page,ver,recdtbo:u32(1632),hdrsz:u32(1644),dtb:u32(1648), name:img.slice(48,64).toString().replace(/\0/g,''), cmdline:img.slice(64,576).toString().replace(/\0.*/s,'')});
const rdOff=page+al(kernel);
const rd=img.slice(rdOff,rdOff+ramdisk);
console.log('ramdisk head', rd.slice(0,8).toString('hex'), 'offset', rdOff);
let cpio;
if(rd[0]==0x1f&&rd[1]==0x8b){cpio=zlib.gunzipSync(rd);console.log('gzip');}
else if(rd.readUInt32LE(0)==0x184C2102){console.log('lz4 legacy');cpio=null;}
else if(rd.slice(0,6).toString()=='070701'){cpio=rd;console.log('raw cpio');}
else console.log('unknown');
if(cpio){fs.writeFileSync('ramdisk.cpio',cpio);
 let o=0;const out=[];
 while(true){ if(cpio.slice(o,o+6).toString()!='070701')break; const h=n=>parseInt(cpio.slice(o+6+n*8,o+14+n*8).toString(),16);
  const mode=h(1),fs_=h(6),nl=h(11); const name=cpio.slice(o+110,o+110+nl-1).toString(); let d=o+110+nl; d=(d+3)&~3; out.push(name+' '+mode.toString(8)+' '+fs_); o=(d+fs_+3)&~3; if(name=='TRAILER!!!')break;}
 console.log(out.join('\n'));}
