package Menu;

import Servicios.ServicioConsulta;
import Servicios.ServicioContratacion;
import Servicios.ServicioEntrenamiento;
import Menu.Auxiliares.EntrenadorMasivo;
import Recital.Cancion;
import Recital.Rol;
import java.util.HashMap;
import java.util.Scanner;

public class ComandoContratarArtistas implements Comando{
    private ServicioContratacion servContr;
    private ServicioConsulta servC;
    private ServicioEntrenamiento servEntrenamiento;

    public ComandoContratarArtistas(ServicioConsulta sc, ServicioContratacion scontr, ServicioEntrenamiento servEntrenamiento){
        if(sc == null || scontr == null || servEntrenamiento == null){
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.servC = sc;
        this.servContr = scontr;
        this.servEntrenamiento = servEntrenamiento;
    }

    @Override
    public void ejecutar() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     CONTRATAR ARTISTAS PARA TODAS LAS CANCIONES");
        System.out.println("=".repeat(60));
        System.out.println("\n[*] Iniciando proceso de contratación masiva...");
        System.out.println("[*] Optimizando costos con descuentos contextuales...\n");
        
        HashMap<Rol, Integer> rolesFaltantes = servContr.contratarParaTodo(servC);
        
        if (rolesFaltantes == null) {
            mostrarContratacionExitosa();
        } else {
            manejarContratacionParcial(rolesFaltantes);
        }
    }

    @Override
    public String getDescripcion() {
        return "Contratar artistas para todas las canciones a la vez";
    }
    
    private void mostrarContratacionExitosa() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     [OK] CONTRATACION EXITOSA");
        System.out.println("=".repeat(60));
        
        mostrarEstadisticasContratacion();
    }
    
    private void mostrarEstadisticasContratacion() {
        System.out.println("\n[*] Total de contratos activos: " + servContr.getContratos().size());
        
        double costoTotal = 0;
        for (var contrato : servContr.getContratos()) {
            costoTotal += contrato.obtenerCostoContrato();
        }
        System.out.println("[*] Costo total acumulado: $" + String.format("%.2f", costoTotal));
        System.out.println("\n" + "=".repeat(60) + "\n");
    }
    
    private void manejarContratacionParcial(HashMap<Rol, Integer> rolesFaltantes) {
        mostrarMensajeContratacionParcial();
        mostrarRolesFaltantes(rolesFaltantes);
        
        Scanner scanner = new Scanner(System.in);
        java.util.List<EntrenadorMasivo.EntrenamientoRealizado> entrenamientosRealizados = 
            EntrenadorMasivo.entrenarRolesFaltantes(rolesFaltantes, servC, servContr, servEntrenamiento, scanner);
        
        System.out.println("[*] Reintentando contratación masiva tras entrenamientos...\n");
        rolesFaltantes = servContr.contratarParaTodoConPrioridad(servC, entrenamientosRealizados);
        
        if (rolesFaltantes == null) {
            mostrarContratacionCompletadaTrasEntrenamientos();
        } else {
            mostrarMensajeAunFaltanRoles();
        }
    }
    
    private void mostrarMensajeContratacionParcial() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           CONTRATACION PARCIAL");
        System.out.println("=".repeat(60));
        System.out.println("\n[!] No hay suficientes artistas disponibles para completar");
        System.out.println("    todas las canciones del recital.");
        System.out.println("\n[*] ROLES QUE REQUIEREN ENTRENAMIENTO:\n");
    }
    
    private void mostrarRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes) {
        for (Rol rol : rolesFaltantes.keySet()) {
            int cantidad = rolesFaltantes.get(rol);
            System.out.println("      - " + rol.getNombre() + " (cantidad: " + cantidad + ")");
        }
        
        System.out.println("=".repeat(60));
        System.out.println("\n[!] Debe entrenar artistas para estos roles antes de");
        System.out.println("    poder completar la contratación.\n");
    }
    
    private void mostrarContratacionCompletadaTrasEntrenamientos() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     [OK] CONTRATACION COMPLETADA TRAS ENTRENAMIENTOS");
        System.out.println("=".repeat(60));
        
        mostrarEstadisticasContratacion();
    }
    
    private void mostrarMensajeAunFaltanRoles() {
        System.out.println("\n[!] Aún faltan roles por entrenar. Regrese al menú para intentarlo nuevamente.");
        System.out.println("=".repeat(60));
    }

}
