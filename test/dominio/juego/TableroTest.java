package dominio.juego;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Tablero}.
 */
class TableroTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(5, 8);
    }

    @Test
    void dimensionesCorrectas() {
        assertEquals(5, tablero.getFilas());
        assertEquals(8, tablero.getColumnas());
    }

    @Test
    void todasLasCeldasInicializadasComoLibre() {
        for (int f = 0; f < 5; f++)
            for (int c = 0; c < 8; c++)
                assertEquals(TipoCelda.LIBRE, tablero.getCelda(f, c).getTipo());
    }

    @Test
    void getCeldaFueraDeRangoRetornaNull() {
        assertNull(tablero.getCelda(-1, 0));
        assertNull(tablero.getCelda(5, 0));
        assertNull(tablero.getCelda(0, -1));
        assertNull(tablero.getCelda(0, 8));
    }

    @Test
    void celdaLibreEsTransitable() {
        assertTrue(tablero.esTransitable(2, 3));
    }

    @Test
    void celdaParedNoEsTransitable() {
        tablero.getCelda(2, 3).setTipo(TipoCelda.PARED);
        assertFalse(tablero.esTransitable(2, 3));
    }

    @Test
    void posicionFueraDeRangoNoEsTransitable() {
        assertFalse(tablero.esTransitable(10, 10));
    }

    @Test
    void celdaLibreEsTransitablePorEnemigo() {
        assertTrue(tablero.esTransitablePorEnemigo(1, 1));
    }

    @Test
    void zonaInicialNoEsTransitablePorEnemigo() {
        tablero.getCelda(1, 1).setTipo(TipoCelda.ZONA_INICIAL);
        assertFalse(tablero.esTransitablePorEnemigo(1, 1));
    }

    @Test
    void estaEnBordeFila0() {
        assertTrue(tablero.estaEnBorde(0, 3));
    }

    @Test
    void estaEnBordeFilaMax() {
        assertTrue(tablero.estaEnBorde(4, 3));
    }

    @Test
    void estaEnBordeColumna0() {
        assertTrue(tablero.estaEnBorde(2, 0));
    }

    @Test
    void estaEnBordeColumnaMax() {
        assertTrue(tablero.estaEnBorde(2, 7));
    }

    @Test
    void celdaInteriorNoEsEnBorde() {
        assertFalse(tablero.estaEnBorde(2, 4));
    }
}
