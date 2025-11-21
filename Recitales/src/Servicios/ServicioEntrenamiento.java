package Servicios;

import Artista.Artista;
import Artista.ArtistaExterno;
import Recital.Rol;


public class ServicioEntrenamiento {
    private final double FACTOR_AUMENTO_HABILIDAD = 1.5;

    public String entrenarArtista(ServicioContratacion serv, Artista a, Rol rol) throws IllegalArgumentException {
        if(serv == null){
            throw new IllegalArgumentException("El servicio de contratacion no puede ser nulo");
        }
        if(a == null){
            throw new IllegalArgumentException("El artista no puede ser nulo");
        }

        if(!a.puedeSerEntrenado()){
            return "El artista es Base, no puede ser entrenado";
        }
        //es artista externo, casteamos
        ArtistaExterno artistaExterno = (ArtistaExterno) a;

        if(serv.tieneAlgunContrato(a)){
            return "El artista ya tiene un contrato vigente, no puede ser entrenado";
        }
        if(a.puedeTocarRol(rol.getNombre())){
            return "El artista ya posee el rol: " + rol.getNombre();
        }
       artistaExterno.agregarRolEntrenado(rol, FACTOR_AUMENTO_HABILIDAD);

         return "El artista " + a.getNombre() + " ha sido entrenado exitosamente en el rol: " + rol.getNombre();
    }


}
