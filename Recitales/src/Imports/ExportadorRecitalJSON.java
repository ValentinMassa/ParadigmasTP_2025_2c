package Imports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import Recital.*;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.Rol;

/**
 * Exportador para generar un archivo JSON con el estado final del recital
 * incluyendo información de contratación y totalizadores.
 */
public class ExportadorRecitalJSON {
    
    private Recital recital;
    private String rutaSalida;
    
    /**
     * Constructor del exportador.
     * @param recital El recital a exportar
     * @param rutaSalida La ruta donde guardar el archivo (ej: "data/ArchivosOutput/recital-out.json")
     */
    public ExportadorRecitalJSON(Recital recital, String rutaSalida) {
        this.recital = recital;
        this.rutaSalida = rutaSalida;
    }
    
    /**
     * Exporta el estado del recital a un archivo JSON.
     * @throws IOException si hay error al escribir el archivo
     */
    public void exportar() throws IOException {
        try {
            // Crear directorio si no existe
            File archivo = new File(rutaSalida);
            File directorio = archivo.getParentFile();
            if (directorio != null && !directorio.exists()) {
                directorio.mkdirs();
            }
            
            // Generar contenido JSON
            String jsonContent = generarJSON();
            
            // Escribir a archivo
            try (FileWriter writer = new FileWriter(rutaSalida)) {
                writer.write(jsonContent);
            }
            
            System.out.println("[OK] Estado del recital exportado a: " + rutaSalida);
            
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo exportar el recital: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Genera el JSON con la estructura del recital.
     */
    private String generarJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // Información general del recital
        json.append("  \"resumen\": {\n");
        json.append("    \"totalCanciones\": ").append(recital.getCanciones().size()).append(",\n");
        json.append("    \"totalArtistasBase\": ").append(recital.getArtistasBase().size()).append(",\n");
        json.append("    \"totalArtistasExternos\": ").append(recital.getArtistasExternos().size()).append(",\n");
        json.append("    \"totalContratos\": ").append(recital.getContratos().size()).append(",\n");
        json.append("    \"costoTotal\": ").append(String.format("%.2f", recital.getCostoTotalRecital())).append("\n");
        json.append("  },\n");
        
        // Canciones con sus roles y artistas asignados
        json.append("  \"canciones\": [\n");
        List<Cancion> cancionesList = new ArrayList<>(recital.getCanciones());
        for (int i = 0; i < cancionesList.size(); i++) {
            Cancion cancion = cancionesList.get(i);
            json.append("    {\n");
            json.append("      \"titulo\": \"").append(escaparJson(cancion.getTitulo())).append("\",\n");
            
            // Roles requeridos vs cubiertos
            Map<Rol, Integer> rolesRequeridos = cancion.getRolesRequeridos();
            Map<Rol, List<String>> rolesAsignados = new HashMap<>();
            
            for (Contrato contrato : recital.getContratos()) {
                if (contrato.getCancion().equals(cancion)) {
                    Rol rol = contrato.getRol();
                    String artista = contrato.getArtista().getNombre();
                    rolesAsignados.computeIfAbsent(rol, k -> new ArrayList<>()).add(artista);
                }
            }
            
            // Estructura de roles
            json.append("      \"rolesRequeridos\": {\n");
            List<Rol> rolesList = new ArrayList<>(rolesRequeridos.keySet());
            for (int j = 0; j < rolesList.size(); j++) {
                Rol rol = rolesList.get(j);
                int cantidad = rolesRequeridos.get(rol);
                List<String> artistas = rolesAsignados.getOrDefault(rol, new ArrayList<>());
                
                json.append("        \"").append(rol.getNombre()).append("\": {\n");
                json.append("          \"requeridos\": ").append(cantidad).append(",\n");
                json.append("          \"asignados\": ").append(artistas.size()).append(",\n");
                json.append("          \"cubierto\": ").append(artistas.size() >= cantidad ? "true" : "false").append(",\n");
                json.append("          \"artistas\": [");
                for (int k = 0; k < artistas.size(); k++) {
                    json.append("\"").append(escaparJson(artistas.get(k))).append("\"");
                    if (k < artistas.size() - 1) json.append(", ");
                }
                json.append("]\n");
                json.append("        }");
                if (j < rolesList.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("      },\n");
            
            // Costo de la canción
            double costoCancion = recital.getCostosPorCancion().getOrDefault(cancion, 0.0);
            json.append("      \"costo\": ").append(String.format("%.2f", costoCancion)).append("\n");
            json.append("    }");
            if (i < cancionesList.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // Artistas contratados con sus detalles
        json.append("  \"artistas\": {\n");
        json.append("    \"base\": [\n");
        
        List<ArtistaBase> artistasBase = new ArrayList<>(recital.getArtistasBase());
        for (int i = 0; i < artistasBase.size(); i++) {
            ArtistaBase artista = artistasBase.get(i);
            json.append("      {\n");
            json.append("        \"nombre\": \"").append(escaparJson(artista.getNombre())).append("\",\n");
            json.append("        \"tipo\": \"ArtistaBase\",\n");
            json.append("        \"costo\": ").append(String.format("%.2f", artista.getCosto())).append(",\n");
            json.append("        \"maxCanciones\": ").append(artista.getMaxCanciones()).append(",\n");
            json.append("        \"cancionesAsignadas\": ").append(artista.getCantCancionesAsignado()).append(",\n");
            json.append("        \"roles\": [");
            List<Rol> roles = new ArrayList<>(artista.getRoles());
            for (int j = 0; j < roles.size(); j++) {
                json.append("\"").append(escaparJson(roles.get(j).getNombre())).append("\"");
                if (j < roles.size() - 1) json.append(", ");
            }
            json.append("]\n");
            json.append("      }");
            if (i < artistasBase.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("    ],\n");
        json.append("    \"externos\": [\n");
        
        List<ArtistaExterno> artistasExternos = new ArrayList<>(recital.getArtistasExternos());
        for (int i = 0; i < artistasExternos.size(); i++) {
            ArtistaExterno artista = artistasExternos.get(i);
            
            // Contar cuántas veces está contratado
            int contratosDelArtista = 0;
            for (Contrato c : recital.getContratos()) {
                if (c.getArtista().equals(artista)) {
                    contratosDelArtista++;
                }
            }
            
            json.append("      {\n");
            json.append("        \"nombre\": \"").append(escaparJson(artista.getNombre())).append("\",\n");
            json.append("        \"tipo\": \"ArtistaExterno\",\n");
            json.append("        \"costo\": ").append(String.format("%.2f", artista.getCosto())).append(",\n");
            json.append("        \"maxCanciones\": ").append(artista.getMaxCanciones()).append(",\n");
            json.append("        \"cancionesAsignadas\": ").append(artista.getCantCancionesAsignado()).append(",\n");
            json.append("        \"contratado\": ").append(contratosDelArtista > 0 ? "true" : "false").append(",\n");
            json.append("        \"contratosAsignados\": ").append(contratosDelArtista).append(",\n");
            json.append("        \"roles\": [");
            List<Rol> roles = new ArrayList<>(artista.getRoles());
            for (int j = 0; j < roles.size(); j++) {
                json.append("\"").append(escaparJson(roles.get(j).getNombre())).append("\"");
                if (j < roles.size() - 1) json.append(", ");
            }
            json.append("]\n");
            json.append("      }");
            if (i < artistasExternos.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("    ]\n");
        json.append("  },\n");
        
        // Contratos detallados
        json.append("  \"contratos\": [\n");
        List<Contrato> contratos = recital.getContratos();
        for (int i = 0; i < contratos.size(); i++) {
            Contrato contrato = contratos.get(i);
            json.append("    {\n");
            json.append("      \"artista\": \"").append(escaparJson(contrato.getArtista().getNombre())).append("\",\n");
            json.append("      \"cancion\": \"").append(escaparJson(contrato.getCancion().getTitulo())).append("\",\n");
            json.append("      \"rol\": \"").append(escaparJson(contrato.getRol().getNombre())).append("\",\n");
            json.append("      \"costo\": ").append(String.format("%.2f", contrato.obtenerCostoContrato())).append("\n");
            json.append("    }");
            if (i < contratos.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // Totalizadores
        json.append("  \"totalizadores\": {\n");
        json.append("    \"costoTotalRecital\": ").append(String.format("%.2f", recital.getCostoTotalRecital())).append(",\n");
        json.append("    \"costosPorArtista\": {\n");
        Map<Artista, Double> costosPorArtista = recital.getCostosPorArtista();
        List<Map.Entry<Artista, Double>> costosOrdenados = new ArrayList<>(costosPorArtista.entrySet());
        for (int i = 0; i < costosOrdenados.size(); i++) {
            Map.Entry<Artista, Double> entry = costosOrdenados.get(i);
            json.append("      \"").append(escaparJson(entry.getKey().getNombre())).append("\": ")
                .append(String.format("%.2f", entry.getValue()));
            if (i < costosOrdenados.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("    },\n");
        json.append("    \"costosPorCancion\": {\n");
        Map<Cancion, Double> costosPorCancion = recital.getCostosPorCancion();
        List<Map.Entry<Cancion, Double>> costosCancion = new ArrayList<>(costosPorCancion.entrySet());
        for (int i = 0; i < costosCancion.size(); i++) {
            Map.Entry<Cancion, Double> entry = costosCancion.get(i);
            json.append("      \"").append(escaparJson(entry.getKey().getTitulo())).append("\": ")
                .append(String.format("%.2f", entry.getValue()));
            if (i < costosCancion.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("    },\n");
        
        // Roles faltantes
        Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantes();
        json.append("    \"rolesFaltantes\": ");
        if (rolesFaltantes.isEmpty()) {
            json.append("{}");
        } else {
            json.append("{\n");
            List<Map.Entry<Rol, Integer>> rolesFaltantesList = new ArrayList<>(rolesFaltantes.entrySet());
            for (int i = 0; i < rolesFaltantesList.size(); i++) {
                Map.Entry<Rol, Integer> entry = rolesFaltantesList.get(i);
                json.append("      \"").append(escaparJson(entry.getKey().getNombre())).append("\": ")
                    .append(entry.getValue());
                if (i < rolesFaltantesList.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("    }");
        }
        json.append("\n");
        json.append("  }\n");
        
        json.append("}\n");
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales en JSON.
     */
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
