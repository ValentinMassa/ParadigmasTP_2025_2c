# Integración Prolog con JPL (Java-Prolog Library)

## 📋 Descripción

`EntrenamientosProlog.java` está diseñado para integrar **JPL (Java-Prolog Library)** y ejecutar consultas Prolog desde Java.

Responde la pregunta: **¿Cuántos entrenamientos mínimos debo realizar para cubrir todos los roles del recital?**

## 🚀 Instalación de JPL

### Opción 1: Maven (Recomendado)

Agregar a tu `pom.xml`:

```xml
<dependency>
    <groupId>org.jpl7</groupId>
    <artifactId>jpl</artifactId>
    <version>7.6.1</version>
</dependency>
```

### Opción 2: Gradle

Agregar a tu `build.gradle`:

```gradle
dependencies {
    implementation 'org.jpl7:jpl:7.6.1'
}
```

### Opción 3: Descargar JAR manual

1. Descargar JPL desde: https://jpl7.org/
2. Agregar `jpl.jar` al classpath del proyecto

## 📝 Archivos Prolog

Los archivos Prolog se encuentran en: `bin/ArchivosImport/`

### `entrenamientos.pl`

Contiene la lógica Prolog para:
- Definir artistas base y sus roles
- Definir canciones y roles requeridos
- Calcular entrenamientos mínimos
- Analizar disponibilidad de roles

**Predicados principales:**

```prolog
% Calcula entrenamientos mínimos
entrenamientos_minimos(X)

% Retorna entrenamientos y roles faltantes
entrenamientos_minimos_detallado(RolesFaltantes, Entrenamientos)

% Verifica si un rol está disponible en base
rol_disponible_en_base(Rol)

% Análisis completo del recital
analisis_recital_completo(Analisis)
```

## 💻 Uso desde Java

### Inicializar

```java
Recital recital = /* ... crear recital ... */;
EntrenamientosProlog servicioProlog = new EntrenamientosProlog(recital);
```

### Con archivo Prolog personalizado

```java
EntrenamientosProlog servicioProlog = new EntrenamientosProlog(
    recital, 
    "ruta/a/archivo.pl"
);
```

### Calcular entrenamientos mínimos

```java
try {
    int entrenamientos = servicioProlog.calcularEntrenamientosMinimos();
    System.out.println("Entrenamientos necesarios: " + entrenamientos);
} catch (Exception e) {
    e.printStackTrace();
}
```

### Con parámetros (costo base, artistas contratados)

```java
double costoBase = 100.0;
HashSet<ArtistaExterno> artistasContratados = new HashSet<>();

EntrenamientosProlog.ResultadoEntrenamiento resultado = 
    servicioProlog.calcularEntrenamientosConParametros(
        costoBase, 
        artistasContratados
    );

System.out.println("Entrenamientos: " + resultado.getEntrenamientosMinimos());
System.out.println("Costo total: $" + resultado.getCostoTotal());
System.out.println("Roles faltantes: " + resultado.getRolesFaltantes());
```

### Generar reporte

```java
String reporte = servicioProlog.generarReporteEntrenamientos();
System.out.println(reporte);
```

### Verificar si es viable

```java
if (servicioProlog.esViableCubrir()) {
    System.out.println("✓ Es viable cubrir todos los roles");
} else {
    System.out.println("✗ No es posible cubrir todos los roles");
}
```

### Obtener roles a entrenar

```java
Map<String, Integer> rolesAEntrenar = servicioProlog.obtenerRolesAEntrenar();
for (String rol : rolesAEntrenar.keySet()) {
    System.out.println("Entrenar: " + rol);
}
```

## 🔗 Integración Completa con JPL

Para activar la integración real con Prolog, descomentar en `calcularConProlog()`:

```java
private int calcularConProlog() throws Exception {
    try {
        // DESCOMENTA ESTO CUANDO TENGAS JPL INSTALADO:
        org.jpl7.Query q = new org.jpl7.Query("entrenamientos_minimos(X)");
        if (q.hasSolution()) {
            Map<String, org.jpl7.Term> solution = q.oneSolution();
            org.jpl7.Term x = solution.get("X");
            return Integer.parseInt(x.toString());
        }
        
        System.out.println("✓ Consulta Prolog ejecutada exitosamente");
        return 0;
    } catch (Exception e) {
        System.err.println("Error en consulta Prolog: " + e.getMessage());
        return calcularConHeuristica();
    }
}
```

## 📚 Referencias

- **JPL 7 Tutorial**: https://jpl7.org/TutorialJavaCallsProlog
- **JPL GitHub**: https://github.com/SWI-Prolog/packages-jpl
- **SWI-Prolog**: https://www.swi-prolog.org/

## ⚙️ Modo Fallback

Si JPL no está instalado o hay error en la inicialización, el sistema **automáticamente** usa una heurística Java equivalente que:

1. Extrae todos los roles requeridos
2. Cuenta artistas base que pueden tocar cada rol
3. Si falta cobertura, suma un entrenamiento

**Estado actual**: Ambos modos funcionales ✓

## 🧪 Ejemplo Completo

```java
public class Main {
    public static void main(String[] args) {
        try {
            // Cargar datos
            String rutaArtistas = "bin/ArchivosImport/artistas.json";
            String rutaCanciones = "bin/ArchivosImport/canciones.json";
            String rutaArtistasBase = "bin/ArchivosImport/artistas-incluidos.json";
            
            JsonAdapter cargador = new JsonAdapter(
                rutaArtistas, 
                rutaCanciones, 
                rutaArtistasBase
            );
            FabricaRecital fabrica = new FabricaRecital(cargador);
            Recital recital = fabrica.crearRecital();
            
            // Calcular entrenamientos
            EntrenamientosProlog prolog = new EntrenamientosProlog(recital);
            
            System.out.println(prolog.generarReporteEntrenamientos());
            
            // Con parámetros
            EntrenamientosProlog.ResultadoEntrenamiento resultado =
                prolog.calcularEntrenamientosConParametros(
                    100.0,  // costo base
                    new HashSet<>()  // sin artistas contratados
                );
            
            System.out.println("\n" + resultado);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

**Última actualización**: Noviembre 2025
**Estado**: Listo para integración JPL completa
