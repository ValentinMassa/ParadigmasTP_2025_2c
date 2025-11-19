package DataLoader;

import java.util.HashSet;

import Artista.ArtistaBase;
import Artista.ArtistaExterno;
import Recital.Cancion;

public interface ICargarRecital {
    

    HashSet<ArtistaExterno> cargarArtistasExternos() throws Exception;
    HashSet<ArtistaBase> cargarArtistasBase() throws Exception;
    HashSet<Cancion> cargarCanciones() throws Exception;
}
