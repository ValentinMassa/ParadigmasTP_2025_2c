import DataLoader.FabricaRecital;
import DataLoader.JsonAdapter;
import Menu.Comando;
import Menu.ComandoContratarArtistaParaCancionX;
import Menu.ComandoContratarArtistas;
import Menu.ComandoEntrenarArtista;
import Menu.ComandoRolesFaltantesPorCancion;
import Menu.ComandoRolesTodasLasCanciones;
import Menu.MenuPrincipal;
import Recital.Recital;
import Repositorios.BandaCatalogoMemory;
import Repositorios.RepositorioArtistasMemory;
import Repositorios.RolCatalogoMemory;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;

import java.util.ArrayList;
import java.util.List;

import DataExport.ExportadorRecital;
import Menu.ComandoHacerSnapshot;
import Menu.ComandoHistorialDeColaboraciones;
import Menu.ComandoCargarEstadoPrevio;
import Menu.ComandoListarEstadoCancion;
import Menu.ComandoProlog;
import Menu.ComandoListarArtistasContratados;
import Menu.ComandoArrepentimiento;

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
            System.out.println("     >> SISTEMA DE GESTION DE RECITALES <<");
            System.out.println("=".repeat(60));
            System.out.println("\n[*] Cargando datos del recital...");
            
            Recital recital = fabrica.crearRecital();
            RepositorioArtistasMemory repositorio = fabrica.crearRepositorioArtistas();
            RolCatalogoMemory rolCatalogo = fabrica.construirRoles();
            BandaCatalogoMemory bandaCatalogo = fabrica.construirBandas();
            
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
            comandos.add(new ComandoContratarArtistaParaCancionX(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoContratarArtistas(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoEntrenarArtista(servicioConsulta, servicioContratacion, servicioEntrenamiento));
            comandos.add(new ComandoListarArtistasContratados(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoListarEstadoCancion(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoProlog(servicioConsulta, servicioContratacion));
            
            comandos.add(new ComandoArrepentimiento(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoHacerSnapshot(servicioConsulta,servicioContratacion));
            comandos.add(new ComandoCargarEstadoPrevio(servicioConsulta, servicioContratacion));
            comandos.add(new ComandoHistorialDeColaboraciones(servicioConsulta));
            
            
            
            
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
