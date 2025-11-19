package Artista;

import java.util.HashSet;

import Recital.Banda;
import Recital.Rol;

public class ArtistaBase extends Artista {
    
    public ArtistaBase(String nombre, int maxcanciones, double costo, HashSet<Rol> roles, HashSet<Banda> bandaHistorico) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo, roles, bandaHistorico);
    }

    @Override
    public Boolean puedeSerEntrenado() {
        return false; // Los artistas base NO pueden ser entrenados
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArtistaBase otro = (ArtistaBase) obj;
        return nombre.equals(otro.nombre);
    }
}
