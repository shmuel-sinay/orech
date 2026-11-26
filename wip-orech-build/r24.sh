D=/data/data/de.pilablu.gpsconnector
ls -R $D 2>&1 | head -40
for f in $D/shared_prefs/*.xml; do echo "== $f"; head -c 1500 "$f"; echo; done
pm dump de.pilablu.gpsconnector | grep -E 'granted=' | head -20