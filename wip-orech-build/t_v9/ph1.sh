P=$(ls -d /data/app/*/com.orech.seller-*/ | head -1)
mkdir -p ${P}lib/arm64
echo dummy > ${P}lib/arm64/libffmpeg_test.so
chmod 644 ${P}lib/arm64/libffmpeg_test.so
echo "$P" > /data/local/tmp/v9test.path
ls -l ${P}lib/arm64/libffmpeg_test.so
