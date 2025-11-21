package Repositorios;

import java.util.HashSet;
import Artista.*;
import Repositorios.Interfaz.IRepositorioArtista;

public class RepositorioArtistasMemory{

    private HashSet<ArtistaDiscografica> artistaBase;
    private HashSet<ArtistaExterno> artistaExternos;

    public RepositorioArtistasMemory() {
        this.artistaBase = new HashSet<>();
        this.artistaExternos = new HashSet<>();
    }

    public RepositorioArtistasMemory(HashSet<ArtistaDiscografica> artistaBase, HashSet<ArtistaExterno> artistaExternos) {
        this.artistaBase = new HashSet<>(artistaBase);
        this.artistaExternos = new HashSet<>(artistaExternos);
    }

    public HashSet<ArtistaDiscografica> getArtistasDiscografica() {
        return new HashSet<>(artistaBase);
    }

    public HashSet<ArtistaExterno> getArtistasExternos() {
        return new HashSet<>(artistaExternos);
    }
}
