# Recitales 🎵

Proyecto de gestión y optimización de contratación de artistas para recitales.

---

## Integrantes

- Valentín Massa — vmassa@alumno.unlam.edu.ar — DNI 44510875
- María del Pilar Bourdieu — mbourdieu653@alumno.unlam.edu.ar — DNI 45289653

---

## Funcionalidades destacadas

### Gestión de Snapshots y Carga de Estado Previo

El sistema incluye funcionalidades avanzadas para guardar y restaurar el estado completo del recital, permitiendo una experiencia de usuario fluida y segura.

- **Hacer Snapshot Completo**: Permite guardar el estado actual del recital (artistas contratados, contratos activos, estadísticas, etc.) en un archivo JSON. Los snapshots se almacenan automáticamente en la carpeta `data/Snapshots/` con un nombre que incluye fecha y hora (ej: `snapshot_completo_25_11_2025_14_30_15.json`). Esta funcionalidad es útil para:
  - Crear puntos de respaldo antes de realizar cambios masivos.
  - Compartir estados entre sesiones de desarrollo.
  - Recuperarse de errores o experimentos fallidos.

- **Cargar Estado Previo**: Permite seleccionar y cargar un snapshot guardado previamente desde `data/Snapshots/`. Al cargar un snapshot, el sistema restaura completamente el estado del recital, incluyendo repositorios de artistas, contratos y estadísticas. Esto es ideal para:
  - Continuar trabajando desde un estado específico.
  - Probar escenarios alternativos sin perder progreso.
  - Recuperar datos después de un cierre inesperado.

**Nota**: Los archivos de salida del sistema (como exportaciones finales del recital) se guardan en `data/Output/`. Asegúrate de que estas carpetas existan o sean creadas automáticamente por el sistema.

---

## Estructura del proyecto (visual y detallada)

La siguiente representación está pensada para ser clara y fácil de leer en el informe; muestra carpetas clave, paquetes y archivos representativos.

```text
Recitales/ 🎵
├─ Prolog/
│  └─ entrenamientos.pl               # reglas y predicados (min_trainings/1, rol_faltante/1)
├─ data/
│  ├─ Json/
│  │  ├─ artistas.json
│  │  └─ recital.json
│  ├─ Output/
│  │  └─ <archivos-json-de-salida>
│  ├─ Snapshots/
│  │  └─ snapshot_completo_*.json
│  └─ XML/
│     ├─ artistas.xml
│     └─ recital.xml
├─ bin/                               # ejecutables / build output (si aplica)
├─ src/
│  ├─ App.java                        # entry point
│  ├─ Artista/
│  │  ├─ Artista.java
│  │  ├─ ArtistaDiscografica.java
│  │  └─ ArtistaExterno.java
│  ├─ Recital/
│  │  ├─ Cancion.java
│  │  ├─ Rol.java
│  │  ├─ Banda.java
│  │  ├─ Contrato.java
│  │  └─ Recital.java
│  ├─ Servicios/
│  │  ├─ ServicioConsulta.java
│  │  ├─ ServicioContratacion.java
│  │  └─ ServicioProlog.java          # integra JPL / orquesta consultas
│  ├─ DataLoader/
│  │  ├─ FabricaRecital.java
│  │  ├─ ICargarRecital.java
│  │  └─ JsonLoaderEstadoPrevio.java  # snapshot / carga previa
│  ├─ DataExport/
│  │  ├─ ExportadorRecital.java
│  │  └─ ExportadorSnapshotCompleto.java
│  ├─ Repositorios/
│  │  ├─ RepositorioArtistas.java
│  │  └─ RepositorioRecitales.java
│  ├─ Menu/
│  │  ├─ MenuPrincipal.java
│  │  ├─ Comando.java
│  │  ├─ ComandoContratarArtistas.java
│  │  └─ ComandoHacerSnapshot.java
│  └─ libs/                           # dependencias jar (jpl.jar, gson.jar...)
├─ src/test/                          # pruebas JUnit
│  └─ java/
│     └─ (tests unitarios e integración)
├─ scripts/
│  ├─ ejecutar-tests-completo.ps1
│  ├─ ejecutar-tests-completo.bat
│  ├─ ejecutar-test-simple.ps1
│  ├─ run.ps1
│  ├─ run-with-prolog.ps1
│  ├─ EjecutarMain.bat
└─ README.md

```

Consejos rápidos:

- `Prolog/` debe distribuirse junto con la release si se esperan tests/funcionalidades basadas en JPL.
- `src/libs/jpl.jar` en el classpath y la carpeta nativa (`.../swipl/bin`) en `java.library.path` o `PATH`.
- Mantener `DataLoader` y `DataExport` desacoplados para facilitar añadir nuevos formatos (CSV, DB).
- Tests de integración que usan SWI‑Prolog marcar con `@Tag("integration")` y ejecutarlos en un job CI separado.

---

## Requisitos previos

- JDK (recomendado 11+; el proyecto puede configurarse a nivel de compilador 21 en IDEs si se desea).
- SWI‑Prolog (si querés usar la integración lógica). Ruta típica en Windows: `C:\Program Files\swipl`.
- JARs en `src/libs` (por ejemplo `gson`, `jpl.jar`, `junit` si corres tests manualmente).

---

## Cómo ejecutar (Windows)

1) PowerShell — modo general (compila y ejecuta):

```powershell
cd Recitales
# Permitir ejecución temporal si es necesario
powershell -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process -Force"
.
\./scripts/run.ps1
```

2) PowerShell — ejecutar con configuración completa para SWI‑Prolog:

```powershell
cd Recitales
\./scripts/run-with-prolog.ps1
```

Este script configura `PATH`, `SWI_HOME_DIR` y `java.library.path` antes de lanzar la app, por lo que es la forma recomendada cuando querés usar la capa Prolog.

3) CMD / Batch (rápido):

```bat
cd Recitales
scripts\EjecutarMain.bat
```

4) Ejecución desde Eclipse

- Importar proyecto: `File → Import → Existing Java Project` (o añadir carpeta existente).
- Añadir JARs a `Build Path`: `Right-click project → Build Path → Configure Build Path → Libraries → Add JARs...` → seleccionar los jars en `src/libs`.
- Para `jpl.jar`, configurar la ubicación de la librería nativa: en `jpl.jar` seleccionar `Native library location` y poner la carpeta de `bin` de SWI‑Prolog (ej. `C:\Program Files\swipl\bin`).
- Run Configuration → Environment: añadir `SWI_HOME_DIR = C:\Program Files\swipl` y opcionalmente añadir `PATH` con la ruta al `bin` de SWI.
- VM arguments → añadir:

```
-Djava.library.path="C:\Program Files\swipl\bin"
```

- Ajustar nivel de compilador si corresponde: `Project → Properties → Java Compiler → Compiler compliance level = 21` (o la versión que uses).

Notas: si Eclipse no encuentra `jpl.dll` / `libswipl.dll`, verificar que la carpeta `bin` de SWI esté en `PATH` y que `-Djava.library.path` apunte a esa carpeta.

---

## Guía paso a paso para ejecutar el programa

Sigue estos pasos para ejecutar el sistema de gestión de recitales desde cero. Asegúrate de tener JDK instalado y, opcionalmente, SWI-Prolog para funcionalidades avanzadas.

### Paso 1: Preparar el entorno
1. Abre una terminal (PowerShell o CMD) en Windows.
2. Navega al directorio del proyecto:
   ```bash
   cd C:\Users\[TuUsuario]\Desktop\TODO_VALEN\REPOSITORIO_GITHUB\ParadigmasTP_2025_2c\Recitales
   ```
   (Reemplaza `[TuUsuario]` con tu nombre de usuario real).

### Paso 2: Ejecutar el programa
1. Una vez en el directorio `Recitales`, ejecuta el script de lanzamiento:
   - **Opción recomendada (con Prolog)**: 
     ```powershell
     .\scripts\launcher.ps1
     ```
     Esto compila el proyecto, configura SWI-Prolog si está disponible, y ejecuta la aplicación.
   
   - **Opción alternativa (sin Prolog)**:
     ```batch
     scripts\EjecutarMain.bat
     ```
     Compila y ejecuta sin configuración adicional de Prolog.

2. Si es la primera ejecución, el script descargará dependencias automáticamente si es necesario.

### Paso 3: Interactuar con el programa
1. El programa mostrará un menú principal con opciones numeradas.
2. Selecciona la opción deseada escribiendo el número correspondiente y presionando Enter.
3. Para funcionalidades como "Hacer Snapshot" o "Cargar Estado Previo", los archivos se guardarán/cargarán automáticamente desde `data/Snapshots/` y `data/Output/`.
4. Para salir, selecciona la opción de salida en el menú (generalmente la última opción).

### Paso 4: Verificar resultados
- Los snapshots se guardan en `data/Snapshots/` (archivos JSON con timestamp).
- Las salidas finales se guardan en `data/Output/` (archivos JSON de exportación).
- Si usas Prolog, asegúrate de que SWI-Prolog esté instalado para cálculos de entrenamientos.

### Notas importantes
- Si encuentras errores de "ruta no encontrada", verifica que estés ejecutando desde el directorio `Recitales`.
- Para SWI-Prolog, instala desde https://www.swi-prolog.org/download/stable y reinicia la terminal.
- Los tests se pueden ejecutar con `.\scripts\launcher.ps1` seleccionando la opción 2.

---

## Tests y Validación 🧪

Se implementó una suite de pruebas automatizadas con **JUnit 5** que cubre la lógica crítica y la integración con Prolog. Los tests se dividen en:

- Unitarios: rápidos, aislados, no dependen de SWI‑Prolog (mockean la interfaz hacia JPL/ServicioProlog).
- Integración: requieren SWI‑Prolog y JPL nativo — marcados con `@Tag("integration")` o ejecutados mediante scripts especiales.

Cobertura destacada:

- **Contratación**: `testDescuentoPorBandaCompartida`, `testOptimizacionPorCosto` — priorización por costo y aplicación correcta del 50% de descuento cuando corresponde.
- **Casos límite**: canciones sin roles faltantes; artistas con `maxCanciones` alcanzado; entradas nulas/vacías.
- **Entrenamiento**: `testEntrenamientoYRecontratacion` — al entrenar, un artista queda inmediatamente elegible para nuevos roles.
- **Integración Prolog**: tests que verifican `min_trainings/1` y `rol_faltante/1` en escenarios controlados; unitarios mockean JPL para independencia.

Cómo ejecutar los tests con los scripts incluidos:

```powershell
cd Recitales
# Ejecuta TODOS los tests, incluyendo los que usan Prolog (requiere SWI-Prolog instalado)
.\scripts\ejecutar-tests-completo.ps1

# O desde CMD:
.\scripts\ejecutar-tests-completo.bat

# Test rápido / desarrollo iterativo (PowerShell)
.\scripts\ejecutar-test-simple.ps1
```

Alternativa con Maven/Gradle:

```powershell
# Maven (unit + integration si SWI disponible)
.\mvnw.cmd test

# Solo unitarios (si integraciones están etiquetadas):
.\mvnw.cmd -DskipITs test

# Gradle:
.\gradlew.bat test
```

Notas para tests que usan Prolog
- SWI‑Prolog instalado y la librería nativa (`jpl.dll`) accesible en `java.library.path` o `PATH`.
- Recomendación: marcar los tests que dependen de Prolog como integración y ejecutarlos en CI en un job separado que instale SWI‑Prolog.
- Para unit tests usar mocks (Mockito) sobre `ServicioProlog` o la capa que llama JPL.

Medición de cobertura
- Recomendamos integrar **JaCoCo** para medir cobertura y publicar el badge en el `README`.

---

## Scripts incluidos

 - `scripts\ejecutar-tests-completo.bat` / `.ps1` — wrappers portables que delegan al script raíz o usan mvn/gradle.
 - `scripts\ejecutar-test-simple.ps1` — wrapper para un test rápido.
 - `scripts\run.ps1` — wrapper portable que delega al `run.ps1` de la raíz.
 - `scripts\run-with-prolog.ps1` — wrapper que configura SWI‑Prolog y delega al script de la raíz.
 - `scripts\EjecutarMain.bat` — wrapper batch que delega al `EjecutarMain.bat` de la raíz.

---

## Requisitos específicos para Prolog / JPL

- Instalar SWI‑Prolog 8+ compatible con la versión de `jpl.jar` incluida.
- Asegurarse de que la carpeta `bin` de SWI esté en `PATH` o pasarla en `-Djava.library.path`.
- Mantener `jpl.jar` en el classpath (ej. `src/libs/jpl.jar`) y configurar la `Native library location` cuando se trabaje desde IDE.

---

## Conclusión

El desarrollo de este sistema fue más que una entrega: fue una experiencia de diseño. La combinación de POO y razonamiento lógico mostró que:

- Java aporta control, estructura y gestión de estado (snapshots, persistencia, patrones).
- Prolog aporta expresividad para reglas e inferencia, resolviendo con claridad problemas de brechas de habilidades que en Java serían verbosos.
- La arquitectura por capas y patrones (Factory, Adapter, Repository, Command) permitió evolucionar el sistema con cambios mínimos sobre la base existente.

Como resultado, el proyecto no sólo cumple con los requisitos funcionales (optimización de contrataciones, respeto de restricciones y trazabilidad), sino que deja una base sólida para escalar: CI con tests de integración Prolog, mejoras heurísticas y una interfaz más rica. En pocas palabras: de solución puntual a plataforma extensible. 🎯

---

## Contacto

Para dudas o ejecución en entornos específicos, escribir a:
- vmassa@alumno.unlam.edu.ar
