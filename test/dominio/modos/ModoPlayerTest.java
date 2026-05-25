package dominio.modos;

import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.objetivos.MonedaAmarilla;
import dominio.personajes.CuadradoRojo;
import dominio.zonas.ZonaFinal;
import dominio.zonas.ZonaInicial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ModoPlayer}.
 */
class ModoPlayerTest {

    private Tablero tablero;
    private Nivel nivel;
    private CuadradoRojo jugador;
    private ModoPlayer modo;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
        nivel = new Nivel("Test", tablero, 30);
        jugador = new CuadradoRojo(5, 5);
        modo = new ModoPlayer(jugador, nivel);
    }

    @Test
    void getNombreEsPlayer() {
        assertEquals("Player", modo.getNombre());
    }

    @Test
    void getJugadorRetornaElMismoJugador() {
        assertSame(jugador, modo.getJugador());
    }

    @Test
    void verificarVictoriaFalsoSinCumplirCondiciones() {
        assertFalse(modo.verificarVictoria());
    }

    @Test
    void verificarVictoriaVerdaderoConNivelCompleto() {
        ZonaFinal zf = new ZonaFinal(9, 9, 1, 1);
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(zf);
        CuadradoRojo j2 = new CuadradoRojo(9, 9);
        ModoPlayer m2 = new ModoPlayer(j2, nivel);
        assertTrue(m2.verificarVictoria());
    }

    @Test
    void actualizarRecogeMonedasEnPosicionJugador() {
        nivel.agregarMoneda(new MonedaAmarilla(5, 5));
        modo.actualizar();
        assertTrue(nivel.todasMonedasRecogidas());
    }

    @Test
    void actualizarNoRecogeMonedasEnOtraPosicion() {
        nivel.agregarMoneda(new MonedaAmarilla(1, 1));
        modo.actualizar();
        assertFalse(nivel.todasMonedasRecogidas());
    }

    @Test
    void iniciarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> modo.iniciar());
    }
}
