package Servicios;

import Repositorios.*;
import Recital.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import Artista.ArtistaBase;



public class ServicioConsulta {
    private RepositorioArtistas repositorioArtistas;
    private Recital recital;
    
    
    public ServicioConsulta(RepositorioArtistas rA, Recital recital  ) 
            throws IllegalArgumentException {
        if (rA == null|| recital == null) {
            throw new IllegalArgumentException("Ningun parametro puede ser nulo");
        }
        this.repositorioArtistas = rA;
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
        return new HashMap<>(cancion.getRolesRequeridos());
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
            
            resultado.put(cancion, new HashMap<>(cancion.getRolesRequeridos()));
        }
        return resultado;
    }

    public List<ArtistaBase> getArtistasBase(){
        HashSet<ArtistaBase> a = repositorioArtistas.getArtistaBase();
        List<ArtistaBase> artistas = new ArrayList<>();
        artistas.addAll(a);
        return artistas;
    }

    private void restarRolDeCancion(HashMap<Cancion, HashMap<Rol, Integer>> roles, Cancion cancion, Rol rol) {
        HashMap<Rol, Integer> rolesFaltantes = roles.get(cancion);
        if (rolesFaltantes != null && rolesFaltantes.containsKey(rol)) {
            rolesFaltantes.put(rol, Math.max(0, rolesFaltantes.get(rol) - 1));
            
        }
    }
    public HashMap<Cancion, HashMap<Rol, Integer>> calcularRolesFaltantesTodasLasCanciones(ServicioContratacion servicioC){
        HashMap<Cancion, HashMap<Rol, Integer>> resultado = getRolesDeTodasLasCanciones();
        
        for (Contrato contrato : servicioC.getContratos()) {
            restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
        }

        for(ArtistaBase a : getArtistasBase()) {
            List<Contrato> c = servicioC.posiblesContratoArtista(a, resultado);
            for(Contrato contrato : c) {
                System.out.println("Asignando contrato: Artista " + contrato.getArtista().getNombre() + ", Cancion " + contrato.getCancion().getTitulo() + ", Rol " + contrato.getRol().getNombre());
                restarRolDeCancion(resultado, contrato.getCancion(), contrato.getRol());
            }
        }
        return resultado;
    }

    






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

    
}
