import java.util.HashSet;
import Recital.*;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.*;
import Recital.Banda.*;
import Recital.Menu.MenuPrincipal;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            // Inicializar datos de prueba
            System.out.println("========== INICIALIZANDO SISTEMA DE RECITALES ==========\n");
            
            // Crear roles
            HashSet<Rol> rolesBase = crearRoles();
            
            // Crear catálogo de roles
            RolCatalogo rolCatalogo = new RolCatalogo();
            for (Rol rol : rolesBase) {
                rolCatalogo.obtenerRol(rol);
            }
            
            // Crear bandas
            HashSet<Banda> bandasQueen = new HashSet<>();
            bandasQueen.add(new Banda("Queen"));
            
            // Crear artistas base (contratados de la discográfica)
            HashSet<ArtistaBase> artistasBase = crearArtistasBase(rolesBase, bandasQueen);
            
            // Crear artistas externos disponibles
            HashSet<ArtistaExterno> artistasExternos = crearArtistasExternos(rolesBase);
            
            // Crear canciones del recital
            HashSet<Cancion> canciones = crearCanciones(rolesBase);
            
            // Crear servicio de contratación
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            
            // Crear el recital
            Recital recital = new Recital(artistasBase, artistasExternos, canciones, servicioContratacion);
            
            System.out.println("✅ Sistema inicializado correctamente\n");
            System.out.println("Artistas base: " + artistasBase.size());
            System.out.println("Artistas externos disponibles: " + artistasExternos.size());
            System.out.println("Canciones del recital: " + canciones.size());
            System.out.println("\n========== INICIANDO MENÚ ==========\n");
            
            // Mostrar menú principal
            MenuPrincipal menu = new MenuPrincipal(recital, servicioContratacion, rolCatalogo);
            menu.mostrarMenu();
            
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static HashSet<Rol> crearRoles() {
        HashSet<Rol> roles = new HashSet<>();
        roles.add(new Rol("voz principal"));
        roles.add(new Rol("guitarra eléctrica"));
        roles.add(new Rol("bajo"));
        roles.add(new Rol("batería"));
        roles.add(new Rol("piano"));
        roles.add(new Rol("coros"));
        return roles;
    }
    
    private static HashSet<ArtistaBase> crearArtistasBase(HashSet<Rol> rolesBase, HashSet<Banda> bandas) {
        HashSet<ArtistaBase> artistasBase = new HashSet<>();
        
        // Brian May
        HashSet<Rol> rolesBrian = new HashSet<>();
        rolesBrian.add(buscarRol(rolesBase, "guitarra eléctrica"));
        rolesBrian.add(buscarRol(rolesBase, "coros"));
        artistasBase.add(new ArtistaBase("Brian May", 100, 0, rolesBrian, bandas));
        
        // Roger Taylor
        HashSet<Rol> rolesRoger = new HashSet<>();
        rolesRoger.add(buscarRol(rolesBase, "batería"));
        rolesRoger.add(buscarRol(rolesBase, "coros"));
        artistasBase.add(new ArtistaBase("Roger Taylor", 100, 0, rolesRoger, bandas));
        
        // John Deacon
        HashSet<Rol> rolesJohn = new HashSet<>();
        rolesJohn.add(buscarRol(rolesBase, "bajo"));
        artistasBase.add(new ArtistaBase("John Deacon", 100, 0, rolesJohn, bandas));
        
        return artistasBase;
    }
    
    private static HashSet<ArtistaExterno> crearArtistasExternos(HashSet<Rol> rolesBase) {
        HashSet<ArtistaExterno> externos = new HashSet<>();
        
        // George Michael
        HashSet<Rol> rolesGeorge = new HashSet<>();
        rolesGeorge.add(buscarRol(rolesBase, "voz principal"));
        HashSet<Banda> bandasGeorge = new HashSet<>();
        bandasGeorge.add(new Banda("Wham!"));
        externos.add(new ArtistaExterno("George Michael", 3, 1000, rolesGeorge, bandasGeorge));
        
        // Elton John
        HashSet<Rol> rolesElton = new HashSet<>();
        rolesElton.add(buscarRol(rolesBase, "voz principal"));
        rolesElton.add(buscarRol(rolesBase, "piano"));
        HashSet<Banda> bandasElton = new HashSet<>();
        bandasElton.add(new Banda("Elton John Band"));
        externos.add(new ArtistaExterno("Elton John", 2, 1200, rolesElton, bandasElton));
        
        return externos;
    }
    
    private static HashSet<Cancion> crearCanciones(HashSet<Rol> rolesBase) {
        HashSet<Cancion> canciones = new HashSet<>();
        
        // Somebody to Love
        HashSet<Rol> rolesSomebody = new HashSet<>();
        rolesSomebody.add(buscarRol(rolesBase, "voz principal"));
        rolesSomebody.add(buscarRol(rolesBase, "guitarra eléctrica"));
        rolesSomebody.add(buscarRol(rolesBase, "bajo"));
        rolesSomebody.add(buscarRol(rolesBase, "batería"));
        rolesSomebody.add(buscarRol(rolesBase, "piano"));
        canciones.add(new Cancion("Somebody to Love", rolesSomebody));
        
        // We Will Rock You
        HashSet<Rol> rolesRockYou = new HashSet<>();
        rolesRockYou.add(buscarRol(rolesBase, "voz principal"));
        rolesRockYou.add(buscarRol(rolesBase, "guitarra eléctrica"));
        rolesRockYou.add(buscarRol(rolesBase, "bajo"));
        rolesRockYou.add(buscarRol(rolesBase, "batería"));
        canciones.add(new Cancion("We Will Rock You", rolesRockYou));
        
        return canciones;
    }
    
    private static Rol buscarRol(HashSet<Rol> roles, String nombre) {
        return roles.stream()
            .filter(r -> r.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .orElse(null);
    }
}
