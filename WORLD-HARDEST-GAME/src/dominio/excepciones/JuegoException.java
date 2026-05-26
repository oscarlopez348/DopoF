package dominio.excepciones;

/**
 * Excepción unificada para todos los errores del dominio del juego.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class JuegoException extends Exception {
    public static final String CONFIG_NO_ENCONTRADA    = "El archivo de configuracion no fue encontrado";
    public static final String CONFIG_FORMATO_INVALIDO = "El archivo de configuracion tiene un formato invalido";
    public static final String MOVIMIENTO_INVALIDO     = "El movimiento solicitado no es valido";
    public static final String NIVEL_SIN_ZONA_FIN      = "El nivel no tiene una zona de fin definida";
    public static final String NIVEL_SIN_ZONA_INICIO   = "El nivel no tiene una zona de inicio definida";

    public JuegoException(String mensaje) {
        super(mensaje);
    }

    public JuegoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
