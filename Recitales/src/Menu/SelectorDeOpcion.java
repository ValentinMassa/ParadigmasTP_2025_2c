package Menu;

import java.util.HashMap;
import java.util.Scanner;

public class SelectorDeOpcion {
    
    public static <T> T seleccionarDeLista(HashMap<Integer, T> mapaOpciones, String mensajePrompt, Scanner scanner) {
        String opcion;
        int opcionNum;
        
        System.out.print(mensajePrompt);
        opcion = scanner.nextLine();
        
        if (opcion.equalsIgnoreCase("S")) {
            System.out.println("\n🔙 Volviendo al menú principal...\n");
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