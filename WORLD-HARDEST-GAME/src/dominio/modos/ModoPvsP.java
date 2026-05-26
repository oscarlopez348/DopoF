package dominio.modos;

/**
 * Modo Player vs Player: dos jugadores compiten en el mismo tablero.
 * Jugador 1 usa W/A/S/D; Jugador 2 usa las flechas del teclado.
 * Gana quien complete el nivel primero.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import dominio.juego.Nivel;
import dominio.personajes.Cuadrado;

public class ModoPvsP implements ModoJuego {

    private Cuadrado jugador1;
    private Cuadrado jugador2;
    private Nivel nivel;

    private boolean jugador1Gano = false;
    private boolean jugador2Gano = false;
    private boolean terminado    = false;

    /**
     * @param jugador1 cuadrado del primer jugador (W/A/S/D)
     * @param jugador2 cuadrado del segundo jugador (flechas)
     * @param nivel    nivel sobre el que se juega
     */
    public ModoPvsP(Cuadrado jugador1, Cuadrado jugador2, Nivel nivel) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.nivel    = nivel;
    }

    @Override
    public void iniciar() {
        System.out.println("Modo PvsP iniciado: " + jugador1.getNombre() + " vs " + jugador2.getNombre());
    }

    /**
     * @return true si el nivel ha terminado (algún jugador ganó)
     */
    @Override
    public boolean verificarVictoria() {
        if (terminado) return true;
        if (!jugador1Gano && nivel.estaCompleto(jugador1)) {
            jugador1Gano = true;
            terminado    = true;
            return true;
        }
        if (!jugador2Gano && nivel.estaCompleto(jugador2)) {
            jugador2Gano = true;
            terminado    = true;
            return true;
        }
        return false;
    }

    @Override
    public void actualizar() {
        nivel.verificarColisiones(jugador1);
        nivel.recogerMonedasEn(jugador1.getFila(), jugador1.getColumna(), jugador1);
        nivel.verificarColisiones(jugador2);
        nivel.recogerMonedasEn(jugador2.getFila(), jugador2.getColumna(), jugador2);
    }

    @Override
    public String getNombre() { return "PvsP"; }

    /**
     * @return jugador 1
     */
    public Cuadrado getJugador1() { return jugador1; }

    /**
     * @return jugador 2
     */
    public Cuadrado getJugador2() { return jugador2; }

    /**
     * @return true si jugador 1 ganó
     */
    public boolean jugador1Gano() { return jugador1Gano; }

    /**
     * @return true si jugador 2 ganó
     */
    public boolean jugador2Gano() { return jugador2Gano; }
}