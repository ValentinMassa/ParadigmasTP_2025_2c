package Recital;

import java.util.HashSet;
import java.util.List;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.Rol;
import java.util.Map;

public class Recital {
    private HashSet<ArtistaBase> artistaBase;
    private HashSet<ArtistaExterno> artistaExternos;
    private HashSet<Cancion> canciones;
    private List<Contrato> contratos;
    private ServicioContratacion servicioContratacion;


    public Recital(HashSet<ArtistaBase> artistaBase, HashSet<ArtistaExterno> artistaExternos,
                   HashSet<Cancion> canciones, ServicioContratacion servicioContratacion)
                   throws IllegalArgumentException {
        if (artistaExternos == null || artistaBase == null || canciones == null || servicioContratacion == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        } 
        this.artistaBase = new HashSet<ArtistaBase>();
        this.artistaExternos = new HashSet<ArtistaExterno>();
        this.canciones = new HashSet<Cancion>();
        this.servicioContratacion = new ServicioContratacion();
        this.artistaBase.addAll(artistaBase);
        this.artistaExternos.addAll(artistaExternos);
        this.canciones.addAll(canciones);
        this.contratos = servicioContratacion.contratarParaTodo(this);
    }

    public Map<Rol, Integer> getRolesFaltantes(){
        //Falta Desarrollar
        return null;
    }

    public List<Contrato> getContratos(){
        return contratos;
    }

    public Map<Artista, Double> getCostosPorArtista(){
        //Falta Desarrollar
        return null;
    }

    public double getCostoTotalRecital(){
        //Falta Desarrollar
        return 0;
    }

    public HashSet<Cancion> getCancionesCompletas() {
        
        //Falta Desarrollar
        return null;
    }
    public HashSet<Cancion> getCancionesIncompletas() {
        
        //Falta Desarrollar
        return null;
    }

    public Map<Cancion, Double> getCostosPorCancion(){
        //Falta Desarrollar
        return null;
    }

}
