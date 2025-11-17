package Recital;

import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import Recital.Artista.*;
import Recital.Contratos.*;
import Recital.Rol.Rol;
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
        this.servicioContratacion = new ServicioContratacion();
        this.artistaBase.addAll(artistaBase);
        this.artistaExternos.addAll(artistaExternos);
        this.canciones.addAll(canciones);
        this.contratos = servicioContratacion.contratarParaTodo(this);
    }

        public List<Contrato> getContratos(){
        return contratos;
    }

    public Map<Artista, Double> getCostosPorArtista(){
        //Falta Desarrollar
        return null;
    }

    public double getCostoTotalRecital(){
        //Falta Desarrollar
        return 0;
    }


    public Map<Cancion, Double> getCostosPorCancion(){
        //Falta Desarrollar
        return null;
    }
    public HashSet<Cancion> getCanciones() {
        return canciones;
    }

    public Map<Rol, Integer> getRolesFaltantes(){
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        
        // Contar todos los roles requeridos para todas las canciones
        for (Cancion cancion : canciones) {
            for (Rol rol : cancion.getRolesRequeridos()) {
                rolesRequeridos.put(rol, rolesRequeridos.getOrDefault(rol, 0) + 1);
            }
        }
        // Calcular roles que pueden cubrir los artistas base DINÁMICAMENTE
        // Asignando cada artista base a UNA CANCIÓN donde pueda cubrir VARIOS roles
        Map<Rol, Integer> rolesCubiertosOptimos = calcularAsignacionOptima();
        
        // Contar roles cubiertos por contratos existentes (si los hay)
        if (contratos != null) {
            for (Contrato contrato : contratos) {
                Rol rol = contrato.getRol();
                rolesCubiertosOptimos.put(rol, rolesCubiertosOptimos.getOrDefault(rol, 0) + 1);
            }
        }
        // Calcular faltantes
        Map<Rol, Integer> rolesFaltantes = new HashMap<>();
        for (Map.Entry<Rol, Integer> entry : rolesRequeridos.entrySet()) {
            Rol rol = entry.getKey();
            int requerido = entry.getValue();
            int cubierto = rolesCubiertosOptimos.getOrDefault(rol, 0);
            int faltante = requerido - cubierto;
            
            if (faltante > 0) {
                rolesFaltantes.put(rol, faltante);
            }
        }
        return rolesFaltantes;
    }
    
    private Map<Rol, Integer> calcularAsignacionOptima() {
        Map<Rol, Integer> rolesCubiertos = new HashMap<>();
        
        // Para cada canción, asignar artistas base que puedan cubrir sus roles
        for (Cancion cancion : canciones) {
            Map<Rol, Integer> rolesNecesarios = new HashMap<>();
            
            // Contar roles necesarios para esta canción
            for (Rol rol : cancion.getRolesRequeridos()) {
                rolesNecesarios.put(rol, rolesNecesarios.getOrDefault(rol, 0) + 1);
            }
            
            // Para cada artista base, ver si puede contribuir a esta canción
            for (ArtistaBase artista : artistaBase) {
                // Un artista base toca UN rol por canción
                // Elegimos el primer rol que pueda tocar que sea necesario
                for (Rol rolDelArtista : artista.getRoles()) {
                    if (rolesNecesarios.getOrDefault(rolDelArtista, 0) > 0) {
                        // Este artista puede cubrir este rol en esta canción
                        rolesCubiertos.put(rolDelArtista, rolesCubiertos.getOrDefault(rolDelArtista, 0) + 1);
                        rolesNecesarios.put(rolDelArtista, rolesNecesarios.get(rolDelArtista) - 1);
                        break; // Este artista ya cubrió un rol en esta canción
                    }
                }
            }
        }
        
        return rolesCubiertos;
    }

    public Map<Rol, Integer> getRolesFaltantesParaCancion(Cancion cancion) {
        Map<Rol, Integer> rolesRequeridos = new HashMap<>();
        Map<Rol, Integer> rolesCubiertos = new HashMap<>();

        // Contar roles requeridos por la canción
        for (Rol rol : cancion.getRolesRequeridos()) {
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
