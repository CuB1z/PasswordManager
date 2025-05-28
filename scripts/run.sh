#!/bin/bash
set -e

# Set variables
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT_DIR="$PROJECT_DIR/bin"
VERSION="1.0-SNAPSHOT"
ARTIFACT_ID="pwmanager"
JAR_NAME="$ARTIFACT_ID-$VERSION.jar"
JAR_PATH="$OUTPUT_DIR/$JAR_NAME"

# Check if JAR exists
if [[ ! -f "$JAR_PATH" ]]; then
    echo "JAR file not found at: $JAR_PATH"
    echo "Please run build.sh first"
    exit 1
fi

# Run the application
java -jar "$JAR_PATH"

exit $?