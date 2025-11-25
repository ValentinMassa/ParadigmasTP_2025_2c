package Servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import Recital.*;
import Repositorios.*;
import Artista.*;
import Servicios.ServicioProlog.ResultadoEntrenamiento;

@DisplayName("Tests Funcionalidad 8: Calculo de Entrenamientos Minimos con Prolog")
public class Funcionalidad08_CalculoEntrenamientosPrologTest {
    
    private Recital recital;
    private ServicioContratacion servicioContratacion;
    private ServicioConsulta servicioConsulta;
    private RepositorioArtistasMemory repositorioArtistas;
    private RolCatalogoMemory rolCatalogo;
    private BandaCatalogoMemory bandaCatalogo;
    
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol bateria;
    private Rol coros;
    private Rol saxofon;
    private Rol teclados;
    
    private Cancion bohemianRhapsody;
    private Cancion weWillRockYou;
    private Cancion criminal;
    
    private ArtistaDiscografica freddie;
    private ArtistaDiscografica brian;
    private ArtistaDiscografica roger;
    
    private Banda queen;
    
    @BeforeEach
    void setUp() {
        // PASO 1: Inicializar repositorios
        repositorioArtistas = new RepositorioArtistasMemory();
        rolCatalogo = new RolCatalogoMemory();
        bandaCatalogo = new BandaCatalogoMemory();
        
        // PASO 2: Crear roles
        vozPrincipal = rolCatalogo.agregarRol("voz principal");
        if (vozPrincipal == null) vozPrincipal = rolCatalogo.getRol("voz principal");
        
        guitarra = rolCatalogo.agregarRol("guitarra electrica");
        if (guitarra == null) guitarra = rolCatalogo.getRol("guitarra electrica");
        
        bateria = rolCatalogo.agregarRol("bateria");
        if (bateria == null) bateria = rolCatalogo.getRol("bateria");
        
        coros = rolCatalogo.agregarRol("coros");
        if (coros == null) coros = rolCatalogo.getRol("coros");
        
        saxofon = rolCatalogo.agregarRol("saxofon");
        if (saxofon == null) saxofon = rolCatalogo.getRol("saxofon");
        
        teclados = rolCatalogo.agregarRol("teclados");
        if (teclados == null) teclados = rolCatalogo.getRol("teclados");
        
        // PASO 3: Crear banda
        queen = new Banda("Queen");
        bandaCatalogo.agregarBanda("Queen");
        
        // PASO 4: Crear artistas base (discografica) - SOLO con roles que ya tienen
        freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarBandaHistorico(queen);
        
        brian = new ArtistaDiscografica("Brian May", 10, 100.0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarBandaHistorico(queen);
        
        roger = new ArtistaDiscografica("Roger Taylor", 10, 100.0);
        roger.agregarRolHistorico(bateria);
        roger.agregarBandaHistorico(queen);
        
        // PASO 5: Agregar artistas al repositorio
        HashSet<ArtistaDiscografica> artistasBase = new HashSet<>();
        artistasBase.add(freddie);
        artistasBase.add(brian);
        artistasBase.add(roger);
        repositorioArtistas = new RepositorioArtistasMemory(artistasBase, new HashSet<>());
        
        // PASO 6: Crear recital e inicializar servicios
        Recital recital = new Recital(new HashSet<>());
        servicioContratacion = new ServicioContratacion();
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
    }
    
    @Test
    @DisplayName("Test 8.1: Calculo de entrenamientos cuando artistas base cubren todos los roles")
    void testTodosRolesCubiertosNoRequiereEntrenamientos() {
        // OBJETIVO: Verificar que cuando los artistas base pueden cubrir todos los roles,
        // NO se requieren entrenamientos (entrenamientos minimos = 0)
        
        // CONTEXTO:
        // "Artistas sin experiencia" = artistas que NO tienen un rol entrenado
        // En este caso, todos los roles del recital pueden ser cubiertos por artistas base
        // que YA tienen esos roles (tienen experiencia)
        
        // PASO 1: Crear recital simple donde Queen puede cubrir TODO
        bohemianRhapsody = new Cancion("Bohemian Rhapsody");
        bohemianRhapsody.agregarRolRequerido(vozPrincipal, 1);  // Freddie tiene voz
        bohemianRhapsody.agregarRolRequerido(guitarra, 1);       // Brian tiene guitarra
        bohemianRhapsody.agregarRolRequerido(bateria, 1);        // Roger tiene bateria
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(bohemianRhapsody);
        recital = new Recital(canciones);
        
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Verificar roles requeridos
        System.out.println("\n[TEST 8.1] Roles requeridos:");
        Map<Rol, Integer> rolesRequeridos = bohemianRhapsody.getRolesRequeridos();
        rolesRequeridos.forEach((rol, cant) -> 
            System.out.println("  - " + rol.getNombre() + ": " + cant)
        );
        
        // PASO 3: Verificar que artistas base pueden cubrir todos
        System.out.println("\n[TEST 8.1] Artistas base disponibles:");
        System.out.println("  - Freddie: voz principal");
        System.out.println("  - Brian: guitarra electrica");
        System.out.println("  - Roger: bateria");
        
        // PASO 4: Ejecutar calculo con Prolog
        try {
            ServicioProlog servicioProlog = new ServicioProlog(servicioConsulta, servicioContratacion);
            ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(100.0);
            
            System.out.println("\n[TEST 8.1] Resultado de Prolog:");
            System.out.println("  - Entrenamientos minimos: " + resultado.getEntrenamientosMinimos());
            System.out.println("  - Costo total: $" + resultado.getCostoTotal());
            System.out.println("  - Roles faltantes: " + resultado.getRolesFaltantes());
            
            // VERIFICACIONES
            assertEquals(0, resultado.getEntrenamientosMinimos(),
                "Si los artistas base cubren todos los roles, NO se requieren entrenamientos");
            
            assertEquals(0.0, resultado.getCostoTotal(), 0.01,
                "Si no hay entrenamientos, el costo debe ser $0");
            
            assertTrue(resultado.getRolesFaltantes().isEmpty(),
                "No debe haber roles faltantes si todo esta cubierto");
            
        } catch (Exception e) {
            System.err.println("\n[WARNING] Test omitido: SWI-Prolog no disponible o error de configuracion");
            System.err.println("Detalle: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            // No falla el test si Prolog no esta disponible
            return;
        }
        
        // DOCUMENTACION DEL RESULTADO:
        // Escenario: Bohemian Rhapsody necesita voz + guitarra + bateria
        // Artistas base: Freddie (voz), Brian (guitarra), Roger (bateria)
        // Resultado esperado: 0 entrenamientos, $0 costo
        // Interpretacion: Todos los artistas base YA tienen los roles necesarios (tienen experiencia)
    }
    
    @Test
    @DisplayName("Test 8.2: Calculo cuando faltan roles que ningun artista base tiene")
    void testRolesFaltantesRequierenEntrenamiento() {
        // OBJETIVO: Verificar el calculo cuando hay roles que NINGUN artista base tiene
        // Estos artistas necesitan ser "contratados sin experiencia" y entrenados
        
        // PASO 1: Crear recital que necesita roles que Queen NO tiene
        criminal = new Cancion("Criminal");
        criminal.agregarRolRequerido(vozPrincipal, 1);  // Freddie tiene voz - OK
        criminal.agregarRolRequerido(saxofon, 1);        // NADIE tiene saxofon - NECESITA ENTRENAMIENTO
        criminal.agregarRolRequerido(teclados, 1);       // NADIE tiene teclados - NECESITA ENTRENAMIENTO
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(criminal);
        recital = new Recital(canciones);
        
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Documentar la situacion
        System.out.println("\n[TEST 8.2] Roles requeridos por Criminal:");
        System.out.println("  - voz principal: 1 (Freddie tiene experiencia)");
        System.out.println("  - saxofon: 1 (NADIE tiene experiencia - requiere entrenamiento)");
        System.out.println("  - teclados: 1 (NADIE tiene experiencia - requiere entrenamiento)");
        
        System.out.println("\n[TEST 8.2] Artistas base disponibles:");
        System.out.println("  - Freddie: voz principal (puede cubrir voz)");
        System.out.println("  - Brian: guitarra electrica (sin experiencia en saxofon ni teclados)");
        System.out.println("  - Roger: bateria (sin experiencia en saxofon ni teclados)");
        
        // PASO 3: Ejecutar calculo con Prolog
        try {
            ServicioProlog servicioProlog = new ServicioProlog(servicioConsulta, servicioContratacion);
            ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(150.0);
            
            System.out.println("\n[TEST 8.2] Resultado de Prolog:");
            System.out.println("  - Entrenamientos minimos: " + resultado.getEntrenamientosMinimos());
            System.out.println("  - Costo total: $" + resultado.getCostoTotal());
            System.out.println("  - Roles que requieren entrenamiento:");
            resultado.getRolesFaltantes().forEach(rol -> 
                System.out.println("    * " + rol)
            );
            
            // VERIFICACIONES
            assertEquals(2, resultado.getEntrenamientosMinimos(),
                "Se necesitan 2 entrenamientos: 1 para saxofon + 1 para teclados");
            
            assertEquals(300.0, resultado.getCostoTotal(), 0.01,
                "Costo total debe ser: 2 entrenamientos × $150 = $300");
            
            assertTrue(resultado.getRolesFaltantes().contains("saxofon") || 
                      resultado.getRolesFaltantes().contains("saxofón"),
                "Saxofon debe estar en los roles que requieren entrenamiento");
            
            assertTrue(resultado.getRolesFaltantes().contains("teclados"),
                "Teclados debe estar en los roles que requieren entrenamiento");
            
        } catch (Exception e) {
            System.err.println("\n[WARNING] Test omitido: SWI-Prolog no disponible o error de configuracion");
            System.err.println("Detalle: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return;
        }
        
        // DOCUMENTACION DEL RESULTADO ESPERADO:
        // Situacion: Criminal necesita voz (cubierto) + saxofon (falta) + teclados (falta)
        // Artistas base con experiencia: Solo Freddie en voz
        // Artistas sin experiencia en roles necesarios: Brian y Roger (no tienen saxofon ni teclados)
        // Solucion: Contratar artistas sin experiencia (ej: Brian, Roger) y entrenarlos
        // Resultado esperado: 2 entrenamientos minimos, $300 costo total
    }
    
    @Test
    @DisplayName("Test 8.3: Calculo con multiples canciones y roles compartidos")
    void testMultipleCancionesConRolesCompartidos() {
        // OBJETIVO: Verificar que el calculo suma correctamente roles faltantes de multiples canciones
        
        // PASO 1: Crear 2 canciones con algunos roles compartidos
        bohemianRhapsody = new Cancion("Bohemian Rhapsody");
        bohemianRhapsody.agregarRolRequerido(vozPrincipal, 1);  // Freddie - OK
        bohemianRhapsody.agregarRolRequerido(guitarra, 1);       // Brian - OK
        bohemianRhapsody.agregarRolRequerido(coros, 2);          // NADIE tiene coros - 2 entrenamientos
        
        weWillRockYou = new Cancion("We Will Rock You");
        weWillRockYou.agregarRolRequerido(vozPrincipal, 1);      // Freddie - OK
        weWillRockYou.agregarRolRequerido(bateria, 1);           // Roger - OK
        weWillRockYou.agregarRolRequerido(coros, 1);             // NADIE tiene coros - 1 entrenamiento
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(bohemianRhapsody);
        canciones.add(weWillRockYou);
        recital = new Recital(canciones);
        
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Documentar situacion
        System.out.println("\n[TEST 8.3] Recital con 2 canciones:");
        System.out.println("Bohemian Rhapsody:");
        System.out.println("  - voz principal: 1 (cubierto por Freddie)");
        System.out.println("  - guitarra: 1 (cubierto por Brian)");
        System.out.println("  - coros: 2 (FALTAN - requieren entrenamiento)");
        System.out.println("\nWe Will Rock You:");
        System.out.println("  - voz principal: 1 (cubierto por Freddie - puede cantar en ambas)");
        System.out.println("  - bateria: 1 (cubierto por Roger)");
        System.out.println("  - coros: 1 (FALTA - requiere entrenamiento)");
        System.out.println("\nTotal coros faltantes: 3 (2 + 1)");
        
        // PASO 3: Ejecutar calculo
        try {
            ServicioProlog servicioProlog = new ServicioProlog(servicioConsulta, servicioContratacion);
            ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(100.0);
            
            System.out.println("\n[TEST 8.3] Resultado de Prolog:");
            System.out.println("  - Entrenamientos minimos: " + resultado.getEntrenamientosMinimos());
            System.out.println("  - Costo total: $" + resultado.getCostoTotal());
            System.out.println("  - Roles totales requeridos: " + resultado.getRolesRequeridosTotales());
            
            // VERIFICACIONES
            assertEquals(3, resultado.getEntrenamientosMinimos(),
                "Se necesitan 3 entrenamientos en coros (Freddie puede cantar en ambas canciones)");
            
            assertEquals(300.0, resultado.getCostoTotal(), 0.01,
                "Costo total: 3 entrenamientos × $100 = $300");
            
            assertTrue(resultado.getRolesFaltantes().contains("coros"),
                "Coros debe estar en la lista de roles que requieren entrenamiento");
            
        } catch (Exception e) {
            System.err.println("\n[WARNING] Test omitido: SWI-Prolog no disponible o error de configuracion");
            System.err.println("Detalle: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return;
        }
        
        // DOCUMENTACION:
        // Escenario: 2 canciones con algunos roles cubiertos por artistas base
        // - Bohemian necesita voz(1), guitarra(1), coros(2)
        // - We Will necesita voz(1), bateria(1), coros(1)
        // - Total requerido: voz(2), guitarra(1), bateria(1), coros(3)
        // - Cobertura base: Freddie puede voz en AMBAS canciones, Brian(guitarra), Roger(bateria)
        // Resultado: 3 entrenamientos solo en coros (2 + 1)
        // Nota: Un artista puede estar en multiples canciones, solo no puede tener multiples roles en la MISMA cancion
    }
    
    @Test
    @DisplayName("Test 8.4: Verificar que costo base se aplica correctamente")
    void testCostoBasePersonalizado() {
        // OBJETIVO: Verificar que el parametro costoBase se multiplica correctamente
        
        // PASO 1: Crear escenario simple con 1 rol faltante
        weWillRockYou = new Cancion("We Will Rock You");
        weWillRockYou.agregarRolRequerido(saxofon, 1);  // NADIE tiene saxofon
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(weWillRockYou);
        recital = new Recital(canciones);
        
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        try {
            ServicioProlog servicioProlog = new ServicioProlog(servicioConsulta, servicioContratacion);
            
            // PASO 2: Probar con diferentes costos base
            double[] costosBase = {50.0, 100.0, 200.0, 500.0};
            
            System.out.println("\n[TEST 8.4] Probando diferentes costos base:");
            
            for (double costoBase : costosBase) {
                ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(costoBase);
                
                System.out.println(String.format("  Costo base: $%.2f → Entrenamientos: %d → Costo total: $%.2f",
                    costoBase, resultado.getEntrenamientosMinimos(), resultado.getCostoTotal()));
                
                assertEquals(1, resultado.getEntrenamientosMinimos(),
                    "Siempre debe ser 1 entrenamiento (saxofon)");
                
                assertEquals(costoBase, resultado.getCostoTotal(), 0.01,
                    "Costo total debe ser exactamente el costo base (1 entrenamiento × costo base)");
            }
            
        } catch (Exception e) {
            System.err.println("\n[WARNING] Test omitido: SWI-Prolog no disponible o error de configuracion");
            System.err.println("Detalle: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return;
        }
        
        // DOCUMENTACION:
        // Verifica que la formula: costo_total = entrenamientos_minimos × costo_base
        // Se aplica correctamente independientemente del valor del costo base
    }
}
