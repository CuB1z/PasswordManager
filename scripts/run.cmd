@echo off
setlocal EnableDelayedExpansion

REM Set variables
set "PROJECT_DIR=%~dp0.."
set "OUTPUT_DIR=%PROJECT_DIR%\bin"
set "VERSION=1.0-SNAPSHOT"
set "ARTIFACT_ID=pwmanager"
set "JAR_NAME=%ARTIFACT_ID%-%VERSION%.jar"
set "JAR_PATH=%OUTPUT_DIR%\%JAR_NAME%"

REM Check if JAR exists
if not exist "%JAR_PATH%" (
    echo JAR file not found at: %JAR_PATH%
    echo Please run build.cmd first
    exit /b 1
)

REM Run the application
java -jar "%JAR_PATH%"

exit /b %ERRORLEVEL%