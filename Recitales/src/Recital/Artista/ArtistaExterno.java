package Recital.Artista;

import java.util.HashSet;
import Recital.Banda.Banda;
import Recital.Rol.Rol;

public class ArtistaExterno extends Artista {
    
    public ArtistaExterno(String nombre, int maxcanciones, double costo, HashSet<Rol> roles, HashSet<Banda> bandaHistorico) 
            throws IllegalArgumentException {
        super(nombre, maxcanciones, costo, roles, bandaHistorico);
    }

    @Override
    public Boolean puedeSerEntrenado() {
        return true; // Los artistas externos pueden ser entrenados
    }
    
    /**
     * Calcula el costo con descuento por banda compartida
     */
    public double getCostoConDescuento(java.util.HashSet<ArtistaBase> artistasBase) {
        double costoBase = getCosto();
        
        System.out.println("DEBUG getCostoConDescuento: " + getNombre() + " - Costo base: $" + costoBase);
        System.out.println("DEBUG: Bandas del artista: " + getBandasHistoricas().size());
        for (Banda b : getBandasHistoricas()) {
            System.out.println("  - " + b.getNombre());
        }
        
        // Verificar si comparte banda con algún artista base
        for (ArtistaBase base : artistasBase) {
            System.out.println("DEBUG: Comparando con base: " + base.getNombre());
            for (Banda bandaBase : base.getBandasHistoricas()) {
                System.out.println("  Banda del base: " + bandaBase.getNombre());
                if (getBandasHistoricas().contains(bandaBase)) {
                    System.out.println("  *** DESCUENTO APLICADO! ***");
                    return costoBase * 0.5; // 50% de descuento
                }
            }
        }
        
        System.out.println("DEBUG: Sin descuento - devolviendo: $" + costoBase);
        return costoBase;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ArtistaExterno otro = (ArtistaExterno) obj;
        return nombre.equals(otro.nombre);
    }
}
