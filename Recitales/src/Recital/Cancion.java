package Recital;

import java.util.HashSet;
import Recital.Rol.Rol;

public class Cancion {
    private String titulo;
    private Map<Rol, Integer> rolesRequeridos;

    public Cancion(String titulo, Map<Rol, Integer> rolesRequeridos) throws IllegalArgumentException {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la canción no puede ser nulo o vacío");
        }
        if (rolesRequeridos == null || rolesRequeridos.isEmpty()) {
            throw new IllegalArgumentException("Los roles requeridos no pueden ser nulos o vacíos");
        }
        this.titulo = titulo;
        this.rolesRequeridos = new HashMap<>(rolesRequeridos);
    }

    public String getTitulo() {
        return titulo;
    }

    public Map<Rol, Integer> getRolesRequeridos() {
        return new HashMap<>(rolesRequeridos); 
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cancion cancion = (Cancion) obj;
        return titulo.equals(cancion.titulo);
    }

    @Override
    public int hashCode() {
        return titulo.hashCode();
    }
}
