package Recital;

import java.util.HashSet;
import java.util.List;

import Recital.Artista.*;
import Recital.Contratos.*;
import Repositorios.RepositorioArtistas;
import Servicios.ServicioContratacion;

import java.util.Map;

import Artista.Artista;

public class Recital {
    private HashSet<Cancion> canciones;
    
    public Recital(HashSet<Cancion> c) throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException("El conjunto de canciones no puede ser nulo");
        }
        canciones = new HashSet<>();
        canciones.addAll(c);
    }

    public HashSet<Cancion> getCanciones(){
        return new HashSet<>(canciones);
    }

}
