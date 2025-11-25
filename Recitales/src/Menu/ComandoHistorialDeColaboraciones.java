package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import java.util.List;

public class ComandoHistorialDeColaboraciones implements Comando {
    
    private ServicioConsulta servC;
    private ServicioContratacion servContratacion;

    public ComandoHistorialDeColaboraciones(ServicioConsulta sc, ServicioContratacion sContratacion) {
        if (sc == null || sContratacion == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContratacion = sContratacion;
    }
    
    @Override
    public void ejecutar() {
        System.out.println("Historial de colaboraciones:");
        List<String> relaciones = servC.getRelacionesArtistas(servContratacion);
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
