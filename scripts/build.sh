#!/bin/bash
set -e

# Set variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="$PROJECT_DIR/bin"
VERSION="1.0-SNAPSHOT"
ARTIFACT_ID="pwmanager"

# Configure JAR names and paths
JAR_NAME="${ARTIFACT_ID}-${VERSION}.jar"
TARGET_DIR="$PROJECT_DIR/target"
JAR_PATH="$TARGET_DIR/$JAR_NAME"

echo "==================================="
echo "Building PwManager"
echo "Version: $VERSION"
echo "==================================="

# Clean and build with tests
echo "Running clean build with tests..."
"$PROJECT_DIR/mvnw" clean test package

# Check if JAR was created
if [[ ! -f "$JAR_PATH" ]]; then
    echo "JAR file not created at: $JAR_PATH"
    exit 1
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Remove existing JAR in output directory if it exists
if [[ -f "$OUTPUT_DIR/$JAR_NAME" ]]; then
    rm -f "$OUTPUT_DIR/$JAR_NAME"
fi

# Copy JAR to output directory
echo "Copying JAR to output directory..."
cp -f "$JAR_PATH" "$OUTPUT_DIR/$JAR_NAME"

# Zip all contents of OUTPUT_DIR
echo "Creating zip archive of output directory..."
cd "$OUTPUT_DIR"
zip -r pwmanager.zip ./*
cd "$PROJECT_DIR"

echo "==================================="
echo "Build successful!"
echo "JAR location: $OUTPUT_DIR/$JAR_NAME"
echo "You can run the application with:"
echo "java -jar output/$JAR_NAME"
echo "==================================="

exit 0
