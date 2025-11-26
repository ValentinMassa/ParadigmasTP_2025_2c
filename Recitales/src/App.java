import DataLoader.FabricaRecital;
import DataLoader.Adapters.JsonAdapter;
import DataLoader.Adapters.XmlAdapter;
import DataLoader.ICargarRecital;
import Menu.Comando;
import Menu.ComandoContratarArtistaParaCancionX;
import Menu.ComandoContratarArtistas;
import Menu.ComandoEntrenarArtista;
import Menu.ComandoRolesFaltantesPorCancion;
import Menu.ComandoRolesTodasLasCanciones;
import Menu.MenuPrincipal;
import Recital.Recital;
import Repositorios.RepositorioBandas;
import Repositorios.RepositorioArtistas;
import Repositorios.RepositorioRoles;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.net.URISyntaxException;

import DataExport.ExportadorRecital;
import Menu.ComandoHacerSnapshot;
import Menu.ComandoHistorialDeColaboraciones;
import Menu.ComandoCargarEstadoPrevio;
import Menu.ComandoListarEstadoCancion;
import Menu.ComandoProlog;
import Menu.ComandoListarArtistasContratados;
import Menu.ComandoArrepentimiento;

public class App {
    private static ICargarRecital seleccionarAdaptador(String baseDir, Scanner scanner) {
        // Preguntar al usuario el formato de datos
        System.out.println("Seleccione el formato de datos:");
        System.out.println("1. JSON");
        System.out.println("2. XML");
        System.out.print("Ingrese el número: ");
        int opcion = scanner.nextInt();
        
        String rutaArtistas;
        String rutaCanciones;
        String rutaArtistasBase;
        
        ICargarRecital adapter;
        
        if (opcion == 2) {
            rutaArtistas = baseDir + "/data/XML/artistas.xml";
            rutaCanciones = baseDir + "/data/XML/recital.xml";
            rutaArtistasBase = baseDir + "/data/XML/artistas-discografica.xml";
            adapter = new XmlAdapter(rutaArtistas, rutaCanciones, rutaArtistasBase);
        } else {
            // Por defecto JSON
            if (opcion != 1) {
                System.out.println("Opción inválida, usando JSON por defecto.");
            }
            rutaArtistas = baseDir + "/data/Json/artistas.json";
            rutaCanciones = baseDir + "/data/Json/recital.json";
            rutaArtistasBase = baseDir + "/data/Json/artistas-discografica.json";
            adapter = new JsonAdapter(rutaArtistas, rutaCanciones, rutaArtistasBase);
        }
        
        return adapter;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String baseDir;
            try {
                File jarFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File binDir = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
                baseDir = binDir.getParent();
            } catch (URISyntaxException e) {
                baseDir = System.getProperty("user.dir");
                if (!baseDir.endsWith("Recitales")) {
                    baseDir = baseDir + "/Recitales";
                }
            }
            
            ICargarRecital adapter = seleccionarAdaptador(baseDir, scanner);
            
            // Crear fábrica de recital
            FabricaRecital fabrica = new FabricaRecital(adapter);
            
            // Crear recital y cargar datos
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     >> SISTEMA DE GESTION DE RECITALES <<");
            System.out.println("=".repeat(60));
            System.out.println("\n[*] Cargando datos del recital...");
            
            Recital recital = fabrica.crearRecital();
            RepositorioArtistas repositorio = fabrica.crearRepositorioArtistas();
            RepositorioRoles rolCatalogo = fabrica.construirRoles();
            RepositorioBandas bandaCatalogo = fabrica.construirBandas();
            
            System.out.println("\n[OK] Recital cargado exitosamente!");
            System.out.println("\n" + "-".repeat(60));
            System.out.println("          ESTADISTICAS DEL SISTEMA");
            System.out.println("-".repeat(60));
            System.out.println(String.format("   >> Canciones en repertorio: %d", recital.getCanciones().size()));
            System.out.println(String.format("   >> Artistas de discografica: %d", repositorio.getArtistasDiscografica().size()));
            System.out.println(String.format("   >> Artistas externos: %d", repositorio.getArtistasExternos().size()));
            System.out.println("-".repeat(60) + "\n");
            
            // Crear servicios
            ServicioContratacion servicioContratacion = new ServicioContratacion();
            ServicioConsulta servicioConsulta = new ServicioConsulta(repositorio, recital, rolCatalogo, bandaCatalogo);
            ServicioEntrenamiento servicioEntrenamiento = new ServicioEntrenamiento();
            
            // Crear comandos del menú
            List<Comando> comandos = new ArrayList<>();

            comandos.add(new ComandoRolesFaltantesPorCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoRolesTodasLasCanciones(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoContratarArtistaParaCancionX(servicioConsulta, servicioContratacion, servicioEntrenamiento));
            comandos.add(new ComandoContratarArtistas(servicioConsulta, servicioContratacion, servicioEntrenamiento));
            comandos.add(new ComandoEntrenarArtista(servicioConsulta, servicioContratacion, servicioEntrenamiento));
            comandos.add(new ComandoListarArtistasContratados(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoListarEstadoCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoProlog(servicioConsulta, servicioContratacion, baseDir));
            
            comandos.add(new ComandoArrepentimiento(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoHacerSnapshot(servicioConsulta,servicioContratacion, baseDir));
            comandos.add(new ComandoCargarEstadoPrevio(servicioConsulta, servicioContratacion, baseDir));
            comandos.add(new ComandoHistorialDeColaboraciones(servicioConsulta, servicioContratacion));
            
            
            
            
            // Crear y mostrar menú
            MenuPrincipal menu = new MenuPrincipal(comandos);
            menu.mostrar();
            
            // Exportar estado al salir si el usuario proporcionó un nombre
            String nombreArchivo = menu.getNombreArchivoSalida();
            if (nombreArchivo != null) {
                String rutaSalida = baseDir + "/data/Output/" + nombreArchivo + ".json";
                ExportadorRecital exportador = new ExportadorRecital();
                exportador.exportarEstadoRecital(recital, servicioConsulta, servicioContratacion, rutaSalida);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
