#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
APK="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK" ]; then
  ./gradlew :app:assembleDebug
fi
adb install -r "$APK"
echo "Installed EficazDrive. Launching..."
adb shell monkey -p dev.eficazdrive.app 1 >/dev/null 2>&1 || true




