import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import Recital.*;
import Repositorios.*;
import Artista.*;

@DisplayName("Tests Funcionalidad 1: Calcular Roles Faltantes por Cancion")
public class Funcionalidad01_RolesFaltantesPorCancionTest {
    
    private Recital recital;
    private ServicioContratacion servicioContratacion;
    private ServicioConsulta servicioConsulta;
    private RepositorioArtistasMemory repositorioArtistas;
    private RolCatalogoMemory rolCatalogo;
    private BandaCatalogoMemory bandaCatalogo;
    
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol bateria;
    private Rol coros;
    
    private Cancion cancion1;
    private Cancion cancion2;
    
    private ArtistaDiscografica freddie;
    private ArtistaDiscografica brian;
    private ArtistaDiscografica roger;
    
    @BeforeEach
    void setUp() {
        // PASO 1: Crear repositorios vacios para almacenar artistas, roles y bandas
        repositorioArtistas = new RepositorioArtistasMemory();
        rolCatalogo = new RolCatalogoMemory();
        bandaCatalogo = new BandaCatalogoMemory();
        
        // PASO 2: Crear roles y agregarlos al catalogo
        // agregarRol retorna null si el rol se agrega exitosamente, o el rol existente si ya estaba
        vozPrincipal = rolCatalogo.agregarRol("voz principal");
        if (vozPrincipal == null) vozPrincipal = rolCatalogo.getRol("voz principal");
        
        guitarra = rolCatalogo.agregarRol("guitarra electrica");
        if (guitarra == null) guitarra = rolCatalogo.getRol("guitarra electrica");
        
        bateria = rolCatalogo.agregarRol("bateria");
        if (bateria == null) bateria = rolCatalogo.getRol("bateria");
        
        coros = rolCatalogo.agregarRol("coros");
        if (coros == null) coros = rolCatalogo.getRol("coros");
        
        // PASO 3: Crear canciones con sus roles requeridos
        // Cancion 1: Bohemian Rhapsody requiere 1 voz principal, 1 guitarra y 2 coros
        cancion1 = new Cancion("Bohemian Rhapsody");
        cancion1.agregarRolRequerido(vozPrincipal, 1);
        cancion1.agregarRolRequerido(guitarra, 1);
        cancion1.agregarRolRequerido(coros, 2);
        
        // Cancion 2: We Will Rock You requiere 1 voz principal, 1 bateria
        cancion2 = new Cancion("We Will Rock You");
        cancion2.agregarRolRequerido(vozPrincipal, 1);
        cancion2.agregarRolRequerido(bateria, 1);
        
        // PASO 4: Crear recital con las canciones
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(cancion1);
        canciones.add(cancion2);
        recital = new Recital(canciones);
        
        // PASO 5: Crear artistas con sus habilidades
        freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarRolHistorico(coros);
        
        brian = new ArtistaDiscografica("Brian May", 10, 100.0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarRolHistorico(coros);
        
        roger = new ArtistaDiscografica("Roger Taylor", 10, 100.0);
        roger.agregarRolHistorico(bateria);
        roger.agregarRolHistorico(coros);
        
        // PASO 6: Agregar artistas al repositorio
        HashSet<ArtistaDiscografica> artistasBase = new HashSet<>();
        artistasBase.add(freddie);
        artistasBase.add(brian);
        artistasBase.add(roger);
        repositorioArtistas = new RepositorioArtistasMemory(artistasBase, new HashSet<>());
        
        // PASO 7: Crear servicios de consulta y contratacion
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        servicioContratacion = new ServicioContratacion();
    }
    
    @Test
    @DisplayName("Test 1.1: Calcular roles faltantes de cancion sin contratos")
    void testRolesFaltantesSinContratos() {
        // OBJETIVO: Verificar que una cancion sin contratos muestra todos sus roles como faltantes
        
        // PASO 1: Obtener contratos de la cancion (debe estar vacia porque no hemos contratado a nadie)
        List<Contrato> contratosBohemian = servicioContratacion.getContratosPorCancion(cancion1);
        assertEquals(0, contratosBohemian.size(), 
            "No deberia haber contratos porque no hemos contratado a nadie todavia");
        
        // PASO 2: Calcular roles faltantes usando el metodo de Cancion
        // Este metodo compara los roles requeridos con los contratos existentes
        HashMap<Rol, Integer> rolesFaltantes = cancion1.getRolesFaltantes(contratosBohemian);
        
        // PASO 3: Verificar que todos los roles requeridos estan faltantes
        assertEquals(3, rolesFaltantes.size(), 
            "Bohemian Rhapsody requiere 3 tipos de roles diferentes (voz, guitarra, coros)");
        
        assertEquals(1, rolesFaltantes.get(vozPrincipal), 
            "Falta 1 voz principal porque no hay contratos");
        
        assertEquals(1, rolesFaltantes.get(guitarra), 
            "Falta 1 guitarra porque no hay contratos");
        
        assertEquals(2, rolesFaltantes.get(coros), 
            "Faltan 2 coros porque no hay contratos");
        
        // DOCUMENTACION DEL RESULTADO:
        // Roles faltantes para 'Bohemian Rhapsody' SIN CONTRATOS:
        // - voz principal: 1
        // - guitarra electrica: 1
        // - coros: 2
    }
    
    @Test
    @DisplayName("Test 1.2: Calcular roles faltantes despues de una contratacion parcial")
    void testRolesFaltantesDespuesDeContratacionParcial() {
        // OBJETIVO: Verificar que despues de contratar a un artista, los roles faltantes se actualizan correctamente
        
        // PASO 1: Verificar roles faltantes ANTES de contratar
        List<Contrato> contratosAntes = servicioContratacion.getContratosPorCancion(cancion1);
        HashMap<Rol, Integer> rolesFaltantesAntes = cancion1.getRolesFaltantes(contratosAntes);
        
        assertEquals(1, rolesFaltantesAntes.get(vozPrincipal), 
            "ANTES de contratar: Falta 1 voz principal");
        assertEquals(1, rolesFaltantesAntes.get(guitarra), 
            "ANTES de contratar: Falta 1 guitarra");
        assertEquals(2, rolesFaltantesAntes.get(coros), 
            "ANTES de contratar: Faltan 2 coros");
        
        // PASO 2: Contratar a Freddie para voz principal en Bohemian Rhapsody
        Contrato contratoFreddie = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie);
        freddie.setCantCancionesAsignado(1);
        
        // PASO 3: Verificar roles faltantes DESPUES de contratar a Freddie
        List<Contrato> contratosDespues = servicioContratacion.getContratosPorCancion(cancion1);
        assertEquals(1, contratosDespues.size(), 
            "Ahora debe haber 1 contrato (Freddie para voz principal)");
        
        HashMap<Rol, Integer> rolesFaltantesDespues = cancion1.getRolesFaltantes(contratosDespues);
        
        // PASO 4: Verificar que voz principal ya no falta, pero guitarra y coros SI faltan
        assertEquals(0, rolesFaltantesDespues.get(vozPrincipal), 
            "DESPUES de contratar: Ya NO falta voz principal (Freddie la cubre)");
        
        assertEquals(1, rolesFaltantesDespues.get(guitarra), 
            "DESPUES de contratar: Todavia falta 1 guitarra (no hemos contratado guitarrista)");
        
        assertEquals(2, rolesFaltantesDespues.get(coros), 
            "DESPUES de contratar: Todavia faltan 2 coros (no hemos contratado coristas)");
        
        // DOCUMENTACION DEL RESULTADO:
        // Roles faltantes para 'Bohemian Rhapsody' ANTES de contratar:
        // - voz principal: 1
        // - guitarra electrica: 1
        // - coros: 2
        //
        // Roles faltantes para 'Bohemian Rhapsody' DESPUES de contratar a Freddie:
        // - voz principal: 0 (YA NO FALTA!)
        // - guitarra electrica: 1 (todavia falta)
        // - coros: 2 (todavia falta)
    }
    
    @Test
    @DisplayName("Test 1.3: Calcular roles faltantes despues de multiples contrataciones")
    void testRolesFaltantesDespuesDeMultiplesContrataciones() {
        // OBJETIVO: Verificar que al contratar varios artistas, los roles faltantes se reducen correctamente
        
        // PASO 1: Estado inicial - todos los roles faltan
        HashMap<Rol, Integer> rolesFaltantesInicio = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        
        assertEquals(1, rolesFaltantesInicio.get(vozPrincipal));
        assertEquals(1, rolesFaltantesInicio.get(guitarra));
        assertEquals(2, rolesFaltantesInicio.get(coros));
        
        // PASO 2: Contratar a Freddie para voz principal
        Contrato contratoFreddie = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie);
        freddie.setCantCancionesAsignado(1);
        
        // PASO 3: Verificar que voz principal ya esta cubierta
        HashMap<Rol, Integer> rolesDespuesFreddie = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        
        assertEquals(0, rolesDespuesFreddie.get(vozPrincipal), 
            "Despues de contratar a Freddie: voz principal cubierta");
        assertEquals(1, rolesDespuesFreddie.get(guitarra), 
            "Despues de contratar a Freddie: guitarra todavia falta");
        assertEquals(2, rolesDespuesFreddie.get(coros), 
            "Despues de contratar a Freddie: coros todavia faltan");
        
        // PASO 4: Contratar a Brian para guitarra
        Contrato contratoBrian = new Contrato(cancion1, guitarra, brian, 100.0);
        servicioContratacion.agregarContrato(contratoBrian);
        brian.setCantCancionesAsignado(1);
        
        // PASO 5: Verificar que voz y guitarra estan cubiertas
        HashMap<Rol, Integer> rolesDespuesBrian = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        
        assertEquals(0, rolesDespuesBrian.get(vozPrincipal), 
            "Despues de contratar a Brian: voz principal todavia cubierta");
        assertEquals(0, rolesDespuesBrian.get(guitarra), 
            "Despues de contratar a Brian: guitarra ahora cubierta");
        assertEquals(2, rolesDespuesBrian.get(coros), 
            "Despues de contratar a Brian: coros todavia faltan (necesitamos 2)");
        
        // PASO 6: Contratar a Roger para primer coro
        Contrato contratoRoger1 = new Contrato(cancion1, coros, roger, 100.0);
        servicioContratacion.agregarContrato(contratoRoger1);
        roger.setCantCancionesAsignado(1);
        
        // PASO 7: Verificar que solo falta 1 coro
        HashMap<Rol, Integer> rolesDespuesRoger = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        
        assertEquals(0, rolesDespuesRoger.get(vozPrincipal), 
            "Despues de contratar a Roger: voz principal todavia cubierta");
        assertEquals(0, rolesDespuesRoger.get(guitarra), 
            "Despues de contratar a Roger: guitarra todavia cubierta");
        assertEquals(1, rolesDespuesRoger.get(coros), 
            "Despues de contratar a Roger: solo falta 1 coro (teniamos 2, contratamos 1)");
        
        // PASO 8: Contratar a Freddie para segundo coro (puede tocar coros tambien)
        Contrato contratoFreddie2 = new Contrato(cancion1, coros, freddie, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie2);
        freddie.setCantCancionesAsignado(2);
        
        // PASO 9: Verificar que la cancion esta completa (no falta ningun rol)
        HashMap<Rol, Integer> rolesFinal = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        
        assertEquals(0, rolesFinal.get(vozPrincipal), 
            "Estado final: voz principal cubierta");
        assertEquals(0, rolesFinal.get(guitarra), 
            "Estado final: guitarra cubierta");
        assertEquals(0, rolesFinal.get(coros), 
            "Estado final: TODOS los coros cubiertos (2/2)");
        
        // PASO 10: Verificar que tenemos 4 contratos en total
        assertEquals(4, servicioContratacion.getContratosPorCancion(cancion1).size(), 
            "Deberiamos tener 4 contratos: Freddie(voz), Brian(guitarra), Roger(coro), Freddie(coro)");
        
        // DOCUMENTACION DEL RESULTADO:
        // Evolucion de roles faltantes para 'Bohemian Rhapsody':
        //
        // INICIO (sin contratos):
        // - voz principal: 1
        // - guitarra electrica: 1
        // - coros: 2
        //
        // DESPUES de contratar a Freddie (voz principal):
        // - voz principal: 0
        // - guitarra electrica: 1
        // - coros: 2
        //
        // DESPUES de contratar a Brian (guitarra):
        // - voz principal: 0
        // - guitarra electrica: 0
        // - coros: 2
        //
        // DESPUES de contratar a Roger (coro):
        // - voz principal: 0
        // - guitarra electrica: 0
        // - coros: 1
        //
        // DESPUES de contratar a Freddie (segundo coro):
        // - voz principal: 0
        // - guitarra electrica: 0
        // - coros: 0 (CANCION COMPLETA!)
    }
    
    @Test
    @DisplayName("Test 1.4: Roles faltantes de cancion diferente no se afectan entre si")
    void testRolesFaltantesDeCancionesDiferentes() {
        // OBJETIVO: Verificar que contratar para una cancion no afecta los roles faltantes de otra cancion
        
        // PASO 1: Verificar roles faltantes iniciales de ambas canciones
        HashMap<Rol, Integer> rolesBohemian = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        HashMap<Rol, Integer> rolesWeWillRock = cancion2.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion2));
        
        // Bohemian Rhapsody requiere: voz(1), guitarra(1), coros(2)
        assertEquals(1, rolesBohemian.get(vozPrincipal));
        assertEquals(1, rolesBohemian.get(guitarra));
        assertEquals(2, rolesBohemian.get(coros));
        
        // We Will Rock You requiere: voz(1), bateria(1)
        assertEquals(1, rolesWeWillRock.get(vozPrincipal));
        assertEquals(1, rolesWeWillRock.get(bateria));
        
        // PASO 2: Contratar a Freddie para voz principal en Bohemian Rhapsody (cancion1)
        Contrato contratoFreddie = new Contrato(cancion1, vozPrincipal, freddie, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie);
        freddie.setCantCancionesAsignado(1);
        
        // PASO 3: Verificar que solo cambio Bohemian Rhapsody, NO We Will Rock You
        HashMap<Rol, Integer> rolesBohemianDespues = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        HashMap<Rol, Integer> rolesWeWillRockDespues = cancion2.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion2));
        
        // Bohemian Rhapsody: voz ya no falta
        assertEquals(0, rolesBohemianDespues.get(vozPrincipal), 
            "Bohemian Rhapsody: voz principal cubierta por Freddie");
        
        // We Will Rock You: NO cambia (sigue faltando todo)
        assertEquals(1, rolesWeWillRockDespues.get(vozPrincipal), 
            "We Will Rock You: voz principal TODAVIA falta (Freddie solo esta en Bohemian)");
        assertEquals(1, rolesWeWillRockDespues.get(bateria), 
            "We Will Rock You: bateria TODAVIA falta (no contratamos baterista)");
        
        // PASO 4: Contratar a Roger para bateria en We Will Rock You (cancion2)
        Contrato contratoRoger = new Contrato(cancion2, bateria, roger, 100.0);
        servicioContratacion.agregarContrato(contratoRoger);
        roger.setCantCancionesAsignado(1);
        
        // PASO 5: Verificar que solo cambio We Will Rock You, NO Bohemian Rhapsody
        HashMap<Rol, Integer> rolesBohemianFinal = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        HashMap<Rol, Integer> rolesWeWillRockFinal = cancion2.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion2));
        
        // Bohemian Rhapsody: NO cambia
        assertEquals(0, rolesBohemianFinal.get(vozPrincipal), 
            "Bohemian Rhapsody: voz principal sigue cubierta");
        assertEquals(1, rolesBohemianFinal.get(guitarra), 
            "Bohemian Rhapsody: guitarra sigue faltando (Roger toca bateria, no guitarra)");
        
        // We Will Rock You: bateria ya no falta
        assertEquals(0, rolesWeWillRockFinal.get(bateria), 
            "We Will Rock You: bateria ahora cubierta por Roger");
        assertEquals(1, rolesWeWillRockFinal.get(vozPrincipal), 
            "We Will Rock You: voz principal todavia falta");
        
        // DOCUMENTACION DEL RESULTADO:
        // Los contratos son especificos por cancion, NO compartidos:
        //
        // Bohemian Rhapsody:
        // - Freddie contratado para voz principal
        // - Guitarra y coros todavia faltan
        //
        // We Will Rock You:
        // - Roger contratado para bateria
        // - Voz principal todavia falta
        //
        // Contratar a Freddie para Bohemian NO lo contrata automaticamente para We Will Rock You
    }
    
    @Test
    @DisplayName("Test 1.5: Roles faltantes con rol que requiere multiples personas")
    void testRolesFaltantesConRolMultiple() {
        // OBJETIVO: Verificar el calculo correcto cuando un rol requiere mas de 1 persona (ej: 2 coristas)
        
        // PASO 1: Verificar que coros requiere 2 personas en Bohemian Rhapsody
        HashMap<Rol, Integer> rolesRequeridos = cancion1.getRolesRequeridos();
        assertEquals(2, rolesRequeridos.get(coros), 
            "Bohemian Rhapsody requiere 2 coristas");
        
        // PASO 2: Sin contratos, faltan 2 coros
        HashMap<Rol, Integer> rolesFaltantesInicio = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        assertEquals(2, rolesFaltantesInicio.get(coros), 
            "Sin contratos: faltan 2 coros");
        
        // PASO 3: Contratar a Freddie para coros (primera persona)
        Contrato contratoFreddie = new Contrato(cancion1, coros, freddie, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie);
        freddie.setCantCancionesAsignado(1);
        
        HashMap<Rol, Integer> rolesDespuesFreddie = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        assertEquals(1, rolesDespuesFreddie.get(coros), 
            "Despues de contratar a Freddie: falta 1 coro (teniamos 2, contratamos 1)");
        
        // PASO 4: Contratar a Brian para coros (segunda persona)
        Contrato contratoBrian = new Contrato(cancion1, coros, brian, 100.0);
        servicioContratacion.agregarContrato(contratoBrian);
        brian.setCantCancionesAsignado(1);
        
        HashMap<Rol, Integer> rolesDespuesBrian = cancion1.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(cancion1));
        assertEquals(0, rolesDespuesBrian.get(coros), 
            "Despues de contratar a Brian: NO falta ningun coro (2/2 cubiertos)");
        
        // PASO 5: Verificar que hay 2 contratos diferentes para el mismo rol (coros)
        List<Contrato> contratosCoros = servicioContratacion.getContratosPorCancion(cancion1);
        long contratosDeCoros = contratosCoros.stream()
            .filter(c -> c.getRol().equals(coros))
            .count();
        assertEquals(2, contratosDeCoros, 
            "Deberia haber 2 contratos para el rol 'coros' (Freddie y Brian)");
        
        // DOCUMENTACION DEL RESULTADO:
        // Roles que requieren multiples personas se manejan correctamente:
        //
        // Bohemian Rhapsody requiere 2 coristas:
        // - Estado inicial: 2 coros faltantes
        // - Despues de contratar a Freddie: 1 coro faltante
        // - Despues de contratar a Brian: 0 coros faltantes
        //
        // Cada contrato cubre 1 posicion del rol
        // Se necesitan 2 contratos separados para cubrir 2 posiciones del mismo rol
    }
}
