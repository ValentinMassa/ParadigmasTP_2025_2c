import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
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
        
        // Queen - Brian May
        HashSet<Rol> rolesBrian = new HashSet<>();
        rolesBrian.add(buscarRol(rolesBase, "guitarra eléctrica"));
        rolesBrian.add(buscarRol(rolesBase, "coros"));
        artistasBase.add(new ArtistaBase("Brian May", 5, 500, rolesBrian, bandas));
        
        // Queen - Roger Taylor
        HashSet<Rol> rolesRoger = new HashSet<>();
        rolesRoger.add(buscarRol(rolesBase, "batería"));
        rolesRoger.add(buscarRol(rolesBase, "coros"));
        artistasBase.add(new ArtistaBase("Roger Taylor", 5, 450, rolesRoger, bandas));
        
        // Queen - John Deacon
        HashSet<Rol> rolesJohn = new HashSet<>();
        rolesJohn.add(buscarRol(rolesBase, "bajo"));
        artistasBase.add(new ArtistaBase("John Deacon", 5, 400, rolesJohn, bandas));
        
        // Adicionales Queen
        HashSet<Rol> rolesFreddie = new HashSet<>();
        rolesFreddie.add(buscarRol(rolesBase, "voz principal"));
        rolesFreddie.add(buscarRol(rolesBase, "piano"));
        artistasBase.add(new ArtistaBase("Freddie Mercury", 4, 800, rolesFreddie, bandas));
        
        HashSet<Rol> rolesPiano = new HashSet<>();
        rolesPiano.add(buscarRol(rolesBase, "piano"));
        rolesPiano.add(buscarRol(rolesBase, "coros"));
        artistasBase.add(new ArtistaBase("David Richards", 3, 350, rolesPiano, bandas));
        
        return artistasBase;
    }
    
    private static HashSet<ArtistaExterno> crearArtistasExternos(HashSet<Rol> rolesBase) {
        HashSet<ArtistaExterno> externos = new HashSet<>();
        
        // George Michael - voz principal (Wham! band)
        HashSet<Rol> rolesGeorge = new HashSet<>();
        rolesGeorge.add(buscarRol(rolesBase, "voz principal"));
        HashSet<Banda> bandasGeorge = new HashSet<>();
        bandasGeorge.add(new Banda("Wham!"));
        externos.add(new ArtistaExterno("George Michael", 3, 1000, rolesGeorge, bandasGeorge));
        
        // Elton John - voz principal + piano (Elton John Band)
        HashSet<Rol> rolesElton = new HashSet<>();
        rolesElton.add(buscarRol(rolesBase, "voz principal"));
        rolesElton.add(buscarRol(rolesBase, "piano"));
        HashSet<Banda> bandasElton = new HashSet<>();
        bandasElton.add(new Banda("Elton John Band"));
        externos.add(new ArtistaExterno("Elton John", 2, 1200, rolesElton, bandasElton));
        
        // David Bowie - voz principal
        HashSet<Rol> rolesBowie = new HashSet<>();
        rolesBowie.add(buscarRol(rolesBase, "voz principal"));
        rolesBowie.add(buscarRol(rolesBase, "guitarra eléctrica"));
        HashSet<Banda> bandasBowie = new HashSet<>();
        bandasBowie.add(new Banda("The Spiders"));
        externos.add(new ArtistaExterno("David Bowie", 4, 1100, rolesBowie, bandasBowie));
        
        // Billy Idol - guitarra eléctrica + voz
        HashSet<Rol> rolesBilly = new HashSet<>();
        rolesBilly.add(buscarRol(rolesBase, "guitarra eléctrica"));
        rolesBilly.add(buscarRol(rolesBase, "voz principal"));
        HashSet<Banda> bandasBilly = new HashSet<>();
        bandasBilly.add(new Banda("Generation X"));
        externos.add(new ArtistaExterno("Billy Idol", 3, 900, rolesBilly, bandasBilly));
        
        // Phil Collins - batería + voz
        HashSet<Rol> rolesPhil = new HashSet<>();
        rolesPhil.add(buscarRol(rolesBase, "batería"));
        rolesPhil.add(buscarRol(rolesBase, "voz principal"));
        HashSet<Banda> bandasPhil = new HashSet<>();
        bandasPhil.add(new Banda("Genesis"));
        externos.add(new ArtistaExterno("Phil Collins", 3, 950, rolesPhil, bandasPhil));
        
        // Sting - bajo + voz principal
        HashSet<Rol> rolesStting = new HashSet<>();
        rolesStting.add(buscarRol(rolesBase, "bajo"));
        rolesStting.add(buscarRol(rolesBase, "voz principal"));
        HashSet<Banda> bandasSting = new HashSet<>();
        bandasSting.add(new Banda("The Police"));
        externos.add(new ArtistaExterno("Sting", 3, 1050, rolesStting, bandasSting));
        
        // Tina Turner - voz principal
        HashSet<Rol> rolesTina = new HashSet<>();
        rolesTina.add(buscarRol(rolesBase, "voz principal"));
        rolesTina.add(buscarRol(rolesBase, "coros"));
        HashSet<Banda> bandasTina = new HashSet<>();
        bandasTina.add(new Banda("Ike & Tina Turner"));
        externos.add(new ArtistaExterno("Tina Turner", 2, 1100, rolesTina, bandasTina));
        
        // Stevie Nicks - voz principal + piano
        HashSet<Rol> rolesStevie = new HashSet<>();
        rolesStevie.add(buscarRol(rolesBase, "voz principal"));
        rolesStevie.add(buscarRol(rolesBase, "piano"));
        HashSet<Banda> bandasStevie = new HashSet<>();
        bandasStevie.add(new Banda("Fleetwood Mac"));
        externos.add(new ArtistaExterno("Stevie Nicks", 2, 1080, rolesStevie, bandasStevie));
        
        return externos;
    }
    
    private static HashSet<Cancion> crearCanciones(HashSet<Rol> rolesBase) {
        HashSet<Cancion> canciones = new HashSet<>();
        
        // 1. Somebody to Love - Queen
        Map<Rol, Integer> rolesSomebody = new HashMap<>();
        rolesSomebody.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesSomebody.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesSomebody.put(buscarRol(rolesBase, "bajo"), 1);
        rolesSomebody.put(buscarRol(rolesBase, "batería"), 1);
        rolesSomebody.put(buscarRol(rolesBase, "piano"), 1);
        canciones.add(new Cancion("Somebody to Love", rolesSomebody));
        
        // 2. We Will Rock You - Queen
        Map<Rol, Integer> rolesRockYou = new HashMap<>();
        rolesRockYou.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesRockYou.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesRockYou.put(buscarRol(rolesBase, "bajo"), 1);
        rolesRockYou.put(buscarRol(rolesBase, "batería"), 1);
        canciones.add(new Cancion("We Will Rock You", rolesRockYou));
        
        // 3. Another One Bites the Dust - Queen
        Map<Rol, Integer> rolesAnother = new HashMap<>();
        rolesAnother.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesAnother.put(buscarRol(rolesBase, "bajo"), 1);
        rolesAnother.put(buscarRol(rolesBase, "batería"), 1);
        rolesAnother.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        canciones.add(new Cancion("Another One Bites the Dust", rolesAnother));
        
        // 4. Bohemian Rhapsody - Queen
        Map<Rol, Integer> rolesBohemian = new HashMap<>();
        rolesBohemian.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesBohemian.put(buscarRol(rolesBase, "piano"), 1);
        rolesBohemian.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesBohemian.put(buscarRol(rolesBase, "bajo"), 1);
        rolesBohemian.put(buscarRol(rolesBase, "batería"), 1);
        rolesBohemian.put(buscarRol(rolesBase, "coros"), 2);
        canciones.add(new Cancion("Bohemian Rhapsody", rolesBohemian));
        
        // 5. Don't Stop Me Now - Queen
        Map<Rol, Integer> rolesDontStop = new HashMap<>();
        rolesDontStop.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesDontStop.put(buscarRol(rolesBase, "piano"), 1);
        rolesDontStop.put(buscarRol(rolesBase, "bajo"), 1);
        rolesDontStop.put(buscarRol(rolesBase, "batería"), 1);
        canciones.add(new Cancion("Don't Stop Me Now", rolesDontStop));
        
        // 6. Rocket Man - Elton John
        Map<Rol, Integer> rolesRocket = new HashMap<>();
        rolesRocket.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesRocket.put(buscarRol(rolesBase, "piano"), 1);
        rolesRocket.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesRocket.put(buscarRol(rolesBase, "bajo"), 1);
        canciones.add(new Cancion("Rocket Man", rolesRocket));
        
        // 7. Heroes - David Bowie
        Map<Rol, Integer> rolesHeroes = new HashMap<>();
        rolesHeroes.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesHeroes.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesHeroes.put(buscarRol(rolesBase, "bajo"), 1);
        rolesHeroes.put(buscarRol(rolesBase, "batería"), 1);
        rolesHeroes.put(buscarRol(rolesBase, "piano"), 1);
        canciones.add(new Cancion("Heroes", rolesHeroes));
        
        // 8. Under Pressure - Queen & David Bowie
        Map<Rol, Integer> rolesUnder = new HashMap<>();
        rolesUnder.put(buscarRol(rolesBase, "voz principal"), 2);
        rolesUnder.put(buscarRol(rolesBase, "bajo"), 1);
        rolesUnder.put(buscarRol(rolesBase, "batería"), 1);
        rolesUnder.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        canciones.add(new Cancion("Under Pressure", rolesUnder));
        
        // 9. White Wedding - Billy Idol
        Map<Rol, Integer> rolesWhite = new HashMap<>();
        rolesWhite.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesWhite.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesWhite.put(buscarRol(rolesBase, "bajo"), 1);
        rolesWhite.put(buscarRol(rolesBase, "batería"), 1);
        canciones.add(new Cancion("White Wedding", rolesWhite));
        
        // 10. In the Air Tonight - Phil Collins
        Map<Rol, Integer> rolesAir = new HashMap<>();
        rolesAir.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesAir.put(buscarRol(rolesBase, "batería"), 1);
        rolesAir.put(buscarRol(rolesBase, "piano"), 1);
        rolesAir.put(buscarRol(rolesBase, "bajo"), 1);
        canciones.add(new Cancion("In the Air Tonight", rolesAir));
        
        // 11. Private Eyes - Sting
        Map<Rol, Integer> rolesPrivate = new HashMap<>();
        rolesPrivate.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesPrivate.put(buscarRol(rolesBase, "bajo"), 1);
        rolesPrivate.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesPrivate.put(buscarRol(rolesBase, "batería"), 1);
        rolesPrivate.put(buscarRol(rolesBase, "coros"), 1);
        canciones.add(new Cancion("Private Eyes", rolesPrivate));
        
        // 12. Private Dancer - Tina Turner
        Map<Rol, Integer> rolesDancer = new HashMap<>();
        rolesDancer.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesDancer.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesDancer.put(buscarRol(rolesBase, "bajo"), 1);
        rolesDancer.put(buscarRol(rolesBase, "batería"), 1);
        rolesDancer.put(buscarRol(rolesBase, "coros"), 1);
        canciones.add(new Cancion("Private Dancer", rolesDancer));
        
        // 13. Dreams - Fleetwood Mac (Stevie Nicks)
        Map<Rol, Integer> rolesDreams = new HashMap<>();
        rolesDreams.put(buscarRol(rolesBase, "voz principal"), 1);
        rolesDreams.put(buscarRol(rolesBase, "piano"), 1);
        rolesDreams.put(buscarRol(rolesBase, "guitarra eléctrica"), 1);
        rolesDreams.put(buscarRol(rolesBase, "bajo"), 1);
        canciones.add(new Cancion("Dreams", rolesDreams));
        
        return canciones;
    }
    
    private static Rol buscarRol(HashSet<Rol> roles, String nombre) {
        return roles.stream()
            .filter(r -> r.getNombre().equalsIgnoreCase(nombre))
            .findFirst()
            .orElse(null);
    }
}
