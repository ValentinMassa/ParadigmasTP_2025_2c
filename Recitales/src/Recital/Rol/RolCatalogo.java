package Recital.Rol;

import java.util.HashMap;

public class RolCatalogo {

    private HashMap<String,Rol> roles;

    public RolCatalogo() {
        this.roles = new HashMap<>();
    }
    public Rol obtenerRol(String nombreRol) {
        
        if(!existeRol(nombreRol)) {
            return agregarRol(nombreRol);
        }
        return roles.get(nombreRol);
    }

    public Rol getRol(String nombreRol) {
        return roles.get(nombreRol);
    }

    private Boolean existeRol(String nombreRol) {
        return roles.containsKey(nombreRol);
    }
    private Rol agregarRol(String rolNuevo) {
        Rol r = new Rol(rolNuevo);
        roles.put(rolNuevo, r);
        return r;
    }
}
