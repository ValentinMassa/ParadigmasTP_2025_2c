@echo off
REM Script para ejecutar TODOS los tests incluyendo Prolog
REM Autor: GitHub Copilot
REM Fecha: 25/11/2025

echo ========================================
echo   EJECUTANDO TESTS COMPLETOS (CON PROLOG)
echo ========================================
echo.

REM Configurar variables de entorno para SWI-Prolog
set PATH=C:\Program Files\swipl\bin;%PATH%
set SWI_HOME_DIR=C:\Program Files\swipl

echo [OK] PATH configurado con SWI-Prolog
echo [OK] SWI_HOME_DIR configurado
echo.

REM Limpiar bin/
echo Limpiando directorio bin/...
del /Q bin\* 2>nul
for /d %%i in (bin\*) do rmdir /S /Q "%%i" 2>nul
echo [OK] Directorio bin/ limpio
echo.

REM Compilar TODAS las clases fuente
echo Compilando clases fuente (src/)...
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" src\App.java src\Artista\*.java src\DataExport\*.java src\DataLoader\*.java src\DataLoader\Adapters\*.java src\Menu\*.java src\Menu\Auxiliares\*.java src\Recital\*.java src\Repositorios\*.java src\Servicios\*.java

if errorlevel 1 (
    echo [ERROR] Error al compilar clases fuente
    exit /b 1
)
echo [OK] Clases fuente compiladas exitosamente
echo.

REM Compilar tests desde src/test/java/Servicios/
echo Compilando tests...
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" src\test\java\Servicios\Funcionalidad01_RolesFaltantesPorCancionTest.java src\test\java\Servicios\Funcionalidad03_ContratarCancionEspecificaTest.java src\test\java\Servicios\Funcionalidad08_CalculoEntrenamientosPrologTest.java src\test\java\Servicios\Funcionalidad09_ArrepentimientoTest.java

if errorlevel 1 (
    echo [ERROR] Error al compilar tests
    exit /b 1
)
echo [OK] Tests compilados exitosamente
echo.

REM Ejecutar tests
echo ========================================
echo   EJECUTANDO TESTS
echo ========================================
echo.

java "-Djava.library.path=C:\Program Files\swipl\bin" -jar src\libs\junit-platform-console-standalone-1.10.1.jar --class-path "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" --scan-class-path --details=summary

echo.
if errorlevel 1 (
    echo ========================================
    echo   [X] ALGUNOS TESTS FALLARON
    echo ========================================
) else (
    echo ========================================
    echo   [OK] TODOS LOS TESTS COMPLETADOS
    echo ========================================
)
