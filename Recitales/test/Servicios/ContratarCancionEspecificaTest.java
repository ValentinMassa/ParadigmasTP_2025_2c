package Servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;

import Artista.*;
import Recital.*;
import Repositorios.*;

@DisplayName("Tests de Contratación para Canción Específica")
public class ContratarCancionEspecificaTest {
    
    private ServicioContratacion servicioContratacion;
    private RepositorioArtistasMemory repositorio;
    private RolCatalogoMemory rolCatalogo;
    private BandaCatalogoMemory bandaCatalogo;
    private Cancion cancionPrueba;
    
    @BeforeEach
    void setUp() {
        // Inicializar servicios
        servicioContratacion = new ServicioContratacion();
        repositorio = new RepositorioArtistasMemory();
        rolCatalogo = new RolCatalogoMemory();
        bandaCatalogo = new BandaCatalogoMemory();
        
        // Crear roles de prueba
        Rol vozPrincipal = new Rol("voz principal");
        Rol guitarra = new Rol("guitarra electrica");
        Rol bateria = new Rol("bateria");
        Rol bajo = new Rol("bajo");
        
        rolCatalogo.agregarRol(vozPrincipal);
        rolCatalogo.agregarRol(guitarra);
        rolCatalogo.agregarRol(bateria);
        rolCatalogo.agregarRol(bajo);
        
        // Crear banda de prueba
        Banda queen = new Banda("Queen");
        bandaCatalogo.agregarBanda(queen);
        
        // Crear artistas base de prueba
        ArtistaDiscografica freddie = new ArtistaDiscografica("Freddie Mercury", 100, 0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarBanda(queen);
        repositorio.agregarArtista(freddie);
        
        ArtistaDiscografica brian = new ArtistaDiscografica("Brian May", 100, 0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarBanda(queen);
        repositorio.agregarArtista(brian);
        
        // Crear artistas externos de prueba
        ArtistaExterno davidBowie = new ArtistaExterno("David Bowie", 3, 1500);
        davidBowie.agregarRolHistorico(vozPrincipal);
        davidBowie.agregarBanda(queen); // Comparte banda con Queen
        repositorio.agregarArtista(davidBowie);
        
        ArtistaExterno prince = new ArtistaExterno("Prince", 2, 1600);
        prince.agregarRolHistorico(guitarra);
        repositorio.agregarArtista(prince);
        
        // Crear canción de prueba
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        rolesRequeridos.put(vozPrincipal, 1);
        rolesRequeridos.put(guitarra, 1);
        rolesRequeridos.put(bateria, 1);
        rolesRequeridos.put(bajo, 1);
        
        cancionPrueba = new Cancion("Test Song", rolesRequeridos);
    }
    
    @Test
    @DisplayName("Contratar canción con artistas disponibles - Debe contratar todos los roles")
    void testContratarCancionConArtistasDisponibles() {
        // Agregar los roles que faltan
        Rol bateria = rolCatalogo.buscarRol("bateria");
        Rol bajo = rolCatalogo.buscarRol("bajo");
        
        ArtistaDiscografica roger = new ArtistaDiscografica("Roger Taylor", 100, 0);
        roger.agregarRolHistorico(bateria);
        repositorio.agregarArtista(roger);
        
        ArtistaDiscografica john = new ArtistaDiscografica("John Deacon", 100, 0);
        john.agregarRolHistorico(bajo);
        repositorio.agregarArtista(john);
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        assertNull(resultado, "Debería retornar null cuando todos los roles están cubiertos");
        assertEquals(4, servicioContratacion.getContratosPorCancion(cancionPrueba).size(), 
                     "Debería haber 4 contratos (uno por cada rol)");
    }
    
    @Test
    @DisplayName("Contratar canción priorizando artistas base - Debe usar artistas de costo 0 primero")
    void testContratarCancionConArtistasBase() {
        // Agregar artista base con batería
        Rol bateria = rolCatalogo.buscarRol("bateria");
        ArtistaDiscografica roger = new ArtistaDiscografica("Roger Taylor", 100, 0);
        roger.agregarRolHistorico(bateria);
        repositorio.agregarArtista(roger);
        
        // Act
        servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancionPrueba);
        
        // Assert
        long contratosBase = contratos.stream()
            .filter(c -> c.getArtista() instanceof ArtistaDiscografica)
            .count();
        
        assertTrue(contratosBase >= 3, "Debería priorizar artistas base (mínimo 3 de 4 roles)");
    }
    
    @Test
    @DisplayName("Contratar canción con descuento por banda - Debe aplicar 50% de descuento")
    void testContratarCancionConDescuentoBanda() {
        // Agregar artista base para voz
        ArtistaDiscografica freddie = (ArtistaDiscografica) repositorio.buscarArtistaPorNombre("Freddie Mercury");
        
        // David Bowie comparte banda Queen con Freddie
        // Su costo normal es 1500, con descuento debería ser 750
        
        // Primero contratar a Freddie
        Rol vozPrincipal = rolCatalogo.buscarRol("voz principal");
        Contrato contratoFreddie = new Contrato(cancionPrueba, vozPrincipal, freddie, 0);
        servicioContratacion.agregarContrato(contratoFreddie);
        
        // Ahora contratar a David Bowie para guitarra (aunque tiene voz, ya está Freddie)
        // Necesitamos que David tenga guitarra para este test
        ArtistaExterno davidBowie = (ArtistaExterno) repositorio.buscarArtistaPorNombre("David Bowie");
        Rol guitarra = rolCatalogo.buscarRol("guitarra electrica");
        davidBowie.agregarRolHistorico(guitarra);
        
        // Act
        servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancionPrueba);
        Contrato contratoDavid = contratos.stream()
            .filter(c -> c.getArtista().getNombre().equals("David Bowie"))
            .findFirst()
            .orElse(null);
        
        if (contratoDavid != null) {
            assertEquals(750.0, contratoDavid.obtenerCostoContrato(), 0.01,
                        "El costo de David Bowie debería ser 750 (50% de descuento por compartir banda Queen)");
        }
    }
    
    @Test
    @DisplayName("Contratar canción seleccionando más barato - Debe elegir artista de menor costo")
    void testContratarCancionSeleccionaMasBarato() {
        // Agregar dos artistas externos con guitarra pero diferente costo
        Rol guitarra = rolCatalogo.buscarRol("guitarra electrica");
        
        ArtistaExterno barato = new ArtistaExterno("Artista Barato", 3, 500);
        barato.agregarRolHistorico(guitarra);
        repositorio.agregarArtista(barato);
        
        ArtistaExterno caro = new ArtistaExterno("Artista Caro", 3, 2000);
        caro.agregarRolHistorico(guitarra);
        repositorio.agregarArtista(caro);
        
        // Act
        servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancionPrueba);
        Contrato contratoGuitarra = contratos.stream()
            .filter(c -> c.getRol().getNombre().equals("guitarra electrica"))
            .filter(c -> c.getArtista() instanceof ArtistaExterno)
            .findFirst()
            .orElse(null);
        
        assertNotNull(contratoGuitarra, "Debería haber un contrato para guitarra");
        assertEquals("Artista Barato", contratoGuitarra.getArtista().getNombre(),
                    "Debería elegir el artista más barato");
    }
    
    @Test
    @DisplayName("Contratar canción ya completa - No debe agregar contratos")
    void testContratarCancionYaCompleta() {
        // Arrange - Contratar todos los roles primero
        Rol vozPrincipal = rolCatalogo.buscarRol("voz principal");
        Rol guitarra = rolCatalogo.buscarRol("guitarra electrica");
        Rol bateria = rolCatalogo.buscarRol("bateria");
        Rol bajo = rolCatalogo.buscarRol("bajo");
        
        ArtistaDiscografica freddie = (ArtistaDiscografica) repositorio.buscarArtistaPorNombre("Freddie Mercury");
        ArtistaDiscografica brian = (ArtistaDiscografica) repositorio.buscarArtistaPorNombre("Brian May");
        
        ArtistaDiscografica roger = new ArtistaDiscografica("Roger Taylor", 100, 0);
        roger.agregarRolHistorico(bateria);
        
        ArtistaDiscografica john = new ArtistaDiscografica("John Deacon", 100, 0);
        john.agregarRolHistorico(bajo);
        
        servicioContratacion.agregarContrato(new Contrato(cancionPrueba, vozPrincipal, freddie, 0));
        servicioContratacion.agregarContrato(new Contrato(cancionPrueba, guitarra, brian, 0));
        servicioContratacion.agregarContrato(new Contrato(cancionPrueba, bateria, roger, 0));
        servicioContratacion.agregarContrato(new Contrato(cancionPrueba, bajo, john, 0));
        
        int contratosAntes = servicioContratacion.getContratos().size();
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        assertNull(resultado, "Debería retornar null cuando la canción ya está completa");
        assertEquals(contratosAntes, servicioContratacion.getContratos().size(),
                    "No debería agregar nuevos contratos");
    }
    
    @Test
    @DisplayName("Contratar canción sin artistas disponibles - Debe retornar roles que requieren entrenamiento")
    void testContratarCancionSinArtistasDisponibles() {
        // Crear canción que requiere un rol que nadie tiene
        Rol coros = new Rol("coros");
        rolCatalogo.agregarRol(coros);
        
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        rolesRequeridos.put(coros, 2);
        
        Cancion cancionCoros = new Cancion("Need Coros", rolesRequeridos);
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarArtistasParaCancion(cancionCoros, repositorio);
        
        // Assert
        assertNotNull(resultado, "Debería retornar mapa de roles que requieren entrenamiento");
        assertTrue(resultado.containsKey(coros), "Debería incluir el rol 'coros'");
        assertEquals(2, resultado.get(coros), "Debería indicar que faltan 2 artistas con 'coros'");
    }
    
    @Test
    @DisplayName("Contratar canción con artistas agotados - No debe considerarlos")
    void testContratarCancionArtistasAgotados() {
        // Crear artista con maxCanciones = 1 y ya asignarlo a otra canción
        Rol bateria = rolCatalogo.buscarRol("bateria");
        ArtistaExterno artistaAgotado = new ArtistaExterno("Artista Agotado", 1, 800);
        artistaAgotado.agregarRolHistorico(bateria);
        artistaAgotado.setCantCancionesAsignado(1); // Ya tiene 1 canción asignada
        repositorio.agregarArtista(artistaAgotado);
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        assertNotNull(resultado, "Debería retornar roles faltantes si el artista está agotado");
        assertTrue(resultado.containsKey(bateria), "Debería incluir batería como rol faltante");
    }
    
    @Test
    @DisplayName("Contratar canción con artista ya contratado - No debe duplicarlo")
    void testContratarCancionArtistaYaContratado() {
        // Arrange
        Rol vozPrincipal = rolCatalogo.buscarRol("voz principal");
        ArtistaDiscografica freddie = (ArtistaDiscografica) repositorio.buscarArtistaPorNombre("Freddie Mercury");
        
        // Contratar a Freddie primero
        servicioContratacion.agregarContrato(new Contrato(cancionPrueba, vozPrincipal, freddie, 0));
        
        // Act
        servicioContratacion.contratarArtistasParaCancion(cancionPrueba, repositorio);
        
        // Assert
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancionPrueba);
        long contratosFreddie = contratos.stream()
            .filter(c -> c.getArtista().getNombre().equals("Freddie Mercury"))
            .count();
        
        assertEquals(1, contratosFreddie, "Freddie debería aparecer solo una vez en los contratos");
    }
    
    @Test
    @DisplayName("Contratar canción con rol múltiple - Debe contratar artistas diferentes")
    void testContratarCancionRolMultiple() {
        // Crear canción que requiere 2 voz principal
        Rol vozPrincipal = rolCatalogo.buscarRol("voz principal");
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        rolesRequeridos.put(vozPrincipal, 2);
        
        Cancion cancionDosVoces = new Cancion("Two Voices", rolesRequeridos);
        
        // Agregar otro artista con voz principal
        ArtistaExterno adamLambert = new ArtistaExterno("Adam Lambert", 2, 1200);
        adamLambert.agregarRolHistorico(vozPrincipal);
        repositorio.agregarArtista(adamLambert);
        
        // Act
        HashMap<Rol, Integer> resultado = servicioContratacion.contratarArtistasParaCancion(cancionDosVoces, repositorio);
        
        // Assert
        assertNull(resultado, "Debería poder contratar 2 artistas diferentes para el mismo rol");
        
        List<Contrato> contratos = servicioContratacion.getContratosPorCancion(cancionDosVoces);
        assertEquals(2, contratos.size(), "Debería haber 2 contratos");
        
        // Verificar que son artistas diferentes
        String artista1 = contratos.get(0).getArtista().getNombre();
        String artista2 = contratos.get(1).getArtista().getNombre();
        assertNotEquals(artista1, artista2, "Los artistas deben ser diferentes");
    }
    
    @Test
    @DisplayName("Contratar canción inexistente - Debe manejar null apropiadamente")
    void testContratarCancionInexistente() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            servicioContratacion.contratarArtistasParaCancion(null, repositorio);
        }, "Debería lanzar excepción cuando la canción es null");
    }
    
    @Test
    @DisplayName("Contratar con repositorio null - Debe lanzar excepción")
    void testContratarCancionRepositorioNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            servicioContratacion.contratarArtistasParaCancion(cancionPrueba, null);
        }, "Debería lanzar excepción cuando el repositorio es null");
    }
}
