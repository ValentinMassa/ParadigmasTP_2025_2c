package Recital.Banda;

public class Banda {
    private String nombre;

    public Banda(String nombre) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la banda no puede ser nulo o vacío.");
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
        if(!(obj instanceof Banda)) return false;
        Banda banda = (Banda) obj;
        return nombre != null ? nombre.equals(banda.nombre) : banda.nombre == null;
    }
}
