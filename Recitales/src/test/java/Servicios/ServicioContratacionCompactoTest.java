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
 * Cubre los casos principales de contratación masiva de artistas
 */
@DisplayName("Test de ServicioContratacion - Contratar Artistas Masivo")
public class ServicioContratacionCompactoTest {

    private ServicioContratacion servicioContratacion;
    private RepositorioRoles repositorioRoles;
    private RepositorioBandas repositorioBandas;
    
    private Rol rolVoz;
    private Rol rolGuitarra;
    private Rol rolBajo;
    private Rol rolPiano;
    
    private Banda bandaRock;
    
    @BeforeEach
    void setUp() {
        servicioContratacion = new ServicioContratacion();
        
        rolVoz = new Rol("Voz");
        rolGuitarra = new Rol("Guitarra");
        rolBajo = new Rol("Bajo");
        rolPiano = new Rol("Piano");
        
        repositorioRoles = new RepositorioRoles();
        repositorioRoles.agregarRol("Voz");
        repositorioRoles.agregarRol("Guitarra");
        repositorioRoles.agregarRol("Bajo");
        repositorioRoles.agregarRol("Piano");
        
        bandaRock = new Banda("Rock Band");
        repositorioBandas = new RepositorioBandas();
        repositorioBandas.agregarBanda("Rock Band");
    }
    
    private ServicioConsulta crearServicioConsulta(RepositorioArtistas repo, HashSet<Cancion> canciones) {
        Recital recital = new Recital(canciones);
        return new ServicioConsulta(repo, recital, repositorioRoles, repositorioBandas);
    }
    
    @Test
    @DisplayName("01 - Asigna el artista más barato")
    void testAsignaArtistaMasBarato() {
        ArtistaExterno artistaCaro = new ArtistaExterno("Caro", 5, 1000.0);
        artistaCaro.agregarRolHistorico(rolVoz);
        
        ArtistaExterno artistaBarato = new ArtistaExterno("Barato", 5, 100.0);
        artistaBarato.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaCaro);
        externos.add(artistaBarato);
        
        Cancion cancion = new Cancion("Test Song");
        cancion.agregarRolRequerido(rolVoz, 1);
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion);
        
        RepositorioArtistas repo = new RepositorioArtistas(new HashSet<>(), externos);
        ServicioConsulta servConsulta = crearServicioConsulta(repo, canciones);
        
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servConsulta);
        
        assertNull(resultado, "No debería haber roles faltantes");
        assertEquals(1, servicioContratacion.getContratos().size());
        
        Contrato contrato = servicioContratacion.getContratos().get(0);
        assertEquals("Barato", contrato.getArtista().getNombre());
        assertEquals(100.0, contrato.obtenerCostoContrato());
    }
    
    @Test
    @DisplayName("02 - NO asigna artista ya en otro rol de la misma canción")
    void testNoAsignaArtistaYaContratado() {
        ArtistaExterno multiTalento = new ArtistaExterno("Multi", 3, 200.0);
        multiTalento.agregarRolHistorico(rolVoz);
        multiTalento.agregarRolHistorico(rolGuitarra);
        
        ArtistaExterno guitarrista = new ArtistaExterno("Guitarrista", 3, 300.0);
        guitarrista.agregarRolHistorico(rolGuitarra);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(multiTalento);
        externos.add(guitarrista);
        
        Cancion cancion = new Cancion("Dual Role");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolGuitarra, 1);
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion);
        
        RepositorioArtistas repo = new RepositorioArtistas(new HashSet<>(), externos);
        ServicioConsulta servConsulta = crearServicioConsulta(repo, canciones);
        
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servConsulta);
        
        assertNull(resultado);
        assertEquals(2, servicioContratacion.getContratos().size());
        
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancion);
        String art1 = contratos.get(0).getArtista().getNombre();
        String art2 = contratos.get(1).getArtista().getNombre();
        
        assertNotEquals(art1, art2, "Los roles deben estar cubiertos por artistas diferentes");
    }
    
    @Test
    @DisplayName("03 - Detecta roles que requieren entrenamiento")
    void testDetectaRolesQueRequierenEntrenamiento() {
        ArtistaExterno vocalista = new ArtistaExterno("Vocalista", 3, 200.0);
        vocalista.agregarRolHistorico(rolVoz);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(vocalista);
        
        Cancion cancion = new Cancion("Song con Piano");
        cancion.agregarRolRequerido(rolVoz, 1);
        cancion.agregarRolRequerido(rolPiano, 1);  // No hay pianista
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion);
        
        RepositorioArtistas repo = new RepositorioArtistas(new HashSet<>(), externos);
        ServicioConsulta servConsulta = crearServicioConsulta(repo, canciones);
        
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servConsulta);
        
        assertNotNull(resultado, "Debe haber roles faltantes");
        assertTrue(resultado.containsKey(rolPiano));
        assertEquals(1, resultado.get(rolPiano).intValue());
        assertEquals(1, servicioContratacion.getContratos().size(), "Solo voz contratada");
    }
    
    @Test
    @DisplayName("04 - Aplica descuento por banda compartida")
    void testAplicaDescuentoPorBandaCompartida() {
        ArtistaDiscografica artistaBase = new ArtistaDiscografica("Base", 3, 100.0);
        artistaBase.agregarRolHistorico(rolGuitarra);
        artistaBase.agregarBandaHistorico(bandaRock);
        
        ArtistaExterno artistaExterno = new ArtistaExterno("Externo", 3, 400.0);
        artistaExterno.agregarRolHistorico(rolVoz);
        artistaExterno.agregarBandaHistorico(bandaRock);
        
        HashSet<ArtistaDiscografica> bases = new HashSet<>();
        bases.add(artistaBase);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(artistaExterno);
        
        Cancion cancion = new Cancion("Song con descuento");
        cancion.agregarRolRequerido(rolGuitarra, 1);
        cancion.agregarRolRequerido(rolVoz, 1);
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion);
        
        RepositorioArtistas repo = new RepositorioArtistas(bases, externos);
        ServicioConsulta servConsulta = crearServicioConsulta(repo, canciones);
        
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servConsulta);
        
        assertNull(resultado);
        assertEquals(2, servicioContratacion.getContratos().size());
        
        Contrato contratoExterno = null;
        for (Contrato c : servicioContratacion.getContratos()) {
            if (c.getArtista() instanceof ArtistaExterno) {
                contratoExterno = c;
                break;
            }
        }
        
        assertNotNull(contratoExterno);
        assertEquals(200.0, contratoExterno.obtenerCostoContrato(), 0.01, 
            "50% descuento: 400 * 0.5 = 200");
    }
    
    @Test
    @DisplayName("05 - Contratación masiva múltiples canciones")
    void testContratacionMasiva() {
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
        
        Cancion cancion1 = new Cancion("Song 1");
        cancion1.agregarRolRequerido(rolVoz, 1);
        cancion1.agregarRolRequerido(rolGuitarra, 1);
        
        Cancion cancion2 = new Cancion("Song 2");
        cancion2.agregarRolRequerido(rolVoz, 1);
        cancion2.agregarRolRequerido(rolBajo, 1);
        
        Cancion cancion3 = new Cancion("Song 3");
        cancion3.agregarRolRequerido(rolGuitarra, 1);
        cancion3.agregarRolRequerido(rolBajo, 1);
        
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion1);
        canciones.add(cancion2);
        canciones.add(cancion3);
        
        RepositorioArtistas repo = new RepositorioArtistas(new HashSet<>(), externos);
        ServicioConsulta servConsulta = crearServicioConsulta(repo, canciones);
        
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarParaTodo(servConsulta);
        
        assertNull(resultado);
        assertEquals(6, servicioContratacion.getContratos().size(), "2 contratos por canción");
        
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion1).size());
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion2).size());
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancion3).size());
    }
}
