package dominio.zonas;

import dominio.juego.TipoCelda;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ZonaInicial} y {@link ZonaFinal}.
 */
class ZonaTest {
    @Test
    void zonaInicialContieneEsquinaSupIzq() {
        ZonaInicial z = new ZonaInicial(2, 3, 4, 3);
        assertTrue(z.contiene(2, 3));
    }

    @Test
    void zonaInicialContieneEsquinaInfDer() {
        ZonaInicial z = new ZonaInicial(2, 3, 4, 3);
        assertTrue(z.contiene(4, 6));
    }

    @Test
    void zonaInicialNoContieneExterior() {
        ZonaInicial z = new ZonaInicial(2, 3, 4, 3);
        assertFalse(z.contiene(1, 3));
        assertFalse(z.contiene(5, 3));
        assertFalse(z.contiene(2, 2));
        assertFalse(z.contiene(2, 7));
    }

    @Test
    void zonaInicialRetornaTipoCorrecto() {
        ZonaInicial z = new ZonaInicial(0, 0, 2, 2);
        assertEquals(TipoCelda.ZONA_INICIAL, z.getTipo());
    }

    @Test
    void zonaInicialGettersCorrectos() {
        ZonaInicial z = new ZonaInicial(1, 2, 3, 4);
        assertEquals(1, z.getFila());
        assertEquals(2, z.getColumna());
        assertEquals(3, z.getAncho());
        assertEquals(4, z.getAlto());
    }
    @Test
    void zonaFinalContieneInterior() {
        ZonaFinal z = new ZonaFinal(5, 5, 3, 3);
        assertTrue(z.contiene(6, 6));
    }

    @Test
    void zonaFinalNoContieneExterior() {
        ZonaFinal z = new ZonaFinal(5, 5, 3, 3);
        assertFalse(z.contiene(4, 5));
        assertFalse(z.contiene(8, 5));
    }

    @Test
    void zonaFinalRetornaTipoCorrecto() {
        ZonaFinal z = new ZonaFinal(0, 0, 2, 2);
        assertEquals(TipoCelda.ZONA_FINAL, z.getTipo());
    }
}
