package Servicios;

import java.util.*;
import Artista.*;
import Recital.*;
import Repositorios.RepositorioArtistasMemory;

public class ServicioContratacion {
    private List<Contrato> contratos;
    private final double descuento_banda = 0.5;
    
     public ServicioContratacion() {
        this.contratos = new ArrayList<Contrato>();
    }
    
    /* 
        * Contrata artistas para una canción específica, priorizando artistas de discográficas y luego artistas externos para roles faltantes.
        * @param cancion La canción para la cual se desean contratar artistas.
        * @param repo El repositorio de artistas que contiene tanto artistas de discográficas como externos
        * @return void
    */
    public void contratarArtistasParaCancion(Cancion cancion, RepositorioArtistasMemory repo) {     
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(this.getContratosPorCancion(cancion));
        
        if (!hayRolesFaltantes(rolesFaltantes)) {
            System.out.println("No hay roles faltantes para la canción " + cancion.getTitulo());
            return;
        }

        for(ArtistaDiscografica artista : repo.getArtistasDiscografica()) {
            Contrato contrato = posibleContrato(artista, cancion, rolesFaltantes);
            if (contrato != null) {
                contratos.add(contrato);
                Rol rolTomado = contrato.getRol();
                rolesFaltantes.put(rolTomado, rolesFaltantes.get(rolTomado) - 1);
                artista.set_CantCancionesAsignado(artista.getCantCancionesAsignadas() + 1);
            }
        }
        // Contratación para roles faltantes con artistas externos
        for (Rol rol : rolesFaltantes.keySet()) {
            while (rolesFaltantes.get(rol) > 0) {

                ArtistaExterno mejorExterno = null;
                double menorCosto = Double.MAX_VALUE;

                for (ArtistaExterno externo : repo.getArtistasExternos()) {
                    if (!tieneContratoConCancion(externo, cancion) && externo.puedeTocarRol(rol)) {
                        double costo = obtenerCostoExterno(externo, cancion);
                        if (costo < menorCosto) {
                            menorCosto = costo;
                            mejorExterno = externo;
                        }
                    }
                }
                // Si encontramos uno, lo contratamos
                if (mejorExterno != null) {
                    Contrato contrato = new Contrato(cancion, rol, mejorExterno, menorCosto);
                    contratos.add(contrato);
                    // actualizar roles faltantes
                    rolesFaltantes.put(rol, rolesFaltantes.get(rol) - 1);
                } else {
                    // No hay artistas externos disponibles para este rol
                    break;
                }
            
            }
        }
    }

    private boolean hayRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes) {
        for (Integer cantidad : rolesFaltantes.values()) {
            if (cantidad > 0) return true;
        }
        return false;
    }
    

    private Contrato posibleContrato(ArtistaDiscografica artista,
                                Cancion cancion,
                                Map<Rol, Integer> rolesFaltantes) {

        if (tieneContratoConCancion(artista, cancion))
            return null;

        for (Rol rol : rolesFaltantes.keySet()) {
            if (rolesFaltantes.get(rol) > 0 && artista.puedeTocarRol(rol)) {
                return new Contrato(cancion, rol, artista, artista.getCosto());
            }
        }
        return null;
    }
    
    public List<Contrato> posiblesContratos(ArtistaDiscografica artista,
                                        HashMap<Cancion, HashMap<Rol, Integer>> cancionesRoles) {

        List<Contrato> lista = new ArrayList<>();
        int cupo = artista.getMaxCanciones() - artista.getCantCancionesAsignadas();

        for (Cancion c : cancionesRoles.keySet()) {
            if (cupo <= 0) break;

            Contrato posible = posibleContrato(artista, c, cancionesRoles.get(c));

            if (posible != null) {
                lista.add(posible);
                cupo--;
            }
        }

        return lista;
    }


    public List<Contrato> contratarParaTodo(Recital recital){
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
    }
    
    public List<Contrato> getContratosPorCancion(Cancion cancion) {
        List<Contrato> contratosDeCancion = new ArrayList<Contrato>();

        for(Contrato contrato : contratos) {
            if (contrato.getCancion().equals(cancion)) {
                contratosDeCancion.add(contrato);
            }
        }
        return contratosDeCancion;
    }


    public List<Contrato> getContratos() {
        return contratos;
    }

    private Boolean tieneContratoConCancion(Artista a, Cancion c) {
        for (Contrato contrato : contratos) {
            if (contrato.getArtista().equals(a) && contrato.getCancion().equals(c)) {
                return true;
            }
        }
        return false;
    }

    /*
    * Calcula el costo de contratar a un artista externo para una canción, aplicando descuentos si corresponde.
    * @param externo El artista externo a contratar.
    * @param c La canción para la cual se está considerando la contratación.
    * @return El costo total de la contratación, considerando descuentos por bandas compartidas.
    */

    private double obtenerCostoExterno(ArtistaExterno externo, Cancion c) {
        HashSet<ArtistaDiscografica> artistasBaseContratados = new HashSet<>();
        double costo = externo.getCosto();
        // Obtengo artistas base contratados para la cancion
        for(Contrato contrato : contratos){
            if(contrato.getCancion().equals(c) && contrato.getArtista() instanceof ArtistaDiscografica){
                artistasBaseContratados.add((ArtistaDiscografica) contrato.getArtista());
            }
        }
        // Busco coincidencia en bandas entre Artista Externo y Artistas Base
        boolean yaAplicoDescuento = false;
        for(ArtistaDiscografica artistaBase : artistasBaseContratados){
            for(Banda banda : artistaBase.getBandas()){
                if(yaAplicoDescuento) break;
                if(externo.getBandas().contains(banda)){
                    costo *= descuento_banda;
                    yaAplicoDescuento = true;
                    break;
                }
            }
        }
        return costo;
    }


}





    /*public List<Contrato> posiblesContratoArtista(ArtistaDiscografica a, HashMap<Cancion, HashMap<Rol, Integer>> c){
        
        List<Contrato> posiblesContratos = new ArrayList<Contrato>();
        int maxCanciones = a.getMaxCanciones() - a.getCantCancionesAsignadas(); 
        
        for(Cancion cancion : c.keySet()) {
            HashMap<Rol, Integer> roles = c.get(cancion);
            
            if(maxCanciones <= 0)
                break;

            for(Rol rol : roles.keySet()) {
                if(a.puedeTocarRol(rol) && roles.get(rol) > 0 && !tieneContratoConCancion(a, cancion)) {
                    Contrato contrato = new Contrato(cancion, rol, a, a.getCosto()); //costo)
                    posiblesContratos.add(contrato);
                    maxCanciones--;
                    break;
                }
            }
        }
        System.out.println("Posibles contratos para el artista " + a.getNombre() + ": " + posiblesContratos.size());
        return posiblesContratos;
    }*/