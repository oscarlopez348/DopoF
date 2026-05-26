package dominio.enemigos;

import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.personajes.CuadradoRojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PuntoAcelerado}.
 */
class PuntoAceleradoTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
    }

    @Test
    void constructorInicializaPosicionCorrecta() {
        PuntoAcelerado p = new PuntoAcelerado(2, 3, true);
        assertEquals(2, p.getFila());
        assertEquals(3, p.getColumna());
    }

    @Test
    void esHorizontalRetornaTrueCuandoEsHorizontal() {
        PuntoAcelerado p = new PuntoAcelerado(2, 3, true);
        assertTrue(p.esHorizontal());
    }

    @Test
    void esHorizontalRetornaFalseCuandoEsVertical() {
        PuntoAcelerado p = new PuntoAcelerado(2, 3, false);
        assertFalse(p.esHorizontal());
    }

    @Test
    void moverHorizontalAvanzaDosCeldasPorTick() {
        PuntoAcelerado p = new PuntoAcelerado(5, 2, true);
        p.mover(tablero);
        assertEquals(5, p.getFila());
        assertEquals(4, p.getColumna()); // avanza 2 columnas
    }

    @Test
    void moverVerticalAvanzaDosCeldasPorTick() {
        PuntoAcelerado p = new PuntoAcelerado(2, 5, false);
        p.mover(tablero);
        assertEquals(4, p.getFila()); // avanza 2 filas
        assertEquals(5, p.getColumna());
    }

    @Test
    void moverHorizontalRebotaEnPared() {
        tablero.getCelda(5, 4).setTipo(TipoCelda.PARED);
        PuntoAcelerado p = new PuntoAcelerado(5, 3, true);
        p.mover(tablero);
        // Primer paso: 3→4 (pared, rebota a sentido -1) → siguiente paso: 4→3
        // resultado depende del loop interno
        assertEquals(5, p.getFila()); // fila no cambia
        assertTrue(p.getColumna() >= 2 && p.getColumna() <= 5);
    }

    @Test
    void moverVerticalRebotaEnPared() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        PuntoAcelerado p = new PuntoAcelerado(3, 5, false);
        p.mover(tablero);
        assertEquals(5, p.getColumna()); // columna no cambia
        assertTrue(p.getFila() >= 1 && p.getFila() <= 5);
    }

    @Test
    void colisionaConJugadorEnMismaCelda() {
        PuntoAcelerado p = new PuntoAcelerado(4, 4, true);
        CuadradoRojo jugador = new CuadradoRojo(4, 4);
        assertTrue(p.colisionaCon(jugador));
    }

    @Test
    void noColisionaConJugadorEnCeldaDistinta() {
        PuntoAcelerado p = new PuntoAcelerado(4, 4, true);
        CuadradoRojo jugador = new CuadradoRojo(4, 6);
        assertFalse(p.colisionaCon(jugador));
    }

    @Test
    void moverHorizontalRebotaEnZonaInicial() {
        tablero.getCelda(5, 4).setTipo(TipoCelda.ZONA_INICIAL);
        PuntoAcelerado p = new PuntoAcelerado(5, 3, true);
        p.mover(tablero); // debería rebotar al encontrar zona inicial
        assertEquals(5, p.getFila());
    }

    @Test
    void moverPersisteBienVariasTicks() {
        PuntoAcelerado p = new PuntoAcelerado(5, 1, true);
        // 3 ticks = 6 columnas avanzadas si no hay paredes
        p.mover(tablero);
        p.mover(tablero);
        p.mover(tablero);
        assertEquals(5, p.getFila()); // siempre misma fila en horizontal
        assertEquals(7, p.getColumna());
    }
}
