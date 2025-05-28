@echo off
setlocal EnableDelayedExpansion

REM Set variables
set "PROJECT_DIR=%~dp0.."
set "OUTPUT_DIR=%PROJECT_DIR%\bin"
set "VERSION=1.0"
set "ARTIFACT_ID=pwmanager"

REM Configure JAR names and paths
set "JAR_NAME=%ARTIFACT_ID%-%VERSION%.jar"
set "TARGET_DIR=%PROJECT_DIR%\target"
set "JAR_PATH=%TARGET_DIR%\%JAR_NAME%"

echo ===================================
echo Building PwManager
echo Version: %VERSION%
echo ===================================

REM Clean and build with tests
echo Running clean build with tests...
call "%PROJECT_DIR%\mvnw.cmd" clean test package

if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

REM Check if JAR was created
if not exist "%JAR_PATH%" (
    echo JAR file not created at: %JAR_PATH%
    exit /b 1
)

REM Create output directory if it doesn't exist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM Remove existing JAR in output directory if it exists
if exist "%OUTPUT_DIR%\%JAR_NAME%" del /f "%OUTPUT_DIR%\%JAR_NAME%"

REM Copy JAR to output directory
echo Copying JAR to output directory...
copy /Y "%JAR_PATH%" "%OUTPUT_DIR%\%JAR_NAME%"

REM Zip all contents of OUTPUT_DIR
echo Creating zip archive of output directory...
powershell -Command "Compress-Archive -Path '%OUTPUT_DIR%\*' -DestinationPath '%OUTPUT_DIR%\pwmanager.zip' -Force"

echo ===================================
echo Build successful!
echo JAR location: %OUTPUT_DIR%\%JAR_NAME%
echo You can run the application with:
echo java -jar output\%JAR_NAME%
echo ===================================

exit /b 0