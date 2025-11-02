# Paradigmas de Programación — Trabajo Práctico N°2 (2025)

Una discográfica necesita formar una "banda temporal" para un recital. Este repositorio contiene la propuesta de implementación para el Trabajo Práctico N°2 de la materia "Paradigmas de Programación" (Ciclo 2025), centrada en modelar y resolver la selección y contratación óptima de artistas utilizando principios de Programación Orientada a Objetos y razonamiento con Prolog.

---

Badges: (añadir según CI / cobertura / licencia)

---

Índice
- Descripción
- Fechas importantes
- Objetivos
- Resumen de la consigna
- Funcionalidades
- Reglas de negocio
- Formato de datos (ejemplos)
- Ejecución rápida
- Arquitectura y estructura del proyecto
- Pruebas
- Integración con Prolog
- Bonus opcionales
- Entrega y defensa
- Autores y licencia
- Contacto

---

Descripción
Este trabajo aplica conceptos de POO (clases, encapsulamiento, herencia, polimorfismo, interfaces) para resolver un problema real: cubrir los roles requeridos por un recital contratando artistas externos de manera óptima, respetando restricciones y aprovechando descuentos derivados de relaciones históricas.

Fechas importantes
- Entrega intermedia (diagrama de clases tentativo): 28 de Octubre de 2025  
- Entrega final: 18 y 25 de Noviembre de 2025

Objetivo general
Diseñar e implementar un sistema extensible y testeable que permita planificar la contratación de artistas para un recital, optimizando costos y cumpliendo las reglas del dominio.

Objetivos específicos
- Modelar la problemática con clases y responsabilidades claras.
- Mantener bajo acoplamiento y alta cohesión.
- Cargar datos desde archivos externos para permitir nuevos escenarios sin cambiar código.
- Implementar pruebas automatizadas (JUnit).
- Integrar razonamiento en Prolog para consultas específicas sobre entrenamientos mínimos.

Resumen de la consigna
- Cada canción requiere roles (p. ej. voz principal, guitarra, bajo, batería, teclados).
- Existe un conjunto de artistas base (permanentes) y artistas candidatos.
- Cada artista: nombre, roles históricos, bandas colaboradas, costo por canción, maxCanciones por recital.
- Si un candidato compartió banda histórica con algún artista base, su costo se reduce al 50% (no acumulativo).
- Se puede entrenar a un artista (añadir rol) con incremento de costo.
- El sistema debe:
  - Informar roles faltantes por canción y para todo el recital.
  - Contratar optimizando costo por canción o para todo el recital (respetando maxCanciones y descuentos).
  - Permitir entrenamientos como opción ante falta de candidatos disponibles.
  - Integrarse con Prolog para contar entrenamientos mínimos bajo ciertas hipótesis.

Funcionalidades principales
- Consultas:
  - Roles faltantes para una canción (con cantidades).
  - Roles faltantes para todo el recital.
- Contrataciones:
  - Contratar para una canción optimizando costo (registro persistente).
  - Contratar para todo el recital optimizando costo global (respetando restricciones).
- Gestión de artistas:
  - Entrenar artista (no aplicable a artistas base ni a artistas ya contratados).
  - Listar artistas contratados con detalles y costos.
  - Listar canciones con su estado, costo estimado y artistas asignados.
- Errores y propuestas de solución:
  - Si no hay artistas válidos, lanzar error claro y ofrecer entrenamiento si es viable.

Reglas de negocio (claves)
- Un artista solo puede cubrir roles que haya desempeñado históricamente salvo si se entrena.
- Descuento del 50% para candidatos que comparten banda histórica con algún artista base.
- El descuento no acumula si comparte banda con múltiples bases.
- El artista tiene un límite maxCanciones por recital.
- No se puede entrenar:
  - a un artista base;
  - a un artista ya contratado para alguna canción.

Formato de datos (entrada)
Los datos deben cargarse desde archivos externos (JSON, XML u otro). Mínimo requerido:
- artistas.json — lista con información completa de artistas.
- recital.json — lista de canciones y roles requeridos.
- artistas-discografica.json (o artistas-incluidos.json) — artistas base (nombres coincidentes con artistas.json).

Ejemplo: artistas.json
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
  }
]
```

Ejemplo: recital.json
```json
[
  {
    "titulo": "Somebody to Love",
    "rolesRequeridos": ["voz principal", "guitarra eléctrica", "bajo", "batería", "piano"]
  },
  {
    "titulo": "Under Pressure",
    "rolesRequeridos": ["voz principal", "voz principal", "guitarra eléctrica", "bajo", "batería"]
  }
]
```

Ejemplo: artistas-discografica.json
```json
["Brian May", "Roger Taylor", "John Deacon"]
```

Salida opcional (bonus)
- `recital-out.json` — estado final o intermedio del recital con contrataciones y totales.

Ejecución rápida
1. Clonar:
   git clone https://github.com/ValentinMassa/ParadigmasTP_2025_2c.git
2. Colocar datos en `data/` (artistas.json, recital.json, artistas-discografica.json).
3. Compilar y ejecutar:
   - Maven: `mvn clean package && java -jar target/<artifact>.jar`
   - O ejecutar `Main` desde el IDE.
4. Tests:
   - `mvn test`

Arquitectura y estructura sugerida
- src/main/java/ — modelo (Artista, Cancion, Recital), servicios (ContratacionService, EntrenamientoService), persistencia (JsonLoader, JsonExporter), integración (PrologAdapter), CLI (Main).
- src/test/java/ — tests JUnit por componente y por caso funcional.
- data/ — archivos de entrada/salida.
- docs/ — diagramas, informe y material para la defensa.

Pruebas
- Cobertura esperada: validaciones de contrataciones (por canción y global), aplicación de descuentos, límites de maxCanciones, operación de entrenamiento, manejo de errores y exportación de estado.
- Usar JUnit para todas las pruebas automatizadas.

Integración con Prolog
- Objetivo: calcular entrenamientos mínimos necesarios para cubrir todos los roles bajo el supuesto: solo miembros base + artistas "sin experiencia" contratables, con coste base igual (parámetro).
- Recomendada: JPL (JPL/JNI) con SWI‑Prolog.
- Referencias:
  - https://jpl7.org/TutorialJavaCallsProlog
  - https://github.com/SWI-Prolog/packages-jpl

Bonus sugeridos (opcional)
1. Artista estrella invitado (descuento por tipo de recital) — 2 pts.  
2. Arrepentimiento (quitar artista ya seleccionado) — 2 pts.  
3. Visualizar grafo de colaboraciones (bandas compartidas) — 1 pt.  
4. Restricciones logísticas (disponibilidad horaria) — 2 pts.  
5. Soporte ampliado de formatos y carga/guardado de estados — 1–2 pts.

Entrega y defensa
- Trabajo grupal (4–6 integrantes).
- Informe mínimo: carátula, índice, introducción, desarrollo, conclusiones y referencias (APA).
- Se realizará defensa oral en la fecha designada.

Autores
- Completar con nombres del grupo.

Licencia
- Indicar la licencia del proyecto (ej.: MIT).

Contacto
- ValentinMassa — (añadir contacto)

Notas finales
Este README está redactado para ser claro, directo y usable durante la implementación y la demostración: contiene lo esencial para ejecutar, probar y entender las decisiones de diseño, y facilita la extensión del proyecto sin tocar lógica ya existente.
