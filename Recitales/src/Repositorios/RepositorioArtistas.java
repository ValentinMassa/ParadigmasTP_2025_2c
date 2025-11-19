package Repositorios;

import java.util.HashSet;

import Artista.ArtistaBase;
import Artista.ArtistaExterno;

public class RepositorioArtistas {

    private HashSet<ArtistaBase> artistaBase;
    private HashSet<ArtistaExterno> artistaExternos;

    public HashSet<ArtistaBase> getArtistaBase() {
        return artistaBase;
    }

    public HashSet<ArtistaExterno> getArtistaExternos() {
        return artistaExternos;
    }
}
