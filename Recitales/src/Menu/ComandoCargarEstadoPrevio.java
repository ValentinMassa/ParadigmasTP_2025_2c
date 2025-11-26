package Menu;

import java.util.List;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;

import DataLoader.JsonLoaderEstadoPrevio;
import Menu.Auxiliares.SelectorDeOpcion;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoCargarEstadoPrevio implements Comando {
    
    private ServicioConsulta servicioConsulta;
    private ServicioContratacion servicioContratacion;
    private JsonLoaderEstadoPrevio.SnapshotCompleto snapshotCargado;
    private String baseDir;
    
    public ComandoCargarEstadoPrevio(ServicioConsulta servicioConsulta, ServicioContratacion servicioContratacion, String baseDir){
        if(servicioConsulta == null || servicioContratacion == null || baseDir == null){
            throw new IllegalArgumentException("ServicioConsulta no puede ser nulo");
        }
        this.servicioConsulta = servicioConsulta;
        this.snapshotCargado = null;
        this.servicioContratacion = servicioContratacion;
        this.baseDir = baseDir;
    } 

    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        
        // listamos arch. disponibles
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         CARGAR ESTADO PREVIO DEL RECITAL");
        System.out.println("=".repeat(60));
        System.out.println("\nListando snapshots disponibles...\n");
        
        List<String> archivos = JsonLoaderEstadoPrevio.listarArchivosEstadoPrevio(baseDir + "/data/Snapshots");
        
        if (archivos.isEmpty()) {
            System.out.println("[!] No se encontraron archivos de snapshot en la carpeta Snapshots.");
            return;
        }
        
        HashMap<Integer, String> mapaArchivos = new HashMap<>();
        for (int i = 0; i < archivos.size(); i++) {
            System.out.println((i + 1) + ". " + archivos.get(i));
            mapaArchivos.put(i + 1, archivos.get(i));
        }
        
        String archivoSeleccionado;
        try {
            archivoSeleccionado = SelectorDeOpcion.seleccionarDeLista(
                mapaArchivos, 
                "\nSeleccione el número del archivo a cargar (S para cancelar): ", 
                scanner
            );
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        
        if (archivoSeleccionado == null) {
            return;
        }
        
        String rutaCompleta = baseDir + "/data/Snapshots/" + archivoSeleccionado;
        
        // Cargar el snapshot completo
        System.out.println("\n[*] Cargando snapshot desde: " + archivoSeleccionado);
        
        try {
            JsonLoaderEstadoPrevio loader = new JsonLoaderEstadoPrevio();
            snapshotCargado = loader.cargarSnapshotCompleto(rutaCompleta);
            
            // Actualizar el ServicioConsulta con los nuevos objetos
            servicioConsulta.actualizarDesdeSnapshot(
                snapshotCargado.repositorioArtistas,
                snapshotCargado.recital,
                snapshotCargado.rolCatalogo,
                snapshotCargado.bandaCatalogo
            );

            servicioContratacion.actualizarDesdeSnapshot(
                snapshotCargado.servicioContratacion.getContratos()
            );
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("        SNAPSHOT CARGADO Y APLICADO EXITOSAMENTE");
            System.out.println("=".repeat(60));
            System.out.println(" Fecha de exportación: " + snapshotCargado.fechaExportacion);
            System.out.println(" Canciones en repertorio: " + snapshotCargado.recital.getCanciones().size());
            System.out.println(" Artistas de discográfica: " + snapshotCargado.repositorioArtistas.getArtistasDiscografica().size());
            System.out.println(" Artistas externos: " + snapshotCargado.repositorioArtistas.getArtistasExternos().size());
            System.out.println(" Total de contratos: " + snapshotCargado.servicioContratacion.getContratos().size());
            System.out.println("=".repeat(60));
            
            System.out.println("\n[OK] El sistema ha sido restaurado al estado del snapshot.");
            System.out.println("[!] IMPORTANTE: Todos los cambios no guardados han sido reemplazados.");
            
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo cargar el archivo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ERROR] Error al procesar el snapshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getDescripcion() {
        return "Cargar snapshot completo del recital";
    }
    
    public JsonLoaderEstadoPrevio.SnapshotCompleto getSnapshotCargado() {
        return snapshotCargado;
    }
}
