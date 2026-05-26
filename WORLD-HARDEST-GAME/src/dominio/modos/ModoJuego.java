package dominio.modos;

/**
 * Interfaz que define el contrato para los distintos modos de juego.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public interface ModoJuego {

    /**
     * Inicializa el modo de juego y prepara los recursos necesarios.
     */
    void iniciar();

    /**
     * Verifica si se han cumplido las condiciones de victoria del modo actual.
     *
     * @return true si el jugador ha ganado, false en caso contrario
     */
    boolean verificarVictoria();

    /**
     * Ejecuta la lógica de actualización del modo en cada ciclo del juego.
     */
    void actualizar();

    /**
     * Retorna el nombre identificador del modo de juego.
     *
     * @return nombre del modo
     */
    String getNombre();
}
