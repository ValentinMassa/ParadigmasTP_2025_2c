package Menu;

import java.util.HashMap;

import Recital.Cancion;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoRolesTodasLasCanciones implements Comando {
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
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     REPORTE DE ROLES FALTANTES - TODAS LAS CANCIONES");
        System.out.println("=".repeat(60));
        
        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            System.out.println("\n   >> Cancion: " + cancion.getTitulo());
            System.out.println("   " + "-".repeat(55));
            HashMap<Rol, Integer> rolesFaltantes = todasLasCancionesRoles.get(cancion);
            boolean tieneFaltantes = false;
            for (Rol rol : rolesFaltantes.keySet()) {
                Integer cantidadFaltante = rolesFaltantes.get(rol);
                if (cantidadFaltante > 0) {
                    System.out.println(String.format("      [!] %s: %d artista(s)", rol.getNombre(), cantidadFaltante));
                    tieneFaltantes = true;
                }
            }
            if (!tieneFaltantes) {
                System.out.println("      [OK] Sin roles faltantes!");
            }
        }
        System.out.println("\n" + "=".repeat(60));
    }

    public void ejecutar() {
        HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles;
        todasLasCancionesRoles = servC.calcularRolesFaltantesTodasLasCanciones(servContr);
        imprimirRolesFaltantes(todasLasCancionesRoles);
    }

    public String getDescripcion() {
        return "Calcular roles faltantes para todas las canciones";
    }
}
