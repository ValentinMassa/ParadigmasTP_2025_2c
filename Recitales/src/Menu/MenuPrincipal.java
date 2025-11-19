
package Menu;
import Recital.*;
import java.io.PrintStream;
import java.util.*;

public class MenuPrincipal {
    private List<Comando> comandos;
    public MenuPrincipal(List<Comando> comandos) {
        this.comandos = new ArrayList<>(comandos); 
    }
    public void agregarComando(Comando comando) {
        this.comandos.add(comando);
    }
    
    public void mostrar() {
        int opcion;
        int contador;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("=== Menú ===");
            contador = 1;
            for (Comando comando : comandos) {  // <-- este es el for-each
                System.out.println(contador + ". " + comando.getDescripcion());
                contador++;
            }
            System.out.println(contador + ". Salir");
            System.out.print("Ingrese una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            if (opcion > 0 && opcion < contador) { 
                comandos.get(opcion - 1).ejecutar();
                } else if (opcion == contador) { 
                    System.out.println("Saliendo...");
                } else { 
                    System.out.println("Opción inválida.");
            }
        } while (opcion != contador);
    }
}
