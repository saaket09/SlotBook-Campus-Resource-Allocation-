@echo off
title SlotBook — Shutdown
color 0C
echo.
echo  ========================================
echo    Stopping SlotBook Services...
echo  ========================================
echo.

echo  Stopping Backend (port 8080)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo  Stopping Frontend (port 3000)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo  ========================================
echo   SlotBook has been stopped.
echo  ========================================
echo.
pause

