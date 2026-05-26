package dominio.modos;

/**
 * Modo de juego en el que un jugador humano controla el cuadrado.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Nivel;
import dominio.personajes.Cuadrado;

public class ModoPlayer implements ModoJuego {
    private Cuadrado jugador;
    private Nivel nivel;

    /**
     * Construye el modo player asociando un jugador y un nivel.
     *
     * @param jugador cuadrado controlado por el jugador humano
     * @param nivel   nivel activo sobre el que se juega
     */
    public ModoPlayer(Cuadrado jugador, Nivel nivel) {
        this.jugador = jugador;
        this.nivel = nivel;
    }

    /**
     * Inicia el modo player mostrando un mensaje de bienvenida en consola.
     */
    @Override
    public void iniciar() {
        System.out.println("Modo Player iniciado con " + jugador.getNombre());
    }

    /**
     * Verifica si el nivel está completo para el jugador actual.
     *
     * @return true si el nivel está completado, false en caso contrario
     */
    @Override
    public boolean verificarVictoria() {
        return nivel.estaCompleto(jugador);
    }

    /**
     * Se llama después de cada movimiento del jugador.
     * Verifica colisiones con enemigos y recoge monedas en la posición actual.
     */
    @Override
    public void actualizar() {
        nivel.verificarColisiones(jugador);
        nivel.recogerMonedasEn(jugador.getFila(), jugador.getColumna(), jugador);
    }

    /**
     * Retorna el nombre de este modo de juego.
     *
     * @return nombre del modo
     */
    @Override
    public String getNombre() { return "Player"; }

    /**
     * Retorna el cuadrado controlado por el jugador.
     *
     * @return cuadrado del jugador
     */
    public Cuadrado getJugador() { return jugador; }
}