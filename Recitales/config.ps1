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
    if (Test-Path "$path\bin\swipl.dll") {
        $global:SWIPL_HOME = $path
        $global:SWIPL_BIN = "$path\bin"
        Write-Host "✓ SWI-Prolog encontrado en: $path" -ForegroundColor Green
        break
    }
}

if ($null -eq $global:SWIPL_HOME) {
    Write-Host "⚠ ADVERTENCIA: SWI-Prolog no encontrado" -ForegroundColor Yellow
    Write-Host "  El proyecto funcionará pero sin integración Prolog" -ForegroundColor Yellow
} else {
    # Configurar variables de entorno temporalmente para esta sesión
    $env:SWIPL_HOME = $global:SWIPL_HOME
    $env:SWI_HOME_DIR = $global:SWIPL_HOME
    $env:Path = "$global:SWIPL_BIN;$env:Path"
    Write-Host "✓ Variables de entorno configuradas para esta sesión" -ForegroundColor Green
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
    javac -cp "src/libs/gson-2.13.1.jar;src/libs/jpl.jar" -d bin -encoding UTF-8 $javaFiles
    
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
    
    if ($null -ne $global:SWIPL_BIN) {
        java -Djava.library.path="$global:SWIPL_BIN" -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" App
    } else {
        java -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" App
    }
}

# Exportar funciones
Export-ModuleMember -Function Compile-Project, Run-Project
