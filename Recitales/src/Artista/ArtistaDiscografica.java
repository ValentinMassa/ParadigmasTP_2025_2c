package Artista;



public class ArtistaDiscografica extends Artista {
    
    public ArtistaDiscografica(String nombre, int maxcanciones, double costo) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo);
    }

    @Override
    public Boolean puedeSerEntrenado() {
        return false; // Los artistas base NO pueden ser entrenados
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArtistaDiscografica otro = (ArtistaDiscografica) obj;
        return nombre.equals(otro.nombre);
    }
}
