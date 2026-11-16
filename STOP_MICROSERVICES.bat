@echo off
title SlotBook — Stop Microservices
color 0C
echo.
echo  ======================================================
echo    Stopping SlotBook Microservices...
echo  ======================================================
echo.

echo  Stopping User Service (Port 8081)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo  Stopping Resource Service (Port 8082)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo  Stopping Booking Service (Port 8083)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8083" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo  Stopping Frontend Server (Port 3000)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo  ======================================================
echo   All SlotBook services have been stopped.
echo  ======================================================
echo.
pause
