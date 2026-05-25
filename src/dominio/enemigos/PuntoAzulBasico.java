package dominio.enemigos;

/**
 * Enemigo básico que se desplaza horizontalmente o verticalmente rebotando en obstáculos.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Tablero;

public class PuntoAzulBasico extends Enemigo {
    private boolean horizontal;
    private int sentido;

    /**
     * Construye un PuntoAzulBasico con dirección de movimiento indicada.
     *
     * @param fila       fila inicial del enemigo
     * @param columna    columna inicial del enemigo
     * @param horizontal true si el enemigo se mueve en horizontal, false si es vertical
     */
    public PuntoAzulBasico(int fila, int columna, boolean horizontal) {
        super(fila, columna, 1.0);
        this.horizontal = horizontal;
        this.sentido = 1;
    }

    /**
     * Mueve el enemigo en su dirección actual; si choca con un obstáculo o zona segura,
     * invierte el sentido de movimiento.
     *
     * @param tablero tablero del nivel usado para validar si la celda destino es transitable
     */
    @Override
    public void mover(Tablero tablero) {
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
