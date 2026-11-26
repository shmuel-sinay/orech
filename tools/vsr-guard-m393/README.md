# vsr-guard-m393

Magisk module and runbook that stops apps from decoding video on the M393 (Android 13, Magisk 26).
This repo is meant to be handed to a Claude Code session to reproduce or continue the work.

## What is here
| path | what |
|---|---|
| `module/` | the Magisk module (`service.sh`, `module.prop`, `uninstall.sh`, `allowlist.txt`) |
| `scripts/install.ps1` | push + install + start the module on the connected device |
| `scripts/verify.ps1` | read-only state check |
| `scripts/uninstall.ps1` | restore lib permissions and remove the module |
| `docs/COMMANDS_LOG.md` | every command run, bugs found, decisions, final state |
| `docs/REPORT.html` | the Hebrew report (RTL) of everything created |

## Layers on the device
1. `/vendor/etc/media_codecs*.xml` without video decoders plus `enabled="false"` for software ones (done earlier, not part of this repo; originals are in `zamir\m393\vendor_codecs\orig` on the PC).
2. This module: every ~10s scans new or updated packages in `/data/app` and `chmod 000` on native libs that look like video decoders, unless the package is in `allowlist.txt`.

## Instructions for a Claude session
1. Read `docs/COMMANDS_LOG.md` first, then run `scripts/verify.ps1 -Adb "<path to adb.exe>"`.
2. To reproduce on a fresh device: `scripts/install.ps1`, then add the packages the user wants kept (currently only `in.krosbits.musicolet`) to `allowlist.txt` BEFORE installing.
3. Do not block app installation and do not set Device Owner unless the user asks explicitly.
4. Keep the two fixes from the log: `${pkg%%-*}` and `pkill -f "vsr_blo[c]k/..."`.
5. Ask before anything irreversible; locking a lib is reversible with `chmod 755` on the path in `locked.txt`.

## Known gaps
Musicolet stays unlocked (its native engine needs its FFmpeg). Apps that download or hide decoders
after install, and browsers/WebView, are not covered. Boot autostart is untested.
