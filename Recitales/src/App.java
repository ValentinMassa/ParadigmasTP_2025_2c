import DataLoader.FabricaRecital;
import DataLoader.JsonAdapter;
import Menu.Comando;
import Menu.ComandoRolesFaltantesPorCancion;
import Menu.ComandoRolesTodasLasCanciones;
import Menu.MenuPrincipal;
import Recital.Recital;
import Repositorios.RepositorioArtistas;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            // Configurar rutas de archivos JSON
            String rutaArtistas = "Recitales/data/artistas.json";
            String rutaCanciones = "Recitales/data/recital.json";
            String rutaArtistasBase = "Recitales/data/artistas-discografica.json";
            
            // Crear adaptador y fábrica de recital
            JsonAdapter jsonAdapter = new JsonAdapter(rutaArtistas, rutaCanciones, rutaArtistasBase);
            FabricaRecital fabrica = new FabricaRecital(jsonAdapter);
            
            // Crear recital y cargar datos
            System.out.println("Cargando recital...");
            Recital recital = fabrica.crearRecital();
            
            // Cargar artistas
            RepositorioArtistas repositorio = new RepositorioArtistas(
                jsonAdapter.cargarArtistasBase(),
                jsonAdapter.cargarArtistasExternos()
            );
            
            System.out.println("Recital cargado exitosamente!");
            System.out.println("Canciones: " + recital.getCanciones().size());
            System.out.println("Artistas Base: " + repositorio.getArtistaBase().size());
            System.out.println("Artistas Externos: " + repositorio.getArtistaExternos().size());
            System.out.println();
            
            // Crear servicios
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            ServicioConsulta servicioConsulta = new ServicioConsulta(repositorio, recital);
            
            // Crear comandos del menú
            List<Comando> comandos = new ArrayList<>();
            comandos.add(new ComandoRolesFaltantesPorCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoRolesTodasLasCanciones(servicioConsulta, servicioContratacion));
            
            // Crear y mostrar menú
            MenuPrincipal menu = new MenuPrincipal(comandos);
            menu.mostrar();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
