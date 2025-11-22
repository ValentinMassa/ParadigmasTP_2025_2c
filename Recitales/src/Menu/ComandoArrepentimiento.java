package Menu;
import Servicios.*;
import Recital.*;
import Artista.*;
import Repositorios.*;
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
    private Integer seleccionarArtista(List<Artista> artistas){
        Scanner scanner = new Scanner(System.in);
        String input;
        Integer opcion = null;
        if(artistas.isEmpty()){
            System.out.println("No hay artistas contratados para arrepentirse.");
            return null;
        }

        System.out.println("\nArtistas disponibles para arrepentimiento:");
        for (int i = 0; i < artistas.size(); i++) {
            System.out.println((i + 1) + ".- " + artistas.get(i).getNombre());
        }
        System.out.print("Seleccione el numero del artista para arrepentirse o 'S' para salir: ");

        while (opcion == null) {
            input = scanner.nextLine().trim();

            // opción de salida
            if (input.equalsIgnoreCase("S")) {
                System.out.println("Saliendo...");
                return null;
            }

            // si es un número válido
            if (input.matches("\\d+")) {
                int n = Integer.parseInt(input);

                if (n >= 1 && n <= artistas.size()) {
                    opcion = n; //  seleccionó un índice válido
                } else {
                    System.out.println("Número fuera de rango. Intente nuevamente:");
                }

            } else {
                System.out.println("Entrada inválida. Intente nuevamente:");
            }
        }

        return opcion;
    }
    public void ejecutar() {
        HashSet<Artista> artistasContratados = new HashSet<>(servC.getArtistasContratados(servContr));
        List<Artista> artistas = new ArrayList<>(artistasContratados);
        Integer opc = seleccionarArtista(artistas);
        if(opc == null){
            return;
        }
        else{
            Artista artistaSeleccionado = artistas.get(opc - 1);
            servContr.eliminarContratosDeArtista(artistaSeleccionado);
            System.out.println("Se han eliminado todos los contratos del artista: " + artistaSeleccionado.getNombre());

        }

    }

}
