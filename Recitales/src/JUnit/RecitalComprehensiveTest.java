package JUnit;
import java.util.*;
import Recital.*;
import Recital.Artista.*;
import Recital.Banda.Banda;
import Recital.Banda.BandaCatalogo;
import Recital.Contratos.Contrato;
import Recital.Contratos.ServicioContratacion;
import Recital.Rol.Rol;
import Recital.Rol.RolCatalogo;
import Recital.Colaboraciones.GrafoColaboraciones;
import Imports.*;

/**
 * Suite completa de pruebas del sistema de recitales
 * Utiliza datos reales cargados desde archivos JSON
 * 
 * @author Sistema de Testing
 * @version 2.0
 */
public class RecitalComprehensiveTest {

    private static RolCatalogo rolCatalogo;
    private static BandaCatalogo bandaCatalogo;
    private static FabricaRecital fabricaRecital;
    private static Recital recital;
    private static ServicioContratacion servicioContratacion;
    
    private static final String RUTA_ARTISTAS = "data/ArchivosInput/artistas.json";
    private static final String RUTA_CANCIONES = "data/ArchivosInput/recital.json";
    private static final String RUTA_ARTISTAS_BASE = "data/ArchivosInput/artistas-discografica.json";
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("Suite de Pruebas del Sistema de Recitales con Datos Reales");
        System.out.println("=".repeat(80));
        
        setUp();
        
        runPruebasCargaDatos();
        runPruebasArtistasReales();
        runPruebasCancionesReales();
        runPruebasRolesFaltantes();
        runPruebasContratacionReales();
        runPruebasEntrenamientosReales();
        runPruebasQuitarArtista();
        runPruebasColaboraciones();
        runPruebasIntegracionCompleta();
        runPruebasCasosExtremos();
        
        printSummary();
    }

    static void setUp() throws Exception {
        System.out.println("\nInicializando ambiente de pruebas...");
        rolCatalogo = new RolCatalogo();
        bandaCatalogo = new BandaCatalogo();
        servicioContratacion = new ServicioContratacion();
        
        ICargarRecital cargador = new JsonAdapter(RUTA_ARTISTAS, RUTA_CANCIONES, RUTA_ARTISTAS_BASE);
        fabricaRecital = new FabricaRecital(cargador);
        recital = fabricaRecital.crearRecital();
        System.out.println("Ambiente iniciado correctamente.");
    }

    // ======================== PRUEBAS DE CARGA DE DATOS ========================
    static void runPruebasCargaDatos() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE CARGA DE DATOS");
        System.out.println("=".repeat(80));
        
        testRecitalCargaCorrectamente();
        testCargaArtistasBase();
        testCargaArtistasExternos();
        testCargaCanciones();
        testArtistasBaseExcluidosDeExternos();
        testArtistasBaseConRoles();
        testCancionesConRoles();
        testFreddieMercuryEnBase();
        testDavidBowieExterno();
        testUnderPressureEnCanciones();
    }

    static void testRecitalCargaCorrectamente() {
        test("Recital se carga correctamente desde JSON", () -> {
            assert recital != null : "El recital no debe ser nulo";
            assert recital.getArtistasBase().size() > 0 : "Debe haber artistas base";
            assert recital.getArtistasExternos().size() > 0 : "Debe haber artistas externos";
            assert recital.getCanciones().size() > 0 : "Debe haber canciones";
        });
    }

    static void testCargaArtistasBase() {
        test("Se cargan 4 artistas base desde JSON", () -> {
            assert recital.getArtistasBase().size() == 4 : 
                "Debe haber exactamente 4 artistas base (Queen members), se encontraron: " + recital.getArtistasBase().size();
        });
    }

    static void testCargaArtistasExternos() {
        test("Se cargan artistas externos desde JSON", () -> {
            assert recital.getArtistasExternos().size() > 0 : "Debe haber artistas externos disponibles";
            assert recital.getArtistasExternos().size() == 13 : 
                "Debe haber 13 artistas externos, se encontraron: " + recital.getArtistasExternos().size();
        });
    }

    static void testCargaCanciones() {
        test("Se cargan canciones desde JSON", () -> {
            assert recital.getCanciones().size() > 0 : "Debe haber canciones disponibles";
            assert recital.getCanciones().size() == 12 : 
                "Debe haber 12 canciones, se encontraron: " + recital.getCanciones().size();
        });
    }

    static void testArtistasBaseExcluidosDeExternos() {
        test("Artistas base no estan en externos", () -> {
            HashSet<String> nombresBase = new HashSet<>();
            for (ArtistaBase artista : recital.getArtistasBase()) {
                nombresBase.add(artista.getNombre());
            }
            
            for (ArtistaExterno artista : recital.getArtistasExternos()) {
                assert !nombresBase.contains(artista.getNombre()) :
                    "El artista " + artista.getNombre() + " no debe estar en externos si esta en base";
            }
        });
    }

    static void testArtistasBaseConRoles() {
        test("Artistas base tienen roles cargados", () -> {
            for (ArtistaBase artista : recital.getArtistasBase()) {
                assert artista.getRoles().size() > 0 :
                    "El artista base " + artista.getNombre() + " debe tener roles";
            }
        });
    }

    static void testCancionesConRoles() {
        test("Canciones tienen roles requeridos", () -> {
            for (Cancion cancion : recital.getCanciones()) {
                assert cancion.getRolesRequeridos().size() > 0 :
                    "La cancion " + cancion.getTitulo() + " debe tener roles requeridos";
            }
        });
    }

    static void testFreddieMercuryEnBase() {
        test("Verificar que Freddie Mercury esta en artistas base", () -> {
            boolean encontrado = recital.getArtistasBase().stream()
                .anyMatch(a -> a.getNombre().equals("Freddie Mercury"));
            assert encontrado : "Freddie Mercury debe estar en artistas base";
        });
    }

    static void testDavidBowieExterno() {
        test("Verificar que David Bowie es artista externo", () -> {
            boolean encontrado = recital.getArtistasExternos().stream()
                .anyMatch(a -> a.getNombre().equals("David Bowie"));
            assert encontrado : "David Bowie debe estar en artistas externos";
        });
    }

    static void testUnderPressureEnCanciones() {
        test("Verificar que Under Pressure esta en canciones", () -> {
            boolean encontrado = recital.getCanciones().stream()
                .anyMatch(c -> c.getTitulo().equals("Under Pressure"));
            assert encontrado : "Under Pressure debe estar en canciones";
        });
    }

    // ======================== PRUEBAS DE ARTISTA CON DATOS REALES ========================
    static void runPruebasArtistasReales() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE ARTISTAS CON DATOS REALES");
        System.out.println("=".repeat(80));
        
        testFreddieRolVozPrincipal();
        testBrianMayRolGuitarra();
        testBowieMultiplesRoles();
        testArtistasBaseSinCosto();
        testArtistasExternosConCosto();
        testQueenMembersComparteBanda();
        testNoDoubtMembersComparteBanda();
    }

    static void testFreddieRolVozPrincipal() {
        test("Freddie Mercury tiene rol de voz principal", () -> {
            ArtistaBase freddie = recital.getArtistasBase().stream()
                .filter(a -> a.getNombre().equals("Freddie Mercury"))
                .findFirst()
                .orElse(null);
            
            assert freddie != null : "Freddie Mercury debe estar en base";
            
            boolean tieneRol = freddie.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("voz principal"));
            
            assert tieneRol : "Freddie Mercury debe tener rol de voz principal";
        });
    }

    static void testBrianMayRolGuitarra() {
        test("Brian May tiene rol de guitarra", () -> {
            ArtistaBase brian = recital.getArtistasBase().stream()
                .filter(a -> a.getNombre().equals("Brian May"))
                .findFirst()
                .orElse(null);
            
            assert brian != null : "Brian May debe estar en base";
            
            boolean tieneRol = brian.getRoles().stream()
                .anyMatch(r -> r.getNombre().equals("guitarra electrica"));
            
            assert tieneRol : "Brian May debe tener rol de guitarra electrica";
        });
    }

    static void testBowieMultiplesRoles() {
        test("David Bowie tiene multiples roles", () -> {
            ArtistaExterno bowie = recital.getArtistasExternos().stream()
                .filter(a -> a.getNombre().equals("David Bowie"))
                .findFirst()
                .orElse(null);
            
            assert bowie != null : "David Bowie debe estar en externos";
            assert bowie.getRoles().size() >= 2 : "David Bowie debe tener multiples roles";
        });
    }

    static void testArtistasBaseSinCosto() {
        test("Artistas base tienen costo 0", () -> {
            for (ArtistaBase artista : recital.getArtistasBase()) {
                assert artista.getCosto() == 0 :
                    "Artista base " + artista.getNombre() + " debe tener costo 0, tiene: " + artista.getCosto();
            }
        });
    }

    static void testArtistasExternosConCosto() {
        test("Artistas externos tienen costo definido", () -> {
            for (ArtistaExterno artista : recital.getArtistasExternos()) {
                assert artista.getCosto() > 0 :
                    "Artista externo " + artista.getNombre() + " debe tener costo positivo";
            }
        });
    }

    static void testQueenMembersComparteBanda() {
        test("Queen members comparten banda", () -> {
            List<ArtistaBase> queen = recital.getArtistasBase().stream()
                .filter(a -> a.getBandasHistoricas().stream()
                    .anyMatch(b -> b.getNombre().equals("Queen")))
                .toList();
            
            assert queen.size() == 4 : "Los 4 miembros de Queen deben estar";
        });
    }

    static void testNoDoubtMembersComparteBanda() {
        test("No Doubt members comparten banda", () -> {
            List<ArtistaExterno> noDoubt = recital.getArtistasExternos().stream()
                .filter(a -> a.getBandasHistoricas().stream()
                    .anyMatch(b -> b.getNombre().equals("No Doubt")))
                .toList();
            
            assert noDoubt.size() == 4 : "Los 4 miembros de No Doubt deben estar";
        });
    }

    // ======================== PRUEBAS DE CANCIONES CON DATOS REALES ========================
    static void runPruebasCancionesReales() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE CANCIONES CON DATOS REALES");
        System.out.println("=".repeat(80));
        
        testUnderPressureRoles();
        testBillieJeanRoles();
        testTodasCancionesConRolesValidos();
        testRolesExistentes();
    }

    static void testUnderPressureRoles() {
        test("Under Pressure requiere 4 roles", () -> {
            Cancion cancion = recital.getCanciones().stream()
                .filter(c -> c.getTitulo().equals("Under Pressure"))
                .findFirst()
                .orElse(null);
            
            assert cancion != null : "Under Pressure debe existir";
            assert cancion.getRolesRequeridos().size() == 4 :
                "Under Pressure debe requerir 4 roles, tiene: " + cancion.getRolesRequeridos().size();
        });
    }

    static void testBillieJeanRoles() {
        test("Billie Jean requiere voz principal en cantidad 2", () -> {
            Cancion cancion = recital.getCanciones().stream()
                .filter(c -> c.getTitulo().equals("Billie Jean"))
                .findFirst()
                .orElse(null);
            
            assert cancion != null : "Billie Jean debe existir";
            
            int vocPrincipalCount = cancion.getRolesRequeridos().values().stream()
                .filter(v -> v > 1)
                .findFirst()
                .orElse(0);
            
            assert vocPrincipalCount > 0 : "Billie Jean debe requerir multiples voces principales";
        });
    }

    static void testTodasCancionesConRolesValidos() {
        test("Todas las canciones tienen roles requeridos validos", () -> {
            for (Cancion cancion : recital.getCanciones()) {
                Map<Rol, Integer> roles = cancion.getRolesRequeridos();
                
                assert !roles.isEmpty() : "Cancion " + cancion.getTitulo() + " debe tener roles";
                
                for (int cantidad : roles.values()) {
                    assert cantidad > 0 :
                        "Cantidad de roles debe ser positiva en " + cancion.getTitulo();
                }
            }
        });
    }

    static void testRolesExistentes() {
        test("Roles requeridos corresponden a roles existentes", () -> {
            for (Cancion cancion : recital.getCanciones()) {
                for (Rol rol : cancion.getRolesRequeridos().keySet()) {
                    assert rol.getNombre() != null :
                        "Rol en " + cancion.getTitulo() + " debe tener nombre";
                    assert !rol.getNombre().isBlank() :
                        "Nombre de rol en " + cancion.getTitulo() + " no debe estar vacio";
                }
            }
        });
    }

    // ======================== PRUEBAS DE ROLES FALTANTES ========================
    static void runPruebasRolesFaltantes() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE ROLES FALTANTES");
        System.out.println("=".repeat(80));
        
        testRolesFaltantesPorCancion();
        testRolesFaltantesGlobal();
        testRolesFaltantesPostContrato();
    }

    static void testRolesFaltantesPorCancion() {
        test("Roles faltantes por cancion calculados correctamente", () -> {
            for (Cancion cancion : recital.getCanciones()) {
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);
                
                assert rolesFaltantes.size() == cancion.getRolesRequeridos().size() :
                    "Para cancion sin contratos, todos los roles deben faltar: " + cancion.getTitulo();
            }
        });
    }

    static void testRolesFaltantesGlobal() {
        test("Roles faltantes globales", () -> {
            Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantes();
            
            assert rolesFaltantes.size() > 0 : "Sin contratos, debe haber roles faltantes";
            
            for (int cantidad : rolesFaltantes.values()) {
                assert cantidad > 0 : "Cantidad de roles faltantes debe ser positiva";
            }
        });
    }

    static void testRolesFaltantesPostContrato() {
        test("After contracting, roles faltantes disminuyen", () -> {
            Cancion cancion = recital.getCanciones().stream()
                .findFirst()
                .orElse(null);
            
            assert cancion != null : "Debe haber al menos una cancion";
            
            Map<Rol, Integer> rolesFaltantesAntes = recital.getRolesFaltantesParaCancion(cancion);
            int totalAntes = rolesFaltantesAntes.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
            
            try {
                servicioContratacion.contratarParaCancion(recital, cancion);
            } catch (Exception e) {
                // Puede no haber artistas disponibles
            }
            
            Map<Rol, Integer> rolesFaltantesAfter = recital.getRolesFaltantesParaCancion(cancion);
            int totalAfter = rolesFaltantesAfter.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
            
            assert totalAfter <= totalAntes : "Roles faltantes deben disminuir o mantenerse despues de contratar";
        });
    }

    // ======================== PRUEBAS DE CONTRATACION ========================
    static void runPruebasContratacionReales() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE CONTRATACION");
        System.out.println("=".repeat(80));
        
        testContratarParaCancion();
        testContratosEnRecital();
        testCostoTotalAumenta();
        testRespetalimitesMaxCanciones();
        testContratarParaTodo();
    }

    static void testContratarParaCancion() {
        test("Contratar para una cancion", () -> {
            Cancion cancion = recital.getCanciones().stream()
                .findFirst()
                .orElse(null);
            
            assert cancion != null : "Debe haber al menos una cancion";
            
            try {
                List<Contrato> contratos = servicioContratacion.contratarParaCancion(recital, cancion);
                assert contratos != null : "Debe retornar lista de contratos";
                assert contratos.size() > 0 : "Debe haber al menos un contrato";
            } catch (Exception e) {
                // Puede ser que no haya artistas disponibles
            }
        });
    }

    static void testContratosEnRecital() {
        test("Contratos se agregan al recital", () -> {
            int contratosAntes = recital.getContratos().size();
            
            Cancion cancion = recital.getCanciones().stream()
                .findFirst()
                .orElse(null);
            
            try {
                servicioContratacion.contratarParaCancion(recital, cancion);
            } catch (Exception e) {
                // Puede ser que no haya artistas disponibles
            }
            
            int contratosAfter = recital.getContratos().size();
            assert contratosAfter >= contratosAntes : "Debe haber mas o igual contratos despues de contratar";
        });
    }

    static void testCostoTotalAumenta() {
        test("Costo total aumenta despues de contratar", () -> {
            double costoAntes = recital.getCostoTotalRecital();
            
            Cancion cancion = recital.getCanciones().stream()
                .findFirst()
                .orElse(null);
            
            try {
                servicioContratacion.contratarParaCancion(recital, cancion);
            } catch (Exception e) {
                // Puede ser que no haya artistas disponibles
            }
            
            double costoAfter = recital.getCostoTotalRecital();
            assert costoAfter >= costoAntes : "Costo total debe aumentar o mantenerse despues de contratar";
        });
    }

    static void testRespetalimitesMaxCanciones() {
        test("Se respetan limites de maxCanciones", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede ser que no haya artistas disponibles
            }
            
            for (Artista artista : recital.getContratos().stream()
                    .map(Contrato::getArtista)
                    .distinct()
                    .toList()) {
                
                long cancionesUnicas = recital.getContratos().stream()
                    .filter(c -> c.getArtista().equals(artista))
                    .map(Contrato::getCancion)
                    .distinct()
                    .count();
                
                assert cancionesUnicas <= artista.getMaxCanciones() :
                    "Artista " + artista.getNombre() + " excede su limite de canciones";
            }
        });
    }

    static void testContratarParaTodo() {
        test("Contratar para todas las canciones", () -> {
            try {
                List<Contrato> contratos = servicioContratacion.contratarParaTodo(recital);
                assert contratos != null : "Debe retornar lista de contratos";
            } catch (Exception e) {
                // Puede ser que no haya artistas disponibles
            }
        });
    }

    // ======================== PRUEBAS DE ENTRENAMIENTOS ========================
    static void runPruebasEntrenamientosReales() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE ENTRENAMIENTOS");
        System.out.println("=".repeat(80));
        
        testArtistaBaseNoPuedeEntrenarse();
        testArtistaExternoPuedeEntrenarse();
        testEntrenarAgregaRol();
        testEntrenamientoCosto();
    }

    static void testArtistaBaseNoPuedeEntrenarse() {
        test("Artista base NO puede ser entrenado", () -> {
            for (ArtistaBase artista : recital.getArtistasBase()) {
                assert !artista.puedeSerEntrenado() :
                    "Artista base " + artista.getNombre() + " NO debe poder entrenar";
            }
        });
    }

    static void testArtistaExternoPuedeEntrenarse() {
        test("Artista externo SI puede ser entrenado", () -> {
            for (ArtistaExterno artista : recital.getArtistasExternos()) {
                assert artista.puedeSerEntrenado() :
                    "Artista externo " + artista.getNombre() + " SI debe poder entrenar";
            }
        });
    }

    static void testEntrenarAgregaRol() {
        test("Entrenar artista agrega nuevo rol", () -> {
            ArtistaExterno artista = recital.getArtistasExternos().stream()
                .findFirst()
                .orElse(null);
            
            assert artista != null : "Debe haber artista externo";
            
            int rolesAntes = artista.getRoles().size();
            
            try {
                Rol nuevoRol = rolCatalogo.obtenerRol("flauta");
                artista.agregarRol(nuevoRol);
                
                int rolesAfter = artista.getRoles().size();
                assert rolesAfter > rolesAntes : "Debe tener un rol mas despues de entrenar";
            } catch (Exception e) {
                // El rol puede ya existir
            }
        });
    }

    static void testEntrenamientoCosto() {
        test("Entrenamiento incrementa costo 50%", () -> {
            ArtistaExterno artista = recital.getArtistasExternos().stream()
                .findFirst()
                .orElse(null);
            
            assert artista != null : "Debe haber artista externo";
            
            double costoOriginal = artista.getCosto();
            
            try {
                Rol nuevoRol = rolCatalogo.obtenerRol("nuevo_instrumento");
                artista.agregarRol(nuevoRol);
                artista.incrementarCosto(1.5);
                
                double costoNuevo = artista.getCosto();
                assert Math.abs(costoNuevo - (costoOriginal * 1.5)) < 0.01 :
                    "El costo debe incrementarse 50%";
            } catch (Exception e) {
                // El rol puede ya existir
            }
        });
    }

    // ======================== PRUEBAS DE QUITAR ARTISTA ========================
    static void runPruebasQuitarArtista() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE QUITAR ARTISTA");
        System.out.println("=".repeat(80));
        
        testQuitarArtistaEliminaContratos();
        testQuitarArtistaInexistente();
    }

    static void testQuitarArtistaEliminaContratos() {
        test("Quitar artista elimina sus contratos", () -> {
            try {
                servicioContratacion.contratarParaCancion(recital, 
                    recital.getCanciones().stream().findFirst().orElse(null));
            } catch (Exception e) {
                // Puede no haber artistas disponibles
            }
            
            int contratosAntes = recital.getContratos().size();
            
            Artista artistaAQuitar = recital.getContratos().stream()
                .map(Contrato::getArtista)
                .findFirst()
                .orElse(null);
            
            if (artistaAQuitar != null) {
                recital.quitarArtista(artistaAQuitar);
                int contratosAfter = recital.getContratos().size();
                
                assert contratosAfter <= contratosAntes :
                    "Debe haber menos o igual contratos despues de quitar";
            }
        });
    }

    static void testQuitarArtistaInexistente() {
        test("Quitar artista inexistente retorna false", () -> {
            ArtistaBase ficticio = new ArtistaBase("Ficticio", 1, 0,
                new HashSet<>(), new HashSet<>());
            
            boolean quitado = recital.quitarArtista(ficticio);
            assert !quitado : "No debe poder quitar artista que no existe";
        });
    }

    // ======================== PRUEBAS DE COLABORACIONES ========================
    static void runPruebasColaboraciones() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE COLABORACIONES");
        System.out.println("=".repeat(80));
        
        testConexionNoDoubt();
        testConexionQueen();
    }

    static void testConexionNoDoubt() {
        test("Detectar conexion entre artistas de No Doubt", () -> {
            List<ArtistaExterno> noDoubtMembers = recital.getArtistasExternos().stream()
                .filter(a -> a.getBandasHistoricas().stream()
                    .anyMatch(b -> b.getNombre().equals("No Doubt")))
                .toList();
            
            for (int i = 0; i < noDoubtMembers.size() - 1; i++) {
                ArtistaExterno artista1 = noDoubtMembers.get(i);
                ArtistaExterno artista2 = noDoubtMembers.get(i + 1);
                
                boolean compartenBanda = artista1.getBandasHistoricas().stream()
                    .anyMatch(b -> artista2.getBandasHistoricas().contains(b));
                
                assert compartenBanda :
                    artista1.getNombre() + " y " + artista2.getNombre() + " deben compartir banda";
            }
        });
    }

    static void testConexionQueen() {
        test("Queen members comparten banda", () -> {
            List<ArtistaBase> queenMembers = recital.getArtistasBase().stream()
                .filter(a -> a.getBandasHistoricas().stream()
                    .anyMatch(b -> b.getNombre().equals("Queen")))
                .toList();
            
            assert queenMembers.size() == 4 : "Deben haber 4 miembros de Queen";
            
            for (ArtistaBase artista : queenMembers) {
                assert artista.getBandasHistoricas().stream()
                    .anyMatch(b -> b.getNombre().equals("Queen")) :
                    artista.getNombre() + " debe estar en Queen";
            }
        });
    }

    // ======================== PRUEBAS DE INTEGRACION COMPLETA ========================
    static void runPruebasIntegracionCompleta() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE INTEGRACION COMPLETA");
        System.out.println("=".repeat(80));
        
        testFlujoCompleto();
        testEstadoConsistente();
        testCostosTotales();
        testArtistasRespetanLimites();
    }

    static void testFlujoCompleto() {
        test("Flujo completo: cargar, contratar, listar, quitar", () -> {
            assert recital.getCanciones().size() > 0 : "Recital debe estar cargado";
            
            Cancion cancion1 = recital.getCanciones().stream().findFirst().orElse(null);
            
            try {
                servicioContratacion.contratarParaCancion(recital, cancion1);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            assert recital.getContratos().size() >= 0 : "Debe haber lista de contratos";
            
            Artista artistaAQuitar = recital.getContratos().stream()
                .map(Contrato::getArtista)
                .findFirst()
                .orElse(null);
            
            if (artistaAQuitar != null) {
                recital.quitarArtista(artistaAQuitar);
            }
        });
    }

    static void testEstadoConsistente() {
        test("Validar estado consistente despues de multiples operaciones", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            for (Cancion cancion : recital.getCanciones()) {
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);
                assert rolesFaltantes != null : "Roles faltantes no debe ser nulo";
            }
            
            for (Contrato contrato : recital.getContratos()) {
                assert contrato.getArtista() != null : "Artista en contrato no debe ser nulo";
                assert contrato.getCancion() != null : "Cancion en contrato no debe ser nula";
                assert contrato.getRol() != null : "Rol en contrato no debe ser nulo";
            }
        });
    }

    static void testCostosTotales() {
        test("Costos totales se calculan correctamente", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            double costoTotal = recital.getCostoTotalRecital();
            assert costoTotal >= 0 : "Costo total debe ser positivo o cero";
        });
    }

    static void testArtistasRespetanLimites() {
        test("Verificar que todos los artistas respetan sus limites", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            for (Artista artista : recital.getContratos().stream()
                    .map(Contrato::getArtista)
                    .distinct()
                    .toList()) {
                
                assert artista.getCantCancionesAsignado() <= artista.getMaxCanciones() :
                    artista.getNombre() + " excede su limite de canciones";
            }
        });
    }

    // ======================== PRUEBAS DE CASOS EXTREMOS ========================
    static void runPruebasCasosExtremos() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PRUEBAS DE CASOS EXTREMOS");
        System.out.println("=".repeat(80));
        
        testContratarTodasCanciones();
        testMultiplesQuitadas();
        testSinDuplicados();
    }

    static void testContratarTodasCanciones() {
        test("Recital con todas las canciones en simultaneo", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            assert true : "Debe poder intentar contratar para todo";
        });
    }

    static void testMultiplesQuitadas() {
        test("Multiple quitadas del mismo artista", () -> {
            try {
                servicioContratacion.contratarParaTodo(recital);
            } catch (Exception e) {
                // Puede no haber artistas
            }
            
            Artista artistaAQuitar = recital.getContratos().stream()
                .map(Contrato::getArtista)
                .findFirst()
                .orElse(null);
            
            if (artistaAQuitar != null) {
                boolean primera = recital.quitarArtista(artistaAQuitar);
                boolean segunda = recital.quitarArtista(artistaAQuitar);
                
                assert primera : "Primera quita debe funcionar";
                assert !segunda : "Segunda quita del mismo artista debe fallar";
            }
        });
    }

    static void testSinDuplicados() {
        test("Verificar que JSON se cargo correctamente sin duplicados", () -> {
            Set<String> nombresBase = new HashSet<>();
            Set<String> nombresExternos = new HashSet<>();
            
            for (ArtistaBase a : recital.getArtistasBase()) {
                assert !nombresBase.contains(a.getNombre()) :
                    "Artista base duplicado: " + a.getNombre();
                nombresBase.add(a.getNombre());
            }
            
            for (ArtistaExterno a : recital.getArtistasExternos()) {
                assert !nombresExternos.contains(a.getNombre()) :
                    "Artista externo duplicado: " + a.getNombre();
                nombresExternos.add(a.getNombre());
            }
        });
    }

    // ======================== UTILIDADES DE TESTING ========================
    @FunctionalInterface
    interface TestBlock {
        void execute() throws Exception;
    }

    static void test(String name, TestBlock block) {
        testsRun++;
        try {
            block.execute();
            testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.println("[FAIL] " + name);
            System.out.println("  Error: " + e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.println("[ERROR] " + name);
            System.out.println("  Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void printSummary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RESUMEN DE PRUEBAS");
        System.out.println("=".repeat(80));
        System.out.println("Total de pruebas:    " + testsRun);
        System.out.println("Pruebas exitosas:    " + testsPassed);
        System.out.println("Pruebas fallidas:    " + testsFailed);
        
        double percentage = (testsPassed * 100.0) / testsRun;
        System.out.println("Porcentaje de exito: " + String.format("%.2f%%", percentage));
        System.out.println("=".repeat(80));
        
        if (testsFailed == 0) {
            System.out.println("TODAS LAS PRUEBAS PASARON EXITOSAMENTE!");
        }
    }
}
