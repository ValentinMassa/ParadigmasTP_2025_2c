package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoContratarArtistas implements Comando{
    private ServicioContratacion servContr;
    private ServicioConsulta servC;

    public ComandoContratarArtistas(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    }

    public void ejecutar() {
        
    }


    public String getDescripcion() {
        return "Contratar artistas para todas las canciones a la vez";
    }
}
