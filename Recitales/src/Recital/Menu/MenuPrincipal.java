package Recital.Menu;

import java.util.*;
import Recital.*;
import Recital.Contratos.*;
import Recital.Artista.*;
import Recital.Rol.Rol;
import Recital.Rol.RolCatalogo;
import Recital.Colaboraciones.GrafoColaboraciones;
import Recital.ServicioProlog.ServicioPrologIntegracion;

public class MenuPrincipal {
    private Recital recital;
    private Scanner scanner;
    private ServicioContratacion servicioContratacion;
    private RolCatalogo rolCatalogo;

    public MenuPrincipal(Recital recital, ServicioContratacion servicioContratacion, RolCatalogo rolCatalogo) {
        this.recital = recital;
        this.servicioContratacion = servicioContratacion;
        this.rolCatalogo = rolCatalogo;
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
            System.out.println("8. Listar artistas por rol en cada canción");
            System.out.println("9. Consulta Prolog - Entrenamientos mínimos");
            System.out.println("10. Quitar artista (Arrepentimiento)");
            System.out.println("11. Ver grafo de colaboraciones");
            System.out.println("12. Salir");
            System.out.println("=====================================================");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1:
                        rolesFaltantesParaCancion();
                        break;
                    case 2:
                        rolesFaltantesParaTodo();
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
                        listarArtistassPorRol();
                        break;
                    case 9:
                        consultaProlog();
                        break;
                    case 10:
                        quitarArtista();
                        break;
                    case 11:
                        mostrarColaboraciones();
                        break;
                    case 12:
                        salir = true;
                        System.out.println("\n¡Hasta luego!");
                        break;
                    default:
                        System.out.println("\nOpción no válida. Por favor, intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nError: Ingrese un número válido");
                scanner.nextLine(); // Limpiar buffer
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void rolesFaltantesParaCancion() {
        System.out.print("\nIngrese el título de la canción: ");
        String titulo = scanner.nextLine().trim();
        
        if (titulo.isEmpty()) {
            System.out.println("El título no puede estar vacío.");
            return;
        }

        // Buscar la canción en el recital
        Cancion cancionEncontrada = null;
        for (Cancion c : recital.getCanciones()) {
            if (c.getTitulo().equalsIgnoreCase(titulo)) {
                cancionEncontrada = c;
                break; 
            }
        }

        if (cancionEncontrada == null) {
            System.out.println("No se encontró la canción \"" + titulo + "\".");
            return;
        }

        Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancionEncontrada);

        if (rolesFaltantes.isEmpty()) {
            System.out.println("Todos los roles están cubiertos para \"" + titulo + "\".");
        } else {
            System.out.println("\nRoles faltantes para \"" + titulo + "\":");
            for (Map.Entry<Rol, Integer> entry : rolesFaltantes.entrySet()) {
                System.out.println("  - " + entry.getKey().getNombre() + ": " + entry.getValue());
            }
        }
    }

    private void rolesFaltantesParaTodo() {
        // Obtener todos los roles faltantes del recital
        Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantes();

        if (rolesFaltantes.isEmpty()) {
            System.out.println("Todos los roles están cubiertos para todo el recital.");
        } else {
            System.out.println("\nRoles faltantes para todo el recital:");
            for (Map.Entry<Rol, Integer> entry : rolesFaltantes.entrySet()) {
                System.out.println("  - " + entry.getKey().getNombre() + ": " + entry.getValue());
            }
        }
    }
    private void contratarCancion() {
        try {
            System.out.print("\nIngrese el título de la canción: ");
            String titulo = scanner.nextLine().trim();
            
            if (titulo.isEmpty()) {
                System.out.println("El título no puede estar vacío.");
                return;
            }
            
            // Buscar la canción
            Cancion cancionEncontrada = null;
            for (Cancion c : recital.getCanciones()) {
                if (c.getTitulo().equalsIgnoreCase(titulo)) {
                    cancionEncontrada = c;
                    break;
                }
            }
            
            if (cancionEncontrada == null) {
                System.out.println("La canción \"" + titulo + "\" no fue encontrada.");
                return;
            }
            
            System.out.println("\nContratando artistas para: " + titulo);
            List<Contrato> contratos = servicioContratacion.contratarParaCancion(recital, cancionEncontrada);
            
            if (contratos != null && !contratos.isEmpty()) {
                System.out.println("[OK] Contratación realizada:");
                contratos.forEach(c -> System.out.println("  - " + c));
            } else {
                System.out.println("No se pudieron contratar artistas.");
            }
            
        } catch (ServicioContratacion.ContratacionException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
            
            List<String> rolesNoDisponibles = e.getRolesNoDisponibles();
            int artistasDisponibles = e.getArtistasDisponiblesRestantes();
            
            System.out.println("\nDetalles del error:");
            System.out.println("  - Roles no disponibles: " + String.join(", ", rolesNoDisponibles));
            System.out.println("  - Artistas disponibles para entrenar: " + artistasDisponibles);
            
            // Ofrecer opción de entrenar
            if (artistasDisponibles > 0) {
                mostrarArtistasEntrenables();
                System.out.print("\n¿Desea entrenar artistas para cubrir estos roles? (s/n): ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                if (respuesta.equals("s")) {
                    entrenarArtista();
                    // Reintentar contratación
                    System.out.println("\nReintentando contratación...");
                    contratarCancion();
                }
            } else {
                System.out.println("\nNo hay artistas disponibles para entrenar.");
            }
            
        } catch (Exception e) {
            System.out.println("[ERROR] Error inesperado: " + e.getMessage());
        }
    }

    private void contratarTodo() {
        try {
            System.out.println("\nContratando artistas para todas las canciones...");
            
            List<Contrato> contratos = servicioContratacion.contratarParaTodo(recital);
            
            if (contratos != null && !contratos.isEmpty()) {
                System.out.println("\n[OK] Contratación total realizada:");
                System.out.println("Total de contratos: " + contratos.size());
                
                double costoTotal = recital.getCostoTotalRecital();
                System.out.println("Costo total: $" + String.format("%.2f", costoTotal));
                
                contratos.forEach(c -> System.out.println("  - " + c));
            } else {
                System.out.println("No se pudieron realizar contrataciones.");
            }
            
        } catch (ServicioContratacion.ContratacionException e) {
            System.out.println("\n[ERROR] ERROR EN CONTRATACIÓN: " + e.getMessage());
            
            mostrarArtistasEntrenables();
            System.out.print("\n¿Desea entrenar artistas para intentar nuevamente? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("s")) {
                entrenarArtista();
                // Reintentar contratación
                System.out.println("\nReintentando contratación...");
                contratarTodo();
            }
            
        } catch (Exception e) {
            System.out.println("[ERROR] Error inesperado: " + e.getMessage());
        }
    }

    private void entrenarArtista() {
        try {
            // Obtener entrada del usuario
            System.out.print("\nIngrese el nombre del artista a entrenar: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            
            System.out.print("Ingrese el rol a entrenar: ");
            String nombreRol = scanner.nextLine().trim();
            if (nombreRol.isEmpty()) {
                System.out.println("El rol no puede estar vacío.");
                return;
            }
            
            // Validación de rol y artista.
            ArtistaExterno artista = validarArtistaEntrenable(nombre, nombreRol);
            if (artista == null) return;
            
            // Entrenar artista
            Rol rol = rolCatalogo.obtenerRol(nombreRol);
            double costoAnterior = artista.getCosto();
            artista.agregarRol(rol);
            artista.incrementarCosto(1.5);  // Incrementar 50% (multiplicar por 1.5)
            double costoNuevo = artista.getCosto();
            
            //  Mostrar resultado
            System.out.println("\nArtista entrenado exitosamente!");
            System.out.println("Artista: " + artista.getNombre());
            System.out.println("Nuevo rol: " + rol.getNombre());
            System.out.println("Costo anterior: $" + String.format("%.2f", costoAnterior));
            System.out.println("Costo nuevo: $" + String.format("%.2f", costoNuevo));
            System.out.println("Incremento: $" + String.format("%.2f", (costoNuevo - costoAnterior)));
            
        } catch (Exception e) {
            System.out.println("Error al entrenar: " + e.getMessage());
        }
    }
    private ArtistaExterno validarArtistaEntrenable(String nombre, String nombreRol) {
        if (!rolCatalogo.existeRol(nombreRol)) {
            System.out.println("El rol ingresado no existe en el catálogo.");
            return null;
        }
        ArtistaExterno artista = buscarArtistaExterno(nombre);
        if (artista == null) {
            System.out.println("Artista no encontrado o base.");
            return null;
        }
        if (estaContratado(artista)) {
            System.out.println("El artista ya está contratado.");
            return null;
        }
        return artista;
    }
    private ArtistaExterno buscarArtistaExterno(String nombre) {
        for (ArtistaExterno a : recital.getArtistasExternos()) {
            if (a.getNombre().equalsIgnoreCase(nombre)) {
                return a;
            }
        }
        return null;
    }
    
    private boolean estaContratado(Artista artista) {
        for (Contrato c : recital.getContratos()) {
            if (c.getArtista().equals(artista)) {
                return true;
            }
        }
        return false;
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
                    artista.getCantCancionesAsignado(),
                    costo))
            );
            
            double costoTotal = recital.getCostoTotalRecital();
            System.out.println("================================================================");
            System.out.println("COSTO TOTAL: $" + String.format("%.2f", costoTotal));
            
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private void listarCanciones() {
        try {
            System.out.println("\n========== ESTADO DE CANCIONES ==========");
            System.out.println(String.format("%-30s %-25s %s", 
                "Canción", "Roles Faltantes", "Costo"));
            System.out.println("===========================================================================");
            
            Map<Cancion, Double> costosPorCancion = recital.getCostosPorCancion();
            
            for (Cancion cancion : recital.getCanciones()) {
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantesParaCancion(cancion);
                double costo = costosPorCancion.getOrDefault(cancion, 0.0);
                
                String rolesStr = rolesFaltantes.isEmpty() ? "Cubiertos" : 
                    rolesFaltantes.size() + " rol(es)";
                
                System.out.println(String.format("%-30s %-25s $%.2f", 
                    cancion.getTitulo(), 
                    rolesStr, 
                    costo));
            }
            
            double costoTotal = recital.getCostoTotalRecital();
            System.out.println("===========================================================================");
            System.out.println("COSTO TOTAL DEL RECITAL: $" + String.format("%.2f", costoTotal));
            
        } catch (Exception e) {
            System.out.println("Error al listar: " + e.getMessage());
        }
    }

    private void consultaProlog() {
        try {
            System.out.println("\n========== CONSULTA PROLOG ==========");
            System.out.println("Calculando entrenamientos mínimos necesarios...\n");
            
            ServicioPrologIntegracion servicio = new ServicioPrologIntegracion(recital);
            int entrenamientos = servicio.consultarEntrenamientosMínimos();
            
            if (entrenamientos == 0) {
                System.out.println("✓ No se necesitan entrenamientos adicionales.");
                System.out.println("Todos los roles pueden cubrirse con artistas base y contratados.");
            } else if (entrenamientos > 0) {
                System.out.println("✓ Entrenamientos mínimos necesarios: " + entrenamientos);
                System.out.println("Se necesita entrenar a " + entrenamientos + " artista(s) externo(s)");
                System.out.println("para cubrir todos los roles del recital.");
            } else {
                System.out.println("✗ Imposible cubrir todos los roles.");
                System.out.println("No hay suficientes artistas disponibles para entrenar.");
            }
            
        } catch (Exception e) {
            System.out.println("Error en consulta Prolog: " + e.getMessage());
        }
    }

    private void quitarArtista() {
        try {
            System.out.println("\n========== QUITAR ARTISTA (ARREPENTIMIENTO) ==========");
            
            List<Contrato> contratos = recital.getContratos();
            
            if (contratos == null || contratos.isEmpty()) {
                System.out.println("No hay artistas contratados para quitar.");
                return;
            }
            
            // Mostrar artistas contratados
            System.out.println("\nArtistas contratados disponibles para quitar:\n");
            Map<String, Artista> artistasUnicos = new HashMap<>();
            
            for (Contrato contrato : contratos) {
                Artista artista = contrato.getArtista();
                artistasUnicos.put(artista.getNombre(), artista);
            }
            
            System.out.println(String.format("%-25s %-10s %-15s %s", 
                "Artista", "Costo", "Canciones", "Roles"));
            System.out.println("================================================================");
            
            int index = 1;
            for (Artista artista : artistasUnicos.values()) {
                // Contar contratos de este artista
                int contratosDelArtista = 0;
                for (Contrato c : contratos) {
                    if (c.getArtista().equals(artista)) {
                        contratosDelArtista++;
                    }
                }
                
                StringBuilder roles = new StringBuilder();
                for (Rol r : artista.getRoles()) {
                    if (roles.length() > 0) roles.append(", ");
                    roles.append(r.getNombre());
                }
                
                System.out.println(String.format("%d. %-21s $%-9.2f %-15d %s", 
                    index,
                    artista.getNombre(), 
                    artista.getCosto(),
                    contratosDelArtista,
                    roles.toString()));
                index++;
            }
            
            System.out.print("\nIngrese el número del artista a quitar (0 para cancelar): ");
            int seleccion = scanner.nextInt();
            scanner.nextLine();
            
            if (seleccion == 0) {
                System.out.println("Operación cancelada.");
                return;
            }
            
            if (seleccion < 1 || seleccion > artistasUnicos.size()) {
                System.out.println("Selección inválida.");
                return;
            }
            
            // Obtener el artista seleccionado
            Artista artistaAQuitar = (Artista) artistasUnicos.values().toArray()[seleccion - 1];
            
            // Calcular costo total a recuperar
            double costoARecuperar = 0;
            int contratosAQuitar = 0;
            List<String> cancionesALiberar = new ArrayList<>();
            
            for (Contrato c : contratos) {
                if (c.getArtista().equals(artistaAQuitar)) {
                    costoARecuperar += c.obtenerCostoContrato();
                    contratosAQuitar++;
                    if (!cancionesALiberar.contains(c.getCancion().getTitulo())) {
                        cancionesALiberar.add(c.getCancion().getTitulo());
                    }
                }
            }
            
            // Confirmación
            System.out.println("\n========== RESUMEN DE CAMBIOS ==========");
            System.out.println("Artista a quitar: " + artistaAQuitar.getNombre());
            System.out.println("Contratos a eliminar: " + contratosAQuitar);
            System.out.println("Canciones a liberar: " + String.join(", ", cancionesALiberar));
            System.out.println("Costo a recuperar: $" + String.format("%.2f", costoARecuperar));
            System.out.println("Nuevo costo total: $" + String.format("%.2f", recital.getCostoTotalRecital() - costoARecuperar));
            
            System.out.print("\n¿Está seguro de quitar al artista? (s/n): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();
            
            if (!confirmacion.equals("s")) {
                System.out.println("Operación cancelada.");
                return;
            }
            
            // Ejecutar la eliminación
            if (recital.quitarArtista(artistaAQuitar)) {
                System.out.println("\n[OK] Artista eliminado exitosamente!");
                System.out.println("Se han liberado " + contratosAQuitar + " contrato(s).");
                System.out.println("Se ha recuperado: $" + String.format("%.2f", costoARecuperar));
                System.out.println("Nuevo costo total del recital: $" + String.format("%.2f", recital.getCostoTotalRecital()));
                
                // Mostrar roles que ahora están faltantes
                Map<Rol, Integer> rolesFaltantes = recital.getRolesFaltantes();
                if (!rolesFaltantes.isEmpty()) {
                    System.out.println("\nRoles que ahora están faltantes:");
                    for (Map.Entry<Rol, Integer> entry : rolesFaltantes.entrySet()) {
                        System.out.println("  - " + entry.getKey().getNombre() + ": " + entry.getValue());
                    }
                }
            } else {
                System.out.println("[ERROR] No se pudo quitar al artista.");
            }
            
        } catch (InputMismatchException e) {
            System.out.println("\nError: Ingrese un número válido");
            scanner.nextLine(); // Limpiar buffer
        } catch (Exception e) {
            System.out.println("[ERROR] Error al quitar artista: " + e.getMessage());
        }
    }

    private void listarArtistassPorRol() {
        try {
            System.out.println("\n========== ARTISTAS POR ROL EN CADA CANCIÓN ==========\n");
            
            for (Cancion cancion : recital.getCanciones()) {
                System.out.println("Cancion: " + cancion.getTitulo());
                System.out.println("Roles requeridos:");
                
                // Obtener contratos de esta canción
                Map<Rol, List<String>> rolesArtistas = new HashMap<>();
                
                for (Contrato contrato : recital.getContratos()) {
                    if (contrato.getCancion().equals(cancion)) {
                        Rol rol = contrato.getRol();
                        String artista = contrato.getArtista().getNombre();
                        rolesArtistas.computeIfAbsent(rol, k -> new ArrayList<>()).add(artista);
                    }
                }
                
                // Mostrar roles requeridos con artistas asignados
                for (Map.Entry<Rol, Integer> entry : cancion.getRolesRequeridos().entrySet()) {
                    Rol rol = entry.getKey();
                    int cantidad = entry.getValue();
                    List<String> artistas = rolesArtistas.getOrDefault(rol, new ArrayList<>());
                    
                    System.out.print("  - " + rol.getNombre() + " (x" + cantidad + "): ");
                    if (artistas.isEmpty()) {
                        System.out.println("[NO ASIGNADO]");
                    } else {
                        System.out.println(String.join(", ", artistas));
                    }
                }
                System.out.println();
            }
            
        } catch (Exception e) {
            System.out.println("[ERROR] Error al listar artistas por rol: " + e.getMessage());
        }
    }

    private void mostrarColaboraciones() {
        try {
            System.out.println("\n========== HISTORIAL DE COLABORACIONES ==========");
            
            // Recopilar todos los artistas (base y externos)
            Set<Artista> todosLosArtistas = new HashSet<>();
            todosLosArtistas.addAll(recital.getArtistasBase());
            todosLosArtistas.addAll(recital.getArtistasExternos());

            if (todosLosArtistas.isEmpty()) {
                System.out.println("No hay artistas registrados.");
                return;
            }

            GrafoColaboraciones grafo = new GrafoColaboraciones(todosLosArtistas);
            grafo.mostrarGrafo();

        } catch (Exception e) {
            System.out.println("[ERROR] Error al mostrar colaboraciones: " + e.getMessage());
        }
    }

    private void mostrarArtistasEntrenables() {
        System.out.println("\n========== ARTISTAS DISPONIBLES PARA ENTRENAR ==========");
        List<Artista> artistasEntrenables = new ArrayList<>();
        
        // Crear lista de nombres de artistas base para excluir
        Set<String> nombresArtistasBase = new HashSet<>();
        for (ArtistaBase base : recital.getArtistasBase()) {
            nombresArtistasBase.add(base.getNombre());
        }
        
        // Buscar artistas externos que NO son base y NO están contratados
        for (Artista a : recital.getArtistasExternos()) {
            boolean esArtistaBase = nombresArtistasBase.contains(a.getNombre());
            if (a.puedeSerEntrenado() && !estaContratado(a) && !esArtistaBase) {
                artistasEntrenables.add(a);
            }
        }
        
        if (artistasEntrenables.isEmpty()) {
            System.out.println("No hay artistas disponibles para entrenar.");
            System.out.println("(Los artistas base no pueden ser entrenados, y los artistas");
            System.out.println("contratados tampoco pueden ser entrenados)");
            return;
        }
        
        System.out.println(String.format("%-25s %-10s %s", 
            "Artista", "Costo", "Roles Actuales"));
        System.out.println("=========================================================");
        
        for (Artista artista : artistasEntrenables) {
            StringBuilder roles = new StringBuilder();
            for (Rol r : artista.getRoles()) {
                if (roles.length() > 0) roles.append(", ");
                roles.append(r.getNombre());
            }
            System.out.println(String.format("%-25s $%-9.2f %s", 
                artista.getNombre(), 
                artista.getCosto(),
                roles.toString()));
        }
        System.out.println("=========================================================");
        System.out.println("Total disponibles: " + artistasEntrenables.size());
    }
}
