@echo off
REM Script para compilar y ejecutar el proyecto con Prolog de forma portable

echo ========================================
echo Compilando proyecto con Prolog...
echo ========================================

REM Limpiar compilaciones anteriores
if exist bin rmdir /s /q bin
mkdir bin

REM Compilar
javac -cp "src/libs/gson-2.13.1.jar;src/libs/jpl.jar" -d bin -encoding UTF-8 src\*.java src\Artista\*.java src\DataExport\*.java src\DataLoader\*.java src\DataLoader\Adapters\*.java src\Menu\*.java src\Menu\Auxiliares\*.java src\Recital\*.java src\Repositorios\*.java src\Servicios\*.java

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo ERROR: Falló la compilación
    echo ========================================
    pause
    exit /b 1
)

REM Crear directorio para archivos Prolog
if not exist bin\ArchivosImport mkdir bin\ArchivosImport

REM Copiar archivo Prolog si existe en src
if exist src\ArchivosImport\entrenamientos.pl (
    copy src\ArchivosImport\entrenamientos.pl bin\ArchivosImport\entrenamientos.pl > nul
)

echo.
echo ========================================
echo Compilación exitosa!
echo Ejecutando aplicación...
echo ========================================
echo.

REM Agregar SWI-Prolog al PATH temporalmente
set "PATH=C:\Program Files\swipl\bin;%PATH%"
set "SWI_HOME_DIR=C:\Program Files\swipl"

REM Ejecutar con el path de SWI-Prolog incluido
java -Djava.library.path="C:\Program Files\swipl\bin" -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" App

pause
