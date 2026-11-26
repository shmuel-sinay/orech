B=/mnt/vendor/protect_s/orech
touch $B/testbreak
sed -i '5s/^./0/' $B/manifest.sha256
echo armed
