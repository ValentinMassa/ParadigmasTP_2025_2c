package Servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import Artista.Artista;
import Artista.ArtistaBase;
import Recital.Cancion;
import Recital.Contrato;
import Recital.Recital;
import Recital.Rol;


public class ServicioContratacion {
    private List<Contrato> contratos;
    
    public ServicioContratacion() {
        this.contratos = new ArrayList<Contrato>();
    }

    public List<Contrato> contratarParaCancion(Cancion cancion) {
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital){
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
    }
    
    public List<Contrato> getContratosPorCancion(Cancion cancion) {
        List<Contrato> contratosDeCancion = new ArrayList<Contrato>();

        for(Contrato contrato : contratos) {
            if (contrato.getCancion().equals(cancion)) {
                contratosDeCancion.add(contrato);
            }
        }
        return contratosDeCancion;
    }


    public List<Contrato> getContratos() {
        return contratos;
    }

    private Boolean tieneContratoConCancion(Artista a, Cancion c) {
        for (Contrato contrato : contratos) {
            if (contrato.getArtista().equals(a) && contrato.getCancion().equals(c)) {
                return true;
            }
        }
        return false;
    }

    public List<Contrato> posiblesContratoArtista(ArtistaBase a, HashMap<Cancion, HashMap<Rol, Integer>> c){
        
        List<Contrato> posiblesContratos = new ArrayList<Contrato>();
        int maxCanciones = a.getMaxCanciones() - a.getCantCancionesAsignadas(); 
        
        for(Cancion cancion : c.keySet()) {
            HashMap<Rol, Integer> roles = c.get(cancion);
            
            if(maxCanciones <= 0)
                break;

            for(Rol rol : roles.keySet()) {
                if(a.puedeTocarRol(rol) && roles.get(rol) > 0 && !tieneContratoConCancion(a, cancion)) {
                    Contrato contrato = new Contrato(cancion, rol, a);
                    posiblesContratos.add(contrato);
                    maxCanciones--;
                    break;
                }
            }
        }
        System.out.println("Posibles contratos para el artista " + a.getNombre() + ": " + posiblesContratos.size());
        return posiblesContratos;
    }

}
