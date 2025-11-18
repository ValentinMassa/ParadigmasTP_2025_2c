@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"

echo ==========================================
echo Compilando proyecto...
echo ==========================================
javac -encoding UTF-8 -d bin src\App.java src\Imports\*.java src\Recital\*.java src\Recital\Artista\*.java src\Recital\Banda\*.java src\Recital\Rol\*.java src\Recital\Contratos\*.java src\Recital\Menu\*.java

if errorlevel 1 (
    echo.
    echo ERROR: Falló la compilación
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Ejecutando programa...
echo ==========================================
echo.
java -Dfile.encoding=UTF-8 -cp bin App

pause
