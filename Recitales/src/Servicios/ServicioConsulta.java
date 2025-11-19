package Servicios;

import Repositorios.*;
import Recital.*;
import java.util.HashMap;
import java.util.List;


public class ServicioConsulta {
    private BandaCatalogo bandaCatalogo;
    private RepositorioArtistas repositorioArtistas;
    private RolCatalogo rolCatalogo;
    private Recital recital;
    
    
    public ServicioConsulta(BandaCatalogo bC, RepositorioArtistas rA, RolCatalogo rC, Recital recital  ) 
            throws IllegalArgumentException {
        if (bC == null || rA == null || rC == null || recital == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.bandaCatalogo = bC;
        this.repositorioArtistas = rA;
        this.rolCatalogo = rC;
        this.recital = recital;
    }

    public Cancion getCancionPorNombre(String nombre) {
        for (Cancion c : recital.getCanciones()) {
            if (c.getTitulo().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }

    public HashMap<Rol, Integer> getRolesDeCancion(Cancion cancion) {
        return cancion.getRolesRequeridos();
    }

    public HashMap<Rol, Integer> calcularRolesFaltantes(Cancion cancion, List<Contrato> contratos) {
        
        HashMap<Rol, Integer> rolesFaltantes = getRolesDeCancion(cancion);
        
        for (Contrato contrato : contratos) {
            Rol rolContratado = contrato.getRol();
            if (rolesFaltantes.containsKey(rolContratado)) {
                rolesFaltantes.put(rolContratado, Math.max(0, rolesFaltantes.get(rolContratado) - 1));
            }
        }
        return rolesFaltantes;
    }

    public HashMap<Cancion, HashMap<Rol, Integer>> getRolesDeTodasLasCanciones(){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = new HashMap<>();

        for (Cancion cancion : recital.getCanciones()) {
            resultado.put(cancion, cancion.getRolesRequeridos());
        }
        return resultado;
    }

    public HashMap<Cancion, HashMap<Rol, Integer>> calcularRolesFaltantesTodasLasCanciones(List<Contrato> contratos){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = getRolesDeTodasLasCanciones();
        
        //Freedy Mercury | Voz, Piano | max= 3 canciones

        //I love You | Voz = 0, Piano = 1

        // Por cada artista se itera por cada cancion y se reduce el rol contratado
        // Primero: restamos contratos
        // hashmap: Artista,(maximo-actual)
        // hasmap: HashMap<Cancion, HashMap<Rol, Integer>> rolesFaltantes
        // para cada artista 
        //    para cada cancion
        //        buscar contrato del artista en la cancion 
        //        si no hay: 
        //            buscar primer rol positivo y restas rol requerido y restas del maximo
        //  
        // Freddy, pedro, raul
        //  a,b,c
        // posibleC(ArtistaList, HashMap<Cancion, HashMap<Rol, Integer>>, Maximo(maximo-actual))
    
        //  restar a HashMap<Cancion, HashMap<Rol, Integer>> roles de contratos ya hechos
        //  for(Artista a : repositorioArtistas.getTodosLosArtistas())
        //  {
        //  posibleCUnitaria(a, HashMap<Cancion, HashMap<Rol, Integer>>): contratosout
        //  con los contratos se itera sobre hashmap y se reduce el rol contratado
        // sumar contratosout a contratos
        //  }
        
        for (Cancion cancion : recital.getCanciones()) {
            HashMap<Rol, Integer> rolesFaltantes = calcularRolesFaltantes(cancion, contratos);
            resultado.put(cancion, rolesFaltantes);
        }
        return resultado;
    }

    
}
