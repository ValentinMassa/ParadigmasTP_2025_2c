package Menu.Auxiliares;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;
import Artista.Artista;
import Recital.Rol;

public class EntrenadorMasivo {
    
    // Clase interna para representar un entrenamiento realizado
    public static class EntrenamientoRealizado {
        public final Artista artista;
        public final Rol rol;
        
        public EntrenamientoRealizado(Artista artista, Rol rol) {
            this.artista = artista;
            this.rol = rol;
        }
    }
    
    public static List<EntrenamientoRealizado> entrenarRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes, 
                                              ServicioConsulta servC, 
                                              ServicioContratacion servContr, 
                                              ServicioEntrenamiento servEntrenamiento, 
                                              Scanner scanner) {
        List<EntrenamientoRealizado> entrenamientos = new ArrayList<>();
        
        for (Rol rol : rolesFaltantes.keySet()) {
            int cantidad = rolesFaltantes.get(rol);
            for (int i = 0; i < cantidad; i++) {
                Artista artistaEntrenado = entrenarRol(rol, servC, servContr, servEntrenamiento, scanner);
                if (artistaEntrenado != null) {
                    entrenamientos.add(new EntrenamientoRealizado(artistaEntrenado, rol));
                }
            }
        }
        
        return entrenamientos;
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
    
    private static Artista entrenarRol(Rol rolRequerido, 
                                   ServicioConsulta servC, 
                                   ServicioContratacion servContr, 
                                   ServicioEntrenamiento servEntrenamiento, 
                                   Scanner scanner) {
        // Preguntar si desea entrenar
        System.out.print("\n>> ¿Desea entrenar un artista ahora para el rol '" + rolRequerido.getNombre() + "'? (S/N): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        
        if (!respuesta.equals("s")) {
            System.out.println("[*] Entrenamiento omitido.");
            return null;
        }
        
        // Seleccionar artista entrenable
        Artista artistaSeleccionado = SelectorArtistaEntrenable.seleccionarParaRol(servC, servContr, rolRequerido, scanner);
        if (artistaSeleccionado == null) {
            return null;
        }
        
        // Entrenar artista
        System.out.println("\n[*] Procesando entrenamiento...");
        String resultado = servEntrenamiento.entrenarArtista(servContr, artistaSeleccionado, rolRequerido);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            RESULTADO DEL ENTRENAMIENTO");
        System.out.println("=".repeat(60));
        System.out.println("   " + resultado);
        System.out.println("=".repeat(60));
        
        return artistaSeleccionado;
    }
}