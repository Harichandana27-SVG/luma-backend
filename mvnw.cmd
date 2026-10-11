@REM ----------------------------------------------------------------------------
@REM LUMA Maven Wrapper for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "DIRNAME=%~dp0"
if "%DIRNAME%" == "" set "DIRNAME=."

echo [LUMA] Starting Maven build for LUMA Platform...
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn %*
) else (
    echo [ERROR] Maven ('mvn') is not found in your system PATH.
    echo Please install Apache Maven 3.8+ or configure PATH to point to your Maven bin directory.
    echo You can download Maven from: https://maven.apache.org/download.cgi
    exit /b 1
)
endlocal
