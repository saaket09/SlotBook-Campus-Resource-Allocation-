@echo off
title Push SlotBook to GitHub
color 0B
echo.
echo ===================================================
echo   Pushing SlotBook to GitHub...
echo ===================================================
echo.
cd /d "%~dp0"
echo Repository: https://github.com/saaket09/SlotBook-Campus-Resource-Allocation-.git
echo.
git push -u origin main
echo.
if %errorlevel% equ 0 (
    color 0A
    echo ===================================================
    echo   SUCCESS! SlotBook has been pushed to GitHub.
    echo ===================================================
) else (
    color 0C
    echo ===================================================
    echo   Push failed or was cancelled. Check output above.
    echo ===================================================
)
echo.
pause
