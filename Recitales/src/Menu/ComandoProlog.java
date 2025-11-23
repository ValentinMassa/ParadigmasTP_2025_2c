package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.EntrenamientosProlog;
import Servicios.EntrenamientosProlog.ResultadoEntrenamiento;
import Artista.ArtistaExterno;
import java.util.HashSet;
import java.util.Scanner;

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
            HashSet<ArtistaExterno> artistasContratados = servicioContratacion.obtenerExternosSinExperiencia(
                servC.getRepositorioArtistas()
            );
            EntrenamientosProlog servicioProlog = new EntrenamientosProlog(servC);
            ResultadoEntrenamiento resultado = servicioProlog.calcularEntrenamientosConParametros(
                costoBase, 
                artistasContratados
            );
            
            imprimirResultados(resultado);
            
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
