package Repositorios.Interfaz;
import java.util.HashSet;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;

public interface IRepositorioArtista {
    public HashSet<ArtistaExterno> getArtistaExternos();
    public HashSet<ArtistaDiscografica> getArtistaBase();
}
