
package Menu;
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
            System.out.println("\n" + "═".repeat(60));
            System.out.println("   🎭 MENÚ PRINCIPAL 🎭");
            System.out.println("═".repeat(60));
            contador = 1;
            for (Comando comando : comandos) {  // <-- este es el for-each
                System.out.println(String.format("   [%d] %s", contador, comando.getDescripcion()));
                contador++;
            }
            System.out.println(String.format("   [%d] 🚪 Salir del sistema", contador));
            System.out.println("═".repeat(60));
            System.out.print("\n👉 Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            if (opcion > 0 && opcion < contador) { 
                System.out.println();
                comandos.get(opcion - 1).ejecutar();
                System.out.println("\n✅ Operación completada. Presione Enter para continuar...");
                sc.nextLine();
                } else if (opcion == contador) { 
                    sc.close();
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("   👋 ¡Gracias por usar el sistema!");
                    System.out.println("   🎵 ¡Hasta pronto!");
                    System.out.println("=".repeat(60) + "\n");
                } else { 
                    System.out.println("\n❌ Opción inválida. Por favor, intente nuevamente.\n");
            }
        } while (opcion != contador);
        sc.close();
    }
}
