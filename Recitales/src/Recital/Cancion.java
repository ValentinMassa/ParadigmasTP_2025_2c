package Recital;

import java.util.HashMap;
import java.util.List;

public class Cancion {
    private String titulo;
    private HashMap<Rol, Integer> rolesRequeridos;

    public Cancion(String titulo) throws IllegalArgumentException {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la canción no puede ser nulo o vacío");
        }
        this.titulo = titulo;
        this.rolesRequeridos = new HashMap<>();
    }

    public void agregarRolRequerido(Rol rol, int cantidad) throws IllegalArgumentException {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad requerida debe ser al menos 1");
        }
        rolesRequeridos.put(rol, cantidad);
    }

    public String getTitulo() {
        return titulo;
    }

    public HashMap<Rol, Integer> getRolesRequeridos() {
        return new HashMap<>(rolesRequeridos);
    }

    public HashMap<Rol, Integer> getRolesFaltantes(List<Contrato> contratosDeCancion) {
        HashMap<Rol, Integer> rolesReq = new HashMap<>(rolesRequeridos);
        for (Contrato contrato : contratosDeCancion) {
            Rol rolContratado = contrato.getRol();
            if (rolesReq.containsKey(rolContratado)) {
                rolesReq.put(rolContratado, Math.max(0, rolesReq.get(rolContratado) - 1));
            }
        }
        return this.rolesRequeridos;
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
