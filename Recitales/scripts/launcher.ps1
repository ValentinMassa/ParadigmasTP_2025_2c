# Launcher unificado para Recitales
# Combina ejecución de programa y pruebas en un menú interactivo
# Portable: usa rutas relativas para ejecutarse desde cualquier ubicación

# Configuración de Variables para Prolog Portable
$swiPrologPaths = @(
    "C:\Program Files\swipl",
    "C:\Program Files (x86)\swipl",
    "$env:LOCALAPPDATA\Programs\swipl",
    "$PSScriptRoot\..\swipl"  # Instalación portable en el mismo directorio
)

$global:SWIPL_HOME = $null
$global:SWIPL_BIN = $null

foreach ($path in $swiPrologPaths) {
    if (Test-Path "$path\bin\jpl.dll") {
        $global:SWIPL_HOME = $path
        $global:SWIPL_BIN = "$path\bin"
        Write-Host "[OK] SWI-Prolog encontrado en: $path" -ForegroundColor Green
        break
    }
}

if ($null -eq $global:SWIPL_HOME) {
    Write-Host "[WARN] SWI-Prolog no encontrado" -ForegroundColor Yellow
    Write-Host "  El sistema no detecto una instalacion estandar de SWI-Prolog." -ForegroundColor Yellow
    Write-Host "  Por favor, instale SWI-Prolog (64-bit) desde: https://www.swi-prolog.org/download/stable" -ForegroundColor Yellow
    Write-Host "  O configure la variable de entorno SWIPL_HOME apuntando a su instalacion." -ForegroundColor Yellow
    Write-Host "  El proyecto funcionara pero sin integracion Prolog" -ForegroundColor Yellow
} else {
    # Configurar variables de entorno temporalmente para esta sesión
    $env:SWIPL_HOME = $global:SWIPL_HOME
    $env:SWI_HOME_DIR = $global:SWIPL_HOME
    $env:Path = "$global:SWIPL_BIN;$env:Path"
    Write-Host "[OK] Variables de entorno configuradas para esta sesion" -ForegroundColor Green
}

# Determinar qué jpl.jar usar
$global:JPL_JAR = "$PSScriptRoot\..\src\libs\jpl.jar"
if ($null -ne $global:SWIPL_HOME -and (Test-Path "$global:SWIPL_HOME\lib\jpl.jar")) {
    $global:JPL_JAR = "$global:SWIPL_HOME\lib\jpl.jar"
    Write-Host "[OK] Usando libreria JPL del sistema: $global:JPL_JAR" -ForegroundColor Green
}

# Función para compilar proyecto (fuentes principales, excluyendo tests)
function Compile-Project {
    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host "  COMPILANDO PROYECTO" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan
    
    # Limpiar
    if (Test-Path "$PSScriptRoot\..\bin") {
        Remove-Item -Recurse -Force "$PSScriptRoot\..\bin"
    }
    New-Item -ItemType Directory -Force -Path "$PSScriptRoot\..\bin" | Out-Null
    
    # Compilar código principal (sin incluir tests)
    $javaFiles = Get-ChildItem -Recurse -Filter "*.java" -Path "$PSScriptRoot\..\src" | Where-Object { $_.FullName -notlike "*\src\test\*" } | Select-Object -ExpandProperty FullName
    Write-Host "-> Archivos fuente encontrados: $($javaFiles.Count)" -ForegroundColor Gray
    
    $javacArgs = @("-cp", "$PSScriptRoot\..\src\libs\gson-2.13.1.jar;$global:JPL_JAR", "-d", "$PSScriptRoot\..\bin", "-encoding", "UTF-8") + $javaFiles
    & javac $javacArgs
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Compilacion exitosa" -ForegroundColor Green
        return $true
    } else {
        Write-Host "[ERROR] Error en compilacion" -ForegroundColor Red
        return $false
    }
}

# Función para ejecutar aplicación
function Run-Project {
    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host "  EJECUTANDO APLICACION" -ForegroundColor Cyan
    Write-Host "=======================================`n" -ForegroundColor Cyan
    
    $javaArgs = @()
    
    if ($null -ne $global:SWIPL_BIN) {
        $javaArgs += "-Djava.library.path=$global:SWIPL_BIN"
    }
    
    $javaArgs += "--enable-native-access=ALL-UNNAMED"
    $javaArgs += "-cp"
    $javaArgs += "$PSScriptRoot\..\bin;$PSScriptRoot\..\src\libs\gson-2.13.1.jar;$global:JPL_JAR"
    $javaArgs += "App"
    
    & java $javaArgs
}

# Función para compilar tests
function Compile-Tests {
    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host "  COMPILANDO TESTS" -ForegroundColor Cyan
    Write-Host "=======================================" -ForegroundColor Cyan
    
    # Compilar tests (asumiendo que fuentes ya están compiladas)
    $testFiles = Get-ChildItem -Recurse -Filter "*.java" -Path "$PSScriptRoot\..\src\test\java" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName
    Write-Host "-> Archivos de test encontrados: $($testFiles.Count)" -ForegroundColor Gray
    
    if ($testFiles.Count -gt 0) {
        $javacTestArgs = @("-cp", "$PSScriptRoot\..\bin;$PSScriptRoot\..\src\libs\junit-platform-console-standalone-1.10.1.jar;$PSScriptRoot\..\src\libs\gson-2.13.1.jar;$global:JPL_JAR", "-d", "$PSScriptRoot\..\bin", "-encoding", "UTF-8") + $testFiles
        & javac $javacTestArgs
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Tests compilados exitosamente" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[ERROR] Error al compilar tests" -ForegroundColor Red
            return $false
        }
    } else {
        Write-Host "[WARN] No se encontraron archivos de test" -ForegroundColor Yellow
        return $false
    }
}

# Función para ejecutar tests
function Run-Tests {
    Write-Host "`n=======================================" -ForegroundColor Cyan
    Write-Host "  EJECUTANDO TESTS" -ForegroundColor Cyan
    Write-Host "=======================================`n" -ForegroundColor Cyan
    
    & java "-Djava.library.path=$global:SWIPL_BIN" `
         -jar "$PSScriptRoot\..\src\libs\junit-platform-console-standalone-1.10.1.jar" `
         --class-path "$PSScriptRoot\..\bin;$PSScriptRoot\..\src\libs\gson-2.13.1.jar;$global:JPL_JAR" `
         --scan-class-path `
         --details=summary
}

# Menú principal
Write-Host "`n=======================================" -ForegroundColor Cyan
Write-Host "  LAUNCHER RECITALES" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Selecciona una opcion:" -ForegroundColor Yellow
Write-Host "1) Ejecutar programa" -ForegroundColor White
Write-Host "2) Ejecutar pruebas" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Ingresa tu eleccion (1 o 2)"

switch ($choice) {
    "1" {
        Write-Host "`nOpcion seleccionada: Ejecutar programa" -ForegroundColor Green
        if (Compile-Project) {
            Run-Project
        }
    }
    "2" {
        Write-Host "`nOpcion seleccionada: Ejecutar pruebas" -ForegroundColor Green
        if (Compile-Project) {
            if (Compile-Tests) {
                Run-Tests
            }
        }
    }
    default {
        Write-Host "`n[ERROR] Opcion invalida. Saliendo..." -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n=======================================" -ForegroundColor Cyan
Write-Host "Presiona Enter para salir..." -ForegroundColor Yellow
Read-Host