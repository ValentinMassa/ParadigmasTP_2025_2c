package Repositorios;

import java.util.HashMap;
import java.util.HashSet;

import Recital.Rol;


public class RepositorioRoles {

    private HashMap<String, Rol> roles;

    public RepositorioRoles() {
        this.roles = new HashMap<>();
    }

    public Boolean existeRol(String nombreRol) {
        return roles.containsKey(nombreRol);
    }

    public Rol getRol(String nombreRol) {
        return roles.get(nombreRol);
    }

    public Rol agregarRol(String rolNuevo) {
        Rol r = new Rol(rolNuevo);   
        return this.roles.putIfAbsent(rolNuevo, r);
    }

    public HashSet<Rol> getTodosLosRoles() {
        return new HashSet<>(roles.values());
    }
}
