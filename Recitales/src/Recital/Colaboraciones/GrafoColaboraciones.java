package Recital.Colaboraciones;

import java.util.*;
import Recital.Artista.Artista;
import Recital.Banda.Banda;

public class GrafoColaboraciones {
    private Map<String, Set<String>> adyacencias;
    private Map<String, Set<String>> colaboracionesPorBanda;

    public GrafoColaboraciones(Set<Artista> artistas) {
        this.adyacencias = new HashMap<>();
        this.colaboracionesPorBanda = new HashMap<>();
        construirGrafo(artistas);
    }

    private void construirGrafo(Set<Artista> artistas) {
        Map<String, List<String>> bandasArtistas = new HashMap<>();

        for (Artista artista : artistas) {
            String nombreArtista = artista.getNombre();
            adyacencias.putIfAbsent(nombreArtista, new HashSet<>());
            
            for (Banda banda : artista.getBandasHistoricas()) {
                String nombreBanda = banda.getNombre();
                bandasArtistas.putIfAbsent(nombreBanda, new ArrayList<>());
                bandasArtistas.get(nombreBanda).add(nombreArtista);
            }
        }

        for (Map.Entry<String, List<String>> entry : bandasArtistas.entrySet()) {
            String banda = entry.getKey();
            List<String> miembros = entry.getValue();

            for (int i = 0; i < miembros.size(); i++) {
                for (int j = i + 1; j < miembros.size(); j++) {
                    String a1 = miembros.get(i);
                    String a2 = miembros.get(j);
                    
                    adyacencias.get(a1).add(a2);
                    adyacencias.get(a2).add(a1);
                    
                    String clave = a1.compareTo(a2) < 0 ? a1 + "|" + a2 : a2 + "|" + a1;
                    colaboracionesPorBanda.putIfAbsent(clave, new HashSet<>());
                    colaboracionesPorBanda.get(clave).add(banda);
                }
            }
        }
    }

    public void mostrarGrafo() {
        System.out.println("\n========== GRAFO DE COLABORACIONES ==========\n");

        if (adyacencias.isEmpty()) {
            System.out.println("No hay artistas registrados.");
            return;
        }

        List<String> artistas = new ArrayList<>(adyacencias.keySet());
        Collections.sort(artistas);

        for (String artista : artistas) {
            Set<String> colaboradores = adyacencias.get(artista);

            if (colaboradores.isEmpty()) {
                System.out.println(artista + " (sin colaboraciones)");
            } else {
                List<String> colaboradoresSorted = new ArrayList<>(colaboradores);
                Collections.sort(colaboradoresSorted);

                System.out.println(artista + ":");
                for (String colaborador : colaboradoresSorted) {
                    String clave = artista.compareTo(colaborador) < 0 
                        ? artista + "|" + colaborador 
                        : colaborador + "|" + artista;
                    
                    Set<String> bandas = colaboracionesPorBanda.getOrDefault(clave, new HashSet<>());
                    String bandaStr = String.join(", ", bandas);
                    
                    System.out.println("  ↔ " + colaborador + " (banda: " + bandaStr + ")");
                }
            }
            System.out.println();
        }
    }
}
