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
10. [Guía de Uso](#guía-de-uso)
11. [Pruebas Automatizadas](#pruebas-automatizadas)
12. [Integración con Prolog](#integración-con-prolog)
13. [Requisitos Técnicos](#requisitos-técnicos)
14. [Bonus Opcionales](#bonus-opcionales)
15. [Entrega y Defensa](#entrega-y-defensa)
16. [Integrantes](#integrantes)
17. [Licencia](#licencia)

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

## 🚀 Guía de Uso

### Requisitos Previos
- **Java 11+**
- **Maven 3.6+**
- **SWI-Prolog 8+** (para integración Prolog)
- **JPL (Java Prolog Library)** para integración

### Instalación y Ejecución

#### 1. Clonar el repositorio
```bash
git clone https://github.com/ValentinMassa/ParadigmasTP_2025_2c.git
cd ParadigmasTP_2025_2c/Recitales
```

#### 2. Preparar datos
Colocar archivos en `data/`:
- `artistas.json`
- `recital.json`
- `artistas-discografica.json`

#### 3. Compilar con Maven
```bash
mvn clean compile
```

#### 4. Ejecutar tests
```bash
mvn test
```

#### 5. Ejecutar la aplicación
```bash
mvn exec:java -Dexec.mainClass="App"
```

O desde el IDE (ejecutar `App.java`).

---

## 🧪 Pruebas Automatizadas

### Estrategia de Testing

Usar **JUnit 5** con cobertura mínima del 70%.

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
mvn test                                    # Todos los tests
mvn test -Dtest=ArtistaTest                # Tests específicos
mvn test -Dcode-coverage                    # Con cobertura
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

**En `pom.xml`**:
```xml
<dependency>
    <groupId>org.jpl7</groupId>
    <artifactId>jpl</artifactId>
    <version>7.8.0</version>
</dependency>
```

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

