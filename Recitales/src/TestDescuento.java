import java.util.HashSet;
import Recital.Artista.*;
import Recital.Banda.Banda;
import Recital.Rol.Rol;

public class TestDescuento {
    public static void main(String[] args) {
        try {
            // Crear banda Queen
            Banda queen = new Banda("Queen");
            HashSet<Banda> bandasQueen = new HashSet<>();
            bandasQueen.add(queen);
            
            // Crear rol voz principal
            Rol vozPrincipal = new Rol("voz principal");
            HashSet<Rol> rolesVoz = new HashSet<>();
            rolesVoz.add(vozPrincipal);
            
            // Crear artista base Freddie Mercury
            ArtistaBase freddie = new ArtistaBase("Freddie Mercury", 100, 0, rolesVoz, bandasQueen);
            HashSet<ArtistaBase> artistasBase = new HashSet<>();
            artistasBase.add(freddie);
            
            // Crear Adam Lambert (artista externo con banda Queen)
            ArtistaExterno adam = new ArtistaExterno("Adam Lambert", 2, 1200, rolesVoz, bandasQueen);
            
            // Probar descuento
            double costoOriginal = adam.getCosto();
            double costoConDescuento = adam.getCostoConDescuento(artistasBase);
            
            System.out.println("=== TEST DESCUENTO POR BANDA COMPARTIDA ===");
            System.out.println("Adam Lambert:");
            System.out.println("  Costo original: $" + costoOriginal);
            System.out.println("  Costo con descuento: $" + costoConDescuento);
            System.out.println("  Descuento aplicado: " + (costoOriginal != costoConDescuento ? "SI" : "NO"));
            System.out.println("  Banda compartida: Queen");
            
            if (costoConDescuento == 600.0) {
                System.out.println("✓ CORRECTO: El descuento se aplicó correctamente ($600)");
            } else {
                System.out.println("✗ ERROR: El descuento no se aplicó correctamente");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}