package Recital.Artista;

import java.util.HashSet;
import Recital.Banda.Banda;
import Recital.Rol.Rol;

public class ArtistaExterno extends Artista {
    
    public ArtistaExterno(String nombre, int maxcanciones, double costo, HashSet<Rol> roles, HashSet<Banda> bandaHistorico) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo, roles, bandaHistorico);
    }

    @Override
    public Boolean puedeSerEntrenado() {
        return true; // Los artistas externos pueden ser entrenados
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArtistaExterno otro = (ArtistaExterno) obj;
        return nombre.equals(otro.nombre);
    }
}
