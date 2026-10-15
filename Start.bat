@echo off
setlocal EnableDelayedExpansion
title SecureBank NetBanking Runner

echo ===============================================================================
echo                SECUREBANK - ONLINE BANKING MANAGEMENT SYSTEM
echo ===============================================================================
echo.

:: 1. Check Java
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not detected in your PATH.
    echo Please install Java 21 or higher and add it to your PATH environment variable.
    pause
    exit /b 1
)

:: 2. Check Node.js
where node >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Node.js is not detected in your PATH.
    echo Please install Node.js and add it to your PATH environment variable.
    pause
    exit /b 1
)

set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "FRONTEND_DIR=%PROJECT_ROOT%frontend"

echo [1/3] Environment Verified:
echo       - Java detected
echo       - Node.js and npm detected
echo.

echo [2/3] Launching Spring Boot Backend on http://localhost:8080 ...
start "SecureBank Backend (Spring Boot)" cmd /k "cd /d ""%BACKEND_DIR%"" && call mvnw.cmd spring-boot:run"

echo [3/3] Launching Angular Frontend on http://localhost:4200 ...
start "SecureBank Frontend (Angular)" cmd /k "cd /d ""%FRONTEND_DIR%"" && npm start"

echo.
echo ===============================================================================
echo                SERVICES ARE LAUNCHING IN DEDICATED WINDOWS
echo ===============================================================================
echo.
echo  * Frontend Portal:      http://localhost:4200
echo  * Swagger API Docs:     http://localhost:8080/swagger-ui.html
echo  * H2 Database Console:  http://localhost:8080/h2-console
echo.
echo  SYNTHETIC DEMO CREDENTIALS:
echo  -------------------------------------------------------------
echo  Customer 1: Customer ID = customer101   Password = Password@123
echo  Customer 2: Customer ID = customer102   Password = Password@123
echo  -------------------------------------------------------------
echo.
echo  Leave the two opened command windows running while using the application.
echo ===============================================================================
echo.
pause
