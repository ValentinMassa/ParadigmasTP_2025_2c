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

    public Cancion getCancionPorNombre(String nombre) {
        for (Cancion c : recital.getCanciones()) {
            if (c.getTitulo().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }

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
    public List<ArtistaDiscografica> getArtistasBase(){
        HashSet<ArtistaDiscografica> a = repositorioArtistas.getArtistasDiscografica();
        List<ArtistaDiscografica> artistas = new ArrayList<>();
        artistas.addAll(a);
        return artistas;
    }

    /*
    * Calcula los roles faltantes para todas las canciones en el recital, considerando los contratos existentes
    * y posibles contratos con artistas base.
    * @param servicioC El servicio de contratación que proporciona los contratos existentes.
    * @return Un HashMap que mapea cada canción a otro HashMap de roles y sus cantidades faltantes.
     */
    private void restarRolDeCancion(HashMap<Cancion, HashMap<Rol, Integer>> roles, Cancion cancion, Rol rol) {
        HashMap<Rol, Integer> rolesFaltantes = roles.get(cancion);
        if (rolesFaltantes != null && rolesFaltantes.containsKey(rol)) {
            rolesFaltantes.put(rol, Math.max(0, rolesFaltantes.get(rol) - 1));
            
        }
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
    public HashMap<Cancion, HashMap<Rol, Integer>> calcularRolesFaltantesTodasLasCanciones(ServicioContratacion servicioC){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = getRolesDeTodasLasCanciones();
        
        for (Contrato contrato : servicioC.getContratos()) {
            restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
        }

        for(ArtistaDiscografica a : getArtistasBase()) {
            List<Contrato> c = servicioC.posiblesContratos(a, resultado);
            for(Contrato contrato : c) {
                System.out.println("Asignando contrato: Artista " + contrato.getArtista().getNombre() + ", Cancion " + contrato.getCancion().getTitulo() + ", Rol " + contrato.getRol().getNombre());
                restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
            }
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
    public HashSet<Artista> getArtistasContratados(ServicioContratacion sc){
        HashSet<Artista> artistasContratados = new HashSet<>();

        for (Contrato contrato : sc.getContratos()) {
            artistasContratados.add(contrato.getArtista());
        }

        return artistasContratados;
    }
}




        //Freedy Mercury | Voz, Piano | max= 3 canciones

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

    
