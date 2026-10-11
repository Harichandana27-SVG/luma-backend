@echo off
title Push LUMA Backend to GitHub
cls
echo ===================================================================
echo             LUMA Backend - Pushing to GitHub
echo             Target: https://github.com/Harichandana27-SVG/luma-backend
echo ===================================================================
echo.

set GH_USER=Harichandana27-SVG

cd /d "%~dp0"

echo Configuring Git author identity...
git init
git config user.name "Harichandana27-SVG"
git config user.email "Harichandana27-SVG@users.noreply.github.com"

echo Adding all backend Java files...
git add -A
git commit -m "LUMA Backend - Spring Boot 3.2.4, Security 6, JWT, Vault Architecture" 2>nul
git branch -M main
git remote remove origin 2>nul
git remote add origin https://github.com/%GH_USER%/luma-backend.git

echo.
echo Pushing files to GitHub...
echo (If a GitHub sign-in window appears, click 'Sign in with browser')
echo.
git push -u origin main --force

echo.
if %ERRORLEVEL% EQU 0 (
    echo ===================================================================
    echo  [SUCCESS!] Backend code successfully pushed to GitHub!
    echo.
    echo  Backend GitHub Link:
    echo  https://github.com/%GH_USER%/luma-backend
    echo ===================================================================
) else (
    echo ===================================================================
    echo  If push failed, make sure you clicked 'Sign in with browser'
    echo ===================================================================
)
echo.
pause
