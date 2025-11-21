package Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import Recital.Cancion;
import Recital.Contrato;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoContratarArtistaParaCancionX implements Comando{
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    
    public ComandoContratarArtistaParaCancionX(ServicioConsulta sc, ServicioContratacion scontr){
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

        System.out.println("\n" + "─".repeat(60));
        System.out.println("   🎼 CANCIONES DISPONIBLES PARA CONTRATACIÓN");
        System.out.println("─".repeat(60));
        
        todasLasCancionesRoles = servC.getRolesDeTodasLasCanciones();

        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            System.out.println(String.format("   [%d] 🎵 %s", indice, cancion.getTitulo()));
            mapaIndicesCanciones.put(indice, cancion);
            indice++;
        }
        
        System.out.println("─".repeat(60));
        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesCanciones, 
            "\n👉 Ingrese el número de la canción o 'S' para salir: ", scanner);
    }
    
    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        Cancion cancion = seleccionarCancion(scanner);
        if (cancion == null) {
            return;
        }

        System.out.println("\n⏳ Procesando contratación de artistas...");
        servContr.contratarArtistasParaCancion(cancion, servC.getRepositorioArtistas());
        List<Contrato> contratos = servContr.getContratosPorCancion(cancion);
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println(String.format("   📋 CONTRATOS PARA: %s", cancion.getTitulo()));
        System.out.println("═".repeat(60));
        
        if (contratos.isEmpty()) {
            System.out.println("   ⚠️  No se realizaron contratos para esta canción.");
        } else {
            for (int i = 0; i < contratos.size(); i++) {
                System.out.println(String.format("\n   [Contrato #%d]", (i + 1)));
                System.out.println("   " + contratos.get(i).toString());
            }
        }
        System.out.println("\n" + "═".repeat(60));
    }

    public String getDescripcion() {
        return "Contratar artistas para una canción X del recital";
    }

}
