const fs=require('fs');
const B='C:/פרוייקטים קלוד/zamir/m393/backups_2026-11-22/BT_Addr.orig';
const samples=[];
for(const f of fs.readdirSync('nv/')){ const b=fs.readFileSync('nv/'+f); if(b.length>4 && b[b.length-2]===0xaa && f!=='APRDEB_BT_Addr') samples.push([f,b]); }
const o=fs.readFileSync(B); samples.push(['BT_orig',o]);
samples.push(['BT_cur',fs.readFileSync('nv/APRDEB_BT_Addr')]);
const cands={
 sum8:(d)=>d.reduce((a,x)=>(a+x)&255,0),
 sumEven:(d)=>d.reduce((a,x,i)=>i%2===0?(a+x)&255:a,0),
 sumOdd:(d)=>d.reduce((a,x,i)=>i%2===1?(a+x)&255:a,0),
 xor8:(d)=>d.reduce((a,x)=>a^x,0),
 notsum:(d)=>(~d.reduce((a,x)=>(a+x)&255,0))&255,
 negsum:(d)=>(256-d.reduce((a,x)=>(a+x)&255,0))&255,
 sum32fold:(d)=>{let s=0;for(let i=0;i<d.length;i+=4){s=(s+(d[i]|(d[i+1]||0)<<8|(d[i+2]||0)<<16|(d[i+3]||0)<<24))>>>0;} return ((s&255)+((s>>>8)&255)+((s>>>16)&255)+((s>>>24)&255))&255;},
 sum16fold:(d)=>{let s=0;for(let i=0;i<d.length;i+=2){s=(s+(d[i]|((d[i+1]||0)<<8)))&0xffff;} return ((s&255)+(s>>8))&255;},
 crc8:(d)=>{let c=0;for(const x of d){c^=x;for(let k=0;k<8;k++)c=(c&0x80)?((c<<1)^0x07)&255:(c<<1)&255;}return c;},
 crc8_31:(d)=>{let c=0;for(const x of d){c^=x;for(let k=0;k<8;k++)c=(c&0x80)?((c<<1)^0x31)&255:(c<<1)&255;}return c;},
 // sum excluding trailing zero bytes / ff bytes
 sumNoFF:(d)=>d.reduce((a,x)=>x===255?a:(a+x)&255,0),
};
const ranges={ 'data(0..n-3)':(b)=>b.slice(0,b.length-2), 'data+aa(0..n-2)':(b)=>b.slice(0,b.length-1), 'all but last 2 of rec':(b)=>b.slice(0,b.length-2) };
for(const [rn,rf] of Object.entries(ranges)){
  for(const [cn,cf] of Object.entries(cands)){
    let ok=0; const res=[];
    for(const [name,b] of samples){ const v=cf([...rf(b)]); const want=b[b.length-1]; if(v===want) ok++; res.push(name+':'+v.toString(16)+(v===want?'*':'/'+want.toString(16))); }
    if(ok>=samples.length-2) console.log(ok+'/'+samples.length, rn, cn, res.join(' '));
  }
}
console.log('samples:',samples.map(s=>s[0]).join(','));
