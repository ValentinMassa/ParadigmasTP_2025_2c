# Script para ejecutar el programa con soporte completo de SWI-Prolog
# Configura todas las variables de entorno necesarias

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Configurando entorno para SWI-Prolog + Java" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Configurar PATH para incluir SWI-Prolog bin (donde estan las DLLs)
$SWIPL_BIN = "C:\Program Files\swipl\bin"
$SWIPL_LIB = "C:\Program Files\swipl\lib"
$SWIPL_HOME = "C:\Program Files\swipl"

Write-Host "[1] Agregando SWI-Prolog al PATH..." -ForegroundColor Yellow
$env:PATH = "$SWIPL_BIN;$env:PATH"

Write-Host "[2] Configurando SWI_HOME_DIR..." -ForegroundColor Yellow
$env:SWI_HOME_DIR = $SWIPL_HOME

Write-Host "[3] Configurando variables JPL..." -ForegroundColor Yellow
$env:JPLJAR = "$SWIPL_LIB\jpl.jar"

Write-Host ""
Write-Host "[OK] Entorno configurado correctamente" -ForegroundColor Green
Write-Host "     - PATH incluye: $SWIPL_BIN" -ForegroundColor Gray
Write-Host "     - SWI_HOME_DIR: $env:SWI_HOME_DIR" -ForegroundColor Gray
Write-Host "     - JPLJAR: $env:JPLJAR" -ForegroundColor Gray
Write-Host ""

# 2. Verificar que jpl.dll existe
if (Test-Path "$SWIPL_BIN\jpl.dll") {
    Write-Host "[OK] jpl.dll encontrado" -ForegroundColor Green
} else {
    Write-Host "[ERROR] No se encuentra jpl.dll en $SWIPL_BIN" -ForegroundColor Red
    exit 1
}

# 3. Verificar que libswipl.dll existe
if (Test-Path "$SWIPL_BIN\libswipl.dll") {
    Write-Host "[OK] libswipl.dll encontrado" -ForegroundColor Green
} else {
    Write-Host "[ERROR] No se encuentra libswipl.dll en $SWIPL_BIN" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Ejecutando aplicacion Java" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# 4. Ejecutar Java con todas las configuraciones
$CLASSPATH = "bin;src/libs/*;$SWIPL_LIB\jpl.jar"
$JAVA_LIBRARY_PATH = $SWIPL_BIN

java "-Djava.library.path=$JAVA_LIBRARY_PATH" `
     -cp $CLASSPATH `
     App

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Fin de ejecucion" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
