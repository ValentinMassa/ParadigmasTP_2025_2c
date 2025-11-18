package Recital.ServicioProlog;

import java.util.*;
import Recital.Artista.Artista;
import Recital.Artista.ArtistaBase;
import Recital.Artista.ArtistaExterno;
import Recital.Recital;
import Recital.Rol.Rol;
import Recital.Contratos.Contrato;

public class ServicioPrologIntegracion {
    private Recital recital;

    public ServicioPrologIntegracion(Recital recital) {
        this.recital = recital;
    }

    public int consultarEntrenamientosMínimos() {
        int totalRolesFaltantes = obtenerRolesFaltantes();
        int capacidadBase = obtenerCapacidadBase();
        int artistasDisponibles = obtenerArtistasDisponibles();
        
        return consultarProlog(totalRolesFaltantes, capacidadBase, artistasDisponibles);
    }

    private int consultarProlog(int rolesFaltantes, int capacidadBase, int artistasDisponibles) {
        try {
            String consulta = String.format(
                "consult('src/Recital/ServicioProlog/entrenamientos.pl'), " +
                "findall(E, entrenamientos_minimos(%d, %d, %d, E), [Resultado]), " +
                "R = Resultado",
                rolesFaltantes,
                capacidadBase,
                artistasDisponibles
            );
            
            Class<?> queryClass = Class.forName("org.jpl7.Query");
            Object query = queryClass.getConstructor(String.class).newInstance(consulta);
            java.lang.reflect.Method hasSolution = queryClass.getMethod("hasSolution");
            
            if ((Boolean) hasSolution.invoke(query)) {
                java.lang.reflect.Method oneSolution = queryClass.getMethod("oneSolution");
                @SuppressWarnings("unchecked")
                Map<String, Object> solucion = (Map<String, Object>) oneSolution.invoke(query);
                
                Object resultado = solucion.get("R");
                java.lang.reflect.Method intValue = resultado.getClass().getMethod("intValue");
                return (Integer) intValue.invoke(resultado);
            }
            return -1;
            
        } catch (Exception e) {
            return -2; // Error en Prolog
        }
    }

    private int obtenerRolesFaltantes() {
        Map<Rol, Integer> rolesRequeridos = recital.getRolesFaltantes();
        return rolesRequeridos.values().stream().mapToInt(Integer::intValue).sum();
    }

    private int obtenerCapacidadBase() {
        Set<ArtistaBase> artistasBase = recital.getArtistasBase();
        return artistasBase.stream().mapToInt(ArtistaBase::getMaxCanciones).sum();
    }

    private int obtenerArtistasDisponibles() {
        Set<ArtistaExterno> artistasExternos = recital.getArtistasExternos();
        List<Contrato> contratos = recital.getContratos();
        
        Set<ArtistaExterno> artistasContratados = new HashSet<>();
        for (Contrato c : contratos) {
            Artista artista = c.getArtista();
            if (artista instanceof ArtistaExterno) {
                artistasContratados.add((ArtistaExterno) artista);
            }
        }
        
        return (int) artistasExternos.stream()
            .filter(e -> !artistasContratados.contains(e))
            .count();
    }
}


