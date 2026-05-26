package dominio.modos;

import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.objetivos.MonedaAmarilla;
import dominio.personajes.CuadradoRojo;
import dominio.zonas.ZonaFinal;
import dominio.zonas.ZonaInicial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ModoPlayerVsMaquina}.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
class ModoPlayerVsMaquinaTest {

    /*
     * Tablero 10x10 con esta disposición:
     *
     *   col: 0  1  2  3  4  5  6  7  8  9
     * fila 0: .  .  .  .  .  .  .  .  .  .
     * fila 1: Z  Z  .  .  .  .  .  .  F  F
     * fila 2: Z  Z  .  .  .  .  .  .  F  F
     * fila 3: .  .  .  .  .  .  .  .  .  .
     * ...
     *
     * Z = zona inicial (col 0-1, fila 1-2)
     * F = zona final   (col 8-9, fila 1-2)
     * Todo lo demás: LIBRE
     */

    private Tablero tablero;
    private Nivel nivel;
    private CuadradoRojo humano;
    private CuadradoRojo maquina;
    private ModoPlayerVsMaquina modo;

    @BeforeEach
    void setUp() {
        tablero = new Tablero(10, 10);

        // Zona inicial
        tablero.getCelda(1, 0).setTipo(TipoCelda.ZONA_INICIAL);
        tablero.getCelda(1, 1).setTipo(TipoCelda.ZONA_INICIAL);
        tablero.getCelda(2, 0).setTipo(TipoCelda.ZONA_INICIAL);
        tablero.getCelda(2, 1).setTipo(TipoCelda.ZONA_INICIAL);

        // Zona final
        tablero.getCelda(1, 8).setTipo(TipoCelda.ZONA_FINAL);
        tablero.getCelda(1, 9).setTipo(TipoCelda.ZONA_FINAL);
        tablero.getCelda(2, 8).setTipo(TipoCelda.ZONA_FINAL);
        tablero.getCelda(2, 9).setTipo(TipoCelda.ZONA_FINAL);

        nivel = new Nivel("Test PvM", tablero, 60);
        nivel.agregarZona(new ZonaInicial(1, 0, 2, 2));
        nivel.agregarZona(new ZonaFinal(1, 8, 2, 2));

        humano  = new CuadradoRojo(1, 0);
        maquina = new CuadradoRojo(2, 0);

        modo = new ModoPlayerVsMaquina(humano, maquina, nivel,
                                        ModoPlayerVsMaquina.Dificultad.NORMAL);
    }

    // --- Nombre y getters básicos ---

    @Test
    void getNombreEsPlayerVsMaquina() {
        assertEquals("PlayerVsMaquina", modo.getNombre());
    }

    @Test
    void getJugadorHumanoRetornaHumano() {
        assertSame(humano, modo.getJugadorHumano());
    }

    @Test
    void getJugadorIARetornaMaquina() {
        assertSame(maquina, modo.getJugadorIA());
    }

    @Test
    void getDificultadRetornaDificultadConfigurada() {
        assertEquals(ModoPlayerVsMaquina.Dificultad.NORMAL, modo.getDificultad());
    }

    // --- iniciar ---

    @Test
    void iniciarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> modo.iniciar());
    }

    // --- verificarVictoria sin condiciones ---

    @Test
    void verificarVictoriaFalsoSinCondiciones() {
        modo.iniciar();
        assertFalse(modo.verificarVictoria());
    }

    @Test
    void ningunoGanoAlIniciar() {
        modo.iniciar();
        assertFalse(modo.humanoGano());
        assertFalse(modo.maquinaGano());
    }

    // --- Victoria del humano ---

    @Test
    void humanoGanaAlCompletarNivel() {
        modo.iniciar();
        humano.setPosicion(1, 8);           // zona final, sin monedas pendientes
        assertTrue(modo.verificarVictoria());
        assertTrue(modo.humanoGano());
        assertFalse(modo.maquinaGano());
    }

    @Test
    void verificarVictoriaIdempotenteTrasHumanoGanar() {
        modo.iniciar();
        humano.setPosicion(1, 8);
        modo.verificarVictoria();
        assertTrue(modo.verificarVictoria());   // segunda llamada sigue devolviendo true
    }

    // --- Victoria de la máquina ---

    @Test
    void maquinaGanaAlCompletarNivel() {
        modo.iniciar();
        maquina.setPosicion(1, 8);
        assertTrue(modo.verificarVictoria());
        assertTrue(modo.maquinaGano());
        assertFalse(modo.humanoGano());
    }

    // --- Solo gana uno aunque ambos estén en la zona final ---

    @Test
    void soloGanaUnJugadorAunqueLosDosTenganPosicionFinal() {
        modo.iniciar();
        humano.setPosicion(1, 8);
        maquina.setPosicion(1, 9);
        modo.verificarVictoria();
        assertFalse(modo.humanoGano() && modo.maquinaGano());
    }

    // --- actualizar: recoger monedas ---

    @Test
    void actualizarRecogeMonedasEnPosicionHumano() {
        nivel.agregarMoneda(new MonedaAmarilla(1, 0));
        modo.iniciar();
        modo.actualizar();
        assertTrue(nivel.getMonedasRecogidas() >= 1);
    }

    @Test
    void actualizarRecogeMonedasEnPosicionIA() {
        nivel.agregarMoneda(new MonedaAmarilla(2, 0));
        modo.iniciar();
        modo.actualizar();
        assertTrue(nivel.getMonedasRecogidas() >= 1);
    }

    // --- IA avanza hacia la moneda ---

    @Test
    void iaSeAcercaAMonedaConDificultadDificil() {
        // Colocar una moneda a la derecha de la IA
        nivel.agregarMoneda(new MonedaAmarilla(2, 5));

        ModoPlayerVsMaquina modoDificil = new ModoPlayerVsMaquina(
                humano, maquina, nivel, ModoPlayerVsMaquina.Dificultad.DIFICIL);
        modoDificil.iniciar();

        int colInicial = maquina.getColumna();

        // Con DIFICIL la IA se mueve en cada tick; tras varios ticks debe avanzar
        for (int i = 0; i < 5; i++) modoDificil.actualizar();

        // La IA debería haber avanzado al menos una celda hacia la moneda (col 5)
        assertTrue(maquina.getColumna() > colInicial
                || maquina.getFila() != 2,   // o cambió de fila buscando el camino
                "La IA debería haberse movido hacia la moneda");
    }

    @Test
    void iaNoSeMueveCadaTickEnDificultadFacil() {
        nivel.agregarMoneda(new MonedaAmarilla(2, 5));

        ModoPlayerVsMaquina modoFacil = new ModoPlayerVsMaquina(
                humano, maquina, nivel, ModoPlayerVsMaquina.Dificultad.FACIL);
        modoFacil.iniciar();

        int filaPre = maquina.getFila();
        int colPre  = maquina.getColumna();

        // Solo 1 tick: con FACIL (ticksPorPaso=3) la IA aún no debería haber avanzado
        modoFacil.actualizar();

        assertEquals(filaPre, maquina.getFila(),
                "La IA no debería moverse en el primer tick con dificultad FACIL");
        assertEquals(colPre, maquina.getColumna(),
                "La IA no debería moverse en el primer tick con dificultad FACIL");
    }

    @Test
    void iaSeMueveTrasTicksSuficientesEnDificultadFacil() {
        nivel.agregarMoneda(new MonedaAmarilla(2, 5));

        ModoPlayerVsMaquina modoFacil = new ModoPlayerVsMaquina(
                humano, maquina, nivel, ModoPlayerVsMaquina.Dificultad.FACIL);
        modoFacil.iniciar();

        // FACIL = 3 ticks por paso; tras 3 actualizaciones debe haber movido
        for (int i = 0; i < 3; i++) modoFacil.actualizar();

        boolean seMuvio = maquina.getFila() != 2 || maquina.getColumna() != 0;
        assertTrue(seMuvio, "La IA debería haberse movido tras 3 ticks en dificultad FACIL");
    }

    // --- Dificultad ---

    @Test
    void dificultadFacilTiene3TicksPorPaso() {
        assertEquals(3, ModoPlayerVsMaquina.Dificultad.FACIL.getTicksPorPaso());
    }

    @Test
    void dificultadNormalTiene2TicksPorPaso() {
        assertEquals(2, ModoPlayerVsMaquina.Dificultad.NORMAL.getTicksPorPaso());
    }

    @Test
    void dificultadDificilTiene1TickPorPaso() {
        assertEquals(1, ModoPlayerVsMaquina.Dificultad.DIFICIL.getTicksPorPaso());
    }
}
