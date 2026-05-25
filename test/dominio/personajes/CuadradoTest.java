package dominio.personajes;

import dominio.juego.Direccion;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CuadradoTest {

    private Tablero tablero;
    private CuadradoRojo rojo;
    private CuadradoAzul azul;
    private CuadradoVerde verde;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
        rojo  = new CuadradoRojo(5, 5);
        azul  = new CuadradoAzul(5, 5);
        verde = new CuadradoVerde(5, 5);
    }


    @Test
    void rojoNombreYColor() {
        assertEquals("Blinky", rojo.getNombre());
        assertEquals("#E24B4A", rojo.getColor());
    }

    @Test
    void azulNombreYColor() {
        assertEquals("Inky", azul.getNombre());
        assertEquals("#4A90D9", azul.getColor());
    }

    @Test
    void verdeNombreCorrecto() {
        assertEquals("Clyde", verde.getNombre());
    }

    @Test
    void verdeColorSanoEsVerdeDark() {
        assertEquals("#4CAF50", verde.getColor());
    }

    @Test
    void verdeColorGolpeadoEsVerdeClaro() {
        verde.morir();
        assertEquals("#A8D8A8", verde.getColor());
    }


    @Test
    void rojoVelocidadNormal() {
        assertEquals(1.0, rojo.getVelocidad());
    }

    @Test
    void azulVelocidadDoble() {
        assertEquals(2.0, azul.getVelocidad());
    }

    @Test
    void azulTamañoMayor() {
        assertTrue(azul.getTamaño() > 1.0);
    }

    @Test
    void verdeVelocidadNormalAlInicio() {
        assertEquals(1.0, verde.getVelocidad());
    }


    @Test
    void posicionInicialCorrecta() {
        assertEquals(5, rojo.getFila());
        assertEquals(5, rojo.getColumna());
    }

    @Test
    void muertesComienzanEnCero() {
        assertEquals(0, rojo.getMuertes());
    }

    @Test
    void setPosicionActualizaPosicionYSpawn() {
        rojo.setPosicion(2, 3);
        assertEquals(2, rojo.getFila());
        assertEquals(3, rojo.getColumna());
        rojo.morir();
        assertEquals(2, rojo.getFila());
        assertEquals(3, rojo.getColumna());
    }

    @Test
    void setMuertesFijaContador() {
        rojo.setMuertes(7);
        assertEquals(7, rojo.getMuertes());
    }

    @Test
    void actualizarPuntoReaparicionCambiaDondeReaparece() {
        rojo.actualizarPuntoReaparicion(2, 3);
        rojo.mover(Direccion.SUR, tablero);
        rojo.morir();
        assertEquals(2, rojo.getFila());
        assertEquals(3, rojo.getColumna());
    }


    @Test
    void moverNorteReduceFila() {
        assertTrue(rojo.mover(Direccion.NORTE, tablero));
        assertEquals(4, rojo.getFila());
    }

    @Test
    void moverSurIncrementaFila() {
        rojo.mover(Direccion.SUR, tablero);
        assertEquals(6, rojo.getFila());
    }

    @Test
    void moverEsteIncrementaColumna() {
        rojo.mover(Direccion.ESTE, tablero);
        assertEquals(6, rojo.getColumna());
    }

    @Test
    void moverOesteReduceColumna() {
        rojo.mover(Direccion.OESTE, tablero);
        assertEquals(4, rojo.getColumna());
    }

    @Test
    void moverNoresteActualizaAmbasCoordenadas() {
        rojo.mover(Direccion.NORESTE, tablero);
        assertEquals(4, rojo.getFila());
        assertEquals(6, rojo.getColumna());
    }

    @Test
    void moverNoroesteCorrecto() {
        rojo.mover(Direccion.NOROESTE, tablero);
        assertEquals(4, rojo.getFila());
        assertEquals(4, rojo.getColumna());
    }

    @Test
    void moverSuresteCorrecto() {
        rojo.mover(Direccion.SURESTE, tablero);
        assertEquals(6, rojo.getFila());
        assertEquals(6, rojo.getColumna());
    }

    @Test
    void moverSuroesteCorrecto() {
        rojo.mover(Direccion.SUROESTE, tablero);
        assertEquals(6, rojo.getFila());
        assertEquals(4, rojo.getColumna());
    }

    @Test
    void moverContraParedRetornaFalsoYNoCambia() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        assertFalse(rojo.mover(Direccion.NORTE, tablero));
        assertEquals(5, rojo.getFila());
    }

    @Test
    void moverNingunaRetornaFalsoYNoCambia() {
        assertFalse(rojo.mover(Direccion.NINGUNA, tablero));
        assertEquals(5, rojo.getFila());
        assertEquals(5, rojo.getColumna());
    }

    @Test
    void moverFueraDelTableroNoMueve() {
        CuadradoRojo borde = new CuadradoRojo(0, 0);
        assertFalse(borde.mover(Direccion.NORTE, tablero));
        assertEquals(0, borde.getFila());
    }


    @Test
    void morirIncrementaMuertes() {
        rojo.morir();
        assertEquals(1, rojo.getMuertes());
    }

    @Test
    void morirVuelveAlSpawn() {
        rojo.mover(Direccion.SUR, tablero);
        rojo.mover(Direccion.ESTE, tablero);
        rojo.morir();
        assertEquals(5, rojo.getFila());
        assertEquals(5, rojo.getColumna());
    }

    @Test
    void morirVariasVecesCuentaCorrectamente() {
        rojo.morir();
        rojo.morir();
        rojo.morir();
        assertEquals(3, rojo.getMuertes());
    }

    @Test
    void morirLimpiaSkinTemporal() {
        rojo.aplicarSkinTemporal("Azul");
        rojo.morir();
        assertNull(rojo.getSkinTemporal());
    }


    @Test
    void skinTemporalNuloAlInicio() {
        assertNull(rojo.getSkinTemporal());
    }

    @Test
    void aplicarSkinTemporalLoGuarda() {
        rojo.aplicarSkinTemporal("Verde");
        assertEquals("Verde", rojo.getSkinTemporal());
    }

    @Test
    void limpiarSkinTemporalLoDejaNull() {
        rojo.aplicarSkinTemporal("Rojo");
        rojo.limpiarSkinTemporal();
        assertNull(rojo.getSkinTemporal());
    }


    @Test
    void verdeNoBorraAlPrimerGolpe() {
        verde.morir();
        assertEquals(5, verde.getFila());
        assertEquals(5, verde.getColumna());
        assertEquals(0, verde.getMuertes());
    }

    @Test
    void verdePrimerGolpeReduceVelocidad() {
        verde.morir();
        assertEquals(0.7, verde.getVelocidad(), 0.001);
    }

    @Test
    void verdePrimerGolpeMarcaGolpeado() {
        verde.morir();
        assertTrue(verde.estaGolpeado());
    }

    @Test
    void verdeSegundoGolpeMataYVuelveAlSpawn() {
        verde.morir();
        verde.quitarInvulnerabilidad();
        verde.morir();
        assertEquals(1, verde.getMuertes());
        assertEquals(5, verde.getFila());
        assertEquals(5, verde.getColumna());
    }

    @Test
    void verdeSegundoGolpeRestauráVelocidad() {
        verde.morir();
        verde.quitarInvulnerabilidad();
        verde.morir();
        assertEquals(1.0, verde.getVelocidad(), 0.001);
    }

    @Test
    void verdeInvulnerableIgnoraGolpeSeguido() {
        verde.morir();
        verde.morir(); // invulnerable: ignorado
        assertEquals(0, verde.getMuertes());
        assertTrue(verde.estaGolpeado());
    }

    @Test
    void verdeQuitarInvulnerabilidadPermiteSegundoGolpe() {
        verde.morir();
        verde.quitarInvulnerabilidad();
        verde.morir();
        assertEquals(1, verde.getMuertes());
    }

    @Test
    void verdeReiniciarEstadoRestauraTodo() {
        verde.morir();
        verde.reiniciarEstado();
        assertFalse(verde.estaGolpeado());
        assertEquals(1.0, verde.getVelocidad(), 0.001);
    }

    @Test
    void verdeSegundoGolpeLimpiaSkinTemporal() {
        verde.aplicarSkinTemporal("Rojo");
        verde.morir();
        verde.quitarInvulnerabilidad();
        verde.morir();
        assertNull(verde.getSkinTemporal());
    }
}