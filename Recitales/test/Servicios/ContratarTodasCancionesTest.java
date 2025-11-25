package Servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import Recital.*;
import Repositorios.*;
import Artista.*;
import Menu.Auxiliares.EntrenadorMasivo;

@DisplayName("Tests de Contratar Todas las Canciones (Opción 4 - Bug Fix Crítico)")
public class ContratarTodasCancionesTest {
    
    private Recital recital;
    private ServicioContratacion servicioContratacion;
    private ServicioConsulta servicioConsulta;
    private RepositorioArtistasMemory repositorioArtistas;
    private RolCatalogoMemory rolCatalogo;
    
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol coros;
    private Rol bateria;
    
    @BeforeEach
    void setUp() {
        // Inicializar repositorios
        repositorioArtistas = new RepositorioArtistasMemory();
        rolCatalogo = new RolCatalogoMemory();
        
        // Crear roles
        vozPrincipal = new Rol("voz principal");
        guitarra = new Rol("guitarra electrica");
        coros = new Rol("coros");
        bateria = new Rol("bateria");
        
        rolCatalogo.agregar(vozPrincipal);
        rolCatalogo.agregar(guitarra);
        rolCatalogo.agregar(coros);
        rolCatalogo.agregar(bateria);
        
        // Crear recital
        recital = new Recital();
        
        // Crear servicios
        servicioConsulta = new ServicioConsulta(recital, repositorioArtistas, rolCatalogo);
        servicioContratacion = new ServicioContratacion(recital, repositorioArtistas);
    }
    
    @Test
    @DisplayName("TEST CRÍTICO: Contratar todas canciones SIN entrenamiento - Debe completar canciones posibles")
    void testContratarTodasCancionesSinEntrenamiento() {
        // Arrange - Crear 3 canciones, todas completables sin entrenamiento
        crearCancionSimple("Cancion 1", 1, 1, 0, 0);
        crearCancionSimple("Cancion 2", 1, 0, 0, 1);
        crearCancionSimple("Cancion 3", 1, 1, 0, 0);
        
        // Agregar artistas que cubren esos roles
        ArtistaDiscografica artista1 = new ArtistaDiscografica("Singer", 10, 100, null);
        artista1.entrenarRol(vozPrincipal);
        
        ArtistaDiscografica artista2 = new ArtistaDiscografica("Guitarist", 10, 100, null);
        artista2.entrenarRol(guitarra);
        
        ArtistaDiscografica artista3 = new ArtistaDiscografica("Drummer", 10, 100, null);
        artista3.entrenarRol(bateria);
        
        repositorioArtistas.agregar(artista1);
        repositorioArtistas.agregar(artista2);
        repositorioArtistas.agregar(artista3);
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertEquals(3, recital.getCanciones().size(), "Deberían haber 3 canciones");
        
        // Verificar que todas las canciones están completas
        for (Cancion cancion : recital.getCanciones()) {
            assertTrue(cancion.isCompleta(), "La canción '" + cancion.getNombre() + "' debería estar completa");
        }
        
        assertTrue(rolesFaltantes.isEmpty(), "No deberían quedar roles faltantes");
    }
    
    @Test
    @DisplayName("TEST CRÍTICO BUG FIX: Contratar con prioridad - Artista entrenado DEBE asignarse a canción que lo necesita")
    void testContratarTodasCancionesConPrioridad_ArtistaEntrenadoSeAsignaCorrectamente() {
        // Arrange - Recrear escenario del BUG: "Baby One More Time" necesita coros entrenado
        Cancion babyOneMoreTime = crearCancionSimple("Baby One More Time", 1, 1, 1, 0);
        Cancion otraCancion = crearCancionSimple("Otra Cancion", 1, 0, 1, 0);
        
        // Artista SIN coros entrenado inicialmente
        ArtistaDiscografica tonyKanal = new ArtistaDiscografica("Tony Kanal", 10, 100, null);
        tonyKanal.entrenarRol(vozPrincipal);
        tonyKanal.entrenarRol(guitarra);
        // NO tiene coros
        
        ArtistaDiscografica artista2 = new ArtistaDiscografica("Singer", 10, 100, null);
        artista2.entrenarRol(vozPrincipal);
        
        repositorioArtistas.agregar(tonyKanal);
        repositorioArtistas.agregar(artista2);
        
        // Simular que se entrena en "coros"
        tonyKanal.entrenarRol(coros);
        
        // Crear lista de entrenamientos realizados
        List<EntrenadorMasivo.EntrenamientoRealizado> entrenamientos = new ArrayList<>();
        entrenamientos.add(new EntrenadorMasivo.EntrenamientoRealizado(tonyKanal, coros));
        
        // Act - Usar método CON prioridad
        HashMap<Rol, Integer> rolesFaltantes = servicioContratacion.contratarParaTodoConPrioridad(
            servicioConsulta, 
            entrenamientos
        );
        
        // Assert
        // CRÍTICO: Verificar que Tony Kanal fue asignado a "Baby One More Time" en rol "coros"
        List<Contrato> contratosBaby = recital.getContratosDeCancion(babyOneMoreTime);
        
        boolean tonyEnCoros = contratosBaby.stream()
            .anyMatch(c -> c.getArtista().equals(tonyKanal) && c.getRol().equals(coros));
        
        assertTrue(tonyEnCoros, "Tony Kanal DEBE estar contratado en 'coros' para 'Baby One More Time'");
        assertTrue(babyOneMoreTime.isCompleta(), "'Baby One More Time' DEBE estar completa después del entrenamiento");
    }
    
    @Test
    @DisplayName("TEST CRÍTICO: Contratar con prioridad - Múltiples entrenamientos deben priorizarse todos")
    void testContratarTodasCancionesConPrioridad_MultiplesEntrenamientos() {
        // Arrange
        Cancion cancion1 = crearCancionSimple("Cancion 1", 0, 0, 1, 1); // necesita coros y batería
        Cancion cancion2 = crearCancionSimple("Cancion 2", 1, 1, 0, 0);
        
        ArtistaDiscografica artista1 = new ArtistaDiscografica("Artista1", 10, 100, null);
        artista1.entrenarRol(vozPrincipal);
        
        ArtistaDiscografica artista2 = new ArtistaDiscografica("Artista2", 10, 100, null);
        artista2.entrenarRol(guitarra);
        
        repositorioArtistas.agregar(artista1);
        repositorioArtistas.agregar(artista2);
        
        // Entrenar ambos en los roles faltantes
        artista1.entrenarRol(coros);
        artista2.entrenarRol(bateria);
        
        List<EntrenadorMasivo.EntrenamientoRealizado> entrenamientos = new ArrayList<>();
        entrenamientos.add(new EntrenadorMasivo.EntrenamientoRealizado(artista1, coros));
        entrenamientos.add(new EntrenadorMasivo.EntrenamientoRealizado(artista2, bateria));
        
        // Act
        servicioContratacion.contratarParaTodoConPrioridad(servicioConsulta, entrenamientos);
        
        // Assert
        List<Contrato> contratosCancion1 = recital.getContratosDeCancion(cancion1);
        
        boolean artista1EnCoros = contratosCancion1.stream()
            .anyMatch(c -> c.getArtista().equals(artista1) && c.getRol().equals(coros));
        
        boolean artista2EnBateria = contratosCancion1.stream()
            .anyMatch(c -> c.getArtista().equals(artista2) && c.getRol().equals(bateria));
        
        assertTrue(artista1EnCoros, "Artista1 debe estar en coros para Cancion 1");
        assertTrue(artista2EnBateria, "Artista2 debe estar en batería para Cancion 1");
        assertTrue(cancion1.isCompleta(), "Cancion 1 debe estar completa");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Restricción: Un artista solo un rol por canción")
    void testContratarTodasCanciones_UnArtistaUnRolPorCancion() {
        // Arrange
        Cancion cancion = crearCancionSimple("Test Song", 1, 1, 1, 0);
        
        ArtistaDiscografica multirol = new ArtistaDiscografica("Multirole Artist", 10, 100, null);
        multirol.entrenarRol(vozPrincipal);
        multirol.entrenarRol(guitarra);
        multirol.entrenarRol(coros);
        
        repositorioArtistas.agregar(multirol);
        
        // Act
        servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        List<Contrato> contratos = recital.getContratosDeCancion(cancion);
        
        // Contar cuántas veces aparece el artista en la misma canción
        long vecesContratado = contratos.stream()
            .filter(c -> c.getArtista().equals(multirol))
            .count();
        
        assertEquals(1, vecesContratado, 
            "El artista debe estar contratado solo UNA vez en la misma canción (un rol por canción)");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Mismo artista puede estar en múltiples canciones")
    void testContratarTodasCanciones_MismoArtistaVariasCanciones() {
        // Arrange
        Cancion cancion1 = crearCancionSimple("Cancion 1", 1, 0, 0, 0);
        Cancion cancion2 = crearCancionSimple("Cancion 2", 1, 0, 0, 0);
        Cancion cancion3 = crearCancionSimple("Cancion 3", 1, 0, 0, 0);
        
        ArtistaDiscografica cantante = new ArtistaDiscografica("Singer", 10, 100, null);
        cantante.entrenarRol(vozPrincipal);
        
        repositorioArtistas.agregar(cantante);
        
        // Act
        servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        List<Contrato> todosLosContratos = recital.getContratos();
        
        long vecesContratado = todosLosContratos.stream()
            .filter(c -> c.getArtista().equals(cantante))
            .count();
        
        assertEquals(3, vecesContratado, 
            "El mismo artista PUEDE estar en las 3 canciones diferentes");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Con artistas insuficientes quedan roles faltantes")
    void testContratarTodasCanciones_ArtistasInsuficientes() {
        // Arrange
        crearCancionSimple("Cancion 1", 1, 1, 1, 1);
        crearCancionSimple("Cancion 2", 1, 1, 1, 1);
        
        // Solo agregar un artista con un rol
        ArtistaDiscografica artista = new ArtistaDiscografica("Singer", 10, 100, null);
        artista.entrenarRol(vozPrincipal);
        
        repositorioArtistas.agregar(artista);
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertFalse(rolesFaltantes.isEmpty(), "Deberían quedar roles faltantes");
        assertTrue(rolesFaltantes.get(guitarra) > 0, "Debería faltar guitarra");
        assertTrue(rolesFaltantes.get(coros) > 0, "Debería faltar coros");
        assertTrue(rolesFaltantes.get(bateria) > 0, "Debería faltar batería");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Artistas externos también pueden ser asignados")
    void testContratarTodasCanciones_ConArtistasExternos() {
        // Arrange
        Cancion cancion = crearCancionSimple("Test Song", 1, 1, 0, 0);
        
        ArtistaExterno cantante = new ArtistaExterno("External Singer", 10, 150);
        cantante.entrenarRol(vozPrincipal);
        
        ArtistaExterno guitarrista = new ArtistaExterno("External Guitarist", 10, 150);
        guitarrista.entrenarRol(guitarra);
        
        repositorioArtistas.agregar(cantante);
        repositorioArtistas.agregar(guitarrista);
        
        // Act
        servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertTrue(cancion.isCompleta(), "La canción debe completarse con artistas externos");
        
        List<Contrato> contratos = recital.getContratosDeCancion(cancion);
        assertEquals(2, contratos.size(), "Deben haber 2 contratos");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Debe optimizar costos (artistas más baratos primero)")
    void testContratarTodasCanciones_OptimizaCostos() {
        // Arrange
        Cancion cancion = crearCancionSimple("Test Song", 1, 0, 0, 0);
        
        ArtistaDiscografica barato = new ArtistaDiscografica("Cheap", 5, 50, null);
        barato.entrenarRol(vozPrincipal);
        
        ArtistaDiscografica caro = new ArtistaDiscografica("Expensive", 10, 200, null);
        caro.entrenarRol(vozPrincipal);
        
        repositorioArtistas.agregar(caro); // Agregar primero el caro
        repositorioArtistas.agregar(barato);
        
        // Act
        servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        List<Contrato> contratos = recital.getContratosDeCancion(cancion);
        Contrato contratoVoz = contratos.stream()
            .filter(c -> c.getRol().equals(vozPrincipal))
            .findFirst()
            .orElseThrow();
        
        assertEquals(barato, contratoVoz.getArtista(), 
            "Debe seleccionar al artista más barato");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Verifica que lista de entrenamientos vacía funciona igual")
    void testContratarTodasCancionesConPrioridad_EntrenamientosVacios() {
        // Arrange
        crearCancionSimple("Cancion 1", 1, 1, 0, 0);
        
        ArtistaDiscografica artista1 = new ArtistaDiscografica("Singer", 10, 100, null);
        artista1.entrenarRol(vozPrincipal);
        
        ArtistaDiscografica artista2 = new ArtistaDiscografica("Guitarist", 10, 100, null);
        artista2.entrenarRol(guitarra);
        
        repositorioArtistas.agregar(artista1);
        repositorioArtistas.agregar(artista2);
        
        List<EntrenadorMasivo.EntrenamientoRealizado> entrenamientosVacios = new ArrayList<>();
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = servicioContratacion.contratarParaTodoConPrioridad(
            servicioConsulta, 
            entrenamientosVacios
        );
        
        // Assert
        assertTrue(rolesFaltantes.isEmpty(), "No deberían quedar roles faltantes");
        assertTrue(recital.getCanciones().get(0).isCompleta(), "La canción debe estar completa");
    }
    
    @Test
    @DisplayName("Contratar todas canciones con prioridad - No afecta canciones ya completas")
    void testContratarTodasCancionesConPrioridad_NoAfectaCancionesCompletas() {
        // Arrange
        Cancion cancionCompleta = crearCancionSimple("Completa", 1, 0, 0, 0);
        Cancion cancionIncompleta = crearCancionSimple("Incompleta", 0, 1, 0, 0);
        
        ArtistaDiscografica cantante = new ArtistaDiscografica("Singer", 10, 100, null);
        cantante.entrenarRol(vozPrincipal);
        
        ArtistaDiscografica guitarrista = new ArtistaDiscografica("Guitarist", 10, 100, null);
        guitarrista.entrenarRol(guitarra);
        
        repositorioArtistas.agregar(cantante);
        repositorioArtistas.agregar(guitarrista);
        
        // Completar la primera canción manualmente
        recital.agregarContrato(new Contrato(cancionCompleta, vozPrincipal, cantante, 100));
        
        int contratosAntesCompleta = recital.getContratosDeCancion(cancionCompleta).size();
        
        // Entrenar guitarrista en otro rol
        guitarrista.entrenarRol(bateria);
        List<EntrenadorMasivo.EntrenamientoRealizado> entrenamientos = new ArrayList<>();
        entrenamientos.add(new EntrenadorMasivo.EntrenamientoRealizado(guitarrista, bateria));
        
        // Act
        servicioContratacion.contratarParaTodoConPrioridad(servicioConsulta, entrenamientos);
        
        // Assert
        int contratosDespuesCompleta = recital.getContratosDeCancion(cancionCompleta).size();
        
        assertEquals(contratosAntesCompleta, contratosDespuesCompleta, 
            "La canción ya completa NO debe recibir más contratos");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Recital sin canciones debe retornar vacío")
    void testContratarTodasCanciones_RecitalVacio() {
        // Arrange - No agregar canciones
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertTrue(rolesFaltantes.isEmpty(), "No deben haber roles faltantes en recital vacío");
        assertEquals(0, recital.getContratos().size(), "No deben crearse contratos");
    }
    
    @Test
    @DisplayName("Contratar todas canciones - Artistas con banda aplican descuento correctamente")
    void testContratarTodasCanciones_DescuentoBanda() {
        // Arrange
        Banda banda = new Banda("The Band", 20); // 20% de descuento
        
        Cancion cancion = crearCancionSimple("Test Song", 1, 0, 0, 0);
        
        ArtistaDiscografica artistaBanda = new ArtistaDiscografica("Band Member", 10, 100, banda);
        artistaBanda.entrenarRol(vozPrincipal);
        
        repositorioArtistas.agregar(artistaBanda);
        
        // Act
        servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        List<Contrato> contratos = recital.getContratosDeCancion(cancion);
        Contrato contrato = contratos.get(0);
        
        // Costo esperado con descuento: 100 * 0.8 = 80
        assertEquals(80, contrato.getCosto(), 
            "El costo debe reflejar el descuento de banda (20%)");
    }
    
    // Método helper para crear canciones simples
    private Cancion crearCancionSimple(String nombre, int voz, int guitar, int chorus, int drums) {
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        if (voz > 0) rolesRequeridos.put(vozPrincipal, voz);
        if (guitar > 0) rolesRequeridos.put(guitarra, guitar);
        if (chorus > 0) rolesRequeridos.put(coros, chorus);
        if (drums > 0) rolesRequeridos.put(bateria, drums);
        
        Cancion cancion = new Cancion(nombre, rolesRequeridos);
        recital.agregarCancion(cancion);
        return cancion;
    }
}
