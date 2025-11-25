

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Recital.Banda;
import Recital.Cancion;
import Recital.Contrato;
import Recital.Rol;
import Repositorios.BandaCatalogoMemory;
import Repositorios.RepositorioContratos;
import Repositorios.RolCatalogoMemory;
import Servicios.CalculadoraCostos;
import Servicios.ContratadorMasivo;
import Servicios.ValidadorDisponibilidad;
/**
 * Test para ContratadorMasivo - prueba el algoritmo de contratación masiva.
 * Verifica optimización por costos, escasez de roles, y capacidad.
 */
public class ContratadorMasivoTest {
    
    private RolCatalogoMemory rolCatalogo;
    private BandaCatalogoMemory bandaCatalogo;
    private RepositorioContratos repositorioContratos;
    private CalculadoraCostos calculadora;
    private ValidadorDisponibilidad validador;
    private ContratadorMasivo contratadorMasivo;
    
    // Roles
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol bateria;
    private Rol bajo;
    private Rol teclado;
    
    // Bandas
    private Banda queen;
    private Banda beatles;
    private Banda pinkFloyd;
    
    @BeforeEach
    public void setUp() {
        // Inicializar catálogos
        rolCatalogo = new RolCatalogoMemory();
        bandaCatalogo = new BandaCatalogoMemory();
        
        // Crear roles
        vozPrincipal = rolCatalogo.agregarRol("voz principal");
        if (vozPrincipal == null) vozPrincipal = rolCatalogo.getRol("voz principal");
        
        guitarra = rolCatalogo.agregarRol("guitarra");
        if (guitarra == null) guitarra = rolCatalogo.getRol("guitarra");
        
        bateria = rolCatalogo.agregarRol("bateria");
        if (bateria == null) bateria = rolCatalogo.getRol("bateria");
        
        bajo = rolCatalogo.agregarRol("bajo");
        if (bajo == null) bajo = rolCatalogo.getRol("bajo");
        
        teclado = rolCatalogo.agregarRol("teclado");
        if (teclado == null) teclado = rolCatalogo.getRol("teclado");
        
        // Crear bandas
        queen = bandaCatalogo.agregarBanda("Queen");
        if (queen == null) queen = bandaCatalogo.getBanda("Queen");
        
        beatles = bandaCatalogo.agregarBanda("The Beatles");
        if (beatles == null) beatles = bandaCatalogo.getBanda("The Beatles");
        
        pinkFloyd = bandaCatalogo.agregarBanda("Pink Floyd");
        if (pinkFloyd == null) pinkFloyd = bandaCatalogo.getBanda("Pink Floyd");
        
        // Inicializar servicios
        repositorioContratos = new RepositorioContratos();
        calculadora = new CalculadoraCostos();
        validador = new ValidadorDisponibilidad();
        contratadorMasivo = new ContratadorMasivo(repositorioContratos, calculadora, validador);
    }
    
    /**
     * TEST 1: Caso base - Contrata artistas base correctamente.
     */
    @Test
    public void testCasoBase_ContrataArtistasBase() {
        // Artistas base (Queen)
        ArtistaDiscografica freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarBandaHistorico(queen);
        
        ArtistaDiscografica brian = new ArtistaDiscografica("Brian May", 10, 80.0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarBandaHistorico(queen);
        
        List<ArtistaDiscografica> artistasBase = new ArrayList<>();
        artistasBase.add(freddie);
        artistasBase.add(brian);
        
        // Canciones
        Cancion bohemian = new Cancion("Bohemian Rhapsody");
        bohemian.agregarRolRequerido(vozPrincipal, 1);
        bohemian.agregarRolRequerido(guitarra, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(vozPrincipal, 1);
        rolesCancion.put(guitarra, 1);
        rolesFaltantes.put(bohemian, rolesCancion);
        
        // Ejecutar contratación
        HashMap<Rol, Integer> resultado = contratadorMasivo.contratarParaTodo(
            rolesFaltantes, artistasBase, new ArrayList<>());
        
        // Verificar resultado
        assertNull(resultado, "No debería requerir entrenamiento");
        
        // Verificar contratos creados
        List<Contrato> contratos = repositorioContratos.obtenerTodos();
        assertEquals(2, contratos.size(), "Deberían haberse creado 2 contratos");
        
        // Verificar contadores de artistas
        assertEquals(1, freddie.getCantCancionesAsignadas(), "Freddie debería estar en 1 canción");
        assertEquals(1, brian.getCantCancionesAsignadas(), "Brian debería estar en 1 canción");
    }
    
    /**
     * TEST 2: No asigna dos roles al mismo artista en la misma canción.
     */
    @Test
    public void testNoAsignaDosRolesMismoArtistaEnCancion() {
        // Artista que puede tocar múltiples roles
        ArtistaDiscografica versatil = new ArtistaDiscografica("Artista Versátil", 10, 100.0);
        versatil.agregarRolHistorico(vozPrincipal);
        versatil.agregarRolHistorico(guitarra);
        versatil.agregarBandaHistorico(queen);
        
        List<ArtistaDiscografica> artistasBase = new ArrayList<>();
        artistasBase.add(versatil);
        
        // Canción que necesita voz y guitarra
        Cancion cancion = new Cancion("Canción Test");
        cancion.agregarRolRequerido(vozPrincipal, 1);
        cancion.agregarRolRequerido(guitarra, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(vozPrincipal, 1);
        rolesCancion.put(guitarra, 1);
        rolesFaltantes.put(cancion, rolesCancion);
        
        // Ejecutar contratación
        contratadorMasivo.contratarParaTodo(rolesFaltantes, artistasBase, new ArrayList<>());
        
        // Verificar que solo se le asignó UN rol
        List<Contrato> contratos = repositorioContratos.obtenerPorCancion(cancion);
        assertEquals(1, contratos.size(), 
            "El artista versátil solo debería tener 1 contrato en esta canción");
    }
    
    /**
     * TEST 3: Optimización de costos - Elige al más barato entre externos.
     */
    @Test
    public void testOptimizacionCostos_EligeMasBarato() {
        // Dos guitarristas externos con costos diferentes
        ArtistaExterno guitarristaBarato = new ArtistaExterno("Guitarrista Barato", 10, 50.0);
        guitarristaBarato.agregarRolHistorico(guitarra);
        guitarristaBarato.agregarBandaHistorico(pinkFloyd);
        
        ArtistaExterno guitarristaCaro = new ArtistaExterno("Guitarrista Caro", 10, 300.0);
        guitarristaCaro.agregarRolHistorico(guitarra);
        guitarristaCaro.agregarBandaHistorico(beatles);
        
        List<ArtistaExterno> artistasExternos = new ArrayList<>();
        artistasExternos.add(guitarristaCaro);  // Agregar primero el caro
        artistasExternos.add(guitarristaBarato);
        
        // Canción que solo necesita guitarra
        Cancion cancion = new Cancion("Solo de Guitarra");
        cancion.agregarRolRequerido(guitarra, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(guitarra, 1);
        rolesFaltantes.put(cancion, rolesCancion);
        
        // Ejecutar contratación
        contratadorMasivo.contratarParaTodo(rolesFaltantes, new ArrayList<>(), artistasExternos);
        
        // Verificar que se contrató al barato
        List<Contrato> contratos = repositorioContratos.obtenerTodos();
        assertEquals(1, contratos.size(), "Debería haber 1 contrato");
        assertEquals(guitarristaBarato, contratos.get(0).getArtista(), 
            "Debería haberse contratado al guitarrista barato");
    }
    
    /**
     * TEST 4: Preserva artistas escasos para roles con pocos candidatos.
     */
    @Test
    public void testPreservaArtistasEscasos() {
        // Artista con capacidad 1 que puede tocar teclado (escaso) y guitarra
        ArtistaExterno tecladista = new ArtistaExterno("Tecladista Escaso", 1, 100.0);
        tecladista.agregarRolHistorico(teclado);
        tecladista.agregarRolHistorico(guitarra);
        tecladista.agregarBandaHistorico(pinkFloyd);
        
        // Otro guitarrista disponible
        ArtistaExterno otroGuitarrista = new ArtistaExterno("Otro Guitarrista", 5, 150.0);
        otroGuitarrista.agregarRolHistorico(guitarra);
        otroGuitarrista.agregarBandaHistorico(beatles);
        
        List<ArtistaExterno> artistasExternos = new ArrayList<>();
        artistasExternos.add(tecladista);
        artistasExternos.add(otroGuitarrista);
        
        // Dos canciones: una necesita guitarra, otra necesita teclado
        Cancion cancionGuitarra = new Cancion("Canción Guitarra");
        cancionGuitarra.agregarRolRequerido(guitarra, 1);
        
        Cancion cancionTeclado = new Cancion("Canción Teclado");
        cancionTeclado.agregarRolRequerido(teclado, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        
        HashMap<Rol, Integer> rolesGuitarra = new HashMap<>();
        rolesGuitarra.put(guitarra, 1);
        rolesFaltantes.put(cancionGuitarra, rolesGuitarra);
        
        HashMap<Rol, Integer> rolesTeclado = new HashMap<>();
        rolesTeclado.put(teclado, 1);
        rolesFaltantes.put(cancionTeclado, rolesTeclado);
        
        // Ejecutar contratación
        contratadorMasivo.contratarParaTodo(rolesFaltantes, new ArrayList<>(), artistasExternos);
        
        // Verificar que el tecladista fue usado para teclado, no guitarra
        List<Contrato> contratosTecladista = repositorioContratos.obtenerTodos().stream()
            .filter(c -> c.getArtista().equals(tecladista))
            .toList();
        
        assertEquals(1, contratosTecladista.size(), "Tecladista debería tener 1 contrato");
        assertEquals(teclado, contratosTecladista.get(0).getRol(), 
            "Tecladista debería usarse para teclado (rol escaso)");
    }
    
    /**
     * TEST 5: Descuento por banda compartida (50%).
     */
    @Test
    public void testDescuentoPorBandaCompartida() {
        // Artista base de Queen
        ArtistaDiscografica brian = new ArtistaDiscografica("Brian May", 10, 80.0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarBandaHistorico(queen);
        
        List<ArtistaDiscografica> artistasBase = new ArrayList<>();
        artistasBase.add(brian);
        
        // Artista externo también de Queen
        ArtistaExterno externoQueen = new ArtistaExterno("Externo Queen", 5, 200.0);
        externoQueen.agregarRolHistorico(teclado);
        externoQueen.agregarBandaHistorico(queen);
        
        List<ArtistaExterno> artistasExternos = new ArrayList<>();
        artistasExternos.add(externoQueen);
        
        // Canción que necesita guitarra y teclado
        Cancion cancion = new Cancion("Canción Completa");
        cancion.agregarRolRequerido(guitarra, 1);
        cancion.agregarRolRequerido(teclado, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(guitarra, 1);
        rolesCancion.put(teclado, 1);
        rolesFaltantes.put(cancion, rolesCancion);
        
        // Ejecutar contratación
        contratadorMasivo.contratarParaTodo(rolesFaltantes, artistasBase, artistasExternos);
        
        // Buscar contrato del externo
        List<Contrato> contratos = repositorioContratos.obtenerTodos();
        Contrato contratoExterno = contratos.stream()
            .filter(c -> c.getArtista().equals(externoQueen))
            .findFirst()
            .orElse(null);
        
        assertNotNull(contratoExterno, "Debería haber contrato para el externo");
        
        // Verificar descuento: 200 * 0.5 = 100
        assertEquals(100.0, contratoExterno.obtenerCostoContrato(), 0.01, 
            "Debería aplicar 50% de descuento por compartir banda");
    }
    
    /**
     * TEST 6: Detecta roles que requieren entrenamiento.
     */
    @Test
    public void testDetectaRolesQueRequierenEntrenamiento() {
        // Crear rol que nadie puede tocar
        Rol saxofon = rolCatalogo.agregarRol("saxofon");
        if (saxofon == null) saxofon = rolCatalogo.getRol("saxofon");
        
        // Canción que necesita saxofon
        Cancion cancion = new Cancion("Careless Whisper");
        cancion.agregarRolRequerido(saxofon, 2);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(saxofon, 2);
        rolesFaltantes.put(cancion, rolesCancion);
        
        // Ejecutar contratación (sin artistas disponibles)
        HashMap<Rol, Integer> resultado = contratadorMasivo.contratarParaTodo(
            rolesFaltantes, new ArrayList<>(), new ArrayList<>());
        
        // Verificar resultado
        assertNotNull(resultado, "Debería retornar roles que requieren entrenamiento");
        assertTrue(resultado.containsKey(saxofon), "Debería indicar que saxofon requiere entrenamiento");
        assertEquals(2, resultado.get(saxofon), "Debería indicar que faltan 2 saxofonistas");
    }
    
    /**
     * TEST 7: No requiere entrenamiento cuando todos los roles están cubiertos.
     */
    @Test
    public void testNoRequiereEntrenamiento() {
        // Artista que cubre el rol requerido
        ArtistaDiscografica freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarBandaHistorico(queen);
        
        List<ArtistaDiscografica> artistasBase = new ArrayList<>();
        artistasBase.add(freddie);
        
        // Canción simple
        Cancion cancion = new Cancion("Bohemian Rhapsody");
        cancion.agregarRolRequerido(vozPrincipal, 1);
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        HashMap<Rol, Integer> rolesCancion = new HashMap<>();
        rolesCancion.put(vozPrincipal, 1);
        rolesFaltantes.put(cancion, rolesCancion);
        
        // Ejecutar contratación
        HashMap<Rol, Integer> resultado = contratadorMasivo.contratarParaTodo(
            rolesFaltantes, artistasBase, new ArrayList<>());
        
        // Verificar resultado
        assertNull(resultado, "No debería requerir entrenamiento");
    }
    
    /**
     * TEST 8: Capacidad insuficiente - Artista con límite de canciones.
     */
    @Test
    public void testCapacidadInsuficiente() {
        // Artista con capacidad máxima de 2 canciones
        ArtistaDiscografica guitarrista = new ArtistaDiscografica("Guitarrista Limitado", 2, 100.0);
        guitarrista.agregarRolHistorico(guitarra);
        guitarrista.agregarBandaHistorico(queen);
        
        List<ArtistaDiscografica> artistasBase = new ArrayList<>();
        artistasBase.add(guitarrista);
        
        // 5 canciones que necesitan guitarra
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Cancion cancion = new Cancion("Canción " + i);
            cancion.agregarRolRequerido(guitarra, 1);
            
            HashMap<Rol, Integer> rolesCancion = new HashMap<>();
            rolesCancion.put(guitarra, 1);
            rolesFaltantes.put(cancion, rolesCancion);
        }
        
        // Ejecutar contratación
        HashMap<Rol, Integer> resultado = contratadorMasivo.contratarParaTodo(
            rolesFaltantes, artistasBase, new ArrayList<>());
        
        // Verificar resultado
        assertNotNull(resultado, "Debería detectar falta de capacidad");
        assertTrue(resultado.containsKey(guitarra), "Debería indicar que guitarra requiere más artistas");
        assertEquals(3, resultado.get(guitarra), "Deberían faltar 3 guitarristas (5 - 2)");
        
        // Verificar que el guitarrista llegó a su límite
        assertEquals(2, guitarrista.getCantCancionesAsignadas(), 
            "El guitarrista debería estar en 2 canciones (su máximo)");
    }
}