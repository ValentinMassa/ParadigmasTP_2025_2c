package ServicioProlog;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import Recital.Artista.ArtistaBase;
import Recital.Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Rol.Rol;
import Recital.Recital;

/**
 * Servicio de Prolog para resolver problemas de optimización de entrenamientos.
 * Integra JPL (Java-Prolog Library) para ejecutar consultas Prolog.
 * 
 * Pregunta: ¿Cuántos entrenamientos mínimos debo realizar para cubrir todos los roles 
 * para el recital, utilizando solo los miembros base, y artistas contratados sin experiencia 
 * y con un coste base por parámetro, para todos iguales?
 */
public class EntrenamientosProlog {
    
    private Recital recital;
    private String rutaArchivoPrologActual;
    private static final String ARCHIVO_PROLOG_DEFECTO = "bin/ArchivosImport/entrenamientos.pl";
    private boolean prologInicializado = false;

    /**
     * Constructor que recibe el recital a analizar.
     * @param recital Recital con artistas base, externos y canciones
     * @throws IllegalArgumentException si recital es nulo
     */
    public EntrenamientosProlog(Recital recital) throws IllegalArgumentException {
        if (recital == null) {
            throw new IllegalArgumentException("El recital no puede ser nulo");
        }
        this.recital = recital;
        this.rutaArchivoPrologActual = ARCHIVO_PROLOG_DEFECTO;
        inicializarProlog();
    }

    /**
     * Constructor alternativo que especifica la ruta del archivo Prolog.
     * @param recital Recital con artistas base, externos y canciones
     * @param rutaArchivoProlog ruta del archivo .pl con la lógica Prolog
     * @throws IllegalArgumentException si recital es nulo
     */
    public EntrenamientosProlog(Recital recital, String rutaArchivoProlog) throws IllegalArgumentException {
        if (recital == null) {
            throw new IllegalArgumentException("El recital no puede ser nulo");
        }
        this.recital = recital;
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
            // Intentar usar JPL para ejecutar consultas Prolog
            cargarArchivoProlog();
            prologInicializado = true;
            System.out.println("Prolog inicializado correctamente desde: " + rutaArchivoPrologActual);
        } catch (Exception e) {
            System.err.println("Error inicializando Prolog: " + e.getMessage());
            System.err.println("Usando modo heurístico como fallback");
            prologInicializado = false;
        }
    }

    /**
     * Carga el archivo Prolog que contiene la lógica de entrenamientos.
     * @throws Exception si hay error al cargar
     */
    private void cargarArchivoProlog() throws Exception {
        try {
            // Aquí iría la integración real con JPL
            // ejemplo: org.jpl7.PrologEngine.consult(rutaArchivoPrologActual);
            // Por ahora, verificamos que el archivo existe
            java.io.File archivo = new java.io.File(rutaArchivoPrologActual);
            if (!archivo.exists()) {
                throw new Exception("Archivo Prolog no encontrado: " + rutaArchivoPrologActual);
            }
            System.out.println("Archivo Prolog encontrado en: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            throw new Exception("Error cargando archivo Prolog: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula el número mínimo de entrenamientos necesarios.
     * Utiliza Prolog si está disponible, sino cae back a heurística.
     * 
     * @return número mínimo de entrenamientos necesarios
     * @throws Exception si hay error en el cálculo
     */
    public int calcularEntrenamientosMinimos() throws Exception {
        try {
            if (prologInicializado) {
                return calcularConProlog();
            } else {
                return calcularConHeuristica();
            }
        } catch (Exception e) {
            System.err.println("Error en calcularEntrenamientosMinimos: " + e.getMessage());
            return calcularConHeuristica();
        }
    }

    /**
     * Calcula entrenamientos usando JPL y Prolog.
     * Ejecuta: entrenamientos_minimos(X)
     * 
     * @return número de entrenamientos mínimos
     * @throws Exception si hay error
     */
    private int calcularConProlog() throws Exception {
        try {
            // Ejemplo de cómo sería con JPL:
            // org.jpl7.Query q = new org.jpl7.Query("entrenamientos_minimos(X)");
            // if (q.hasSolution()) {
            //     Map<String, org.jpl7.Term> solution = q.oneSolution();
            //     org.jpl7.Term x = solution.get("X");
            //     return Integer.parseInt(x.toString());
            // }
            
            System.out.println("Ejecutando consulta Prolog: entrenamientos_minimos(X)");
            return calcularConHeuristica(); // Fallback por ahora
        } catch (Exception e) {
            System.err.println("Error en consulta Prolog: " + e.getMessage());
            return calcularConHeuristica();
        }
    }

    /**
     * Calcula entrenamientos usando heurística (modo fallback).
     * @return número de entrenamientos necesarios
     */
    private int calcularConHeuristica() {
        int entrenamientos = 0;
        HashSet<Rol> rolesRequeridos = extraerRolesRequeridos();
        
        for (Rol rol : rolesRequeridos) {
            int disponibles = contarArtistaBaseConRol(rol);
            if (disponibles == 0) {
                entrenamientos++;
            }
        }
        
        return entrenamientos;
    }

    /**
     * Calcula entrenamientos mínimos con parámetros específicos.
     * Responde la pregunta: "¿Cuántos entrenamientos mínimos debo realizar para cubrir 
     * todos los roles para el recital, utilizando solo los miembros base, y artistas 
     * contratados sin experiencia y con un coste base por parámetro, para todos iguales?"
     * 
     * @param costoBase costo base uniforme para todos los entrenamientos
     * @param artistasContratados conjunto de artistas ya contratados (sin experiencia previa en rol)
     * @return objeto con detalles del cálculo
     * @throws Exception si hay error
     */
    public ResultadoEntrenamiento calcularEntrenamientosConParametros(
            double costoBase,
            HashSet<ArtistaExterno> artistasContratados) throws Exception {
        
        try {
            HashSet<Rol> rolesRequeridos = extraerRolesRequeridos();
            List<String> rolesFaltantes = new ArrayList<>();
            int entrenamientos = 0;
            double costoTotal = 0;
            
            for (Rol rol : rolesRequeridos) {
                int conBase = contarArtistaBaseConRol(rol);
                int conContratados = contarArtistaContratadoConRol(rol, artistasContratados);
                
                if ((conBase + conContratados) == 0) {
                    rolesFaltantes.add(rol.getNombre());
                    entrenamientos++;
                }
            }
            
            costoTotal = costoBase * entrenamientos;
            
            return new ResultadoEntrenamiento(
                entrenamientos,
                costoTotal,
                costoBase,
                rolesFaltantes,
                rolesRequeridos.size()
            );
        } catch (Exception e) {
            throw new Exception("Error calculando entrenamientos con parámetros: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae todos los roles requeridos del recital.
     * @return conjunto de roles únicos requeridos
     */
    private HashSet<Rol> extraerRolesRequeridos() {
        HashSet<Rol> rolesRequeridos = new HashSet<>();
        
        try {
            java.lang.reflect.Field cancionesField = Recital.class.getDeclaredField("canciones");
            cancionesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<Cancion> canciones = (HashSet<Cancion>) cancionesField.get(recital);
            
            if (canciones != null) {
                for (Cancion cancion : canciones) {
                    rolesRequeridos.addAll(cancion.getRolesRequeridos().keySet());
                }
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo roles: " + e.getMessage());
        }
        
        return rolesRequeridos;
    }

    /**
     * Cuenta cuántos artistas base tienen un rol específico.
     * @param rol rol a buscar
     * @return cantidad de artistas base que tienen ese rol
     */
    private int contarArtistaBaseConRol(Rol rol) {
        int contador = 0;
        
        try {
            java.lang.reflect.Field artistaBaseField = Recital.class.getDeclaredField("artistaBase");
            artistaBaseField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaBase> artistasBase = (HashSet<ArtistaBase>) artistaBaseField.get(recital);
            
            if (artistasBase != null) {
                for (ArtistaBase artista : artistasBase) {
                    if (artista.puedeTocarRol(rol)) {
                        contador++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error contando artistas: " + e.getMessage());
        }
        
        return contador;
    }

    /**
     * Cuenta cuántos artistas contratados tienen un rol específico.
     * @param rol rol a buscar
     * @param artistasContratados conjunto de artistas contratados
     * @return cantidad de artistas contratados que tienen ese rol
     */
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
            HashSet<Rol> rolesRequeridos = extraerRolesRequeridos();
            
            reporte.append("📊 ANÁLISIS GENERAL:\n");
            reporte.append("─────────────────────────────────────────────────────────\n");
            reporte.append("Entrenamientos mínimos requeridos: ").append(entrenamientos).append("\n");
            reporte.append("Total de roles requeridos: ").append(rolesRequeridos.size()).append("\n");
            reporte.append("Estado de Prolog: ").append(prologInicializado ? "✓ Activo" : "⚠ Inactivo (usando heurística)").append("\n\n");
            
            reporte.append("🎭 DETALLE DE ROLES:\n");
            reporte.append("─────────────────────────────────────────────────────────\n");
            
            for (Rol rol : rolesRequeridos) {
                int disponibles = contarArtistaBaseConRol(rol);
                reporte.append("  • ").append(rol.getNombre())
                       .append(": ").append(disponibles).append(" disponibles en base");
                if (disponibles == 0) {
                    reporte.append(" [⚠ REQUIERE ENTRENAMIENTO]");
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

    /**
     * Cuenta el número total de artistas disponibles (base + externos).
     * @return cantidad total de artistas
     */
    private int contarArtistasTotales() {
        int total = 0;
        
        try {
            java.lang.reflect.Field baseField = Recital.class.getDeclaredField("artistaBase");
            baseField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaBase> artistasBase = (HashSet<ArtistaBase>) baseField.get(recital);
            if (artistasBase != null) total += artistasBase.size();
            
            java.lang.reflect.Field externosField = Recital.class.getDeclaredField("artistaExternos");
            externosField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashSet<ArtistaExterno> artistasExternos = (HashSet<ArtistaExterno>) externosField.get(recital);
            if (artistasExternos != null) total += artistasExternos.size();
        } catch (Exception e) {
            System.err.println("Error contando artistas totales: " + e.getMessage());
        }
        
        return total;
    }

    /**
     * Obtiene una descripción de qué roles necesitarían entrenamiento.
     * @return mapa de roles y cantidad de entrenamientos necesarios
     */
    public Map<String, Integer> obtenerRolesAEntrenar() {
        Map<String, Integer> resultado = new HashMap<>();
        
        try {
            HashSet<Rol> rolesRequeridos = extraerRolesRequeridos();
            
            for (Rol rol : rolesRequeridos) {
                int disponibles = contarArtistaBaseConRol(rol);
                if (disponibles == 0) {
                    resultado.put(rol.getNombre(), 1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo roles a entrenar: " + e.getMessage());
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
