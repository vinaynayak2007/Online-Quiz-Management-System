#!/bin/bash
# Standalone Native Android APK Build Script for Online Quiz Management System (Pure Android, No Web/HTML)
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
ROOT_DIR="$(dirname "$DIR")"
BUILD_DIR="/tmp/native_android"

echo "==> Compiling 100% Pure Native Android APK (com.quizsystem.android)..."
mkdir -p "$BUILD_DIR/gen" "$BUILD_DIR/bin" "$BUILD_DIR/res" "$BUILD_DIR/src/com/quizsystem/android"

cp -r "$DIR/src/main/res"/* "$BUILD_DIR/res/"
cp "$DIR/src/main/AndroidManifest.xml" "$BUILD_DIR/AndroidManifest.xml"
cp "$DIR/src/main/java/com/quizsystem/android"/*.java "$BUILD_DIR/src/com/quizsystem/android/"

cd "$BUILD_DIR"
aapt package -f -m -J gen -M AndroidManifest.xml -S res -I /tmp/android.jar
javac -source 8 -target 8 -bootclasspath /tmp/android.jar -d bin gen/com/quizsystem/android/R.java src/com/quizsystem/android/*.java
java -cp /tmp/r8.jar com.android.tools.r8.D8 --lib /tmp/android.jar --output bin/ bin/com/quizsystem/android/*.class
aapt package -f -M AndroidManifest.xml -S res -I /tmp/android.jar -F bin/unaligned.apk
cd bin && aapt add unaligned.apk classes.dex && cd ..
zipalign -f -p 4 bin/unaligned.apk bin/aligned.apk
apksigner sign --ks /tmp/quiz_keystore.jks --ks-pass pass:quizpass123 --key-pass pass:quizpass123 --ks-key-alias quizkey --out "$ROOT_DIR/OnlineQuizSystem.apk" bin/aligned.apk
cp "$ROOT_DIR/OnlineQuizSystem.apk" "$DIR/OnlineQuizSystem.apk"

echo "==> Successfully built signed native APK: $ROOT_DIR/OnlineQuizSystem.apk"
