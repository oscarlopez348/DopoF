/**
 * Punto de entrada principal del juego DOPO Hardest Game.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import presentacion.VentanaPrincipal;
import javax.swing.*;

public class Main {

    /**
     * Método principal que lanza la ventana del juego en el hilo de eventos de Swing.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
