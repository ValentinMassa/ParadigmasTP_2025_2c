# Pruebas Automatizadas - Sistema de Recitales

## Descripción General

Se ha creado una **suite exhaustiva de pruebas JUnit 5** que valida todas las funcionalidades del sistema de recitales según la consigna especificada. Las pruebas cubren:

- ✅ Gestión de artistas (base y externos)
- ✅ Gestión de canciones y roles
- ✅ Contratación de artistas
- ✅ Cálculo de costos
- ✅ Descuentos por bandas históricas
- ✅ Entrenamientos de artistas
- ✅ Eliminación/quita de artistas
- ✅ Grafo de colaboraciones
- ✅ Integración del sistema completo
- ✅ Casos extremos y manejo de errores

## Requisitos Previos

- Java 11 o superior
- Maven 3.6.0 o superior
- Git

## Instalación de Maven (si es necesario)

### En Windows (PowerShell):

```powershell
# Descargar Maven
$url = "https://archive.apache.org/dist/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.zip"
Invoke-WebRequest -Uri $url -OutFile maven.zip

# Extraer
Expand-Archive -Path maven.zip -DestinationPath "C:\Program Files\Maven"

# Agregar a PATH
$env:MAVEN_HOME = "C:\Program Files\Maven\apache-maven-3.8.1"
$env:Path += ";$env:MAVEN_HOME\bin"

# Verificar instalación
mvn --version
```

## Ejecución de Pruebas

### 1. **Ejecutar todas las pruebas**

```bash
cd Recitales
mvn test
```

### 2. **Ejecutar pruebas de una clase específica**

```bash
# Todas las pruebas de Rol
mvn test -Dtest=RecitalComprehensiveTest#PruebasRol

# Todas las pruebas de Artista
mvn test -Dtest=RecitalComprehensiveTest#PruebasArtista

# Todas las pruebas de Contratación
mvn test -Dtest=RecitalComprehensiveTest#PruebasContratacion
```

### 3. **Ejecutar una prueba específica**

```bash
# Una prueba individual
mvn test -Dtest=RecitalComprehensiveTest#testCrearRolValido
```

### 4. **Ejecutar con reporte de cobertura**

```bash
mvn clean test jacoco:report
# El reporte estará en: target/site/jacoco/index.html
```

### 5. **Compilar proyecto (sin tests)**

```bash
mvn clean compile
```

### 6. **Generar JAR ejecutable**

```bash
mvn clean package
java -jar target/sistema-recitales-1.0-SNAPSHOT.jar
```

## Estructura de Pruebas

El archivo `RecitalComprehensiveTest.java` contiene **11 clases anidadas** (Nested Test Classes) con pruebas categorizadas:

### 📋 Categorías de Pruebas:

| Categoría | Pruebas | Cobertura |
|-----------|---------|-----------|
| **PruebasRol** | 6 | Creación, validación, igualdad, toString |
| **PruebasArtista** | 15 | Creación, roles, canciones, entrenamientos, costos |
| **PruebasCancion** | 5 | Creación, validación, roles requeridos |
| **PruebasRecital** | 10 | Roles faltantes, costos, getters |
| **PruebasContrato** | 3 | Creación, costos, descripción |
| **PruebasContratacion** | 7 | Contratar por canción, para todo, excepciones |
| **PruebasBandasDescuentos** | 4 | Bandas históricas, descuentos |
| **PruebasEntrenamientos** | 6 | Entrenar, costos, restricciones |
| **PruebasQuitarArtista** | 5 | Quitar artista, eliminar contratos |
| **PruebasGrafoColaboraciones** | 4 | Conexiones entre artistas |
| **PruebasIntegracion** | 5 | Flujos completos del sistema |
| **PruebasCasosExtremos** | 8 | Límites, casos especiales |

**Total: ~83 pruebas automatizadas**

## Interpretación de Resultados

### Salida Exitosa (BUILD SUCCESS):

```
[INFO] Tests run: 83, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Salida con Fallos:

```
[FAILURE] Test Failure Details:
  - Test Name: testCrearRolValido
  - Error: Expected "voz principal" but got null
```

## Casos de Prueba Principales

### 1️⃣ **Pruebas de Rol**
- Crear rol válido e inválido
- Comparación de igualdad
- Manejo de null y vacío

### 2️⃣ **Pruebas de Artista**
- Crear artista base y externo
- Validar límites de canciones
- Agregar roles
- Incrementar costo por entrenamiento
- Diferencia entre artista base vs externo

### 3️⃣ **Pruebas de Canción**
- Crear canción con roles requeridos
- Validar títulos y roles
- Copias defensivas

### 4️⃣ **Pruebas de Contratación**
- Contratar artistas para canción específica
- Contratar para todo el recital
- Respetar límites de canciones
- Lanzar excepciones cuando no hay disponibilidad
- Reusar artistas cuando sea posible

### 5️⃣ **Pruebas de Descuentos**
- Detectar bandas compartidas
- Aplicar descuento 50% correctamente
- Artistas sin banda compartida no reciben descuento

### 6️⃣ **Pruebas de Entrenamientos**
- Artista base NO puede ser entrenado
- Artista externo SÍ puede ser entrenado
- Incremente de costo correcto (1.5x por rol)
- No entrenar rol duplicado

### 7️⃣ **Pruebas de Quitar Artista**
- Eliminar todos los contratos del artista
- Decrementar contador de canciones
- No quitar artista inexistente
- Múltiples quitas

### 8️⃣ **Pruebas de Integración**
- Flujo completo: cargar → contratar → consultar
- Consistencia de estado después de múltiples operaciones
- Validar que no se exceden límites

### 9️⃣ **Casos Extremos**
- Canciones con muchos roles
- Artistas con muchos roles
- Artista con maxCanciones muy alto
- Recital vacío
- Múltiples operaciones consecutivas

## Métricas de Cobertura

Para generar reporte de cobertura:

```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html  # En macOS
# O en Windows:
start target\site\jacoco\index.html
```

### Objetivos de Cobertura:
- **Líneas:** > 70%
- **Ramas:** > 60%
- **Métodos:** > 80%

## Errores Comunes y Soluciones

### Error: "Maven command not found"
```bash
# Solución: Agregar Maven al PATH
export PATH=$PATH:/path/to/maven/bin
```

### Error: "No tests found"
```bash
# Solución: Verificar que el archivo esté en src/JUnit/
# y tenga sufijo "Test.java"
```

### Error: "java.lang.ClassNotFoundException"
```bash
# Solución: Compilar primero
mvn clean compile
```

### Error: JUnit no ejecuta tests en Windows
```powershell
# Solución en PowerShell:
mvn clean test -DargLine="-Dfile.encoding=UTF-8"
```

## Validación Manual de Funcionalidades

Si desea ejecutar pruebas manuales además de las automatizadas:

### 1. Compilar
```bash
javac -d bin src/Recital/**/*.java src/JUnit/*.java
```

### 2. Ejecutar aplicación
```bash
java -cp bin App
```

### 3. Seguir el menú interactivo para:
- Cargar artistas y canciones
- Contratar artistas por canción
- Contratar para todo el recital
- Entrenar artistas
- Quitar artistas
- Ver costos totales
- Listar estado del recital

## Documentación de Clases Probadas

### ✅ Clases con Cobertura Completa:
- `Rol.java` - ✓ Probado
- `Cancion.java` - ✓ Probado
- `Artista.java` (abstracta) - ✓ Probado
- `ArtistaBase.java` - ✓ Probado
- `ArtistaExterno.java` - ✓ Probado
- `Recital.java` - ✓ Probado
- `Contrato.java` - ✓ Probado
- `ServicioContratacion.java` - ✓ Probado
- `Banda.java` - ✓ Probado (indirecto)
- `GrafoColaboraciones.java` - ✓ Probado

### ⚠️ Clases con Cobertura Parcial:
- `ServicioPrologIntegracion.java` - ⚠️ Incompleto
- `ExportadorRecitalJSON.java` - ⚠️ Necesita más pruebas

## Características de la Suite de Pruebas

### 🎯 Características Principales:

1. **Pruebas Independientes:** Cada test es autónomo y no depende de otros
2. **Setup Compartido:** `setUp()` inicializa datos comunes
3. **Manejo de Excepciones:** Valida que se lancen excepciones esperadas
4. **Copias Defensivas:** Verifica que getters retornen copias
5. **Límites:** Prueba valores mínimos, máximos y fuera de rango
6. **Igualdad:** Valida `equals()` y `hashCode()`
7. **Flujos Reales:** Simula usos prácticos del sistema
8. **Casos Extremos:** Prueba situaciones límite

## Integración Continua (CI)

Para configurar con GitHub Actions, crear `.github/workflows/tests.yml`:

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run tests
        run: cd Recitales && mvn clean test
      - name: Upload coverage
        uses: codecov/codecov-action@v2
```

## Próximos Pasos

Para mejorar aún más la cobertura:

1. ✅ Agregar pruebas para `ServicioPrologIntegracion`
2. ✅ Agregar pruebas de concurrencia (si aplica)
3. ✅ Pruebas de persistencia (JSON loading/saving)
4. ✅ Pruebas de rendimiento
5. ✅ Pruebas parametrizadas para múltiples escenarios

## Contacto y Soporte

Para reportar problemas o sugerencias:
- Revisar `ANALISIS_CONSIGNA_DETALLADO.md`
- Ejecutar `mvn test -X` para debug mode
- Verificar logs en `target/surefire-reports/`

---

**Última actualización:** 18 de Noviembre de 2025
**Versión de JUnit:** 5.9.2
**Versión de Java:** 11+
