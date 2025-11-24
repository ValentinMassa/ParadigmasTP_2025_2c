package DataLoader.Adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import Artista.*;
import DataLoader.ICargarRecital;
import Recital.Banda;
import Recital.Cancion;
import Recital.Rol;
import Repositorios.BandaCatalogoMemory;
import Repositorios.RolCatalogoMemory;


public class XmlAdapter implements ICargarRecital {
    
    private String rutaArtistas;
    private String rutaCanciones;
    private String rutaArtistasBase;
    private RolCatalogoMemory rolCatalogo;
    private BandaCatalogoMemory bandaCatalogo;

    private HashSet<ArtistaExterno> artistasExternos;
    private HashSet<ArtistaDiscografica> artistasBase;
    
    /**
     * Constructor que especifica las rutas de los archivos XML.
     * @param rutaArtistas ruta del archivo con artistas externos
     * @param rutaCanciones ruta del archivo con canciones
     * @param rutaArtistasBase ruta del archivo con artistas base
     * @throws IllegalArgumentException si alguna ruta está vacía o nula
     */
    public XmlAdapter(String rutaArtistas, String rutaCanciones, String rutaArtistasBase) 
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
        this.rolCatalogo = new RolCatalogoMemory();
        this.bandaCatalogo = new BandaCatalogoMemory();
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

        List<String> artistasBaseString = new ArrayList<>();
        HashSet<ArtistaExterno> artistasExternos = new HashSet<>();
        HashSet<ArtistaDiscografica> artistasDiscograficas = new HashSet<>();

        try {
            // Parsear artistas-discografica.xml
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(rutaArtistasBase));
            doc.getDocumentElement().normalize();
            
            NodeList artistaNodes = doc.getElementsByTagName("artista");
            for (int i = 0; i < artistaNodes.getLength(); i++) {
                Node node = artistaNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    artistasBaseString.add(elem.getTextContent());
                }
            }

            // Parsear artistas.xml
            doc = builder.parse(new File(rutaArtistas));
            doc.getDocumentElement().normalize();
            
            NodeList artistaList = doc.getElementsByTagName("artista");
            for (int i = 0; i < artistaList.getLength(); i++) {
                Node node = artistaList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    esArtistaDiscografica = false;
                    
                    String nombre = elem.getElementsByTagName("nombre").item(0).getTextContent();
                    int maxCanciones = Integer.parseInt(elem.getElementsByTagName("maxCanciones").item(0).getTextContent());
                    double costo = Double.parseDouble(elem.getElementsByTagName("costo").item(0).getTextContent());

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

                    NodeList rolesList = elem.getElementsByTagName("rol");
                    for (int j = 0; j < rolesList.getLength(); j++) {
                        Node rolNode = rolesList.item(j);
                        if (rolNode.getNodeType() == Node.ELEMENT_NODE) {
                            String rolNombre = rolNode.getTextContent();
                            Rol rol;
                            if(rolCatalogo.existeRol(rolNombre)){
                                rol = rolCatalogo.getRol(rolNombre);
                            } else {
                                rol = new Rol(rolNombre);
                                rolCatalogo.agregarRol(rolNombre);
                            }
                            a.agregarRolHistorico(rol);
                        }
                    }

                    NodeList bandasList = elem.getElementsByTagName("banda");
                    for (int j = 0; j < bandasList.getLength(); j++) {
                        Node bandaNode = bandasList.item(j);
                        if (bandaNode.getNodeType() == Node.ELEMENT_NODE) {
                            String bandaNombre = bandaNode.getTextContent();
                            Banda banda;
                            if(bandaCatalogo.existeBanda(bandaNombre)){
                                banda = bandaCatalogo.getBanda(bandaNombre);
                            } else {
                                banda = new Banda(bandaNombre);
                                bandaCatalogo.agregarBanda(bandaNombre);
                            }
                            a.agregarBandaHistorico(banda);
                        }
                    }
                    if(esArtistaDiscografica){
                        artistasDiscograficas.add((ArtistaDiscografica) a);
                    } else {
                        artistasExternos.add((ArtistaExterno) a);
                    }
                }
            }
            this.artistasBase = artistasDiscograficas;
            this.artistasExternos = artistasExternos;

        }
        catch(ParserConfigurationException | SAXException | IOException e){
            throw new Exception ("No se pudo abrir el archivo de artistas: " + e.getMessage());
        }
    }

    /**
     * Carga las canciones desde el archivo XML.
     * @return conjunto de canciones
     * @throws Exception si ocurre un error al leer el archivo
     */
    @Override
    public HashSet<Cancion> cargarCanciones() throws Exception {        
        HashSet<Cancion> canciones = new HashSet<>();
        
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(rutaCanciones));
            doc.getDocumentElement().normalize();
            
            NodeList recitalList = doc.getElementsByTagName("recital");
            for (int i = 0; i < recitalList.getLength(); i++) {
                Node node = recitalList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String titulo = elem.getElementsByTagName("titulo").item(0).getTextContent();

                    Cancion cancion = new Cancion(titulo);
                    
                    Element rolesElem = (Element) elem.getElementsByTagName("rolesRequeridos").item(0);
                    NodeList rolList = rolesElem.getElementsByTagName("rol");
                    for (int j = 0; j < rolList.getLength(); j++) {
                        Node rolNode = rolList.item(j);
                        if (rolNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element rolElem = (Element) rolNode;
                            String rolNombre = rolElem.getAttribute("nombre");
                            int cantidad = Integer.parseInt(rolElem.getAttribute("cantidad"));

                            Rol rol;
                            if(rolCatalogo.existeRol(rolNombre)){
                                rol = rolCatalogo.getRol(rolNombre);
                            } else {
                                rol = new Rol(rolNombre);
                                rolCatalogo.agregarRol(rolNombre);
                            }
                            cancion.agregarRolRequerido(rol, cantidad);
                        }
                    }
                    canciones.add(cancion);
                }
            }
            
        }
        catch(ParserConfigurationException | SAXException | IOException e){
            throw new Exception ("No se pudo abrir el archivo de canciones: " + e.getMessage());
        }
        return canciones;
    }

    public RolCatalogoMemory getRolCatalogo() {
        return this.rolCatalogo;
    }

    public BandaCatalogoMemory getBandaCatalogo() {
        return this.bandaCatalogo;
    }

}
