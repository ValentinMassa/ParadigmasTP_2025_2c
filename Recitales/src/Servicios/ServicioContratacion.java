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
    public HashMap<Rol, Integer> contratarArtistasParaCancion(Cancion cancion, RepositorioArtistasMemory repo) {     
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
    public HashMap<Rol, Integer> contratarParaTodo(ServicioConsulta sc){
        // Estructuras principales
        HashMap<Artista, Integer> maxCancionesPorArtista = new HashMap<>();
        List<Contrato> nuevosContratos = new ArrayList<>();
        
        HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes = sc.calcularRolesFaltantes(this);
        HashMap<Rol, Integer> rolesQueRequierenEntrenamiento = new HashMap<>();
        
        // Restar roles ya contratados (contratos reales existentes)
        for (Contrato contrato : this.contratos) {
            HashMap<Rol, Integer> rolesCancion = rolesFaltantes.get(contrato.getCancion());
            if (rolesCancion != null && rolesCancion.containsKey(contrato.getRol())) {
                int cantidad = rolesCancion.get(contrato.getRol());
                if (cantidad > 0) {
                    rolesCancion.put(contrato.getRol(), cantidad - 1);
                }
            }
        }

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
            
            // ASIGNAR ARTISTAS
            for (Rol rol : rolesCancion.keySet()) {
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
                    
                    // Tomar el más barato
                    Artista mejorArtista = listaOrdenada.get(0).getKey();
                    double costoFinal = listaOrdenada.get(0).getValue();
                    
                    // Registrar contrato tentativo
                    Contrato nuevoContrato = new Contrato(cancion, rol, mejorArtista, costoFinal);
                    nuevosContratos.add(nuevoContrato);
                    
                    // Actualizar max canciones
                    int capacidadActual = maxCancionesPorArtista.get(mejorArtista) - 1;
                    maxCancionesPorArtista.put(mejorArtista, capacidadActual);
                    
                    // Si ya no puede más, eliminarlo de todas las listas de candidatos
                    if (capacidadActual == 0) {
                        eliminarArtistaDeTodosLosRoles(candidatosPorRol, mejorArtista);
                    } else {
                        // Solo eliminar del rol actual para evitar doble asignación en misma canción
                        mapaCandidatos.remove(mejorArtista);
                    }
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
            
            // Simular contratos existentes + nuevos para calcular descuento correcto
            List<Contrato> contratosSimulados = new ArrayList<>(this.contratos);
            contratosSimulados.addAll(nuevosContratos);
            
            // Obtener el costo con descuento usando el método existente (simula el descuento)
            double costoBaseConDescuento = obtenerCostoExterno(externo, cancion, contratosSimulados);
            // Si obtenerCostoExterno aplicó descuento, también lo aplicamos al costo con entrenamiento
            // Prevenir división por cero
            double factorDescuento = (costoActual == 0) ? 1.0 : (costoBaseConDescuento / costoActual);
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

