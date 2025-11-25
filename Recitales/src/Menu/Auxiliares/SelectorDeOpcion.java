package Menu.Auxiliares;

import java.util.HashMap;
import java.util.Scanner;

public class SelectorDeOpcion {
    
    public static <T> T seleccionarDeLista(HashMap<Integer, T> mapaOpciones, String mensajePrompt, Scanner scanner) {
        String opcion;
        int opcionNum;
        
        System.out.print(mensajePrompt);
        
        // Consumir líneas vacías silenciosamente
        while (true) {
            opcion = scanner.nextLine().trim();
            if (!opcion.isEmpty()) {
                break;
            }
            // No mostrar el prompt de nuevo, solo consumir la línea vacía
        }
        
        if (opcion.equalsIgnoreCase("S")) {
            System.out.println("\n[<<] Volviendo al menu principal...\n");
            return null;
        }
        
        try {
            opcionNum = Integer.parseInt(opcion);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Opción inválida. Debe ingresar un número o 'S' para salir.");
        }
        
        T seleccion = mapaOpciones.get(opcionNum);
        if (seleccion == null) {
            throw new IllegalArgumentException("Opción inválida. No existe una opción con ese número.");
        }
        
        return seleccion;
    }
}