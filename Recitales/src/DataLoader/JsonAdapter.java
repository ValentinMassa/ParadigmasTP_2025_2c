package DataLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Artista.ArtistaBase;
import Artista.ArtistaExterno;
import Recital.Banda;
import Recital.Cancion;
import Recital.Rol;

import java.util.HashMap;

/**
 * Adaptador para cargar información de recitales desde archivos JSON.
 * Utiliza solo bibliotecas estándar de Java para parsear JSON.
 */
public class JsonAdapter implements ICargarRecital {
    
    private String rutaArtistas;
    private String rutaCanciones;
    private String rutaArtistasBase;
    private HashSet<ArtistaExterno> artistasCache;
    private HashSet<Cancion> cancionesCache;

    /**
     * Constructor que especifica las rutas de los archivos JSON.
     * @param rutaArtistas ruta del archivo con artistas externos
     * @param rutaCanciones ruta del archivo con canciones
     * @param rutaArtistasBase ruta del archivo con artistas base
     * @throws IllegalArgumentException si alguna ruta está vacía o nula
     */
    public JsonAdapter(String rutaArtistas, String rutaCanciones, String rutaArtistasBase) 
            throws IllegalArgumentException {
        if (rutaArtistas == null || rutaArtistas.isBlank()) {
            throw new IllegalArgumentException("Ruta de artistas no puede ser nula o vacía");
        }
        if (rutaCanciones == null || rutaCanciones.isBlank()) {
            throw new IllegalArgumentException("Ruta de canciones no puede ser nula o vacía");
        }
        if (rutaArtistasBase == null || rutaArtistasBase.isBlank()) {
            throw new IllegalArgumentException("Ruta de artistas base no puede ser nula o vacía");
        }
        
        this.rutaArtistas = rutaArtistas;
        this.rutaCanciones = rutaCanciones;
        this.rutaArtistasBase = rutaArtistasBase;
        this.artistasCache = null;
        this.cancionesCache = null;
    }

    @Override
    public HashSet<ArtistaExterno> cargarArtistasExternos() throws IOException {
        // Usar caché si ya está cargado
        if (artistasCache != null) {
            return new HashSet<>(artistasCache);
        }
        
        HashSet<ArtistaExterno> artistas = new HashSet<>();
        File file = new File(rutaArtistas);
        
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + rutaArtistas);
        }
        
        try {
            String contenido = new String(Files.readAllBytes(file.toPath()));
            List<Map<String, Object>> datos = parsearJsonArray(contenido);
            
            for (Map<String, Object> artistaData : datos) {
                ArtistaExterno artista = construirArtistaExterno(artistaData);
                if (artista != null) {
                    artistas.add(artista);
                }
            }
            
            // Guardar en caché
            artistasCache = new HashSet<>(artistas);
        } catch (Exception e) {
            throw new IOException("Error al parsear archivo de artistas: " + e.getMessage(), e);
        }
        
        return artistas;
    }

    @Override
    public HashSet<ArtistaBase> cargarArtistasBase() throws IOException {
        HashSet<ArtistaBase> artistas = new HashSet<>();
        File file = new File(rutaArtistasBase);
        
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + rutaArtistasBase);
        }
        
        try {
            String contenido = new String(Files.readAllBytes(file.toPath()));
            List<String> nombresArtistas = parsearJsonArrayStrings(contenido);
            
            // Cargar artistas en caché si no están cargados (O(n) una sola vez)
            HashSet<ArtistaExterno> todosLosArtistas = getArtistasEnCache();
            
            // Crear mapa para búsqueda O(1) en lugar de O(n)
            Map<String, ArtistaExterno> artistasMap = new HashMap<>();
            for (ArtistaExterno a : todosLosArtistas) {
                artistasMap.put(a.getNombre(), a);
            }
            
            // Buscar artistas base (O(1) por lookup)
            for (String nombre : nombresArtistas) {
                ArtistaExterno artistaOriginal = artistasMap.get(nombre);
                if (artistaOriginal != null) {
                    ArtistaBase base = new ArtistaBase(
                        artistaOriginal.getNombre(),
                        artistaOriginal.getMaxCanciones(),
                        artistaOriginal.getCosto(),
                        artistaOriginal.getRoles(),
                        artistaOriginal.getBandas()
                    );
                    artistas.add(base);
                }
            }
        } catch (Exception e) {
            throw new IOException("Error al parsear archivo de artistas base: " + e.getMessage(), e);
        }
        
        return artistas;
    }

    /**
     * Obtiene artistas desde caché, cargándolos si es necesario.
     * Evita cargar múltiples veces el archivo.
     * Complejidad: O(n) primera vez, O(1) posteriores
     */
    private HashSet<ArtistaExterno> getArtistasEnCache() throws IOException {
        if (artistasCache == null) {
            artistasCache = cargarArtistasExternos();
        }
        return new HashSet<>(artistasCache);
    }

    @Override
    public HashSet<Cancion> cargarCanciones() throws IOException {
        // Usar caché si ya está cargado
        if (cancionesCache != null) {
            return new HashSet<>(cancionesCache);
        }
        
        HashSet<Cancion> canciones = new HashSet<>();
        File file = new File(rutaCanciones);
        
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + rutaCanciones);
        }
        
        try {
            String contenido = new String(Files.readAllBytes(file.toPath()));
            List<Map<String, Object>> datos = parsearJsonArray(contenido);
            
            for (Map<String, Object> cancionData : datos) {
                Cancion cancion = construirCancion(cancionData);
                if (cancion != null) {
                    canciones.add(cancion);
                }
            }
            
            // Guardar en caché
            cancionesCache = new HashSet<>(canciones);
        } catch (Exception e) {
            throw new IOException("Error al parsear archivo de canciones: " + e.getMessage(), e);
        }
        
        return canciones;
    }

    /**
     * Construye un objeto ArtistaExterno a partir de un Map con datos JSON.
     * @param data Map con los datos del artista
     * @return ArtistaExterno construido o null si hay error
     */
    private ArtistaExterno construirArtistaExterno(Map<String, Object> data) {
        try {
            String nombre = (String) data.get("nombre");
            Number maxCancionesNum = (Number) data.get("maxCanciones");
            int maxCanciones = maxCancionesNum != null ? maxCancionesNum.intValue() : 100;
            
            Number costoNum = (Number) data.get("costo");
            double costo = costoNum != null ? costoNum.doubleValue() : 0.0;
            
            @SuppressWarnings("unchecked")
            List<String> rolesStr = (List<String>) data.get("roles");
            HashSet<Rol> roles = new HashSet<>();
            if (rolesStr != null) {
                for (String rolStr : rolesStr) {
                    roles.add(new Rol(rolStr));
                }
            }
            
            @SuppressWarnings("unchecked")
            List<String> bandasStr = (List<String>) data.get("bandas");
            HashSet<Banda> bandas = new HashSet<>();
            if (bandasStr != null) {
                for (String bandaStr : bandasStr) {
                    bandas.add(new Banda(bandaStr));
                }
            }
            
            return new ArtistaExterno(nombre, maxCanciones, costo, roles, bandas);
        } catch (Exception e) {
            System.err.println("Error construyendo ArtistaExterno: " + e.getMessage());
            return null;
        }
    }

    /**
     * Construye un objeto Cancion a partir de un Map con datos JSON.
     * @param data Map con los datos de la canción
     * @return Cancion construida o null si hay error
     */
    private Cancion construirCancion(Map<String, Object> data) {
        try {
            String titulo = (String) data.get("titulo");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> rolesRequeridos = (Map<String, Object>) data.get("rolesRequeridos");
            HashMap<Rol, Integer> roles = new HashMap<>();
            if (rolesRequeridos != null) {
                for (Map.Entry<String, Object> entry : rolesRequeridos.entrySet()) {
                    Rol rol = new Rol(entry.getKey());
                    Integer cantidad = ((Number) entry.getValue()).intValue();
                    roles.put(rol, cantidad);
                }
            }
            
            return new Cancion(titulo, roles);
        } catch (Exception e) {
            System.err.println("Error construyendo Cancion: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parsea un JSON simple en formato de array de objetos.
     * Implementación optimizada: una sola pasada por el string.
     * Complejidad: O(n) donde n es la longitud del JSON
     */
    private List<Map<String, Object>> parsearJsonArray(String json) {
        List<Map<String, Object>> result = new ArrayList<>();
        json = json.trim();
        
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return result;
        }
        
        json = json.substring(1, json.length() - 1);
        
        int braceCount = 0;
        int start = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            // Manejo de strings
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{') {
                    if (braceCount == 0) start = i;
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        String objeto = json.substring(start, i + 1).trim();
                        if (!objeto.isEmpty()) {
                            Map<String, Object> map = parsearJsonObjeto(objeto);
                            if (!map.isEmpty()) {
                                result.add(map);
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * Parsea un JSON simple en formato de array de strings.
     */
    private List<String> parsearJsonArrayStrings(String json) {
        List<String> result = new ArrayList<>();
        json = json.trim();
        
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return result;
        }
        
        // Remover corchetes externos
        json = json.substring(1, json.length() - 1);
        
        // Dividir por comillas
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"') {
                if (!inQuotes) {
                    inQuotes = true;
                } else {
                    inQuotes = false;
                    result.add(current.toString());
                    current = new StringBuilder();
                }
            } else if (inQuotes) {
                current.append(c);
            }
        }
        
        return result;
    }

    /**
     * Parsea un objeto JSON individual.
     */
    private Map<String, Object> parsearJsonObjeto(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.trim();
        
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return map;
        }
        
        // Remover llaves
        json = json.substring(1, json.length() - 1);
        
        // Parsear pares clave-valor
        int braceCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;
        int keyStart = -1;
        int valueStart = -1;
        String currentKey = null;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                if (!inQuotes) {
                    inQuotes = true;
                    if (keyStart == -1) keyStart = i + 1;
                } else {
                    inQuotes = false;
                    if (currentKey == null) {
                        currentKey = json.substring(keyStart, i);
                    }
                }
            } else if (!inQuotes) {
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                if (c == '[') bracketCount++;
                if (c == ']') bracketCount--;
                
                if (c == ':' && braceCount == 0 && bracketCount == 0) {
                    valueStart = i + 1;
                } else if (c == ',' && braceCount == 0 && bracketCount == 0) {
                    if (currentKey != null && valueStart != -1) {
                        Object value = parsearValor(json.substring(valueStart, i).trim());
                        map.put(currentKey, value);
                    }
                    currentKey = null;
                    valueStart = -1;
                    keyStart = -1;
                }
            }
        }
        
        // Procesar último par
        if (currentKey != null && valueStart != -1) {
            Object value = parsearValor(json.substring(valueStart).trim());
            map.put(currentKey, value);
        }
        
        return map;
    }

    /**
     * Parsea un valor JSON individual.
     */
    private Object parsearValor(String valor) {
        valor = valor.trim();
        
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            return valor.substring(1, valor.length() - 1);
        } else if (valor.startsWith("{") && valor.endsWith("}")) {
            return parsearJsonObjeto(valor);
        } else if (valor.startsWith("[") && valor.endsWith("]")) {
            return parsearLista(valor);
        } else if (valor.equals("true")) {
            return true;
        } else if (valor.equals("false")) {
            return false;
        } else if (valor.equals("null")) {
            return null;
        } else {
            try {
                if (valor.contains(".")) {
                    return Double.parseDouble(valor);
                } else {
                    return Integer.parseInt(valor);
                }
            } catch (NumberFormatException e) {
                return valor;
            }
        }
    }

    /**
     * Parsea una lista JSON.
     */
    private List<String> parsearLista(String json) {
        List<String> result = new ArrayList<>();
        json = json.substring(1, json.length() - 1).trim();
        
        if (json.isEmpty()) {
            return result;
        }
        
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                String item = current.toString().trim();
                if (item.startsWith("\"") && item.endsWith("\"")) {
                    result.add(item.substring(1, item.length() - 1));
                } else {
                    result.add(item);
                }
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        String item = current.toString().trim();
        if (!item.isEmpty()) {
            if (item.startsWith("\"") && item.endsWith("\"")) {
                result.add(item.substring(1, item.length() - 1));
            } else {
                result.add(item);
            }
        }
        
        return result;
    }
}
