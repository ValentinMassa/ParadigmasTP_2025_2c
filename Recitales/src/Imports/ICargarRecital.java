package Imports;

import java.util.HashSet;
import Recital.Artista.ArtistaBase;
import Recital.Artista.ArtistaExterno;
import Recital.Cancion;

public interface ICargarRecital {
    

    HashSet<ArtistaExterno> cargarArtistasExternos() throws Exception;
    HashSet<ArtistaBase> cargarArtistasBase() throws Exception;
    HashSet<Cancion> cargarCanciones() throws Exception;
}
