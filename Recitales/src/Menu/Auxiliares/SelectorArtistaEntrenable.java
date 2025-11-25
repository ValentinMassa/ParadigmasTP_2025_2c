package Menu.Auxiliares;

import java.util.HashMap;
import java.util.Scanner;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Artista.Artista;
import Recital.Rol;

public class SelectorArtistaEntrenable {
    
    public static Artista seleccionar(ServicioConsulta servC, ServicioContratacion servContr, Scanner scanner) {
        int cont = 1;
        HashMap<Integer, Artista> mapaIndicesArtistas = new HashMap<>();

        System.out.println("\n" + "-".repeat(60));
        System.out.println("         ARTISTAS DISPONIBLES PARA ENTRENAR");
        System.out.println("         (Externos sin contratos activos)");
        System.out.println("-".repeat(60));
        
        for(Artista a : servC.getArtistasEntrenables()){
            // Filtrar solo artistas que NO tienen contratos activos
            if(!servContr.tieneAlgunContrato(a)){
                mapaIndicesArtistas.put(cont, a);
                System.out.println(String.format("   [%d] %s (Costo actual: $%.2f)", 
                    cont, a.getNombre(), a.getCosto()));
                cont++;
            }
        }
        
        if(mapaIndicesArtistas.isEmpty()){
            System.out.println("   [!] No hay artistas disponibles para entrenar.");
            System.out.println("   [*] Todos los artistas externos tienen contratos activos.");
            System.out.println("-".repeat(60));
            return null;
        }
        
        System.out.println("-".repeat(60));
        System.out.println("   [*] Nota: El costo aumentará en 50% tras el entrenamiento.");

        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesArtistas, 
            "\n>> Ingrese el numero del artista que desea entrenar o 'S' para salir: ", scanner);
    }
    
    public static Artista seleccionarParaRol(ServicioConsulta servC, ServicioContratacion servContr, Rol rol, Scanner scanner) {
        int cont = 1;
        HashMap<Integer, Artista> mapaIndicesArtistas = new HashMap<>();

        System.out.println("\n" + "-".repeat(60));
        System.out.println(String.format("   ARTISTAS DISPONIBLES PARA ENTRENAR EN: %s", rol.getNombre()));
        System.out.println("   (Externos sin contratos activos y sin poseer el rol)");
        System.out.println("-".repeat(60));
        
        for(Artista a : servC.getArtistasEntrenables()){
            // Filtrar: sin contratos y sin poseer el rol
            if(!servContr.tieneAlgunContrato(a) && !a.puedeTocarRol(rol.getNombre())){
                mapaIndicesArtistas.put(cont, a);
                System.out.println(String.format("   [%d] %s (Costo actual: $%.2f)", 
                    cont, a.getNombre(), a.getCosto()));
                cont++;
            }
        }
        
        if(mapaIndicesArtistas.isEmpty()){
            System.out.println("   [!] No hay artistas disponibles para entrenar en este rol.");
            System.out.println("   [*] Todos los artistas tienen contratos activos o ya poseen el rol.");
            System.out.println("-".repeat(60));
            return null;
        }
        
        System.out.println("-".repeat(60));
        System.out.println("   [*] Nota: El costo aumentará en 50% tras el entrenamiento.");

        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesArtistas, 
            "\n>> Ingrese el numero del artista o 'S' para salir: ", scanner);
    }
}