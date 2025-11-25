package Menu;

import Recital.Cancion;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import java.util.*;

import Menu.Auxiliares.SelectorDeOpcion;
import Recital.Rol;


public class ComandoRolesFaltantesPorCancion implements Comando{
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    
    public ComandoRolesFaltantesPorCancion(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    } 

    private Cancion seleccionarCancion(Scanner scanner){
        HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles;
        HashMap<Integer, Cancion> mapaIndicesCanciones = new HashMap<>();
        int indice = 1;

        System.out.println("\n" + "-".repeat(60));
        System.out.println("            CANCIONES DISPONIBLES");
        System.out.println("-".repeat(60));
        
        todasLasCancionesRoles = servC.getRolesDeTodasLasCanciones();

        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            System.out.println(String.format("   [%d] %s", indice, cancion.getTitulo()));
            mapaIndicesCanciones.put(indice, cancion);
            indice++;
        }
        
        System.out.println("-".repeat(60));
        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesCanciones, 
            "\n>> Ingrese el numero de la cancion o 'S' para salir: ", scanner);
    }

    private void imprimirRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes, Cancion cancion) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println(String.format("         ROLES FALTANTES: %s", cancion.getTitulo()));
        System.out.println("-".repeat(60));
        for (Rol rol : rolesFaltantes.keySet()) {
            Integer cantidadFaltante = rolesFaltantes.get(rol);
            if (cantidadFaltante > 0) {
                System.out.println(String.format("   [!] %s: %d artista(s) necesario(s)", rol.getNombre(), cantidadFaltante));
            }
        }
        System.out.println("-".repeat(60));
    }

    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        Cancion cancion;
        HashMap<Rol, Integer> rolesFaltantes;

        cancion = seleccionarCancion(scanner);
        if(cancion == null){
            return;
        }
        rolesFaltantes = servC.calcularRolesFaltantes(cancion, servContr.getContratosPorCancion(cancion));
        if(rolesFaltantes == null || rolesFaltantes.isEmpty()){
            System.out.println("\n[OK] Perfecto! No hay roles faltantes para la cancion '" + cancion.getTitulo() + "'");
            return;
        }
        imprimirRolesFaltantes(rolesFaltantes, cancion);
    }
    
    public String getDescripcion() {
        return "Calcular roles faltantes por cancion";
    }
}
