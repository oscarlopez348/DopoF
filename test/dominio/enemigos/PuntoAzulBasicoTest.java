package dominio.enemigos;

import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.personajes.CuadradoRojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PuntoAzulBasico}.
 */
class PuntoAzulBasicoTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
    }
    @Test
    void moverHorizontalAvanzaColumna() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 3, true);
        enemigo.mover(tablero);
        assertEquals(5, enemigo.getFila());
        assertEquals(4, enemigo.getColumna());
    }

    @Test
    void moverHorizontalRebotaEnPared() {
        tablero.getCelda(5, 4).setTipo(TipoCelda.PARED);
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 3, true);
        enemigo.mover(tablero);
        assertEquals(5, enemigo.getFila());
        assertEquals(3, enemigo.getColumna());

        enemigo.mover(tablero);
        assertEquals(2, enemigo.getColumna());
    }

    @Test
    void moverHorizontalRebotaEnZonaInicial() {
        tablero.getCelda(5, 4).setTipo(TipoCelda.ZONA_INICIAL);
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 3, true);
        enemigo.mover(tablero);
        assertEquals(3, enemigo.getColumna());
    }
    @Test
    void moverVerticalAvanzaFila() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(3, 5, false);
        enemigo.mover(tablero);
        assertEquals(4, enemigo.getFila());
        assertEquals(5, enemigo.getColumna());
    }

    @Test
    void moverVerticalRebotaEnPared() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        PuntoAzulBasico enemigo = new PuntoAzulBasico(3, 5, false);
        enemigo.mover(tablero);
        assertEquals(3, enemigo.getFila());

        enemigo.mover(tablero);
        assertEquals(2, enemigo.getFila());
    }
    @Test
    void colisionaConJugadorEnMismaCelda() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 5, true);
        CuadradoRojo jugador = new CuadradoRojo(5, 5);
        assertTrue(enemigo.colisionaCon(jugador));
    }

    @Test
    void noColisionaConJugadorEnCeldaDistinta() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 5, true);
        CuadradoRojo jugador = new CuadradoRojo(5, 6);
        assertFalse(enemigo.colisionaCon(jugador));
    }

    @Test
    void gettersRetornanPosicionCorrecta() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(2, 7, true);
        assertEquals(2, enemigo.getFila());
        assertEquals(7, enemigo.getColumna());
    }
}
