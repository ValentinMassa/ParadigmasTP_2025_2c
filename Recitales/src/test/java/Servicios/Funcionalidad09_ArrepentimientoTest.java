package test.java.Servicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.HashSet;

import Recital.*;
import Repositorios.*;
import Servicios.*;
import Artista.*;

@DisplayName("Tests Funcionalidad 9: Arrepentimiento (Quitar Artista Contratado)")
public class Funcionalidad09_ArrepentimientoTest {
    
    private Recital recital;
    private ServicioContratacion servicioContratacion;
    private ServicioConsulta servicioConsulta;
    private RepositorioArtistas repositorioArtistas;
    private RepositorioRoles rolCatalogo;
    private RepositorioBandas bandaCatalogo;
    
    private Rol vozPrincipal;
    private Rol guitarra;
    
    private Cancion cancion1;
    
    private ArtistaDiscografica freddie;
    private ArtistaDiscografica brian;
    
    @BeforeEach
    void setUp() {
        // Crear repositorios
        repositorioArtistas = new RepositorioArtistas();
        rolCatalogo = new RepositorioRoles();
        bandaCatalogo = new RepositorioBandas();
        
        // Crear roles y agregarlos al catálogo
        vozPrincipal = rolCatalogo.agregarRol("voz principal");
        if (vozPrincipal == null) vozPrincipal = rolCatalogo.getRol("voz principal");
        guitarra = rolCatalogo.agregarRol("guitarra electrica");
        if (guitarra == null) guitarra = rolCatalogo.getRol("guitarra electrica");
        
        // Crear canción
        cancion1 = new Cancion("Creep");
        cancion1.agregarRolRequerido(vozPrincipal, 1);
        cancion1.agregarRolRequerido(guitarra, 1);
        
        // Crear recital con canciones
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion1);
        recital = new Recital(canciones);
        
        // Crear artistas
        freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        
        brian = new ArtistaDiscografica("Brian May", 10, 100.0);
        brian.agregarRolHistorico(guitarra);
        
        // Agregar artistas al repositorio
        HashSet<ArtistaDiscografica> artistasBase = new HashSet<>();
        artistasBase.add(freddie);
        artistasBase.add(brian);
        repositorioArtistas = new RepositorioArtistas(artistasBase, new HashSet<>());
        
        // Crear servicios
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        servicioContratacion = new ServicioContratacion();
    }
    
    @Test
    @DisplayName("Test 9.1: Eliminar contratos de un artista actualiza su contador")
    void testEliminarContratosActualizaContador() {
        // Arrange - Contratar a Freddie
        Contrato contrato = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contrato);
        freddie.setCantCancionesAsignado(1);
        
        assertEquals(1, freddie.getCantCancionesAsignadas(), 
            "Freddie debería tener 1 canción asignada antes del arrepentimiento");
        
        // Act - Eliminar contratos de Freddie
        servicioContratacion.eliminarContratosDeArtista(freddie);
        
        // Assert
        assertEquals(0, freddie.getCantCancionesAsignadas(), 
            "Freddie debería tener 0 canciones asignadas después del arrepentimiento");
        assertEquals(0, servicioContratacion.getContratos().size(), 
            "No deberían quedar contratos de Freddie");
    }
    
    @Test
    @DisplayName("Test 9.2: Eliminar contratos de artista con múltiples contratos")
    void testEliminarMultiplesContratos() {
        // Arrange - Crear otra canción y contratar a Freddie en ambas
        Cancion cancion2 = new Cancion("Wonderwall");
        cancion2.agregarRolRequerido(vozPrincipal, 1);
        
        Contrato contrato1 = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        Contrato contrato2 = new Contrato(cancion2, vozPrincipal, freddie, 100.0);
        
        servicioContratacion.agregarContrato(contrato1);
        servicioContratacion.agregarContrato(contrato2);
        freddie.setCantCancionesAsignado(2);
        
        assertEquals(2, servicioContratacion.getContratos().size());
        assertEquals(2, freddie.getCantCancionesAsignadas());
        
        // Act
        servicioContratacion.eliminarContratosDeArtista(freddie);
        
        // Assert
        assertEquals(0, servicioContratacion.getContratos().size(), 
            "Todos los contratos de Freddie deberían ser eliminados");
        assertEquals(0, freddie.getCantCancionesAsignadas(), 
            "Contador de Freddie debería ser 0");
    }
    
    @Test
    @DisplayName("Test 9.3: Eliminar contratos no afecta a otros artistas")
    void testEliminarContratosNoAfectaOtrosArtistas() {
        // Arrange - Contratar a Freddie y Brian
        Contrato contrato1 = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        Contrato contrato2 = new Contrato(cancion1, guitarra, brian, 100.0);
        
        servicioContratacion.agregarContrato(contrato1);
        servicioContratacion.agregarContrato(contrato2);
        freddie.setCantCancionesAsignado(1);
        brian.setCantCancionesAsignado(1);
        
        // Act - Eliminar solo a Freddie
        servicioContratacion.eliminarContratosDeArtista(freddie);
        
        // Assert
        assertEquals(1, servicioContratacion.getContratos().size(), 
            "Debería quedar 1 contrato (el de Brian)");
        assertEquals(0, freddie.getCantCancionesAsignadas(), 
            "Freddie debería tener 0");
        assertEquals(1, brian.getCantCancionesAsignadas(), 
            "Brian NO debería verse afectado");
        
        // Verificar que el contrato de Brian sigue ahí
        List<Contrato> contratos = servicioContratacion.getContratos();
        assertTrue(contratos.stream().anyMatch(c -> c.getArtista().equals(brian)), 
            "El contrato de Brian debería seguir existiendo");
    }
    
    @Test
    @DisplayName("Test 9.4: getArtistasContratados incluye artistas de discográfica")
    void testGetArtistasContratadosIncluyeArtistasBase() {
        // Arrange - Contratar artista de discográfica
        Contrato contrato = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contrato);
        
        // Act
        HashSet<Artista> artistasContratados = servicioConsulta.getArtistasContratados(servicioContratacion);
        
        // Assert
        assertEquals(1, artistasContratados.size(), 
            "Debería haber 1 artista contratado");
        assertTrue(artistasContratados.contains(freddie), 
            "Freddie (ArtistaDiscografica) debería estar en la lista");
    }
    
    @Test
    @DisplayName("Test 9.5: getArtistasContratados incluye artistas externos")
    void testGetArtistasContratadosIncluyeArtistasExternos() {
        // Arrange - Crear y contratar artista externo
        ArtistaExterno madonna = new ArtistaExterno("Madonna", 10, 150.0);
        madonna.agregarRolHistorico(vozPrincipal);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(madonna);
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        Contrato contrato = new Contrato(cancion1, vozPrincipal, madonna, 150.0);
        servicioContratacion.agregarContrato(contrato);
        
        // Act
        HashSet<Artista> artistasContratados = servicioConsulta.getArtistasContratados(servicioContratacion);
        
        // Assert
        assertEquals(1, artistasContratados.size());
        assertTrue(artistasContratados.contains(madonna), 
            "Madonna (ArtistaExterno) debería estar en la lista");
    }
    
    @Test
    @DisplayName("Test 9.6: getArtistasContratados NO incluye artistas sin contratos")
    void testGetArtistasContratadosNoIncluyeSinContratos() {
        // Arrange - No contratar a nadie
        
        // Act
        HashSet<Artista> artistasContratados = servicioConsulta.getArtistasContratados(servicioContratacion);
        
        // Assert
        assertTrue(artistasContratados.isEmpty(), 
            "No debería haber artistas contratados");
    }
    
    @Test
    @DisplayName("Test 9.7: Eliminar contratos sin artista contratado no causa error")
    void testEliminarContratosSinArtista() {
        // Arrange - Artista sin contratos
        assertEquals(0, freddie.getCantCancionesAsignadas());
        
        // Act - No debería lanzar excepción
        assertDoesNotThrow(() -> {
            servicioContratacion.eliminarContratosDeArtista(freddie);
        });
        
        // Assert
        assertEquals(0, freddie.getCantCancionesAsignadas());
    }
    
    @Test
    @DisplayName("Test 9.8: Permite recontratar artista después de arrepentirse")
    void testPermiteRecontratar() {
        // Arrange - Contratar y eliminar
        Contrato contrato1 = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contrato1);
        freddie.setCantCancionesAsignado(1);
        
        servicioContratacion.eliminarContratosDeArtista(freddie);
        assertEquals(0, freddie.getCantCancionesAsignadas());
        
        // Act - Recontratar
        Contrato contrato2 = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contrato2);
        freddie.setCantCancionesAsignado(1);
        
        // Assert
        assertEquals(1, freddie.getCantCancionesAsignadas(), 
            "Freddie debería poder ser recontratado");
        assertEquals(1, servicioContratacion.getContratos().size());
    }
}
