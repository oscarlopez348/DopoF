package dominio.modos;

import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.objetivos.MonedaAmarilla;
import dominio.personajes.CuadradoRojo;
import dominio.zonas.ZonaFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ModoPvsP}.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
class ModoPvsPTest {

    private Tablero tablero;
    private Nivel nivel;
    private CuadradoRojo jugador1;
    private CuadradoRojo jugador2;
    private ModoPvsP modo;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
        nivel = new Nivel("Test", tablero, 60);
        jugador1 = new CuadradoRojo(1, 1);
        jugador2 = new CuadradoRojo(1, 2);
        modo = new ModoPvsP(jugador1, jugador2, nivel);
    }

    @Test
    void accordingGLShould01GetNombreEsPvsP() {
        assertEquals("PvsP", modo.getNombre());
    }

    @Test
    void accordingGLShould02GetJugador1RetornaJugadorCorrecto() {
        assertSame(jugador1, modo.getJugador1());
    }

    @Test
    void accordingGLShould03GetJugador2RetornaJugadorCorrecto() {
        assertSame(jugador2, modo.getJugador2());
    }

    @Test
    void accordingGLShould04VerificarVictoriaFalsoSinCondiciones() {
        assertFalse(modo.verificarVictoria());
    }

    @Test
    void accordingGLShould05Jugador1GanaAlCompletarNivel() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        jugador1.setPosicion(9, 9);
        assertTrue(modo.verificarVictoria());
        assertTrue(modo.jugador1Gano());
        assertFalse(modo.jugador2Gano());
    }

    @Test
    void accordingGLShould06Jugador2GanaAlCompletarNivel() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        jugador2.setPosicion(9, 9);
        assertTrue(modo.verificarVictoria());
        assertTrue(modo.jugador2Gano());
        assertFalse(modo.jugador1Gano());
    }

    @Test
    void accordingGLShould07VerificarVictoriaRetornaTrueIdempotente() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        jugador1.setPosicion(9, 9);
        modo.verificarVictoria();
        assertTrue(modo.verificarVictoria());
    }

    @Test
    void accordingGLShould08SoloGanaUnJugadorAunqueLosDosTenganPosicionFinal() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        jugador1.setPosicion(9, 9);
        jugador2.setPosicion(9, 9);
        modo.verificarVictoria();
        assertFalse(modo.jugador1Gano() && modo.jugador2Gano());
    }

    @Test
    void accordingGLShould09ActualizarRecogeMonedasJugador1() {
        nivel.agregarMoneda(new MonedaAmarilla(1, 1));
        modo.actualizar();
        assertTrue(nivel.todasMonedasRecogidas());
    }

    @Test
    void accordingGLShould10ActualizarRecogeMonedasJugador2() {
        nivel.agregarMoneda(new MonedaAmarilla(1, 2));
        modo.actualizar();
        assertTrue(nivel.todasMonedasRecogidas());
    }

    @Test
    void accordingGLShould11IniciarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> modo.iniciar());
    }
}
