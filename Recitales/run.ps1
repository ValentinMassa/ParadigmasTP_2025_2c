# Script PowerShell para compilar y ejecutar el proyecto con Prolog de forma portable
# Solo ejecuta: .\run.ps1

# Cargar configuración
. "$PSScriptRoot\config.ps1"

# Compilar
$compiled = Compile-Project

if (-not $compiled) {
    Write-Host "`nPresiona Enter para salir..." -ForegroundColor Yellow
    Read-Host
    exit 1
}

# Ejecutar
Run-Project

Write-Host "`n═══════════════════════════════════════" -ForegroundColor Cyan
Write-Host "Presiona Enter para salir..." -ForegroundColor Yellow
Read-Host

