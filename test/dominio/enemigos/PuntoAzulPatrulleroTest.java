package dominio.enemigos;

import dominio.juego.Tablero;
import dominio.personajes.CuadradoRojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PuntoAzulPatrullero}.
 */
class PuntoAzulPatrulleroTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(15, 15);
    }

    @Test
    void constructorInicializaPosicionCorrecta() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(2, 3, 4, 3);
        assertEquals(2, p.getFila());
        assertEquals(3, p.getColumna());
    }

    @Test
    void getAnchoRetornaValorCorrecto() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(2, 3, 4, 3);
        assertEquals(4, p.getAncho());
    }

    @Test
    void getAltoRetornaValorCorrecto() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(2, 3, 4, 3);
        assertEquals(3, p.getAlto());
    }

    @Test
    void moverAvanzaUnaPosPorTick() {
        // En el primer paso (paso=1), con origen (2,3) y ancho=4:
        // p<ancho → fila=2, col=3+1=4
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(2, 3, 4, 3);
        p.mover(tablero);
        assertEquals(2, p.getFila());
        assertEquals(4, p.getColumna());
    }

    @Test
    void moverRecorreLadoSuperiorCompleto() {
        // Con ancho=3, alto=2: lado superior = pasos 0,1,2,3
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(1, 1, 3, 2);
        p.mover(tablero); assertEquals(1, p.getFila()); assertEquals(2, p.getColumna());
        p.mover(tablero); assertEquals(1, p.getFila()); assertEquals(3, p.getColumna());
        p.mover(tablero); assertEquals(1, p.getFila()); assertEquals(4, p.getColumna());
    }

    @Test
    void moverRecorreLadoDerechoCompleto() {
        // Después del lado superior pasa al lado derecho
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(1, 1, 3, 2);
        // Avanza por el lado superior (3 pasos)
        p.mover(tablero); p.mover(tablero); p.mover(tablero);
        // Ahora lado derecho
        p.mover(tablero); assertEquals(2, p.getFila()); assertEquals(4, p.getColumna());
        p.mover(tablero); assertEquals(3, p.getFila()); assertEquals(4, p.getColumna());
    }

    @Test
    void moverRecorreLadoInferiorCompleto() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(1, 1, 3, 2);
        // Superior (3) + Derecho (2) = 5 pasos
        for (int i = 0; i < 5; i++) p.mover(tablero);
        // Lado inferior (va de col 4 → col 1)
        p.mover(tablero); assertEquals(3, p.getFila()); assertEquals(3, p.getColumna());
        p.mover(tablero); assertEquals(3, p.getFila()); assertEquals(2, p.getColumna());
        p.mover(tablero); assertEquals(3, p.getFila()); assertEquals(1, p.getColumna());
    }

    @Test
    void moverRecorreLadoIzquierdoCompleto() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(1, 1, 3, 2);
        // Superior (3) + Derecho (2) + Inferior (3) = 8 pasos
        for (int i = 0; i < 8; i++) p.mover(tablero);
        // Lado izquierdo (sube de fila 3 → 2)
        p.mover(tablero); assertEquals(2, p.getFila()); assertEquals(1, p.getColumna());
    }

    @Test
    void moverRegresaAlOrigenDespuesDeUnCicloCompleto() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(2, 2, 3, 2);
        int totalPasos = 2 * (3 + 2); // 10
        for (int i = 0; i < totalPasos; i++) p.mover(tablero);
        assertEquals(2, p.getFila());
        assertEquals(2, p.getColumna());
    }

    @Test
    void anchoDimensionMinimaEsUno() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(3, 3, 0, 0);
        assertEquals(1, p.getAncho());
        assertEquals(1, p.getAlto());
    }

    @Test
    void colisionaConJugadorEnMismaCelda() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(5, 5, 3, 3);
        CuadradoRojo jugador = new CuadradoRojo(5, 5);
        assertTrue(p.colisionaCon(jugador));
    }

    @Test
    void noColisionaConJugadorEnCeldaDistinta() {
        PuntoAzulPatrullero p = new PuntoAzulPatrullero(5, 5, 3, 3);
        CuadradoRojo jugador = new CuadradoRojo(5, 6);
        assertFalse(p.colisionaCon(jugador));
    }
}
