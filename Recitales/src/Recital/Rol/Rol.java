package Recital.Rol;

public class Rol {
    private String nombre;
    
    public Rol(String nombre) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío.");
        }
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!(obj instanceof Rol)) return false;
        Rol rol = (Rol) obj;
        return nombre.equals(rol.nombre);
    }
}
