package dominio.personajes;

/**
 * Personaje jugable de color azul, conocido como Inky.
 * Se mueve más rápido que el rojo pero ocupa más espacio en el tablero.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class CuadradoAzul extends Cuadrado {

    /**
     * Construye un CuadradoAzul en la posición de inicio indicada
     * con velocidad doble y tamaño 1.2 veces el normal.
     *
     * @param filaInicio    fila de aparición inicial
     * @param columnaInicio columna de aparición inicial
     */
    public CuadradoAzul(int filaInicio, int columnaInicio) {
        super(filaInicio, columnaInicio, 2.0, 1.2);
    }

    /**
     * Retorna el nombre del personaje.
     *
     * @return "Inky"
     */
    @Override
    public String getNombre() { return "Inky"; }

    /**
     * Retorna el color azul del personaje en formato hexadecimal.
     *
     * @return "#4A90D9"
     */
    @Override
    public String getColor()  { return "#4A90D9"; }
}
