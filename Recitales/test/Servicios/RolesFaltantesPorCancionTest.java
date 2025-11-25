package Servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import Recital.*;

@DisplayName("Tests de Cálculo de Roles Faltantes por Canción")
public class RolesFaltantesPorCancionTest {
    
    private Cancion cancion;
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol bateria;
    private Rol bajo;
    
    @BeforeEach
    void setUp() {
        // Crear roles
        vozPrincipal = new Rol("voz principal");
        guitarra = new Rol("guitarra electrica");
        bateria = new Rol("bateria");
        bajo = new Rol("bajo");
        
        // Crear canción de prueba que requiere 1 voz, 1 guitarra, 1 batería, 1 bajo
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        rolesRequeridos.put(vozPrincipal, 1);
        rolesRequeridos.put(guitarra, 1);
        rolesRequeridos.put(bateria, 1);
        rolesRequeridos.put(bajo, 1);
        
        cancion = new Cancion("Test Song", rolesRequeridos);
    }
    
    @Test
    @DisplayName("Roles faltantes sin contratos - Debe retornar todos los roles requeridos")
    void testRolesFaltantesCancionSinContratos() {
        // Arrange
        List<Contrato> contratosVacios = new ArrayList<>();
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(contratosVacios);
        
        // Assert
        assertEquals(4, rolesFaltantes.size(), "Debería haber 4 roles faltantes");
        assertEquals(1, rolesFaltantes.get(vozPrincipal), "Debería faltar 1 voz principal");
        assertEquals(1, rolesFaltantes.get(guitarra), "Debería faltar 1 guitarra");
        assertEquals(1, rolesFaltantes.get(bateria), "Debería faltar 1 batería");
        assertEquals(1, rolesFaltantes.get(bajo), "Debería faltar 1 bajo");
    }
    
    @Test
    @DisplayName("Roles faltantes con algunos contratos - Debe retornar solo los faltantes")
    void testRolesFaltantesCancionConAlgunosContratos() {
        // Arrange
        Artista artistaVoz = crearArtistaMock("Singer");
        Artista artistaGuitarra = crearArtistaMock("Guitarist");
        
        List<Contrato> contratos = new ArrayList<>();
        contratos.add(new Contrato(cancion, vozPrincipal, artistaVoz, 100));
        contratos.add(new Contrato(cancion, guitarra, artistaGuitarra, 100));
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(contratos);
        
        // Assert
        assertEquals(4, rolesFaltantes.size(), "Debería haber 4 roles en el mapa");
        assertEquals(0, rolesFaltantes.get(vozPrincipal), "Voz principal no debería faltar");
        assertEquals(0, rolesFaltantes.get(guitarra), "Guitarra no debería faltar");
        assertEquals(1, rolesFaltantes.get(bateria), "Debería faltar 1 batería");
        assertEquals(1, rolesFaltantes.get(bajo), "Debería faltar 1 bajo");
    }
    
    @Test
    @DisplayName("Roles faltantes con canción completa - Debe retornar mapa con valores en 0")
    void testRolesFaltantesCancionCompleta() {
        // Arrange
        List<Contrato> contratos = new ArrayList<>();
        contratos.add(new Contrato(cancion, vozPrincipal, crearArtistaMock("Singer"), 100));
        contratos.add(new Contrato(cancion, guitarra, crearArtistaMock("Guitarist"), 100));
        contratos.add(new Contrato(cancion, bateria, crearArtistaMock("Drummer"), 100));
        contratos.add(new Contrato(cancion, bajo, crearArtistaMock("Bassist"), 100));
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(contratos);
        
        // Assert
        assertEquals(0, rolesFaltantes.get(vozPrincipal), "No debería faltar voz principal");
        assertEquals(0, rolesFaltantes.get(guitarra), "No debería faltar guitarra");
        assertEquals(0, rolesFaltantes.get(bateria), "No debería faltar batería");
        assertEquals(0, rolesFaltantes.get(bajo), "No debería faltar bajo");
    }
    
    @Test
    @DisplayName("Roles faltantes con rol múltiple - Debe calcular correctamente múltiples instancias")
    void testRolesFaltantesConRolMultiple() {
        // Arrange - Canción que requiere 2 voz principal
        HashMap<Rol, Integer> rolesRequeridos = new HashMap<>();
        rolesRequeridos.put(vozPrincipal, 2);
        rolesRequeridos.put(guitarra, 1);
        
        Cancion cancionDosVoces = new Cancion("Two Voices Song", rolesRequeridos);
        
        // Solo contratar 1 voz
        List<Contrato> contratos = new ArrayList<>();
        contratos.add(new Contrato(cancionDosVoces, vozPrincipal, crearArtistaMock("Singer1"), 100));
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancionDosVoces.getRolesFaltantes(contratos);
        
        // Assert
        assertEquals(1, rolesFaltantes.get(vozPrincipal), "Debería faltar 1 voz principal");
        assertEquals(1, rolesFaltantes.get(guitarra), "Debería faltar 1 guitarra");
    }
    
    @Test
    @DisplayName("Roles faltantes con contratos de otra canción - No debe afectar el cálculo")
    void testRolesFaltantesConContratosOtraCancion() {
        // Arrange
        Cancion otraCancion = new Cancion("Other Song", new HashMap<>());
        
        List<Contrato> contratos = new ArrayList<>();
        // Estos contratos son para otra canción, no deberían afectar
        contratos.add(new Contrato(otraCancion, vozPrincipal, crearArtistaMock("Singer"), 100));
        contratos.add(new Contrato(otraCancion, guitarra, crearArtistaMock("Guitarist"), 100));
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(contratos);
        
        // Assert
        assertEquals(1, rolesFaltantes.get(vozPrincipal), "Debería faltar 1 voz principal");
        assertEquals(1, rolesFaltantes.get(guitarra), "Debería faltar 1 guitarra");
        assertEquals(1, rolesFaltantes.get(bateria), "Debería faltar 1 batería");
        assertEquals(1, rolesFaltantes.get(bajo), "Debería faltar 1 bajo");
    }
    
    @Test
    @DisplayName("Roles faltantes con canción sin roles requeridos - Debe retornar mapa vacío")
    void testRolesFaltantesCancionSinRolesRequeridos() {
        // Arrange
        Cancion cancionVacia = new Cancion("Empty Song", new HashMap<>());
        List<Contrato> contratos = new ArrayList<>();
        
        // Act
        HashMap<Rol, Integer> rolesFaltantes = cancionVacia.getRolesFaltantes(contratos);
        
        // Assert
        assertTrue(rolesFaltantes.isEmpty(), "Debería retornar un mapa vacío");
    }
    
    // Método helper para crear artistas mock simples
    private Artista crearArtistaMock(String nombre) {
        return new Artista(nombre, 10, 100) {
            @Override
            public boolean puedeSerEntrenado() {
                return false;
            }
        };
    }
}
