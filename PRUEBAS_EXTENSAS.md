# Sistema de Gestión de Recitales - Pruebas Extensas

## Datos de Prueba

### Artistas Base (5)
Todos pertenecen a la banda **Queen**:
- **Freddie Mercury** - Voz principal, Piano | Costo: $800 | Max: 4 canciones
- **Brian May** - Guitarra eléctrica, Coros | Costo: $500 | Max: 5 canciones
- **Roger Taylor** - Batería, Coros | Costo: $450 | Max: 5 canciones
- **John Deacon** - Bajo | Costo: $400 | Max: 5 canciones
- **David Richards** - Piano, Coros | Costo: $350 | Max: 3 canciones

### Artistas Externos (8)
- **George Michael** - Voz principal | Banda: Wham! | Costo: $1000 | Max: 3 canciones
- **Elton John** - Voz principal, Piano | Banda: Elton John Band | Costo: $1200 | Max: 2 canciones
- **David Bowie** - Voz principal, Guitarra | Banda: The Spiders | Costo: $1100 | Max: 4 canciones
- **Billy Idol** - Guitarra, Voz principal | Banda: Generation X | Costo: $900 | Max: 3 canciones
- **Phil Collins** - Batería, Voz principal | Banda: Genesis | Costo: $950 | Max: 3 canciones
- **Sting** - Bajo, Voz principal | Banda: The Police | Costo: $1050 | Max: 3 canciones
- **Tina Turner** - Voz principal, Coros | Banda: Ike & Tina Turner | Costo: $1100 | Max: 2 canciones
- **Stevie Nicks** - Voz principal, Piano | Banda: Fleetwood Mac | Costo: $1080 | Max: 2 canciones

### Canciones (13)

#### Canciones Queen
1. **Somebody to Love** - Voz, Guitarra, Bajo, Batería, Piano
2. **We Will Rock You** - Voz, Guitarra, Bajo, Batería
3. **Another One Bites the Dust** - Voz, Bajo, Batería, Guitarra
4. **Bohemian Rhapsody** - Voz, Piano, Guitarra, Bajo, Batería, **2 Coros** ⭐ (Mayor complejidad)
5. **Don't Stop Me Now** - Voz, Piano, Bajo, Batería

#### Canciones de Otros Artistas
6. **Rocket Man** (Elton) - Voz, Piano, Guitarra, Bajo
7. **Heroes** (Bowie) - Voz, Guitarra, Bajo, Batería, Piano
8. **Under Pressure** (Queen/Bowie) - **2 Voces**, Bajo, Batería, Guitarra ⭐ (Requiere 2 cantantes)
9. **White Wedding** (Billy Idol) - Voz, Guitarra, Bajo, Batería
10. **In the Air Tonight** (Phil Collins) - Voz, Batería, Piano, Bajo
11. **Private Eyes** (Sting) - Voz, Bajo, Guitarra, Batería, Coros
12. **Private Dancer** (Tina) - Voz, Guitarra, Bajo, Batería, Coros
13. **Dreams** (Fleetwood Mac) - Voz, Piano, Guitarra, Bajo

## Casos de Prueba Sugeridos

### Prueba 1: Ver Roles Faltantes Totales
```
Opción: 2
```
Resultado esperado: Verás TODOS los roles que faltan para el recital completo con sus cantidades.

### Prueba 2: Buscar Canción Específica (Bohemian Rhapsody)
```
Opción: 1
Canción: "Bohemian Rhapsody"
```
Resultado: Esta canción requiere 2 coros, lo que hace más desafiante la contratación.

### Prueba 3: Buscar Canción con Requerimiento Especial (Under Pressure)
```
Opción: 1
Canción: "Under Pressure"
```
Resultado: Requiere 2 voz principales, lo que fuerza a contratar 2 cantantes diferentes.

### Prueba 4: Contratar para una Canción
```
Opción: 3
Canción: "Somebody to Love"
```
Resultado: El sistema buscará artistas base disponibles y luego externos más baratos.

### Prueba 5: Contratar Todo el Recital
```
Opción: 4
```
Resultado: El sistema intentará contratar todas las 13 canciones. Verá:
- Búsqueda de artistas base primero
- Aplicación de descuentos 50% para externos de Queen
- Optimización por costo
- Respeto de límite de canciones por artista

### Prueba 6: Entrenar Artista Externo
```
Opción: 5
Nombre: "George Michael"
Rol: "guitarra eléctrica"
```
Resultado: 
- George Michael NO puede tocar guitarra (no es su rol histórico)
- Puedes entrenar a cualquier externo en cualquier rol
- Su costo aumentará 50%

### Prueba 7: Listar Contratados
```
Opción: 6
```
Resultado: Tabla con todos los artistas contratados, su tipo, canciones asignadas y costo total.

### Prueba 8: Listar Canciones
```
Opción: 7
```
Resultado: Estado de cada canción (completa o faltante) con costos.

## Escenarios Especiales

### Descuentos (50% si comparten banda)
- **George Michael** (Wham!) - Si se contrata junto a alguien de Queen → Sin descuento
- Los artistas de Queen NO obtienen descuento entre ellos (no son externos)
- Los externos de diferentes bandas NO se descuentan entre sí

### Limitaciones por Artista
- **Elton John**: Máximo 2 canciones
- **Tina Turner**: Máximo 2 canciones
- **Stevie Nicks**: Máximo 2 canciones
- Otros externos: 3-4 canciones máximo
- Artistas base: 3-5 canciones máximo

### Roles Especiales
- **Voz Principal**: El rol más solicitado (8 canciones lo requieren)
- **Guitarra Eléctrica**: 7 canciones
- **Batería**: 7 canciones
- **Bajo**: 9 canciones (Mayor demanda!)
- **Piano**: 7 canciones
- **Coros**: Solo 4 canciones (Bohemian + Private Eyes + Private Dancer + 1 más)

## Cómo Ejecutar

### Opción 1: Doble Click (Recomendado)
```
ejecutar.bat
```

### Opción 2: Desde Terminal PowerShell
```powershell
cd "c:\Users\Pilar\Paradigmas de la programación\Trabajo práctico\ParadigmasTP_2025_2c\Recitales"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
javac -encoding UTF-8 -d bin src\App.java src\Imports\*.java src\Recital\*.java src\Recital\Artista\*.java src\Recital\Banda\*.java src\Recital\Rol\*.java src\Recital\Contratos\*.java src\Recital\Menu\*.java
java -cp bin App
```

## Resultados Esperados

El sistema debe demostrar:
1. ✅ Búsqueda eficiente de artistas (greedy por costo)
2. ✅ Aplicación correcta de descuentos de 50%
3. ✅ Respeto de límites de canciones por artista
4. ✅ Manejo de roles múltiples en canciones complejas
5. ✅ Entrenamiento de artistas con incremento de 50% en costo
6. ✅ Reportes detallados de costos por artista/canción
7. ✅ Validación de disponibilidad
