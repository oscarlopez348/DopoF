package dominio.juego;

/**
 * Representa una celda individual dentro del tablero del juego.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class Celda {
    private int fila;
    private int columna;
    private TipoCelda tipo;

    /**
     * Construye una celda con posición y tipo dados.
     *
     * @param fila    fila de la celda en el tablero
     * @param columna columna de la celda en el tablero
     * @param tipo    tipo de celda (LIBRE, PARED, ZONA_INICIAL, etc.)
     */
    public Celda(int fila, int columna, TipoCelda tipo) {
        this.fila = fila;
        this.columna = columna;
        this.tipo = tipo;
    }

    /**
     * Indica si el jugador puede moverse a esta celda.
     *
     * @return true si la celda no es una pared, false en caso contrario
     */
    public boolean esTransitable() {
        return tipo != TipoCelda.PARED;
    }

    /**
     * Indica si un enemigo puede moverse a esta celda.
     * Los enemigos no pueden entrar en zonas seguras (inicial o final).
     *
     * @return true si la celda es de tipo LIBRE, false en caso contrario
     */
    public boolean esTransitablePorEnemigo() {
        return tipo == TipoCelda.LIBRE;
    }

    /**
     * Retorna la fila de esta celda.
     *
     * @return fila de la celda
     */
    public int getFila()             { return fila; }

    /**
     * Retorna la columna de esta celda.
     *
     * @return columna de la celda
     */
    public int getColumna()          { return columna; }

    /**
     * Retorna el tipo de esta celda.
     *
     * @return tipo de celda
     */
    public TipoCelda getTipo()       { return tipo; }

    /**
     * Establece el tipo de esta celda.
     *
     * @param t nuevo tipo de celda
     */
    public void setTipo(TipoCelda t) { this.tipo = t; }
}
