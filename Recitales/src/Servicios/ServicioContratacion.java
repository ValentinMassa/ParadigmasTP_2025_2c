package Servicios;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Artista.*;
import Recital.*;
import Repositorios.RepositorioArtistas;


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
    public HashMap<Rol, Integer> contratarArtistasParaCancion(Cancion cancion, RepositorioArtistas repo) {     
        HashMap<Rol, Integer> rolesFaltantes = cancion.getRolesFaltantes(this.getContratosPorCancion(cancion));
        
        if (!hayRolesFaltantes(rolesFaltantes)) {
            System.out.println("\n[OK] Perfecto! No hay roles faltantes para la cancion '" + cancion.getTitulo() + "'");
            return null; // Todos los roles están cubiertos
        }

        for(ArtistaDiscografica artista : repo.getArtistasDiscografica()) {
            Contrato contrato = posibleContrato(artista, cancion, rolesFaltantes);
            if (contrato != null) {
                contratos.add(contrato);
                Rol rolTomado = contrato.getRol();
                rolesFaltantes.put(rolTomado, rolesFaltantes.get(rolTomado) - 1);
                artista.setCantCancionesAsignado(artista.getCantCancionesAsignadas() + 1);
            }
        }
        // Contratación para roles faltantes con artistas externos
        for (Rol rol : rolesFaltantes.keySet()) {
            while (rolesFaltantes.get(rol) > 0) {

                ArtistaExterno mejorExterno = null;
                double menorCosto = Double.MAX_VALUE;

                for (ArtistaExterno externo : repo.getArtistasExternos()) {
                    if (!tieneContratoConCancion(externo, cancion) && externo.puedeTocarRol(rol) 
                        && externo.getCantCancionesAsignadas() < externo.getMaxCanciones()) {
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
                    // actualizar contador de canciones del artista externo
                    mejorExterno.setCantCancionesAsignado(mejorExterno.getCantCancionesAsignadas() + 1);
                } else {
                    // No hay artistas externos disponibles para este rol
                    break;
                }
            
            }
        }
        
        // Verificar si aún quedan roles sin cubrir (requieren entrenamiento)
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = new HashMap<>();
        for (Rol rol : rolesFaltantes.keySet()) {
            int cantidad = rolesFaltantes.get(rol);
            if (cantidad > 0) {
                rolesQueRequierenEntrenamiento.put(rol, cantidad);
            }
        }
        
        return rolesQueRequierenEntrenamiento.isEmpty() ? null : rolesQueRequierenEntrenamiento;
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

    private boolean tieneContratoConCancion(Artista a, Cancion c) {
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
        return obtenerCostoExterno(externo, c, this.contratos);
    }

    /*
    * Calcula el costo de contratar a un artista externo para una canción, aplicando descuentos si corresponde.
    * @param externo El artista externo a contratar.
    * @param c La canción para la cual se está considerando la contratación.
    * @param contratos Lista de contratos a considerar (existentes o tentativos).
    * @return El costo total de la contratación, considerando descuentos por bandas compartidas.
    */
    private double obtenerCostoExterno(ArtistaExterno externo, Cancion c, List<Contrato> contratos) {
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
    public boolean tieneAlgunContrato(Artista a) {
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
        // Contar cuántos contratos se van a eliminar
        int contratosEliminados = 0;
        for (Contrato contrato : contratos) {
            if (contrato.getArtista().equals(artista)) {
                contratosEliminados++;
            }
        }
        
        // Eliminar los contratos
        contratos.removeIf(contrato -> contrato.getArtista().equals(artista));
        
        // Actualizar el contador del artista
        if (contratosEliminados > 0) {
            int cancionesActuales = artista.getCantCancionesAsignadas();
            artista.setCantCancionesAsignado(Math.max(0, cancionesActuales - contratosEliminados));
        }
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
    public HashMap<Rol, Integer> contratarParaTodo(ServicioConsulta sc){
        // Estructuras principales
        HashMap<Artista, Integer> maxCancionesPorArtista = new HashMap<>();
        List<Contrato> nuevosContratos = new ArrayList<>();
        
        // calcularRolesFaltantes() YA resta los contratos existentes, no hacerlo de nuevo
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = sc.calcularRolesFaltantes(this);
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = new HashMap<>();

        // Verificar si hay algo que contratar
        if (!hayRolesFaltantesEnAlgunaCancion(rolesFaltantes)) {
            System.out.println("\n[OK] Todas las canciones ya tienen sus roles cubiertos!");
            return null;
        }

        // Inicializar capacidades disponibles para todos los artistas
        for (ArtistaDiscografica artista : sc.getArtistasDiscografica()) {
            maxCancionesPorArtista.put(artista, artista.getMaxCanciones() - artista.getCantCancionesAsignadas());
        }
        for (ArtistaExterno artista : sc.getArtistasExternos()) {
            maxCancionesPorArtista.put(artista, artista.getMaxCanciones() - artista.getCantCancionesAsignadas());
        }

        // PASO 1: Contratar artistas base
        for (Cancion cancion : rolesFaltantes.keySet()) {
            HashMap<Rol, Integer> rolesFaltantesCancion = rolesFaltantes.get(cancion);
            
            for (ArtistaDiscografica artista : sc.getArtistasDiscografica()) {
                if (maxCancionesPorArtista.get(artista) <= 0) continue;
                if (yaEstaContratadoEnCancion(artista, cancion, nuevosContratos)) continue;
                
                Contrato contratoPosible = posibleContrato(artista, cancion, rolesFaltantesCancion);
                if (contratoPosible != null) {
                    nuevosContratos.add(contratoPosible);
                    
                    // Actualizar roles faltantes
                    Rol rolTomado = contratoPosible.getRol();
                    rolesFaltantesCancion.put(rolTomado, rolesFaltantesCancion.get(rolTomado) - 1);
                    
                    // Decrementar capacidad
                    int capacidadActual = maxCancionesPorArtista.get(artista);
                    maxCancionesPorArtista.put(artista, capacidadActual - 1);
                }
            }
        }

        // PASO 2: Para cada canción, generar candidatos y asignar artistas externos
        for (Cancion cancion : rolesFaltantes.keySet()) {
            HashMap<Rol, Integer> rolesCancion = rolesFaltantes.get(cancion);
            
            // Generar estructura: HashMap<Rol, HashMap<Artista, Double>>
            HashMap<Rol, HashMap<Artista, Double>> candidatosPorRol = new HashMap<>();
            // generar candidatos
            for (Rol rol : rolesCancion.keySet()) {
                if (rolesCancion.get(rol) == 0) continue; // Ya cubierto
                
                HashMap<Artista, Double> mapaCandidatos = new HashMap<>();
                
                for (Artista artista : sc.getArtistasPorRol(rol)) {
                    // Filtrar por max canciones
                    if (maxCancionesPorArtista.get(artista) == 0) continue;
                    
                    // Verificar si ya está contratado en esta canción
                    if (yaEstaContratadoEnCancion(artista, cancion, nuevosContratos)) continue;
                    
                    // Calcular costo con descuento si aplica
                    double costo = (artista instanceof ArtistaExterno) 
                        ? obtenerCostoExterno((ArtistaExterno) artista, cancion, nuevosContratos)
                        : artista.getCosto();
                    
                    mapaCandidatos.put(artista, costo);
                }
                
                candidatosPorRol.put(rol, mapaCandidatos);
            }
            
            // ✨ OPTIMIZACIÓN: Ordenar roles por cantidad de candidatos (menos candidatos = más crítico)
            List<Rol> rolesOrdenadosPorEscasez = new ArrayList<>(rolesCancion.keySet());
            rolesOrdenadosPorEscasez.sort((rol1, rol2) -> {
                int candidatos1 = candidatosPorRol.getOrDefault(rol1, new HashMap<>()).size();
                int candidatos2 = candidatosPorRol.getOrDefault(rol2, new HashMap<>()).size();
                return Integer.compare(candidatos1, candidatos2); // Ascendente: menos candidatos primero
            });
            
            // ASIGNAR ARTISTAS
            // Mantener registro de artistas ya usados en esta canción (restricción: un artista = un rol por canción)
            HashSet<Artista> artistasYaUsadosEnCancion = new HashSet<>();
            
            for (Rol rol : rolesOrdenadosPorEscasez) { // CAMBIO: usar lista ordenada en lugar de rolesCancion.keySet()
                int cantidadRequerida = rolesCancion.getOrDefault(rol, 0);
                
                for (int i = 0; i < cantidadRequerida; i++) {
                    // Ordenar candidatos por costo
                    HashMap<Artista, Double> mapaCandidatos = candidatosPorRol.get(rol);
                    if (mapaCandidatos == null || mapaCandidatos.isEmpty()) {
                        // No hay candidatos disponibles -> registrar rol que requiere entrenamiento
                        int cantidadFaltante = cantidadRequerida - i;
                        rolesQueRequierenEntrenamiento.put(rol, rolesQueRequierenEntrenamiento.getOrDefault(rol, 0) + cantidadFaltante);
                        break; // No hay más candidatos para este rol, pasar al siguiente
                    }
                    
                    List<Map.Entry<Artista, Double>> listaOrdenada = 
                        new ArrayList<>(mapaCandidatos.entrySet());
                    listaOrdenada.sort(Map.Entry.comparingByValue());
                    
                    // Buscar el primer artista disponible que NO esté ya usado en esta canción
                    Artista mejorArtista = null;
                    double costoFinal = 0;
                    
                    for (Map.Entry<Artista, Double> entry : listaOrdenada) {
                        if (!artistasYaUsadosEnCancion.contains(entry.getKey())) {
                            mejorArtista = entry.getKey();
                            costoFinal = entry.getValue();
                            break;
                        }
                    }
                    
                    // Si no encontramos un artista disponible (todos ya están usados en esta canción)
                    if (mejorArtista == null) {
                        int cantidadFaltante = cantidadRequerida - i;
                        rolesQueRequierenEntrenamiento.put(rol, rolesQueRequierenEntrenamiento.getOrDefault(rol, 0) + cantidadFaltante);
                        break;
                    }
                    
                    // Registrar contrato tentativo
                    Contrato nuevoContrato = new Contrato(cancion, rol, mejorArtista, costoFinal);
                    nuevosContratos.add(nuevoContrato);
                    
                    // Marcar al artista como ya usado en esta canción
                    artistasYaUsadosEnCancion.add(mejorArtista);
                    
                    // Actualizar max canciones
                    int capacidadActual = maxCancionesPorArtista.get(mejorArtista) - 1;
                    maxCancionesPorArtista.put(mejorArtista, capacidadActual);
                    
                    // Si ya no puede más canciones, eliminarlo de todas las listas de candidatos
                    if (capacidadActual == 0) {
                        eliminarArtistaDeTodosLosRoles(candidatosPorRol, mejorArtista);
                    }
                    // No eliminamos del mapaCandidatos actual porque ya lo filtramos con artistasYaUsadosEnCancion
                }
            }
        }
        
        // Actualizar contadores de artistas antes de agregar contratos
        HashMap<Artista, Integer> contadoresPorArtista = new HashMap<>();
        
        for (Contrato contrato : nuevosContratos) {
            Artista artista = contrato.getArtista();
            contadoresPorArtista.put(artista, contadoresPorArtista.getOrDefault(artista, 0) + 1);
        }
        
        // Actualizar contadores de cada artista
        for (Map.Entry<Artista, Integer> entry : contadoresPorArtista.entrySet()) {
            Artista artista = entry.getKey();
            int cantidadNuevosContratos = entry.getValue();
            artista.setCantCancionesAsignado(artista.getCantCancionesAsignadas() + cantidadNuevosContratos);
        }
        
        // Agregar contratos nuevos evitando duplicados
        HashSet<Contrato> contratosExistentes = new HashSet<>(this.contratos);
        for (Contrato nuevo : nuevosContratos) {
            if (!contratosExistentes.contains(nuevo)) {
                this.contratos.add(nuevo);
            }
        }
    
        // Retornar null si todo se completó exitosamente, o la estructura con roles que requieren entrenamiento
        if (rolesQueRequierenEntrenamiento.isEmpty()) {
            
            return null; // Éxito: todos los roles fueron cubiertos
        } else {
            return rolesQueRequierenEntrenamiento; // Hay roles que requieren entrenamiento
        }
    }

    /**
     * Contrata artistas priorizando los que fueron recién entrenados para roles específicos.
     * Combina la priorización de entrenamientos con la optimización de escasez de candidatos.
     * 
     * @param sc El servicio de consulta
     * @param entrenamientosRealizados Lista de entrenamientos que se deben priorizar
     * @return null si todo fue completado, o HashMap con roles que aún requieren entrenamiento
     */
    public HashMap<Rol, Integer> contratarParaTodoConPrioridad(
            ServicioConsulta sc, 
            List<Menu.Auxiliares.EntrenadorMasivo.EntrenamientoRealizado> entrenamientosRealizados) {
        
        // PASO 0: Priorizar artistas entrenados
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = sc.calcularRolesFaltantes(this);
        
        for (Menu.Auxiliares.EntrenadorMasivo.EntrenamientoRealizado entrenamiento : entrenamientosRealizados) {
            Artista artistaEntrenado = entrenamiento.artista;
            Rol rolEntrenado = entrenamiento.rol;
            
            // Buscar una canción que necesite este rol y donde el artista tenga capacidad
            for (Cancion cancion : rolesFaltantes.keySet()) {
                HashMap<Rol, Integer> rolesCancion = rolesFaltantes.get(cancion);
                
                // Verificar si esta canción necesita el rol entrenado
                if (rolesCancion.containsKey(rolEntrenado) && rolesCancion.get(rolEntrenado) > 0) {
                    // Verificar que el artista no esté ya contratado en esta canción
                    if (!tieneContratoConCancion(artistaEntrenado, cancion)) {
                        // Verificar capacidad del artista
                        int cancionesAsignadas = artistaEntrenado.getCantCancionesAsignadas();
                        if (cancionesAsignadas < artistaEntrenado.getMaxCanciones()) {
                            // Calcular costo
                            double costo = (artistaEntrenado instanceof ArtistaExterno) 
                                ? obtenerCostoExterno((ArtistaExterno) artistaEntrenado, cancion)
                                : artistaEntrenado.getCosto();
                            
                            // Crear y agregar el contrato
                            Contrato nuevoContrato = new Contrato(cancion, rolEntrenado, artistaEntrenado, costo);
                            this.contratos.add(nuevoContrato);
                            artistaEntrenado.setCantCancionesAsignado(cancionesAsignadas + 1);
                            
                            // Actualizar roles faltantes
                            rolesCancion.put(rolEntrenado, rolesCancion.get(rolEntrenado) - 1);
                            
                            // Este artista ya fue asignado, pasar al siguiente entrenamiento
                            break;
                        }
                    }
                }
            }
        }
        
        // PASO 1: Ejecutar contratación normal con optimización de escasez
        return contratarParaTodo(sc);
    }

    /**
     * Verifica si un artista ya está contratado para una canción específica,
     * considerando tanto contratos existentes como nuevos contratos en proceso.
     */
    private boolean yaEstaContratadoEnCancion(Artista artista, Cancion cancion, List<Contrato> nuevosContratos) {
        // Verificar contratos existentes
        if (tieneContratoConCancion(artista, cancion)) {
            return true;
        }
        
        // Verificar nuevos contratos
        for (Contrato contrato : nuevosContratos) {
            if (contrato.getArtista().equals(artista) && contrato.getCancion().equals(cancion)) {
                return true;
            }
        }
        
        return false;
    }


    /**
     * Elimina un artista de todos los mapas de candidatos por rol.
     * Se usa cuando un artista agota su capacidad de canciones.
     * 
     * @param candidatosPorRol Estructura de candidatos por rol
     * @param artista El artista a eliminar
     */
    private void eliminarArtistaDeTodosLosRoles(HashMap<Rol, HashMap<Artista, Double>> candidatosPorRol, Artista artista) {
        for (HashMap<Artista, Double> mapaCandidatos : candidatosPorRol.values()) {
            mapaCandidatos.remove(artista);
        }
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

// HashSet<Rol, HashSet<Artistas>>: roles faltantas + artiastas que pueden hacer ese rol. El hashset se ordena por costo (mas barato primero)
// por cancion: max combinaciones (2) --> HashSet<Cancion, List<Combinaciones(conjunto de contratos posibles)>> 
// HashMap<Artista, MaxAsginado>

// costo, como rehacer esa combinacion -> hashmap<costo, claveCombinacion> / 0 0 0, 1 0 0

// contrata los base -> contrata un artista Externo (que comparta banda y sea el mas barato)
//                         List<Artista>, banda, precio (desc menor precio y comparte banda)
// HashMap<Boolean, Lista<Contratos>>

// new Lista<Contratos>>
// contrata los base -> contrata un artista Externo (que comparta banda y sea el mas barato)
//                         List<Artista>, banda, precio (desc menor precio y comparte banda)


// HashMap<Cancion, HashMap<Rol, List<Artista>, Int: costo>>

