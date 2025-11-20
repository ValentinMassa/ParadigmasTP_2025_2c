package Recital;

import java.util.HashSet;

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
