package Recital.Contratos;
import Recital.Artista.Artista;
import Recital.Cancion;
import Recital.Rol.Rol;


public class Contrato {
    public Cancion cancion;
    public Rol rol;
    public Artista artista;

    public Contrato(Cancion cancion, Rol rol, Artista artista) {
        this.cancion = cancion;
        this.rol = rol;
        this.artista = artista;
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

    public double obtenerCostoContrato(){
        return artista.getCosto();
    }

    @Override
    public String toString() {
        return "  " + artista.getNombre() + " - Rol: " + rol.getNombre() + " - Canción: " + cancion.getTitulo();
    }

}
