package DataLoader;

import java.util.HashSet;

import Artista.ArtistaBase;
import Artista.ArtistaExterno;
import Recital.Cancion;
import Recital.Recital;
import Servicios.ServicioContratacion;

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
            // Cargargamos artistas base y externos
            HashSet<ArtistaBase> artistasBase = cargador.cargarArtistasBase();
            HashSet<ArtistaExterno> artistasExternos = cargador.cargarArtistasExternos();
            
            // Cargarmos canciones
            HashSet<Cancion> canciones = cargador.cargarCanciones();
            
            // Validamos que se cargaron datos
            if (artistasBase == null || artistasExternos == null || canciones == null) {
                throw new Exception("Error: no se pudieron cargar los datos requeridos");
            }
            
            // Creamos el servicio de contratación
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            
            // creamos y retornamos el recital
            return new Recital(artistasBase, artistasExternos, canciones, servicioContratacion);
        } catch (Exception e) {
            throw new Exception("Error al crear el recital: " + e.getMessage(), e);
        }
    }

    public void setCargador(ICargarRecital nuevoCargador) throws IllegalArgumentException {
        // Esto seria pr si queremos cambiar la estrategia de carga en tiempo de ejecucion (Psar de JSON a XML o viceversa por ej)
        if (nuevoCargador == null) {
            throw new IllegalArgumentException("El cargador no puede ser nulo");
        }
        this.cargador = nuevoCargador;
    }
}
