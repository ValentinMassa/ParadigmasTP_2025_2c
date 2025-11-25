package DataLoader.Adapters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import Artista.*;
import DataLoader.ICargarRecital;
import Recital.Banda;
import Recital.Cancion;
import Recital.Rol;
import Repositorios.RepositorioBandas;
import Repositorios.RepositorioRoles;
import com.google.gson.*;


public class JsonAdapter implements ICargarRecital {
    
    private String rutaArtistas;
    private String rutaCanciones;
    private String rutaArtistasBase;
    private RepositorioRoles rolCatalogo;
    private RepositorioBandas bandaCatalogo;

    private HashSet<ArtistaExterno> artistasExternos;
    private HashSet<ArtistaDiscografica> artistasBase;
    
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
        this.rolCatalogo = new RepositorioRoles();
        this.bandaCatalogo = new RepositorioBandas();
        this.artistasExternos = new HashSet<>();
        this.artistasBase = new HashSet<>();
    }

     @Override
    public HashSet<ArtistaDiscografica> cargarArtistasDiscografica() throws Exception{
        if(artistasBase.isEmpty()){
            cargarArtistas();
        }
        return this.artistasBase;
    }

     @Override
    public HashSet<ArtistaExterno> cargarArtistasExternos() throws Exception{
        if(artistasExternos.isEmpty()){
            cargarArtistas();
        }
        return this.artistasExternos;
    }


    private void cargarArtistas() throws Exception {
        boolean esArtistaDiscografica = false;
        FileInputStream filestream = null;
        InputStreamReader reader = null;
        JsonArray jsonArray = null;

        List<String> artistasBaseString = new ArrayList<>();
        HashSet<ArtistaExterno> artistasExternos = new HashSet<>();
        HashSet<ArtistaDiscografica> artistasDiscograficas = new HashSet<>();

        try{
            filestream = new FileInputStream(rutaArtistasBase);
            reader = new InputStreamReader (filestream, StandardCharsets.UTF_8);
            jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            for(JsonElement elem : jsonArray){
                artistasBaseString.add(elem.getAsString());
            }
            
            filestream.close();
            reader.close();

            filestream = new FileInputStream(rutaArtistas);
            reader = new InputStreamReader (filestream, StandardCharsets.UTF_8);
            jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for(JsonElement elem : jsonArray){
                esArtistaDiscografica = false;
                JsonObject obj = elem.getAsJsonObject();
                String nombre = obj.get("nombre").getAsString();
                int maxCanciones = obj.get("maxCanciones").getAsInt();
                double costo = obj.get("costo").getAsDouble();

                Artista a = null;

                for(String ArtistaNombre : artistasBaseString){
                    if(ArtistaNombre.equalsIgnoreCase(nombre))
                        {
                            a = new ArtistaDiscografica(nombre, maxCanciones, costo);
                            esArtistaDiscografica = true;
                            break;
                        }
                }
                if(a == null){
                    a = new ArtistaExterno(nombre, maxCanciones, costo);
                }

                JsonArray rolesArray = obj.getAsJsonArray("roles");
                for(JsonElement roleElem : rolesArray){
                    String rolNombre = roleElem.getAsString();
                    Rol rol;
                    if(rolCatalogo.existeRol(rolNombre)){
                        rol = rolCatalogo.getRol(rolNombre);
                    } else {
                        rol = new Rol(rolNombre);
                        rolCatalogo.agregarRol(rolNombre);
                    }
                    a.agregarRolHistorico(rol);
                }

                JsonArray bandasArray = obj.getAsJsonArray("bandas");
                for(JsonElement bandaElem : bandasArray){
                    String bandaNombre = bandaElem.getAsString();
                    Banda banda;
                    if(bandaCatalogo.existeBanda(bandaNombre)){
                        banda = bandaCatalogo.getBanda(bandaNombre);
                    } else {
                        banda = new Banda(bandaNombre);
                        bandaCatalogo.agregarBanda(bandaNombre);
                    }
                    a.agregarBandaHistorico(banda);
                }
                if(esArtistaDiscografica){
                    artistasDiscograficas.add((ArtistaDiscografica) a);
                } else {
                    artistasExternos.add((ArtistaExterno) a);
                }
            }
            this.artistasBase = artistasDiscograficas;
            this.artistasExternos = artistasExternos;

        }
        catch(IOException e){
            throw new IOException ("No se pudo abrir el archivo de canciones: " + e.getMessage());
        }
        finally {
            if(filestream != null)
                filestream.close();
            if(reader != null)
                reader.close();
        }
    }

    /**
     * Carga las canciones desde el archivo JSON.
     * @return conjunto de canciones
     * @throws IOException si ocurre un error al leer el archivo
     */
    @Override
    public HashSet<Cancion> cargarCanciones() throws IOException {        
        HashSet<Cancion> canciones = new HashSet<>();
        FileInputStream filestream = null;
        InputStreamReader reader = null;
        JsonArray jsonArray;
        
        try{
            filestream = new FileInputStream(rutaCanciones);
            reader = new InputStreamReader (filestream, StandardCharsets.UTF_8);
            jsonArray = JsonParser.parseReader(reader).getAsJsonArray();  
            
            for(JsonElement elem : jsonArray){

                JsonObject obj = elem.getAsJsonObject();
                String titulo = obj.get("titulo").getAsString();

                Cancion cancion = new Cancion(titulo);
                JsonObject rolesObj = obj.getAsJsonObject("rolesRequeridos");

                for(Map.Entry<String, JsonElement> entry : rolesObj.entrySet()){
                    String rolNombre = entry.getKey();
                    int cantidad = entry.getValue().getAsInt();

                    Rol rol;
                    if(rolCatalogo.existeRol(rolNombre)){
                        rol = rolCatalogo.getRol(rolNombre);
                    } else {
                        rol = new Rol(rolNombre);
                        rolCatalogo.agregarRol(rolNombre);
                    }
                    cancion.agregarRolRequerido(rol, cantidad);
                }
                canciones.add(cancion);
            }
            
        }
        catch(IOException e){
            throw new IOException ("No se pudo abrir el archivo de canciones: " + e.getMessage());
        }
        finally {
            if(filestream != null)
                filestream.close();
            if(reader != null)
                reader.close();
        }
        return canciones;
    }

    public RepositorioRoles getRolCatalogo() {
        return this.rolCatalogo;
    }

    public RepositorioBandas getBandaCatalogo() {
        return this.bandaCatalogo;
    }

}
