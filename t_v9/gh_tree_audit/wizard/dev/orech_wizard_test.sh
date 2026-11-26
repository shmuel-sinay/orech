#!/system/bin/sh
# orech setup wizard helper (root). Two jobs:
#   boot: publish sys.orech.chosen=<profile> when a version was already chosen (flag survives factory reset)
#   pick: run what the wizard asked for. Request = property sys.orech.req = "<profile>:<pin>:<deviceid>"
# The wizard runs as a normal system app without root, so this service does the privileged part.
PS=/mnt/vendor/protect_s/orech
FLAG=$PS/version_chosen
LOG=$PS/wizard.log
PIN_SHA256=7de62716c17fc361e78809e6073945f256b10e7c8f92a70d30a82161d85cd797
INSTALLER=/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk
LOCKED=/vendor/etc/orech/GooglePackageInstaller_locked.apk
KEEP=/data/misc/orech_keep_root   # test mode: keep adb and root, no reboot (only root can create it)

log() { echo "$(date '+%F %T') $*" >> "$LOG"; }
done_() { setprop sys.orech.result "$1"; log "result $1"; exit 0; }

case "$1" in
boot)
  [ -s "$FLAG" ] && setprop sys.orech.chosen "$(tr -d '\n\r ' < "$FLAG")"
  exit 0
  ;;
pick)
  req=$(getprop sys.orech.req)
  # fields: profile:pin:deviceid:nonce (the nonce makes a retry re-trigger the property)
  profile=${req%%:*}; rest=${req#*:}; pin=${rest%%:*}; rest=${rest#*:}; devid=${rest%%:*}
  setprop sys.orech.result working
  [ -e "$FLAG" ] && done_ "fail:already"
  case "$profile" in locked|store|open) ;; *) done_ "fail:profile" ;; esac
  h=$(printf '%s' "$pin" | sha256sum | cut -d' ' -f1)
  FAILS=/data/misc/orech_pin_fail
  n=$(cat $FAILS 2>/dev/null); n=${n:-0}
  [ "$n" -lt 10 ] || done_ "fail:locked"
  [ "$h" = "$PIN_SHA256" ] || { echo $((n+1)) > $FAILS; log "bad pin"; done_ "fail:pin"; }
  rm -f $FAILS
  if [ -n "$devid" ]; then
    echo "$devid" | grep -qE '^[A-Z0-9]{3,8}$' || done_ "fail:id"
    echo "$devid" > /mnt/vendor/nvdata/orech_id && setprop persist.sys.orech_id "$devid" || done_ "fail:id-write"
  fi
  case "$profile" in
  locked|store)
    cur=$(sha256sum "$INSTALLER" | cut -d' ' -f1); want=$(sha256sum "$LOCKED" | cut -d' ' -f1)
    if [ "$cur" != "$want" ]; then
      [ "$(stat -c %s "$LOCKED")" -le "$(stat -c %s "$INSTALLER")" ] || done_ "fail:size"
      mount -o remount,rw /
      dd if="$LOCKED" of="$INSTALLER" bs=1048576 conv=notrunc 2>/dev/null
      truncate -s "$(stat -c %s "$LOCKED")" "$INSTALLER"; sync
      [ "$(sha256sum "$INSTALLER" | cut -d' ' -f1)" = "$want" ] || done_ "fail:installer-write"
    fi ;;
  open) log "open: installer left as shipped" ;;
  esac
  echo "$(date '+%F %T'),$devid,$profile" >> $PS/sales.csv
  # integrity: approve the new state, enforce only if it verifies clean (same rule as the seller app)
  sh $PS/orech_integrity.sh approve >/dev/null 2>&1 || done_ "fail:approve"
  out=$(sh $PS/orech_integrity.sh check 2>&1); [ -z "$out" ] || done_ "fail:check"
  echo "$profile" > "$FLAG"; chmod 600 "$FLAG"
  rm -f $PS/noenforce
  setprop sys.orech.chosen "$profile"
  done_ ok
  # lockdown + reboot are done by the wizard's "finish" request below
  ;;
finish)
  [ -s "$FLAG" ] || exit 0
  [ -e /data/misc/orech_finished ] && exit 0
  touch /data/misc/orech_finished
  if [ -e "$KEEP" ]; then log "finish: test mode, root and adb kept, no reboot"; exit 0; fi
  magisk --sqlite "DELETE FROM policies" >/dev/null 2>&1
  magisk --sqlite "DELETE FROM logs" >/dev/null 2>&1
  settings put global adb_enabled 0
  settings put global development_settings_enabled 0
  log "finish: lockdown done, rebooting"
  sleep 2; reboot
  ;;
esac
