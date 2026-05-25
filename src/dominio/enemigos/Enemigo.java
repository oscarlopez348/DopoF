package dominio.enemigos;

/**
 * Clase base abstracta para todos los enemigos del juego.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Tablero;
import dominio.personajes.Cuadrado;

public abstract class Enemigo {
    protected int fila;
    protected int columna;
    protected double velocidad;

    /**
     * Construye un enemigo en la posición y velocidad indicadas.
     *
     * @param fila      fila inicial del enemigo en el tablero
     * @param columna   columna inicial del enemigo en el tablero
     * @param velocidad multiplicador de velocidad del enemigo
     */
    public Enemigo(int fila, int columna, double velocidad) {
        this.fila = fila;
        this.columna = columna;
        this.velocidad = velocidad;
    }

    /**
     * Lógica de movimiento propia de cada tipo de enemigo.
     *
     * @param tablero tablero actual del nivel usado para validar posiciones
     */
    public abstract void mover(Tablero tablero);

    /**
     * Devuelve true si este enemigo ocupa la misma celda que el cuadrado dado.
     *
     * @param c cuadrado jugador con el que se verifica la colisión
     * @return true si hay colisión, false en caso contrario
     */
    public boolean colisionaCon(Cuadrado c) {
        return fila == c.getFila() && columna == c.getColumna();
    }

    /**
     * Retorna la fila actual del enemigo.
     *
     * @return fila del enemigo
     */
    public int getFila()    { return fila; }

    /**
     * Retorna la columna actual del enemigo.
     *
     * @return columna del enemigo
     */
    public int getColumna() { return columna; }
}
