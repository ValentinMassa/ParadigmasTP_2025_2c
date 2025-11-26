package Menu;

import DataExport.ExportadorSnapshotCompleto;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoHacerSnapshot implements Comando {
     
    private ServicioConsulta servC;
    private ServicioContratacion servContr;
    private String baseDir;
    
    public ComandoHacerSnapshot(
            ServicioConsulta servC,
            ServicioContratacion servContr,
            String baseDir) {
        
        if(servC == null || servContr == null || baseDir == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        
        this.servC = servC;
        this.servContr = servContr;
        this.baseDir = baseDir;
    } 

    public void ejecutar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           GUARDAR SNAPSHOT DEL RECITAL");
        System.out.println("=".repeat(60));
        System.out.println("\n[*] Generando snapshot completo del estado actual...\n");
        
        try {
            String rutaArchivo = ExportadorSnapshotCompleto.generarRutaConTimestamp(baseDir + "/data/Snapshots");
            
            // Crear exportador y generar snapshot
            ExportadorSnapshotCompleto exportador = new ExportadorSnapshotCompleto();
            exportador.exportarSnapshotCompleto(
                servC.getRecital(), 
                servC.getRepositorioArtistas(), 
                servC.getTodasLasBandas(), 
                servC.getTodosLosRoles(), 
                servC, 
                servContr, 
                rutaArchivo
            );
            
            System.out.println("\n[✓] Snapshot guardado exitosamente!");
            System.out.println("    Archivo: " + rutaArchivo);
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] No se pudo crear el snapshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getDescripcion() {
        return "Guardar snapshot completo del estado actual";
    }
}
