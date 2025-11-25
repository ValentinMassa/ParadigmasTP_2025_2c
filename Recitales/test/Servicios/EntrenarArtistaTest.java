package Servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import Artista.*;
import Recital.*;
import Repositorios.*;

@DisplayName("Tests de Entrenamiento de Artistas")
public class EntrenarArtistaTest {
    
    private ServicioEntrenamiento servicioEntrenamiento;
    private ServicioContratacion servicioContratacion;
    private RolCatalogoMemory rolCatalogo;
    private ArtistaExterno artistaExterno;
    private ArtistaDiscografica artistaBase;
    private Rol rolNuevo;
    private Rol rolExistente;
    
    @BeforeEach
    void setUp() {
        // Inicializar servicios
        servicioEntrenamiento = new ServicioEntrenamiento();
        servicioContratacion = new ServicioContratacion();
        rolCatalogo = new RolCatalogoMemory();
        
        // Crear roles
        rolExistente = new Rol("guitarra electrica");
        rolNuevo = new Rol("teclados");
        rolCatalogo.agregarRol(rolExistente);
        rolCatalogo.agregarRol(rolNuevo);
        
        // Crear artista externo de prueba
        artistaExterno = new ArtistaExterno("Prince", 3, 1600);
        artistaExterno.agregarRolHistorico(rolExistente);
        
        // Crear artista base de prueba
        artistaBase = new ArtistaDiscografica("Freddie Mercury", 100, 0);
        artistaBase.agregarRolHistorico(rolExistente);
    }
    
    @Test
    @DisplayName("Entrenar artista externo sin contratos - Debe entrenar exitosamente")
    void testEntrenarArtistaExterno() {
        // Act
        String resultado = servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        
        // Assert
        assertTrue(resultado.contains("exitosamente"), 
                  "El mensaje debería indicar que el entrenamiento fue exitoso");
        assertTrue(artistaExterno.puedeTocarRol(rolNuevo), 
                  "El artista debería poder tocar el nuevo rol después del entrenamiento");
    }
    
    @Test
    @DisplayName("Entrenar artista incrementa costo 50% - Debe aumentar el costo correctamente")
    void testEntrenarArtistaIncrementaCosto() {
        // Arrange
        double costoOriginal = artistaExterno.getCosto();
        double costoEsperado = costoOriginal * 1.5;
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        
        // Assert
        assertEquals(costoEsperado, artistaExterno.getCosto(), 0.01,
                    "El costo debería aumentar un 50% después del entrenamiento");
    }
    
    @Test
    @DisplayName("Entrenar artista agrega rol - El artista debe poder tocar el nuevo rol")
    void testEntrenarArtistaAgregaRol() {
        // Arrange
        assertFalse(artistaExterno.puedeTocarRol(rolNuevo), 
                   "El artista no debería tener el rol antes del entrenamiento");
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        
        // Assert
        assertTrue(artistaExterno.puedeTocarRol(rolNuevo),
                  "El artista debería poder tocar el nuevo rol después del entrenamiento");
        assertTrue(artistaExterno.puedeTocarRol(rolExistente),
                  "El artista debería mantener el rol anterior");
    }
    
    @Test
    @DisplayName("Entrenar artista en múltiples roles - Debe permitir entrenar varios roles")
    void testEntrenarArtistaMultiplesRoles() {
        // Arrange
        Rol otroRol = new Rol("bateria");
        rolCatalogo.agregarRol(otroRol);
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, otroRol);
        
        // Assert
        assertTrue(artistaExterno.puedeTocarRol(rolExistente), "Debería mantener rol original");
        assertTrue(artistaExterno.puedeTocarRol(rolNuevo), "Debería tener primer rol entrenado");
        assertTrue(artistaExterno.puedeTocarRol(otroRol), "Debería tener segundo rol entrenado");
    }
    
    @Test
    @DisplayName("Entrenar múltiples roles incrementa costo acumulativamente - Costo = original * 1.5 * 1.5")
    void testEntrenarArtistaMultiplesCostoAcumulativo() {
        // Arrange
        double costoOriginal = artistaExterno.getCosto();
        Rol otroRol = new Rol("bateria");
        rolCatalogo.agregarRol(otroRol);
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, otroRol);
        
        // Assert
        double costoEsperado = costoOriginal * 1.5 * 1.5; // 2.25 veces el original
        assertEquals(costoEsperado, artistaExterno.getCosto(), 0.01,
                    "El costo debería multiplicarse por 1.5 por cada entrenamiento");
    }
    
    @Test
    @DisplayName("Entrenar artista con contrato - Debe retornar error")
    void testEntrenarArtistaConContrato() {
        // Arrange
        Cancion cancion = new Cancion("Test Song", new java.util.HashMap<>());
        Contrato contrato = new Contrato(cancion, rolExistente, artistaExterno, 1600);
        servicioContratacion.agregarContrato(contrato);
        
        // Act
        String resultado = servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        
        // Assert
        assertTrue(resultado.contains("contrato vigente") || resultado.contains("contrato activo"),
                  "Debería indicar que el artista tiene un contrato vigente");
        assertFalse(artistaExterno.puedeTocarRol(rolNuevo),
                   "El artista no debería haber aprendido el nuevo rol");
    }
    
    @Test
    @DisplayName("Entrenar artista base - Debe retornar error")
    void testEntrenarArtistaBase() {
        // Act
        String resultado = servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaBase, rolNuevo);
        
        // Assert
        assertTrue(resultado.contains("Base") || resultado.contains("no puede ser entrenado"),
                  "Debería indicar que los artistas base no pueden ser entrenados");
        assertFalse(artistaBase.puedeTocarRol(rolNuevo),
                   "El artista base no debería haber aprendido el nuevo rol");
    }
    
    @Test
    @DisplayName("Entrenar artista con rol ya existente - Debe retornar error")
    void testEntrenarArtistaRolYaExistente() {
        // Act
        String resultado = servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolExistente);
        
        // Assert
        assertTrue(resultado.contains("ya posee") || resultado.contains("ya tiene"),
                  "Debería indicar que el artista ya posee ese rol");
    }
    
    @Test
    @DisplayName("Entrenar artista null - Debe lanzar excepción")
    void testEntrenarArtistaNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEntrenamiento.entrenarArtista(servicioContratacion, null, rolNuevo);
        }, "Debería lanzar IllegalArgumentException cuando el artista es null");
    }
    
    @Test
    @DisplayName("Entrenar con rol null - Debe lanzar excepción")
    void testEntrenarRolNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, null);
        }, "Debería lanzar IllegalArgumentException cuando el rol es null");
    }
    
    @Test
    @DisplayName("Entrenar con servicio contratación null - Debe lanzar excepción")
    void testEntrenarServicioContratacionNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioEntrenamiento.entrenarArtista(null, artistaExterno, rolNuevo);
        }, "Debería lanzar IllegalArgumentException cuando el servicio es null");
    }
    
    @Test
    @DisplayName("Entrenar artista externo que nunca fue entrenado - Método fueEntrenado debería retornar true")
    void testArtistaExternoFueEntrenadoEstado() {
        // Arrange
        assertFalse(artistaExterno.fueEntrenado(), 
                   "Artista no debería estar entrenado inicialmente");
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        
        // Assert
        assertTrue(artistaExterno.fueEntrenado(),
                  "Artista debería estar marcado como entrenado después del entrenamiento");
    }
    
    @Test
    @DisplayName("Obtener roles entrenados - Debe retornar lista correcta de roles entrenados")
    void testObtenerRolesEntrenados() {
        // Arrange
        Rol otroRol = new Rol("percusion");
        rolCatalogo.agregarRol(otroRol);
        
        // Act
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, rolNuevo);
        servicioEntrenamiento.entrenarArtista(servicioContratacion, artistaExterno, otroRol);
        
        // Assert
        assertEquals(2, artistaExterno.getRolesEntrenados().size(),
                    "Debería haber 2 roles entrenados");
        assertTrue(artistaExterno.getRolesEntrenados().contains(rolNuevo),
                  "Debería incluir el primer rol entrenado");
        assertTrue(artistaExterno.getRolesEntrenados().contains(otroRol),
                  "Debería incluir el segundo rol entrenado");
    }
}
