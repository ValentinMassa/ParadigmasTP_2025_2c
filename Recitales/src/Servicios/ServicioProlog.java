package Servicios;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import Artista.ArtistaDiscografica;
import Recital.Cancion;
import Recital.Rol;

import org.jpl7.Atom;
import org.jpl7.JPL;
import org.jpl7.Query;
import org.jpl7.Term;

public class ServicioProlog {
    private ServicioConsulta servicioConsulta;
    private ServicioContratacion servC;
    private final String RUTA_PROLOG = System.getProperty("user.dir") + File.separator + "Prolog" + File.separator + "entrenamientos.pl";

    public static class ResultadoEntrenamiento {
        private int entrenamientosMinimos;
        private double costoTotal;
        private int rolesRequeridosTotales;
        private List<String> rolesFaltantes;
        private Map<Rol, Integer> rolesFaltantesPorRol;
        private Map<Cancion, Map<Rol, Integer>> rolesFaltantesPorCancion;

        public ResultadoEntrenamiento(int entrenamientosMinimos, double costoTotal, int rolesRequeridosTotales, List<String> rolesFaltantes, Map<Rol, Integer> rolesFaltantesPorRol, Map<Cancion, Map<Rol, Integer>> rolesFaltantesPorCancion) {
            this.entrenamientosMinimos = entrenamientosMinimos;
            this.costoTotal = costoTotal;
            this.rolesRequeridosTotales = rolesRequeridosTotales;
            this.rolesFaltantes = rolesFaltantes;
            this.rolesFaltantesPorRol = rolesFaltantesPorRol;
            this.rolesFaltantesPorCancion = rolesFaltantesPorCancion;
        }

        public int getEntrenamientosMinimos() { return entrenamientosMinimos; }
        public double getCostoTotal() { return costoTotal; }
        public int getRolesRequeridosTotales() { return rolesRequeridosTotales; }
        public List<String> getRolesFaltantes() { return rolesFaltantes; }
        public Map<Rol, Integer> getRolesFaltantesPorRol() { return rolesFaltantesPorRol; }
        public Map<Cancion, Map<Rol, Integer>> getRolesFaltantesPorCancion() { return rolesFaltantesPorCancion; }
    }

    public ServicioProlog(ServicioConsulta servicioConsulta, ServicioContratacion servC) {
        if(servicioConsulta == null || servC == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }   
        this.servicioConsulta = servicioConsulta;
        this.servC = servC;
        JPL.init();
    }

    public ResultadoEntrenamiento calcularEntrenamientosConParametros(double costoBase) {
        // Paso 1: Calcular roles requeridos totales
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        Map<Cancion, Map<Rol, Integer>> rolesFaltantesPorCancion = (Map<Cancion, Map<Rol, Integer>>) (Map) servicioConsulta.rolesFaltantesIncluyendoArtistasDisc(servC);

        int totalRolesRequeridos = 0;
        for(Map<Rol, Integer> rolesCancion : rolesFaltantesPorCancion.values()){
            for(Map.Entry<Rol, Integer> entry : rolesCancion.entrySet()){
                Rol rol = entry.getKey();
                Integer cantidad = entry.getValue();
                rolesRequeridos.put(rol, rolesRequeridos.getOrDefault(rol, 0) + cantidad);
                totalRolesRequeridos += cantidad;
            }
        } 

        // Paso 2: Obtener artistas base
        List<ArtistaDiscografica> artistasBase = obtenerArtistasBase();
        
        // Paso 3: Preparar y asentar hechos en Prolog
        prepararHechosProlog(rolesRequeridos, artistasBase);
        
        // Paso 4: Consultar min_trainings y calcular costo
        return consultarPrologYCosto(costoBase, totalRolesRequeridos, rolesRequeridos, rolesFaltantesPorCancion);
    }

    private List<ArtistaDiscografica> obtenerArtistasBase() {
        return servicioConsulta.getArtistasDiscografica();
    }

    private void prepararHechosProlog(Map<Rol, Integer> rolesRequeridos, List<ArtistaDiscografica> artistasBase) {
        boolean cargado = false;
        try {
            // Consultar el archivo Prolog
            Query q = new Query("consult", new Term[] {new Atom(RUTA_PROLOG)});
            if (q.hasSolution()) {
                cargado = true;
            }
        } catch (org.jpl7.PrologException | UnsatisfiedLinkError e) {
            System.err.println("Excepcion al cargar Prolog: " + e.getMessage());
        }
        
        if (!cargado) {
            System.err.println("Error: No se pudo cargar el archivo " + RUTA_PROLOG);
            // Intentar ruta alternativa por si se ejecuta desde dentro de Recitales
            try {
                String rutaAlternativa = "Prolog" + File.separator + "entrenamientos.pl";
                Query q = new Query("consult", new Term[] {new Atom(rutaAlternativa)});
                if (q.hasSolution()) {
                    cargado = true;
                }
            } catch (org.jpl7.PrologException | UnsatisfiedLinkError e) {
                 System.err.println("Tampoco se pudo cargar desde ruta alternativa");
            }
        }

        // Limpiar hechos anteriores
        try {
            new Query("retractall(rol_requerido(_,_))").hasSolution();
            new Query("retractall(base_tiene_rol(_,_))").hasSolution();
        } catch (Exception e) {
            System.err.println("Error al limpiar hechos Prolog: " + e.getMessage());
        }

        // Asentar roles requeridos
        for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
            String rol = entry.getKey().getNombre().toLowerCase();
            int cantidad = entry.getValue();
            new Query("assert(rol_requerido('" + rol + "', " + cantidad + "))").hasSolution();
        }

        // Asentar roles de base (considerando capacidad disponible)
        // No se asientan base_tiene_rol porque los base ya están asignados para cubrir los roles cubiertos
        // La cobertura base es 0 para los roles faltantes
    }

    private ResultadoEntrenamiento consultarPrologYCosto(double costoBase, int totalRolesRequeridos, Map<Rol, Integer> rolesRequeridos, Map<Cancion, Map<Rol, Integer>> rolesFaltantesPorCancion) {
        // Consultar min_trainings en Prolog
        Query q = new Query("min_trainings", new Term[] {new org.jpl7.Variable("Min")});
        int minTrainings = 0;
        if (q.hasSolution()) {
            Map<String, Term> solution = q.oneSolution();
            minTrainings = solution.get("Min").intValue();
        } else {
            System.err.println("Error: No se pudo consultar min_trainings en Prolog");
            minTrainings = totalRolesRequeridos; // fallback
        }

        // Obtener roles faltantes
        List<String> rolesFaltantesList = new ArrayList<>();
        Query q2 = new Query("rol_faltante", new Term[] {new org.jpl7.Variable("Rol")});
        while (q2.hasNext()) {
            Map<String, Term> solution = q2.nextSolution();
            String rol = solution.get("Rol").name();
            rolesFaltantesList.add(rol);
        }

        double costoTotal = minTrainings * costoBase;
        return new ResultadoEntrenamiento(minTrainings, costoTotal, totalRolesRequeridos, rolesFaltantesList, rolesRequeridos, rolesFaltantesPorCancion);
    }
}