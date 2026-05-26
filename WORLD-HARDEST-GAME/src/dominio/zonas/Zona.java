package dominio.zonas;

/**
 * Clase base abstracta que representa una zona rectangular dentro del tablero.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.TipoCelda;

public abstract class Zona {
    protected int fila;
    protected int columna;
    protected int ancho;
    protected int alto;

    /**
     * Construye una zona con posición y dimensiones dadas.
     *
     * @param fila    fila superior izquierda de la zona
     * @param columna columna superior izquierda de la zona
     * @param ancho   número de columnas que ocupa la zona
     * @param alto    número de filas que ocupa la zona
     */
    public Zona(int fila, int columna, int ancho, int alto) {
        this.fila = fila;
        this.columna = columna;
        this.ancho = ancho;
        this.alto = alto;
    }

    /**
     * Indica si la posición dada se encuentra dentro de los límites de esta zona.
     *
     * @param f fila a verificar
     * @param c columna a verificar
     * @return true si la posición está dentro de la zona, false en caso contrario
     */
    public boolean contiene(int f, int c) {
        return f >= fila && f < fila + alto &&
               c >= columna && c < columna + ancho;
    }

    /**
     * Retorna el tipo de celda asociado a esta zona.
     *
     * @return tipo de celda de la zona
     */
    public abstract TipoCelda getTipo();

    /**
     * Retorna la fila superior izquierda de la zona.
     *
     * @return fila de la zona
     */
    public int getFila()    { return fila; }

    /**
     * Retorna la columna superior izquierda de la zona.
     *
     * @return columna de la zona
     */
    public int getColumna() { return columna; }

    /**
     * Retorna el ancho de la zona en número de columnas.
     *
     * @return ancho de la zona
     */
    public int getAncho()   { return ancho; }

    /**
     * Retorna el alto de la zona en número de filas.
     *
     * @return alto de la zona
     */
    public int getAlto()    { return alto; }
}
