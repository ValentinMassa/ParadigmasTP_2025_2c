package Servicios;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Recital;
import Recital.Rol;

import java.util.ArrayList;
import java.util.List;

import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;
import Repositorios.RepositorioArtistasMemory;

/**
 * Servicio para resolver problemas de optimización de entrenamientos con Prolog.
 * Integra JPL (Java-Prolog Library) para ejecutar consultas Prolog.
 * 
 * Pregunta: ¿Cuántos entrenamientos mínimos debo realizar para cubrir todos los roles 
 * para el recital, utilizando solo los miembros base, y artistas contratados sin experiencia 
 * y con un coste base por parámetro, para todos iguales?
 */
public class EntrenamientosProlog {
    
    private Recital recital;
    private RepositorioArtistasMemory repositorioArtistas;
    private String rutaArchivoPrologActual;
    private static final String ARCHIVO_PROLOG_DEFECTO = "bin/ArchivosImport/entrenamientos.pl";
    private boolean prologInicializado = false;

    /**
     * Constructor que recibe el recital a analizar.
     * @param recital Recital con artistas base, externos y canciones
     * @throws IllegalArgumentException si recital es nulo
     */
    public EntrenamientosProlog(Recital recital) throws IllegalArgumentException {
        inicializarConRecital(recital, null, null);
    }

    /**
     * Constructor alternativo que especifica la ruta del archivo Prolog.
     * @param recital Recital con artistas base, externos y canciones
     * @param rutaArchivoProlog ruta del archivo .pl con la lógica Prolog
     * @throws IllegalArgumentException si recital es nulo
     */
    public EntrenamientosProlog(Recital recital, String rutaArchivoProlog) throws IllegalArgumentException {
        inicializarConRecital(recital, null, rutaArchivoProlog);
    }

    public EntrenamientosProlog(ServicioConsulta servicioConsulta) throws IllegalArgumentException {
        if (servicioConsulta == null) {
            throw new IllegalArgumentException("El servicio de consulta no puede ser nulo");
        }
        inicializarConRecital(servicioConsulta.getRecital(), servicioConsulta.getRepositorioArtistas(), null);
    }

    public EntrenamientosProlog(ServicioConsulta servicioConsulta, String rutaArchivoProlog) throws IllegalArgumentException {
        if (servicioConsulta == null) {
            throw new IllegalArgumentException("El servicio de consulta no puede ser nulo");
        }
        inicializarConRecital(servicioConsulta.getRecital(), servicioConsulta.getRepositorioArtistas(), rutaArchivoProlog);
    }

    private void inicializarConRecital(Recital recital, RepositorioArtistasMemory repositorio, String rutaArchivoProlog) {
        if (recital == null) {
            throw new IllegalArgumentException("El recital no puede ser nulo");
        }
        this.recital = recital;
        this.repositorioArtistas = repositorio;
        this.rutaArchivoPrologActual = (rutaArchivoProlog != null && !rutaArchivoProlog.isBlank())
            ? rutaArchivoProlog
            : ARCHIVO_PROLOG_DEFECTO;
        inicializarProlog();
    }

    /**
     * Inicializa Prolog y carga el archivo de conocimientos.
     */
    private void inicializarProlog() {
        try {
            cargarArchivoProlog();
            prologInicializado = true;
        } catch (Exception e) {
            prologInicializado = false;
        }
    }

    private void cargarArchivoProlog() throws Exception {
        try {
            Query q1 = new Query("consult", new Term[] { new Atom(rutaArchivoPrologActual) });
            if (!q1.hasSolution()) {
                throw new Exception("No se pudo consultar el archivo Prolog: " + rutaArchivoPrologActual);
            }
        } catch (Exception e) {
            throw new Exception("Error cargando archivo Prolog: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula el número mínimo de entrenamientos necesarios.
     * @return número mínimo de entrenamientos necesarios
     * @throws Exception si hay error en el cálculo
     */
    public int calcularEntrenamientosMinimos() throws Exception {
        return calcularConProlog();
    }

    private int calcularConProlog() throws Exception {
        Map<Rol, Integer> rolesReq = extraerRolesRequeridosConCantidad();
        HashSet<ArtistaDiscografica> base = getArtistasBase();
        
        // Limpiar base de conocimiento previa
        Query.hasSolution("retractall(rol_requerido(_, _))");
        Query.hasSolution("retractall(base_tiene_rol(_, _))");
        
        // Assert facts
        for (Map.Entry<Rol, Integer> entry : rolesReq.entrySet()) {
            Rol rol = entry.getKey();
            int cantidad = entry.getValue();
            Query q = new Query("assert", new Term[] { 
                new Compound("rol_requerido", new Term[] { 
                    new Atom(rol.getNombre()), 
                    new org.jpl7.Integer(cantidad) 
                }) 
            });
            q.hasSolution();
        }
        
        for (ArtistaDiscografica a : base) {
            for (Rol r : a.getRoles()) {
                Query q = new Query("assert", new Term[] { 
                    new Compound("base_tiene_rol", new Term[] { 
                        new Atom(a.getNombre()), 
                        new Atom(r.getNombre()) 
                    }) 
                });
                q.hasSolution();
            }
        }
        
        // Query min_trainings
        Variable Min = new Variable("Min");
        Query q = new Query("min_trainings", new Term[] { Min });
        if (q.hasSolution()) {
            Map<String, Term> solution = q.oneSolution();
            Term minTerm = solution.get("Min");
            if (minTerm.isInteger()) {
                return ((org.jpl7.Integer) minTerm).intValue();
            }
        }
        throw new Exception("No se encontró solución para min_trainings");
    }

    public ResultadoEntrenamiento calcularEntrenamientosConParametros(
            double costoBase,
            HashSet<ArtistaExterno> artistasContratados) throws Exception {
        
        try {
            int entrenamientos = calcularConPrologParametrizado(costoBase, artistasContratados);
            
            Map<Rol, Integer> rolesRequeridos = extraerRolesRequeridosConCantidad();
            List<String> rolesFaltantes = new ArrayList<>();
            int totalRoles = 0;
            for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
                Rol rol = entry.getKey();
                int cantidad = entry.getValue();
                totalRoles += cantidad;
                int conBase = contarArtistaBaseConRol(rol);
                int conContratados = contarArtistaContratadoConRol(rol, artistasContratados);
                int faltantes = Math.max(0, cantidad - (conBase + conContratados));
                for (int i = 0; i < faltantes; i++) {
                    rolesFaltantes.add(rol.getNombre());
                }
            }
            
            double costoTotal = costoBase * entrenamientos;
            
            return new ResultadoEntrenamiento(
                entrenamientos,
                costoTotal,
                costoBase,
                rolesFaltantes,
                totalRoles
            );
        } catch (Exception e) {
            throw new Exception("Error calculando entrenamientos con parámetros: " + e.getMessage(), e);
        }
    }

    private int calcularConPrologParametrizado(double costoBase, HashSet<ArtistaExterno> artistasContratados) throws Exception {
        Map<Rol, Integer> rolesReq = extraerRolesRequeridosConCantidad();
        HashSet<ArtistaDiscografica> base = getArtistasBase();
        HashSet<ArtistaExterno> artistasSinExperiencia = filtrarContratadosSinExperiencia(artistasContratados);
        
        // Limpiar base de conocimiento previa
        Query.hasSolution("retractall(rol_requerido(_, _))");
        Query.hasSolution("retractall(base_tiene_rol(_, _))");
        Query.hasSolution("retractall(artista_contratado(_))");
        
        // Assert facts de roles requeridos
        for (Map.Entry<Rol, Integer> entry : rolesReq.entrySet()) {
            Rol rol = entry.getKey();
            int cantidad = entry.getValue();
            Query q = new Query("assert", new Term[] { 
                new Compound("rol_requerido", new Term[] { 
                    new Atom(rol.getNombre()), 
                    new org.jpl7.Integer(cantidad) 
                }) 
            });
            q.hasSolution();
        }
        
        // Assert facts de artistas base con sus roles
        for (ArtistaDiscografica a : base) {
            for (Rol r : a.getRoles()) {
                Query q = new Query("assert", new Term[] { 
                    new Compound("base_tiene_rol", new Term[] { 
                        new Atom(a.getNombre()), 
                        new Atom(r.getNombre()) 
                    }) 
                });
                q.hasSolution();
            }
        }
        
        // Assert facts de artistas contratados sin experiencia
        for (ArtistaExterno a : artistasSinExperiencia) {
            Query q = new Query("assert", new Term[] { 
                new Compound("artista_contratado", new Term[] { new Atom(a.getNombre()) }) 
            });
            q.hasSolution();
        }
        
        // Query min_trainings
        Variable Min = new Variable("Min");
        Query q = new Query("min_trainings", new Term[] { Min });
        if (q.hasSolution()) {
            Map<String, Term> solution = q.oneSolution();
            Term minTerm = solution.get("Min");
            if (minTerm.isInteger()) {
                int entrenamientos = ((org.jpl7.Integer) minTerm).intValue();
                if (entrenamientos > 0 && artistasSinExperiencia.isEmpty()) {
                    throw new Exception("No hay artistas contratados sin experiencia disponibles para entrenar los roles faltantes");
                }
                return entrenamientos;
            }
        }
        throw new Exception("No se encontró solución para min_trainings");
    }

    private HashSet<ArtistaDiscografica> getArtistasBase() {
        if (repositorioArtistas != null) {
            return repositorioArtistas.getArtistasDiscografica();
        }
        try {
            java.lang.reflect.Field field = Recital.class.getDeclaredField("artistaBase");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaDiscografica> artistas = (HashSet<ArtistaDiscografica>) field.get(recital);
            return artistas != null ? artistas : new HashSet<>();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    private Map<Rol, Integer> extraerRolesRequeridosConCantidad() {
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        for (Cancion cancion : recital.getCanciones()) {
            Map<Rol, Integer> requeridos = cancion.getRolesRequeridos();
            for (Map.Entry<Rol, Integer> entry : requeridos.entrySet()) {
                rolesRequeridos.put(entry.getKey(),
                    rolesRequeridos.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return rolesRequeridos;
    }

    private int contarArtistaBaseConRol(Rol rol) {
        int contador = 0;
        HashSet<ArtistaDiscografica> artistasBase = getArtistasBase();
        for (ArtistaDiscografica artista : artistasBase) {
            if (artista.puedeTocarRol(rol)) {
                contador++;
            }
        }
        return contador;
    }

    private int contarArtistaContratadoConRol(Rol rol, HashSet<ArtistaExterno> artistasContratados) {
        if (artistasContratados == null) return 0;
        
        int contador = 0;
        for (ArtistaExterno artista : artistasContratados) {
            if (artista.puedeTocarRol(rol)) {
                contador++;
            }
        }
        return contador;
    }

    private HashSet<ArtistaExterno> filtrarContratadosSinExperiencia(HashSet<ArtistaExterno> artistasContratados) {
        HashSet<ArtistaExterno> novatos = new HashSet<>();
        if (artistasContratados == null) return novatos;
        for (ArtistaExterno artista : artistasContratados) {
            if (artista.getRolesEntrenados().isEmpty()) {
                novatos.add(artista);
            }
        }
        return novatos;
    }

    /**
     * Genera un reporte textual de los entrenamientos necesarios.
     * @return texto descriptivo del análisis
     */
    public String generarReporteEntrenamientos() {
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("╔═══════════════════════════════════════════════════════════╗\n");
        reporte.append("║       REPORTE DE ENTRENAMIENTOS MÍNIMOS                   ║\n");
        reporte.append("╚═══════════════════════════════════════════════════════════╝\n\n");
        
        try {
            int entrenamientos = calcularEntrenamientosMinimos();
            Map<Rol, Integer> rolesRequeridos = extraerRolesRequeridosConCantidad();
            int totalRoles = rolesRequeridos.values().stream().mapToInt(Integer::intValue).sum();

            reporte.append("📊 ANÁLISIS GENERAL:\n");
            reporte.append("─────────────────────────────────────────────────────────\n");
            reporte.append("Entrenamientos mínimos requeridos: ").append(entrenamientos).append("\n");
            reporte.append("Total de roles requeridos: ").append(totalRoles).append("\n");
            reporte.append("Estado de Prolog: ").append(prologInicializado ? "✓ Activo" : "⚠ Inactivo (usando heurística)").append("\n\n");
            
            reporte.append("🎭 DETALLE DE ROLES:\n");
            reporte.append("─────────────────────────────────────────────────────────\n");
            
            Map<String, Integer> rolesAFalt = obtenerRolesAEntrenar();
            for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
                Rol rol = entry.getKey();
                int cantidadRequerida = entry.getValue();
                int disponibles = contarArtistaBaseConRol(rol);
                reporte.append("  • ").append(rol.getNombre())
                       .append(": ").append(disponibles).append(" / ")
                       .append(cantidadRequerida).append(" cubiertos");
                int faltan = rolesAFalt.getOrDefault(rol.getNombre(), 0);
                if (faltan > 0) {
                    reporte.append(" [⚠ ").append(faltan).append(" necesitan entrenamiento]");
                }
                reporte.append("\n");
            }
            
            reporte.append("\n");
        } catch (Exception e) {
            reporte.append("Error al calcular: ").append(e.getMessage()).append("\n");
        }
        
        return reporte.toString();
    }

    /**
     * Valida si es posible cubrir todos los roles con entrenamientos.
     * @return true si es posible, false si falta capacidad
     */
    public boolean esViableCubrir() {
        try {
            int artistas = contarArtistasTotales();
            int entrenamientos = calcularEntrenamientosMinimos();
            return artistas >= entrenamientos;
        } catch (Exception e) {
            return false;
        }
    }

    private int contarArtistasTotales() {
        if (repositorioArtistas != null) {
            return repositorioArtistas.getArtistasDiscografica().size()
                    + repositorioArtistas.getArtistasExternos().size();
        }
        int total = 0;
        
        try {
            java.lang.reflect.Field baseField = Recital.class.getDeclaredField("artistaBase");
            baseField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaDiscografica> artistasBase = (HashSet<ArtistaDiscografica>) baseField.get(recital);
            if (artistasBase != null) total += artistasBase.size();
            
            java.lang.reflect.Field externosField = Recital.class.getDeclaredField("artistaExternos");
            externosField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaExterno> artistasExternos = (HashSet<ArtistaExterno>) externosField.get(recital);
            if (artistasExternos != null) total += artistasExternos.size();
        } catch (Exception e) {
        }
        
        return total;
    }

    public Map<String, Integer> obtenerRolesAEntrenar() {
        Map<String, Integer> resultado = new HashMap<>();
        Map<Rol, Integer> rolesRequeridos = extraerRolesRequeridosConCantidad();
        for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
            int disponibles = contarArtistaBaseConRol(entry.getKey());
            int faltantes = Math.max(0, entry.getValue() - disponibles);
            if (faltantes > 0) {
                resultado.put(entry.getKey().getNombre(), faltantes);
            }
        }
        
        return resultado;
    }

    /**
     * Retorna el estado actual de inicialización de Prolog.
     * @return true si Prolog está inicializado correctamente
     */
    public boolean isPrologInicializado() {
        return prologInicializado;
    }



    /**
     * Clase interna para encapsular resultados de cálculo con parámetros.
     */
    public static class ResultadoEntrenamiento {
        private int entrenamientosMinimos;
        private double costoTotal;
        private double costoBase;
        private List<String> rolesFaltantes;
        private int rolesRequeridosTotales;

        public ResultadoEntrenamiento(int entrenamientosMinimos, double costoTotal, 
                                     double costoBase, List<String> rolesFaltantes, 
                                     int rolesRequeridosTotales) {
            this.entrenamientosMinimos = entrenamientosMinimos;
            this.costoTotal = costoTotal;
            this.costoBase = costoBase;
            this.rolesFaltantes = rolesFaltantes;
            this.rolesRequeridosTotales = rolesRequeridosTotales;
        }

        public int getEntrenamientosMinimos() { return entrenamientosMinimos; }
        public double getCostoTotal() { return costoTotal; }
        public double getCostoBase() { return costoBase; }
        public List<String> getRolesFaltantes() { return rolesFaltantes; }
        public int getRolesRequeridosTotales() { return rolesRequeridosTotales; }

        @Override
        public String toString() {
            return "ResultadoEntrenamiento{" +
                    "entrenamientosMinimos=" + entrenamientosMinimos +
                    ", costoTotal=" + costoTotal +
                    ", costoBase=" + costoBase +
                    ", rolesFaltantes=" + rolesFaltantes +
                    ", rolesRequeridosTotales=" + rolesRequeridosTotales +
                    '}';
        }
    }
}
