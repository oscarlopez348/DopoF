package dominio.juego;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Celda}.
 */
class CeldaTest {

    @Test
    void celdaLibreEsTransitable() {
        Celda celda = new Celda(0, 0, TipoCelda.LIBRE);
        assertTrue(celda.esTransitable());
    }

    @Test
    void celdaParedNoEsTransitable() {
        Celda celda = new Celda(1, 1, TipoCelda.PARED);
        assertFalse(celda.esTransitable());
    }

    @Test
    void celdaZonaInicialEsTransitablePeroNoporEnemigo() {
        Celda celda = new Celda(0, 0, TipoCelda.ZONA_INICIAL);
        assertTrue(celda.esTransitable());
        assertFalse(celda.esTransitablePorEnemigo());
    }

    @Test
    void celdaZonaFinalEsTransitablePeroNoPorEnemigo() {
        Celda celda = new Celda(0, 0, TipoCelda.ZONA_FINAL);
        assertTrue(celda.esTransitable());
        assertFalse(celda.esTransitablePorEnemigo());
    }

    @Test
    void celdaLibreEsTransitablePorEnemigo() {
        Celda celda = new Celda(2, 3, TipoCelda.LIBRE);
        assertTrue(celda.esTransitablePorEnemigo());
    }

    @Test
    void gettersRetornanValoresCorrectos() {
        Celda celda = new Celda(4, 7, TipoCelda.PARED);
        assertEquals(4, celda.getFila());
        assertEquals(7, celda.getColumna());
        assertEquals(TipoCelda.PARED, celda.getTipo());
    }

    @Test
    void setTipoCambiaTipo() {
        Celda celda = new Celda(0, 0, TipoCelda.LIBRE);
        celda.setTipo(TipoCelda.PARED);
        assertEquals(TipoCelda.PARED, celda.getTipo());
        assertFalse(celda.esTransitable());
    }
}
