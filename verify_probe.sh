#!/bin/bash

PACKAGE_NAME="com.test.audioprobe"
PERMISSION="android.permission.CAPTURE_AUDIO_OUTPUT"

# Allow passing flags (like -d, -e, or -s SERIAL) via first arg
ADB_FLAGS="${1:-}"

echo "Checking installation path for $PACKAGE_NAME..."
PATH_OUT=$(adb $ADB_FLAGS shell pm path $PACKAGE_NAME 2>&1)

if [[ -z "$PATH_OUT" ]]; then
    echo "❌ App is not installed."
    exit 1
fi

echo "✅ App is installed: $PATH_OUT"

if echo "$PATH_OUT" | grep -q "/system/priv-app"; then
    echo "✅ App is installed as a priv-app."
else
    echo "❌ App is NOT installed in /system/priv-app/. It is installed at: $PATH_OUT"
fi

echo ""
echo "Checking permission status for $PERMISSION..."

DUMPSYS_OUT=$(adb $ADB_FLAGS shell dumpsys package $PACKAGE_NAME | grep "$PERMISSION")

if [[ -z "$DUMPSYS_OUT" ]]; then
    echo "❌ $PERMISSION not found in dumpsys output for $PACKAGE_NAME."
    exit 1
fi

if echo "$DUMPSYS_OUT" | grep -q "granted=true"; then
    echo "✅ $PERMISSION is GRANTED!"
else
    echo "❌ $PERMISSION is DENIED! Details:"
    echo "$DUMPSYS_OUT"
fi
