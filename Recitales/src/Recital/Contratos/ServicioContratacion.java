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
    public ServicioContratacion() {
    }

    public List<Contrato> contratarParaCancion(Recital recital, Cancion cancion) {
        List<Contrato> nuevosContratos = new ArrayList<>();
        
        // Obtener solo los roles faltantes para esta canción
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
                    Contrato contrato = new Contrato(cancion, rol, artistaSeleccionado);
                    nuevosContratos.add(contrato);
                    recital.getContratos().add(contrato);
                    artistaSeleccionado.asignarCancion();
                } else {
                    throw new IllegalArgumentException("No hay artistas disponibles para el rol: " + rol.getNombre());
                }
            }
        }

        return nuevosContratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital) {
        List<Contrato> contratos = new ArrayList<>();
        
        // Iterar por cada canción del recital
        for (Cancion cancion : recital.getCanciones()) {
            try {
                // Obtener roles faltantes para esta canción
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);
                
                // Si la canción ya tiene todos sus roles cubiertos, saltarla
                if (rolesFaltantes.isEmpty()) {
                    continue;
                }
                
                // Contratar artistas para los roles faltantes
                List<Contrato> contratosCancion = contratarParaCancion(recital, cancion);
                contratos.addAll(contratosCancion);
                
            } catch (IllegalArgumentException e) {
                System.out.println("Advertencia en canción: " + e.getMessage());
            }
        }
        
        return contratos;
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

                // Seleccionar el más barato disponible
                if (costo < mejorCosto) {
                    mejor = externo;
                    mejorCosto = costo;
                }
            }
        }

        return mejor;
    }
}
