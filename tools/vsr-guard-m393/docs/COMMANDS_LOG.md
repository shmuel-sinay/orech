# Command log (chronological, session 2026-11-20 .. 2026-11-26)

Device: M393, Android 13, Magisk 26.0, adb serial 0123456789ABCDEF.
ADB on the PC: `C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe` (not in PATH, bash has no adb).
In `adb shell` use a here-string piped to `adb shell` (PowerShell mangles `(` and quotes in inline strings).
All root commands go through `su -c '...'`.

## 0. Starting state (done before this session, by someone else)
- `/vendor/etc/media_codecs_c2.xml` etc. with video decoders removed and 12 `enabled="false"` entries.
- Gallery/Music/MusicFX removed, PreviewActivity disabled.
- Backup of originals on the PC: `zamir\m393\vendor_codecs\orig`.

## 1. Inspect (read-only)
```
adb devices -l
adb shell "id; getprop ro.product.model; getprop ro.build.version.release; ls -l /vendor/etc/media_codecs_c2.xml"
su -c id                                            -> uid=0 context=u:r:magisk:s0
grep -c 'enabled=.false' /vendor/etc/media_codecs_c2.xml   -> 12
pm list packages -3
```

## 2. Scan for apps carrying video decoders (read-only)
```
su -c 'for f in $(find /data/app -type f -name "*.so"); do if strings -a "$f" | head -c 4000000 | grep -q -F -e libavcodec -e libffmpeg -e vpx_codec -e h264_decode -e hevc_decode -e vp9_decode -e av1_decode; then echo "$f"; fi; done'
```
Found: `ai.vocal.remover.splitter.karaoke.instrumental` (full FFmpeg: h264, hevc, vp9, av1) and `in.krosbits.musicolet` (libavcodec with mpeg4/h263/wmv/flv/vc1 only).
Note: the name `libavcodec` alone matches any FFmpeg, so the module requires >= 2 decoder strings as well.

## 3. Install the Magisk module (`module/` in this repo)
```
adb push module /data/local/tmp/vsr_mod
su -c 'M=/data/adb/modules/vsr_block; mkdir -p $M && cp /data/local/tmp/vsr_mod/* $M/ && chown -R 0:0 $M && chmod 755 $M $M/*.sh && chmod 644 $M/module.prop $M/allowlist.txt && chcon -R u:object_r:system_file:s0 $M'
su -c 'sh -n /data/adb/modules/vsr_block/service.sh'
su -c 'nohup sh /data/adb/modules/vsr_block/service.sh >/dev/null 2>&1 &'
```
Equivalent: `scripts/install.ps1`.

### Bugs hit and fixed (keep these fixes)
1. Package name extraction `${pkg%-*}` was wrong because the install dir suffix contains a `-`
   (`pkg-6rrX8VcoNgUNzw-yATHYpg==`). Fixed to `${pkg%%-*}`. Before the fix the module locked
   Musicolet's `libavcodec.so` although it was in the allowlist. Restored with `sh uninstall.sh`.
2. `su -c 'pkill -f vsr_block/service.sh'` kills its own `su` shell (exit 143). Use the bracket trick:
   `pkill -f "vsr_blo[c]k/service.sh"`.
3. The scan repeated on the same lib every few seconds (duplicate lines in `locked.txt`). Added
   `grep -qxF "$f" "$STATE" && continue`.

## 4. Tests
```
# fake package outside the allowlist, expect it to be locked, then delete it
su -c 'T=/data/app/~~fake==/com.fake.test-AAA==/lib/arm64; mkdir -p $T; cp <vocal-remover libavcodec.so> $T/libavcodec.so; chmod 755 $T/libavcodec.so'
  -> after ~25s: lib is ---------- and logged
su -c 'rm -rf "/data/app/~~fake=="'

# Musicolet dependency test
chmod 000 <musicolet libavcodec.so>; am force-stop in.krosbits.musicolet; play an mp3 via
  am start -a android.intent.action.VIEW -d file:///sdcard/Music/vsr_test.mp3 -t audio/mpeg -p in.krosbits.musicolet
  -> logcat: UnsatisfiedLinkError in.krosbits.nativex.FDTS.ntIDTS, no playback session
chmod 755 <musicolet libavcodec.so>   (restored)
```
Result: Musicolet needs its libavcodec, so it stays in `allowlist.txt`.

## 5. Policy decisions given by the user
- Musicolet must keep playing mp3 -> allowlisted.
- Vocal remover not important -> removed from the allowlist, its `libavcodec.so` is locked (000).
- Do NOT block installation of new apps (no `pm disable-user` on packageinstaller). Not done.
- Device Owner / user restrictions / blackhole VPN from the original guide: not done.

## 6. VLC
User installed `org.videolan.vlc`; the module locked `lib/arm64/libvlc.so` ~seconds later.
Launch test (`monkey -p org.videolan.vlc -c android.intent.category.LAUNCHER 1`) logged:
`Can't load vlcjni library: dlopen failed: library "libvlc.so" not found`.

## 7. Side effects to know
- `logcat -c` run twice. `am force-stop` on musicolet, vocal remover, VLC.
- Temp copy `/sdcard/Music/vsr_test.mp3` created and deleted.
- `/data/local/tmp/vsr_mod` (staging) and `/data/local/tmp/vsr.log` remain.

## 8. Final state (verified)
- Module `vsr_block` in `/data/adb/modules`, service running (started manually, boot autostart NOT verified).
- Locked: Vocal remover `libavcodec.so`, VLC `libvlc.so`. Allowlist: `in.krosbits.musicolet`.

## 9. Not verified
- Module autostart after reboot; mp3 playback end-to-end in Musicolet after all changes;
  any real video playback; browsers/WebView decoders.
