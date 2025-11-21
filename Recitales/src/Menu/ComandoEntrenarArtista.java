package Menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;
import Artista.Artista;


public class ComandoEntrenarArtista implements Comando {
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    private ServicioEntrenamiento servEntrenamiento;
    
    public ComandoEntrenarArtista(ServicioConsulta sc, ServicioContratacion scontr, ServicioEntrenamiento servEntrenamiento){
        if(sc == null || scontr == null || servEntrenamiento == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
        this.servEntrenamiento = servEntrenamiento;
    } 

    private Artista seleccionarArtista(Scanner scanner){
        int cont = 1;
        HashMap<Integer, Artista> mapaIndicesArtistas = new HashMap<>();

        System.out.println("\n" + "─".repeat(60));
        System.out.println("   🎓 ARTISTAS DISPONIBLES PARA ENTRENAR");
        System.out.println("─".repeat(60));
        for(Artista a : servC.getArtistasEntrenables()){
            mapaIndicesArtistas.put(cont, a);
            System.out.println(String.format("   [%d] 🎤 %s", cont, a.getNombre()));
            cont++;
        }
        System.out.println("─".repeat(60));

        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesArtistas, 
            "\n👉 Ingrese el número del artista que desea entrenar o 'S' para salir: ", scanner);
    }

    private Rol seleccionarRolAEntrenar(Artista artista, Scanner scanner){
        int cont = 1;
        HashMap<Integer, Rol> mapaIndicesRoles = new HashMap<>();
        HashSet<Rol> rolesDeArtista = servC.getRolesDeArtista(artista);

        System.out.println("\n" + "─".repeat(60));
        System.out.println(String.format("   🎭 ROLES DISPONIBLES PARA: %s", artista.getNombre()));
        System.out.println("─".repeat(60));
        for(Rol r : servC.getTodosLosRoles()){
            if(rolesDeArtista.contains(r)){
                continue; // El artista ya tiene este rol, no se muestra para entrenar
            }
            mapaIndicesRoles.put(cont, r);
            System.out.println(String.format("   [%d] 🎯 %s", cont, r.getNombre()));
            cont++;
        }
        System.out.println("─".repeat(60));

        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesRoles, 
            "\n👉 Ingrese el número del rol que desea entrenar o 'S' para salir: ", scanner);
    }

    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        Artista a = seleccionarArtista(scanner);
        if (a == null) return;
        
        Rol r = seleccionarRolAEntrenar(a, scanner);
        if (r == null) return;
        
        System.out.println("\n⏳ Procesando entrenamiento...");
        System.out.println("\n" + "═".repeat(60));
        System.out.println("   📊 RESULTADO DEL ENTRENAMIENTO");
        System.out.println("═".repeat(60));
        System.out.println("   " + servEntrenamiento.entrenarArtista(servContr, a, r));
        System.out.println("═".repeat(60));
    }
    
    public String getDescripcion() {
        return "Entrenar artista";
    }


}
