package Recital.Rol;

import java.util.HashSet;

public class RolCatalogo {
    private HashSet<Rol> roles;

    public RolCatalogo() {
        this.roles = new HashSet<>();
    }
    public Rol obtenerRol(String nombreRol) {
        
        if(!existeRol(nombreRol)) {
            return agregarRol(nombreRol);
        }
        return agregarRol(nombreRol);
    }

    public Rol obtenerRol(Rol nombreRol) {
        roles.add(nombreRol);
        return nombreRol;
    }

    public Boolean existeRol(String nombreRol) {
        for (Rol rol : roles) {
            if (rol.getNombre().equals(nombreRol)) {
                return true;
            }
        }
        return false;
    }
    private Rol agregarRol(String rolNuevo) {
        Rol nuevoRol = new Rol(rolNuevo);
        roles.add(nuevoRol);
        return nuevoRol;
    }
}
