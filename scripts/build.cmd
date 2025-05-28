@echo off
setlocal EnableDelayedExpansion

echo ===================================
echo Building PwManager
echo ===================================

REM Check if mvnw.cmd exists
if not exist "%~dp0..\mvnw.cmd" (
    echo Maven wrapper not found. Installing...
    call mvn wrapper:wrapper
)

REM Clean and build with tests
echo Running clean build with tests...
call mvnw.cmd clean test package

if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

REM Check if JAR was created
if not exist "%~dp0..\target\pwmanager-1.0-SNAPSHOT.jar" (
    echo JAR file not created!
    exit /b 1
)

echo ===================================
echo Build successful!
echo You can run the application with:
echo java -jar ..\target\pwmanager-1.0-SNAPSHOT.jar
echo ===================================

exit /b 0