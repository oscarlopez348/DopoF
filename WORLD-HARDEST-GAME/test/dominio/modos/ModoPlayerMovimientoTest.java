package dominio.modos;

import dominio.juego.Direccion;
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
 * Tests unitarios para {@link ModoPlayer} — movimiento y restauración de estado.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
class ModoPlayerMovimientoTest {

    private Tablero tablero;
    private Nivel nivel;
    private CuadradoRojo jugador;
    private ModoPlayer modo;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
        nivel = new Nivel("Test", tablero, 60);
        jugador = new CuadradoRojo(5, 5);
        modo = new ModoPlayer(jugador, nivel);
    }

    @Test
    void accordingGLShould01MovimientoNorteActualizaFila() {
        jugador.mover(Direccion.NORTE, tablero);
        assertEquals(4, jugador.getFila());
        assertEquals(5, jugador.getColumna());
    }

    @Test
    void accordingGLShould02MovimientoSurActualizaFila() {
        jugador.mover(Direccion.SUR, tablero);
        assertEquals(6, jugador.getFila());
    }

    @Test
    void accordingGLShould03MovimientoEsteActualizaColumna() {
        jugador.mover(Direccion.ESTE, tablero);
        assertEquals(6, jugador.getColumna());
    }

    @Test
    void accordingGLShould04MovimientoOesteActualizaColumna() {
        jugador.mover(Direccion.OESTE, tablero);
        assertEquals(4, jugador.getColumna());
    }

    @Test
    void accordingGLShould05MovimientoNoresteActualizaAmbos() {
        jugador.mover(Direccion.NORESTE, tablero);
        assertEquals(4, jugador.getFila());
        assertEquals(6, jugador.getColumna());
    }

    @Test
    void accordingGLShould06MovimientoHaciaParedNoMueve() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        int filaAntes = jugador.getFila();
        jugador.mover(Direccion.NORTE, tablero);
        assertEquals(filaAntes, jugador.getFila());
    }

    @Test
    void accordingGLShould07MovimientoFueraDeTableroNoMueve() {
        CuadradoRojo esquina = new CuadradoRojo(0, 0);
        int filaAntes = esquina.getFila();
        esquina.mover(Direccion.NORTE, tablero);
        assertEquals(filaAntes, esquina.getFila());
    }

    @Test
    void accordingGLShould08MoverRetornaTrueEnCeldaLibre() {
        assertTrue(jugador.mover(Direccion.NORTE, tablero));
    }

    @Test
    void accordingGLShould09MoverRetornaFalseEnPared() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        assertFalse(jugador.mover(Direccion.NORTE, tablero));
    }

    @Test
    void accordingGLShould10MuertePorColisionRestablecePosicion() {
        jugador.morir();
        assertEquals(5, jugador.getFila());
        assertEquals(5, jugador.getColumna());
        assertEquals(1, jugador.getMuertes());
    }

    @Test
    void accordingGLShould11SetPosicionActualizaPosicionYSpawn() {
        jugador.setPosicion(3, 7);
        assertEquals(3, jugador.getFila());
        assertEquals(7, jugador.getColumna());
        jugador.morir();
        assertEquals(3, jugador.getFila());
        assertEquals(7, jugador.getColumna());
    }

    @Test
    void accordingGLShould12SetMuertesRestauraMuertes() {
        jugador.setMuertes(5);
        assertEquals(5, jugador.getMuertes());
    }

    @Test
    void accordingGLShould13MuertesInicianEnCero() {
        assertEquals(0, jugador.getMuertes());
    }

    @Test
    void accordingGLShould14VerificarVictoriaConMonedasYZonaFinal() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        nivel.agregarMoneda(new MonedaAmarilla(5, 5));
        modo.actualizar();
        jugador.setPosicion(9, 9);
        assertTrue(modo.verificarVictoria());
    }

    @Test
    void accordingGLShould15VerificarVictoriaFalsoSinMonedas() {
        tablero.getCelda(9, 9).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(new ZonaFinal(9, 9, 1, 1));
        nivel.agregarMoneda(new MonedaAmarilla(1, 1));
        jugador.setPosicion(9, 9);
        assertFalse(modo.verificarVictoria());
    }
}
