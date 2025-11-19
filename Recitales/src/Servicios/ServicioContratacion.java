package Servicios;

import java.util.ArrayList;
import java.util.List;

import Recital.Cancion;
import Recital.Contrato;
import Recital.Recital;

public class ServicioContratacion {
    private List<Contrato> contratos;
    
    public ServicioContratacion() {
        this.contratos = new ArrayList<Contrato>();
    }

    public List<Contrato> contratarParaCancion(Cancion cancion) {
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
    }

    public List<Contrato> contratarParaTodo(Recital recital){
        List<Contrato> contratos = new ArrayList<Contrato>();
        
        
        //Falta Desarrollar
        
        return contratos;
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
}
