@echo off
setlocal enabledelayedexpansion
title SlotBook — Startup
color 0A
echo.
echo  ========================================
echo    SlotBook -- Campus Resource Booking
echo  ========================================
echo.

:: 1. Backend startup
echo  [1/2] Starting Spring Boot backend...
cd /d "%~dp0slotbook\backend"

:: Check if backend is already listening
curl.exe -s -f http://localhost:8080/api/resources >nul 2>&1
if %errorlevel% equ 0 (
    echo        Backend is already running on port 8080.
) else (
    if exist "target\slotbook-1.0.0.jar" (
        echo        Launching pre-built JAR (fast startup)...
        start "SlotBook Backend" cmd /k "java -jar target\slotbook-1.0.0.jar"
    ) else (
        echo        Launching via Maven...
        start "SlotBook Backend" cmd /k "mvn spring-boot:run"
    )
)

:: 2. Frontend startup
echo.
echo  [2/2] Starting frontend server...
cd /d "%~dp0slotbook\frontend"
curl.exe -s -f http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo        Frontend is already running on port 3000.
) else (
    start "SlotBook Frontend" cmd /k "node server.js 3000"
)

:: 3. Waiting for backend to be fully initialized and ready
echo.
echo  Waiting for backend to be ready on port 8080...
set /a ATTEMPTS=0

:POLL_LOOP
set /a ATTEMPTS+=1
curl.exe -s -f http://localhost:8080/api/resources >nul 2>&1
if %errorlevel% equ 0 goto BACKEND_READY

if %ATTEMPTS% geq 35 (
    echo.
    echo  [WARNING] Backend startup is taking longer than expected.
    echo            Check the "SlotBook Backend" window for error messages.
    goto OPEN_BROWSER
)

<nul set /p=.
timeout /t 1 /nobreak >nul
goto POLL_LOOP

:BACKEND_READY
echo.
echo  [OK] Backend is ready and responding!

:OPEN_BROWSER
echo.
echo  Opening frontend in browser...
start "" "http://localhost:3000"

echo.
echo  ========================================
echo   SlotBook is RUNNING!
echo   Frontend : http://localhost:3000
echo   Backend  : http://localhost:8080
echo   H2 DB    : http://localhost:8080/h2-console
echo  ========================================
echo.
echo  Demo Credentials:
echo   - Admin   : admin@campus.edu  / admin123
echo   - Student : rahul@campus.edu  / password123
echo.
echo  To stop the application, run STOP.bat
echo  or close the Backend and Frontend windows.
echo.
pause
