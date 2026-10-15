@echo off
setlocal
set "MVN_DIST=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.3-bin\326f10f4\apache-maven-3.9.3\bin\mvn.cmd"
if exist "%MVN_DIST%" (
    call "%MVN_DIST%" %*
    exit /b %ERRORLEVEL%
)
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    call mvn %*
    exit /b %ERRORLEVEL%
)
echo [ERROR] Maven not found. Please ensure Java and Maven are installed.
exit /b 1
