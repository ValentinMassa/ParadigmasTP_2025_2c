package Menu.Auxiliares;

import java.util.HashMap;
import java.util.Scanner;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;
import Artista.Artista;
import Recital.Rol;

public class EntrenadorMasivo {
    
    public static void entrenarRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes, 
                                              ServicioConsulta servC, 
                                              ServicioContratacion servContr, 
                                              ServicioEntrenamiento servEntrenamiento, 
                                              Scanner scanner) {
        for (Rol rol : rolesFaltantes.keySet()) {
            int cantidad = rolesFaltantes.get(rol);
            for (int i = 0; i < cantidad; i++) {
                entrenarRol(rol, servC, servContr, servEntrenamiento, scanner);
            }
        }
    }
    
    public static void entrenarRolesFaltantesParaCancion(HashMap<Rol, Integer> rolesFaltantes, 
                                                         ServicioConsulta servC, 
                                                         ServicioContratacion servContr, 
                                                         ServicioEntrenamiento servEntrenamiento, 
                                                         Scanner scanner) {
        for (Rol rol : rolesFaltantes.keySet()) {
            int cantidad = rolesFaltantes.get(rol);
            for (int i = 0; i < cantidad; i++) {
                entrenarRol(rol, servC, servContr, servEntrenamiento, scanner);
            }
        }
    }
    
    private static void entrenarRol(Rol rolRequerido, 
                                   ServicioConsulta servC, 
                                   ServicioContratacion servContr, 
                                   ServicioEntrenamiento servEntrenamiento, 
                                   Scanner scanner) {
        // Preguntar si desea entrenar
        System.out.print("\n>> ¿Desea entrenar un artista ahora para el rol '" + rolRequerido.getNombre() + "'? (S/N): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        
        if (!respuesta.equals("s")) {
            System.out.println("[*] Entrenamiento omitido.");
            return;
        }
        
        // Seleccionar artista entrenable
        Artista artistaSeleccionado = SelectorArtistaEntrenable.seleccionarParaRol(servC, servContr, rolRequerido, scanner);
        if (artistaSeleccionado == null) {
            return;
        }
        
        // Entrenar artista
        System.out.println("\n[*] Procesando entrenamiento...");
        String resultado = servEntrenamiento.entrenarArtista(servContr, artistaSeleccionado, rolRequerido);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            RESULTADO DEL ENTRENAMIENTO");
        System.out.println("=".repeat(60));
        System.out.println("   " + resultado);
        System.out.println("=".repeat(60));
    }
}