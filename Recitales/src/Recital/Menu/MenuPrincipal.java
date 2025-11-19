
package Recital.Menu;
import Recital.*;
import java.io.PrintStream;
import java.util.*;

public class MenuPrincipal {
    private ArrayList<Comando> comandos;
    public MenuPrincipal() {
        this.comandos = new ArrayList<>();
    }
    public void agregarComando(Comando comando) {
        this.comandos.add(comando);
    }

}
