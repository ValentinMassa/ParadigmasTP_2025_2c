package Repositorios;
import java.util.HashSet;

import Recital.Banda;
import Recital.Rol;

public class BandaCatalogoMemory {
    private HashSet<Banda> bandas;

    public BandaCatalogoMemory() {
        this.bandas = new HashSet<>();
    }

    public Boolean existeBanda(String nombreBanda) {
        for (Banda banda : bandas) {
            if (banda.getNombre().equalsIgnoreCase(nombreBanda)) {
                return true;
            }
        }
        return false;
    }

    public Banda getBanda(String nombreRol) {
        for (Banda banda : bandas) {
            if (banda.getNombre().equalsIgnoreCase(nombreRol)) {
                return banda;
            }
        }
        return null;
    }

    public Rol agregarBanda(String rolNuevo) {
        Rol r = new Rol(rolNuevo);   
        return this.bandas.add(new Banda(rolNuevo)) ? r : null;
    }

    public HashSet<Banda> getTodosLasBandas() {
        return new HashSet<>(bandas);
    }
}
