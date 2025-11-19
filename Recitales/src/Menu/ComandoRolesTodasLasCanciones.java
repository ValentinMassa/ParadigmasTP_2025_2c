package Menu;

import java.util.HashMap;

import Recital.Cancion;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoRolesTodasLasCanciones {
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    
    public ComandoRolesTodasLasCanciones(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    } 
    
    private void imprimirRolesFaltantes(HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles) {
        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            System.out.println("Cancion: " + cancion.getTitulo());
            HashMap<Rol, Integer> rolesFaltantes = todasLasCancionesRoles.get(cancion);
            for (Rol rol : rolesFaltantes.keySet()) {
                Integer cantidadFaltante = rolesFaltantes.get(rol);
                if (cantidadFaltante > 0) {
                    System.out.println("- " + rol.getNombre() + ": " + cantidadFaltante);
                }
            }
            System.out.println();
        }
    }

    public void ejecutar() {
        HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles;
        todasLasCancionesRoles = servC.calcularRolesFaltantesTodasLasCanciones(servContr.getContratos());
    
        imprimirRolesFaltantes(todasLasCancionesRoles);
    }

    public String getDescripcion() {
        return "Calcular roles faltantes para todas las canciones";
    }
}
