import java.util.HashSet;
import java.io.IOException;
import Recital.*;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.*;
import Recital.Menu.MenuPrincipal;
import Imports.JsonAdapter;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            System.out.println("========== INICIALIZANDO SISTEMA DE RECITALES ==========\n");
            
            // Cargar datos desde JSON
            JsonAdapter cargador = new JsonAdapter(
                "data/artistas.json",
                "data/recital.json",
                "data/artistas-discografica.json"
            );
            
            System.out.println("Cargando datos desde JSON...\n");
            
            // Cargar artistas externos
            HashSet<ArtistaExterno> artistasExternos = cargador.cargarArtistasExternos();
            System.out.println("[OK] Artistas externos cargados: " + artistasExternos.size());
            
            // Cargar artistas base
            HashSet<ArtistaBase> artistasBase = cargador.cargarArtistasBase();
            System.out.println("[OK] Artistas base cargados: " + artistasBase.size());
            
            // Cargar canciones
            HashSet<Cancion> canciones = cargador.cargarCanciones();
            System.out.println("[OK] Canciones cargadas: " + canciones.size());
            
            // Crear catálogo de roles
            RolCatalogo rolCatalogo = new RolCatalogo();
            for (Cancion cancion : canciones) {
                for (Rol rol : cancion.getRolesRequeridos().keySet()) {
                    rolCatalogo.obtenerRol(rol);
                }
            }
            for (ArtistaBase artista : artistasBase) {
                for (Rol rol : artista.getRoles()) {
                    rolCatalogo.obtenerRol(rol);
                }
            }
            System.out.println("[OK] Catálogo de roles creado con roles únicos\n");
            
            // Crear servicio de contratación
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            
            // Crear el recital
            Recital recital = new Recital(artistasBase, artistasExternos, canciones, servicioContratacion);
            
            System.out.println("[SUCCESS] Sistema inicializado correctamente\n");
            System.out.println("Resumen:");
            System.out.println("  • Artistas base: " + artistasBase.size());
            System.out.println("  • Artistas externos disponibles: " + artistasExternos.size());
            System.out.println("  • Canciones del recital: " + canciones.size());
            System.out.println("\n========== INICIANDO MENÚ ==========\n");
            
            // Mostrar menú principal
            MenuPrincipal menu = new MenuPrincipal(recital, servicioContratacion, rolCatalogo);
            menu.mostrarMenu();
            
        } catch (IOException e) {
            System.err.println("[ERROR] Error al cargar archivos JSON: " + e.getMessage());
            System.err.println("\nAsegúrese de que los archivos existan en la carpeta 'data/':");
            System.err.println("  • data/artistas.json");
            System.err.println("  • data/recital.json");
            System.err.println("  • data/artistas-discografica.json");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Error fatal al iniciar el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
