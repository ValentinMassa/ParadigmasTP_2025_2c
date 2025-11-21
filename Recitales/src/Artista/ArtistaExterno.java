package Artista;

import java.util.HashSet;
import Recital.Rol;

public class ArtistaExterno extends Artista {
    private HashSet<Rol> rolesEntrenados;


    public ArtistaExterno(String nombre, int maxcanciones, double costo) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo);
        this.rolHistorico = new HashSet<>();
        this.rolesEntrenados = new HashSet<>();
    }

    @Override
    public Boolean puedeSerEntrenado() {
        return true; // Los artistas externos pueden ser entrenados
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArtistaExterno otro = (ArtistaExterno) obj;
        return nombre.equals(otro.nombre);
    }

    @Override
    public Boolean puedeTocarRol(String rolBuscado){
        Boolean puedeTocarRolHistorico = super.puedeTocarRol(rolBuscado);
        
        if(puedeTocarRolHistorico){
            return true;
        }
        for(Rol r: rolesEntrenados){
            if(r.getNombre().equalsIgnoreCase(rolBuscado)){
                return true;
            }
        }
        return false; 
    }

    public synchronized boolean agregarRolEntrenado(Rol rol, double multiplicadorDeCosto){

        if(rol == null){
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if(multiplicadorDeCosto < 1){
            throw new IllegalArgumentException("El multiplicador de costo de entrenamiento no puede ser menor a 1");
        }
        if(puedeTocarRol(rol)){
            throw new IllegalArgumentException("El artista ya posee el rol: " + rol.getNombre());
        }

        super.setCosto(super.getCosto() * multiplicadorDeCosto);
        this.rolesEntrenados.add(rol);

        return true;
    }

    @Override
    public HashSet<Rol> getRoles() {
        HashSet<Rol> todosLosRoles = new HashSet<>(super.getRoles());
        todosLosRoles.addAll(rolesEntrenados);
        return todosLosRoles;
    }

}
