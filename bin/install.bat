@echo off
setlocal

set TOOL_NAME=pwmanager
set JAR_NAME=pwmanager-1.0.jar

REM Relaunch as admin if not already
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo This action requires administrator privileges.
    echo Please approve the UAC prompt.
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

echo Welcome to the %TOOL_NAME% installer
echo.

set /p installPath=Install globally? (requires admin - n for local) [Y/n]: 
if /I "%installPath%"=="n" (
    set "BIN_DIR=%USERPROFILE%\AppData\Local\Programs\%TOOL_NAME%"
) else (
    set "BIN_DIR=C:\Program Files\%TOOL_NAME%"
)

REM Try to create the install directory
mkdir "%BIN_DIR%" >nul 2>&1
if not exist "%BIN_DIR%" (
    echo ERROR: Could not create directory "%BIN_DIR%". Try running this script as administrator.
    pause
    exit /b 1
)

REM Copy JAR from script directory
copy "%SCRIPT_DIR%%JAR_NAME%" "%BIN_DIR%" >nul
if not exist "%BIN_DIR%\%JAR_NAME%" (
    echo ERROR: Failed to copy JAR file to "%BIN_DIR%".
    pause
    exit /b 1
)

REM Create wrapper batch file
(
    echo @echo off
    echo java -jar "%%~dp0%JAR_NAME%" %%*
) > "%BIN_DIR%\%TOOL_NAME%.bat"
if not exist "%BIN_DIR%\%TOOL_NAME%.bat" (
    echo ERROR: Failed to create launcher batch file.
    pause
    exit /b 1
)

echo Installed at: %BIN_DIR%
echo.

set /p addToPath=Do you want to add "%BIN_DIR%" to your user PATH? [Y/n]: 
if /I "%addToPath%"=="n" (
    echo Skipping PATH update.
) else (
    setx PATH "%BIN_DIR%;%PATH%"
    echo Added "%BIN_DIR%" to your user PATH. You may need to restart your terminal.
)

echo.
echo Make sure "%BIN_DIR%" is in your PATH to run "%TOOL_NAME%" from anywhere.
pause