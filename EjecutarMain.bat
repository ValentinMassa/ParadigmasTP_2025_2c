@echo off
setlocal enabledelayedexpansion
REM Script portable para compilar y ejecutar el proyecto Java con integración Prolog
REM Verifica dependencias mínimas y configura automáticamente

echo.
echo ========================================
echo   VERIFICANDO DEPENDENCIAS
echo ========================================
echo.

REM Verificar JDK
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] JDK no encontrado en PATH.
    echo.
    echo Por favor, instala Java JDK (version 11 o superior) y asegurate de que 'java' y 'javac' esten en PATH.
    echo Descarga desde: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Compilador Java (javac) no encontrado en PATH.
    echo.
    echo Asegurate de que el JDK completo este instalado y en PATH.
    echo.
    pause
    exit /b 1
)

echo [OK] JDK encontrado

REM Detectar SWI-Prolog
set "SWIPL_HOME="
set "SWIPL_BIN="
set "JPL_JAR=src/libs/jpl.jar"

for %%d in ("C:\Program Files\swipl" "C:\Program Files (x86)\swipl" "%LOCALAPPDATA%\Programs\swipl" "%~dp0swipl") do (
    if exist "%%~d\bin\jpl.dll" (
        set "SWIPL_HOME=%%~d"
        set "SWIPL_BIN=%%~d\bin"
        goto swipl_found
    )
)

:swipl_found
if defined SWIPL_HOME (
    echo [OK] SWI-Prolog encontrado en: !SWIPL_HOME!
    if exist "!SWIPL_HOME!\lib\jpl.jar" (
        set "JPL_JAR=!SWIPL_HOME!\lib\jpl.jar"
        echo [OK] Usando libreria JPL del sistema
    )
) else (
    echo [WARNING] SWI-Prolog no encontrado
    echo El sistema funcionara pero sin integracion Prolog
    echo.
    echo Instala SWI-Prolog desde: https://www.swi-prolog.org/download/stable
)

REM Verificar librerias JAR
if not exist "src/libs/gson-2.13.1.jar" (
    echo [ERROR] gson-2.13.1.jar no encontrado en src/libs/
    echo.
    echo Descarga gson desde: https://github.com/google/gson/releases
    echo.
    pause
    exit /b 1
)

if not exist "%JPL_JAR%" (
    echo [ERROR] jpl.jar no encontrado
    echo.
    if defined SWIPL_HOME (
        echo Buscado en: !SWIPL_HOME!\lib\jpl.jar
    ) else (
        echo Buscado en: src/libs/jpl.jar
    )
    echo.
    pause
    exit /b 1
)

echo [OK] Librerias JAR encontradas

echo.
echo ========================================
echo   COMPILANDO PROYECTO
echo ========================================
echo.

REM Limpiar compilaciones anteriores
if exist bin rmdir /s /q bin >nul 2>&1
mkdir bin

REM Compilar recursivamente
for /r "src" %%f in (*.java) do (
    javac -cp "src/libs/gson-2.13.1.jar;%JPL_JAR%" -d bin -encoding UTF-8 "%%f"
    if !errorlevel! neq 0 goto compile_error
)

echo.
echo [OK] Compilacion exitosa

REM Copiar archivos Prolog si existen
if exist "src\ArchivosImport" (
    xcopy "src\ArchivosImport" "bin\ArchivosImport\" /s /i /q >nul 2>&1
)

echo.
echo ========================================
echo   EJECUTANDO APLICACION
echo ========================================
echo.

REM Configurar opciones Java
set "JAVA_OPTS="
if defined SWIPL_BIN (
    set "JAVA_OPTS=-Djava.library.path=%SWIPL_BIN%"
)

REM Ejecutar
java %JAVA_OPTS% --enable-native-access=ALL-UNNAMED -cp "bin;src/libs/gson-2.13.1.jar;%JPL_JAR%" App

echo.
echo ========================================
echo   FIN DE EJECUCION
echo ========================================
echo.

pause
exit /b 0

:compile_error
echo.
echo ========================================
echo   ERROR EN COMPILACION
echo ========================================
echo.
echo Revisa los errores arriba y corrige los problemas.
echo.
pause
exit /b 1
