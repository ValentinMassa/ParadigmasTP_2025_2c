package DataExport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Recital.Recital;
import Recital.Cancion;
import Recital.Contrato;
import Recital.Rol;
import Recital.Banda;
import Artista.ArtistaExterno;
import Artista.ArtistaDiscografica;
import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Repositorios.RepositorioArtistasMemory;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportadorSnapshotCompleto {

    @SuppressWarnings("unused")
    private static class SnapshotCompleto {
        String fechaExportacion;
        String timestampExportacion;
        RecitalSnapshot recital;
        List<ArtistaSnapshot> artistas;
        List<BandaSnapshot> bandas;
        List<RolSnapshot> roles;
        List<ContratoSnapshot> contratos;
    }

    @SuppressWarnings("unused")
    private static class RecitalSnapshot {
        List<CancionSnapshot> canciones;
        int totalCanciones;
    }

    @SuppressWarnings("unused")
    private static class CancionSnapshot {
        String titulo;
        Map<String, Integer> rolesRequeridos; // nombre del rol -> cantidad
    }

    @SuppressWarnings("unused")
    private static class ArtistaSnapshot {
        String nombre;
        String tipo; // "EXTERNO" o "DISCOGRAFICA"
        double costo;
        int maxCanciones;
        int cancionesAsignadas;
        List<String> rolesHistoricos;
        List<String> rolesEntrenados; // solo para artistas externos
        List<String> bandasHistoricas;
    }

    @SuppressWarnings("unused")
    private static class BandaSnapshot {
        String nombre;
    }

    @SuppressWarnings("unused")
    private static class RolSnapshot {
        String nombre;
    }

    @SuppressWarnings("unused")
    private static class ContratoSnapshot {
        String cancion;
        String artista;
        String rol;
        double costo;
    }

    public void exportarSnapshotCompleto(
            Recital recital, 
            RepositorioArtistasMemory repoArtistas,
            Set<Banda> bandas,
            Set<Rol> roles,
            ServicioConsulta servC, 
            ServicioContratacion servCon, 
            String rutaSalida) {
        
        try {
            SnapshotCompleto snapshot = construirSnapshotCompleto(
                recital, repoArtistas, bandas, roles, servC, servCon
            );
            
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            
            String json = gson.toJson(snapshot);
            
            try (FileWriter writer = new FileWriter(rutaSalida)) {
                writer.write(json);
            }
            
            System.out.println("\n[OK] Snapshot completo exportado exitosamente a: " + rutaSalida);
            mostrarResumenSnapshot(snapshot);
            
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo exportar el snapshot: " + e.getMessage());
        }
    }
    
    /**
     * Genera un nombre de archivo con timestamp para el snapshot
     * @param carpetaOutput Ruta de la carpeta donde se guardará (ej: "data/Output")
     * @return Ruta completa del archivo con timestamp
     */
    public static String generarRutaConTimestamp(String carpetaOutput) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String timestamp = sdf.format(new Date());
        return carpetaOutput + "/snapshot_completo_" + timestamp + ".json";
    }

    private SnapshotCompleto construirSnapshotCompleto(
            Recital recital,
            RepositorioArtistasMemory repoArtistas,
            Set<Banda> bandas,
            Set<Rol> roles,
            ServicioConsulta servC,
            ServicioContratacion servCon) {
        
        SnapshotCompleto snapshot = new SnapshotCompleto();
        
        // Fecha y timestamp
        Date ahora = new Date();
        snapshot.fechaExportacion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ahora);
        snapshot.timestampExportacion = String.valueOf(ahora.getTime());
        
        snapshot.recital = exportarRecital(recital);
        snapshot.artistas = exportarArtistas(repoArtistas);
        snapshot.bandas = exportarBandas(bandas);
        snapshot.roles = exportarRoles(roles);
        snapshot.contratos = exportarContratos(servCon);
        
        return snapshot;
    }

    private RecitalSnapshot exportarRecital(Recital recital) {
        RecitalSnapshot recitalSnap = new RecitalSnapshot();
        recitalSnap.canciones = new ArrayList<>();
        
        for (Cancion cancion : recital.getCanciones()) {
            CancionSnapshot cancionSnap = new CancionSnapshot();
            cancionSnap.titulo = cancion.getTitulo();
            cancionSnap.rolesRequeridos = new HashMap<>();
            
            for (Map.Entry<Rol, Integer> entry : cancion.getRolesRequeridos().entrySet()) {
                cancionSnap.rolesRequeridos.put(entry.getKey().getNombre(), entry.getValue());
            }
            
            recitalSnap.canciones.add(cancionSnap);
        }
        
        recitalSnap.totalCanciones = recitalSnap.canciones.size();
        return recitalSnap;
    }

    private List<ArtistaSnapshot> exportarArtistas(RepositorioArtistasMemory repoArtistas) {
        List<ArtistaSnapshot> artistasSnap = new ArrayList<>();
        
        // Exportar artistas de discográfica
        for (ArtistaDiscografica artista : repoArtistas.getArtistasDiscografica()) {
            ArtistaSnapshot artistaSnap = new ArtistaSnapshot();
            artistaSnap.nombre = artista.getNombre();
            artistaSnap.costo = artista.getCosto();
            artistaSnap.maxCanciones = artista.getMaxCanciones();
            artistaSnap.cancionesAsignadas = artista.getCantCancionesAsignadas();
            artistaSnap.tipo = "DISCOGRAFICA";
            
            // Roles históricos
            artistaSnap.rolesHistoricos = new ArrayList<>();
            for (Rol rol : artista.getRoles()) {
                artistaSnap.rolesHistoricos.add(rol.getNombre());
            }
            
            // Bandas históricas
            artistaSnap.bandasHistoricas = new ArrayList<>();
            for (Banda banda : artista.getBandas()) {
                artistaSnap.bandasHistoricas.add(banda.getNombre());
            }
            
            artistaSnap.rolesEntrenados = new ArrayList<>(); // vacío para discográfica
            artistasSnap.add(artistaSnap);
        }
        
        // Exportar artistas externos
        for (ArtistaExterno artista : repoArtistas.getArtistasExternos()) {
            ArtistaSnapshot artistaSnap = new ArtistaSnapshot();
            artistaSnap.nombre = artista.getNombre();
            artistaSnap.costo = artista.getCosto();
            artistaSnap.maxCanciones = artista.getMaxCanciones();
            artistaSnap.cancionesAsignadas = artista.getCantCancionesAsignadas();
            artistaSnap.tipo = "EXTERNO";
            
            // Bandas históricas
            artistaSnap.bandasHistoricas = new ArrayList<>();
            for (Banda banda : artista.getBandas()) {
                artistaSnap.bandasHistoricas.add(banda.getNombre());
            }
            
            // Usar métodos específicos para separar roles históricos de entrenados
            artistaSnap.rolesHistoricos = new ArrayList<>();
            for (Rol rol : artista.getRolesHistoricos()) {
                artistaSnap.rolesHistoricos.add(rol.getNombre());
            }
            
            artistaSnap.rolesEntrenados = new ArrayList<>();
            for (Rol rol : artista.getRolesEntrenados()) {
                artistaSnap.rolesEntrenados.add(rol.getNombre());
            }
            
            artistasSnap.add(artistaSnap);
        }
        
        return artistasSnap;
    }

    private List<BandaSnapshot> exportarBandas(Set<Banda> bandas) {
        List<BandaSnapshot> bandasSnap = new ArrayList<>();
        
        for (Banda banda : bandas) {
            BandaSnapshot bandaSnap = new BandaSnapshot();
            bandaSnap.nombre = banda.getNombre();
            bandasSnap.add(bandaSnap);
        }
        
        return bandasSnap;
    }

    private List<RolSnapshot> exportarRoles(Set<Rol> roles) {
        List<RolSnapshot> rolesSnap = new ArrayList<>();
        
        for (Rol rol : roles) {
            RolSnapshot rolSnap = new RolSnapshot();
            rolSnap.nombre = rol.getNombre();
            rolesSnap.add(rolSnap);
        }
        
        return rolesSnap;
    }

    private List<ContratoSnapshot> exportarContratos(ServicioContratacion servCon) {
        List<ContratoSnapshot> contratosSnap = new ArrayList<>();
        
        for (Contrato contrato : servCon.getContratos()) {
            ContratoSnapshot contratoSnap = new ContratoSnapshot();
            contratoSnap.cancion = contrato.getCancion().getTitulo();
            contratoSnap.artista = contrato.getArtista().getNombre();
            contratoSnap.rol = contrato.getRol().getNombre();
            contratoSnap.costo = contrato.getCosto();
            contratosSnap.add(contratoSnap);
        }
        
        return contratosSnap;
    }

    private void mostrarResumenSnapshot(SnapshotCompleto snapshot) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          >> RESUMEN DEL SNAPSHOT COMPLETO <<");
        System.out.println("=".repeat(60));
        System.out.println(String.format("   Fecha exportación:     %s", snapshot.fechaExportacion));
        System.out.println("\n   --- RECITAL ---");
        System.out.println(String.format("   Canciones totales:     %d", snapshot.recital.totalCanciones));
        System.out.println("\n   --- ARTISTAS ---");
        System.out.println(String.format("   Total artistas:        %d", snapshot.artistas.size()));
        int externos = 0, discografica = 0;
        for (ArtistaSnapshot a : snapshot.artistas) {
            if ("EXTERNO".equals(a.tipo)) externos++;
            else if ("DISCOGRAFICA".equals(a.tipo)) discografica++;
        }
        System.out.println(String.format("   - Externos:            %d", externos));
        System.out.println(String.format("   - Discográfica:        %d", discografica));
        System.out.println("\n   --- RECURSOS ---");
        System.out.println(String.format("   Total bandas:          %d", snapshot.bandas.size()));
        System.out.println(String.format("   Total roles:           %d", snapshot.roles.size()));
        System.out.println(String.format("   Total contratos:       %d", snapshot.contratos.size()));
        System.out.println("=".repeat(60) + "\n");
    }
}
