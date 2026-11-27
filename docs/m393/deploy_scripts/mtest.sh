for p in org.videolan.vlc net.sf.times in.krosbits.musicolet com.topjohnwu.magisk de.pilablu.gpsconnector bin.mt.plus.canary com.rhmsoft.pulsar; do
  apk=$(pm path $p 2>/dev/null | head -1 | sed 's/package://')
  [ -n "$apk" ] || { echo "$p: not installed"; continue; }
  v=$(unzip -p "$apk" AndroidManifest.xml 2>/dev/null | tr -d '\000' | grep -a -c 'video/')
  i=$(unzip -p "$apk" AndroidManifest.xml 2>/dev/null | tr -d '\000' | grep -a -c 'INSTALL_PACKAGES')
  echo "$p: video/=$v INSTALL_PACKAGES=$i"
done
