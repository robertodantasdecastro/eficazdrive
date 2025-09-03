#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

CMD="gradle"
if [ -x "./gradlew" ]; then
  CMD="./gradlew"
fi

$CMD :app:assembleDebug
echo "APK built at app/build/outputs/apk/debug/app-debug.apk"


