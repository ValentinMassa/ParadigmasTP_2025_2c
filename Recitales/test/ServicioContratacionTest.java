package test.java.Servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import Artista.*;
import Recital.*;
import Repositorios.*;
import Servicios.*;

/**
 * Test completo para ServicioContratacion - Comando ContratarArtistas
 * 
 * Cubre los casos principales de contratación masiva de artistas:
 * - Asignación por costo mínimo
 * - Restricción de un artista por canción
 * - Optimización por capacidad disponible
 * - Detección de necesidad de entrenamiento
 * - Casos sin capacidad disponible
 * - Descuentos por banda compartida
 * - Contratación múltiple (masiva)
 * - Ajuste por contratos existentes
 */
@DisplayName("Test de ServicioContratacion - Contratar Artistas Masivo")
public class ServicioContratacionTest {

    private ServicioContratacion servicioContratacion;
    private RepositorioRoles repositorioRoles;
    private RepositorioBandas repositorioBandas;
    
    // Roles comunes
    private Rol rolVoz;
    private Rol rolGuitarra;
    private Rol rolBajo;
    private Rol rolBateria;
    private Rol rolPiano;
    
    // Bandas comunes
    private Banda bandaRock;
    private Banda bandaPop;
    
    @BeforeEach
    void setUp() {
        // Inicializar servicios
        servicioContratacion = new ServicioContratacion();
        
        // Crear roles
        rolVoz = new Rol("Voz");
        rolGuitarra = new Rol("Guitarra");
        rolBajo = new Rol("Bajo");
        rolBateria = new Rol("Bateria");
        rolPiano = new Rol("Piano");
        
        HashSet<Rol> roles = new HashSet<>();
        roles.add(rolVoz);
        roles.add(rolGuitarra);
        roles.add(rolBajo);
        roles.add(rolBateria);
        roles.add(rolPiano);
        repositorioRoles = new RepositorioRoles(roles);
        
        // Crear bandas
        bandaRock = new Banda("Rock Band");
        bandaPop = new Banda("Pop Band");
        
        HashSet<Banda> bandas = new HashSet<>();
        bandas.add(bandaRock);
        bandas.add(bandaPop);
        repositorioBandas = new RepositorioBandas(bandas);
    }
    
    /**
     * Método auxiliar para crear el servicio de consulta con un recital
     */
    private ServicioConsulta crearServicioConsulta(RepositorioArtistas repoArtistas, HashSet<Cancion> canciones) {
        Recital recital = new Recital(canciones);
        return new ServicioConsulta(repoArtistas, recital, repositorioRoles, repositorioBandas);
    }
    
    @Test
    @DisplayName("01 - Asigna el artista más barato cuando hay varios candidatos")
    void testAsignaArtistaMasBarato() {
        // Arrange - Crear 3 artistas externos con diferentes costos para el mismo rol
        ArtistaExterno artistaCaro = new ArtistaExterno("Artista Caro", 5, 1000.0);
        artistaCaro.agregarRolHistorico(rolVoz);
        
        ArtistaExterno artistaMedio = new ArtistaExterno("Artista Medio", 5, 500.0);
        artistaMedio.agregarRolHistorico(rolVoz);
        
        ArtistaExterno artistaBarato = new ArtistaExterno("Artista Barato", 5, 100.0);
        artistaBarato.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaCaro);
        externos.add(artistaMedio);
        externos.add(artistaBarato);
        
        // Crear canción que requiere 1 voz
        Cancion cancion = new Cancion("Test Song");
        cancion.agregarRolRequerido(rolVoz, 1);
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion);
        
        RepositorioArtistas repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        ServicioConsulta servicioConsulta = crearServicioConsulta(repositorioArtistas, canciones);
        
        // Act - Contratar artistas
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert - Debe asignar al artista más barato
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(1, servicioContratacion.getContratos().size(), 
            "Debe haber exactamente 1 contrato");
        
        Contrato contrato = servicioContratacion.getContratos().get(0);
        assertEquals("Artista Barato", contrato.getArtista().getNombre(), 
            "Debe contratar al artista más barato");
        assertEquals(100.0, contrato.obtenerCostoContrato(), 
            "El costo debe ser el del artista más barato");
    }
    
    @Test
    @DisplayName("02 - NO asigna un artista que ya está en otro rol de la misma canción")
    void testNoAsignaArtistaYaContratadoEnMismaCancion() {
        // Arrange - Crear artista que puede cubrir 2 roles
        ArtistaExterno artista = new ArtistaExterno("Multi Talento", 3, 200.0);
        artista.agregarRolHistorico(rolVoz);
        artista.agregarRolHistorico(rolGuitarra);
        
        // Crear otro artista que solo toca guitarra
        ArtistaExterno guitarrista = new ArtistaExterno("Solo Guitarra", 3, 300.0);
        guitarrista.agregarRolHistorico(rolGuitarra);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artista);
        externos.add(guitarrista);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear canción que requiere voz y guitarra
        Cancion cancion = new Cancion("Dual Role Song");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolGuitarra, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(2, servicioContratacion.getContratos().size(), 
            "Debe haber 2 contratos");
        
        // Verificar que ambos roles están cubiertos por artistas diferentes
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancion);
        assertEquals(2, contratos.size(), "La canción debe tener 2 contratos");
        
        String artista1 = contratos.get(0).getArtista().getNombre();
        String artista2 = contratos.get(1).getArtista().getNombre();
        
        assertNotEquals(artista1, artista2, 
            "Los dos roles deben estar cubiertos por artistas diferentes");
    }
    
    @Test
    @DisplayName("03 - Optimiza por capacidad disponible cuando hay empate en costo")
    void testOptimizaPorCapacidadDisponible() {
        // Arrange - Crear 2 artistas externos con mismo costo pero diferente capacidad
        ArtistaExterno artistaCasiLleno = new ArtistaExterno("Casi Lleno", 3, 200.0);
        artistaCasiLleno.agregarRolHistorico(rolVoz);
        artistaCasiLleno.setCantCancionesAsignado(2); // Ya tiene 2 de 3
        
        ArtistaExterno artistaLibre = new ArtistaExterno("Libre", 5, 200.0);
        artistaLibre.agregarRolHistorico(rolVoz);
        artistaLibre.setCantCancionesAsignado(0); // Tiene toda su capacidad disponible
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaCasiLleno);
        externos.add(artistaLibre);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear 2 canciones que requieren voz
        Cancion cancion1 = new Cancion("Song 1");
        cancion1.agregarRolRequerido(rolVoz, 1);
        
        Cancion cancion2 = new Cancion("Song 2");
        cancion2.agregarRolRequerido(rolVoz, 1);
        
        recital.agregarCancion(cancion1);
        recital.agregarCancion(cancion2);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(2, servicioContratacion.getContratos().size(), 
            "Debe haber 2 contratos");
        
        // Verificar que ambos artistas fueron utilizados (optimización por capacidad)
        boolean artistaLibreContratado = false;
        boolean artistaCasiLlenoContratado = false;
        
        for (Contrato c : servicioContratacion.getContratos()) {
            if (c.getArtista().getNombre().equals("Libre")) {
                artistaLibreContratado = true;
            }
            if (c.getArtista().getNombre().equals("Casi Lleno")) {
                artistaCasiLlenoContratado = true;
            }
        }
        
        assertTrue(artistaLibreContratado, 
            "El artista con más capacidad debe ser contratado");
        assertTrue(artistaCasiLlenoContratado, 
            "El artista con menos capacidad también debe ser utilizado");
    }
    
    @Test
    @DisplayName("04 - Detecta correctamente roles que requieren entrenamiento")
    void testDetectaRolesQueRequierenEntrenamiento() {
        // Arrange - Crear artista que NO puede tocar piano
        ArtistaExterno vocalista = new ArtistaExterno("Solo Voz", 3, 200.0);
        vocalista.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(vocalista);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear canción que requiere voz (cubierto) y piano (no cubierto)
        Cancion cancion = new Cancion("Song con Piano");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolPiano, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNotNull(resultado, "Debe haber roles faltantes");
        assertTrue(resultado.containsKey(rolPiano), 
            "Piano debe estar en los roles que requieren entrenamiento");
        assertEquals(1, resultado.get(rolPiano).intValue(), 
            "Debe faltar 1 pianista");
        
        // Verificar que voz SÍ fue contratada
        assertEquals(1, servicioContratacion.getContratos().size(), 
            "Debe haber 1 contrato (voz)");
        assertEquals(rolVoz, servicioContratacion.getContratos().get(0).getRol(), 
            "El contrato debe ser para voz");
    }
    
    @Test
    @DisplayName("05 - Detecta roles imposibles de cubrir (artista sin capacidad)")
    void testDetectaRolesImposiblesDeCubrir() {
        // Arrange - Crear artista que ya agotó su máximo de canciones
        ArtistaExterno artistaSaturado = new ArtistaExterno("Saturado", 2, 200.0);
        artistaSaturado.agregarRolHistorico(rolVoz);
        artistaSaturado.setCantCancionesAsignado(2); // Ya tiene su máximo
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaSaturado);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear canción que requiere voz
        Cancion cancion = new Cancion("Song sin cubrir");
        cancion.agregarRolRequerido(rolVoz, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNotNull(resultado, "Debe haber roles imposibles de cubrir");
        assertTrue(resultado.containsKey(rolVoz), 
            "Voz debe estar en los roles que requieren entrenamiento/imposibles");
        assertEquals(1, resultado.get(rolVoz).intValue(), 
            "Debe faltar 1 vocalista");
        assertEquals(0, servicioContratacion.getContratos().size(), 
            "No debe haber contratos porque no hay capacidad");
    }
    
    @Test
    @DisplayName("06 - Aplica descuento por compartir banda con artista base")
    void testAplicaDescuentoPorBandaCompartida() {
        // Arrange - Crear artista base que pertenece a bandaRock
        ArtistaDiscografica artistaBase = new ArtistaDiscografica("Base Guitarrista", 3, 100.0);
        artistaBase.agregarRolHistorico(rolGuitarra);
        artistaBase.agregarBandaHistorico(bandaRock);
        
        // Crear artista externo que también pertenece a bandaRock
        ArtistaExterno artistaExterno = new ArtistaExterno("Externo Vocalista", 3, 400.0);
        artistaExterno.agregarRolHistorico(rolVoz);
        artistaExterno.agregarBandaHistorico(bandaRock); // Comparten banda
        
        HashSet<ArtistaDiscografica> bases = new HashSet<>();
        bases.add(artistaBase);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaExterno);
        
        repositorioArtistas = new RepositorioArtistas(bases, externos);
        
        // Crear canción que requiere guitarra y voz
        Cancion cancion = new Cancion("Song con descuento");
        cancion.agregarRolRequerido(rolGuitarra, 1);
        cancion.agregarRolRequerido(rolVoz, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(2, servicioContratacion.getContratos().size(), 
            "Debe haber 2 contratos");
        
        // Buscar el contrato del artista externo
        Contrato contratoExterno = null;
        for (Contrato c : servicioContratacion.getContratos()) {
            if (c.getArtista() instanceof ArtistaExterno) {
                contratoExterno = c;
                break;
            }
        }
        
        assertNotNull(contratoExterno, "Debe existir contrato del artista externo");
        
        // El costo debe ser 400 * 0.5 = 200 (50% de descuento)
        assertEquals(200.0, contratoExterno.obtenerCostoContrato(), 0.01,
            "El costo debe tener descuento del 50% por banda compartida");
    }
    
    @Test
    @DisplayName("07 - Contratación masiva: múltiples canciones con roles distribuidos")
    void testContratacionMasivaMultiplesCanciones() {
        // Arrange - Crear artistas con diferentes roles
        ArtistaExterno vocalista = new ArtistaExterno("Vocalista", 5, 200.0);
        vocalista.agregarRolHistorico(rolVoz);
        
        ArtistaExterno guitarrista = new ArtistaExterno("Guitarrista", 5, 250.0);
        guitarrista.agregarRolHistorico(rolGuitarra);
        
        ArtistaExterno bajista = new ArtistaExterno("Bajista", 5, 220.0);
        bajista.agregarRolHistorico(rolBajo);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(vocalista);
        externos.add(guitarrista);
        externos.add(bajista);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear 3 canciones con diferentes necesidades
        Cancion cancion1 = new Cancion("Song 1");
        cancion1.agregarRolRequerido(rolVoz, 1);
        cancion1.agregarRolRequerido(rolGuitarra, 1);
        
        Cancion cancion2 = new Cancion("Song 2");
        cancion2.agregarRolRequerido(rolVoz, 1);
        cancion2.agregarRolRequerido(rolBajo, 1);
        
        Cancion cancion3 = new Cancion("Song 3");
        cancion3.agregarRolRequerido(rolGuitarra, 1);
        cancion3.agregarRolRequerido(rolBajo, 1);
        
        recital.agregarCancion(cancion1);
        recital.agregarCancion(cancion2);
        recital.agregarCancion(cancion3);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(6, servicioContratacion.getContratos().size(), 
            "Debe haber 6 contratos en total (2 por canción)");
        
        // Verificar que cada canción tiene sus contratos
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion1).size(), 
            "Canción 1 debe tener 2 contratos");
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion2).size(), 
            "Canción 2 debe tener 2 contratos");
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion3).size(), 
            "Canción 3 debe tener 2 contratos");
    }
    
    @Test
    @DisplayName("08 - Ajusta roles faltantes considerando contratos ya existentes")
    void testAjustaRolesFaltantesPorContratosExistentes() {
        // Arrange - Crear artistas
        ArtistaExterno vocalista1 = new ArtistaExterno("Vocalista 1", 3, 200.0);
        vocalista1.agregarRolHistorico(rolVoz);
        
        ArtistaExterno vocalista2 = new ArtistaExterno("Vocalista 2", 3, 250.0);
        vocalista2.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(vocalista1);
        externos.add(vocalista2);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear canción que requiere 2 voces
        Cancion cancion = new Cancion("Dueto");
        cancion.agregarRolRequerido(rolVoz, 2);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Simular contrato ya existente
        Contrato contratoExistente = new Contrato(cancion, rolVoz, vocalista1, 200.0);
        servicioContratacion.agregarContrato(contratoExistente);
        vocalista1.setCantCancionesAsignado(1); // Actualizar contador
        
        // Act - Contratar para todo (debe detectar que ya hay 1 voz contratada)
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        
        // Debe haber 2 contratos en total (1 existente + 1 nuevo)
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion).size(), 
            "La canción debe tener 2 contratos de voz");
        
        // Verificar que ambos vocalistas están contratados
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancion);
        boolean tiene1 = contratos.stream().anyMatch(c -> c.getArtista().equals(vocalista1));
        boolean tiene2 = contratos.stream().anyMatch(c -> c.getArtista().equals(vocalista2));
        
        assertTrue(tiene1, "Debe mantener el contrato existente de Vocalista 1");
        assertTrue(tiene2, "Debe agregar nuevo contrato para Vocalista 2");
    }
    
    @Test
    @DisplayName("09 - Contratación masiva con artistas base y externos")
    void testContratacionConArtistaBaseYExternos() {
        // Arrange - Crear artista base
        ArtistaDiscografica artistaBase = new ArtistaDiscografica("Base Multirol", 5, 150.0);
        artistaBase.agregarRolHistorico(rolVoz);
        artistaBase.agregarRolHistorico(rolGuitarra);
        
        // Crear artista externo
        ArtistaExterno artistaExterno = new ArtistaExterno("Externo Bajista", 5, 300.0);
        artistaExterno.agregarRolHistorico(rolBajo);
        
        HashSet<ArtistaDiscografica> bases = new HashSet<>();
        bases.add(artistaBase);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaExterno);
        
        repositorioArtistas = new RepositorioArtistas(bases, externos);
        
        // Crear canción que requiere todos los roles
        Cancion cancion = new Cancion("Full Band Song");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolGuitarra, 1);
        cancion.agregarRolRequerido(rolBajo, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(3, servicioContratacion.getContratos().size(), 
            "Debe haber 3 contratos");
        
        // Verificar que el artista base fue priorizado (debe tener al menos 1 contrato)
        long contratosBase = servicioContratacion.getContratos().stream()
            .filter(c -> c.getArtista() instanceof ArtistaDiscografica)
            .count();
        
        assertTrue(contratosBase >= 1, 
            "Debe haber al menos 1 contrato de artista base");
        
        // Verificar que todos los roles están cubiertos
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancion);
        boolean tieneVoz = contratos.stream().anyMatch(c -> c.getRol().equals(rolVoz));
        boolean tieneGuitarra = contratos.stream().anyMatch(c -> c.getRol().equals(rolGuitarra));
        boolean tieneBajo = contratos.stream().anyMatch(c -> c.getRol().equals(rolBajo));
        
        assertTrue(tieneVoz, "Debe tener contrato para voz");
        assertTrue(tieneGuitarra, "Debe tener contrato para guitarra");
        assertTrue(tieneBajo, "Debe tener contrato para bajo");
    }
    
    @Test
    @DisplayName("10 - Verifica actualización de contadores de canciones asignadas")
    void testActualizaContadoresCancionesAsignadas() {
        // Arrange
        ArtistaExterno vocalista = new ArtistaExterno("Vocalista", 5, 200.0);
        vocalista.agregarRolHistorico(rolVoz);
        
        assertEquals(0, vocalista.getCantCancionesAsignadas(), 
            "Inicialmente debe tener 0 canciones asignadas");
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(vocalista);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear 3 canciones que requieren voz
        for (int i = 1; i <= 3; i++) {
            Cancion cancion = new Cancion("Song " + i);
            cancion.agregarRolRequerido(rolVoz, 1);
            recital.agregarCancion(cancion);
        }
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(3, servicioContratacion.getContratos().size(), 
            "Debe haber 3 contratos");
        assertEquals(3, vocalista.getCantCancionesAsignadas(), 
            "El artista debe tener 3 canciones asignadas");
    }
    
    @Test
    @DisplayName("11 - Caso complejo: priorización de roles escasos")
    void testPriorizacionRolesEscasos() {
        // Arrange - Crear un artista que puede cubrir 2 roles
        ArtistaExterno multiRol = new ArtistaExterno("Multi Rol", 1, 200.0);
        multiRol.agregarRolHistorico(rolVoz);
        multiRol.agregarRolHistorico(rolPiano);
        
        // Crear artistas específicos
        ArtistaExterno soloVoz = new ArtistaExterno("Solo Voz", 2, 250.0);
        soloVoz.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(multiRol);
        externos.add(soloVoz);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        
        // Crear canción que requiere voz y piano
        Cancion cancion = new Cancion("Song Compleja");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolPiano, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(2, servicioContratacion.getContratos().size(), 
            "Debe haber 2 contratos");
        
        // El sistema debe priorizar asignar multiRol a piano (más escaso)
        // y soloVoz a voz
        boolean pianoCubierto = servicioContratacion.getContratos().stream()
            .anyMatch(c -> c.getRol().equals(rolPiano));
        boolean vozCubierta = servicioContratacion.getContratos().stream()
            .anyMatch(c -> c.getRol().equals(rolVoz));
        
        assertTrue(pianoCubierto, "Piano debe estar cubierto");
        assertTrue(vozCubierta, "Voz debe estar cubierta");
    }
    
    @Test
    @DisplayName("12 - Sin descuento cuando no hay banda compartida")
    void testSinDescuentoSinBandaCompartida() {
        // Arrange - Artista base sin banda
        ArtistaDiscografica artistaBase = new ArtistaDiscografica("Base Sin Banda", 3, 100.0);
        artistaBase.agregarRolHistorico(rolGuitarra);
        
        // Artista externo sin banda
        ArtistaExterno artistaExterno = new ArtistaExterno("Externo Sin Banda", 3, 400.0);
        artistaExterno.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaDiscografica> bases = new HashSet<>();
        bases.add(artistaBase);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaExterno);
        
        repositorioArtistas = new RepositorioArtistas(bases, externos);
        
        // Crear canción
        Cancion cancion = new Cancion("Song sin descuento");
        cancion.agregarRolRequerido(rolGuitarra, 1);
        cancion.agregarRolRequerido(rolVoz, 1);
        recital.agregarCancion(cancion);
        
        servicioConsulta = new ServicioConsulta(
            repositorioArtistas, 
            recital, 
            repositorioRoles, 
            repositorioBandas
        );
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servicioConsulta);
        
        // Assert
        assertNull(resultado, "No debería haber roles faltantes");
        
        // Buscar contrato del artista externo
        Contrato contratoExterno = servicioContratacion.getContratos().stream()
            .filter(c -> c.getArtista() instanceof ArtistaExterno)
            .findFirst()
            .orElse(null);
        
        assertNotNull(contratoExterno, "Debe existir contrato del artista externo");
        assertEquals(400.0, contratoExterno.obtenerCostoContrato(), 0.01,
            "El costo NO debe tener descuento (sin banda compartida)");
    }
}
