package Menu;

import Servicios.ServicioConsulta;
import java.util.List;

public class ComandoHistorialDeColaboraciones implements Comando {
    
    private ServicioConsulta servC;

    public ComandoHistorialDeColaboraciones(ServicioConsulta sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
    }
    
    @Override
    public void ejecutar() {
        System.out.println("Historial de colaboraciones:");
        List<String> relaciones = servC.getRelacionesArtistas();
        if (relaciones.isEmpty()) {
            System.out.println("No hay relaciones de colaboraciones entre artistas.");
        } else {
            for (String relacion : relaciones) {
                System.out.println(relacion);
            }
        }
    }
    
    @Override
    public String getDescripcion() {
        return "Historial de colaboraciones visualizado";
    }
}
