package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;

public class ComandoContratarArtistas implements Comando{
    private ServicioContratacion servContr;
    private ServicioConsulta servC;

    public ComandoContratarArtistas(ServicioConsulta sc, ServicioContratacion scontr){
        if(sc == null || scontr == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
    }

    @Override
    public void ejecutar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     CONTRATAR ARTISTAS PARA TODAS LAS CANCIONES");
        System.out.println("=".repeat(60));
        System.out.println("\n[*] Iniciando proceso de contratación masiva...");
        System.out.println("[*] Optimizando costos con descuentos contextuales...\n");
        
        // Llamar al método contratarParaTodo
        boolean exito = servContr.contratarParaTodo(servC.getRecital(), servC);
        
        if (exito) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("          ✅ CONTRATACION EXITOSA");
            System.out.println("=".repeat(60));
            
            // Mostrar estadísticas
            System.out.println("\n[*] Total de contratos activos: " + servContr.getContratos().size());
            
            // Calcular costo total
            double costoTotal = 0;
            for (var contrato : servContr.getContratos()) {
                costoTotal += contrato.obtenerCostoContrato();
            }
            System.out.println("[*] Costo total acumulado: $" + String.format("%.2f", costoTotal));
            System.out.println("\n" + "=".repeat(60) + "\n");
        } else {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("           CONTRATACION FALLIDA");
            System.out.println("=".repeat(60));
            System.out.println("\n[!] No hay suficientes artistas disponibles para completar");
            System.out.println("    todas las canciones del recital.");
            System.out.println("\n" + "=".repeat(60) + "\n");
        }
    }

    @Override
    public String getDescripcion() {
        return "Contratar artistas para todas las canciones a la vez";
    }
}
