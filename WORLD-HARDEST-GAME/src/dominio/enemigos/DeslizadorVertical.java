package dominio.enemigos;

/**
 * Enemigo Tipo V que se desplaza exclusivamente en línea recta vertical,
 * rebotando al chocar con paredes superior e inferior. Dificultad baja.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Tablero;

public class DeslizadorVertical extends Enemigo {

    private int sentido;

    /**
     * Construye un DeslizadorVertical en la posición indicada moviéndose hacia abajo inicialmente.
     *
     * @param fila    fila inicial del enemigo
     * @param columna columna inicial del enemigo
     */
    public DeslizadorVertical(int fila, int columna) {
        super(fila, columna, 0.7);
        this.sentido = 1;
    }

    /**
     * Mueve el enemigo verticalmente; si la siguiente celda no es transitable,
     * invierte el sentido de movimiento.
     *
     * @param tablero tablero del nivel usado para validar posiciones
     */
    @Override
    public void mover(Tablero tablero) {
        int nuevaFila = fila + sentido;
        if (tablero.esTransitablePorEnemigo(nuevaFila, columna)) {
            fila = nuevaFila;
        } else {
            sentido = -sentido;
        }
    }
}
