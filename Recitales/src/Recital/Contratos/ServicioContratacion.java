package Recital.Contratos;

import java.util.ArrayList;
import java.util.List;
import Recital.Cancion;
import Recital.Recital;

public class ServicioContratacion {
    public ServicioContratacion() {
    }

    public List<Contrato> contratarParaCancion(Recital recital, Cancion cancion) {

        List<Contrato> nuevosContratos = new ArrayList<Contrato>();
            
        Map<Rol, Integer> rolesNecesarios = cancion.getRolesRequeridos();


        for (Map.Entry<Rol, Integer> entry : rolesNecesarios.entrySet()) {

            Rol rol = entry.getKey();
            int cantidadNecesaria = entry.getValue();

            for (int i = 0; i < cantidadNecesaria; i++) {

                Artista artistaSeleccionado = recital.buscarBaseDisponible(cancion, rol);

                if (artistaSeleccionado == null) {
                    artistaSeleccionado = recital.buscarExternoDisponible(cancion, rol);
                }

                if (artistaSeleccionado != null) {

                    // Crear contrato
                    Contrato contrato = new Contrato(cancion, rol, artistaSeleccionado);

                    nuevosContratos.add(contrato);

                    // Agregar contrato al recital
                    recital.getContratos().add(contrato);
                }
            }
        }

        return nuevosContratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital){
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
    }
}
