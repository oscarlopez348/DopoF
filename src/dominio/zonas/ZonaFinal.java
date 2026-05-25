package dominio.zonas;

/**
 * Zona de llegada que el jugador debe alcanzar para completar el nivel.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.TipoCelda;

public class ZonaFinal extends Zona {

    /**
     * Construye la zona final con posición y dimensiones dadas.
     *
     * @param fila    fila superior izquierda de la zona
     * @param columna columna superior izquierda de la zona
     * @param ancho   número de columnas que ocupa la zona
     * @param alto    número de filas que ocupa la zona
     */
    public ZonaFinal(int fila, int columna, int ancho, int alto) {
        super(fila, columna, ancho, alto);
    }

    /**
     * Retorna el tipo de celda correspondiente a la zona final.
     *
     * @return {@link TipoCelda#ZONA_FINAL}
     */
    @Override
    public TipoCelda getTipo() { return TipoCelda.ZONA_FINAL; }
}
