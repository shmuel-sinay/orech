BL=/data/system/orech_vguard/blacklist.txt
pm list packages >/dev/null 2>&1 || { echo "package service not ready, abort"; exit 1; }
# restore the two entries removed by the earlier faulty run, if missing
for p in org.videolan.vlc com.alphainventor.filemanager; do grep -qx "$p" $BL || echo "$p" >> $BL; done
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  out=$(pm uninstall $p 2>&1); echo "uninstall $p: $out"
  case "$out" in *Success*) sed -i "/^$p\$/d" $BL;; esac
done
echo "after: [$(cat $BL | tr '\n' ' ')]"
echo "still installed: $(pm list packages | grep -cE 'vlc|alphainventor')"
