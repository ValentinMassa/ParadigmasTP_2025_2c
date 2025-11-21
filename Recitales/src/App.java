import DataLoader.FabricaRecital;
import DataLoader.JsonAdapter;
import Menu.Comando;
import Menu.ComandoContratarArtistaParaCancionX;
import Menu.ComandoEntrenarArtista;
import Menu.ComandoRolesFaltantesPorCancion;
import Menu.ComandoRolesTodasLasCanciones;
import Menu.MenuPrincipal;
import Recital.Recital;
import Repositorios.RepositorioArtistasMemory;
import Repositorios.RolCatalogoMemory;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;

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
            System.out.println("\n" + "=".repeat(60));
            System.out.println("   🎵  SISTEMA DE GESTIÓN DE RECITALES  🎵");
            System.out.println("=".repeat(60));
            System.out.println("\n⏳ Cargando datos del recital...");
            
            Recital recital = fabrica.crearRecital();
            RepositorioArtistasMemory repositorio = fabrica.crearRepositorioArtistas();
            RolCatalogoMemory rolCatalogo = fabrica.construirRoles();
            
            System.out.println("\n✅ ¡Recital cargado exitosamente!");
            System.out.println("\n" + "-".repeat(60));
            System.out.println("   📊 ESTADÍSTICAS DEL SISTEMA");
            System.out.println("-".repeat(60));
            System.out.println(String.format("   🎼 Canciones en repertorio: %d", recital.getCanciones().size()));
            System.out.println(String.format("   🎤 Artistas de discográfica: %d", repositorio.getArtistasDiscografica().size()));
            System.out.println(String.format("   🎸 Artistas externos: %d", repositorio.getArtistasExternos().size()));
            System.out.println("-".repeat(60) + "\n");
            
            // Crear servicios
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            ServicioConsulta servicioConsulta = new ServicioConsulta(repositorio, recital, rolCatalogo);
            ServicioEntrenamiento servicioEntrenamiento = new ServicioEntrenamiento();
            
            // Crear comandos del menú
            List<Comando> comandos = new ArrayList<>();
            comandos.add(new ComandoRolesFaltantesPorCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoRolesTodasLasCanciones(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoContratarArtistaParaCancionX(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoEntrenarArtista(servicioConsulta, servicioContratacion, servicioEntrenamiento));
            
            // Crear y mostrar menú
            MenuPrincipal menu = new MenuPrincipal(comandos);
            menu.mostrar();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
