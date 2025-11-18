# Análisis Detallado del Trabajo Práctico N°2
## Comparativa: Consigna vs Implementación Actual

**Fecha:** 18 de Noviembre de 2025  
**Proyecto:** Sistema de Contratación de Artistas para Recital  
**Estado General:** ⚠️ **~70% completado** con varios problemas críticos y omisiones

---

## Índice
1. [Incumplimientos de la Consigna](#incumplimientos)
2. [Errores de Lógica Detectados](#errores-logica)
3. [Problemas de Arquitectura](#problemas-arquitectura)
4. [Mejoras Sugeridas](#mejoras-sugeridas)
5. [Matriz de Cumplimiento](#matriz-cumplimiento)

---

## Incumplimientos de la Consigna

### ❌ **1. CRÍTICO: Rol Faltante en Recital**

**Consigna:**
> "¿Qué roles (con cantidad) me faltan para tocar todas las canciones? **En este caso, cada canción puede incluir a los artistas base, disminuyendo la cantidad total de roles**"

**Estado Actual:**
- ✅ Implementado en `Recital.getRolesFaltantes()`
- ❌ **PERO** La lógica es **INCORRECTA**

**El Problema:**
```java
// Código actual (MALO)
for (ArtistaBase artista : artistaBase) {
    for (Rol rol : artista.getRoles()) {
        if (rolesRequeridos.getOrDefault(rol, 0) > 0) {
            int puedeAportar = Math.min(artista.getMaxCanciones(), rolesRequeridos.get(rol));
            rolesCubiertos.put(rol, rolesCubiertos.getOrDefault(rol, 0) + puedeAportar);
        }
    }
}
```

**Problemas específicos:**
- ❌ **Cuenta artistas base como "disponibles por defecto"** sin que realmente estén contratados
- ❌ **Asume que cada artista base tocará automáticamente en `maxCanciones` canciones**, ignorando que:
  - Un artista base PUEDE NO estar asignado en ninguna canción
  - Los artistas base deben ser asignados explícitamente si se desea usarlos
  - El límite `maxCanciones` es un MÁXIMO, no una capacidad garantizada
  
- ❌ **No existe una estrategia clara de asignación** de artistas base a canciones

**Impacto:**
- Subestima significativamente los roles faltantes
- Genera confusión entre "capacidad potencial" vs "capacidad real asignada"
- Puede llevar a cálculos de contratación incorrectos

**Solución Propuesta:**
```java
// Opción 1: Cambiar la interpretación de getRolesFaltantes()
// para que SOLO cuente contratos reales (explícitos), no potencial

Map<Rol, Integer> getRolesFaltantes() {
    Map<Rol, Integer> rolesRequeridos = new HashMap<>();
    Map<Rol, Integer> rolesCubiertos = new HashMap<>();
    
    // 1. Contar todos los roles requeridos
    for (Cancion cancion : canciones) {
        Map<Rol, Integer> rolesCancion = cancion.getRolesRequeridos();
        for (Rol rol : rolesCancion.keySet()) {
            int cantidad = rolesCancion.get(rol);
            rolesRequeridos.put(rol, rolesRequeridos.getOrDefault(rol, 0) + cantidad);
        }
    }
    
    // 2. Contar SOLO roles cubiertos por contratos REALES
    // (no asumir disponibilidad de base)
    if (contratos != null) {
        for (Contrato contrato : contratos) {
            Rol rol = contrato.getRol();
            rolesCubiertos.put(rol, rolesCubiertos.getOrDefault(rol, 0) + 1);
        }
    }
    
    // 3. Calcular faltantes
    Map<Rol, Integer> rolesFaltantes = new HashMap<>();
    for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
        Rol rol = entry.getKey();
        int requerido = entry.getValue();
        int cubierto = rolesCubiertos.getOrDefault(rol, 0);
        int faltante = requerido - cubierto;
        
        if (faltante > 0) {
            rolesFaltantes.put(rol, faltante);
        }
    }
    return rolesFaltantes;
}

// Opción 2: Crear NUEVO método: getRolesFaltantesConArtistasBase()
// que SÍ considere la capacidad teórica de artistas base para sugerencias
```

---

### ❌ **2. CRÍTICO: Algoritmo de Contratación Simplista**

**Consigna:**
> "Para esta operación, se debe **optimizar el costo de todas las contrataciones a la vez**"
> 
> "Se debe optimizar el costo de contratación de artistas para una canción en particular"

**Estado Actual:**
```java
// Implementación actual (SIMPLISTA)
private ArtistaExterno buscarExternoDisponible(...) {
    ArtistaExterno mejor = null;
    double mejorCosto = Double.MAX_VALUE;
    
    for (ArtistaExterno externo : recital.getArtistasExternos()) {
        // ... validaciones ...
        double costo = externo.getCosto();
        
        if (comparteBanda) {
            costo = costo * 0.5;  // Aplicar descuento
        }
        
        if (costo < mejorCosto) {
            mejor = externo;
            mejorCosto = costo;
        }
    }
    return mejor;
}
```

**Problemas:**
- ❌ **Algoritmo greedy naive:** selecciona el artista más barato individualmente
- ❌ **No optimiza globalmente:** No considera que:
  - Un artista caro ahora podría ahorrar dinero después si se usa en múltiples canciones
  - La reutilización de artistas (up to `maxCanciones`) es un factor de optimización importante
  - Los descuentos por banda no se están considerando correctamente
  
- ❌ **Descuento por banda mal aplicado:** 
  ```
  // Confusión: ¿Se aplica descuento en cada rol o por contrato?
  // La consigna dice: "Si algún artista base comparte... el costo se reduce 50%"
  // Pero se aplica al buscar cada rol, no al contrato completo
  ```

- ❌ **No se asigna descuento en el Contrato:** El `Contrato` almacena el artista pero no el costo aplicado

**Impacto:**
- La solución puede NO ser óptima en costo
- Ejemplos donde falla:
  - Contratar 1 artista caro para 3 canciones es más barato que 3 artistas baratos para 1 canción cada uno
  - No se reutilizan artistas con descuento por banda

**Solución Propuesta:**
1. **Refactorizar `Contrato` para almacenar el costo real aplicado:**
   ```java
   public class Contrato {
       public Cancion cancion;
       public Rol rol;
       public Artista artista;
       public double costoAplicado;  // ← Nuevo: costo real con descuentos
       
       public double obtenerCostoContrato() {
           return costoAplicado;  // Usar el costo real
       }
   }
   ```

2. **Implementar algoritmo de optimización global (ej: Búsqueda local, Programación Dinámica, o Backtracking)**
   ```java
   // Pseudocódigo: Búsqueda local + Descuentos
   public List<Contrato> contratarParaTodo_Optimizado(Recital recital) {
       // 1. Calcular matriz de costos (artista, rol, descuentos)
       // 2. Usar algoritmo de cobertura de conjunto mínimo costo
       // 3. Respetar restricción maxCanciones
       // 4. Preferir artistas con descuento por banda
   }
   ```

3. **Crear clase de Helper para Optimización:**
   ```java
   public class OptimizadorContratacion {
       public List<Contrato> optimizarParaTodo(Recital recital, 
                                               Map<Rol, Integer> rolesFaltantes);
   }
   ```

---

### ❌ **3. CRÍTICO: Descuento por Banda Mal Implementado**

**Consigna:**
> "Si algún artista base comparte historial de haber pertenecido a la misma banda histórica con el artista candidato, el costo del candidato se reduce a la mitad (50%). **Si se comparte con más de un base, sigue siendo 50% (no acumulativo).**"

**Estado Actual:**
```java
// Busca si comparte UNA banda
for (ArtistaBase base : recital.getArtistasBase()) {
    for (Banda b : base.getBandasHistoricas()) {
        if (externo.getBandasHistoricas().contains(b)) {
            comparteBanda = true;
            break;  // ← Correcto: solo necesita 1
        }
    }
    if (comparteBanda) break;
}

if (comparteBanda) {
    costo = costo * 0.5;
}
```

**Problemas:**
- ✅ **La lógica de "no acumulativo" SÍ está correcta**
- ❌ **PERO el descuento se aplica en tiempo de búsqueda**, no se almacena
- ❌ **El mismo artista puede aparecer multiple veces sin que el descuento se mantenga**
- ❌ **No hay registro de quién recibió descuento y por qué**

**Impacto:**
- Inconsistencia: si el mismo artista externo se contrata para 2 canciones, el descuento puede variar
- No se puede auditar por qué se pagó cierto costo
- Si se "quita" un artista y se readmite, podría recalcularse el descuento incorrectamente

**Solución Propuesta:**
```java
public class Contrato {
    public Cancion cancion;
    public Rol rol;
    public Artista artista;
    public double costoOriginal;        // ← Nuevo
    public double costoAplicado;        // ← Nuevo
    public boolean conDescuentoBanda;   // ← Nuevo: auditoría
    public String razonDescuento;       // ← Nuevo: trazabilidad
    
    public double obtenerCostoContrato() {
        return costoAplicado;
    }
    
    public String generarReceipt() {
        if (conDescuentoBanda) {
            return String.format("%s: $%.2f (original: $%.2f, %s)", 
                artista.getNombre(), costoAplicado, costoOriginal, razonDescuento);
        }
        return String.format("%s: $%.2f", artista.getNombre(), costoAplicado);
    }
}
```

---

### ❌ **4. CRÍTICO: Entrenamientos con Costo Incorrecto**

**Consigna:**
> "Se puede entrenar a un artista para que adquiera un nuevo rol, **incrementando su costo un 50% por cada rol adicional que se agregue**."

**Estado Actual:**
```java
// Menú - Entrenar artista
artista.agregarRol(rol);
artista.incrementarCosto(1.5);  // Multiplicar por 1.5

public void incrementarCosto(double porcentaje) {
    costo = costo * porcentaje;
}
```

**Problema detectado:**
- ✅ Se multiplica correctamente por 1.5 (es decir, +50%)
- ❌ **PERO**: Si se entrenan **múltiples roles**, el cálculo es incorrecto

**Ejemplo del Error:**
```
Artista: Liam Gallagher
Costo original: $950
Roles originales: ["voz principal"]

Entrenar 1er rol nuevo (guitarra):
  costo = 950 * 1.5 = $1425

Entrenar 2do rol nuevo (batería):
  costo = 1425 * 1.5 = $2137.50  ← INCORRECTA!

Debería ser:
  costo = 950 * (1 + 0.5 * 2) = 950 * 2 = $1900  ← CORRECTA
```

**Impacto:**
- El costo crece exponencialmente en lugar de linealmente
- Artistas entrenados con múltiples roles son más caros de lo debido
- Económicamente inviable para entrenar 3+ roles

**Solución Propuesta:**
```java
// En clase Artista
private int rolesAdicionalesEntrenados = 0;

public void entrenarRol(Rol rolNuevo) throws IllegalArgumentException {
    if (rolNuevo == null) {
        throw new IllegalArgumentException("El rol no puede ser nulo");
    }
    if (rolHistorico.contains(rolNuevo)) {
        throw new IllegalArgumentException("El artista ya tiene este rol");
    }
    
    // Agregar rol
    rolHistorico.add(rolNuevo);
    rolesAdicionalesEntrenados++;
    
    // Recalcular costo: costBase * (1 + 0.5 * rolesAdicionales)
    recalcularCostoConEntrenamiento();
}

private void recalcularCostoConEntrenamiento() {
    // costoBase es el costo original sin entrenamientos
    costo = costoBase * (1.0 + 0.5 * rolesAdicionalesEntrenados);
}
```

**Nota:** Se requiere agregar campo `costoBase` para rastrear el costo inicial.

---

### ❌ **5. CRÍTICO: Artistas Base Mal Manejados**

**Consigna:**
> "Artista: músico o técnico que puede desempeñar un rol por canción, de varios roles... **cada canción puede incluir a los artistas base**, disminuyendo la cantidad total de roles"

**Estado Actual:**
- ❌ **Los artistas base NO tienen una asignación explícita a canciones**
- ❌ **El sistema asume que si hay artista base disponible, lo usará automáticamente**
- ❌ **No hay opción de elegir si usar artista base o contratar externo**

**Ejemplo del Problema:**
```
Canción: "Under Pressure"
Roles requeridos: [voz principal, bajo, batería, teclados]

Artistas base disponibles: [Freddie Mercury (voz), Brian May (guitarra), 
                            Roger Taylor (batería), John Deacon (bajo)]

¿Qué sucede en la implementación actual?

En contratarParaCancion():
  - Busca un artista base para "voz principal" 
    → Encuentra a Freddie Mercury ✓
    
  - Busca un artista base para "bajo"
    → Encuentra a John Deacon ✓
    
  - Busca un artista base para "batería"
    → Encuentra a Roger Taylor ✓
    
  - Busca un artista base para "teclados"
    → NO HAY artista base, busca externo

RESULTADO: Se usan 3 artistas base + 1 externo

PERO: ¿Qué pasa si luego queremos que Freddie Mercury cante también en "When Doves Cry"?
      → No se reutiliza automáticamente (ya que no fue "contratado", simplemente se usó)
      → El contador de canciones se incrementó, pero sin explicitud
```

**Impacto:**
- Ambigüedad: ¿Se consideran los artistas base como "contratados"?
- Incoherencia: En `getCostoTotalRecital()` los artistas base no tienen costo, pero sí ocupan espacios
- No permite decisiones conscientes: No se puede elegir entre usar base o contratar externo

**Solución Propuesta:**
1. **Crear clase `ContratoArtista` para manejar tanto base como externos:**
   ```java
   public abstract class Contrato {
       protected Cancion cancion;
       protected Rol rol;
       protected Artista artista;
       
       abstract double obtenerCosto();
       abstract boolean esArtisaBase();
   }
   
   public class ContratoBase extends Contrato {
       // Artista base: costo 0 o bajo
       @Override
       double obtenerCosto() { return 0; }
       @Override
       boolean esArtistaBase() { return true; }
   }
   
   public class ContratoExterno extends Contrato {
       private double costoAplicado;
       
       @Override
       double obtenerCosto() { return costoAplicado; }
       @Override
       boolean esArtistaBase() { return false; }
   }
   ```

2. **Hacer explícita la decisión de usar artista base:**
   ```java
   // En ServicioContratacion
   public List<Contrato> contratarParaCancion(Recital recital, Cancion cancion) {
       // Opción 1: Contratar explícitamente artistas base disponibles
       // Opción 2: Contratar solo externos
       // Opción 3: Hibrido (base + externos según disponibilidad)
   }
   ```

---

### ⚠️ **6. INCOMPLETO: Integración con Prolog**

**Consigna:**
> "¿Cuántos entrenamientos mínimos debo realizar para cubrir todos los roles para el recital, **utilizando solo los miembros base, y artistas contratados sin experiencia y con un coste base por parámetro, para todos iguales?**"

**Estado Actual:**
```java
// ServicioPrologIntegracion.java
public int consultarEntrenamientosMínimos() {
    int totalRolesFaltantes = obtenerRolesFaltantes();
    int capacidadBase = obtenerCapacidadBase();
    int artistasDisponibles = obtenerArtistasDisponibles();
    
    return consultarProlog(totalRolesFaltantes, capacidadBase, artistasDisponibles);
}
```

**Problemas:**
- ❌ **Archivo Prolog no existe:** `src/Recital/ServicioProlog/entrenamientos.pl` debe crearse
- ❌ **Lógica de Prolog incompleta:** No está clara la codificación del problema
- ❌ **Asunción incorrecta:** El parámetro "costo base igual" NO se está considerando
- ❌ **Usa reflexión para llamar a Prolog:** Frágil y difícil de mantener
- ❌ **Manejo de excepciones genérico:** No hay distinción de errores

**Impacto:**
- La funcionalidad no está totalmente disponible
- El menú ofrece la opción pero probablemente falla
- Incumple requisito técnico de integración Prolog

**Solución Propuesta:**
1. **Crear archivo `entrenamientos.pl` con lógica clara:**
   ```prolog
   % entrenamientos.pl
   % Predicado: entrenamientos_minimos(+RolesFaltantes, +CapacidadBase, 
   %                                     +ArtistasDisponibles, -EntrenamientosNecesarios)
   
   % Si la capacidad base cubre todos los roles, no se necesita entrenar
   entrenamientos_minimos(RolesFaltantes, CapacidadBase, _, 0) :-
       RolesFaltantes =< CapacidadBase.
   
   % Si hay artistas disponibles pero la capacidad base es insuficiente
   % calcular entrenamientos necesarios
   entrenamientos_minimos(RolesFaltantes, CapacidadBase, ArtistasDisponibles, Entrenamientos) :-
       RolesFaltantes > CapacidadBase,
       RolesAFalta is RolesFaltantes - CapacidadBase,
       % Si cada artista entrena 1 rol, se necesita ceil(RolesAFalta / MaxRolesXArtista)
       % Asumiendo max 1 rol por artista para simplicidad:
       (RolesAFalta =< ArtistasDisponibles ->
           Entrenamientos is RolesAFalta
       ;
           Entrenamientos = -1  % Imposible
       ).
   ```

2. **Mejorar el manejo de JPL en Java:**
   ```java
   public int consultarEntrenamientosMínimos() {
       try {
           // Usar JPL de forma más directa
           org.jpl7.Query query = new org.jpl7.Query(
               "consult('src/Recital/ServicioProlog/entrenamientos.pl'), " +
               "entrenamientos_minimos(?, ?, ?, Resultado)",
               new org.jpl7.Term[] {
                   org.jpl7.new_atom(String.valueOf(obtenerRolesFaltantes())),
                   org.jpl7.new_atom(String.valueOf(obtenerCapacidadBase())),
                   org.jpl7.new_atom(String.valueOf(obtenerArtistasDisponibles()))
               }
           );
           
           if (query.hasSolution()) {
               java.util.Map<String, org.jpl7.Term> solution = query.oneSolution();
               return solution.get("Resultado").intValue();
           }
           return -1;
       } catch (Exception e) {
           throw new RuntimeException("Error en consulta Prolog: " + e.getMessage(), e);
       }
   }
   ```

---

### ⚠️ **7. INCOMPLETO: Pruebas Automatizadas**

**Consigna:**
> "Realizar pruebas automáticas que verifiquen el correcto comportamiento del sistema ante diversos escenarios."

**Estado Actual:**
- ❌ **No hay tests visibles en el proyecto**
- ❌ No hay `pom.xml` con dependencia a JUnit
- ❌ No hay reportes de cobertura

**Requisito mínimo:**
- Tests para cada funcionalidad principal
- Cobertura mínima 70%
- Casos de prueba por escenario

**Solución Propuesta:**
Crear estructura de tests con JUnit 5:

```
src/test/java/
├── Recital/
│   ├── ArtistaTest.java
│   ├── CancionTest.java
│   ├── ContratoTest.java
│   ├── RecitalTest.java
│   ├── RolTest.java
│   └── Contratos/
│       └── ServicioContratacionTest.java
```

Ejemplo de suite de tests:
```java
@DisplayName("ServicioContratacion")
class ServicioContratacionTest {
    
    @Test
    @DisplayName("Contratación por canción - caso exitoso")
    void testContratarParaCancion_Exitoso() {
        // DADO un recital con roles faltantes
        // CUANDO se contrata para una canción
        // ENTONCES se asignan artistas disponibles al menor costo
    }
    
    @Test
    @DisplayName("Contratación falla sin artistas disponibles")
    void testContratarParaCancion_SinArtistas() {
        // DADO un recital sin artistas externos válidos
        // CUANDO se intenta contratar
        // ENTONCES se lanza ContratacionException con detalles
    }
    
    @Test
    @DisplayName("Se aplica descuento por banda histórica")
    void testDescuentoPorBanda() {
        // DADO artista externo que comparte banda con base
        // CUANDO se calcula costo
        // ENTONCES el costo es 50% del original
    }
}
```

---

### ⚠️ **8. INCOMPLETO: Persistencia y Exportación**

**Consigna:**
> "Deben utilizarse, al menos, dos archivos para ingresar la información necesaria al sistema."

**Estado Actual:**
- ✅ Se cargan 3 archivos JSON (artistas, recital, artistas-discografica)
- ⚠️ **Se exporta estado en `ExportadorRecitalJSON`** pero no está claro qué contiene
- ❌ **No hay opción para cargar estado previo del recital** (bonus mencionado pero no implementado)

**Mejoras Sugeridas:**
1. **Implementar `CargarEstadoRecital`** para reanudar sesiones
2. **Permitir múltiples formatos** (JSON, XML) via Factory Pattern
3. **Validación de esquemas** JSON/XML contra DTD o JSON Schema

---

### ⚠️ **9. FALTA: Manejo de Restricciones Logísticas** (BONUS)

**Consigna (Bonus):**
> "Restricciones Logísticas (2 pts): Disponibilidad horaria - artistas pueden tocar solo en cierta mitad del show"

**Estado Actual:**
- ❌ No implementado

**Solución Propuesta:**
```java
public enum RangoHorario {
    PRIMERA_MITAD, SEGUNDA_MITAD, AMBAS
}

public class Artista {
    private RangoHorario rangoHorario;
    
    public boolean puedeTocarEnHorario(RangoHorario requerido) {
        return this.rangoHorario == RangoHorario.AMBAS || 
               this.rangoHorario == requerido;
    }
}

public class Cancion {
    private RangoHorario rangoRequerido;
}
```

---

### ⚠️ **10. FALTA: Artista Estrella Invitado** (BONUS)

**Consigna (Bonus):**
> "Artista Estrella Invitado (2 pts): Cada recital tiene un tipo (Rock, Pop, Jazz...). Artistas candidatos pueden preferir ciertos tipos. Descuento adicional 10-20% si es estrella."

**Estado Actual:**
- ❌ No implementado

**Solución Propuesta:**
```java
public enum TipoRecital {
    ROCK, POP, JAZZ, CLASICO, ELECTRONICA
}

public class ArtistaExterno extends Artista {
    private Set<TipoRecital> tiposPreferidos;
    private boolean esEstrella;
    
    public double calcularDescuentoEstrella(TipoRecital tipoRecital) {
        if (esEstrella && tiposPreferidos.contains(tipoRecital)) {
            return 0.85; // 15% descuento
        }
        return 1.0; // Sin descuento
    }
}

public class Recital {
    private TipoRecital tipoRecital;
    private Artista estrellaInvitada; // Solo 1 por recital
}
```

---

### ⚠️ **11. FALTA: Grafo de Colaboraciones** (BONUS)

**Consigna (Bonus):**
> "Grafo de Colaboraciones (1 pt): Visualizar relaciones entre artistas por bandas compartidas"

**Estado Actual:**
- ⚠️ **Existe `GrafoColaboraciones.java` pero no funciona correctamente**
- ❌ Sin capacidad visual (solo texto)

**Verificar Implementación:**
```java
// Revisar: src/Recital/Colaboraciones/GrafoColaboraciones.java
// Debe mostrar conexiones como:
// Freddie Mercury ↔ David Bowie (banda: no compartida)
// Gwen Stefani ↔ Tom Dumont ↔ Tony Kanal ↔ Adrian Young (banda: No Doubt)
```

---

## Errores de Lógica Detectados

### 🔴 **Error 1: El descuento por banda se pierda entre contrataciones**

**Ubicación:** `ServicioContratacion.buscarExternoDisponible()`

**Descripción:**
El descuento se calcula al buscar el artista, pero no se almacena. Si el mismo artista se reutiliza, podría no aplicarse el descuento nuevamente.

**Ejemplo:**
```java
// 1era canción: aplica descuento
ArtistaExterno musiciano = buscarExternoDisponible(...);
// costo internamente = 1000 * 0.5 = 500

// 2da canción: ¿aplica descuento de nuevo?
// No se sabe, porque el descuento no se almacenó
```

**Fix:**
```java
public class Contrato {
    private double costoConDescuento;
    private String razonDescuento; // "descuento_banda", "sin_descuento", etc
}
```

---

### 🔴 **Error 2: `maxCanciones` no se respeta correctamente**

**Ubicación:** `Artista.puedeAceptarNuevaCancion()` y lógica de asignación

**Descripción:**
Se valida que `cantCancionesAsignado < maxCanciones`, pero:
- No hay distinción entre "canciones asignadas" vs "canciones reales"
- Un artista puede ser reutilizado sin incrementar el contador

**Ejemplo:**
```java
// Freddie Mercury tiene maxCanciones = 100
// Se asigna a "Under Pressure" → cantCancionesAsignado = 1
// Se busca reutilizar en "When Doves Cry"
// ¿Se valida que no está en la misma canción?

// El código CORRE, pero es confuso
for (Contrato c : contratos) {
    if (c.getCancion().equals(cancion) && c.getArtista().equals(artistaSeleccionado)) {
        return true;  // Ya tiene contrato en esta canción
    }
}
```

**Fix:** Documentar claramente qué significa `maxCanciones` en contexto de contratación múltiple.

---

### 🔴 **Error 3: `getRolesFaltantes()` mete todo en un pot**

**Ubicación:** `Recital.getRolesFaltantes()`

**Descripción:**
Suma todos los roles faltantes de todas las canciones sin considerar que algunos podrían reutilizarse.

**Ejemplo:**
```
Canción 1: voz principal (x1)
Canción 2: voz principal (x1)

getRolesFaltantes() retorna: {voz principal: 2}

Pero si Freddie Mercury canta en ambas:
  → Solo necesitamos 1 contrato, NO 2

La función lo cuenta como si fueran "roles globales" indistintos,
cuando en realidad están vinculados a canciones específicas.
```

**Fix:** Separar conceptos:
- `getRolesFaltantesGlobales()` - suma total (para estadísticas)
- `getRolesFaltantesPorCancion()` - desglose por canción (para contratación)

---

### 🔴 **Error 4: El contador de canciones asignadas se incrementa sin validar rol**

**Ubicación:** `ServicioContratacion.contratarParaCancion()`

```java
Artista artistaSeleccionado = buscarBaseDisponible(...);
// ...
Contrato contrato = new Contrato(cancion, rol, artistaSeleccionado);
nuevosContratos.add(contrato);
recital.getContratos().add(contrato);
artistaSeleccionado.asignarCancion();  // ← Se incrementa aquí
```

**Problema:**
Si el artista ya toca ese rol en otra canción, aún se incrementa el contador. Debería ser:
```java
// Incrementar solo si es la PRIMERA participación en el recital
Set<Cancion> cancionesDelArtista = obtenerCancionesDelArtista(artistaSeleccionado);
if (cancionesDelArtista.add(cancion)) {  // Si es nuevo
    artistaSeleccionado.asignarCancion();
}
```

---

### 🔴 **Error 5: No hay validación cruzada artista-rol en Contrato**

**Ubicación:** `Contrato.java`

```java
public Contrato(Cancion cancion, Rol rol, Artista artista) {
    // NO VALIDA que artista pueda tocar ese rol!
    this.cancion = cancion;
    this.rol = rol;
    this.artista = artista;
}
```

**Impacto:**
Es posible crear un contrato inválido sin que el sistema se percate.

**Fix:**
```java
public Contrato(Cancion cancion, Rol rol, Artista artista) throws IllegalArgumentException {
    if (!artista.puedeTocarRol(rol)) {
        throw new IllegalArgumentException(
            artista.getNombre() + " no puede tocar " + rol.getNombre()
        );
    }
    this.cancion = cancion;
    this.rol = rol;
    this.artista = artista;
}
```

---

## Problemas de Arquitectura

### 🏗️ **Problema 1: Responsabilidad Difusa en `Recital`**

**Ubicación:** `Recital.java`

La clase hace demasiadas cosas:
```java
// 1. Almacenar datos
public Recital(HashSet<ArtistaBase>, HashSet<ArtistaExterno>, HashSet<Cancion>)

// 2. Calcular roles faltantes
public Map<Rol, Integer> getRolesFaltantes()

// 3. Gestionar contratos
public List<Contrato> getContratos()
public boolean quitarArtista(Artista)

// 4. Calcular costos
public Map<Artista, Double> getCostosPorArtista()
public Map<Cancion, Double> getCostosPorCancion()

// 5. Exportar estado
// (vía ExportadorRecitalJSON que la usa)
```

**SRP Violation:** Debería dividirse en:
```java
// Modelo de datos
public class Recital { /*...*/ }

// Cálculo de roles y análisis
public class AnalizadorRecital {
    public Map<Rol, Integer> getRolesFaltantes(Recital recital) { /*...*/ }
    public Map<Rol, Integer> getRolesFaltantesParaCancion(Recital, Cancion) { /*...*/ }
}

// Gestión de contratos
public class GestorContratos {
    public boolean quitarArtista(Recital, Artista) { /*...*/ }
}

// Análisis de costos
public class AnalizadorCostos {
    public Map<Artista, Double> getCostosPorArtista(Recital) { /*...*/ }
    public double getCostoTotalRecital(Recital) { /*...*/ }
}
```

---

### 🏗️ **Problema 2: Falta de Inversión de Dependencias**

**Ubicación:** `MenuPrincipal` depende directamente de clases concretas

```java
// BAD: Acoplamiento directo
private Recital recital;
private ServicioContratacion servicioContratacion;
private RolCatalogo rolCatalogo;

// Mejor: Usar interfaces
public interface ServicioContratacion { /*...*/ }
public interface RepositorioRoles { /*...*/ }
```

**Impacto:**
- Difícil testear (no se puede mockear)
- No se puede cambiar implementación sin modificar cliente

---

### 🏗️ **Problema 3: Falta de Patrón Factory para Creación de Artistas**

**Ubicación:** `JsonAdapter.java`

```java
// Debería usar Factory Pattern
public class FabricaArtista {
    public static Artista crearArtista(String tipo, String nombre, ...) {
        switch(tipo) {
            case "base":
                return new ArtistaBase(...);
            case "externo":
                return new ArtistaExterno(...);
            default:
                throw new IllegalArgumentException("Tipo desconocido");
        }
    }
}

// Uso:
Artista artista = FabricaArtista.crearArtista("base", "Freddie", ...);
```

---

### 🏗️ **Problema 4: Estructura de Datos Inadecuada**

**Ubicación:** Uso de `HashSet` para todo

```java
private HashSet<Contrato> contratos;  // ← Debería ser List para mantener orden
private HashSet<Cancion> canciones;   // ← OK, pero orden podría importar
private HashSet<Artista> artistas;    // ← OK, pero dificulta búsqueda por nombre
```

**Mejora:**
```java
private List<Contrato> contratos;  // Mantiene orden temporal
private Map<String, Cancion> cancionesPorTitulo;  // Búsqueda rápida
private Map<String, Artista> artistasPorNombre;   // Búsqueda rápida
```

---

### 🏗️ **Problema 5: Falta de Estrategia de Transacciones**

**Ubicación:** `ServicioContratacion`

```java
// Si contratarParaTodo() falla a mitad de camino:
public List<Contrato> contratarParaTodo(Recital recital) {
    for (Cancion cancion : recital.getCanciones()) {
        try {
            List<Contrato> contratosCancion = contratarParaCancion(recital, cancion);
            contratos.addAll(contratosCancion);  // ← Si esto falla, ¿rollback?
        } catch (ContratacionException e) {
            // Sigue intentando otras canciones
            // PERO ya agregó contrato anteriores
        }
    }
}
```

**Riesgo:** Estado inconsistente del recital.

**Fix:**
```java
public class UnitOfWork {
    private List<Contrato> contratosTemporales = new ArrayList<>();
    
    public void agregarContrato(Contrato c) {
        contratosTemporales.add(c);
    }
    
    public void commit(Recital recital) {
        recital.getContratos().addAll(contratosTemporales);
    }
    
    public void rollback() {
        contratosTemporales.clear();
    }
}
```

---

## Mejoras Sugeridas

### ✅ **Mejora 1: Separación de Modelos**

**Crear jerarquía de modelos más clara:**

```
Artista (abstract)
├── ArtistaBase
├── ArtistaExterno
└── ArtistaEntrenado (?)

Contrato (abstract)
├── ContratoArtisaBase
└── ContratoArtista Externo

Recital
├── recitalEnProgreso: boolean
├── estadoContratacion: EstadoContratacion (enum)
└── historicoCambios: List<Cambio>
```

---

### ✅ **Mejora 2: Usar Value Objects para Cálculos**

```java
public class CostoArtista {
    private final double costoBase;
    private final double descuentoPorBanda;
    private final double incrementoPorEntrenamiento;
    private final double costoFinal;
    
    public static CostoArtista calcular(Artista artista, 
                                        Recital recital,
                                        Cancion cancion) {
        // Lógica centralizada
    }
    
    public String getExplicacion() {
        // Para auditoría
    }
}
```

---

### ✅ **Mejora 3: Implementar Repository Pattern**

```java
public interface RepositorioArtista {
    Optional<Artista> buscarPorNombre(String nombre);
    List<Artista> buscarPorRol(Rol rol);
    List<Artista> buscarDisponibles(Cancion cancion);
}

public class RepositorioArtistaSQLite implements RepositorioArtista {
    // Implementación persistente
}
```

---

### ✅ **Mejora 4: Agregar Logging**

```java
// Usar SLF4J + Logback
private static final Logger logger = LoggerFactory.getLogger(ServicioContratacion.class);

public List<Contrato> contratarParaCancion(Recital recital, Cancion cancion) {
    logger.info("Iniciando contratación para: {}", cancion.getTitulo());
    logger.debug("Roles faltantes: {}", rolesFaltantes);
    // ...
    logger.info("Contratación completada con {} contratos", nuevosContratos.size());
}
```

---

### ✅ **Mejora 5: Crear Servicios Especializados**

```java
// Servicio de Análisis
public class AnalizadorRecital {
    public AnálisisRecital analizar(Recital recital);  // Retorna objeto DTO
}

// Servicio de Optimización
public class OptimizadorCostos {
    public List<Contrato> optimizar(Recital recital, Map<Rol,Integer> rolesFaltantes);
}

// Servicio de Validación
public class ValidadorRecital {
    public List<String> validar(Recital recital);  // Retorna problemas encontrados
}
```

---

### ✅ **Mejora 6: Agregar DTOs para Exportación**

```java
public class RecitalDTO {
    public String titulo;
    public List<CancionDTO> canciones;
    public List<ArtistaDTO> artistasContratados;
    public ResumenCostosDTO costos;
}

public class ArtistaDTO {
    public String nombre;
    public List<String> roles;
    public List<String> cancionesAsignadas;
    public double costoTotal;
}
```

---

### ✅ **Mejora 7: Mejorar Manejo de Errores**

Crear excepciones específicas:

```java
public abstract class RecitalException extends Exception { }

public class ArtistaBusquedaException extends RecitalException {
    private List<Artista> candidatos;
}

public class ContratacionOptimizacionException extends RecitalException {
    private Map<Rol, Integer> rolesNoOptimizables;
}

public class IntegracionPrologException extends RecitalException {
    private String consultaProlog;
}
```

---

### ✅ **Mejora 8: Agregar Observador para Cambios**

```java
public interface ObservadorRecital {
    void onContratoAgregado(Contrato contrato);
    void onContratoEliminado(Contrato contrato);
    void onArtistaEntrenado(Artista artista, Rol nuevoRol);
}

public class Recital {
    private List<ObservadorRecital> observadores = new ArrayList<>();
    
    public void agregarObservador(ObservadorRecital obs) {
        observadores.add(obs);
    }
    
    public void eliminarObservador(ObservadorRecital obs) {
        observadores.remove(obs);
    }
    
    private void notificarContratoAgregado(Contrato c) {
        observadores.forEach(obs -> obs.onContratoAgregado(c));
    }
}
```

---

### ✅ **Mejora 9: Implementar Undo/Redo**

```java
public interface Comando {
    void ejecutar();
    void deshacer();
}

public class ComandoContratarArtista implements Comando {
    private Recital recital;
    private Contrato contrato;
    
    @Override
    public void ejecutar() {
        recital.getContratos().add(contrato);
    }
    
    @Override
    public void deshacer() {
        recital.getContratos().remove(contrato);
    }
}

public class Historial {
    private Stack<Comando> comandosEjecutados = new Stack<>();
    
    public void deshacer() {
        if (!comandosEjecutados.isEmpty()) {
            comandosEjecutados.pop().deshacer();
        }
    }
}
```

---

### ✅ **Mejora 10: Crear Reportes Customizables**

```java
public interface GeneradorReporte {
    String generar(Recital recital);
}

public class ReporteCSV implements GeneradorReporte {
    @Override
    public String generar(Recital recital) {
        // Exportar a CSV
    }
}

public class ReportePDF implements GeneradorReporte {
    @Override
    public String generar(Recital recital) {
        // Exportar a PDF
    }
}

public class GeneradorReportes {
    public String generar(Recital recital, TipoReporte tipo) {
        GeneradorReporte generador = seleccionar(tipo);
        return generador.generar(recital);
    }
}
```

---

## Matriz de Cumplimiento

| # | Requisito | Estado | Puntos | Notas |
|---|-----------|--------|--------|-------|
| **FUNCIONALIDADES PRINCIPALES** |
| 1 | Roles faltantes por canción | ⚠️ Parcial | 7/10 | Implementado pero lógica tiene problemas |
| 2 | Roles faltantes global | ⚠️ Parcial | 5/10 | Subestima roles (no cuenta bien artistas base) |
| 3 | Contratar artistas por canción | ✅ Completo | 9/10 | Funciona pero algoritmo no es óptimo |
| 4 | Contratar artistas global | ⚠️ Parcial | 6/10 | Implementado pero greedy, no optimiza |
| 5 | Entrenar artista | ⚠️ Parcial | 6/10 | Cálculo de costo incorrecto en múltiples entrenamientos |
| 6 | Listar artistas contratados | ✅ Completo | 9/10 | Bien implementado |
| 7 | Listar estado canciones | ✅ Completo | 9/10 | Bien implementado |
| **REQUISITOS TÉCNICOS** |
| 8 | POO (clases, herencia, polimorfismo) | ✅ Completo | 10/10 | Bien hecho |
| 9 | Encapsulamiento y responsabilidad única | ⚠️ Parcial | 6/10 | Algunas violaciones de SRP |
| 10 | Colecciones y estructuras dinámicas | ✅ Completo | 9/10 | Bien usado HashSet y List |
| 11 | Archivos externos (JSON) | ✅ Completo | 10/10 | Carga 3 archivos correctamente |
| 12 | Pruebas automatizadas | ❌ No | 0/10 | **FALTA COMPLETAMENTE** |
| 13 | Principio abierto/cerrado | ⚠️ Parcial | 6/10 | Extensible pero no del todo flexible |
| 14 | Integración Prolog | ⚠️ Parcial | 4/10 | Estructura existe pero incompleta |
| **BONUS** |
| 15 | Arrepentimiento (quitar artista) | ✅ Completo | 2/2 | Bien implementado |
| 16 | Grafo de colaboraciones | ⚠️ Parcial | 0/1 | Existe pero no completo |
| 17 | Artista estrella invitado | ❌ No | 0/2 | No implementado |
| 18 | Restricciones logísticas | ❌ No | 0/2 | No implementado |
| 19 | Datos ampliados (XML, YAML) | ❌ No | 0/1 | Solo JSON |
| **PUNTUACIÓN ESTIMADA** | | **~71/100** | |  |

---

## Conclusiones y Recomendaciones

### 🎯 Prioridades de Fix (Orden crítico a importante)

1. **CRÍTICA (hacer antes de entregar):**
   - Crear pruebas automatizadas (JUnit 5)
   - Validar y corregir lógica de descuentos
   - Corregir cálculo de costo en entrenamientos
   - Completar integración Prolog

2. **IMPORTANTE (antes de defensa):**
   - Mejorar algoritmo de optimización de costos
   - Refactorizar para separar responsabilidades
   - Agregar validaciones en `Contrato`
   - Documentar decisiones de diseño

3. **RECOMENDABLE (para mejorar nota):**
   - Implementar bonus (Artista Estrella, Restricciones Horarias)
   - Agregar observador para auditoría de cambios
   - Crear reportes adicionales (PDF, CSV)
   - Mejorar integración de código con JavaDoc

---

### 📝 Checklist Antes de Entrega Final

- [ ] Todos los tests pasan (cobertura > 70%)
- [ ] Archivo `entrenamientos.pl` completo y funcional
- [ ] Cálculo de entrenamientos corregido (x1.5 por cada rol, no exponencial)
- [ ] Descuentos aplicados correctamente y almacenados
- [ ] Artistas base no se cuentan como "automáticamente disponibles"
- [ ] Algoritmo de optimización implementado (no solo greedy)
- [ ] Menú funciona sin errores
- [ ] Exportación JSON genera archivo válido
- [ ] Código documentado con JavaDoc
- [ ] README actualizado con instrucciones de ejecución

---

### 🚀 Sugerencias de Extensión Futura

1. **UI Web:** Migrar de CLI a interfaz web (Spring Boot + React)
2. **Base de datos:** Reemplazar JSON con SQL (PostgreSQL)
3. **Machine Learning:** Usar para predecir costos óptimos
4. **API REST:** Exponer funcionalidades como servicios
5. **Reportes avanzados:** Gráficos de costos, análisis de disponibilidad
6. **Integración con Spotify API:** Obtener bandas históricas reales

---

**Fin del Análisis** ✅
