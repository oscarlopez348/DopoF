package dominio.enemigos;

import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.personajes.CuadradoRojo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link DeslizadorVertical}.
 */
class DeslizadorVerticalTest {

    private Tablero tablero;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
    }

    @Test
    void constructorInicializaPosicionCorrecta() {
        DeslizadorVertical d = new DeslizadorVertical(3, 5);
        assertEquals(3, d.getFila());
        assertEquals(5, d.getColumna());
    }

    @Test
    void moverAvanzaHaciaAbajoPorDefecto() {
        DeslizadorVertical d = new DeslizadorVertical(3, 5);
        d.mover(tablero);
        assertEquals(4, d.getFila());
        assertEquals(5, d.getColumna());
    }

    @Test
    void moverRebotaEnParedInferior() {
        // Pared en fila 4
        tablero.getCelda(4, 5).setTipo(TipoCelda.PARED);
        DeslizadorVertical d = new DeslizadorVertical(3, 5);
        // Intenta ir a fila 4 → rebota, se queda en 3
        d.mover(tablero);
        assertEquals(3, d.getFila());
        // Ahora se mueve hacia arriba
        d.mover(tablero);
        assertEquals(2, d.getFila());
    }

    @Test
    void moverRebotaEnBordeSuperior() {
        // Pared en fila 0 (borde)
        tablero.getCelda(0, 5).setTipo(TipoCelda.PARED);
        DeslizadorVertical d = new DeslizadorVertical(1, 5);
        // Primero mueve hacia abajo (sentido inicial = 1)
        d.mover(tablero);
        assertEquals(2, d.getFila());
    }

    @Test
    void moverRebotaEnZonaInicial() {
        tablero.getCelda(4, 5).setTipo(TipoCelda.ZONA_INICIAL);
        DeslizadorVertical d = new DeslizadorVertical(3, 5);
        d.mover(tablero); // rebota porque zona inicial no es transitable por enemigo
        assertEquals(3, d.getFila());
        d.mover(tablero); // ahora va hacia arriba
        assertEquals(2, d.getFila());
    }

    @Test
    void columnaNoVariaEnNingunMomento() {
        DeslizadorVertical d = new DeslizadorVertical(5, 3);
        for (int i = 0; i < 5; i++) {
            d.mover(tablero);
            assertEquals(3, d.getColumna());
        }
    }

    @Test
    void colisionaConJugadorEnMismaCelda() {
        DeslizadorVertical d = new DeslizadorVertical(4, 4);
        CuadradoRojo jugador = new CuadradoRojo(4, 4);
        assertTrue(d.colisionaCon(jugador));
    }

    @Test
    void noColisionaConJugadorEnCeldaDistinta() {
        DeslizadorVertical d = new DeslizadorVertical(4, 4);
        CuadradoRojo jugador = new CuadradoRojo(4, 5);
        assertFalse(d.colisionaCon(jugador));
    }

    @Test
    void rebotaVariasVecesCorrectamente() {
        // Tablero 5 filas, columna fija
        Tablero pequeño = new Tablero(5, 5);
        pequeño.getCelda(4, 2).setTipo(TipoCelda.PARED);
        pequeño.getCelda(0, 2).setTipo(TipoCelda.PARED);
        DeslizadorVertical d = new DeslizadorVertical(1, 2);
        // Avanza: 1→2→3 (rebota en 4), luego 3→2→1 (rebota en 0), etc.
        d.mover(pequeño); assertEquals(2, d.getFila());
        d.mover(pequeño); assertEquals(3, d.getFila());
        d.mover(pequeño); assertEquals(3, d.getFila()); // rebota en pared fila 4
        d.mover(pequeño); assertEquals(2, d.getFila());
    }
}
