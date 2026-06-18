#!/bin/bash

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_ROOT/src"
OUT_DIR="$PROJECT_ROOT/out"
JAR_NAME="$PROJECT_ROOT/tcrypt.jar"

echo "Cleaning old build..."
rm -rf "$OUT_DIR"
rm -f "$JAR_NAME"

mkdir -p "$OUT_DIR"

echo "Collecting source files..."
find "$SRC_DIR" -name "*.java" > sources.txt

echo "Compiling..."
javac -d "$OUT_DIR" @sources.txt

echo "Creating JAR..."

# Detect main class (optional fallback)
MAIN_CLASS="tcrypt.crypto.Tcrypt"

cat > manifest.txt << EOF
Main-Class: $MAIN_CLASS

EOF

jar cfm "$JAR_NAME" manifest.txt -C "$OUT_DIR" .

rm sources.txt manifest.txt

echo "Build complete: $JAR_NAME"
