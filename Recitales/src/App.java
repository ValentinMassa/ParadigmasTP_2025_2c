import DataLoader.FabricaRecital;
import DataLoader.JsonAdapter;
import Menu.Comando;
import Menu.ComandoContratarArtistaParaCancionX;
import Menu.ComandoRolesFaltantesPorCancion;
import Menu.ComandoRolesTodasLasCanciones;
import Menu.MenuPrincipal;
import Recital.Recital;
import Repositorios.RepositorioArtistasMemory;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            // Determinar el directorio base
            String baseDir = System.getProperty("user.dir");
            if (!baseDir.endsWith("Recitales")) {
                baseDir = baseDir + "/Recitales";
            }
            
            // Configurar rutas de archivos JSON
            String rutaArtistas = baseDir + "/data/artistas.json";
            String rutaCanciones = baseDir + "/data/recital.json";
            String rutaArtistasBase = baseDir + "/data/artistas-discografica.json";
            
            // Crear adaptador y fábrica de recital
            JsonAdapter jsonAdapter = new JsonAdapter(rutaArtistas, rutaCanciones, rutaArtistasBase);
            FabricaRecital fabrica = new FabricaRecital(jsonAdapter);
            
            // Crear recital y cargar datos
            System.out.println("Cargando recital...");
            Recital recital = fabrica.crearRecital();
            
            // Cargar artistas
            RepositorioArtistasMemory repositorio = new RepositorioArtistasMemory(
                jsonAdapter.cargarArtistasBase(),
                jsonAdapter.cargarArtistasExternos()
            );
            
            System.out.println("Recital cargado exitosamente!");
            System.out.println("Canciones: " + recital.getCanciones().size());
            System.out.println("Artistas Base: " + repositorio.getArtistasDiscografica().size());
            System.out.println("Artistas Externos: " + repositorio.getArtistasExternos().size());
            System.out.println();
            
            // Crear servicios
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            ServicioConsulta servicioConsulta = new ServicioConsulta(repositorio, recital);
            
            // Crear comandos del menú
            List<Comando> comandos = new ArrayList<>();
            comandos.add(new ComandoRolesFaltantesPorCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoRolesTodasLasCanciones(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoContratarArtistaParaCancionX(servicioConsulta, servicioContratacion));
            
            // Crear y mostrar menú
            MenuPrincipal menu = new MenuPrincipal(comandos);
            menu.mostrar();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
