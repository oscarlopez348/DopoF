package dominio.enemigos;

/**
 * Enemigo que patrulla una zona específica recorriendo un camino rectangular
 * definido por su posición inicial, ancho y alto del área de patrulla.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Tablero;

public class PuntoAzulPatrullero extends Enemigo {

    private final int filaOrigen;
    private final int colOrigen;
    private final int ancho;
    private final int alto;

    private int paso;
    private final int totalPasos;

    /**
     * Construye un PuntoAzulPatrullero que recorre el perímetro del rectángulo
     * definido por (filaOrigen, colOrigen) con las dimensiones dadas.
     *
     * @param fila       fila inicial del enemigo
     * @param columna    columna inicial del enemigo
     * @param ancho      ancho del área de patrulla (en celdas)
     * @param alto       alto del área de patrulla (en celdas)
     */
    public PuntoAzulPatrullero(int fila, int columna, int ancho, int alto) {
        super(fila, columna, 1.0);
        this.filaOrigen = fila;
        this.colOrigen  = columna;
        this.ancho      = Math.max(1, ancho);
        this.alto       = Math.max(1, alto);
        this.paso       = 0;
        this.totalPasos = 2 * (this.ancho + this.alto);
    }

    /**
     * Avanza un paso en el recorrido perimetral rectangular.
     * Ignora el tablero porque su ruta está definida a priori.
     *
     * @param tablero tablero del nivel (no usado en este enemigo)
     */
    @Override
    public void mover(Tablero tablero) {
        paso = (paso + 1) % totalPasos;
        int[] pos = calcularPosicion(paso);
        fila    = pos[0];
        columna = pos[1];
    }

    private int[] calcularPosicion(int p) {
        if (p < ancho) {
            return new int[]{ filaOrigen, colOrigen + p };
        }
        p -= ancho;
        if (p < alto) {
            return new int[]{ filaOrigen + p, colOrigen + ancho };
        }
        p -= alto;
        if (p < ancho) {
            return new int[]{ filaOrigen + alto, colOrigen + ancho - p };
        }
        p -= ancho;
        return new int[]{ filaOrigen + alto - p, colOrigen };
    }

    /**
     * Retorna el ancho del área de patrulla.
     *
     * @return ancho en celdas
     */
    public int getAncho() { return ancho; }

    /**
     * Retorna el alto del área de patrulla.
     *
     * @return alto en celdas
     */
    public int getAlto() { return alto; }
}
