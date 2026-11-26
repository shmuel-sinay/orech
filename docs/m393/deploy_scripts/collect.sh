rm -rf /data/local/tmp/nv; mkdir -p /data/local/tmp/nv
for f in /mnt/vendor/nvdata/APCFG/APRDCL/* /mnt/vendor/nvdata/APCFG/APRDEB/*; do
  [ -f "$f" ] || continue
  n=$(basename "$f")
  d=$(basename "$(dirname "$f")")
  cp "$f" "/data/local/tmp/nv/${d}_${n}"
done
chmod -R 755 /data/local/tmp/nv
ls /data/local/tmp/nv | wc -l
