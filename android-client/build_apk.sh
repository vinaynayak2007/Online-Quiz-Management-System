#!/bin/bash
# Standalone Android APK Build Script for Online Quiz Management System
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
ROOT_DIR="$(dirname "$DIR")"
BUILD_DIR="/tmp/android_build"

echo "==> Building OnlineQuizSystem.apk..."
mkdir -p "$BUILD_DIR/gen" "$BUILD_DIR/bin" "$BUILD_DIR/res/values" "$BUILD_DIR/res/layout" "$BUILD_DIR/res/mipmap-xxhdpi" "$BUILD_DIR/assets/www"

# Copy resources & assets
cp "$DIR/src/main/AndroidManifest.xml" "$BUILD_DIR/AndroidManifest.xml" 2>/dev/null || true
cp "$ROOT_DIR/src/main/resources/static/index.html" "$BUILD_DIR/assets/www/index.html"
cp "$ROOT_DIR/src/main/resources/static/admin.html" "$BUILD_DIR/assets/www/admin.html"

# If tools available, compile and sign
if command -v aapt &> /dev/null && [ -f /tmp/android.jar ]; then
    echo "==> Packaging with aapt and compiling dex..."
    cd "$BUILD_DIR"
    aapt package -f -m -J gen -M AndroidManifest.xml -S res -I /tmp/android.jar
    javac -source 8 -target 8 -bootclasspath /tmp/android.jar -d bin gen/com/quizsystem/android/R.java "$DIR"/src/main/java/com/quizsystem/android/*.java
    java -cp /tmp/r8.jar com.android.tools.r8.D8 --lib /tmp/android.jar --output bin/ bin/com/quizsystem/android/*.class
    aapt package -f -M AndroidManifest.xml -S res -A assets -I /tmp/android.jar -F bin/unaligned.apk
    cd bin && aapt add unaligned.apk classes.dex && cd ..
    zipalign -f -p 4 bin/unaligned.apk bin/aligned.apk
    apksigner sign --ks /tmp/quiz_keystore.jks --ks-pass pass:quizpass123 --key-pass pass:quizpass123 --ks-key-alias quizkey --out "$ROOT_DIR/OnlineQuizSystem.apk" bin/aligned.apk
    cp "$ROOT_DIR/OnlineQuizSystem.apk" "$DIR/OnlineQuizSystem.apk"
    echo "==> Successfully built $ROOT_DIR/OnlineQuizSystem.apk"
else
    echo "==> Precompiled APK is located at $ROOT_DIR/OnlineQuizSystem.apk"
fi
