@echo off
setlocal enabledelayedexpansion
title SlotBook — Microservices Startup
color 0A
echo.
echo  ======================================================
echo    SlotBook -- Microservices Architecture
echo  ======================================================
echo.

:: 1. User Service (Port 8081)
echo  [1/4] Starting User Service (Port 8081)...
cd /d "%~dp0slotbook\microservices\user-service"
curl.exe -s -f http://localhost:8081/api/users/1 >nul 2>&1
if %errorlevel% equ 0 (
    echo        User Service is already running.
) else (
    start "User Service [Port 8081]" cmd /k "java -jar target\user-service-1.0.0.jar"
)

:: 2. Resource Service (Port 8082)
echo  [2/4] Starting Resource Service (Port 8082)...
cd /d "%~dp0slotbook\microservices\resource-service"
curl.exe -s -f http://localhost:8082/api/resources >nul 2>&1
if %errorlevel% equ 0 (
    echo        Resource Service is already running.
) else (
    start "Resource Service [Port 8082]" cmd /k "java -jar target\resource-service-1.0.0.jar"
)

:: 3. Booking Service (Port 8083)
echo  [3/4] Starting Booking Service (Port 8083)...
cd /d "%~dp0slotbook\microservices\booking-service"
curl.exe -s -f http://localhost:8083/api/bookings/user/1 >nul 2>&1
if %errorlevel% equ 0 (
    echo        Booking Service is already running.
) else (
    start "Booking Service [Port 8083]" cmd /k "java -jar target\booking-service-1.0.0.jar"
)

:: 4. Frontend Server (Port 3000)
echo  [4/4] Starting Frontend Server (Port 3000)...
cd /d "%~dp0slotbook\frontend"
curl.exe -s -f http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo        Frontend is already running.
) else (
    start "SlotBook Frontend [Port 3000]" cmd /k "node server.js 3000"
)

:: Waiting for services to be ready
echo.
echo  Waiting for all microservices to initialize...
set /a ATTEMPTS=0

:POLL_LOOP
set /a ATTEMPTS+=1
curl.exe -s -f http://localhost:8081/api/users/1 >nul 2>&1
if %errorlevel% neq 0 goto WAIT_A_BIT

curl.exe -s -f http://localhost:8082/api/resources >nul 2>&1
if %errorlevel% neq 0 goto WAIT_A_BIT

curl.exe -s -f http://localhost:8083/api/bookings/user/1 >nul 2>&1
if %errorlevel% equ 0 goto ALL_READY

:WAIT_A_BIT
if %ATTEMPTS% geq 35 (
    echo.
    echo  [WARNING] Startup taking longer than expected.
    goto OPEN_BROWSER
)
<nul set /p=.
timeout /t 1 /nobreak >nul
goto POLL_LOOP

:ALL_READY
echo.
echo  [OK] All 3 Microservices and Frontend are ready!

:OPEN_BROWSER
echo.
echo  Opening frontend in browser...
start "" "http://localhost:3000"

echo.
echo  ======================================================
echo   SlotBook Microservices Architecture is RUNNING!
echo   Frontend         : http://localhost:3000
echo   User Service     : http://localhost:8081
echo   Resource Service : http://localhost:8082
echo   Booking Service  : http://localhost:8083
echo  ======================================================
echo.
echo  Demo Credentials:
echo   - Admin   : admin@campus.edu  / admin123
echo   - Student : rahul@campus.edu  / password123
echo.
echo  To stop all services, run STOP_MICROSERVICES.bat
echo.
pause
