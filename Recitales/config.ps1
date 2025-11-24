# Configuración de Variables para Prolog Portable

# Detectar automáticamente la instalación de SWI-Prolog
$swiPrologPaths = @(
    "C:\Program Files\swipl",
    "C:\Program Files (x86)\swipl",
    "$env:LOCALAPPDATA\Programs\swipl",
    "$PSScriptRoot\swipl"  # Instalación portable en el mismo directorio
)

$global:SWIPL_HOME = $null
$global:SWIPL_BIN = $null

foreach ($path in $swiPrologPaths) {
    if (Test-Path "$path\bin\jpl.dll") {
        $global:SWIPL_HOME = $path
        $global:SWIPL_BIN = "$path\bin"
        Write-Host "✓ SWI-Prolog encontrado en: $path" -ForegroundColor Green
        break
    }
}

if ($null -eq $global:SWIPL_HOME) {
    Write-Host "⚠ ADVERTENCIA: SWI-Prolog no encontrado" -ForegroundColor Yellow
    Write-Host "  El sistema no detectó una instalación estándar de SWI-Prolog." -ForegroundColor Yellow
    Write-Host "  Por favor, instale SWI-Prolog (64-bit) desde: https://www.swi-prolog.org/download/stable" -ForegroundColor Yellow
    Write-Host "  O configure la variable de entorno SWIPL_HOME apuntando a su instalación." -ForegroundColor Yellow
    Write-Host "  El proyecto funcionará pero sin integración Prolog" -ForegroundColor Yellow
} else {
    # Configurar variables de entorno temporalmente para esta sesión
    $env:SWIPL_HOME = $global:SWIPL_HOME
    $env:SWI_HOME_DIR = $global:SWIPL_HOME
    $env:Path = "$global:SWIPL_BIN;$env:Path"
    Write-Host "✓ Variables de entorno configuradas para esta sesión" -ForegroundColor Green
}

# Determinar qué jpl.jar usar
$global:JPL_JAR = "src/libs/jpl.jar"
if ($null -ne $global:SWIPL_HOME -and (Test-Path "$global:SWIPL_HOME\lib\jpl.jar")) {
    $global:JPL_JAR = "$global:SWIPL_HOME\lib\jpl.jar"
    Write-Host "✓ Usando librería JPL del sistema: $global:JPL_JAR" -ForegroundColor Green
}

# Función para compilar
function Compile-Project {
    Write-Host "`n═══════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  COMPILANDO PROYECTO" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan
    
    # Limpiar
    if (Test-Path bin) {
        Remove-Item -Recurse -Force bin
    }
    New-Item -ItemType Directory -Force -Path bin | Out-Null
    
    # Compilar
    $javaFiles = Get-ChildItem -Recurse -Filter "*.java" -Path src | Select-Object -ExpandProperty FullName
    javac -cp "src/libs/gson-2.13.1.jar;$global:JPL_JAR" -d bin -encoding UTF-8 $javaFiles
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Compilacion exitosa" -ForegroundColor Green
        return $true
    } else {
        Write-Host "✗ Error en compilación" -ForegroundColor Red
        return $false
    }
}

# Función para ejecutar
function Run-Project {
    Write-Host "`n═══════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  EJECUTANDO APLICACION" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════════`n" -ForegroundColor Cyan
    
    $javaCmd = "java"
    $args = @()
    
    if ($null -ne $global:SWIPL_BIN) {
        $args += "-Djava.library.path=$global:SWIPL_BIN"
    }
    
    $args += "--enable-native-access=ALL-UNNAMED"
    $args += "-cp"
    $args += "bin;src/libs/gson-2.13.1.jar;$global:JPL_JAR"
    $args += "App"
    
    & $javaCmd @args
}

# Exportar funciones (No necesario si se usa dot-sourcing, pero mantenemos compatibilidad si se convierte a modulo)
# Export-ModuleMember -Function Compile-Project, Run-Project
