# Informe del Trabajo Práctico N°2: Paradigmas de Programación (2025)

## Carátula

**Universidad Nacional de La Matanza**  
**Facultad de Ingeniería**  
**Carrera: Ingeniería en Informática**  

**Materia:** Paradigmas de Programación  
**Trabajo Práctico N°2**  
**Tema:** Sistema de Contratación Óptima de Artistas para Recitales  

**Integrantes del Grupo:**  
- Valentín Massa (DNI: 44510875) - vmassa@alumno.unlam.edu.ar  
- María del Pilar Bourdieu (DNI: 45289653) - mbourdieu653@alumno.unlam.edu.ar  
- Rodrigo Varaldo (DNI: 42772765) - rvaraldo@alumno.unlam.edu.ar  
- Christian Ríos (DNI: 39471766) - criosmamani@alumno.unlam.edu.ar  

**Fecha de Entrega:** 18 de Noviembre de 2025  
**Fecha de Defensa:** 25 de Noviembre de 2025  

---

## Índice

1. [Introducción](#introducción)  
2. [Análisis del Problema](#análisis-del-problema)  
3. [Diseño del Sistema](#diseño-del-sistema)  
   3.1. [Arquitectura General](#arquitectura-general)  
   3.2. [Patrones de Diseño Aplicados](#patrones-de-diseño-aplicados)  
   3.3. [Modelo de Datos](#modelo-de-datos)  
4. [Implementación](#implementación)  
   4.1. [Tecnologías Utilizadas](#tecnologías-utilizadas)  
   4.2. [Estructura del Código](#estructura-del-código)  
   4.3. [Integración con Prolog](#integración-con-prolog)  
5. [Funcionalidades Implementadas](#funcionalidades-implementadas)  
   5.1. [Consultas y Análisis](#consultas-y-análisis)  
   5.2. [Contrataciones](#contrataciones)  
   5.3. [Gestión de Artistas](#gestión-de-artistas)  
   5.4. [Persistencia y Bonus](#persistencia-y-bonus)  
6. [Pruebas y Validación](#pruebas-y-validación)  
7. [Dificultades Encontradas y Soluciones](#dificultades-encontradas-y-soluciones)  
8. [Funcionalidades No Implementadas](#funcionalidades-no-implementadas)  
9. [Conclusiones](#conclusiones)  
10. [Referencias](#referencias)  

---

## Introducción

### Contexto del Problema

En el ámbito de la industria musical, las discográficas enfrentan el desafío de organizar recitales de manera eficiente, minimizando costos mientras se garantiza la calidad artística. Este trabajo práctico aborda la problemática de formar "bandas temporales" para recitales mediante la contratación óptima de artistas externos, considerando restricciones como roles musicales, disponibilidad, colaboraciones previas y límites presupuestarios.

### Objetivos del Trabajo

El objetivo principal es desarrollar un sistema que permita planificar y optimizar la contratación de artistas para un recital, aplicando conceptos fundamentales de Programación Orientada a Objetos (POO) y razonamiento automático mediante Prolog. Los objetivos específicos incluyen:

- Aplicar principios de POO en un contexto real
- Diseñar un sistema extensible y mantenible
- Implementar algoritmos de optimización combinatoria
- Integrar tecnologías heterogéneas (Java + Prolog)
- Desarrollar una interfaz de usuario intuitiva

### Alcance y Limitaciones

El sistema desarrollado cubre las funcionalidades core del problema, incluyendo contratación por canción y global, gestión de artistas, y consultas avanzadas. Sin embargo, ciertos bonus opcionales no fueron implementados debido a restricciones de tiempo y complejidad adicional.

---

## Análisis del Problema

### Descripción del Dominio

Una discográfica cuenta con:
- **Artistas base**: Músicos contratados permanentemente (costo fijo o nulo)
- **Artistas candidatos**: Músicos externos disponibles para contratación temporal
- **Recital**: Conjunto de canciones que requieren roles musicales específicos

### Restricciones del Problema

1. **Roles musicales**: Cada canción requiere roles específicos (voz principal, guitarra, batería, etc.)
2. **Disponibilidad limitada**: Los artistas tienen un máximo de canciones por recital
3. **Colaboraciones previas**: Descuentos del 50% por compartir bandas históricas con artistas base
4. **Optimización de costos**: Minimizar el costo total respetando todas las restricciones
5. **Entrenamiento**: Posibilidad de capacitar artistas en nuevos roles (incremento del 50% en costo)

### Análisis de Complejidad

El problema presenta complejidad combinatoria alta debido a:
- Múltiples combinaciones posibles de artistas por canción
- Interdependencias entre canciones (mismo artista limitado)
- Restricciones globales vs. locales
- Optimización multi-objetivo (costo vs. calidad)

---

## Diseño del Sistema

### Arquitectura General

El sistema sigue una arquitectura en capas:

```
[Interfaz CLI] ← [Servicios de Lógica] ← [Repositorios] ← [Adaptadores de Datos]
```

- **Capa de Presentación**: Menú CLI con opciones numeradas
- **Capa de Servicios**: Lógica de negocio (contratación, consultas, entrenamiento)
- **Capa de Datos**: Repositorios en memoria y adaptadores para persistencia
- **Capa de Integración**: Puente con Prolog para consultas avanzadas

### Patrones de Diseño Aplicados

1. **Factory Pattern**: `FabricaRecital` para creación de objetos complejos
2. **Adapter Pattern**: `JsonAdapter` y `XmlAdapter` para formatos de datos
3. **Strategy Pattern**: Diferentes estrategias de contratación
4. **Repository Pattern**: Abstracción del acceso a datos
5. **Command Pattern**: Estructura del menú CLI

### Modelo de Datos

#### Clases Principales

- **Artista**: Clase base con atributos comunes
  - `ArtistaDiscografica`: Artistas base (costo 0)
  - `ArtistaExterno`: Candidatos a contratar
  
- **Recital**: Contenedor principal
  - `Cancion`: Unidades musicales con roles requeridos
  - `Rol`: Posiciones musicales
  
- **Contratos**: Gestión de asignaciones
  - `Contrato`: Vinculación artista-canción-rol

#### Relaciones

```
Recital 1:N Cancion
Cancion 1:N Rol
Artista N:N Rol (a través de contratos)
Artista 1:N Banda
```

---

## Implementación

### Tecnologías Utilizadas

- **Lenguaje Principal**: Java 11+
- **Paradigma Lógico**: SWI-Prolog 8+
- **Persistencia**: JSON/XML nativos
- **Librerías Externas**: 
  - Gson 2.13.1 (procesamiento JSON)
  - JPL 7.8.0 (integración Java-Prolog)
- **Entorno de Desarrollo**: JDK + SWI-Prolog
- **Control de Versiones**: Git

### Estructura del Código

```
src/
├── App.java                 # Punto de entrada
├── Artista/                 # Modelo de artistas
├── Recital/                 # Modelo del recital
├── Servicios/               # Lógica de negocio
├── Repositorios/            # Acceso a datos
├── Menu/                    # Interfaz CLI
├── DataLoader/              # Adaptadores de datos
└── libs/                    # Dependencias externas
```

### Integración con Prolog

La integración se realiza mediante JPL (Java Prolog Library):

```prolog
% Archivo: entrenamientos.pl
:- dynamic rol_requerido/2.
:- dynamic base_tiene_rol/2.

min_trainings(Min) :-
    findall(Faltantes, (rol_requerido(Rol, _), faltantes_por_rol(Rol, Faltantes)), Diferencias),
    sum_list(Diferencias, Min).
```

El sistema carga datos dinámicamente y ejecuta consultas desde Java.

---

## Funcionalidades Implementadas

### Consultas y Análisis

- **Roles faltantes por canción**: Identifica roles no cubiertos
- **Roles faltantes globales**: Análisis completo del recital
- **Información pre-contratación**: Costos y descuentos disponibles

### Contrataciones

- **Contratación por canción**: Optimización individual
- **Contratación global**: Optimización completa respetando límites
- **Validación de restricciones**: Máximo canciones, roles históricos

### Gestión de Artistas

- **Entrenamiento**: Adquisición de nuevos roles (costo +50%)
- **Listado de contratados**: Estado actual con costos
- **Listado por canción**: Asignaciones detalladas

### Persistencia y Bonus

- **Formatos múltiples**: JSON y XML soportados
- **Snapshots**: Guardado/restauración de estados
- **Arrepentimiento**: Descontratación de artistas
- **Historial de colaboraciones**: Análisis de relaciones

---

## Pruebas y Validación

### Estrategia de Testing

Se implementó testing manual exhaustivo cubriendo:

- **Casos normales**: Contrataciones exitosas
- **Casos límite**: Sin candidatos disponibles
- **Validación de reglas**: Descuentos, límites, entrenamientos
- **Integración**: Funcionamiento conjunto de módulos

### Casos de Prueba Ejecutados

| Funcionalidad | Casos Verificados |
|---------------|-------------------|
| Contratación por canción | ✓ Optimización correcta<br/>✓ Aplicación de descuentos<br/>✓ Manejo de errores |
| Contratación global | ✓ Respeto de límites<br/>✓ Optimización multi-canción<br/>✓ Candidatos excluidos |
| Entrenamiento | ✓ Incremento de costo<br/>✓ Validación de restricciones<br/>✓ Nuevos roles |
| Prolog | ✓ Consultas de entrenamientos<br/>✓ Lógica correcta |

### Validación de Requisitos

- ✅ Principios POO aplicados correctamente
- ✅ Sistema extensible (abierto/cerrado)
- ✅ Persistencia externa funcional
- ✅ Interfaz CLI amigable
- ✅ Integración Prolog operativa

---

## Dificultades Encontradas y Soluciones

### Problemas Técnicos

1. **Integración Java-Prolog**: 
   - **Problema**: Configuración de classpath y library path
   - **Solución**: Scripts automatizados de configuración

2. **Optimización Combinatoria**:
   - **Problema**: Algoritmos complejos para asignación óptima
   - **Solución**: Implementación de heurísticas eficientes

3. **Manejo de Estados**:
   - **Problema**: Persistencia de estados intermedios
   - **Solución**: Sistema de snapshots con serialización

### Problemas de Diseño

1. **Acoplamiento**: Separación clara de responsabilidades
2. **Extensibilidad**: Interfaces para nuevos formatos
3. **Validación**: Manejo robusto de errores

---

## Funcionalidades No Implementadas

Debido a restricciones de tiempo y complejidad, no se implementaron los siguientes bonus opcionales:

### 1. Artista Estrella Invitado (2 puntos)
**Descripción**: Sistema de tipos de recital y descuentos adicionales por coincidencia con preferencias del artista.

**Razones**: 
- Complejidad adicional en el modelo de datos
- Requeriría modificación del algoritmo de optimización
- Tiempo limitado para implementación y testing

**Impacto**: No afecta funcionalidades core, pero limita opciones de descuento.

### 4. Restricciones Logísticas (2 puntos)
**Descripción**: Limitaciones de disponibilidad horaria para artistas (primera/segunda mitad del show).

**Razones**:
- Aumento significativo de complejidad algorítmica
- Requeriría reestructuración del modelo de asignación
- Interdependencias temporales complejas

**Impacto**: El sistema asume disponibilidad completa, lo cual es válido para recitales pequeños.

---

## Conclusiones

### Logros Alcanzados

El desarrollo del sistema demostró la aplicabilidad de conceptos teóricos de POO en problemas reales de optimización. Se logró:

- **Sistema funcional completo** con todas las funcionalidades requeridas
- **Integración exitosa** de tecnologías heterogéneas (Java + Prolog)
- **Arquitectura extensible** que facilita mantenimiento y evolución
- **Interfaz intuitiva** que permite uso sin conocimientos técnicos avanzados

### Lecciones Aprendidas

1. **Importancia del diseño**: Una buena arquitectura facilita la implementación
2. **Testing continuo**: Validación temprana previene errores complejos
3. **Separación de responsabilidades**: Mejora mantenibilidad y testabilidad
4. **Documentación**: Esencial para equipos colaborativos

### Trabajo en Equipo

El desarrollo en grupo permitió:
- Distribución efectiva de tareas
- Revisión cruzada de código
- Aprendizaje mutuo de conceptos
- Integración de diferentes perspectivas

### Mejoras Futuras

Para extensiones futuras se recomienda:
- Implementación de bonus pendientes
- Migración a framework web
- Base de datos para persistencia
- Algoritmos de optimización avanzados

### Reflexión Final

Este proyecto consolidó conocimientos de POO y demostró que paradigmas diferentes pueden complementarse efectivamente. La experiencia adquirida será valiosa para proyectos futuros en el ámbito profesional.

---

## Referencias

American Psychological Association. (2020). *Publication manual of the American Psychological Association* (7th ed.). https://doi.org/10.1037/0000165-000

Barnes, D. J., & Kölling, M. (2017). *Objects first with Java: A practical introduction using BlueJ* (6th ed.). Pearson.

Clocksin, W. F., & Mellish, C. S. (2003). *Programming in Prolog: Using the ISO standard* (5th ed.). Springer.

Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design patterns: Elements of reusable object-oriented software*. Addison-Wesley.

Oracle Corporation. (2021). *Java Platform, Standard Edition Documentation*. https://docs.oracle.com/en/java/

SWI-Prolog. (2023). *SWI-Prolog Reference Manual*. https://www.swi-prolog.org/pldoc/doc/_SWI_/home/swiprolog/

---

**Fin del Informe**</content>
<parameter name="filePath">c:\Users\Valee\Desktop\TODO_VALEN\REPOSITORIO_GITHUB\ParadigmasTP_2025_2c\ParadigmasTP_2025_2c\Informe_TP2.md