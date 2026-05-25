package dominio.excepciones;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class JuegoExceptionTest {

    @Test
    void constructorConMensajeGuardaMensaje() {
        JuegoException ex = new JuegoException("error de prueba");
        assertEquals("error de prueba", ex.getMessage());
    }

    @Test
    void constructorConCausaGuardaCausa() {
        Throwable causa = new RuntimeException("causa raiz");
        JuegoException ex = new JuegoException("wrapper", causa);
        assertSame(causa, ex.getCause());
        assertEquals("wrapper", ex.getMessage());
    }

    @Test
    void esSubclaseDeException() {
        JuegoException ex = new JuegoException("test");
        assertInstanceOf(Exception.class, ex);
    }

    @Test
    void sePuedeLanzarYCachar() {
        assertThrows(JuegoException.class, () -> {
            throw new JuegoException(JuegoException.CONFIG_NO_ENCONTRADA);
        });
    }

    @Test
    void sePuedeLanzarConCausaYCachar() {
        IOException causa = new java.io.IOException("disco lleno");
        JuegoException ex = assertThrows(JuegoException.class, () -> {
            throw new JuegoException(JuegoException.CONFIG_NO_ENCONTRADA, causa);
        });
        assertSame(causa, ex.getCause());
    }

    @Test
    void constanteConfigNoEncontradaTieneTexto() {
        assertNotNull(JuegoException.CONFIG_NO_ENCONTRADA);
        assertFalse(JuegoException.CONFIG_NO_ENCONTRADA.isBlank());
    }

    @Test
    void constanteConfigFormatoInvalidoTieneTexto() {
        assertNotNull(JuegoException.CONFIG_FORMATO_INVALIDO);
        assertFalse(JuegoException.CONFIG_FORMATO_INVALIDO.isBlank());
    }

    @Test
    void constanteMovimientoInvalidoTieneTexto() {
        assertNotNull(JuegoException.MOVIMIENTO_INVALIDO);
        assertFalse(JuegoException.MOVIMIENTO_INVALIDO.isBlank());
    }

    @Test
    void constanteNivelSinZonaFinTieneTexto() {
        assertNotNull(JuegoException.NIVEL_SIN_ZONA_FIN);
        assertFalse(JuegoException.NIVEL_SIN_ZONA_FIN.isBlank());
    }

    @Test
    void constanteNivelSinZonaInicioTieneTexto() {
        assertNotNull(JuegoException.NIVEL_SIN_ZONA_INICIO);
        assertFalse(JuegoException.NIVEL_SIN_ZONA_INICIO.isBlank());
    }

    @Test
    void todasLasConstantesSonDistintas() {
        assertNotEquals(JuegoException.CONFIG_NO_ENCONTRADA,    JuegoException.CONFIG_FORMATO_INVALIDO);
        assertNotEquals(JuegoException.CONFIG_NO_ENCONTRADA,    JuegoException.MOVIMIENTO_INVALIDO);
        assertNotEquals(JuegoException.CONFIG_NO_ENCONTRADA,    JuegoException.NIVEL_SIN_ZONA_FIN);
        assertNotEquals(JuegoException.CONFIG_NO_ENCONTRADA,    JuegoException.NIVEL_SIN_ZONA_INICIO);
        assertNotEquals(JuegoException.CONFIG_FORMATO_INVALIDO, JuegoException.MOVIMIENTO_INVALIDO);
        assertNotEquals(JuegoException.NIVEL_SIN_ZONA_FIN,      JuegoException.NIVEL_SIN_ZONA_INICIO);
    }

    @Test
    void mensajeConConstanteConfigNoEncontrada() {
        JuegoException ex = new JuegoException(JuegoException.CONFIG_NO_ENCONTRADA + ": nivel.txt");
        assertTrue(ex.getMessage().contains(JuegoException.CONFIG_NO_ENCONTRADA));
    }

    @Test
    void mensajeConConstanteFormatoInvalido() {
        JuegoException ex = new JuegoException(JuegoException.CONFIG_FORMATO_INVALIDO + ": linea 5");
        assertTrue(ex.getMessage().contains(JuegoException.CONFIG_FORMATO_INVALIDO));
    }
}