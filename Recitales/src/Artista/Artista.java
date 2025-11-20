package Artista;
import java.util.HashSet;

import Recital.Banda;
import Recital.Rol;

public abstract class Artista {
    protected final int maxcanciones;
    protected String nombre;    
    protected HashSet<Rol> rolHistorico;
    protected HashSet<Banda> bandaHistorico;
    protected double costo; 
    protected int cantCancionesAsignado;

    public Artista(String nombre, int maxcanciones, double costo, HashSet<Rol> roles, HashSet<Banda> bandaHistorico) throws IllegalArgumentException{
        if(maxcanciones < 1){
            throw new IllegalArgumentException("La cantidad maxima de canciones debe ser al menos 1");
        }
        if(costo < 0){
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if(roles == null){
            throw new IllegalArgumentException("El conjunto de roles no puede ser nulo");
        }
        if(bandaHistorico == null){
            throw new IllegalArgumentException("El conjunto de bandas no puede ser nulo");
        }
        this.nombre = nombre;
        this.maxcanciones = maxcanciones;
        this.costo = costo;
        this.rolHistorico = new HashSet<>();
        this.bandaHistorico = new HashSet<>();
        this.rolHistorico.addAll(roles);
        this.bandaHistorico.addAll(bandaHistorico);
        this.cantCancionesAsignado = 0;
        
    }

    public String getNombre() {
        return nombre;
    }

    public int getMaxCanciones() {
        return maxcanciones;
    }

    public double getCosto() {
        return costo;
    }

    public HashSet<Rol> getRoles() {
        return new HashSet<>(rolHistorico);
    }

    public HashSet<Banda> getBandas() {
        return new HashSet<>(bandaHistorico);
    }

    public Boolean puedeAceptarNuevaCancion(){
        return cantCancionesAsignado < maxcanciones;
    }
    public Boolean asignarCancion(){
        if(!puedeAceptarNuevaCancion()){
            return false;
        }
        cantCancionesAsignado++;
        return true;
    }

    public Boolean puedeTocarRol(String rolBuscado){
        if(!rolHistorico.isEmpty()){
            return false;
        }
        for(Rol r: rolHistorico){
            if(r.getNombre() == rolBuscado){
                return true;
            }
        }
        return false;
    }

    public Boolean puedeTocarRol(Rol rolBuscado){
        if(rolHistorico.isEmpty()){
            return false;
        }
        for(Rol r: rolHistorico){
            if(r.equals(rolBuscado)){
                return true;
            }
        }
        return false;
    }
    public abstract Boolean puedeSerEntrenado();

    public int getCantCancionesAsignadas() {
        return cantCancionesAsignado;
    }  

}
