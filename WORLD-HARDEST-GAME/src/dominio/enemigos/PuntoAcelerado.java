package dominio.enemigos;

/**
 * Enemigo Tipo A que se desplaza en línea recta (horizontal o vertical)
 * al doble de velocidad que los demás enemigos. Rebota en paredes. Dificultad: Alta.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Tablero;

public class PuntoAcelerado extends Enemigo {

    private final boolean horizontal;
    private int sentido;
    private boolean moverEste;

    /**
     * Construye un PuntoAcelerado en la posición indicada.
     *
     * @param fila       fila inicial del enemigo
     * @param columna    columna inicial del enemigo
     * @param horizontal true si se mueve horizontalmente, false si verticalmente
     */
    public PuntoAcelerado(int fila, int columna, boolean horizontal) {
        super(fila, columna, 2.0);
        this.horizontal = horizontal;
        this.sentido    = 1;
        this.moverEste  = true;
    }

    /**
     * Mueve el enemigo dos celdas por tick (doble velocidad).
     * Si algún paso choca con una pared, invierte el sentido.
     *
     * @param tablero tablero del nivel usado para validar posiciones
     */
    @Override
    public void mover(Tablero tablero) {
        for (int i = 0; i < 2; i++) {
            int nuevaFila    = fila    + (horizontal ? 0 : sentido);
            int nuevaColumna = columna + (horizontal ? sentido : 0);
            if (tablero.esTransitablePorEnemigo(nuevaFila, nuevaColumna)) {
                fila    = nuevaFila;
                columna = nuevaColumna;
            } else {
                sentido = -sentido;
            }
        }
    }

    /**
     * Indica si este enemigo se mueve horizontalmente.
     *
     * @return true si es horizontal, false si es vertical
     */
    public boolean esHorizontal() { return horizontal; }
}
