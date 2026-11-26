BL=/data/system/orech_vguard/blacklist.txt
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  echo "uninstall $p: $(pm uninstall $p 2>&1)"
done
: > /dev/null
for p in $(cat $BL); do pm path $p >/dev/null 2>&1 || sed -i "/^$p\$/d" $BL; done
echo "after: [$(cat $BL | tr '\n' ' ')]"
pm list packages | grep -E 'vlc|alphainventor' | wc -l
