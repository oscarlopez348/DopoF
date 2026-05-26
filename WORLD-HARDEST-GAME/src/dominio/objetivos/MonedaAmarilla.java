package dominio.objetivos;

/**
 * Moneda de color amarillo, variante concreta de {@link Moneda}.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class MonedaAmarilla extends Moneda {

    /**
     * Construye una moneda amarilla en la posición indicada.
     *
     * @param fila    fila donde se ubica la moneda
     * @param columna columna donde se ubica la moneda
     */
    public MonedaAmarilla(int fila, int columna) {
        super(fila, columna);
    }

    /**
     * Retorna el color amarillo de esta moneda en formato hexadecimal.
     *
     * @return cadena "#EF9F27" representando el color amarillo
     */
    @Override
    public String getColor() { return "#EF9F27"; }
}
