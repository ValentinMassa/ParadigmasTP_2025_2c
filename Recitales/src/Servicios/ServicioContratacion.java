package Servicios;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Artista.*;
import Recital.*;
import Repositorios.RepositorioArtistasMemory;


public class ServicioContratacion {
    private List<Contrato> contratos;
    private final double descuento_banda = 0.5;
    
     public ServicioContratacion() {
        this.contratos = new ArrayList<Contrato>();
    }
    
    /* 
        * Contrata artistas para una canción específica, priorizando artistas de discográficas y luego artistas externos para roles faltantes.
        * @param cancion La canción para la cual se desean contratar artistas.
        * @param repo El repositorio de artistas que contiene tanto artistas de discográficas como externos
        * @return void
    */
    public void contratarArtistasParaCancion(Cancion cancion, RepositorioArtistasMemory repo) {     
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(this.getContratosPorCancion(cancion));
        
        if (!hayRolesFaltantes(rolesFaltantes)) {
            System.out.println("\n[OK] Perfecto! No hay roles faltantes para la cancion '" + cancion.getTitulo() + "'");
            return;
        }

        for(ArtistaDiscografica artista : repo.getArtistasDiscografica()) {
            Contrato contrato = posibleContrato(artista, cancion, rolesFaltantes);
            if (contrato != null) {
                contratos.add(contrato);
                Rol rolTomado = contrato.getRol();
                rolesFaltantes.put(rolTomado, rolesFaltantes.get(rolTomado) - 1);
                artista.set_CantCancionesAsignado(artista.getCantCancionesAsignadas() + 1);
            }
        }
        // Contratación para roles faltantes con artistas externos
        for (Rol rol : rolesFaltantes.keySet()) {
            while (rolesFaltantes.get(rol) > 0) {

                ArtistaExterno mejorExterno = null;
                double menorCosto = Double.MAX_VALUE;

                for (ArtistaExterno externo : repo.getArtistasExternos()) {
                    if (!tieneContratoConCancion(externo, cancion) && externo.puedeTocarRol(rol)) {
                        double costo = obtenerCostoExterno(externo, cancion);
                        if (costo < menorCosto) {
                            menorCosto = costo;
                            mejorExterno = externo;
                        }
                    }
                }
                // Si encontramos uno, lo contratamos
                if (mejorExterno != null) {
                    Contrato contrato = new Contrato(cancion, rol, mejorExterno, menorCosto);
                    contratos.add(contrato);
                    // actualizar roles faltantes
                    rolesFaltantes.put(rol, rolesFaltantes.get(rol) - 1);
                } else {
                    // No hay artistas externos disponibles para este rol
                    break;
                }
            
            }
        }
    }

    private boolean hayRolesFaltantes(HashMap<Rol, Integer> rolesFaltantes) {
        for (Integer cantidad : rolesFaltantes.values()) {
            if (cantidad > 0) return true;
        }
        return false;
    }
    

    private Contrato posibleContrato(ArtistaDiscografica artista,
                                Cancion cancion,
                                Map<Rol, Integer> rolesFaltantes) {

        if (tieneContratoConCancion(artista, cancion))
            return null;

        for (Rol rol : rolesFaltantes.keySet()) {
            if (rolesFaltantes.get(rol) > 0 && artista.puedeTocarRol(rol)) {
                return new Contrato(cancion, rol, artista, artista.getCosto());
            }
        }
        return null;
    }
    
    public List<Contrato> posiblesContratos(ArtistaDiscografica artista,
                                        HashMap<Cancion, HashMap<Rol, Integer>> cancionesRoles) {

        List<Contrato> lista = new ArrayList<>();
        int cupo = artista.getMaxCanciones() - artista.getCantCancionesAsignadas();

        for (Cancion c : cancionesRoles.keySet()) {
            if (cupo <= 0) break;

            Contrato posible = posibleContrato(artista, c, cancionesRoles.get(c));

            if (posible != null) {
                lista.add(posible);
                cupo--;
            }
        }

        return lista;
    }
    
    public List<Contrato> getContratosPorCancion(Cancion cancion) {
        List<Contrato> contratosDeCancion = new ArrayList<Contrato>();

        for(Contrato contrato : contratos) {
            if (contrato.getCancion().equals(cancion)) {
                contratosDeCancion.add(contrato);
            }
        }
        return contratosDeCancion;
    }


    public List<Contrato> getContratos() {
        return contratos;
    }

    private Boolean tieneContratoConCancion(Artista a, Cancion c) {
        for (Contrato contrato : contratos) {
            if (contrato.getArtista().equals(a) && contrato.getCancion().equals(c)) {
                return true;
            }
        }
        return false;
    }

    /*
    * Calcula el costo de contratar a un artista externo para una canción, aplicando descuentos si corresponde.
    * @param externo El artista externo a contratar.
    * @param c La canción para la cual se está considerando la contratación.
    * @return El costo total de la contratación, considerando descuentos por bandas compartidas.
    */
    private double obtenerCostoExterno(ArtistaExterno externo, Cancion c) {
        HashSet<ArtistaDiscografica> artistasBaseContratados = new HashSet<>();
        double costo = externo.getCosto();
        // Obtengo artistas base contratados para la cancion
        for(Contrato contrato : contratos){
            if(contrato.getCancion().equals(c) && contrato.getArtista() instanceof ArtistaDiscografica){
                artistasBaseContratados.add((ArtistaDiscografica) contrato.getArtista());
            }
        }
        // Busco coincidencia en bandas entre Artista Externo y Artistas Base
        boolean yaAplicoDescuento = false;
        for(ArtistaDiscografica artistaBase : artistasBaseContratados){
            for(Banda banda : artistaBase.getBandas()){
                if(yaAplicoDescuento) break;
                if(externo.getBandas().contains(banda)){
                    costo *= descuento_banda;
                    yaAplicoDescuento = true;
                    break;
                }
            }
        }
        return costo;
    }

    /*
    * Verifica si un artista ya tiene algún contrato en el servicio de contratación.
    * @param a El artista a verificar.
    * @return true si el artista tiene al menos un contrato, false en caso contrario.
    */
    public Boolean tieneAlgunContrato(Artista a) {
        for (Contrato contrato : contratos) {
            if (contrato.getArtista().equals(a)) {
                return true;
            }
        }
        return false;
    }

    public void agregarContrato(Contrato contrato) {
        this.contratos.add(contrato);
    }

    public void actualizarDesdeSnapshot(List<Contrato> contratosSnapshot) {
        this.contratos = contratosSnapshot;
    }

    public void eliminarContratosDeArtista(Artista artista){
        contratos.removeIf(contrato -> contrato.getArtista().equals(artista));
    }


    
    /**
     * Contrata artistas para todas las canciones del recital optimizando el costo total.
     * Aplica descuentos contextuales: si un artista externo comparte banda histórica con
     * un artista base YA CONTRATADO EN ESA CANCIÓN, recibe 50% de descuento.
     * Las canciones que ya tienen todos sus roles cubiertos quedan exentas.
     * 
     * @param recital El recital con todas las canciones
     * @param sc El servicio de consulta para obtener roles faltantes
     * @return true si se pudieron contratar todos los roles faltantes, false si falta capacidad
     */
    public Boolean contratarParaTodo(Recital recital, ServicioConsulta sc){
        // Obtener roles faltantes (canciones completas quedan automáticamente excluidas)
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = sc.calcularRolesFaltantesTodasLasCanciones(this);
        
        // Verificar si hay algo que contratar
        if (!hayRolesFaltantesEnAlgunaCancion(rolesFaltantes)) {
            System.out.println("\n[OK] ¡Todas las canciones están completas! No hay roles faltantes.");
            return true;
        }
        
        List<Contrato> nuevosContratos = new ArrayList<>();
        RepositorioArtistasMemory repo = sc.getRepositorioArtistas();
        
        // Procesar cada canción
        for (Cancion cancion : rolesFaltantes.keySet()) {
            HashMap<Rol, Integer> rolesFaltantesCancion = rolesFaltantes.get(cancion);
            
            // Procesar cada rol faltante en esta canción
            for (Rol rol : rolesFaltantesCancion.keySet()) {
                int cantidadRequerida = rolesFaltantesCancion.get(rol);
                
                // Contratar la cantidad necesaria para este rol
                for (int i = 0; i < cantidadRequerida; i++) {
                    // Buscar el mejor candidato para este rol en esta canción
                    CandidatoOptimo mejor = encontrarMejorCandidato(
                        cancion, 
                        rol, 
                        repo,
                        nuevosContratos
                    );
                    
                    if (mejor == null) {
                        // No hay artistas disponibles, intentar entrenar
                        System.out.println("\n[ADVERTENCIA] No hay artistas disponibles para el rol '" 
                            + rol.getNombre() + "' en la canción '" + cancion.getTitulo() + "'");
                        System.out.println("[*] Buscando artistas entrenables...");
                        
                        ArtistaExterno artistaEntrenable = buscarArtistaEntrenable(rol, cancion, repo, nuevosContratos);
                        
                        if (artistaEntrenable == null) {
                            System.out.println("\n[ERROR] No hay artistas entrenables disponibles para el rol '" 
                                + rol.getNombre() + "'");
                            System.out.println("[!] No se puede completar la contratación del recital.");
                            return false;
                        }
                        
                        // Entrenar al artista
                        System.out.println("[ENTRENAMIENTO] Entrenando a " + artistaEntrenable.getNombre() 
                            + " en el rol " + rol.getNombre());
                        artistaEntrenable.agregarRolEntrenado(rol, 1.5); // 50% aumento de costo
                        
                        // Calcular costo con descuento contextual después del entrenamiento
                        double costoEfectivo = obtenerCostoExterno(artistaEntrenable, cancion);
                        
                        // Crear y agregar el contrato
                        Contrato nuevoContrato = new Contrato(cancion, rol, artistaEntrenable, costoEfectivo);
                        nuevosContratos.add(nuevoContrato);
                        artistaEntrenable.asignarCancion();
                        
                        System.out.println("[CONTRATADO] " + artistaEntrenable.getNombre() 
                            + " (entrenado) para '" + cancion.getTitulo() + "' como " + rol.getNombre() 
                            + " - Costo: $" + costoEfectivo);
                    } else {
                        // Crear y agregar el contrato con artista disponible
                        Contrato nuevoContrato = new Contrato(cancion, rol, mejor.artista, mejor.costoEfectivo);
                        nuevosContratos.add(nuevoContrato);
                        mejor.artista.asignarCancion();
                        
                        System.out.println("[CONTRATADO] " + mejor.artista.getNombre() 
                            + " para '" + cancion.getTitulo() + "' como " + rol.getNombre() 
                            + " - Costo: $" + mejor.costoEfectivo);
                    }
                }
            }
        }
        
        // Agregar todos los nuevos contratos
        this.contratos.addAll(nuevosContratos);
        
        System.out.println("\n[OK] ¡Contratación masiva completada exitosamente!");
        System.out.println("Total de nuevos contratos: " + nuevosContratos.size());
        
        return true;
    }
    
    /**
     * Clase auxiliar para representar un candidato con su costo efectivo calculado.
     */
    private static class CandidatoOptimo {
        Artista artista;
        double costoEfectivo;
        
        CandidatoOptimo(Artista artista, double costoEfectivo) {
            this.artista = artista;
            this.costoEfectivo = costoEfectivo;
        }
    }
    
    /**
     * Encuentra el mejor candidato (menor costo) para un rol específico en una canción.
     * Aplica descuento contextual si el candidato externo comparte banda con artistas base
     * YA CONTRATADOS EN ESA CANCIÓN ESPECÍFICA.
     */
    private CandidatoOptimo encontrarMejorCandidato(
            Cancion cancion, 
            Rol rol,
            RepositorioArtistasMemory repo,
            List<Contrato> nuevosContratos) {
        
        CandidatoOptimo mejorCandidato = null;
        double menorCosto = Double.MAX_VALUE;
        
        // 1. PRIORIDAD: Artistas de discográfica
        for (ArtistaDiscografica base : repo.getArtistasDiscografica()) {
            if (base.puedeTocarRol(rol) && 
                base.puedeAceptarNuevaCancion() &&
                !estaContratadoEnCancion(base, cancion, nuevosContratos)) {
                
                double costo = base.getCosto();
                if (costo < menorCosto) {
                    menorCosto = costo;
                    mejorCandidato = new CandidatoOptimo(base, costo);
                }
            }
        }
        
        // 2. Artistas externos (con descuento contextual)
        for (ArtistaExterno externo : repo.getArtistasExternos()) {
            if (externo.puedeTocarRol(rol) && 
                externo.puedeAceptarNuevaCancion() &&
                !estaContratadoEnCancion(externo, cancion, nuevosContratos)) {
                
                // Calcular costo con descuento CONTEXTUAL (solo para esta canción)
                double costo = obtenerCostoExterno(externo, cancion);
                
                if (costo < menorCosto) {
                    menorCosto = costo;
                    mejorCandidato = new CandidatoOptimo(externo, costo);
                }
            }
        }
        
        return mejorCandidato;
    }
    
    /**
     * Busca el artista externo MÁS BARATO que pueda ser entrenado para el rol faltante.
     * Considera TODOS los artistas externos sin importar contratos actuales.
     * Solo considera artistas externos que:
     * - Puedan ser entrenados (sean entrenables)
     * - NO tengan ya ese rol
     * - Tengan capacidad disponible para una nueva canción
     * 
     * @param rol El rol para el cual se busca un artista entrenable
     * @param cancion La canción para la cual se necesita el artista
     * @param repo El repositorio de artistas
     * @param nuevosContratos Lista de contratos en proceso
     * @return El mejor artista entrenable (menor costo después del entrenamiento) o null si no hay
     */
    private ArtistaExterno buscarArtistaEntrenable(
            Rol rol,
            Cancion cancion,
            RepositorioArtistasMemory repo,
            List<Contrato> nuevosContratos) {
        
        ArtistaExterno mejorEntrenable = null;
        double menorCostoConEntrenamiento = Double.MAX_VALUE;
        
        for (ArtistaExterno externo : repo.getArtistasExternos()) {
            // Verificar que pueda ser entrenado
            if (!externo.puedeSerEntrenado()) {
                continue;
            }
            
            // No debe tener el rol ya
            if (externo.puedeTocarRol(rol)) {
                continue;
            }
            
            // Debe tener capacidad disponible (considerando contratos existentes y nuevos)
            int cancionesContratadas = contarCancionesContratadas(externo, nuevosContratos);
            if (cancionesContratadas >= externo.getMaxCanciones()) {
                continue;
            }
            
            // Calcular costo después del entrenamiento (costo actual * 1.5)
            // y aplicar descuento contextual si corresponde
            double costoActual = externo.getCosto();
            double costoConEntrenamiento = costoActual * 1.5; // 50% aumento por entrenamiento
            
            // Obtener el costo con descuento usando el método existente (simula el descuento)
            double costoBaseConDescuento = obtenerCostoExterno(externo, cancion);
            // Si obtenerCostoExterno aplicó descuento, también lo aplicamos al costo con entrenamiento
            double factorDescuento = costoBaseConDescuento / costoActual; // 0.5 si hay descuento, 1.0 si no
            double costoFinal = costoConEntrenamiento * factorDescuento;
            
            if (costoFinal < menorCostoConEntrenamiento) {
                menorCostoConEntrenamiento = costoFinal;
                mejorEntrenable = externo;
            }
        }
        
        return mejorEntrenable;
    }
    
    /**
     * Cuenta cuántas canciones diferentes tiene contratadas un artista
     * (incluyendo contratos existentes y nuevos contratos en proceso).
     */
    private int contarCancionesContratadas(Artista artista, List<Contrato> nuevosContratos) {
        HashSet<Cancion> cancionesUnicas = new HashSet<>();
        
        // Contratos existentes
        for (Contrato contrato : this.contratos) {
            if (contrato.getArtista().equals(artista)) {
                cancionesUnicas.add(contrato.getCancion());
            }
        }
        
        // Nuevos contratos en proceso
        for (Contrato contrato : nuevosContratos) {
            if (contrato.getArtista().equals(artista)) {
                cancionesUnicas.add(contrato.getCancion());
            }
        }
        
        return cancionesUnicas.size();
    }
    
    /**
     * Verifica si un artista ya está contratado para una canción específica.
     * Revisa tanto contratos existentes como nuevos contratos en proceso.
     */
    private boolean estaContratadoEnCancion(
            Artista artista,
            Cancion cancion,
            List<Contrato> nuevosContratos) {
        
        // Verificar contratos existentes
        if (tieneContratoConCancion(artista, cancion)) {
            return true;
        }
        
        // Verificar nuevos contratos en proceso
        for (Contrato contrato : nuevosContratos) {
            if (contrato.getArtista().equals(artista) && 
                contrato.getCancion().equals(cancion)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si hay roles faltantes en al menos una canción.
     */
    private boolean hayRolesFaltantesEnAlgunaCancion(HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes) {
        for (HashMap<Rol, Integer> rolesCancion : rolesFaltantes.values()) {
            if (hayRolesFaltantes(rolesCancion)) {
                return true;
            }
        }
        return false;
    }
     
}





