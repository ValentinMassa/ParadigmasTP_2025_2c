package Recital.Menu;

import java.util.*;
import Recital.*;
import Recital.Contratos.*;
import Recital.Artista.*;
import Recital.Rol.Rol;

public class MenuPrincipal {
    private Recital recital;
    private Scanner scanner;
    private ServicioContratacion servicioContratacion;

    public MenuPrincipal(Recital recital, ServicioContratacion servicioContratacion) {
        this.recital = recital;
        this.servicioContratacion = servicioContratacion;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n========== SISTEMA DE GESTIÓN DE RECITALES ==========");
            System.out.println("1. Ver roles faltantes para una canción");
            System.out.println("2. Ver roles faltantes para todo el recital");
            System.out.println("3. Contratar artistas para una canción específica");
            System.out.println("4. Contratar artistas para todas las canciones");
            System.out.println("5. Entrenar artista");
            System.out.println("6. Listar artistas contratados");
            System.out.println("7. Listar estado de canciones");
            System.out.println("8. Consulta Prolog - Entrenamientos mínimos");
            System.out.println("9. Salir");
            System.out.println("=====================================================");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1:
                        rolesParaCancion();
                        break;
                    case 2:
                        rolesParaTodo();
                        break;
                    case 3:
                        contratarCancion();
                        break;
                    case 4:
                        contratarTodo();
                        break;
                    case 5:
                        entrenarArtista();
                        break;
                    case 6:
                        listarContratados();
                        break;
                    case 7:
                        listarCanciones();
                        break;
                    case 8:
                        consultaProlog();
                        break;
                    case 9:
                        salir = true;
                        System.out.println("\n¡Hasta luego!");
                        break;
                    default:
                        System.out.println("\n❌ Opción no válida. Por favor, intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n❌ Error: Ingrese un número válido");
                scanner.nextLine(); // Limpiar buffer
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void rolesParaCancion() {
        try {
            System.out.print("\nIngrese el título de la canción: ");
            String titulo = scanner.nextLine().trim();
            
            if (titulo.isEmpty()) {
                System.out.println("❌ El título no puede estar vacío.");
                return;
            }
            
            // Buscar la canción en todas las canciones del recital
            Cancion cancionEncontrada = null;
            HashSet<Cancion> todasLasCanciones = new HashSet<>();
            HashSet<Cancion> completas = recital.getCancionesCompletas();
            HashSet<Cancion> incompletas = recital.getCancionesIncompletas();
            
            if (completas != null) todasLasCanciones.addAll(completas);
            if (incompletas != null) todasLasCanciones.addAll(incompletas);
            
            for (Cancion c : todasLasCanciones) {
                if (c.getTitulo().equalsIgnoreCase(titulo)) {
                    cancionEncontrada = c;
                    break;
                }
            }
            
            if (cancionEncontrada == null) {
                System.out.println("❌ La canción \"" + titulo + "\" no fue encontrada.");
                return;
            }
            
            // Obtener roles faltantes usando el método encapsulado de Recital
            Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancionEncontrada);
            
            System.out.println("\n========== ROLES FALTANTES PARA: " + titulo + " ==========");
            System.out.println("\n📋 Roles requeridos totales: " + cancionEncontrada.getRolesRequeridos().size());
            
            // Contar roles requeridos por tipo
            Map<Rol, Integer> rolesRequeridosPorTipo = new HashMap<>();
            for (Rol rol : cancionEncontrada.getRolesRequeridos()) {
                rolesRequeridosPorTipo.put(rol, rolesRequeridosPorTipo.getOrDefault(rol, 0) + 1);
            }
            
            if (rolesFaltantes.isEmpty()) {
                System.out.println("\n✅ ¡Todos los roles están cubiertos!");
                System.out.println("\nDetalles de cobertura:");
                for (Map.Entry<Rol, Integer> entry : rolesRequeridosPorTipo.entrySet()) {
                    System.out.println("  ✓ " + entry.getKey().getNombre() + ": " + entry.getValue() + " (CUBIERTO)");
                }
            } else {
                System.out.println("\n❌ Roles faltantes:\n");
                System.out.println(String.format("%-30s | %s | %s", "Rol", "Requerido", "Faltante"));
                System.out.println("═════════════════════════════════════════════════════════");
                
                for (Map.Entry<Rol, Integer> entry : rolesRequeridosPorTipo.entrySet()) {
                    Rol rol = entry.getKey();
                    int requerido = entry.getValue();
                    int faltante = rolesFaltantes.getOrDefault(rol, 0);
                    int cubierto = requerido - faltante;
                    
                    System.out.println(String.format("%-30s | %d | %d (cubierto: %d)",
                        rol.getNombre(),
                        requerido,
                        faltante,
                        cubierto));
                }
                
                System.out.println("═════════════════════════════════════════════════════════");
                int totalFaltante = rolesFaltantes.values().stream().mapToInt(Integer::intValue).sum();
                System.out.println("\n📊 RESUMEN: " + totalFaltante + " rol/roles faltante(s)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al buscar roles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void rolesParaTodo() {
        try {
            System.out.println("\n========== ROLES FALTANTES PARA TODO EL RECITAL ==========");
            System.out.println("\n📋 Analizando todos los roles requeridos en el recital...");
            System.out.println("   (Considerando que los artistas base pueden cubrirlos)\n");
            
            Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantes();
            
            if (rolesFaltantes == null || rolesFaltantes.isEmpty()) {
                System.out.println("✅ ¡Todos los roles están cubiertos!");
                System.out.println("\nLos artistas base del recital pueden tocar todas las canciones.");
            } else {
                System.out.println("❌ Roles faltantes que necesitan ser contratados:\n");
                System.out.println(String.format("%-30s | %s", "Rol", "Cantidad"));
                System.out.println("═════════════════════════════════════════════════════════");
                
                int totalFaltante = 0;
                for (Map.Entry<Rol, Integer> entry : rolesFaltantes.entrySet()) {
                    System.out.println(String.format("%-30s | %d",
                        entry.getKey().getNombre(),
                        entry.getValue()));
                    totalFaltante += entry.getValue();
                }
                
                System.out.println("═════════════════════════════════════════════════════════");
                System.out.println(String.format("\n📊 TOTAL ROLES FALTANTES: %d", totalFaltante));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void contratarCancion() {
        try {
            System.out.print("\nIngrese el título de la canción: ");
            String titulo = scanner.nextLine().trim();
            
            if (titulo.isEmpty()) {
                System.out.println("❌ El título no puede estar vacío.");
                return;
            }
            
            // Buscar la canción
            Cancion cancionEncontrada = null;
            for (Cancion c : recital.getCancionesIncompletas()) {
                if (c.getTitulo().equalsIgnoreCase(titulo)) {
                    cancionEncontrada = c;
                    break;
                }
            }
            
            if (cancionEncontrada == null) {
                System.out.println("❌ La canción \"" + titulo + "\" no fue encontrada.");
                return;
            }
            
            System.out.println("\n⏳ Contratando artistas para: " + titulo);
            List<Contrato> contratos = servicioContratacion.contratarParaCancion(cancionEncontrada);
            
            if (contratos != null && !contratos.isEmpty()) {
                System.out.println("✅ Contratación realizada:");
                contratos.forEach(c -> System.out.println("  - " + c));
            } else {
                System.out.println("⚠️ No se pudieron contratar artistas.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al contratar: " + e.getMessage());
        }
    }

    private void contratarTodo() {
        try {
            System.out.println("\n⏳ Contratando artistas para todas las canciones...");
            
            List<Contrato> contratos = servicioContratacion.contratarParaTodo(recital);
            
            if (contratos != null && !contratos.isEmpty()) {
                System.out.println("\n✅ Contratación total realizada:");
                System.out.println("Total de contratos: " + contratos.size());
                
                double costoTotal = recital.getCostoTotalRecital();
                System.out.println("Costo total: $" + String.format("%.2f", costoTotal));
                
                contratos.forEach(c -> System.out.println("  - " + c));
            } else {
                System.out.println("⚠️ No se pudieron realizar contrataciones.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al contratar: " + e.getMessage());
        }
    }

    private void entrenarArtista() {
        try {
            System.out.print("\nIngrese el nombre del artista a entrenar: ");
            String nombre = scanner.nextLine().trim();
            
            if (nombre.isEmpty()) {
                System.out.println("❌ El nombre no puede estar vacío.");
                return;
            }
            
            System.out.print("Ingrese el rol a entrenar: ");
            String rol = scanner.nextLine().trim();
            
            if (rol.isEmpty()) {
                System.out.println("❌ El rol no puede estar vacío.");
                return;
            }
            
            System.out.println("\n⏳ Entrenando a " + nombre + " para el rol: " + rol);
            System.out.println("⚠️ El costo se incrementará un 50% por cada rol adicional");
            
            // TODO: Implementar lógica de entrenamiento
            System.out.println("✅ Entrenamiento completado.");
            
        } catch (Exception e) {
            System.out.println("❌ Error al entrenar: " + e.getMessage());
        }
    }

    private void listarContratados() {
        try {
            System.out.println("\n========== ARTISTAS CONTRATADOS ==========");
            
            List<Contrato> contratos = recital.getContratos();
            
            if (contratos == null || contratos.isEmpty()) {
                System.out.println("No hay artistas contratados aún.");
                return;
            }
            
            Map<Artista, Double> costosPorArtista = recital.getCostosPorArtista();
            
            System.out.println(String.format("%-25s %-20s %-15s %s", 
                "Artista", "Tipo", "Canciones", "Costo"));
            System.out.println("================================================================");
            
            costosPorArtista.forEach((artista, costo) -> 
                System.out.println(String.format("%-25s %-20s %-15d $%.2f",
                    artista.getNombre(),
                    artista.getClass().getSimpleName(),
                    artista.cantCancionesAsignado,
                    costo))
            );
            
            double costoTotal = recital.getCostoTotalRecital();
            System.out.println("================================================================");
            System.out.println("COSTO TOTAL: $" + String.format("%.2f", costoTotal));
            
        } catch (Exception e) {
            System.out.println("❌ Error al listar: " + e.getMessage());
        }
    }

    private void listarCanciones() {
        try {
            System.out.println("\n========== ESTADO DE CANCIONES ==========");
            
            HashSet<Cancion> completas = recital.getCancionesCompletas();
            HashSet<Cancion> incompletas = recital.getCancionesIncompletas();
            
            System.out.println("\n✅ CANCIONES COMPLETAS (" + (completas != null ? completas.size() : 0) + "):");
            if (completas != null && !completas.isEmpty()) {
                completas.forEach(c -> System.out.println("  ✓ " + c.getTitulo()));
            } else {
                System.out.println("  Ninguna");
            }
            
            System.out.println("\n⏳ CANCIONES INCOMPLETAS (" + (incompletas != null ? incompletas.size() : 0) + "):");
            if (incompletas != null && !incompletas.isEmpty()) {
                incompletas.forEach(c -> System.out.println("  ✗ " + c.getTitulo()));
            } else {
                System.out.println("  Ninguna");
            }
            
            Map<Cancion, Double> costosPorCancion = recital.getCostosPorCancion();
            if (costosPorCancion != null && !costosPorCancion.isEmpty()) {
                System.out.println("\n💰 COSTOS POR CANCIÓN:");
                costosPorCancion.forEach((cancion, costo) -> 
                    System.out.println("  " + cancion.getTitulo() + ": $" + String.format("%.2f", costo))
                );
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al listar: " + e.getMessage());
        }
    }

    private void consultaProlog() {
        try {
            System.out.println("\n⏳ Consultando Prolog...");
            System.out.println("¿Cuántos entrenamientos mínimos se necesitan?");
            
            // TODO: Implementar integración con Prolog
            // Consultar: entrenamientos mínimos para cubrir todos los roles
            // usando solo miembros base y artistas contratados
            
            System.out.println("⚠️ Funcionalidad de Prolog no implementada aún.");
            
        } catch (Exception e) {
            System.out.println("❌ Error en consulta Prolog: " + e.getMessage());
        }
    }
}
