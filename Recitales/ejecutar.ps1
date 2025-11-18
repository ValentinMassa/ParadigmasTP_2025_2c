# Script para compilar y ejecutar el proyecto de Recitales

$JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$JAVA_HOME\bin;$env:PATH"

# Configurar encoding UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Cambiar al directorio del script
Set-Location $PSScriptRoot

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Compilando proyecto..." -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan

javac -encoding UTF-8 -d bin src\App.java src\Imports\*.java src\Recital\*.java src\Recital\Artista\*.java src\Recital\Banda\*.java src\Recital\Rol\*.java src\Recital\Contratos\*.java src\Recital\Menu\*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Falló la compilación" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Green
Write-Host "Ejecutando programa..." -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green
Write-Host ""

java -Dfile.encoding=UTF-8 -cp bin App

Write-Host ""
Read-Host "Presiona Enter para salir"
