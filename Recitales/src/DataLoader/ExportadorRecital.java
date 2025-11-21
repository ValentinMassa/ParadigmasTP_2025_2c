package DataLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Recital.Recital;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Recital.Cancion;
import Recital.Contrato;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExportadorRecital {

    @SuppressWarnings("unused")
    private static class EstadoRecitalDTO {
        String fechaExportacion;
        List<CancionEstadoDTO> canciones;
        TotalizadoresDTO totalizadores;
    }
    
    @SuppressWarnings("unused")
    private static class CancionEstadoDTO {
        String nombre;
        List<ContratoDTO> contratos;
        int rolesRequeridos;
        int rolesContratados;
        int rolesRestantes;
        boolean estadoCompleto;
        double costoTotal;
    }
    
    @SuppressWarnings("unused")
    private static class ContratoDTO {
        String artista;
        String rol;
        double costo;
    }
    
    @SuppressWarnings("unused")
    private static class TotalizadoresDTO {
        int totalCanciones;
        int cancionesCompletas;
        int cancionesIncompletas;
        double porcentajeCompletitud;
        int totalContratos;
        double costoTotalRecital;
    }


    public void exportarEstadoRecital(Recital recital,ServicioConsulta servC, ServicioContratacion servCon, String rutaSalida) {
        try {
            EstadoRecitalDTO estado = construirEstadoRecital(recital, servC, servCon);
            
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            
            String json = gson.toJson(estado);
            
            try (FileWriter writer = new FileWriter(rutaSalida)) {
                writer.write(json);
            }
            
            System.out.println("\n[OK] Estado del recital exportado exitosamente a: " + rutaSalida);
            mostrarResumen(estado);
            
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo exportar el estado del recital: " + e.getMessage());
        }
    }
    
    private EstadoRecitalDTO construirEstadoRecital(Recital recital,ServicioConsulta servC, ServicioContratacion servCon) {
        EstadoRecitalDTO estado = new EstadoRecitalDTO();
        estado.fechaExportacion = new Date().toString();
        estado.canciones = new ArrayList<>();
        estado.totalizadores = new TotalizadoresDTO();
        
        double costoTotal = 0;
        int totalContratos = 0;
        int cancionesCompletas = 0;
        int cancionesIncompletas = 0;
        
        for (Cancion cancion : recital.getCanciones()) {
            CancionEstadoDTO cancionDTO = new CancionEstadoDTO();
            cancionDTO.nombre = cancion.getTitulo();
            cancionDTO.contratos = new ArrayList<>();
            
            Set<String> rolesContratados = new HashSet<>();
            double costoPorCancion = 0;
            
            // Obtener solo los contratos de esta canción específica
            for (Contrato contrato : servCon.getContratosPorCancion(cancion)) {
                ContratoDTO contratoDTO = new ContratoDTO();
                contratoDTO.artista = contrato.getArtista().getNombre();
                contratoDTO.rol = contrato.getRol().getNombre();
                contratoDTO.costo = contrato.getCosto();
                
                cancionDTO.contratos.add(contratoDTO);
                rolesContratados.add(contrato.getRol().getNombre());
                costoPorCancion += contrato.getCosto();
                totalContratos++;
            }
            
            cancionDTO.costoTotal = costoPorCancion;
            cancionDTO.rolesRequeridos = cancion.getRolesRequeridos().size();
            cancionDTO.rolesContratados = rolesContratados.size();
            cancionDTO.rolesRestantes = cancionDTO.rolesRequeridos - cancionDTO.rolesContratados;
            cancionDTO.estadoCompleto = (cancionDTO.rolesRestantes == 0);
            
            if (cancionDTO.estadoCompleto) {
                cancionesCompletas++;
            } else {
                cancionesIncompletas++;
            }
            
            costoTotal += costoPorCancion;
            estado.canciones.add(cancionDTO);
        }
        
        // Llenar totalizadores
        estado.totalizadores.totalCanciones = recital.getCanciones().size();
        estado.totalizadores.cancionesCompletas = cancionesCompletas;
        estado.totalizadores.cancionesIncompletas = cancionesIncompletas;
        estado.totalizadores.porcentajeCompletitud = (double) cancionesCompletas / estado.totalizadores.totalCanciones * 100;
        estado.totalizadores.totalContratos = totalContratos;
        estado.totalizadores.costoTotalRecital = costoTotal;
        
        return estado;
    }
    
    private void mostrarResumen(EstadoRecitalDTO estado) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          >> RESUMEN DE EXPORTACION <<");
        System.out.println("=".repeat(60));
        System.out.println(String.format("   Canciones totales:     %d", estado.totalizadores.totalCanciones));
        System.out.println(String.format("   Canciones completas:   %d", estado.totalizadores.cancionesCompletas));
        System.out.println(String.format("   Canciones incompletas: %d", estado.totalizadores.cancionesIncompletas));
        System.out.println(String.format("   Completitud:           %.1f%%", estado.totalizadores.porcentajeCompletitud));
        System.out.println(String.format("   Total contratos:       %d", estado.totalizadores.totalContratos));
        System.out.println(String.format("   Costo total:           $%.2f", estado.totalizadores.costoTotalRecital));
        System.out.println("=".repeat(60) + "\n");
    }
}