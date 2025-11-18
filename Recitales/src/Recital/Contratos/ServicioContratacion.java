package Recital.Contratos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Recital.Cancion;
import Recital.Recital;
import Recital.Rol.Rol;
import Recital.Artista.Artista;
import Recital.Artista.ArtistaBase;
import Recital.Artista.ArtistaExterno;
import Recital.Banda.Banda;

public class ServicioContratacion {
    
    public static class ContratacionException extends Exception {
        private List<String> rolesNoDisponibles;
        private int artistasDisponiblesRestantes;
        
        public ContratacionException(String mensaje, List<String> rolesNoDisponibles, 
                                    int artistasDisponiblesRestantes) {
            super(mensaje);
            this.rolesNoDisponibles = rolesNoDisponibles;
            this.artistasDisponiblesRestantes = artistasDisponiblesRestantes;
        }
        
        public List<String> getRolesNoDisponibles() {
            return rolesNoDisponibles;
        }
        
        public int getArtistasDisponiblesRestantes() {
            return artistasDisponiblesRestantes;
        }
    }
    
    public ServicioContratacion() {
    }

    public List<Contrato> contratarParaCancion(Recital recital, Cancion cancion) 
            throws ContratacionException {
        List<Contrato> nuevosContratos = new ArrayList<>();
        List<String> rolesNoDisponibles = new ArrayList<>();
        
        // Obtener solo los roles faltantes para esta cancion
        Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);

        for (Map.Entry<Rol, Integer> entry : rolesFaltantes.entrySet()) {
            Rol rol = entry.getKey();
            int cantidadNecesaria = entry.getValue();

            for (int i = 0; i < cantidadNecesaria; i++) {
                Artista artistaSeleccionado = buscarBaseDisponible(recital, cancion, rol);

                if (artistaSeleccionado == null) {
                    artistaSeleccionado = buscarExternoDisponible(recital, cancion, rol);
                }

                if (artistaSeleccionado != null) {
                    Contrato contrato = new Contrato(cancion, rol, artistaSeleccionado, recital);
                    nuevosContratos.add(contrato);
                    recital.getContratos().add(contrato);
                    artistaSeleccionado.asignarCancion();
                } else {
                    rolesNoDisponibles.add(rol.getNombre());
                }
            }
        }
        
        // Si hay roles que no pudieron ser cubiertos, lanzar excepcion
        if (!rolesNoDisponibles.isEmpty()) {
            int artistasDisponibles = contarArtistasDisponibles(recital, cancion);
            throw new ContratacionException(
                "No hay artistas disponibles para algunos roles: " + String.join(", ", rolesNoDisponibles),
                rolesNoDisponibles,
                artistasDisponibles
            );
        }

        return nuevosContratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital) 
            throws ContratacionException {
        List<Contrato> contratos = new ArrayList<>();
        List<String> cancionesConError = new ArrayList<>();
        
        // Iterar por cada cancion del recital
        for (Cancion cancion : recital.getCanciones()) {
            try {
                // Obtener roles faltantes para esta cancion
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);
                
                // Si la cancion ya tiene todos sus roles cubiertos, saltarla
                if (rolesFaltantes.isEmpty()) {
                    continue;
                }
                
                // Contratar artistas para los roles faltantes
                List<Contrato> contratosCancion = contratarParaCancion(recital, cancion);
                contratos.addAll(contratosCancion);
                
            } catch (ContratacionException e) {
                cancionesConError.add(cancion.getTitulo() + ": " + e.getMessage());
            }
        }
        
        // Si hubo errores en alguna cancion, lanzar excepcion con el resumen
        if (!cancionesConError.isEmpty()) {
            throw new ContratacionException(
                "Errores de contratacion en " + cancionesConError.size() + " cancion(es):\n" + 
                String.join("\n", cancionesConError),
                new ArrayList<>(),
                0
            );
        }
        
        return contratos;
    }
    
    private int contarArtistasDisponibles(Recital recital, Cancion cancion) {
        int disponibles = 0;
        
        // Contar artistas base disponibles
        for (ArtistaBase artista : recital.getArtistasBase()) {
            if (artista.puedeAceptarNuevaCancion() && 
                !tieneContratoEnCancion(recital, artista, cancion)) {
                disponibles++;
            }
        }
        
        // Contar artistas externos disponibles
        for (ArtistaExterno artista : recital.getArtistasExternos()) {
            if (artista.puedeAceptarNuevaCancion() && 
                !tieneContratoEnCancion(recital, artista, cancion)) {
                disponibles++;
            }
        }
        
        return disponibles;
    }

    private boolean tieneContratoEnCancion(Recital recital, Artista artista, Cancion cancion) {
        for (Contrato c : recital.getContratos()) {
            if (c.getCancion().equals(cancion) && c.getArtista().equals(artista)) {
                return true;
            }
        }
        return false;
    }

    private ArtistaBase buscarBaseDisponible(Recital recital, Cancion cancion, Rol rol) {
        for (ArtistaBase artista : recital.getArtistasBase()) {
            boolean puedeTocarRol = artista.getRoles().contains(rol);
            boolean tieneDisponibilidad = artista.puedeAceptarNuevaCancion();
            boolean noEstaEnCancion = !tieneContratoEnCancion(recital, artista, cancion);

            if (puedeTocarRol && tieneDisponibilidad && noEstaEnCancion) {
                return artista;  
            }
        }
        return null; 
    }

    private ArtistaExterno buscarExternoDisponible(Recital recital, Cancion cancion, Rol rol) {
        ArtistaExterno mejor = null;
        double mejorCosto = Double.MAX_VALUE;

        for (ArtistaExterno externo : recital.getArtistasExternos()) {
            boolean puedeTocarRol = externo.getRoles().contains(rol);
            boolean tieneDisponibilidad = externo.puedeAceptarNuevaCancion();
            boolean noTieneContratoEnCancion = !tieneContratoEnCancion(recital, externo, cancion);
            
            if (puedeTocarRol && tieneDisponibilidad && noTieneContratoEnCancion) {
                
                // Calcular costo con posible descuento
                double costo = externo.getCosto();
                boolean comparteBanda = false;
                
                for (ArtistaBase base : recital.getArtistasBase()) {
                    for (Banda b : base.getBandasHistoricas()) {
                        if (externo.getBandasHistoricas().contains(b)) {
                            comparteBanda = true;
                            break;
                        }
                    }
                    if (comparteBanda) break;
                }

                if (comparteBanda) {
                    costo = costo * 0.5;   // 50% de descuento
                }

                // Seleccionar el mas barato disponible
                if (costo < mejorCosto) {
                    mejor = externo;
                    mejorCosto = costo;
                }
            }
        }

        return mejor;
    }
}
