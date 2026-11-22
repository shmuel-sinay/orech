#!/system/bin/sh
# orech integrity monitor. Lives on the protect_s partition (not in super, survives reflashing it).
#   monitor   - verify file hashes after boot and every 5 minutes; power off if something changed
#   approve   - record the current hashes as the approved state (the seller app calls this after a PIN-approved change)
#   check     - print the changed files, if any
B=/mnt/vendor/protect_s/orech
MAN=$B/manifest.sha256
PATHS=$B/paths.txt
LOG=$B/integrity.log
GRACE=${ORECH_GRACE:-120}
INTERVAL=${ORECH_INTERVAL:-300}

log() { echo "$(date '+%F %T') $*" >> "$LOG"; }

approve() {
  : > "$MAN.new"
  for p in $(tr -d '\r' < "$PATHS"); do
    [ -f "$p" ] && sha256sum "$p" >> "$MAN.new"
  done
  mv -f "$MAN.new" "$MAN"
  log "approved $(wc -l < "$MAN") files"
}

check() {
  bad=""
  [ -s "$MAN" ] || { echo "no-manifest"; return; }
  while read -r h p; do
    cur=$(sha256sum "$p" 2>/dev/null | cut -d' ' -f1)
    [ "$cur" = "$h" ] || bad="$bad $p"
  done < "$MAN"
  echo "$bad"
}

power_off() {
  if [ -n "$ORECH_DRYRUN" ]; then log "DRYRUN: would power off"; return; fi
  reboot -p
}

case "$1" in
  approve) approve ;;
  check) check ;;
  monitor)
    mkdir /dev/orech_integrity.lock 2>/dev/null || exit 0
    until [ "$(getprop sys.boot_completed)" = "1" ]; do sleep 5; done
    sleep 60
    while true; do
      bad=$(check)
      if [ "$bad" = "no-manifest" ]; then
        :
      elif [ -n "$bad" ]; then
        log "MISMATCH:$bad"
        if [ -e "$B/noenforce" ]; then
          log "noenforce set, not powering off"
        else
          log "powering off in ${GRACE}s unless noenforce appears or the change is approved"
          sleep "$GRACE"
          bad=$(check)
          if [ -n "$bad" ] && [ "$bad" != "no-manifest" ] && [ ! -e "$B/noenforce" ]; then
            log "POWER OFF:$bad"
            power_off
          fi
        fi
      fi
      sleep "$INTERVAL"
    done
    ;;
  *) echo "usage: $0 monitor|approve|check" ;;
esac