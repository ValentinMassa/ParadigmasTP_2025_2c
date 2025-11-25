package DataLoader;

import java.util.HashSet;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Recital.Cancion;
import Repositorios.RepositorioRoles;
import Repositorios.RepositorioBandas;

public interface ICargarRecital {
    

    public HashSet<ArtistaExterno> cargarArtistasExternos() throws Exception;
    public HashSet<ArtistaDiscografica> cargarArtistasDiscografica() throws Exception;
    public HashSet<Cancion> cargarCanciones() throws Exception;
    public RepositorioRoles getRolCatalogo();
    public RepositorioBandas getBandaCatalogo();
}
