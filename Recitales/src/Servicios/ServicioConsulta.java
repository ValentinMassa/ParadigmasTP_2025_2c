package Servicios;

import Repositorios.*;
import Recital.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import Artista.Artista;
import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Repositorios.BandaCatalogoMemory;



public class ServicioConsulta {
    
    private RepositorioArtistasMemory repositorioArtistas;
    private BandaCatalogoMemory bandas;
    private Recital recital;
    private RolCatalogoMemory rolCatalogo;
    
    
    public ServicioConsulta(RepositorioArtistasMemory rA, Recital recital, 
        RolCatalogoMemory rolCatalogo, BandaCatalogoMemory bandas) 
            throws IllegalArgumentException {
        if (rA == null|| recital == null || rolCatalogo == null || bandas == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.repositorioArtistas = rA;
        this.recital = recital;
        this.rolCatalogo = rolCatalogo;
        this.bandas = bandas;
    }

    /* 
    * Obtiene una canción por su nombre.
    * @param nombre El nombre de la canción a buscar.
    * @return La canción encontrada o null si no existe.
    */
    public Cancion getCancionPorNombre(String nombre) {
        for (Cancion c : recital.getCanciones()) {
            if (c.getTitulo().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }

    /*
    * Obtiene los roles requeridos para una canción dada.
    * @param cancion La canción para la cual se desean obtener los roles.
    * @return Un HashMap que mapea cada rol a la cantidad requerida para la canción.
    */
    public HashMap<Rol, Integer> getRolesDeCancion(Cancion cancion) {
        return new HashMap<>(cancion.getRolesRequeridos());
    }

    /*
    * Calcula los roles faltantes para una canción dada, considerando los contratos existentes.
    * @param cancion La canción para la cual se desean calcular los roles faltantes.
    * @param contratos La lista de contratos existentes para la canción.
    * @return Un HashMap que mapea cada rol a la cantidad faltante para la canción.
     */
    public HashMap<Rol, Integer> calcularRolesFaltantes(Cancion cancion, List<Contrato> contratos) {
        return cancion.getRolesFaltantes(contratos);
    }

    /*
    * Obtiene los roles requeridos para todas las canciones en el recital.
    * @return Un HashMap que mapea cada canción a otro HashMap de roles y sus cantidades requeridas.
     */
    public HashMap<Cancion, HashMap<Rol, Integer>> getRolesDeTodasLasCanciones(){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = new HashMap<>();
        for (Cancion cancion : recital.getCanciones())   
            resultado.put(cancion, new HashMap<>(cancion.getRolesRequeridos()));
        return resultado;
    }

    /*
    * Obtiene la lista de artistas base desde el repositorio.
    * @return Una lista de artistas base.
    */
    public List<ArtistaDiscografica> getArtistasDiscografica(){
        HashSet<ArtistaDiscografica> a = repositorioArtistas.getArtistasDiscografica();
        List<ArtistaDiscografica> artistas = new ArrayList<>();
        artistas.addAll(a);
        return artistas;
    }


    /* 
    * Obtiene la lista de artistas externos desde el repositorio.
    * @return Una lista de artistas externos.
    */
    public List<ArtistaExterno> getArtistasExternos(){
        HashSet<ArtistaExterno> a = repositorioArtistas.getArtistasExternos();
        List<ArtistaExterno> artistas = new ArrayList<>();
        artistas.addAll(a);
        return artistas;
    }


    private void restarRolDeCancion(HashMap<Cancion, HashMap<Rol, Integer>> roles, Cancion cancion, Rol rol) {
        HashMap<Rol, Integer> rolesFaltantes = roles.get(cancion);
        if (rolesFaltantes != null && rolesFaltantes.containsKey(rol)) {
            rolesFaltantes.put(rol, Math.max(0, rolesFaltantes.get(rol) - 1));            
        }
        roles.put(cancion, rolesFaltantes);
    }

    /*
    * Obtiene el repositorio de artistas.
    * @return El repositorio de artistas.
    */
    public RepositorioArtistasMemory getRepositorioArtistas() {
        return this.repositorioArtistas;
    }

    /*
    * Calcula los roles faltantes para todas las canciones en el recital, considerando los contratos existentes
    * y posibles contratos con artistas base.
    * @param servicioC El servicio de contratación que proporciona los contratos existentes.
    * @return Un HashMap que mapea cada canción a otro HashMap de roles y sus cantidades faltantes.
    */
    public HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantesIncluyendoArtistasDisc(ServicioContratacion servicioC){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = getRolesDeTodasLasCanciones();
        
        /// Por cada contrato existente, restar el rol contratado
        for (Contrato contrato : servicioC.getContratos()) {
            restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
        }

        for(ArtistaDiscografica a : getArtistasDiscografica()) {
            List<Contrato> c = servicioC.posiblesContratos(a, resultado);
            for(Contrato contrato : c) {
                System.out.println("Cubriendo con artista base: Artista " + contrato.getArtista().getNombre() + ", Cancion " + contrato.getCancion().getTitulo() + ", Rol " + contrato.getRol().getNombre());
                restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
            }
        }
        return resultado;
    }

    public HashMap<Cancion, HashMap<Rol, Integer>> calcularRolesFaltantes(ServicioContratacion servicioC){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = getRolesDeTodasLasCanciones();
        
        for (Contrato contrato : servicioC.getContratos()) {
            resultado.get(contrato.getCancion()).put(contrato.getRol(), 
                Math.max(0, resultado.get(contrato.getCancion()).get(contrato.getRol()) - 1));
        }
        return resultado;
    }


    /*
    * Obtiene la lista de artistas entrenables desde el repositorio.
    * @return Una lista de artistas entrenables. 
    */
    public HashSet<Artista> getArtistasEntrenables() {
        return repositorioArtistas.getArtistasEntrenables();
    }

    public HashSet<Rol> getRolesDeArtista(Artista artista){
        return repositorioArtistas.getRolesArtista(artista);
    }

    public HashSet<Rol> getTodosLosRoles(){
        return rolCatalogo.getTodosLosRoles();
    }

    public HashSet<Banda> getTodasLasBandas(){
        return bandas.getTodosLasBandas();
    }

    public Recital getRecital() {
        return this.recital;
    }
    
    /**
     * Actualiza todos los objetos del servicio con los datos de un snapshot cargado.
     * Este método reemplaza completamente el estado actual con el estado del snapshot.
     * 
     * @param nuevoRepositorio El nuevo repositorio de artistas
     * @param nuevoRecital El nuevo recital
     * @param nuevoRolCatalogo El nuevo catálogo de roles
     * @param nuevoBandaCatalogo El nuevo catálogo de bandas
     * @param nuevoServicioContratacion El nuevo servicio de contratación con los contratos
     */
    public void actualizarDesdeSnapshot(RepositorioArtistasMemory nuevoRepositorio,
                                       Recital nuevoRecital,
                                       RolCatalogoMemory nuevoRolCatalogo,
                                       BandaCatalogoMemory nuevoBandaCatalogo) {
        if (nuevoRepositorio == null || nuevoRecital == null || 
            nuevoRolCatalogo == null || nuevoBandaCatalogo == null) {
            throw new IllegalArgumentException("Ningún parámetro puede ser nulo al actualizar desde snapshot");
        }
        
        this.repositorioArtistas = nuevoRepositorio;
        this.recital = nuevoRecital;
        this.rolCatalogo = nuevoRolCatalogo;
        this.bandas = nuevoBandaCatalogo;
    }

    /*
    * Obtiene los artistas que tienen contratos vigentes.
    * @param sc El servicio de contratación que proporciona los contratos existentes.
    * @return Un conjunto de artistas que tienen contratos vigentes.
    */
    public HashSet<Artista> getArtistasContratados(ServicioContratacion sc){
        HashSet<Artista> artistasContratados = new HashSet<>(this.getArtistasExternos());

        for(ArtistaExterno a : this.getArtistasExternos()){
            if(!sc.tieneAlgunContrato(a)){
                artistasContratados.remove(a);
            }
        }
        return artistasContratados;
    }

    /*
    * Obtiene los artistas que no tienen contratos vigentes.
    * @param sc El servicio de contratación que proporciona los contratos existentes.
    * @return Un conjunto de artistas que no tienen contratos vigentes.
    */
    public HashSet<ArtistaExterno> getArtistasExternosContratados(ServicioContratacion sc){
        HashSet<ArtistaExterno> artistasContratados = new HashSet<>(this.getArtistasExternos());

        for(ArtistaExterno a : this.getArtistasExternos()){
            if(!sc.tieneAlgunContrato(a)){
                artistasContratados.remove(a);
            }
        }
        return artistasContratados;
    }


    /**
     * Calcula la cantidad total requerida de cada rol sumando los requerimientos de todas las canciones.
     * 
     * @return Un HashMap que mapea cada rol a la cantidad total requerida en todas las canciones del recital
     */
    public HashMap<Rol, Integer> calcularCantidadTotalRequeridaPorRol() {
        HashMap<Rol, Integer> totalPorRol = new HashMap<>();
        
        // Iterar sobre todas las canciones del recital
        for (Cancion cancion : recital.getCanciones()) {
            HashMap<Rol, Integer> rolesCancion = cancion.getRolesRequeridos();
            
            // Sumar los roles de esta canción al total
            for (Rol rol : rolesCancion.keySet()) {
                int cantidadRequerida = rolesCancion.get(rol);
                totalPorRol.put(rol, totalPorRol.getOrDefault(rol, 0) + cantidadRequerida);
            }
        }
        
        return totalPorRol;
    }

    /**
     * Genera una lista de candidatos por rol, considerando tanto artistas de la discográfica como externos.
     * Solo incluye artistas que pueden tocar el rol y tienen capacidad disponible.
     * 
     * @param necesidadTotal Mapa que indica la cantidad necesaria por cada rol
     * @return Un mapa que asocia cada rol con la lista de artistas candidatos que pueden tocarlo
     */
    public HashMap<Rol, List<Artista>> generarCandidatosPorRol(HashMap<Rol, Integer> necesidadTotal) {
        HashMap<Rol, List<Artista>> candidatosPorRol = new HashMap<>();

        for (Rol rol : necesidadTotal.keySet()) {
            List<Artista> lista = new ArrayList<>();

            // ----- 1. Artistas de la discográfica -----
            for (ArtistaDiscografica a : repositorioArtistas.getArtistasDiscografica()) {
                if (a.puedeTocarRol(rol)) {
                    int capacidad = a.getMaxCanciones() - a.getCantCancionesAsignadas();
                    if (capacidad > 0) {
                        lista.add(a);
                    }
                }
            }

            // ----- 2. Artistas externos -----
            for (ArtistaExterno a : repositorioArtistas.getArtistasExternos()) {
                if (a.puedeTocarRol(rol)) {
                    int capacidad = a.getMaxCanciones() - a.getCantCancionesAsignadas();
                    if (capacidad > 0) {
                        lista.add(a);
                    }
                }
            }

            candidatosPorRol.put(rol, lista);
        }

        return candidatosPorRol;
    }

    /*
    * Obtiene los artistas externos sin experiencia (no entrenados) que no tienen contratos vigentes.
    * @param sc El servicio de contratación que proporciona los contratos existentes.
    * @return Un conjunto de artistas externos sin experiencia y sin contratos vigentes.
    */

    public List<ArtistaExterno> getArtistasExternosSinExperiencia(ServicioContratacion servC){
        HashSet<ArtistaExterno> artistasSinExperiencia = getArtistasExternosContratados(servC);
        for(ArtistaExterno a : this.getArtistasExternos()){
            if(a.fueEntrenado()){
                artistasSinExperiencia.remove(a);
            }
        }
        return new ArrayList<>(artistasSinExperiencia);

    }

    /**
     * Obtiene una lista de relaciones entre artistas y bandas basadas en colaboraciones en canciones.
     * Cada relación se representa como una cadena en formato "Artista ↔ Banda por Canción".
     * Una colaboración ocurre cuando un artista toca en una canción con todos los miembros de una banda.
     * 
     * @param sc El servicio de contratación para obtener los contratos.
     * @return Una lista de cadenas representando las relaciones entre artistas y bandas.
     */
    public List<String> getRelacionesArtistas(ServicioContratacion sc) {
        List<String> relaciones = new ArrayList<>();
        List<Contrato> contratos = sc.getContratos();
        
        // Agrupar contratos por canción
        HashMap<Cancion, List<Contrato>> contratosPorCancion = new HashMap<>();
        for (Contrato contrato : contratos) {
            contratosPorCancion.computeIfAbsent(contrato.getCancion(), k -> new ArrayList<>()).add(contrato);
        }
        
        // Para cada banda
        for (Banda banda : bandas.getTodosLasBandas()) {
            // Obtener miembros de la banda
            List<Artista> miembrosBanda = new ArrayList<>();
            for (Artista artista : getArtistasDiscografica()) {
                if (artista.getBandas().contains(banda)) {
                    miembrosBanda.add(artista);
                }
            }
            for (Artista artista : getArtistasExternos()) {
                if (artista.getBandas().contains(banda)) {
                    miembrosBanda.add(artista);
                }
            }
            
            // Para cada canción
            for (Cancion cancion : contratosPorCancion.keySet()) {
                List<Contrato> contratosCancion = contratosPorCancion.get(cancion);
                HashSet<Artista> artistasContratados = new HashSet<>();
                for (Contrato c : contratosCancion) {
                    artistasContratados.add(c.getArtista());
                }
                
                // Verificar si todos los miembros de la banda están contratados
                boolean todosMiembrosContratados = true;
                for (Artista miembro : miembrosBanda) {
                    if (!artistasContratados.contains(miembro)) {
                        todosMiembrosContratados = false;
                        break;
                    }
                }
                
                if (todosMiembrosContratados) {
                    // Los otros artistas contratados colaboran con la banda
                    for (Artista artista : artistasContratados) {
                        if (!miembrosBanda.contains(artista)) {
                            relaciones.add(artista.getNombre() + " <-> " + banda.getNombre() + " por " + cancion.getTitulo());
                        }
                    }
                }
            }
        }
        
        return relaciones;
    }


    public List<Artista> getArtistasPorRol(Rol rol) {
        List<Artista> artistasPorRol = new ArrayList<>();

        for (ArtistaDiscografica a : repositorioArtistas.getArtistasDiscografica()) {
            if (a.puedeTocarRol(rol)) {
                artistasPorRol.add(a);
            }
        }

        for (ArtistaExterno a : repositorioArtistas.getArtistasExternos()) {
            if (a.puedeTocarRol(rol)) {
                artistasPorRol.add(a);
            }
        }

        return artistasPorRol;
    }
 }

        //I love You | Voz = 0, Piano = 1

        // Por cada artista se itera por cada cancion y se reduce el rol contratado
        // Primero: restamos contratos
        // hashmap: Artista,(maximo-actual)
        // hasmap: HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes
        // para cada artista 
        //    para cada cancion
        //        buscar contrato del artista en la cancion 
        //        si no hay: 
        //            buscar primer rol positivo y restas rol requerido y restas del maximo
        //  
        // Freddy, pedro, raul
        //  a,b,c
        // posibleC(ArtistaList, HashMap<Cancion, HashMap<Rol, Integer>>, Maximo(maximo-actual))
    
        //  restar a HashMap<Cancion, HashMap<Rol, Integer>> roles de contratos ya hechos
        //  for(Artista a : repositorioArtistas.getTodosLosArtistas())
        //  {
        //  posibleCUnitaria(a, HashMap<Cancion, HashMap<Rol, Integer>>): contratosout
        //  con los contratos se itera sobre hashmap y se reduce el rol contratado
        // sumar contratosout a contratos
        //  }

    
