package Menu;
import Servicios.*;
import Artista.*;
import Menu.Auxiliares.SelectorDeOpcion;

import java.util.*;

public class ComandoArrepentimiento implements Comando {
    private ServicioContratacion servContr;
    private ServicioConsulta servC;

    public ComandoArrepentimiento(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    }

    public String getDescripcion() {
        return "Quitar artista contratado (arrepentimiento).";
    }
    
    private Artista seleccionarArtista(Scanner scanner) {
        HashSet<Artista> artistasContratados = new HashSet<>(servC.getArtistasContratados(servContr));
        List<Artista> artistas = new ArrayList<>(artistasContratados);
        
        if(artistas.isEmpty()){
            System.out.println("No hay artistas contratados para arrepentirse.");
            return null;
        }

        int cont = 1;
        HashMap<Integer, Artista> mapaIndicesArtistas = new HashMap<>();

        System.out.println("\n" + "-".repeat(60));
        System.out.println("         ARTISTAS DISPONIBLES PARA ARREPENTIMIENTO");
        System.out.println("-".repeat(60));
        for(Artista a : artistas) {
            mapaIndicesArtistas.put(cont, a);
            System.out.println(String.format("   [%d] %s", cont, a.getNombre()));
            cont++;
        }
        System.out.println("-".repeat(60));

        return SelectorDeOpcion.seleccionarDeLista(mapaIndicesArtistas, 
            "\n>> Seleccione el numero del artista para arrepentirse o 'S' para salir: ", scanner);
    }
    
    private void eliminarContratos(Artista artista) {
        servContr.eliminarContratosDeArtista(artista);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            RESULTADO DEL ARREPENTIMIENTO");
        System.out.println("=".repeat(60));
        System.out.println("   Se han eliminado todos los contratos del artista: " + artista.getNombre());
        System.out.println("=".repeat(60));
    }
    
    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        Artista artistaSeleccionado = seleccionarArtista(scanner);
        
        if(artistaSeleccionado == null){
            return;
        }
        
        eliminarContratos(artistaSeleccionado);
    }

}
