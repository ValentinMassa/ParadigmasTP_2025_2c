package DataLoader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import Artista.*;
import Recital.*;
import Repositorios.*;
import Servicios.*;

public class JsonLoaderEstadoPrevio {
    
    public static class SnapshotCompleto {
        public String fechaExportacion;
        public String timestampExportacion;
        public Recital recital;
        public RepositorioArtistas repositorioArtistas;
        public RepositorioRoles rolCatalogo;
        public RepositorioBandas bandaCatalogo;
        public ServicioContratacion servicioContratacion;
        public List<ContratoSnapshot> contratos;
        
        public SnapshotCompleto() {
            this.contratos = new ArrayList<>();
        }
    }
    
    public static class ContratoSnapshot {
        public String cancion;
        public String artista;
        public String rol;
        public double costo;
        
        public ContratoSnapshot(String cancion, String artista, String rol, double costo) {
            this.cancion = cancion;
            this.artista = artista;
            this.rol = rol;
            this.costo = costo;
        }
    }
    
    /**
     * Carga un snapshot completo del sistema desde un archivo JSON.
     * Restaura todos los objetos: recital, artistas, roles, bandas y contratos.
     * @param rutaArchivo Ruta al archivo snapshot JSON
     * @return SnapshotCompleto con todos los datos restaurados
     * @throws IOException Si hay un error leyendo el archivo
     */
    public SnapshotCompleto cargarSnapshotCompleto(String rutaArchivo) throws IOException {
        SnapshotCompleto snapshot = new SnapshotCompleto();
        
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            
            // 1. Cargar metadatos
            if (jsonObject.has("fechaExportacion")) {
                snapshot.fechaExportacion = jsonObject.get("fechaExportacion").getAsString();
            }
            if (jsonObject.has("timestampExportacion")) {
                snapshot.timestampExportacion = jsonObject.get("timestampExportacion").getAsString();
            }
            
            // 2. Cargar catálogos de roles y bandas primero
            snapshot.rolCatalogo = cargarRoles(jsonObject);
            snapshot.bandaCatalogo = cargarBandas(jsonObject);
            
            // 3. Cargar artistas
            snapshot.repositorioArtistas = cargarArtistas(jsonObject, snapshot.rolCatalogo, snapshot.bandaCatalogo);
            
            // 4. Cargar recital con canciones
            snapshot.recital = cargarRecital(jsonObject, snapshot.rolCatalogo);
            
            // 5. Cargar contratos y reconstruir ServicioContratacion
            snapshot.servicioContratacion = cargarContratos(jsonObject, snapshot.recital, 
                                                           snapshot.repositorioArtistas, 
                                                           snapshot.rolCatalogo);
            
            System.out.println("\n[OK] Snapshot cargado exitosamente desde: " + rutaArchivo);
            System.out.println("     Fecha exportación: " + snapshot.fechaExportacion);
            
        }
        
        return snapshot;
    }
    
    private RepositorioRoles cargarRoles(JsonObject jsonObject) {
        RepositorioRoles catalogo = new RepositorioRoles();
        
        if (jsonObject.has("roles")) {
            JsonArray rolesArray = jsonObject.getAsJsonArray("roles");
            for (JsonElement rolElement : rolesArray) {
                JsonObject rolObj = rolElement.getAsJsonObject();
                String nombreRol = rolObj.get("nombre").getAsString();
                catalogo.agregarRol(nombreRol);
            }
        }
        
        return catalogo;
    }
    
    private RepositorioBandas cargarBandas(JsonObject jsonObject) {
        RepositorioBandas catalogo = new RepositorioBandas();
        
        if (jsonObject.has("bandas")) {
            JsonArray bandasArray = jsonObject.getAsJsonArray("bandas");
            for (JsonElement bandaElement : bandasArray) {
                JsonObject bandaObj = bandaElement.getAsJsonObject();
                String nombreBanda = bandaObj.get("nombre").getAsString();
                catalogo.agregarBanda(nombreBanda);
            }
        }
        
        return catalogo;
    }
    
    private RepositorioArtistas cargarArtistas(JsonObject jsonObject, 
                                                     RepositorioRoles rolCatalogo,
                                                     RepositorioBandas bandaCatalogo) {
        HashSet<ArtistaDiscografica> artistasDiscografica = new HashSet<>();
        HashSet<ArtistaExterno> artistasExternos = new HashSet<>();
        
        if (jsonObject.has("artistas")) {
            JsonArray artistasArray = jsonObject.getAsJsonArray("artistas");
            
            for (JsonElement artistaElement : artistasArray) {
                JsonObject artistaObj = artistaElement.getAsJsonObject();
                
                String nombre = artistaObj.get("nombre").getAsString();
                String tipo = artistaObj.get("tipo").getAsString();
                double costo = artistaObj.get("costo").getAsDouble();
                int maxCanciones = artistaObj.get("maxCanciones").getAsInt();
                int cancionesAsignadas = artistaObj.get("cancionesAsignadas").getAsInt();
                
                // Cargar roles históricos
                HashSet<Rol> rolesHistoricos = new HashSet<>();
                if (artistaObj.has("rolesHistoricos")) {
                    JsonArray rolesArray = artistaObj.getAsJsonArray("rolesHistoricos");
                    for (JsonElement rolElement : rolesArray) {
                        String nombreRol = rolElement.getAsString();
                        Rol rol = rolCatalogo.getRol(nombreRol);
                        if (rol != null) {
                            rolesHistoricos.add(rol);
                        }
                    }
                }
                
                // Cargar bandas históricas
                HashSet<Banda> bandasHistoricas = new HashSet<>();
                if (artistaObj.has("bandasHistoricas")) {
                    JsonArray bandasArray = artistaObj.getAsJsonArray("bandasHistoricas");
                    for (JsonElement bandaElement : bandasArray) {
                        String nombreBanda = bandaElement.getAsString();
                        Banda banda = bandaCatalogo.getBanda(nombreBanda);
                        if (banda != null) {
                            bandasHistoricas.add(banda);
                        }
                    }
                }
                
                if (tipo.equals("DISCOGRAFICA")) {
                    ArtistaDiscografica artista = new ArtistaDiscografica(nombre, maxCanciones, costo);
                    artista.setCantCancionesAsignado(cancionesAsignadas);
                    for (Rol rol : rolesHistoricos) {
                        artista.agregarRolHistorico(rol);
                    }
                    for (Banda banda : bandasHistoricas) {
                        artista.agregarBandaHistorico(banda);
                    }
                    artistasDiscografica.add(artista);
                    
                } else if (tipo.equals("EXTERNO")) {
                    ArtistaExterno artista = new ArtistaExterno(nombre, maxCanciones, costo);
                    artista.setCantCancionesAsignado(cancionesAsignadas);
                    for (Rol rol : rolesHistoricos) {
                        artista.agregarRolHistorico(rol);
                    }
                    for (Banda banda : bandasHistoricas) {
                        artista.agregarBandaHistorico(banda);
                    }
                    
                    // Cargar roles entrenados para artistas externos
                    if (artistaObj.has("rolesEntrenados")) {
                        JsonArray rolesEntrenadosArray = artistaObj.getAsJsonArray("rolesEntrenados");
                        for (JsonElement rolElement : rolesEntrenadosArray) {
                            String nombreRol = rolElement.getAsString();
                            Rol rol = rolCatalogo.getRol(nombreRol);
                            if (rol != null) {
                                artista.agregarRolEntrenado(rol, 1.0);
                            }
                        }
                    }
                    
                    artistasExternos.add(artista);
                }
            }
        }
        
        return new RepositorioArtistas(artistasDiscografica, artistasExternos);
    }
    
    private Recital cargarRecital(JsonObject jsonObject, RepositorioRoles rolCatalogo) {
        HashSet<Cancion> canciones = new HashSet<>();
        
        if (jsonObject.has("recital")) {
            JsonObject recitalObj = jsonObject.getAsJsonObject("recital");
            
            if (recitalObj.has("canciones")) {
                JsonArray cancionesArray = recitalObj.getAsJsonArray("canciones");
                
                for (JsonElement cancionElement : cancionesArray) {
                    JsonObject cancionObj = cancionElement.getAsJsonObject();
                    String titulo = cancionObj.get("titulo").getAsString();
                    
                    Cancion cancion = new Cancion(titulo);
                    
                    // Cargar roles requeridos
                    if (cancionObj.has("rolesRequeridos")) {
                        JsonObject rolesRequeridosObj = cancionObj.getAsJsonObject("rolesRequeridos");
                        
                        for (String nombreRol : rolesRequeridosObj.keySet()) {
                            int cantidad = rolesRequeridosObj.get(nombreRol).getAsInt();
                            if (cantidad > 0) {
                                Rol rol = rolCatalogo.getRol(nombreRol);
                                if (rol != null) {
                                    cancion.agregarRolRequerido(rol, cantidad);
                                }
                            }
                        }
                    }
                    
                    canciones.add(cancion);
                }
            }
        }
        
        return new Recital(canciones);
    }
    
    private ServicioContratacion cargarContratos(JsonObject jsonObject, 
                                                 Recital recital,
                                                 RepositorioArtistas repositorio,
                                                 RepositorioRoles rolCatalogo) {
        ServicioContratacion servicio = new ServicioContratacion();
        
        if (jsonObject.has("contratos")) {
            JsonArray contratosArray = jsonObject.getAsJsonArray("contratos");
            
            for (JsonElement contratoElement : contratosArray) {
                JsonObject contratoObj = contratoElement.getAsJsonObject();
                
                String nombreCancion = contratoObj.get("cancion").getAsString();
                String nombreArtista = contratoObj.get("artista").getAsString();
                String nombreRol = contratoObj.get("rol").getAsString();
                double costo = contratoObj.get("costo").getAsDouble();
                
                Cancion cancion = buscarCancionPorNombre(recital, nombreCancion);
                Artista artista = buscarArtistaPorNombre(repositorio, nombreArtista);
                Rol rol = rolCatalogo.getRol(nombreRol);
                
                if (cancion != null && artista != null && rol != null) {
                    Contrato contrato = new Contrato(cancion, rol, artista, costo);
                    servicio.agregarContrato(contrato);
                }
            }
        }
        
        return servicio;
    }
    
    private Cancion buscarCancionPorNombre(Recital recital, String nombre) {
        for (Cancion cancion : recital.getCanciones()) {
            if (cancion.getTitulo().equals(nombre)) {
                return cancion;
            }
        }
        return null;
    }
    
    private Artista buscarArtistaPorNombre(RepositorioArtistas repositorio, String nombre) {
        for (ArtistaDiscografica artista : repositorio.getArtistasDiscografica()) {
            if (artista.getNombre().equals(nombre)) {
                return artista;
            }
        }
        for (ArtistaExterno artista : repositorio.getArtistasExternos()) {
            if (artista.getNombre().equals(nombre)) {
                return artista;
            }
        }
        return null;
    }
    
    public static List<String> listarArchivosEstadoPrevio(String rutaCarpeta) {
        List<String> archivos = new ArrayList<>();
        java.io.File carpeta = new java.io.File(rutaCarpeta);
        
        if (carpeta.exists() && carpeta.isDirectory()) {
            java.io.File[] listaArchivos = carpeta.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".json") && name.toLowerCase().contains("snapshot"));
            
            if (listaArchivos != null) {
                // Ordenar por fecha de modificación (más reciente primero)
                Arrays.sort(listaArchivos, (f1, f2) -> 
                    Long.compare(f2.lastModified(), f1.lastModified()));
                
                for (java.io.File archivo : listaArchivos) {
                    archivos.add(archivo.getName());
                }
            }
        }
        
        return archivos;
    }
}
