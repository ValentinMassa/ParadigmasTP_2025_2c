package Recital;

import java.util.HashSet;
import java.util.List;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.Rol;
import java.util.Map;

public class Recital {
    @SuppressWarnings("unused")
	private RepositorioArtistas repositorioArtistas;
    private HashSet<Cancion> canciones;
    private List<Contrato> contratos;
    /// private ServicioContratacion servicioContratacion;
    public Recital(RepositorioArtistas repositorioArtistas,
                   HashSet<Cancion> canciones, ServicioContratacion servicioContratacion)
                   throws IllegalArgumentException {
        if (repositorioArtistas == null || canciones == null || servicioContratacion == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        } 
        this.repositorioArtistas = repositorioArtistas;
        this.canciones = new HashSet<Cancion>();
        this.canciones.addAll(canciones);
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

    public Map<Cancion, Double> getCostosPorCancion(){
        //Falta Desarrollar
        return null;
    }

}
