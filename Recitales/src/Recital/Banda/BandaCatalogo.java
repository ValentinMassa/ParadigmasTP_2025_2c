package Recital.Banda;
import java.util.HashSet;

public class BandaCatalogo {
    private HashSet<Banda> bandas;

    public BandaCatalogo() {
        this.bandas = new HashSet<>();
    }

    public Banda obtenerBanda(String nombreBanda) {
        
        if(!existeBanda(nombreBanda)) {
            return agregarBanda(nombreBanda);
        }
        return agregarBanda(nombreBanda);
    }

    public Banda obtenerBanda(Banda banda) {
        bandas.add(banda);
        return banda;
    }

    private Boolean existeBanda(String nombreBanda) {
        for (Banda banda : bandas) {
            if (banda.getNombre().equals(nombreBanda)) {
                return true;
            }
        }
        return false;
    }
    private Banda agregarBanda(String nombreBanda) {
        Banda nuevaBanda = new Banda(nombreBanda);
        bandas.add(nuevaBanda);
        return nuevaBanda;
    }
}
