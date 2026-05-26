package dominio.zonas;

/**
 * Zona de inicio segura desde donde el jugador parte al comenzar o tras morir.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.TipoCelda;

public class ZonaInicial extends Zona {

    /**
     * Construye la zona inicial con posición y dimensiones dadas.
     *
     * @param fila    fila superior izquierda de la zona
     * @param columna columna superior izquierda de la zona
     * @param ancho   número de columnas que ocupa la zona
     * @param alto    número de filas que ocupa la zona
     */
    public ZonaInicial(int fila, int columna, int ancho, int alto) {
        super(fila, columna, ancho, alto);
    }

    /**
     * Retorna el tipo de celda correspondiente a la zona inicial.
     *
     * @return {@link TipoCelda#ZONA_INICIAL}
     */
    @Override
    public TipoCelda getTipo() { return TipoCelda.ZONA_INICIAL; }
}
