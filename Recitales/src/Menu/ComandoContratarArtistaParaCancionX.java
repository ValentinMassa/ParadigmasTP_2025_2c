package Menu;

import java.util.HashMap;

import Recital.Cancion;
import Recital.Contrato;
import Recital.Rol;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import java.util.List;

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

    private Cancion seleccionarCancion(){
        System.out.println("Seleccione una canción: ");
        HashMap<Cancion, HashMap<Rol,Integer>> todasLasCancionesRoles;
        todasLasCancionesRoles = servC.getRolesDeTodasLasCanciones();
        int indice = 1;
        HashMap<Integer, Cancion> mapaIndicesCanciones = new HashMap<>();
        for (Cancion cancion : todasLasCancionesRoles.keySet()) {
            System.out.println(indice + ". " + cancion.getTitulo());
            mapaIndicesCanciones.put(indice, cancion);
            indice++;
        }
        System.out.print("Ingrese el número de la canción: ");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int opcion = scanner.nextInt();
        Cancion cancionSeleccionada = mapaIndicesCanciones.get(opcion);
        if (cancionSeleccionada == null) {
            throw new IllegalArgumentException("Opción inválida. No existe una canción con ese número.");
        }
        return cancionSeleccionada;
    }
    
    public void ejecutar() {
        servContr.contratarArtistasParaCancion(seleccionarCancion(), servC.getRepositorioArtistas());
        List<Contrato> contratos = servContr.getContratosPorCancion(seleccionarCancion());
        System.out.println("Contratos para la canción seleccionada: ");
        for (Contrato contrato : contratos) {
            System.out.println(contrato.toString());
        }

    }

    public String getDescripcion() {
        return "Contratar artistas para una canción X del recital";
    }

}
