package Menu;

import Recital.Cancion;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import java.util.*; 
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

    private Cancion obtenerCancion(){
        Scanner sc = new Scanner(System.in);
        Cancion cancion;
        do{
            System.out.println("Ingrese el nombre de la cancion o 'S' para salir: ");
            String nombreCancion = sc.nextLine().trim();
            if(nombreCancion.equalsIgnoreCase("S")){
                return null;
            }
            cancion = servC.getCancionPorNombre(nombreCancion);
            if(cancion == null)
                System.out.println("La cancion ingresada no existe. Intente nuevamente.");

        }while(cancion == null);
        return cancion;
    } 
    private void imprimirRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes, Cancion cancion) {
        System.out.println("Roles faltantes para la cancion " + cancion.getTitulo() + ":");
        for (Rol rol : rolesFaltantes.keySet()) {
            Integer cantidadFaltante = rolesFaltantes.get(rol);
            if (cantidadFaltante > 0) {
                System.out.println("- " + rol.getNombre() + ": " + cantidadFaltante);
            }
        }
    }

    public void ejecutar() {
        Cancion cancion;
        HashMap<Rol, Integer> rolesFaltantes;

        cancion = obtenerCancion();
        if(cancion == null){
            System.out.println("Saliendo del comando.");
            return;
        }
        rolesFaltantes = servC.calcularRolesFaltantes(cancion, servContr.getContratosPorCancion(cancion));
        if(rolesFaltantes == null || rolesFaltantes.isEmpty()){
            System.out.println("No hay roles faltantes para la cancion " + cancion.getTitulo());
            return;
        }
        imprimirRolesFaltantes(rolesFaltantes, cancion);
    }
    
    public String getDescripcion() {
        return "Calcular roles faltantes por cancion";
    }
}
