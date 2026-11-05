#!/usr/bin/env bash
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
BUILD="$ROOT/build"
rm -rf "$BUILD"
mkdir -p "$BUILD/classes" "$BUILD/javadoc"
find "$ROOT/src/main/java" -name '*.java' | sort > "$BUILD/sources.txt"
javac --release 17 -d "$BUILD/classes" @"$BUILD/sources.txt"
printf 'Manifest-Version: 1.0\nMain-Class: com.smartschedule.SmartScheduleApp\n\n' > "$BUILD/MANIFEST.MF"
jar cfm "$BUILD/SmartSchedule.jar" "$BUILD/MANIFEST.MF" -C "$BUILD/classes" .
javadoc -quiet -d "$BUILD/javadoc" -sourcepath "$ROOT/src/main/java" -subpackages com.smartschedule
printf 'Built: %s\n' "$BUILD/SmartSchedule.jar"
