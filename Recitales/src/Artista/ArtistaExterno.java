package Artista;


public class ArtistaExterno extends Artista {
    
    public ArtistaExterno(String nombre, int maxcanciones, double costo) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo);
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
