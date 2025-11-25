# Script para ejecutar TODOS los tests incluyendo Prolog
# Autor: GitHub Copilot
# Fecha: 25/11/2025

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  EJECUTANDO TESTS COMPLETOS (CON PROLOG)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar variables de entorno para SWI-Prolog
$env:PATH = "C:\Program Files\swipl\bin;$env:PATH"
$env:SWI_HOME_DIR = "C:\Program Files\swipl"

Write-Host "✓ PATH configurado con SWI-Prolog" -ForegroundColor Green
Write-Host "✓ SWI_HOME_DIR configurado" -ForegroundColor Green
Write-Host ""

# Limpiar bin/
Write-Host "Limpiando directorio bin/..." -ForegroundColor Yellow
Remove-Item -Recurse -Force bin\* -ErrorAction SilentlyContinue
Write-Host "✓ Directorio bin/ limpio" -ForegroundColor Green
Write-Host ""

# Compilar TODAS las clases fuente
Write-Host "Compilando clases fuente (src/)..." -ForegroundColor Yellow
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" (Get-ChildItem -Recurse -Filter *.java src/).FullName

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error al compilar clases fuente" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Clases fuente compiladas exitosamente" -ForegroundColor Green
Write-Host ""

# Compilar tests
Write-Host "Compilando tests..." -ForegroundColor Yellow
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" `
    test/Funcionalidad01_RolesFaltantesPorCancionTest.java `
    test/Funcionalidad03_ContratarCancionEspecificaTest.java `
    test/Funcionalidad08_CalculoEntrenamientosPrologTest.java `
    test/Funcionalidad09_ArrepentimientoTest.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error al compilar tests" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Tests compilados exitosamente" -ForegroundColor Green
Write-Host ""

# Ejecutar tests
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  EJECUTANDO TESTS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

java "-Djava.library.path=C:\Program Files\swipl\bin" `
     -jar src/libs/junit-platform-console-standalone-1.10.1.jar `
     --class-path "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" `
     --scan-class-path `
     --details=summary

Write-Host ""
if ($LASTEXITCODE -eq 0) {
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ TODOS LOS TESTS COMPLETADOS" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
} else {
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  ✗ ALGUNOS TESTS FALLARON" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}
