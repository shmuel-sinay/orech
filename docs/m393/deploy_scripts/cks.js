const fs=require('fs');
const dir='nv/';
const files=fs.readdirSync(dir);
for(const f of files){
  const b=fs.readFileSync(dir+f); const n=b.length;
  const tail=[...b.slice(n-4)].map(x=>x.toString(16).padStart(2,'0')).join(' ');
  let sum=0; for(let i=0;i<n-2;i++) sum=(sum+b[i])&0xff;
  let sumAll=0; for(let i=0;i<n-1;i++) sumAll=(sumAll+b[i])&0xff;
  let x=0; for(let i=0;i<n-2;i++) x^=b[i];
  console.log(f.padEnd(18),'n=',String(n).padStart(5),'tail=',tail,'sum8(0..n-3)=',sum.toString(16).padStart(2,'0'),'sum8(0..n-2)=',sumAll.toString(16).padStart(2,'0'),'xor=',x.toString(16).padStart(2,'0'));
}
// try also the current BT_Addr written by the HAL vs original backup
const cur=fs.readFileSync(dir+'APRDEB_BT_Addr'), orig=fs.readFileSync('C:/פרוייקטים קלוד/zamir/m393/backups_2026-11-22/BT_Addr.orig');
console.log('cur==orig?', Buffer.compare(cur,orig)===0, ' differing bytes:', [...cur].map((v,i)=>v!==orig[i]?i+':'+v.toString(16)+'/'+orig[i].toString(16):null).filter(Boolean).join(' '));
