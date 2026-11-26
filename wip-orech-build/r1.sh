F=/data/system/users/0/package-restrictions.xml
ls -l $F
head -c 120 $F | od -c | head -3
grep -c sdksandbox $F