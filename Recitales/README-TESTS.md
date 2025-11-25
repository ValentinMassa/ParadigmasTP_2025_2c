# 🧪 Guía de Ejecución de Tests

## ✅ Tests Disponibles: 30/30 (100%)

- **Funcionalidad 01** - Roles Faltantes por Canción: 5 tests
- **Funcionalidad 03** - Contratar Canción Específica: 10 tests
- **Funcionalidad 08** - Cálculo de Entrenamientos con Prolog: 7 tests ⚠️
- **Funcionalidad 09** - Arrepentimiento (Quitar Artista): 8 tests

---

## 🚀 Ejecución Rápida

### Opción 1: Script Automatizado (Windows)
```bash
.\ejecutar-tests-completo.bat
```

Este script:
- ✅ Configura automáticamente SWI-Prolog
- ✅ Limpia y recompila todo el proyecto
- ✅ Ejecuta los 30 tests
- ✅ Muestra resultados resumidos

---

## ⚙️ Requisitos Previos

### 1. JDK 17 o superior
Verifica tu versión:
```bash
java -version
```

### 2. SWI-Prolog (SOLO para Funcionalidad 08)
⚠️ **Si no tienes SWI-Prolog, los 7 tests de Funcionalidad 08 fallarán**

**Instalación de SWI-Prolog en Windows:**
1. Descarga desde: https://www.swi-prolog.org/Download.html
2. Instala en: `C:\Program Files\swipl`
3. Verifica instalación:
   ```bash
   swipl --version
   ```

**Ubicaciones esperadas:**
- Ejecutable: `C:\Program Files\swipl\bin\swipl.exe`
- Librería JPL: `C:\Program Files\swipl\bin\jpl.dll`

Si instalaste en otra ubicación, edita `ejecutar-tests-completo.bat` y cambia las rutas.

---

## 🔧 Ejecución Manual (Sin Script)

### Paso 1: Configurar Variables de Entorno
```powershell
$env:PATH = "C:\Program Files\swipl\bin;$env:PATH"
$env:SWI_HOME_DIR = "C:\Program Files\swipl"
```

### Paso 2: Limpiar Compilación Anterior
```powershell
Remove-Item -Recurse -Force bin\*
```

### Paso 3: Compilar Código Fuente
```bash
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" src\App.java src\Artista\*.java src\DataExport\*.java src\DataLoader\*.java src\DataLoader\Adapters\*.java src\Menu\*.java src\Menu\Auxiliares\*.java src\Recital\*.java src\Repositorios\*.java src\Servicios\*.java
```

### Paso 4: Compilar Tests
```bash
javac -encoding UTF-8 -d bin -cp "bin;src/libs/junit-platform-console-standalone-1.10.1.jar;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" src\test\java\Servicios\Funcionalidad01_RolesFaltantesPorCancionTest.java src\test\java\Servicios\Funcionalidad03_ContratarCancionEspecificaTest.java src\test\java\Servicios\Funcionalidad08_CalculoEntrenamientosPrologTest.java src\test\java\Servicios\Funcionalidad09_ArrepentimientoTest.java
```

### Paso 5: Ejecutar Tests
```bash
java "-Djava.library.path=C:\Program Files\swipl\bin" -jar src\libs\junit-platform-console-standalone-1.10.1.jar --class-path "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" --scan-class-path --details=summary
```

---

## 🐛 Solución de Problemas

### Error: "no jpl in java.library.path"
**Causa:** No se encuentra la librería nativa de Prolog.

**Solución:**
1. Verifica que SWI-Prolog esté instalado: `swipl --version`
2. Verifica que existe el archivo: `C:\Program Files\swipl\bin\jpl.dll`
3. Configura la variable de entorno:
   ```bash
   set PATH=C:\Program Files\swipl\bin;%PATH%
   ```

### Error: "Could not find system resources"
**Causa:** Falta la variable `SWI_HOME_DIR`.

**Solución:**
```bash
set SWI_HOME_DIR=C:\Program Files\swipl
```

### Error: "ClassNotFoundException: org/jpl7/PrologException"
**Causa:** Falta `jpl.jar` en el classpath.

**Solución:**
Verifica que el comando incluya: `-cp "..;src/libs/jpl.jar"`

### Error de compilación en ServicioContratacion
**Causa:** Puede que el código fuente esté desactualizado.

**Solución:**
```bash
git pull origin PiluYValen
```

### Tests de Funcionalidad 08 fallan con NoClassDefFoundError
**Solución:** Simplemente no tienes SWI-Prolog instalado. Los otros 23 tests (01, 03, 09) funcionarán perfectamente.

---

## 📂 Estructura de Tests

Los tests están ubicados en:
```
src/test/java/Servicios/
├── Funcionalidad01_RolesFaltantesPorCancionTest.java (426 líneas)
├── Funcionalidad03_ContratarCancionEspecificaTest.java (684 líneas)
├── Funcionalidad08_CalculoEntrenamientosPrologTest.java (636 líneas)
└── Funcionalidad09_ArrepentimientoTest.java (238 líneas)
```

**Nota:** Los archivos en `test/` raíz fueron eliminados porque estaban vacíos.

---

## 📊 Resultado Esperado

```
Test run finished after ~200-250 ms
[         7 containers found      ]
[         0 containers skipped    ]
[         7 containers started    ]
[         0 containers aborted    ]
[         7 containers successful ]
[         0 containers failed     ]
[        30 tests found           ]
[         0 tests skipped         ]
[        30 tests started         ]
[         0 tests aborted         ]
[        30 tests successful      ] ✅
[         0 tests failed          ]
```

---

## 💡 Consejos

1. **Sin Prolog:** Si no necesitas probar la Funcionalidad 08, simplemente ignora los 7 fallos. Los otros 23 tests funcionan sin Prolog.

2. **Eclipse IDE:** Si usas Eclipse, simplemente haz `Project → Clean` antes de ejecutar los tests desde el IDE.

3. **VS Code:** Usa el script `.bat` o los comandos manuales desde la terminal integrada.

4. **Git:** Siempre haz `git pull` antes de ejecutar tests para tener la última versión del código.

---

## 🆘 Contacto

Si tienes problemas, verifica:
1. ✅ Tienes JDK 17+
2. ✅ Estás en la carpeta `Recitales/`
3. ✅ Hiciste `git pull` reciente
4. ✅ (Opcional) Instalaste SWI-Prolog para Funcionalidad 08
