package Recital;
import Artista.Artista;


public class Contrato {
    private Cancion cancion;
    private Rol rol;
    private Artista artista;
    private double costo;

    public Contrato(Cancion cancion, Rol rol, Artista artista, double costo) throws IllegalArgumentException {
        if (cancion == null || rol == null || artista == null || costo < 0) {
            throw new IllegalArgumentException("Los parametros no pueden ser nulos");        
        }
        this.cancion = cancion;
        this.rol = rol;
        this.artista = artista;
        this.costo = costo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!(obj instanceof Contrato)) return false;
        Contrato contrato = (Contrato) obj;
        return cancion.equals(contrato.cancion) &&
               rol.equals(contrato.rol) &&
               artista.equals(contrato.artista) && 
               Double.compare(contrato.costo, costo) == 0;
    }

    public double obtenerCostoContrato(){
        return costo;
    }
    public Cancion getCancion() {
        return cancion;
    }
    public Rol getRol() {
        return rol;
    }
    public Artista getArtista() {
        return artista;
    }
    public double getCosto() {
        return costo;
    }

    @Override
    public String toString() {
        return "+------------------------------------------+\n" +
               "|         >> CONTRATO MUSICAL <<           |\n" +
               "+------------------------------------------+\n" +
               String.format("| Cancion:  %-30s |\n", cancion.getTitulo()) +
               String.format("| Rol:      %-30s |\n", rol.getNombre()) +
               String.format("| Artista:  %-30s |\n", artista.getNombre()) +
               String.format("| Costo:    $%-29.2f |\n", costo) +
               "+------------------------------------------+";
    }
}
