package dominio.juego;

import dominio.enemigos.PuntoAzulBasico;
import dominio.objetivos.MonedaAmarilla;
import dominio.personajes.CuadradoRojo;
import dominio.zonas.ZonaFinal;
import dominio.zonas.ZonaInicial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Nivel}.
 */
class NivelTest {

    private Tablero tablero;
    private Nivel nivel;
    private CuadradoRojo jugador;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);
        nivel = new Nivel("Nivel 1", tablero, 60);
        jugador = new CuadradoRojo(5, 5);
    }
    @Test
    void tiempoRestanteInicialEsElLimite() {
        assertEquals(60, nivel.getTiempoRestante());
    }

    @Test
    void avanzarTiempoReduceTiempoRestante() {
        nivel.avanzarTiempo();
        assertEquals(59, nivel.getTiempoRestante());
    }

    @Test
    void tiempoNoEsNegativo() {
        for (int i = 0; i < 70; i++) nivel.avanzarTiempo();
        assertEquals(0, nivel.getTiempoRestante());
    }

    @Test
    void tiempoAgotadoCuandoLlegaAlLimite() {
        for (int i = 0; i < 60; i++) nivel.avanzarTiempo();
        assertTrue(nivel.tiempoAgotado());
    }

    @Test
    void tiempoNoAgotadoAntesDeLimite() {
        nivel.avanzarTiempo();
        assertFalse(nivel.tiempoAgotado());
    }
    @Test
    void sinMonedasTodasRecogidasEsTrue() {
        assertTrue(nivel.todasMonedasRecogidas());
    }

    @Test
    void monedasNoRecogidasFallaTodasRecogidas() {
        nivel.agregarMoneda(new MonedaAmarilla(3, 3));
        assertFalse(nivel.todasMonedasRecogidas());
    }

    @Test
    void recogerMonedaEnPosicionCorrecta() {
        nivel.agregarMoneda(new MonedaAmarilla(3, 3));
        nivel.recogerMonedasEn(3, 3);
        assertTrue(nivel.todasMonedasRecogidas());
    }

    @Test
    void recogerMonedaEnPosicionIncorrectaNoLaRecoge() {
        nivel.agregarMoneda(new MonedaAmarilla(3, 3));
        nivel.recogerMonedasEn(4, 4);
        assertFalse(nivel.todasMonedasRecogidas());
    }

    @Test
    void getMonedasRecogidasContaCorrectamente() {
        nivel.agregarMoneda(new MonedaAmarilla(2, 2));
        nivel.agregarMoneda(new MonedaAmarilla(3, 3));
        nivel.recogerMonedasEn(2, 2);
        assertEquals(1, nivel.getMonedasRecogidas());
    }
    @Test
    void colisionConEnemigoMataAlJugador() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 5, true);
        nivel.agregarEnemigo(enemigo);
        nivel.verificarColisiones(jugador);
        assertEquals(1, jugador.getMuertes());
    }

    @Test
    void sinEnemigoNoMuere() {
        nivel.verificarColisiones(jugador);
        assertEquals(0, jugador.getMuertes());
    }

    @Test
    void enemigoEnOtraCeldaNoCausaMuerte() {
        PuntoAzulBasico enemigo = new PuntoAzulBasico(1, 1, true);
        nivel.agregarEnemigo(enemigo);
        nivel.verificarColisiones(jugador);
        assertEquals(0, jugador.getMuertes());
    }

    @Test
    void jugadorEnZonaInicialEsInmune() {
        tablero.getCelda(5, 5).setTipo(TipoCelda.ZONA_INICIAL);
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 5, true);
        nivel.agregarEnemigo(enemigo);
        nivel.verificarColisiones(jugador);
        assertEquals(0, jugador.getMuertes());
    }

    @Test
    void jugadorEnZonaFinalEsInmune() {
        tablero.getCelda(5, 5).setTipo(TipoCelda.ZONA_FINAL);
        PuntoAzulBasico enemigo = new PuntoAzulBasico(5, 5, true);
        nivel.agregarEnemigo(enemigo);
        nivel.verificarColisiones(jugador);
        assertEquals(0, jugador.getMuertes());
    }
    @Test
    void nivelCompletoConTodasMonedasYEnZonaFinal() {
        ZonaFinal zf = new ZonaFinal(8, 8, 1, 1);
        tablero.getCelda(8, 8).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(zf);

        nivel.agregarMoneda(new MonedaAmarilla(3, 3));
        nivel.recogerMonedasEn(3, 3);

        CuadradoRojo jugadorFinal = new CuadradoRojo(8, 8);
        assertTrue(nivel.estaCompleto(jugadorFinal));
    }

    @Test
    void nivelNoCompletoSiHayMonedasPendientes() {
        ZonaFinal zf = new ZonaFinal(8, 8, 1, 1);
        tablero.getCelda(8, 8).setTipo(TipoCelda.ZONA_FINAL);
        nivel.agregarZona(zf);
        nivel.agregarMoneda(new MonedaAmarilla(3, 3));

        CuadradoRojo jugadorFinal = new CuadradoRojo(8, 8);
        assertFalse(nivel.estaCompleto(jugadorFinal));
    }

    @Test
    void nivelNoCompletoSiJugadorFueraDeZonaFinal() {
        ZonaFinal zf = new ZonaFinal(8, 8, 1, 1);
        nivel.agregarZona(zf);

        assertFalse(nivel.estaCompleto(jugador));
    }
    @Test
    void agregarZonaInicialActualizaSpawn() {
        ZonaInicial zi = new ZonaInicial(2, 4, 2, 2);
        nivel.agregarZona(zi);
        assertEquals(3, nivel.getFilaSpawn());
        assertEquals(5, nivel.getColumnaSpawn());
    }
    @Test
    void getNombreCorrectos() {
        assertEquals("Nivel 1", nivel.getNombre());
    }

    @Test
    void getTableroCorrectos() {
        assertSame(tablero, nivel.getTablero());
    }

    @Test
    void getTiempoLimiteCorrectos() {
        assertEquals(60, nivel.getTiempoLimite());
    }
}
