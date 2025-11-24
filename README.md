# Paradigmas de Programación — Trabajo Práctico N°2 (2025)

**Formación de Banda Temporal para Recital — Sistema de Contratación Óptima de Artistas**

Una discográfica necesita formar una "banda temporal" para un recital especial. Este repositorio contiene la implementación del Trabajo Práctico N°2 de la materia "Paradigmas de Programación" (Ciclo 2025), aplicando conceptos de **Programación Orientada a Objetos** para modelar y resolver la selección y contratación óptima de artistas, con integración de razonamiento automático mediante **Prolog**.

---

## 📋 Índice

1. [Descripción General](#descripción-general)
2. [Fechas Clave](#fechas-clave)
3. [Objetivos](#objetivos)
4. [Problema y Solución](#problema-y-solución)
5. [Glosario](#glosario)
6. [Funcionalidades Principales](#funcionalidades-principales)
7. [Reglas de Negocio](#reglas-de-negocio)
8. [Formato de Datos](#formato-de-datos)
9. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
10. [Guía de Instalación y Ejecución](#guía-de-instalación-y-ejecución)
11. [Problemas Comunes y Soluciones](#problemas-comunes-y-soluciones)
12. [Cosas a Tener en Cuenta](#cosas-a-tener-en-cuenta)
13. [Pruebas y Validación](#pruebas-y-validación)
14. [Integración con Prolog](#integración-con-prolog)
15. [Requisitos Técnicos](#requisitos-técnicos)
16. [Bonus Opcionales](#bonus-opcionales)
17. [Entrega y Defensa](#entrega-y-defensa)
18. [Integrantes](#integrantes)
19. [Licencia](#licencia)

---

## 🎭 Descripción General

Este sistema permite planificar y optimizar la contratación de artistas externos para formar una banda temporal que interprete todas las canciones de un recital. El desafío es **minimizar costos** mientras se respetan restricciones del dominio: roles históricos, disponibilidad máxima de canciones por artista, relaciones de colaboración pasada, y la posibilidad de entrenar artistas en nuevos roles.

---

## 📅 Fechas Clave

| Hito | Fecha |
|------|-------|
| **Entrega Intermedia** (diagrama de clases) | 28 de Octubre de 2025 |
| **Entrega Final** | 18 y 25 de Noviembre de 2025 |
| **Defensa Oral** | Según cronograma |

---

## 🎯 Objetivos

### Objetivo General
Diseñar e implementar un sistema extensible y testeable que permita planificar la contratación óptima de artistas para un recital, aplicando principios de POO y optimización combinatoria.

### Objetivos Específicos
✓ Aplicar conceptos fundamentales de POO (clases, herencia, polimorfismo, interfaces) en un contexto real.  
✓ Analizar y modelar una problemática real mediante clases, relaciones y responsabilidades claras.  
✓ Diseñar un sistema extensible con bajo acoplamiento y alta cohesión.  
✓ Trabajar con colecciones, estructuras dinámicas y archivos externos (JSON/XML).  
✓ Implementar pruebas automatizadas con cobertura adecuada (JUnit).  
✓ Integrar razonamiento en Prolog para consultas específicas sobre entrenamientos.  
✓ Cumplir el principio abierto/cerrado: agregar artistas y canciones sin modificar código existente.

---

## 🎵 Problema y Solución

### Contexto
Una discográfica cuenta con:
- **Artistas base**: contratados permanentemente (costo 0 o bajo)
- **Artistas candidatos**: disponibles a contratar (con costo variable)
- **Recital**: conjunto de canciones que requieren roles específicos

### Desafío
Seleccionar y contratar artistas externos de manera que:
1. Se cubran **todos los roles** de todas las canciones
2. Se **minimice el costo total**
3. Se respeten las restricciones: roles históricos, máximo de canciones por artista, descuentos por colaboración pasada
4. Se ofrezca la opción de entrenar artistas si no hay candidatos disponibles

---

## 📖 Glosario

| Término | Definición |
|---------|-----------|
| **Canción** | Pieza musical a interpretar. Requiere uno o más roles (voz principal, guitarra, bajo, batería, etc.). |
| **Artista** | Músico/técnico con: nombre, roles históricos, bandas/colaboraciones, costo por canción, límite de canciones. |
| **Rol** | Posición musical (ej: voz principal, guitarra eléctrica, batería, teclados, coros, etc.). |
| **Artista Base** | Integrante permanente de la discográfica, generalmente a costo 0 o reducido. |
| **Artista Candidato** | Artista externo disponible para contratación temporal. |
| **Recital** | Conjunto de canciones a interpretar en el evento. |
| **Descuento por Colaboración** | Reducción del 50% en costo si el candidato compartió banda histórica con algún artista base. |
| **Entrenamiento** | Adquisición de nuevo rol por un artista, incrementando su costo un 50% por rol adicional. |
| **Costo de Contratación** | Precio fijo por cada canción en la que participa el artista. |
| **maxCanciones** | Cantidad máxima de canciones que un artista puede tocar en un mismo recital. |

---

## 🎯 Funcionalidades Principales

### 1. Consultas y Análisis

| Funcionalidad | Descripción |
|---------------|------------|
| **Roles faltantes (por canción)** | ¿Qué roles (con cantidad) me faltan para tocar una canción X? |
| **Roles faltantes (global)** | ¿Qué roles (con cantidad) me faltan para tocar TODAS las canciones? |
| **Información pre-contratación** | Mostrar candidatos disponibles, costos, descuentos aplicables antes de contratar. |

### 2. Contrataciones

| Funcionalidad | Descripción |
|---------------|------------|
| **Contratar por canción** | Optimizar costo de contratación para una canción específica. Registro persistente. |
| **Contratar global** | Optimizar costo para TODAS las canciones a la vez, respetando: descuentos por banda, límite de canciones por artista, candidatos ya contratados. |
| **Manejo de errores** | Si no hay artistas válidos, ofrecer entrenamiento como solución. |

### 3. Gestión de Artistas

| Funcionalidad | Descripción |
|---------------|------------|
| **Entrenar artista** | Añadir nuevo rol a un artista (incremento 50% por rol). No aplicable a artistas base ni ya contratados. |
| **Listar artistas contratados** | Mostrar: nombre, roles asignados, canciones asignadas, costo total. |
| **Listar estado de canciones** | Mostrar: canción, roles cubiertos, roles faltantes, costo estimado, artistas asignados. |

### 4. Integración Prolog

| Funcionalidad | Descripción |
|---------------|------------|
| **Entrenamientos mínimos** | ¿Cuántos entrenamientos mínimos necesito para cubrir roles con solo artistas base + artistas sin experiencia a costo base? |

### 5. Menú Principal (CLI)

```
=== SISTEMA DE CONTRATACIÓN DE ARTISTAS ===
1. Consultar roles faltantes (por canción)
2. Consultar roles faltantes (global)
3. Contratar artistas para una canción
4. Contratar artistas para todas las canciones
5. Entrenar artista
6. Listar artistas contratados
7. Listar estado de canciones
8. Consultar entrenamientos mínimos (Prolog)
9. Exportar estado del recital (BONUS)
10. Cargar estado previo (BONUS)
11. Salir
```

---

## 📋 Reglas de Negocio

### Restricciones Claves

1. **Roles históricos**: Un artista solo puede ser asignado a roles que haya desempeñado previamente (salvo si se entrena).

2. **Descuento por colaboración**: 
   - Si un candidato compartió banda histórica con **al menos un** artista base → descuento del 50%
   - El descuento NO acumula si comparte con múltiples bases
   - Costo con descuento = costo original ÷ 2

3. **Límite de canciones**: Cada artista tiene `maxCanciones` como límite de participaciones por recital.

4. **Entrenamiento no permitido para**:
   - Artistas base
   - Artistas ya contratados para alguna canción

5. **Costo de entrenamiento**: Incremento del 50% por cada rol adicional.
   - Ejemplo: artista con costo 1000 y 2 roles nuevos → nuevo costo = 1000 × (1 + 0.5 × 2) = 2000

6. **Información pre-decisión**: Antes de cualquier contratación, mostrar:
   - Candidatos disponibles y sus costos (con/sin descuento)
   - Roles que pueden cubrir
   - Impacto en el total

---

## 📁 Formato de Datos

### Entrada Requerida

El sistema carga datos desde archivos externos. Se requieren mínimo **3 archivos**:

#### 1. `artistas.json` — Catálogo completo de artistas

```json
[
  {
    "nombre": "Brian May",
    "roles": ["guitarra eléctrica", "voz secundaria"],
    "bandas": ["Queen"],
    "costo": 0,
    "maxCanciones": 100
  },
  {
    "nombre": "George Michael",
    "roles": ["voz principal"],
    "bandas": ["Wham!", "George Michael"],
    "costo": 1000,
    "maxCanciones": 3
  },
  {
    "nombre": "Elton John",
    "roles": ["voz principal", "piano"],
    "bandas": ["Elton John Band"],
    "costo": 1200,
    "maxCanciones": 2
  },
  {
    "nombre": "David Bowie",
    "roles": ["voz principal"],
    "bandas": ["Tin Machine", "David Bowie"],
    "costo": 1500,
    "maxCanciones": 2
  }
]
```

#### 2. `recital.json` — Canciones y roles requeridos

```json
[
  {
    "titulo": "Somebody to Love",
    "rolesRequeridos": ["voz principal", "guitarra eléctrica", "bajo", "batería", "piano"]
  },
  {
    "titulo": "We Will Rock You",
    "rolesRequeridos": ["voz principal", "guitarra eléctrica", "bajo", "batería"]
  },
  {
    "titulo": "Under Pressure",
    "rolesRequeridos": ["voz principal", "voz principal", "guitarra eléctrica", "bajo", "batería"]
  }
]
```

#### 3. `artistas-discografica.json` — Artistas base (incluidos)

```json
[
  "Brian May",
  "Roger Taylor",
  "John Deacon"
]
```

### Salida Opcional (BONUS)

#### `recital-out.json` — Estado final/intermedio del recital

```json
{
  "titulo_recital": "Recital Especial 2025",
  "canciones": [
    {
      "titulo": "Somebody to Love",
      "artistas_asignados": ["Brian May", "George Michael", "Elton John"],
      "costo_total": 2200,
      "estado": "completa"
    }
  ],
  "resumen_contrataciones": {
    "artistas_contratados": 2,
    "costo_total": 5400,
    "descuentos_aplicados": 600
  }
}
```

---

## 🏗️ Arquitectura del Proyecto

```
ParadigmasTP_2025_2c/
├── Recitales/
│   ├── src/
│   │   ├── Artista/
│   │   │   ├── Artista.java              (clase base)
│   │   │   ├── ArtistaBase.java          (artista fijo)
│   │   │   └── ArtistaExterno.java       (candidato)
│   │   ├── Banda/
│   │   │   ├── Banda.java
│   │   │   └── BandaCatalogoMemory.java
│   │   ├── Rol/
│   │   │   ├── Rol.java
│   │   │   └── RolCatalogo.java
│   │   ├── Contratos/
│   │   │   ├── Contrato.java
│   │   │   └── ServicioContratacion.java
│   │   ├── Recital/
│   │   │   ├── Recital.java
│   │   │   └── Cancion.java
│   │   ├── Imports/
│   │   │   ├── FabricaRecital.java       (factory pattern)
│   │   │   ├── ICargarRecital.java       (interfaz)
│   │   │   └── JsonAdapter.java          (persistencia)
│   │   ├── ServicioProlog/
│   │   │   └── EntrenamientosProlog.java (integración JPL)
│   │   ├── App.java                      (lógica principal)
│   │   └── Menu/                         (CLI)
│   ├── test/
│   │   ├── ArtistaTest.java
│   │   ├── ContratacionTest.java
│   │   ├── RecitalTest.java
│   │   └── IntegracionPrologTest.java
│   ├── data/
│   │   ├── artistas.json
│   │   ├── recital.json
│   │   ├── artistas-discografica.json
│   │   └── recital-out.json              (BONUS)
│   ├── pom.xml                           (Maven)
│   └── README.md
└── docs/
    ├── diagrama-clases.png
    ├── informe.pdf
    └── manual-usuario.md
```

### Patrones de Diseño Aplicados

- **Factory Pattern**: `FabricaRecital` para crear recitales desde archivos
- **Adapter Pattern**: `JsonAdapter` para persistencia
- **Strategy Pattern**: Diferentes estrategias de contratación (por canción vs. global)
- **MVC (Model-View-Controller)**: Separación modelo (clases de dominio), vista (CLI), controlador (servicios)

---

## 🚀 Guía de Instalación y Ejecución

### Requisitos del Sistema

#### Requisitos Obligatorios
- **Sistema Operativo**: Windows 10/11, Linux o macOS
- **Java Development Kit (JDK)**: Versión 11 o superior (recomendado JDK 17+)
  - Descargar desde: https://adoptium.net/temurin/releases/
- **SWI-Prolog**: Versión 8.0 o superior (para integración Prolog)
  - Descargar desde: https://www.swi-prolog.org/download/stable/
  - **Importante**: Instalar la versión de 64 bits

#### Librerías Incluidas
El proyecto incluye las siguientes librerías en `src/libs/`:
- **Gson 2.13.1**: Para procesamiento de JSON
- **JPL (Java Prolog Library)**: Para integración con Prolog

### Instalación Paso a Paso

#### 1. Clonar el Repositorio
```bash
git clone https://github.com/ValentinMassa/ParadigmasTP_2025_2c.git
cd ParadigmasTP_2025_2c/Recitales
```

#### 2. Verificar JDK
```bash
java -version
javac -version
```
Debe mostrar versión 11 o superior.

#### 3. Verificar SWI-Prolog
```bash
swipl --version
```
Debe mostrar versión 8.0 o superior.

#### 4. Verificar Archivos de Datos
Asegurarse de que existan los archivos en `data/Json/`:
- `artistas.json`
- `recital.json`
- `artistas-discografica.json`

### Ejecución del Programa

#### Opción 1: Script PowerShell (Recomendado para Windows)
```powershell
.\run.ps1
```

#### Opción 2: Script Batch (Alternativo para Windows)
```batch
run.bat
```

#### Opción 3: Ejecución Manual
```bash
# Compilar
javac -cp "src/libs/gson-2.13.1.jar;src/libs/jpl.jar" -d bin -encoding UTF-8 src\*.java src\Artista\*.java src\DataExport\*.java src\DataLoader\*.java src\Menu\*.java src\Recital\*.java src\Repositorios\*.java src\Servicios\*.java

# Ejecutar
java -Djava.library.path="C:\Program Files\swipl\bin" -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" App
```

### Interfaz del Programa
Al ejecutar, el programa mostrará:
1. Selección de formato de datos (JSON/XML)
2. Estadísticas del sistema cargado
3. Menú principal con opciones numeradas

### Archivos Generados
- **Snapshots**: Guardados en `data/Snapshots/`
- **Output**: Exportaciones en `data/Output/`
- **Compilación**: Archivos `.class` en `bin/`

---

## ⚠️ Problemas Comunes y Soluciones

### Error: "java command not found" o "javac command not found"
**Causa**: JDK no instalado o no configurado en PATH.
**Solución**:
1. Instalar JDK desde https://adoptium.net/temurin/releases/
2. Agregar `JAVA_HOME` y `%JAVA_HOME%\bin` al PATH del sistema
3. Reiniciar terminal y verificar con `java -version`

### Error: "swipl command not found"
**Causa**: SWI-Prolog no instalado o no en PATH.
**Solución**:
1. Instalar SWI-Prolog 64-bit desde https://www.swi-prolog.org/download/stable/
2. Asegurar que esté en PATH (normalmente se agrega automáticamente)
3. Verificar con `swipl --version`

### Error de Compilación: "package org.jpl7 does not exist"
**Causa**: Librería JPL no encontrada o versión incorrecta.
**Solución**:
1. Verificar que `src/libs/jpl.jar` exista
2. Si usa SWI-Prolog del sistema, el script debería detectarlo automáticamente
3. Para instalación portable, colocar SWI-Prolog en el directorio del proyecto

### Error: "Could not find or load main class App"
**Causa**: Problemas en la compilación o classpath.
**Solución**:
1. Limpiar directorio `bin/` y recompilar
2. Verificar que todas las dependencias estén en classpath
3. Asegurar que `App.class` se generó en `bin/`

### Error: "java.library.path" o DLL no encontrada
**Causa**: Problemas con la integración de JPL/SWI-Prolog.
**Solución**:
1. Verificar instalación de SWI-Prolog 64-bit
2. Asegurar que `jpl.dll` esté en el PATH de SWI-Prolog
3. En Windows, verificar que no haya conflicto entre versiones 32/64-bit

### Error: "FileNotFoundException" al cargar datos
**Causa**: Archivos de datos faltantes o rutas incorrectas.
**Solución**:
1. Verificar que existan `data/Json/artistas.json`, `recital.json`, `artistas-discografica.json`
2. Ejecutar desde el directorio `Recitales/`
3. Verificar permisos de lectura en archivos

### Error: "Exception in thread 'main' java.lang.UnsupportedClassVersionError"
**Causa**: Versión de Java incompatible.
**Solución**:
1. Verificar versión de Java: `java -version` debe ser 11+
2. Si tiene múltiples JDK, usar el correcto
3. Configurar JAVA_HOME apuntando a JDK 11+

### Problema: El programa se ejecuta pero la integración Prolog no funciona
**Causa**: SWI-Prolog no configurado correctamente.
**Solución**:
1. El programa funciona sin Prolog, pero la opción 8 del menú estará limitada
2. Verificar que SWI-Prolog esté instalado y en PATH
3. Revisar logs del programa para mensajes de advertencia sobre Prolog

### Problema: Scripts no se ejecutan (PowerShell/Batch)
**Causa**: Políticas de ejecución o permisos.
**Solución para PowerShell**:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```
**Solución para Batch**: Ejecutar como administrador.

### Problema: Archivos JSON malformados
**Causa**: Errores de sintaxis en archivos de datos.
**Solución**:
1. Validar JSON con herramientas online
2. Revisar comillas, comas y estructura
3. Comparar con los ejemplos en el README

### Problema: Memoria insuficiente
**Causa**: Archivos grandes o algoritmos complejos.
**Solución**:
1. Aumentar memoria JVM: `java -Xmx2g ...`
2. Optimizar archivos de datos si es necesario

---

## 📋 Cosas a Tener en Cuenta

### Arquitectura del Sistema
- **Compilación Manual**: No usa Maven/Gradle, se compila con `javac` directamente
- **Dependencias Externas**: Gson para JSON, JPL para Prolog
- **Persistencia**: Archivos JSON/XML externos, no base de datos
- **Interfaz**: CLI basada en menús numéricos

### Limitaciones Conocidas
- **Prolog Opcional**: El sistema funciona sin SWI-Prolog, pero con funcionalidad reducida
- **Formato de Datos**: Solo JSON y XML soportados
- **Plataforma**: Probado principalmente en Windows
- **Codificación**: Archivos deben estar en UTF-8

### Recomendaciones de Desarrollo
- **IDE**: Usar IntelliJ IDEA, Eclipse o VS Code con extensiones Java
- **Debugging**: El menú incluye opciones para exportar estado del sistema
- **Testing**: Ejecutar desde línea de comandos para verificar integración completa
- **Versionado**: Commits frecuentes con snapshots del estado

### Consideraciones de Rendimiento
- **Optimización**: Algoritmos de contratación consideran múltiples factores
- **Memoria**: Cargar archivos grandes puede requerir más RAM
- **Tiempo de Ejecución**: Consultas complejas pueden demorar según el tamaño de datos

### Seguridad y Validación
- **Validación de Datos**: El sistema valida archivos de entrada
- **Manejo de Errores**: Mensajes claros para problemas comunes
- **Persistencia Segura**: Snapshots permiten recuperar estados anteriores

### Extensibilidad
- **Patrones de Diseño**: Factory, Strategy, Adapter facilitan extensiones
- **Nuevo Formatos**: Agregar adapters para YAML, CSV, etc.
- **Nuevos Roles**: Extensibles sin modificar código existente
- **Integraciones**: Posible agregar otras tecnologías de razonamiento

## 🧪 Pruebas y Validación

### Estrategia de Testing
El proyecto incluye pruebas automatizadas usando JUnit. Para ejecutarlas:

#### Compilar y Ejecutar Tests
```bash
# Compilar incluyendo tests (si existen archivos de test)
javac -cp "src/libs/gson-2.13.1.jar;src/libs/jpl.jar" -d bin -encoding UTF-8 src\*.java src\Artista\*.java src\DataExport\*.java src\DataLoader\*.java src\Menu\*.java src\Recital\*.java src\Repositorios\*.java src\Servicios\*.java

# Si hay archivos de test, compilar también
# javac -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" -d bin test\*.java

# Ejecutar aplicación para testing manual
java -Djava.library.path="C:\Program Files\swipl\bin" -cp "bin;src/libs/gson-2.13.1.jar;src/libs/jpl.jar" App
```

### Validación Manual
- **Funcionalidades Core**: Probar todas las opciones del menú
- **Casos Edge**: Artistas sin roles, canciones sin artistas base, etc.
- **Persistencia**: Crear snapshots y cargarlos
- **Integración Prolog**: Verificar consultas de entrenamientos mínimos

### Casos de Prueba por Funcionalidad

| Funcionalidad | Casos de Prueba |
|---------------|-----------------|
| **Roles faltantes** | ✓ Canción con todos los roles cubiertos por base<br/>✓ Canción con algunos roles faltantes<br/>✓ Canción con todos los roles faltantes |
| **Contratación por canción** | ✓ Contratación exitosa optimizada<br/>✓ Aplicación correcta de descuentos<br/>✓ Error: no hay candidatos válidos<br/>✓ Error: candidato no tiene rol requerido |
| **Contratación global** | ✓ Optimización con múltiples canciones<br/>✓ Respeto del límite maxCanciones<br/>✓ Descuentos múltiples (no acumulativos)<br/>✓ Candidatos ya contratados excluidos |
| **Entrenamiento** | ✓ Entrenamiento exitoso<br/>✓ Incremento correcto del costo (50% por rol)<br/>✓ Error: entrenar artista base<br/>✓ Error: entrenar artista ya contratado |
| **Integración Prolog** | ✓ Consulta de entrenamientos mínimos<br/>✓ Validación de lógica Prolog |

### Ejecución de Tests
```bash
# Nota: El proyecto no incluye suite de tests automatizada completa
# Las validaciones se realizan mediante testing manual del menú
```

---

## 🧠 Integración con Prolog

### Objetivo
Responder: **¿Cuántos entrenamientos mínimos debo realizar para cubrir todos los roles con solo artistas base + artistas sin experiencia a costo base igual?**

### Approach
1. Usar **JPL (Java Prolog Library)** para llamar a Prolog desde Java
2. Codificar el conocimiento del dominio en hechos y reglas Prolog
3. Realizar consultas desde `EntrenamientosProlog.java`

### Instalación de Dependencias
Las librerías JPL están incluidas en `src/libs/jpl.jar`. El script de ejecución configura automáticamente el classpath y las variables de entorno para SWI-Prolog.

### Ejemplo de Consulta Prolog

```prolog
% hechos
rol_requerido(cancion1, voz_principal).
rol_requerido(cancion1, guitarra).

artista_base(brian_may, [guitarra, voz_secundaria]).
artista_base(roger_taylor, [bateria]).

% regla: entrenamientos necesarios
entrenamientos_minimos(Cantidad) :-
    findall(R, rol_requerido(_, R), RolesRequeridos),
    findall(A, artista_base(A, _), Bases),
    calcular_entrenamientos(RolesRequeridos, Bases, Cantidad).
```

### Referencias
- [JPL Documentation](https://jpl7.org/TutorialJavaCallsProlog)
- [SWI-Prolog JPL](https://github.com/SWI-Prolog/packages-jpl)

---

## ✅ Requisitos Técnicos

✓ **POO**: Clases, objetos, herencia, polimorfismo, interfaces, encapsulamiento.  
✓ **Composición**: Relaciones entre Artista, Rol, Canción, Recital.  
✓ **Principio de Responsabilidad Única**: Cada clase con una responsabilidad clara.  
✓ **Pruebas Automatizadas**: JUnit con buena cobertura.  
✓ **Principio Abierto/Cerrado**: Extensible sin modificar código existente.  
✓ **Persistencia**: Archivos JSON/XML externos.  
✓ **Integración Prolog**: JPL + SWI-Prolog para consultas avanzadas.  
✓ **Interfaz CLI**: Menú amigable y mensajes claros.

---

## 🎁 Bonus Opcionales (Hasta +6 puntos)

### 1. Artista Estrella Invitado (2 pts)
- Cada recital tiene un tipo (Rock, Pop, Jazz, etc.)
- Artistas candidatos pueden preferir ciertos tipos
- Descuento adicional (10-20%) si el artista es estrella para ese tipo
- Solo aplicable a **un artista** por recital

### 2. Arrepentimiento — Quitar Artista (2 pts)
- Opción en menú para descontratar un artista ya seleccionado
- Recalcular roles faltantes
- Reembolso de costo asociado

### 3. Grafo de Colaboraciones (1 pt)
- Visualizar relaciones entre artistas por bandas compartidas
- Formato: texto simple (ej: `Bowie ↔ Queen [Under Pressure]`)

### 4. Restricciones Logísticas (2 pts)
- Disponibilidad horaria: artistas pueden tocar solo en cierta mitad del show
- Afecta al algoritmo de asignación

### 5. Datos Ampliados (1-2 pts)
- Soporte para formatos adicionales (XML, YAML, BD)
- Cargar/guardar estados intermedios para continuar después

---

## 📦 Entrega y Defensa

### Entregables

1. **Código fuente** en repositorio Git
2. **Informe técnico** con:
   - Carátula
   - Índice
   - Introducción
   - Desarrollo (diseño, decisiones, implementación)
   - Conclusiones
   - Referencias (APA)
3. **Diagrama de Clases UML** (entrega intermedia: 28/10)
4. **Demostración en vivo** de todas las funcionalidades
5. **Tests ejecutables** y reporte de cobertura

### Grupo
- Trabajo grupal: **4–6 integrantes**
- Todas las personas del grupo deben participar en la defensa

### Defensa Oral
- Presentación: ~15 minutos
- Preguntas: ~10 minutos
- Demostración del sistema funcionando

---

## 👥 Integrantes

| DNI | Nombre | Apellido | Email |
|-----|--------|----------|-------|
| 44510875 | Valentín | Massa | vmassa@alumno.unlam.edu.ar |
| 45289653 | María del Pilar | Bourdieu | mbourdieu653@alumno.unlam.edu.ar |
| 42772765 | Rodrigo | Varaldo | rvaraldo@alumno.unlam.edu.ar |
| 39471766 | Christian | Ríos | criosmamani@alumno.unlam.edu.ar |

---

## 📜 Licencia

Este proyecto está bajo licencia **MIT**. Ver archivo `LICENSE` para más detalles.

---

## 📞 Contacto y Soporte

Para consultas o reporte de bugs:
- **Email**: vmassa@alumno.unlam.edu.ar
- **GitHub Issues**: [ParadigmasTP_2025_2c/issues](https://github.com/ValentinMassa/ParadigmasTP_2025_2c/issues)

---

## 📝 Notas Finales

Este README es una **guía completa y viva** del proyecto. Se espera que:

1. **Durante el desarrollo**, se use como referencia para implementación
2. **En la defensa**, se demuestre adherencia a todos los requisitos aquí mencionados
3. **Post-entrega**, sirva como documentación para mantener y extender el proyecto