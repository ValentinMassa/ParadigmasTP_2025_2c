package Recital.Contratos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Recital.Cancion;
import Recital.Recital;
import Recital.Rol.Rol;
import Recital.Artista.Artista;

public class ServicioContratacion {
    public ServicioContratacion() {
    }

    public List<Contrato> contratarParaCancion(Cancion cancion) {

        List<Contrato> nuevosContratos = new ArrayList<Contrato>();
            
        Map<Rol, Integer> rolesNecesarios = cancion.getRolesRequeridos();

        // Este método se ha movido: ahora se llama desde Recital
        // para poder acceder a los métodos de búsqueda
        
        return nuevosContratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital){
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        for (Cancion cancion : recital.getCanciones()) {
            Map<Rol, Integer> rolesRequeridos = cancion.getRolesRequeridos();
            
            for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
                Rol rol = entry.getKey();
                int cantidadNecesaria = entry.getValue();
                
                for (int i = 0; i < cantidadNecesaria; i++) {
                    Artista artistaSeleccionado = recital.buscarBaseDisponible(cancion, rol);
                    
                    if (artistaSeleccionado == null) {
                        artistaSeleccionado = recital.buscarExternoDisponible(cancion, rol);
                    }
                    
                    if (artistaSeleccionado != null) {
                        Contrato contrato = new Contrato(cancion, rol, artistaSeleccionado);
                        contratos.add(contrato);
                        recital.getContratos().add(contrato);
                    }
                }
            }
        }
        
        return contratos;
    }
}
