package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioProlog;
import Servicios.ServicioProlog.ResultadoEntrenamiento;
import java.util.Scanner;
import java.util.Map;
import Recital.Cancion;
import Recital.Rol;

public class ComandoProlog implements Comando{
    private ServicioConsulta servC;
    private ServicioContratacion servicioContratacion;

    public ComandoProlog(ServicioConsulta sc, ServicioContratacion servicioContratacion){
        if(sc == null || servicioContratacion == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servicioContratacion = servicioContratacion;
    }

    private double solicitarCostoBase(Scanner scanner) {
        System.out.print("\n>> Ingrese el costo base por entrenamiento: $");
        try {
            double costoBase = Double.parseDouble(scanner.nextLine().trim());
            if (costoBase < 0) {
                System.out.println("\n[!] ERROR: El costo no puede ser negativo.");
                return -1;
            }
            return costoBase;
        } catch (NumberFormatException e) {
            System.out.println("\n[!] ERROR: Debe ingresar un número válido.");
            return -1;
        }
    }


    private void imprimirResultados(ResultadoEntrenamiento resultado) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("         RESULTADOS DEL ANÁLISIS");
        System.out.println("-".repeat(60));
        System.out.println(String.format("   Entrenamientos mínimos necesarios: %d", resultado.getEntrenamientosMinimos()));
        System.out.println(String.format("   Costo total: $%.2f", resultado.getCostoTotal()));
        System.out.println(String.format("   Total de roles requeridos: %d", resultado.getRolesRequeridosTotales()));
        
        if (!resultado.getRolesFaltantes().isEmpty()) {
            System.out.println("\n   Roles que requieren entrenamiento:");
            for (String rol : resultado.getRolesFaltantes()) {
                System.out.println(String.format("      [!] %s", rol));
            }

            System.out.println("\n   Cantidad total por rol que requiere entrenamiento:");
            for (Map.Entry<Rol, Integer> entry : resultado.getRolesFaltantesPorRol().entrySet()) {
                if (entry.getValue() > 0) {
                    System.out.println(String.format("      %s: %d", entry.getKey().getNombre(), entry.getValue()));
                }
            }

            System.out.println("\n   Roles faltantes por canción:");
            for (Map.Entry<Cancion, Map<Rol, Integer>> entryCancion : resultado.getRolesFaltantesPorCancion().entrySet()) {
                Cancion cancion = entryCancion.getKey();
                Map<Rol, Integer> roles = entryCancion.getValue();
                boolean hasFaltantes = roles.values().stream().anyMatch(v -> v > 0);
                if (hasFaltantes) {
                    System.out.println(String.format("      %s:", cancion.getTitulo()));
                    for (Map.Entry<Rol, Integer> entryRol : roles.entrySet()) {
                        if (entryRol.getValue() > 0) {
                            System.out.println(String.format("         %s: %d", entryRol.getKey().getNombre(), entryRol.getValue()));
                        }
                    }
                }
            }
        } else {
            System.out.println("\n   [OK] Todos los roles están cubiertos.");
        }
        System.out.println("-".repeat(60));
    }

    public void ejecutar() {
        Scanner scanner = new Scanner(System.in);
        
        double costoBase = solicitarCostoBase(scanner);
        if (costoBase < 0) {
            return;
        }
        
        System.out.println("\n[*] Calculando entrenamientos con Prolog...");
        
        try {
            ServicioProlog servicioProlog = new ServicioProlog(servC, servicioContratacion);
            ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(costoBase);
            
            imprimirResultados(resultado);
            
        } catch (UnsatisfiedLinkError e) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("         ERROR DE CONFIGURACIÓN PROLOG");
            System.out.println("-".repeat(60));
            System.out.println("   [!] No se encontró la instalación de SWI-Prolog.");
            System.out.println("   [!] El sistema requiere SWI-Prolog (64-bit) instalado.");
            System.out.println("   [!] Detalle: " + e.getMessage());
            System.out.println("-".repeat(60));
        } catch (Exception e) {
            System.out.println("\n" + "-".repeat(60));
            System.out.println("         ERROR EN EL CÁLCULO");
            System.out.println("-".repeat(60));
            System.out.println("   [!] " + e.getMessage());
            System.out.println("-".repeat(60));
        }
    }

    public String getDescripcion() {
        return "Calcular entrenamientos minimos (Prolog)";
    }
}
