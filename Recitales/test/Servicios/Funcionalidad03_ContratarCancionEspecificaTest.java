import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Recital.Recital;
import Recital.Cancion;
import Recital.Rol;
import Recital.Banda;
import Recital.Contrato;
import Repositorios.RepositorioArtistas;
import Repositorios.RepositorioRoles;
import Repositorios.RepositorioBandas;
import Artista.Artista;
import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Servicios.ServicioContratacion;
import Servicios.ServicioConsulta;
import Servicios.ServicioEntrenamiento;

@DisplayName("Tests Funcionalidad 3: Contratar Artistas para Cancion Especifica")
public class Funcionalidad03_ContratarCancionEspecificaTest {
    
    private Recital recital;
    private ServicioContratacion servicioContratacion;
    private ServicioConsulta servicioConsulta;
    private RepositorioArtistas repositorioArtistas;
    private RepositorioRoles rolCatalogo;
    private RepositorioBandas bandaCatalogo;
    
    private Rol vozPrincipal;
    private Rol guitarra;
    private Rol bateria;
    private Rol coros;
    
    private Cancion bohemianRhapsody;
    private Cancion weWillRockYou;
    
    private ArtistaDiscografica freddie;
    private ArtistaDiscografica brian;
    private ArtistaDiscografica roger;
    
    private Banda queen;
    
    @BeforeEach
    void setUp() {
        // PASO 1: Inicializar repositorios
        repositorioArtistas = new RepositorioArtistas();
        rolCatalogo = new RepositorioRoles();
        bandaCatalogo = new RepositorioBandas();
        
        // PASO 2: Crear roles
        vozPrincipal = rolCatalogo.agregarRol("voz principal");
        if (vozPrincipal == null) vozPrincipal = rolCatalogo.getRol("voz principal");
        
        guitarra = rolCatalogo.agregarRol("guitarra electrica");
        if (guitarra == null) guitarra = rolCatalogo.getRol("guitarra electrica");
        
        bateria = rolCatalogo.agregarRol("bateria");
        if (bateria == null) bateria = rolCatalogo.getRol("bateria");
        
        coros = rolCatalogo.agregarRol("coros");
        if (coros == null) coros = rolCatalogo.getRol("coros");
        
        // PASO 3: Crear banda
        queen = new Banda("Queen");
        bandaCatalogo.agregarBanda("Queen");
        
        // PASO 4: Crear canciones con roles requeridos
        bohemianRhapsody = new Cancion("Bohemian Rhapsody");
        bohemianRhapsody.agregarRolRequerido(vozPrincipal, 1);
        bohemianRhapsody.agregarRolRequerido(guitarra, 1);
        bohemianRhapsody.agregarRolRequerido(coros, 2);
        
        weWillRockYou = new Cancion("We Will Rock You");
        weWillRockYou.agregarRolRequerido(vozPrincipal, 1);
        weWillRockYou.agregarRolRequerido(bateria, 1);
        
        // PASO 5: Crear recital
        HashSet<Cancion> canciones = new HashSet<>();
        canciones.add(bohemianRhapsody);
        canciones.add(weWillRockYou);
        recital = new Recital(canciones);
        
        // PASO 6: Crear artistas base (discografica) con sus roles y banda
        freddie = new ArtistaDiscografica("Freddie Mercury", 10, 100.0);
        freddie.agregarRolHistorico(vozPrincipal);
        freddie.agregarRolHistorico(coros);
        freddie.agregarBandaHistorico(queen);
        
        brian = new ArtistaDiscografica("Brian May", 10, 100.0);
        brian.agregarRolHistorico(guitarra);
        brian.agregarRolHistorico(coros);
        brian.agregarBandaHistorico(queen);
        
        roger = new ArtistaDiscografica("Roger Taylor", 10, 100.0);
        roger.agregarRolHistorico(bateria);
        roger.agregarRolHistorico(coros);
        roger.agregarBandaHistorico(queen);
        
        // PASO 7: Agregar artistas al repositorio
        HashSet<ArtistaDiscografica> artistasBase = new HashSet<>();
        artistasBase.add(freddie);
        artistasBase.add(brian);
        artistasBase.add(roger);
        repositorioArtistas = new RepositorioArtistas(artistasBase, new HashSet<>());
        
        // PASO 8: Crear servicios
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        servicioContratacion = new ServicioContratacion();
    }
    
    @Test
    @DisplayName("Test 3.1: Contratar para cancion que requiere solo artistas base")
    void testContratarSoloArtistasBase() {
        // OBJETIVO: Verificar que se contratan correctamente artistas base cuando pueden cubrir todos los roles
        
        // PASO 1: Verificar estado inicial - no hay contratos
        assertEquals(0, servicioContratacion.getContratos().size(), 
            "Estado inicial: no debe haber contratos");
        
        // PASO 2: Contratar artistas para We Will Rock You (voz + bateria)
        // Esta cancion solo necesita artistas base (Freddie y Roger)
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = 
            servicioContratacion.contratarArtistasParaCancion(weWillRockYou, repositorioArtistas);
        
        // PASO 3: Verificar que NO se requiere entrenamiento (todos los roles cubiertos)
        assertNull(rolesQueRequierenEntrenamiento, 
            "No deberia requerir entrenamiento porque los artistas base cubren todos los roles");
        
        // PASO 4: Verificar que se crearon exactamente 2 contratos
        assertEquals(2, servicioContratacion.getContratosPorCancion(weWillRockYou).size(), 
            "Deberia haber 2 contratos: 1 para voz (Freddie) y 1 para bateria (Roger)");
        
        // PASO 5: Verificar que los artistas correctos fueron contratados
        boolean freddieContratado = servicioContratacion.getContratosPorCancion(weWillRockYou).stream()
            .anyMatch(c -> c.getArtista().equals(freddie) && c.getRol().equals(vozPrincipal));
        assertTrue(freddieContratado, "Freddie debe estar contratado para voz principal");
        
        boolean rogerContratado = servicioContratacion.getContratosPorCancion(weWillRockYou).stream()
            .anyMatch(c -> c.getArtista().equals(roger) && c.getRol().equals(bateria));
        assertTrue(rogerContratado, "Roger debe estar contratado para bateria");
        
        // PASO 6: Verificar que los costos son los correctos (sin descuentos, son artistas base)
        double costoTotal = servicioContratacion.getContratosPorCancion(weWillRockYou).stream()
            .mapToDouble(Contrato::obtenerCostoContrato)
            .sum();
        assertEquals(200.0, costoTotal, 0.01, 
            "Costo total debe ser 200 (100 de Freddie + 100 de Roger)");
        
        // DOCUMENTACION DEL RESULTADO:
        // We Will Rock You contratada exitosamente:
        // - Freddie Mercury (voz principal): $100
        // - Roger Taylor (bateria): $100
        // - Total: $200
        // - Sin entrenamiento requerido
    }
    
    @Test
    @DisplayName("Test 3.2: Contratar con descuento por banda compartida")
    void testDescuentoPorBandaCompartida() {
        // OBJETIVO: Verificar que un artista externo recibe 50% de descuento si comparte banda con un artista base ya contratado
        
        // PASO 1: Crear artista externo que comparte banda Queen
        ArtistaExterno adamLambert = new ArtistaExterno("Adam Lambert", 10, 300.0);
        adamLambert.agregarRolHistorico(coros);
        adamLambert.agregarBandaHistorico(queen); // Comparte banda con Freddie, Brian y Roger
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(adamLambert);
        repositorioArtistas = new RepositorioArtistas(
            new HashSet<>(java.util.Arrays.asList(freddie, brian, roger)), 
            externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Contratar primero a artistas base para Bohemian Rhapsody
        // Esto contratara a Freddie (voz), Brian (guitarra) y necesitara 2 coros
        HashMap<Rol, Integer> rolesEntrenamiento = 
            servicioContratacion.contratarArtistasParaCancion(bohemianRhapsody, repositorioArtistas);
        
        // PASO 3: Verificar que Freddie, Brian y Roger estan contratados
        // Bohemian Rhapsody necesita: voz (1), guitarra (1), coros (2)
        // Artistas base contratados: Freddie (voz), Brian (guitarra), Roger (coros)
        long contratosBase = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getArtista() instanceof ArtistaDiscografica)
            .count();
        assertEquals(3, contratosBase, 
            "Freddie (voz), Brian (guitarra) y Roger (coros) deberian estar contratados");
        
        // PASO 4: Verificar que Adam Lambert fue contratado con descuento
        // Como comparte banda Queen con Freddie y Brian, debe tener 50% descuento
        boolean adamContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(adamLambert));
        assertTrue(adamContratado, "Adam Lambert debe estar contratado para coros");
        
        // PASO 5: Verificar el costo aplicado (debe ser 150.0, que es 300.0 * 0.5)
        double costoAdam = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getArtista().equals(adamLambert))
            .findFirst()
            .map(Contrato::obtenerCostoContrato)
            .orElse(0.0);
        
        assertEquals(150.0, costoAdam, 0.01, 
            "Adam Lambert debe tener 50% de descuento: $300 * 0.5 = $150");
        
        // DOCUMENTACION DEL RESULTADO:
        // Bohemian Rhapsody con descuento por banda compartida:
        // - Freddie Mercury (voz): $100 (base)
        // - Brian May (guitarra): $100 (base)
        // - Adam Lambert (coro 1): $150 (50% descuento porque comparte banda Queen con Freddie y Brian)
        // - Falta 1 coro mas (requiere entrenamiento u otro artista)
        // 
        // REGLA: Si un artista externo comparte banda historica con AL MENOS UN artista base
        // ya contratado en esa cancion, recibe 50% de descuento (NO es acumulativo)
    }
    
    @Test
    @DisplayName("Test 3.3: Contratar sin descuento si NO comparte banda")
    void testSinDescuentoSiNoComparteBanda() {
        // OBJETIVO: Verificar que un artista externo NO recibe descuento si no comparte banda con artistas base
        
        // PASO 1: Crear artista externo SIN banda Queen
        Banda soloCareer = new Banda("Solo Career");
        ArtistaExterno prince = new ArtistaExterno("Prince", 10, 400.0);
        prince.agregarRolHistorico(coros);
        prince.agregarBandaHistorico(soloCareer); // NO comparte banda con Queen
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(prince);
        repositorioArtistas = new RepositorioArtistas(
            new HashSet<>(java.util.Arrays.asList(freddie, brian, roger)), 
            externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Contratar para Bohemian Rhapsody
        servicioContratacion.contratarArtistasParaCancion(bohemianRhapsody, repositorioArtistas);
        
        // PASO 3: Verificar que Prince fue contratado
        boolean princeContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(prince));
        assertTrue(princeContratado, "Prince debe estar contratado para coros");
        
        // PASO 4: Verificar que el costo es el COMPLETO (sin descuento)
        double costoPrince = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getArtista().equals(prince))
            .findFirst()
            .map(Contrato::obtenerCostoContrato)
            .orElse(0.0);
        
        assertEquals(400.0, costoPrince, 0.01, 
            "Prince NO debe tener descuento porque no comparte banda con artistas base: $400");
        
        // DOCUMENTACION DEL RESULTADO:
        // Prince NO recibe descuento porque:
        // - Su banda historica es 'Solo Career'
        // - Los artistas base contratados (Freddie, Brian) tienen banda 'Queen'
        // - NO hay coincidencia de bandas, entonces paga precio completo
    }
    
    @Test
    @DisplayName("Test 3.4: Descuento NO acumulativo con multiples artistas base")
    void testDescuentoNoAcumulativo() {
        // OBJETIVO: Verificar que el descuento es 50% fijo, NO se acumula si comparte banda con multiples artistas base
        
        // PASO 1: Crear artista externo que comparte banda con TODOS los artistas base
        ArtistaExterno johnDeacon = new ArtistaExterno("John Deacon", 10, 500.0);
        johnDeacon.agregarRolHistorico(coros);
        johnDeacon.agregarBandaHistorico(queen); // Comparte con Freddie, Brian Y Roger
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(johnDeacon);
        repositorioArtistas = new RepositorioArtistas(
            new HashSet<>(java.util.Arrays.asList(freddie, brian, roger)), 
            externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Contratar para Bohemian Rhapsody (contrata Freddie, Brian y John)
        servicioContratacion.contratarArtistasParaCancion(bohemianRhapsody, repositorioArtistas);
        
        // PASO 3: Verificar que John esta contratado
        boolean johnContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(johnDeacon));
        assertTrue(johnContratado, "John Deacon debe estar contratado");
        
        // PASO 4: Verificar que el descuento es EXACTAMENTE 50%, NO acumulativo
        double costoJohn = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getArtista().equals(johnDeacon))
            .findFirst()
            .map(Contrato::obtenerCostoContrato)
            .orElse(0.0);
        
        assertEquals(250.0, costoJohn, 0.01, 
            "Descuento debe ser 50% (no acumulativo): $500 * 0.5 = $250, NO $500 * 0.25 = $125");
        
        // DOCUMENTACION DEL RESULTADO:
        // John Deacon comparte banda Queen con Freddie Y Brian (2 artistas base)
        // Pero el descuento NO es acumulativo:
        // - NO es: 50% + 50% = 100% descuento (gratis)
        // - NO es: 25% + 25% = 50% descuento
        // - SI es: 50% fijo (sin importar con cuantos artistas base comparte banda)
        // 
        // Resultado: $500 * 0.5 = $250
    }
    
    @Test
    @DisplayName("Test 3.5: Caso limite - cancion sin roles faltantes")
    void testCancionYaCompletada() {
        // OBJETIVO: Verificar comportamiento cuando la cancion ya tiene todos los roles cubiertos
        
        // PASO 1: Contratar manualmente todos los roles de We Will Rock You
        Contrato contratoFreddie = new Contrato(weWillRockYou, vozPrincipal, freddie, 100.0);
        Contrato contratoRoger = new Contrato(weWillRockYou, bateria, roger, 100.0);
        servicioContratacion.agregarContrato(contratoFreddie);
        servicioContratacion.agregarContrato(contratoRoger);
        freddie.setCantCancionesAsignado(1);
        roger.setCantCancionesAsignado(1);
        
        // PASO 2: Verificar que la cancion esta completa
        HashMap<Rol, Integer> rolesFaltantes = weWillRockYou.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(weWillRockYou));
        assertEquals(0, rolesFaltantes.get(vozPrincipal));
        assertEquals(0, rolesFaltantes.get(bateria));
        
        // PASO 3: Intentar contratar nuevamente para la misma cancion
        int contratosAntes = servicioContratacion.getContratos().size();
        HashMap<Rol, Integer> resultado = 
            servicioContratacion.contratarArtistasParaCancion(weWillRockYou, repositorioArtistas);
        
        // PASO 4: Verificar que NO se agregaron nuevos contratos
        assertNull(resultado, "Debe retornar null porque la cancion ya esta completa");
        assertEquals(contratosAntes, servicioContratacion.getContratos().size(), 
            "No debe agregar nuevos contratos si la cancion ya esta completa");
        
        // DOCUMENTACION DEL RESULTADO:
        // Caso limite: Cancion ya completada
        // - Entrada: We Will Rock You con todos los roles ya contratados
        // - Salida: null (indica que no hay roles faltantes)
        // - Efecto: No se agregan contratos duplicados
    }
    
    @Test
    @DisplayName("Test 3.6: Caso limite - no hay artistas disponibles para un rol")
    void testSinArtistasDisponibles() {
        // OBJETIVO: Verificar que se detectan roles que requieren entrenamiento cuando no hay artistas disponibles
        
        // PASO 1: Crear cancion que requiere un rol que nadie puede tocar
        Rol saxofon = rolCatalogo.agregarRol("saxofon");
        if (saxofon == null) saxofon = rolCatalogo.getRol("saxofon");
        
        Cancion carelessWhisper = new Cancion("Careless Whisper");
        carelessWhisper.agregarRolRequerido(saxofon, 1);
        carelessWhisper.agregarRolRequerido(vozPrincipal, 1);
        
        HashSet<Cancion> canciones = new HashSet<>(recital.getCanciones());
        canciones.add(carelessWhisper);
        recital = new Recital(canciones);
        
        // PASO 2: Actualizar servicio de consulta
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 3: Intentar contratar para Careless Whisper
        HashMap<Rol, Integer> rolesEntrenamiento = 
            servicioContratacion.contratarArtistasParaCancion(carelessWhisper, repositorioArtistas);
        
        // PASO 4: Verificar que se detecta el rol faltante
        assertNotNull(rolesEntrenamiento, 
            "Debe retornar HashMap con roles que requieren entrenamiento");
        assertTrue(rolesEntrenamiento.containsKey(saxofon), 
            "Saxofon debe estar en roles que requieren entrenamiento");
        assertEquals(1, rolesEntrenamiento.get(saxofon), 
            "Falta 1 saxofonista");
        
        // PASO 5: Verificar que voz principal SI fue contratada (Freddie puede tocarla)
        boolean vozContratada = servicioContratacion.getContratosPorCancion(carelessWhisper).stream()
            .anyMatch(c -> c.getRol().equals(vozPrincipal));
        assertTrue(vozContratada, "Voz principal debe estar contratada (Freddie la cubre)");
        
        // DOCUMENTACION DEL RESULTADO:
        // Caso limite: Rol sin artistas disponibles
        // - Cancion: Careless Whisper requiere saxofon (1) y voz principal (1)
        // - Ningun artista en el repositorio puede tocar saxofon
        // - Resultado: voz principal contratada, saxofon requiere entrenamiento
        // - HashMap retornado: {saxofon: 1}
    }
    
    @Test
    @DisplayName("Test 3.7: Optimizacion por costo - elige artista mas barato")
    void testOptimizacionPorCosto() {
        // OBJETIVO: Verificar que cuando hay multiples artistas externos para un rol, se elige el mas barato
        
        // PASO 1: Crear artistas base SIN habilidad de coros (para forzar contratacion de externos)
        ArtistaDiscografica freddieVozSolo = new ArtistaDiscografica("Freddie Solo Voz", 10, 100.0);
        freddieVozSolo.agregarRolHistorico(vozPrincipal);
        
        ArtistaDiscografica brianGuitarraSolo = new ArtistaDiscografica("Brian Solo Guitarra", 10, 100.0);
        brianGuitarraSolo.agregarRolHistorico(guitarra);
        
        // PASO 2: Crear 3 artistas externos con diferentes costos, todos pueden tocar coros
        ArtistaExterno barato = new ArtistaExterno("Corista Barato", 10, 50.0);
        barato.agregarRolHistorico(coros);
        
        ArtistaExterno mediano = new ArtistaExterno("Corista Mediano", 10, 100.0);
        mediano.agregarRolHistorico(coros);
        
        ArtistaExterno caro = new ArtistaExterno("Corista Caro", 10, 200.0);
        caro.agregarRolHistorico(coros);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(barato);
        externos.add(mediano);
        externos.add(caro);
        
        repositorioArtistas = new RepositorioArtistas(
            new HashSet<>(java.util.Arrays.asList(freddieVozSolo, brianGuitarraSolo)), 
            externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 3: Contratar para Bohemian Rhapsody (necesita 2 coros)
        // Los artistas base solo cubren voz y guitarra, los coros se contratan con externos
        servicioContratacion.contratarArtistasParaCancion(bohemianRhapsody, repositorioArtistas);
        
        // PASO 4: Verificar que se contrataron los 2 mas baratos
        boolean baratoContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(barato));
        assertTrue(baratoContratado, "Debe contratar al corista mas barato ($50)");
        
        boolean medianoContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(mediano));
        assertTrue(medianoContratado, "Debe contratar al segundo mas barato ($100)");
        
        boolean caroContratado = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .anyMatch(c -> c.getArtista().equals(caro));
        assertFalse(caroContratado, "NO debe contratar al corista mas caro ($200)");
        
        // PASO 5: Verificar costo total de los coros
        double costoTotalCoros = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getRol().equals(coros))
            .mapToDouble(Contrato::obtenerCostoContrato)
            .sum();
        
        assertEquals(150.0, costoTotalCoros, 0.01, 
            "Costo total de coros debe ser $150 ($50 + $100), NO $300");
        
        // DOCUMENTACION DEL RESULTADO:
        // Optimizacion por costo en contratacion especifica:
        // - Disponibles: Barato ($50), Mediano ($100), Caro ($200)
        // - Se necesitan: 2 coristas
        // - Algoritmo: Ordena por costo y elige los 2 mas baratos
        // - Resultado: Barato + Mediano = $150 (ahorro de $200 vs contratar Mediano + Caro)
    }
    
    @Test
    @DisplayName("Test 3.8: Caso limite - artista no puede tener multiples contratos en misma cancion")
    void testUnArtistaUnRolPorCancion() {
        // OBJETIVO: Verificar que un artista no es contratado multiples veces para la misma cancion
        
        // PASO 1: Crear artista que puede tocar TODOS los roles de una cancion
        ArtistaExterno multiinstrumentista = new ArtistaExterno("Multi Instrumentista", 10, 100.0);
        multiinstrumentista.agregarRolHistorico(vozPrincipal);
        multiinstrumentista.agregarRolHistorico(guitarra);
        multiinstrumentista.agregarRolHistorico(coros);
        
        HashSet<ArtistaExterno> externos = new HashSet<>();
        externos.add(multiinstrumentista);
        
        // NO agregar artistas base, solo el multiinstrumentista
        repositorioArtistas = new RepositorioArtistas(new HashSet<>(), externos);
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Contratar para Bohemian Rhapsody (voz + guitarra + 2 coros = 4 roles)
        servicioContratacion.contratarArtistasParaCancion(bohemianRhapsody, repositorioArtistas);
        
        // PASO 3: Verificar que el multiinstrumentista solo tiene 1 contrato (no 4)
        long contratosMulti = servicioContratacion.getContratosPorCancion(bohemianRhapsody).stream()
            .filter(c -> c.getArtista().equals(multiinstrumentista))
            .count();
        
        assertEquals(1, contratosMulti, 
            "Un artista solo puede tener 1 contrato por cancion, aunque pueda tocar multiples roles");
        
        // PASO 4: Verificar que quedan roles faltantes
        HashMap<Rol, Integer> rolesFaltantes = bohemianRhapsody.getRolesFaltantes(
            servicioContratacion.getContratosPorCancion(bohemianRhapsody));
        
        int totalRolesFaltantes = rolesFaltantes.values().stream().mapToInt(Integer::intValue).sum();
        assertTrue(totalRolesFaltantes >= 3, 
            "Deben faltar al menos 3 roles (de los 4 totales, solo 1 esta cubierto)");
        
        // DOCUMENTACION DEL RESULTADO:
        // Regla: Un artista = Un rol por cancion
        // - Multi Instrumentista puede tocar voz, guitarra y coros
        // - Bohemian Rhapsody necesita: voz (1) + guitarra (1) + coros (2) = 4 roles
        // - Pero el artista solo puede cubrir 1 de estos roles
        // - Los otros 3 roles quedan sin cubrir
        // 
        // Esto simula la realidad: una persona no puede tocar 4 instrumentos simultaneamente en vivo!
    }
    
    @Test
    @DisplayName("Test 3.9: Detectar necesidad de entrenamiento - cancion Criminal")
    void testDetectarNecesidadEntrenamiento() {
        // OBJETIVO: Verificar que el sistema detecta roles que requieren entrenamiento y retorna la informacion correcta
        // CASO REAL: Cancion "Criminal" de Fiona Apple requiere guitarra acustica, que nadie en el repositorio puede tocar
        
        // PASO 1: Crear rol de guitarra acustica (que nadie tiene)
        Rol guitarraAcustica = rolCatalogo.agregarRol("guitarra acustica");
        if (guitarraAcustica == null) guitarraAcustica = rolCatalogo.getRol("guitarra acustica");
        
        Rol bajo = rolCatalogo.agregarRol("bajo");
        if (bajo == null) bajo = rolCatalogo.getRol("bajo");
        
        Rol teclados = rolCatalogo.agregarRol("teclados");
        if (teclados == null) teclados = rolCatalogo.getRol("teclados");
        
        // PASO 2: Crear cancion Criminal con sus requisitos reales
        Cancion criminal = new Cancion("Criminal");
        criminal.agregarRolRequerido(vozPrincipal, 1);
        criminal.agregarRolRequerido(guitarraAcustica, 1); // NADIE puede tocar esto
        criminal.agregarRolRequerido(bajo, 1);
        criminal.agregarRolRequerido(bateria, 1);
        criminal.agregarRolRequerido(teclados, 1);
        
        HashSet<Cancion> canciones = new HashSet<>(recital.getCanciones());
        canciones.add(criminal);
        recital = new Recital(canciones);
        
        // PASO 3: Actualizar servicio de consulta
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 4: Intentar contratar artistas para Criminal
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = 
            servicioContratacion.contratarArtistasParaCancion(criminal, repositorioArtistas);
        
        // PASO 5: Verificar que se detectaron roles faltantes
        assertNotNull(rolesQueRequierenEntrenamiento, 
            "Debe retornar HashMap porque faltan roles que requieren entrenamiento");
        
        // PASO 6: Verificar que guitarra acustica, bajo y teclados requieren entrenamiento
        assertTrue(rolesQueRequierenEntrenamiento.containsKey(guitarraAcustica), 
            "Guitarra acustica debe requerir entrenamiento (nadie la toca)");
        assertEquals(1, rolesQueRequierenEntrenamiento.get(guitarraAcustica), 
            "Falta 1 guitarrista acustico");
        
        assertTrue(rolesQueRequierenEntrenamiento.containsKey(bajo), 
            "Bajo debe requerir entrenamiento (nadie lo toca)");
        assertEquals(1, rolesQueRequierenEntrenamiento.get(bajo), 
            "Falta 1 bajista");
        
        assertTrue(rolesQueRequierenEntrenamiento.containsKey(teclados), 
            "Teclados debe requerir entrenamiento (nadie lo toca)");
        assertEquals(1, rolesQueRequierenEntrenamiento.get(teclados), 
            "Falta 1 tecladista");
        
        // PASO 7: Verificar que algunos roles SI fueron contratados (los que los artistas base pueden tocar)
        long contratosCreados = servicioContratacion.getContratosPorCancion(criminal).size();
        assertTrue(contratosCreados >= 2, 
            "Deberian haberse contratado al menos 2 artistas (Freddie para voz, Roger para bateria)");
        
        boolean freddieContratado = servicioContratacion.getContratosPorCancion(criminal).stream()
            .anyMatch(c -> c.getArtista().equals(freddie) && c.getRol().equals(vozPrincipal));
        assertTrue(freddieContratado, "Freddie debe estar contratado para voz principal");
        
        boolean rogerContratado = servicioContratacion.getContratosPorCancion(criminal).stream()
            .anyMatch(c -> c.getArtista().equals(roger) && c.getRol().equals(bateria));
        assertTrue(rogerContratado, "Roger debe estar contratado para bateria");
        
        // DOCUMENTACION DEL RESULTADO:
        // Caso real: Cancion "Criminal" de Fiona Apple
        // 
        // Requisitos:
        // - voz principal (1): Freddie puede -> CONTRATADO
        // - guitarra acustica (1): NADIE puede -> REQUIERE ENTRENAMIENTO
        // - bajo (1): NADIE puede -> REQUIERE ENTRENAMIENTO  
        // - bateria (1): Roger puede -> CONTRATADO
        // - teclados (1): NADIE puede -> REQUIERE ENTRENAMIENTO
        //
        // Resultado del metodo contratarArtistasParaCancion:
        // - Retorna HashMap<Rol, Integer> con:
        //   {guitarra acustica: 1, bajo: 1, teclados: 1}
        //
        // Accion esperada del sistema:
        // 1. Mostrar mensaje: "Faltan roles que requieren entrenamiento"
        // 2. Listar roles faltantes con cantidades
        // 3. Usuario puede entonces usar funcionalidad 5 (Entrenar artista)
        // 4. Entrenar a Brian en guitarra acustica
        // 5. Entrenar a Roger en bajo (puede tocar bateria Y bajo)
        // 6. Entrenar a Freddie en teclados
        // 7. Volver a intentar contratacion para completar la cancion
    }
    
    @Test
    @DisplayName("Test 3.10: Entrenamiento y recontratacion exitosa")
    void testEntrenamientoYRecontratacion() {
        // OBJETIVO: Verificar el flujo completo: detectar rol faltante -> entrenar artista -> recontratar exitosamente
        
        // PASO 1: Crear cancion que necesita guitarra acustica
        final Rol guitarraAcustica = rolCatalogo.agregarRol("guitarra acustica") != null 
            ? rolCatalogo.agregarRol("guitarra acustica") 
            : rolCatalogo.getRol("guitarra acustica");
        
        Cancion cancionSimple = new Cancion("Blackbird");
        cancionSimple.agregarRolRequerido(vozPrincipal, 1);
        cancionSimple.agregarRolRequerido(guitarraAcustica, 1);
        
        HashSet<Cancion> canciones = new HashSet<>(recital.getCanciones());
        canciones.add(cancionSimple);
        recital = new Recital(canciones);
        
        servicioConsulta = new ServicioConsulta(repositorioArtistas, recital, rolCatalogo, bandaCatalogo);
        
        // PASO 2: Primer intento de contratacion - deberia fallar parcialmente
        HashMap<Rol, Integer> rolesEntrenamientoAntes = 
            servicioContratacion.contratarArtistasParaCancion(cancionSimple, repositorioArtistas);
        
        assertNotNull(rolesEntrenamientoAntes, 
            "Primera contratacion: debe faltar guitarra acustica");
        assertTrue(rolesEntrenamientoAntes.containsKey(guitarraAcustica), 
            "Guitarra acustica debe requerir entrenamiento");
        
        // Verificar que voz SI fue contratada
        boolean vozContratadaAntes = servicioContratacion.getContratosPorCancion(cancionSimple).stream()
            .anyMatch(c -> c.getRol().equals(vozPrincipal));
        assertTrue(vozContratadaAntes, "Voz principal debe estar contratada (Freddie)");
        
        // PASO 3: ENTRENAR a Brian en guitarra acustica (simulando funcionalidad 5)
        // En el sistema real, esto se haria con ServicioEntrenamiento.entrenarArtista()
        // Aqui lo simulamos directamente agregando el rol a su historial
        brian.agregarRolHistorico(guitarraAcustica);
        
        // PASO 4: Verificar que Brian ahora puede tocar guitarra acustica
        assertTrue(brian.puedeTocarRol(guitarraAcustica), 
            "Despues del entrenamiento: Brian debe poder tocar guitarra acustica");
        
        // PASO 5: Segundo intento de contratacion - ahora deberia ser exitoso
        HashMap<Rol, Integer> rolesEntrenamientoDespues = 
            servicioContratacion.contratarArtistasParaCancion(cancionSimple, repositorioArtistas);
        
        // PASO 6: Verificar que ahora NO se requiere entrenamiento (cancion completa)
        assertNull(rolesEntrenamientoDespues, 
            "Segunda contratacion: NO debe requerir entrenamiento porque Brian fue entrenado");
        
        // PASO 7: Verificar que Brian fue contratado para guitarra acustica
        boolean brianContratado = servicioContratacion.getContratosPorCancion(cancionSimple).stream()
            .anyMatch(c -> c.getArtista().equals(brian) && c.getRol().equals(guitarraAcustica));
        assertTrue(brianContratado, 
            "Brian debe estar contratado para guitarra acustica despues del entrenamiento");
        
        // PASO 8: Verificar que la cancion esta completa (2 contratos: voz + guitarra acustica)
        assertEquals(2, servicioContratacion.getContratosPorCancion(cancionSimple).size(), 
            "La cancion debe tener 2 contratos: voz (Freddie) + guitarra acustica (Brian)");
        
        // PASO 9: Verificar contador de canciones asignadas
        assertEquals(1, freddie.getCantCancionesAsignadas(), 
            "Freddie debe tener 1 cancion asignada");
        assertEquals(1, brian.getCantCancionesAsignadas(), 
            "Brian debe tener 1 cancion asignada");
        
        // DOCUMENTACION DEL RESULTADO:
        // Flujo completo de entrenamiento y contratacion:
        //
        // SITUACION INICIAL:
        // - Cancion "Blackbird" requiere: voz principal (1) + guitarra acustica (1)
        // - Freddie puede tocar voz principal
        // - NADIE puede tocar guitarra acustica
        //
        // PRIMER INTENTO DE CONTRATACION:
        // - Contrata a Freddie para voz principal
        // - Detecta que falta guitarra acustica
        // - Retorna: {guitarra acustica: 1}
        //
        // USUARIO ENTRENA A BRIAN:
        // - Brian aprende guitarra acustica
        // - Ahora Brian.puedeTocarRol(guitarraAcustica) == true
        //
        // SEGUNDO INTENTO DE CONTRATACION:
        // - Contrata a Brian para guitarra acustica
        // - Cancion completa!
        // - Retorna: null (exito)
        //
        // CONTRATOS FINALES:
        // 1. Freddie Mercury -> voz principal -> $100
        // 2. Brian May -> guitarra acustica -> $100
        // Total: $200
    }
}
