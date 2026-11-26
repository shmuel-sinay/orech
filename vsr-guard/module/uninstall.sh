#!/system/bin/sh
# restore permissions of every lib this module locked
MODDIR=${0%/*}
while read -r f; do [ -f "$f" ] && chmod 755 "$f"; done < "$MODDIR/locked.txt"
