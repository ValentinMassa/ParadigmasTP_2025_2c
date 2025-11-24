package Menu;
import Servicios.*;
import Recital.*;
import Artista.*;
import java.util.*;

public class ComandoListarArtistasContratados implements Comando{

    private ServicioConsulta servC;
    private ServicioContratacion servContr;

    public ComandoListarArtistasContratados(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    }
    public String getDescripcion() {
        return "Listar artistas contratados por cancion.";
    }

    public void ejecutar() {
       HashSet<Cancion> canciones = servC.getRecital().getCanciones();
         for (Cancion c : canciones){
              List<Contrato> contratosCancion = servContr.getContratosPorCancion(c);
              
              System.out.println("\n========================================");
              System.out.println("Cancion: " + c.getTitulo());
              if(contratosCancion.isEmpty()){
                System.out.println("No hay artistas contratados para esta cancion.");
              } else {
                System.out.println("Artistas contratados:");
                for(Contrato contrato : contratosCancion){
                     Artista artista = contrato.getArtista();
                     Rol rol = contrato.getRol();
                     System.out.println(" - " + artista.getNombre() + " como " + rol.getNombre() + " (Costo: $" + contrato.obtenerCostoContrato() + ")");
                }
              }
              System.out.println("========================================");
         }
    }
}
