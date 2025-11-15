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

    private Boolean existeRol(String nombreRol) {
        for (Rol r : roles) {
            if (r.getNombre().equals(nombreRol)) {
                return true;
            }
        }
        return false;
    }
    private Rol agregarRol(String rolNuevo) {
        Rol r = new Rol(rolNuevo);
        roles.add(r);
        return r;
    }
}
