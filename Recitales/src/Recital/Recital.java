package Recital;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.Rol;
import Recital.Banda.Banda;
import java.util.Map;

public class Recital {
    private HashSet<ArtistaBase> artistaBase;
    private HashSet<ArtistaExterno> artistaExternos;
    private HashSet<Cancion> canciones;
    private List<Contrato> contratos;
    private ServicioContratacion servicioContratacion;


    public Recital(HashSet<ArtistaBase> artistaBase, HashSet<ArtistaExterno> artistaExternos,
                   HashSet<Cancion> canciones, ServicioContratacion servicioContratacion)
                   throws IllegalArgumentException {
        if (artistaExternos == null || artistaBase == null || canciones == null || servicioContratacion == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        } 
        this.artistaBase = new HashSet<ArtistaBase>();
        this.artistaExternos = new HashSet<ArtistaExterno>();
        this.canciones = new HashSet<Cancion>();
        this.servicioContratacion = servicioContratacion;
        this.contratos = new ArrayList<>();
        this.artistaBase.addAll(artistaBase);
        this.artistaExternos.addAll(artistaExternos);
        this.canciones.addAll(canciones);
    }

    public List<Contrato> getContratos(){
        return contratos;
    }

    public Map<Artista, Double> getCostosPorArtista(){
        Map<Artista, Double> costos = new HashMap<>();
        if (contratos != null) {
            for (Contrato contrato : contratos) {
                Artista artista = contrato.getArtista();
                double costo = contrato.obtenerCostoContrato();
                costos.put(artista, costos.getOrDefault(artista, 0.0) + costo);
            }
        }
        return costos;
    }

    public double getCostoTotalRecital(){
        double total = 0;
        if (contratos != null) {
            for (Contrato contrato : contratos) {
                total += contrato.obtenerCostoContrato();
            }
        }
        return total;
    }


    public Map<Cancion, Double> getCostosPorCancion(){
        Map<Cancion, Double> costos = new HashMap<>();
        for (Cancion cancion : canciones) {
            double costoCancion = 0;
            if (contratos != null) {
                for (Contrato contrato : contratos) {
                    if (contrato.getCancion().equals(cancion)) {
                        costoCancion += contrato.obtenerCostoContrato();
                    }
                }
            }
            costos.put(cancion, costoCancion);
        }
        return costos;
    }
    public HashSet<Cancion> getCanciones() {
        return canciones;
    }

    public HashSet<ArtistaBase> getArtistasBase() {
        return new HashSet<>(artistaBase);
    }

    public HashSet<ArtistaExterno> getArtistasExternos() {
        return new HashSet<>(artistaExternos);
    }

    public Map<Rol, Integer> getRolesFaltantes(){
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        Map<Rol, Integer> rolesCubiertos = new HashMap<>();
        
        // Contar todos los roles requeridos para todas las canciones
        for (Cancion cancion : canciones) {
            for (Rol rol : cancion.getRolesRequeridos().keySet()) {
                rolesRequeridos.put(rol, rolesRequeridos.getOrDefault(rol, 0) + 1);
            }
        }
        
        // Contar roles cubiertos por contratos existentes
        if (contratos != null) {
            for (Contrato contrato : contratos) {
                Rol rol = contrato.getRol();
                rolesCubiertos.put(rol, rolesCubiertos.getOrDefault(rol, 0) + 1);
            }
        }
        
        // Calcular faltantes
        Map<Rol, Integer> rolesFaltantes = new HashMap<>();
        for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
            Rol rol = entry.getKey();
            int requerido = entry.getValue();
            int cubierto = rolesCubiertos.getOrDefault(rol, 0);
            int faltante = requerido - cubierto;
            
            if (faltante > 0) {
                rolesFaltantes.put(rol, faltante);
            }
        }
        return rolesFaltantes;
    }
    
    

    public Map<Rol, Integer> getRolesFaltantesParaCancion(Cancion cancion) {
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        Map<Rol, Integer> rolesCubiertos = new HashMap<>();

        // Contar roles requeridos por la canción
        for (Rol rol : cancion.getRolesRequeridos().keySet()) {
            rolesRequeridos.put(rol, rolesRequeridos.getOrDefault(rol, 0) + 1);
        }

        // Contar roles que pueden ser cubiertos por artistas base
        for (ArtistaBase artista : artistaBase) {
            boolean yaCubrio = false;
            for (Rol rol : artista.getRoles()) {
                if (!yaCubrio && rolesRequeridos.getOrDefault(rol, 0) > 0) {
                    rolesCubiertos.put(rol, rolesCubiertos.getOrDefault(rol, 0) + 1);
                    yaCubrio = true; 
                }
            }
        }

        // Contar roles cubiertos por contratos existentes para esta canción
        if (contratos != null) {
            for (Contrato contrato : contratos) {
                if (contrato.getCancion().equals(cancion)) {
                    Rol rol = contrato.getRol();
                    rolesCubiertos.put(rol, rolesCubiertos.getOrDefault(rol, 0) + 1);
                }
            }
        }

        // Calcular roles faltantes
        Map<Rol, Integer> rolesFaltantes = new HashMap<>();
        for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
            Rol rol = entry.getKey();
            int faltante = entry.getValue() - rolesCubiertos.getOrDefault(rol, 0);
            if (faltante > 0) {
                rolesFaltantes.put(rol, faltante);
            }
        }

        return rolesFaltantes;
    }
}
