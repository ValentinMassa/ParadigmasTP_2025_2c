import java.util.*;
import Recital.*;
import Repositorios.*;
import Artista.*;
import Servicios.*;

/**
 * Programa de prueba para verificar manualmente el calculo de Prolog
 * Escenario del Test 8.1: Todos los roles cubiertos por artistas base
 */
public class TestPrologManual {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("TEST MANUAL: Verificar calculo de entrenamientos con Prolog");
        System.out.println("=".repeat(70));
        
        try {
            // PASO 1: Crear catalogos de roles y bandas
            RolCatalogoMemory rolCatalogo = new RolCatalogoMemory();
            BandaCatalogoMemory bandaCatalogo = new BandaCatalogoMemory();
            
            Rol vozPrincipal = rolCatalogo.obtener("voz principal");
            Rol guitarra = rolCatalogo.obtener("guitarra electrica");
            Rol bateria = rolCatalogo.obtener("bateria");
            
            // PASO 2: Crear banda Queen
            Banda queen = new Banda("Queen");
            bandaCatalogo.agregar(queen);
            
            // PASO 3: Crear artistas base de Queen con sus roles
            Artista freddie = new Artista("Freddie Mercury");
            freddie.agregarRolEntrenado(vozPrincipal);
            
            Artista brian = new Artista("Brian May");
            brian.agregarRolEntrenado(guitarra);
            
            Artista roger = new Artista("Roger Taylor");
            roger.agregarRolEntrenado(bateria);
            
            // Agregar artistas a la banda
            queen.agregarMiembroBase(freddie);
            queen.agregarMiembroBase(brian);
            queen.agregarMiembroBase(roger);
            
            // PASO 4: Crear repositorio con artistas base
            HashSet<Artista> artistasBase = new HashSet<>();
            artistasBase.add(freddie);
            artistasBase.add(brian);
            artistasBase.add(roger);
            RepositorioArtistasMemory repositorioArtistas = new RepositorioArtistasMemory(artistasBase, new HashSet<>());
            
            // PASO 5: Crear cancion Bohemian Rhapsody
            Cancion bohemianRhapsody = new Cancion("Bohemian Rhapsody");
            bohemianRhapsody.agregarRolRequerido(vozPrincipal, 1);  // Freddie puede cubrir
            bohemianRhapsody.agregarRolRequerido(guitarra, 1);       // Brian puede cubrir
            bohemianRhapsody.agregarRolRequerido(bateria, 1);        // Roger puede cubrir
            
            // PASO 6: Crear recital con la cancion
            HashSet<Cancion> canciones = new HashSet<>();
            canciones.add(bohemianRhapsody);
            Recital recital = new Recital(canciones);
            
            // PASO 7: Crear servicios
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            ServicioConsulta servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
            
            // PASO 8: Mostrar info del escenario
            System.out.println("\nESCENARIO:");
            System.out.println("  Cancion: Bohemian Rhapsody");
            System.out.println("  Roles requeridos:");
            bohemianRhapsody.getRolesRequeridos().forEach((rol, cant) -> 
                System.out.println("    - " + rol.getNombre() + ": " + cant)
            );
            
            System.out.println("\n  Artistas base disponibles:");
            System.out.println("    - Freddie Mercury: voz principal");
            System.out.println("    - Brian May: guitarra electrica");
            System.out.println("    - Roger Taylor: bateria");
            
            // PASO 9: Calcular con Prolog
            System.out.println("\nEJECUTANDO CALCULO CON PROLOG...");
            ServicioProlog servicioProlog = new ServicioProlog(servicioConsulta, servicioContratacion);
            ServicioProlog.ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(100.0);
            
            // PASO 10: Mostrar resultados
            System.out.println("\n" + "=".repeat(70));
            System.out.println("RESULTADO DEL CALCULO:");
            System.out.println("=".repeat(70));
            System.out.println("  Entrenamientos minimos: " + resultado.getEntrenamientosMinimos());
            System.out.println("  Costo total: $" + String.format("%.2f", resultado.getCostoTotal()));
            System.out.println("  Roles faltantes: " + resultado.getRolesFaltantes());
            System.out.println("  Roles faltantes por rol: " + resultado.getRolesFaltantesPorRol());
            
            // PASO 11: Verificar si cumple expectativa
            System.out.println("\n" + "=".repeat(70));
            System.out.println("VERIFICACION:");
            System.out.println("=".repeat(70));
            
            boolean entrenamientosCorrecto = resultado.getEntrenamientosMinimos() == 0;
            boolean costoCorrecto = resultado.getCostoTotal() == 0.0;
            boolean rolesFaltantesVacio = resultado.getRolesFaltantes().isEmpty();
            
            System.out.println("  [" + (entrenamientosCorrecto ? "OK" : "ERROR") + "] Entrenamientos = 0? " + entrenamientosCorrecto);
            System.out.println("  [" + (costoCorrecto ? "OK" : "ERROR") + "] Costo = $0? " + costoCorrecto);
            System.out.println("  [" + (rolesFaltantesVacio ? "OK" : "ERROR") + "] Sin roles faltantes? " + rolesFaltantesVacio);
            
            if (entrenamientosCorrecto && costoCorrecto && rolesFaltantesVacio) {
                System.out.println("\n✓ TEST EXITOSO: El calculo es correcto!");
            } else {
                System.out.println("\n✗ TEST FALLIDO: El calculo no coincide con lo esperado");
            }
            
        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
