
package Menu;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuPrincipal {
    private List<Comando> comandos;
    private String nombreArchivoSalida;
    
    public MenuPrincipal(List<Comando> comandos) {
        this.comandos = new ArrayList<>(comandos); 
        this.nombreArchivoSalida = null;
    }
    
    public void agregarComando(Comando comando) {
        this.comandos.add(comando);
    }
    
    public String getNombreArchivoSalida() {
        return nombreArchivoSalida;
    }
    
    public void mostrar() {
        int opcion;
        int contador;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              >> MENU PRINCIPAL <<");
            System.out.println("=".repeat(60));
            contador = 1;
            for (Comando comando : comandos) {  // <-- este es el for-each
                System.out.println(String.format("   [%d] %s", contador, comando.getDescripcion()));
                contador++;
            }
            System.out.println(String.format("   [%d] Salir del sistema", contador));
            System.out.println("=".repeat(60));
            System.out.print("\n>> Seleccione una opcion: ");
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            if (opcion > 0 && opcion < contador) { 
                System.out.println();
                comandos.get(opcion - 1).ejecutar();
                System.out.println("\n[OK] Operacion completada. Presione Enter para continuar...");
                sc.nextLine();
                } else if (opcion == contador) {
                    // Solicitar nombre del archivo de salida
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("          >> EXPORTACION DE DATOS <<");
                    System.out.println("=".repeat(60));
                    System.out.print("\n>> Ingrese el nombre del archivo de salida (sin extension): ");
                    String nombreBase = sc.nextLine().trim();
                    
                    if (nombreBase.isEmpty()) {
                        nombreBase = "recital-out";
                        System.out.println("[INFO] Se usara el nombre por defecto: " + nombreBase);
                    }
                    
                    // Agregar fecha y hora al nombre
                    LocalDateTime ahora = LocalDateTime.now();
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
                    String fechaHora = ahora.format(formato);
                    nombreArchivoSalida = nombreBase + "_" + fechaHora;
                    
                    sc.close();
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("         Gracias por usar el sistema!");
                    System.out.println("                Hasta pronto!");
                    System.out.println("=".repeat(60) + "\n");
                } else { 
                    System.out.println("\n[ERROR] Opcion invalida. Por favor, intente nuevamente.\n");
            }
        } while (opcion != contador);
        sc.close();
    }
}
