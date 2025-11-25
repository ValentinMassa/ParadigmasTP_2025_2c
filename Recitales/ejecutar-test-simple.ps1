#!/usr/bin/env pwsh
# Script para compilar y ejecutar el test de ServicioContratacion

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Compilando y Ejecutando Tests JUnit" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que JUnit existe
$junitJar = "src/libs/junit-platform-console-standalone-1.10.1.jar"
if (-not (Test-Path $junitJar)) {
    Write-Host "Descargando JUnit 5..." -ForegroundColor Yellow
    $url = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar"
    New-Item -ItemType Directory -Force -Path "src/libs" | Out-Null
    Invoke-WebRequest -Uri $url -OutFile $junitJar
    Write-Host "JUnit descargado!" -ForegroundColor Green
    Write-Host ""
}

# Crear directorio bin si no existe
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compilando código principal..." -ForegroundColor Yellow

# Compilar todas las clases principales
$sourceFiles = @(
    "src/Recital/*.java",
    "src/Artista/*.java", 
    "src/Repositorios/*.java",
    "src/DataLoader/*.java",
    "src/DataLoader/Adapters/*.java",
    "src/DataExport/*.java",
    "src/Menu/Auxiliares/*.java",
    "src/Servicios/*.java",
    "src/Menu/*.java"
)

javac -encoding UTF-8 -d bin -cp "src/libs/*" $sourceFiles 2>&1 | Out-String | Write-Host

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error en compilación del código principal" -ForegroundColor Red
    exit 1
}

Write-Host "Código principal compilado OK!" -ForegroundColor Green
Write-Host ""
Write-Host "Compilando tests..." -ForegroundColor Yellow

# Compilar el test compacto
javac -encoding UTF-8 -d bin -cp "bin;src/libs/*" "src/test/java/Servicios/ServicioContratacionCompactoTest.java" 2>&1 | Out-String | Write-Host

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error en compilación de tests" -ForegroundColor Red
    exit 1
}

Write-Host "Tests compilados OK!" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Ejecutando Tests" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Ejecutar los tests con JUnit
java -jar $junitJar --class-path bin --select-class test.java.Servicios.ServicioContratacionCompactoTest --details=verbose

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Tests Completados!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
