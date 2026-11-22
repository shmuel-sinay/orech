#!/system/bin/sh
# orech integrity monitor. Lives on the protect_s partition (not in super, survives reflashing it).
#   boot      - runs from init at post-fs-data and blocks boot: power off at once if something changed\n#   monitor   - verify hashes after boot and every minute; power off after a short grace if something changed
#   approve   - record the current hashes as the approved state (the seller app calls this after a PIN-approved change)
#   check     - print the changed files, if any
B=/mnt/vendor/protect_s/orech
MAN=$B/manifest.sha256
PATHS=$B/paths.txt
LOG=$B/integrity.log
GRACE=${ORECH_GRACE:-10}
INTERVAL=${ORECH_INTERVAL:-60}

log() { echo "$(date '+%F %T') $*" >> "$LOG"; }

approve() {
  : > "$MAN.new"
  for p in $(tr -d '\r' < "$PATHS"); do
    [ -e "$p" ] && sha256sum "$p" >> "$MAN.new"
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
  if [ -n "$ORECH_DRYRUN" ] || [ -e "$B/testbreak" ]; then log "DRYRUN: would power off"; return; fi
  reboot -p
}

case "$1" in
  approve) approve ;;
  check) check ;;
  boot)
    if [ -e "$B/noenforce" ]; then log "boot check skipped (noenforce)"; exit 0; fi
    bad=$(check)
    if [ -z "$bad" ]; then log "boot check ok"; exit 0; fi
    [ "$bad" = "no-manifest" ] && { log "boot check: no manifest"; exit 0; }
    log "BOOT MISMATCH:$bad"
    if [ -e "$B/testbreak" ]; then log "TESTBREAK: would stop the boot here"; exit 0; fi
    setprop sys.powerctl shutdown
    sleep 120
    ;;  monitor)
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
  *) echo "usage: $0 boot|monitor|approve|check" ;;
esac