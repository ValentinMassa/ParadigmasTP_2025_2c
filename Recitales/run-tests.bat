@echo off
echo ========================================
echo Ejecutando Tests JUnit
echo ========================================

REM Descargar JUnit si no existe
if not exist "src\libs\junit-platform-console-standalone-1.10.1.jar" (
    echo Descargando JUnit 5...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar' -OutFile 'src\libs\junit-platform-console-standalone-1.10.1.jar'"
    echo JUnit descargado!
)

REM Compilar el código principal
echo.
echo Compilando codigo principal...
javac -encoding UTF-8 -d bin -cp "src/libs/*" src/Recital/*.java src/Artista/*.java src/Repositorios/*.java src/DataLoader/*.java src/DataLoader/Adapters/*.java src/DataExport/*.java src/Menu/Auxiliares/*.java src/Servicios/*.java src/Menu/*.java

if errorlevel 1 (
    echo Error en compilacion del codigo principal
    pause
    exit /b 1
)

REM Compilar los tests
echo.
echo Compilando tests...
javac -encoding UTF-8 -d bin -cp "bin;src/libs/*" test/Servicios/*.java

if errorlevel 1 (
    echo Error en compilacion de tests
    pause
    exit /b 1
)

REM Ejecutar los tests
echo.
echo ========================================
echo Ejecutando todos los tests
echo ========================================
java -jar src/libs/junit-platform-console-standalone-1.10.1.jar --class-path bin --scan-class-path

echo.
echo ========================================
echo Tests completados!
echo ========================================
pause
