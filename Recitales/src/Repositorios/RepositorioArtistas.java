package Repositorios;

import java.util.HashSet;
import Artista.*;

public class RepositorioArtistas {

    private HashSet<ArtistaBase> artistaBase;
    private HashSet<ArtistaExterno> artistaExternos;

    public RepositorioArtistas() {
        this.artistaBase = new HashSet<>();
        this.artistaExternos = new HashSet<>();
    }

    public RepositorioArtistas(HashSet<ArtistaBase> artistaBase, HashSet<ArtistaExterno> artistaExternos) {
        this.artistaBase = new HashSet<>(artistaBase);
        this.artistaExternos = new HashSet<>(artistaExternos);
    }

    public HashSet<ArtistaBase> getArtistaBase() {
        return new HashSet<>(artistaBase);
    }

    public HashSet<ArtistaExterno> getArtistaExternos() {
        return new HashSet<>(artistaExternos);
    }
}
