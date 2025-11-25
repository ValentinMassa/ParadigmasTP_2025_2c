package Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Menu.Auxiliares.SelectorDeOpcion;
import Menu.Auxiliares.EntrenadorMasivo;
import Recital.Cancion;
import Recital.Contrato;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;

public class ComandoContratarArtistaParaCancionX implements Comando{
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    private ServicioEntrenamiento servEntrenamiento;
    
    public ComandoContratarArtistaParaCancionX(ServicioConsulta sc, ServicioContratacion scontr, ServicioEntrenamiento servEntrenamiento){
        if(sc == null || scontr == null || servEntrenamiento == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
        this.servEntrenamiento = servEntrenamiento;
    } 

    private Cancion seleccionarCancion(Scanner scanner){
        HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles;
        HashMap<Integer, Cancion> mapaIndicesCanciones = new HashMap<>();
        int indice = 1;

        System.out.println("\n" + "-".repeat(60));
        System.out.println("      CANCIONES DISPONIBLES PARA CONTRATACION");
        System.out.println("-".repeat(60));
        
        todasLasCancionesRoles = servC.getRolesDeTodasLasCanciones();

        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            // Verificar si la canción tiene roles faltantes
            List<Contrato> contratosCancion = servContr.getContratosPorCancion(cancion);
            HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(contratosCancion);
            
            boolean tieneRolesFaltantes = false;
            for (Integer cantidad : rolesFaltantes.values()) {
                if (cantidad > 0) {
                    tieneRolesFaltantes = true;
                    break;
                }
            }
            
            // Solo mostrar canciones incompletas
            if (tieneRolesFaltantes) {
                System.out.println(String.format("   [%d] %s", indice, cancion.getTitulo()));
                mapaIndicesCanciones.put(indice, cancion);
                indice++;
            }
        }
        
        if (mapaIndicesCanciones.isEmpty()) {
            System.out.println("   [!] Todas las canciones tienen sus roles completos.");
            System.out.println("-".repeat(60));
            return null;
        }
        
        System.out.println("-".repeat(60));
        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesCanciones, 
            "\n>> Ingrese el numero de la cancion o 'S' para salir: ", scanner);
    }
    
    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        Cancion cancion = seleccionarCancion(scanner);
        if (cancion == null) {
            return;
        }

        System.out.println("\n[*] Procesando contratacion de artistas...");
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = 
            servContr.contratarArtistasParaCancion(cancion, servC.getRepositorioArtistas());
        
        List<Contrato> contratos = servContr.getContratosPorCancion(cancion);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println(String.format("            CONTRATOS PARA: %s", cancion.getTitulo()));
        System.out.println("=".repeat(60));
        
        if (contratos.isEmpty()) {
            System.out.println("   [!] No se realizaron contratos para esta cancion.");
        } else {
            for (int i = 0; i < contratos.size(); i++) {
                System.out.println(String.format("\n   [Contrato #%d]", (i + 1)));
                System.out.println(contratos.get(i).toString());
            }
        }
        
        // Verificar si hay roles que requieren entrenamiento
        if (rolesQueRequierenEntrenamiento != null && !rolesQueRequierenEntrenamiento.isEmpty()) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("           ROLES QUE REQUIEREN ENTRENAMIENTO");
            System.out.println("-".repeat(60));
            
            for (Rol rol : rolesQueRequierenEntrenamiento.keySet()) {
                int cantidad = rolesQueRequierenEntrenamiento.get(rol);
                System.out.println(String.format("   [!] %s: %d artista(s) necesario(s)", rol.getNombre(), cantidad));
            }
            
            System.out.println("\n   >> Debe entrenar artistas para estos roles antes de");
            System.out.println("      poder completar la contratacion para esta cancion.");
            
            // Ofrecer entrenamiento
            EntrenadorMasivo.entrenarRolesFaltantesParaCancion(rolesQueRequierenEntrenamiento, servC, servContr, servEntrenamiento, scanner);
            
            // Reintentar contratación después del entrenamiento
            System.out.println("\n[*] Reintentando contratación para la canción tras entrenamientos...\n");
            rolesQueRequierenEntrenamiento = servContr.contratarArtistasParaCancion(cancion, servC.getRepositorioArtistas());
            
            // Mostrar contratos actualizados
            contratos = servContr.getContratosPorCancion(cancion);
            System.out.println("\n" + "=".repeat(60));
            System.out.println(String.format("        CONTRATOS ACTUALIZADOS PARA: %s", cancion.getTitulo()));
            System.out.println("=".repeat(60));
            
            if (contratos.isEmpty()) {
                System.out.println("   [!] No se realizaron contratos para esta canción.");
            } else {
                for (int i = 0; i < contratos.size(); i++) {
                    System.out.println(String.format("\n   [Contrato #%d]", (i + 1)));
                    System.out.println(contratos.get(i).toString());
                }
            }
            
            // Verificar si aún faltan roles
            if (rolesQueRequierenEntrenamiento != null && !rolesQueRequierenEntrenamiento.isEmpty()) {
                System.out.println("\n[!] Aún faltan roles por entrenar para completar la canción.");
                System.out.println("   Regrese al menú para intentarlo nuevamente.");
            } else if (rolesQueRequierenEntrenamiento == null) {
                System.out.println("\n[OK] Todos los roles han sido cubiertos exitosamente tras el entrenamiento!");
            }
        } else if (rolesQueRequierenEntrenamiento == null) {
            System.out.println("\n   [OK] Todos los roles han sido cubiertos exitosamente!");
        }
        
        System.out.println("\n" + "=".repeat(60));
    }

    public String getDescripcion() {
        return "Contratar artistas para una cancion X del recital";
    }

}
