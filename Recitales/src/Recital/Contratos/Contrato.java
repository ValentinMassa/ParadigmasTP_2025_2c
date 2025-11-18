package Recital.Contratos;
import Recital.Artista.Artista;
import Recital.Artista.ArtistaBase;
import Recital.Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Rol.Rol;
import Recital.Banda.Banda;
import Recital.Recital;


public class Contrato {
    public Cancion cancion;
    public Rol rol;
    public Artista artista;
    public Recital recital;

    public Contrato(Cancion cancion, Rol rol, Artista artista, Recital recital) {
        this.cancion = cancion;
        this.rol = rol;
        this.artista = artista;
        this.recital = recital;
    }
    
    // Constructor sin recital para compatibilidad (sin descuento)
    public Contrato(Cancion cancion, Rol rol, Artista artista) {
        this.cancion = cancion;
        this.rol = rol;
        this.artista = artista;
        this.recital = null;
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
        // Si es artista base, no hay costo
        if (artista.getCosto() == 0) {
            return 0;
        }
        
        // Si no tenemos referencia al recital, devolver costo original
        if (recital == null) {
            return artista.getCosto();
        }
        
        // Solo aplicar descuento a artistas externos
        if (artista instanceof ArtistaExterno) {
            ArtistaExterno externo = (ArtistaExterno) artista;
            return externo.getCostoConDescuento(recital.getArtistasBase());
        }
        
        return artista.getCosto();
    }

    @Override
    public String toString() {
        return "  " + artista.getNombre() + " - Rol: " + rol.getNombre() + " - Cancion: " + cancion.getTitulo();
    }

}
