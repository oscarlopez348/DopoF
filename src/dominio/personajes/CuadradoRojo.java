package dominio.personajes;

/**
 * Personaje jugable de color rojo, conocido como Blinky.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class CuadradoRojo extends Cuadrado {

    /**
     * Construye un CuadradoRojo en la posición de inicio indicada con velocidad y tamaño normales.
     *
     * @param filaInicio    fila de aparición inicial
     * @param columnaInicio columna de aparición inicial
     */
    public CuadradoRojo(int filaInicio, int columnaInicio) {
        super(filaInicio, columnaInicio, 1.0, 1.0);
    }

    /**
     * Retorna el nombre del personaje.
     *
     * @return "Blinky"
     */
    @Override
    public String getNombre() { return "Blinky"; }

    /**
     * Retorna el color rojo del personaje en formato hexadecimal.
     *
     * @return "#E24B4A"
     */
    @Override
    public String getColor()  { return "#E24B4A"; }
}
