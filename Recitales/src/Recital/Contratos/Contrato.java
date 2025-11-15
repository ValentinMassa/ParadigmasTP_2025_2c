package Recital.Contratos;
import Recital.Artista.Artista;
import Recital.Cancion;
import Recital.Rol.Rol;


public class Contrato {
    private Cancion cancion;
    private Rol rol;
    private Artista artista;

    public Contrato(Cancion cancion, Rol rol, Artista artista) {
        this.cancion = cancion;
        this.rol = rol;
        this.artista = artista;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!(obj instanceof Contrato)) return false;
        Contrato contrato = (Contrato) obj;
        return cancion.equals(contrato.cancion) &&
               rol.equals(contrato.rol) &&
               artista.equals(contrato.artista);
    }

    public double obtenerCostoContrato(){
        //Falta Desarrollar
        return 0;
    }

}
