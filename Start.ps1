Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "               SECUREBANK - ONLINE BANKING MANAGEMENT SYSTEM" -ForegroundColor Yellow
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $projectRoot "backend"
$frontendDir = Join-Path $projectRoot "frontend"

Write-Host "[1/2] Launching Spring Boot Backend on http://localhost:8080 ..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir'; .\mvnw.cmd spring-boot:run"

Write-Host "[2/2] Launching Angular Frontend on http://localhost:4200 ..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendDir'; npm start"

Write-Host ""
Write-Host "===============================================================================" -ForegroundColor Green
Write-Host "                SERVICES LAUNCHED IN DEDICATED WINDOWS" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Green
Write-Host ""
Write-Host " * Frontend Portal:     http://localhost:4200" -ForegroundColor White
Write-Host " * Swagger API Docs:    http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host " * Database Console:    http://localhost:8080/h2-console" -ForegroundColor White
Write-Host ""
Write-Host " DEMO CREDENTIALS:" -ForegroundColor Yellow
Write-Host " Customer ID: customer101   Password: Password@123" -ForegroundColor White
Write-Host " Customer ID: customer102   Password: Password@123" -ForegroundColor White
Write-Host ""
Write-Host "Please wait ~10 seconds for both services to finish compiling, then open:" -ForegroundColor Gray
Write-Host "http://localhost:4200" -ForegroundColor Cyan
