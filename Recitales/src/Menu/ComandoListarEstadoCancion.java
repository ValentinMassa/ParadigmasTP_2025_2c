package Menu;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Recital.*;
import java.util.*;

public class ComandoListarEstadoCancion implements Comando {

    private ServicioConsulta servC;
    private ServicioContratacion servContr;

    public ComandoListarEstadoCancion(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    }

    public String getDescripcion() {
        return "Listar canciones con su estado.";
    }
    public void ejecutar() {
        HashSet<Cancion> canciones = servC.getRecital().getCanciones();
        
        for (Cancion c : canciones){
            List<Contrato> contratosCancion = servContr.getContratosPorCancion(c);
            HashMap<Rol, Integer> rolesFaltantes = c.getRolesFaltantes(contratosCancion);
            
            System.out.println("\n========================================");
            System.out.println("Cancion: " + c.getTitulo());
            
            // Verificar si la canción está completa
            boolean estaCompleta = true;
            for (Integer cantidad : rolesFaltantes.values()) {
                if (cantidad > 0) {
                    estaCompleta = false;
                    break;
                }
            }
            
            // Mostrar estado
            if (estaCompleta) {
                System.out.println("Estado: COMPLETA");
            } else {
                System.out.println("Estado: INCOMPLETA");
                System.out.println("Roles faltantes:");
                for (Rol r : rolesFaltantes.keySet()) {
                    int cantidadFaltante = rolesFaltantes.get(r);
                    if (cantidadFaltante > 0) {
                        System.out.println("  - " + r.getNombre() + ": " + cantidadFaltante);
                    }
                }
            }
            
            // Calcular y mostrar costo total
            double costoTotal = 0.0;
            for (Contrato contrato : contratosCancion) {
                costoTotal += contrato.getCosto();
            }
            System.out.println("Costo actual: $" + String.format("%.2f", costoTotal));
            System.out.println("Contratos: " + contratosCancion.size());
            System.out.println("========================================");
        }
    }
}


