package dominio.juego;

/**
 * Representa el tablero de juego compuesto por una cuadrícula de celdas.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class Tablero {
    private Celda[][] celdas;
    private int filas;
    private int columnas;

    /**
     * Construye un tablero de las dimensiones indicadas, inicializando todas las celdas como LIBRE.
     *
     * @param filas    número de filas del tablero
     * @param columnas número de columnas del tablero
     */
    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Celda[filas][columnas];
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                celdas[f][c] = new Celda(f, c, TipoCelda.LIBRE);
    }

    /**
     * Retorna la celda en la posición dada, o null si está fuera del tablero.
     *
     * @param fila    fila de la celda solicitada
     * @param columna columna de la celda solicitada
     * @return celda en esa posición, o null si las coordenadas están fuera de rango
     */
    public Celda getCelda(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas)
            return null;
        return celdas[fila][columna];
    }

    /**
     * Indica si el jugador puede moverse a la celda en la posición dada.
     *
     * @param fila    fila a verificar
     * @param columna columna a verificar
     * @return true si la celda existe y es transitable, false en caso contrario
     */
    public boolean esTransitable(int fila, int columna) {
        Celda c = getCelda(fila, columna);
        return c != null && c.esTransitable();
    }

    /**
     * Indica si un enemigo puede moverse a la celda en la posición dada.
     *
     * @param fila    fila a verificar
     * @param columna columna a verificar
     * @return true si la celda existe y es transitable por enemigos, false en caso contrario
     */
    public boolean esTransitablePorEnemigo(int fila, int columna) {
        Celda c = getCelda(fila, columna);
        return c != null && c.esTransitablePorEnemigo();
    }

    /**
     * Indica si la posición dada corresponde al borde del tablero.
     *
     * @param fila    fila a verificar
     * @param columna columna a verificar
     * @return true si la celda está en el borde del tablero
     */
    public boolean estaEnBorde(int fila, int columna) {
        return fila == 0 || fila == filas - 1 || columna == 0 || columna == columnas - 1;
    }

    /**
     * Retorna el número de filas del tablero.
     *
     * @return número de filas
     */
    public int getFilas()        { return filas; }

    /**
     * Retorna el número de columnas del tablero.
     *
     * @return número de columnas
     */
    public int getColumnas()     { return columnas; }

    /**
     * Retorna la matriz completa de celdas del tablero.
     *
     * @return matriz bidimensional de celdas
     */
    public Celda[][] getCeldas() { return celdas; }
}
