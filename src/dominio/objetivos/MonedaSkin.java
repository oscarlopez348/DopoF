package dominio.objetivos;

/**
 * Moneda especial cuyo color coincide con un skin de jugador.
 * Al recolectarla, el jugador adopta temporalmente ese tipo de personaje.
 * El efecto se pierde al recolectar otra moneda o al morir.
 * Deben recolectarse todas para completar el nivel.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class MonedaSkin extends Moneda {

    private final String skinAsociado;
    private final String color;

    /**
     * Construye una MonedaSkin en la posición indicada.
     *
     * @param fila         fila de la moneda en el tablero
     * @param columna      columna de la moneda en el tablero
     * @param skinAsociado nombre del skin que otorga ("Rojo", "Azul" o "Verde")
     */
    public MonedaSkin(int fila, int columna, String skinAsociado) {
        super(fila, columna);
        this.skinAsociado = skinAsociado;
        this.color = switch (skinAsociado) {
            case "Azul"  -> "#4A90D9";
            case "Verde" -> "#4CAF50";
            default      -> "#E24B4A";
        };
    }

    /**
     * Retorna el skin que esta moneda otorga al jugador que la recoge.
     *
     * @return nombre del skin asociado
     */
    public String getSkinAsociado() { return skinAsociado; }

    /**
     * Retorna el color de la moneda en formato hexadecimal,
     * igual al color del skin que representa.
     *
     * @return color hexadecimal
     */
    @Override
    public String getColor() { return color; }
}
