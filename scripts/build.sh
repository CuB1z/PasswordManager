#!/bin/bash
set -e

echo "==================================="
echo "Building PwManager"
echo "==================================="

# Check if mvnw exists
if [ ! -f "$(dirname "$0")/../mvnw" ]; then
    echo "Maven wrapper not found. Installing..."
    (cd "$(dirname "$0")/.." && mvn wrapper:wrapper)
fi

# Clean and build with tests
echo "Running clean build with tests..."
(cd "$(dirname "$0")/.." && ./mvnw clean test package)

# Check if JAR was created
if [ ! -f "$(dirname "$0")/../target/pwmanager-1.0-SNAPSHOT.jar" ]; then
    echo "JAR file not created!"
    exit 1
fi

echo "==================================="
echo "Build successful!"
echo "You can run the application with:"
echo "java -jar ../target/pwmanager-1.0-SNAPSHOT.jar"
echo "==================================="

exit 0
