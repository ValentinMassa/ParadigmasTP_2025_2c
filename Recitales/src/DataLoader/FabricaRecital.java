package DataLoader;

import java.util.HashSet;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Recital;

public class FabricaRecital {
    
    private ICargarRecital cargador;

    public FabricaRecital(ICargarRecital cargador) throws IllegalArgumentException {
        if (cargador == null) {
            throw new IllegalArgumentException("El cargador no puede ser nulo");
        }
        this.cargador = cargador;
    }

    public Recital crearRecital() throws Exception {
        try {
            HashSet<ArtistaDiscografica> artistasBase = cargador.cargarArtistasBase();
            HashSet<ArtistaExterno> artistasExternos = cargador.cargarArtistasExternos();
            HashSet<Cancion> canciones = cargador.cargarCanciones();

            if (artistasBase == null || artistasExternos == null || canciones == null) {
                throw new Exception("Error: no se pudieron cargar los datos requeridos");
            }
            return new Recital(canciones);
        } catch (Exception e) {
            throw new Exception("Error al crear el recital: " + e.getMessage(), e);
        }
    }

    public void setCargador(ICargarRecital nuevoCargador) throws IllegalArgumentException {
        if (nuevoCargador == null) {
            throw new IllegalArgumentException("El cargador no puede ser nulo");
        }
        this.cargador = nuevoCargador;
    }
}
