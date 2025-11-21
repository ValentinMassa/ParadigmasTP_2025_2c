package DataLoader;

import java.util.HashSet;

import Artista.ArtistaDiscografica;
import Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Recital;
import Repositorios.RepositorioArtistasMemory;
import Repositorios.RolCatalogoMemory;

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
            HashSet<Cancion> canciones = cargador.cargarCanciones();
            if (canciones == null) {
                throw new Exception("Error: no se pudieron cargar los datos requeridos");
            }
            return new Recital(canciones);
        } catch (Exception e) {
            throw new Exception("Error al crear el recital: " + e.getMessage(), e);
        }
    }

    public RepositorioArtistasMemory crearRepositorioArtistas() throws Exception {
        try {
            HashSet<ArtistaDiscografica> artistasBase = cargador.cargarArtistasDiscografica();
            HashSet<ArtistaExterno> artistasExternos = cargador.cargarArtistasExternos();
            if (artistasBase == null || artistasExternos == null) {
                throw new Exception("Error: no se pudieron cargar los datos requeridos");
            }
            return new RepositorioArtistasMemory(artistasBase, artistasExternos);
        } catch (Exception e) {
            throw new Exception("Error al crear el repositorio de artistas: " + e.getMessage(), e);
        }
    }

    public void setCargador(ICargarRecital nuevoCargador) throws IllegalArgumentException {
        if (nuevoCargador == null) {
            throw new IllegalArgumentException("El cargador no puede ser nulo");
        }
        this.cargador = nuevoCargador;
    }

    /**
     * Obtiene el catálogo de roles.
     * NOTA: Se debe llamar a crearRepositorioArtistas() y crearRecital() primero para cargar los roles.
     * @return el catálogo de roles
     * @throws IllegalStateException si los roles no han sido cargados
     */
    public RolCatalogoMemory construirRoles() {
        RolCatalogoMemory catalogo = cargador.getRolCatalogo();
        if (catalogo == null) {
            throw new IllegalStateException("Los roles no han sido cargados. Debe llamar a crearRepositorioArtistas() primero.");
        }
        return catalogo;
    }
}
