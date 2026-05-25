package dominio.objetivos;

import dominio.personajes.CuadradoRojo;
import dominio.personajes.CuadradoAzul;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MonedaTest {

    private CuadradoRojo j1;
    private CuadradoAzul j2;

    @BeforeEach
    void setUp() {
        j1 = new CuadradoRojo(1, 1);
        j2 = new CuadradoAzul(2, 2);
    }


    @Test
    void amarillaInicialmenteNoRecolectada() {
        assertFalse(new MonedaAmarilla(3, 4).estaRecolectada());
    }

    @Test
    void amarillaPosicionCorrecta() {
        MonedaAmarilla m = new MonedaAmarilla(6, 9);
        assertEquals(6, m.getFila());
        assertEquals(9, m.getColumna());
    }

    @Test
    void amarillaColorCorrecto() {
        assertEquals("#EF9F27", new MonedaAmarilla(0, 0).getColor());
    }

    @Test
    void amarillaRecolectarSinJugadorMarcaRecolectada() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar();
        assertTrue(m.estaRecolectada());
    }

    @Test
    void amarillaRecolectarConJugadorMarcaRecolectada() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar(j1);
        assertTrue(m.estaRecolectada());
    }

    @Test
    void amarillaReiniciarLaDejaDisponible() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar(j1);
        m.reiniciar();
        assertFalse(m.estaRecolectada());
    }

    @Test
    void amarillaReiniciarBorraRecolector() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar(j1);
        m.reiniciar();
        assertFalse(m.fueRecolidaPor(j1));
    }

    @Test
    void amarillaFueRecolidaPorJugadorCorrecto() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar(j1);
        assertTrue(m.fueRecolidaPor(j1));
        assertFalse(m.fueRecolidaPor(j2));
    }

    @Test
    void amarillaNoFueRecolidaPorNadieAntesDeRecolectar() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        assertFalse(m.fueRecolidaPor(j1));
    }

    @Test
    void amarillaRecolectarSinJugadorNoAsignaRecolector() {
        MonedaAmarilla m = new MonedaAmarilla(1, 1);
        m.recolectar();
        assertFalse(m.fueRecolidaPor(j1));
        assertFalse(m.fueRecolidaPor(j2));
    }


    @Test
    void skinRojoColorCorrecto() {
        assertEquals("#E24B4A", new MonedaSkin(0, 0, "Rojo").getColor());
    }

    @Test
    void skinAzulColorCorrecto() {
        assertEquals("#4A90D9", new MonedaSkin(0, 0, "Azul").getColor());
    }

    @Test
    void skinVerdeColorCorrecto() {
        assertEquals("#4CAF50", new MonedaSkin(0, 0, "Verde").getColor());
    }

    @Test
    void skinAsociadoRojoCorrecto() {
        assertEquals("Rojo", new MonedaSkin(1, 2, "Rojo").getSkinAsociado());
    }

    @Test
    void skinAsociadoAzulCorrecto() {
        assertEquals("Azul", new MonedaSkin(1, 2, "Azul").getSkinAsociado());
    }

    @Test
    void skinAsociadoVerdeCorrecto() {
        assertEquals("Verde", new MonedaSkin(1, 2, "Verde").getSkinAsociado());
    }

    @Test
    void skinPosicionCorrecta() {
        MonedaSkin ms = new MonedaSkin(3, 7, "Azul");
        assertEquals(3, ms.getFila());
        assertEquals(7, ms.getColumna());
    }

    @Test
    void skinInicialmenteNoRecolectada() {
        assertFalse(new MonedaSkin(0, 0, "Rojo").estaRecolectada());
    }

    @Test
    void skinRecolectarConJugadorMarcaRecolectada() {
        MonedaSkin ms = new MonedaSkin(1, 1, "Verde");
        ms.recolectar(j1);
        assertTrue(ms.estaRecolectada());
    }

    @Test
    void skinFueRecolidaPorJugadorCorrecto() {
        MonedaSkin ms = new MonedaSkin(1, 1, "Azul");
        ms.recolectar(j1);
        assertTrue(ms.fueRecolidaPor(j1));
        assertFalse(ms.fueRecolidaPor(j2));
    }

    @Test
    void skinReiniciarLaDejaDisponible() {
        MonedaSkin ms = new MonedaSkin(1, 1, "Rojo");
        ms.recolectar(j1);
        ms.reiniciar();
        assertFalse(ms.estaRecolectada());
        assertFalse(ms.fueRecolidaPor(j1));
    }

    @Test
    void skinDesconocidoUsaColorRojoPorDefecto() {
        MonedaSkin ms = new MonedaSkin(0, 0, "Desconocido");
        assertEquals("#E24B4A", ms.getColor());
    }
}