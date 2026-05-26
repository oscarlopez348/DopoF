package dominio.juego;

/**
 * Representa un nivel del juego, incluyendo tablero, monedas, enemigos y zonas.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.enemigos.Enemigo;
import dominio.objetivos.Moneda;
import dominio.objetivos.MonedaSkin;
import dominio.personajes.Cuadrado;
import dominio.zonas.Zona;
import dominio.zonas.ZonaFinal;
import dominio.zonas.ZonaInicial;

import java.util.ArrayList;
import java.util.List;

public class Nivel {
    private String nombre;
    private Tablero tablero;
    private List<Moneda> monedas;
    private List<Enemigo> enemigos;
    private List<Zona> zonas;
    private int tiempoLimite;
    private int tiempoTranscurrido;

    private int filaSpawn;
    private int columnaSpawn;
    private ZonaInicial zonaInicial;

    /**
     * Construye un nivel con nombre, tablero y tiempo límite dados.
     *
     * @param nombre       nombre descriptivo del nivel
     * @param tablero      tablero de celdas del nivel
     * @param tiempoLimite tiempo máximo en segundos para completar el nivel
     */
    public Nivel(String nombre, Tablero tablero, int tiempoLimite) {
        this.nombre = nombre;
        this.tablero = tablero;
        this.tiempoLimite = tiempoLimite;
        this.tiempoTranscurrido = 0;
        this.monedas = new ArrayList<>();
        this.enemigos = new ArrayList<>();
        this.zonas = new ArrayList<>();
    }

    /**
     * Ordena a todos los enemigos del nivel que ejecuten su lógica de movimiento.
     */
    public void actualizarEnemigos() {
        for (Enemigo e : enemigos) {
            e.mover(tablero);
        }
    }

    /**
     * Verifica si algún enemigo colisiona con el jugador; si es así, lo mata,
     * salvo que el jugador esté en una zona segura.
     *
     * @param jugador cuadrado del jugador cuya posición se verifica
     */
    public void verificarColisiones(Cuadrado jugador) {
        TipoCelda celdaJugador = tablero.getCelda(jugador.getFila(), jugador.getColumna()).getTipo();
        if (celdaJugador == TipoCelda.ZONA_INICIAL || celdaJugador == TipoCelda.ZONA_FINAL) {
            return;
        }
        for (Enemigo e : enemigos) {
            if (e.colisionaCon(jugador)) {
                int filaAntes    = jugador.getFila();
                int columnaAntes = jugador.getColumna();
                int muertesAntes = jugador.getMuertes();
                jugador.morir();
                boolean seMovioAlSpawn = jugador.getFila() != filaAntes || jugador.getColumna() != columnaAntes;
                if (jugador.getMuertes() > muertesAntes && seMovioAlSpawn) {
                    reiniciarMonedasDeJugador(jugador);
                }
                break;
            }
        }
    }

    /**
     * Reinicia solo las monedas recogidas por el jugador dado,
     * preservando el progreso de otros jugadores en modos multijugador.
     *
     * @param jugador jugador cuyas monedas se deben reiniciar
     */
    public void reiniciarMonedasDeJugador(Cuadrado jugador) {
        for (Moneda m : monedas) {
            if (m.fueRecolidaPor(jugador)) {
                m.reiniciar();
            }
        }
    }

    /**
     * Marca como recolectadas todas las monedas no recogidas en la posición indicada.
     * Si la moneda es una MonedaSkin, aplica el skin temporal al jugador
     * y limpia el skin temporal previo antes de aplicar el nuevo.
     *
     * @param fila    fila donde se encuentra el jugador
     * @param columna columna donde se encuentra el jugador
     * @param jugador cuadrado del jugador que recoge la moneda
     */
    public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
        for (Moneda m : monedas) {
            if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
                jugador.limpiarSkinTemporal();
                m.recolectar(jugador);
                if (m instanceof MonedaSkin ms) {
                    jugador.aplicarSkinTemporal(ms.getSkinAsociado());
                }
            }
        }
    }

    /**
     * @deprecated Usar {@link #recogerMonedasEn(int, int, Cuadrado)} para soporte de MonedaSkin.
     */
    @Deprecated
    public void recogerMonedasEn(int fila, int columna) {
        for (Moneda m : monedas) {
            if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
                m.recolectar();
            }
        }
    }

    /**
     * Determina si el nivel está completo: todas las monedas recogidas y el jugador en la zona final.
     *
     * @param jugador cuadrado del jugador cuya posición se verifica
     * @return true si el nivel está completado, false en caso contrario
     */
    public boolean estaCompleto(Cuadrado jugador) {
        if (!todasMonedasRecogidas()) return false;
        for (Zona z : zonas) {
            if (z instanceof ZonaFinal && z.contiene(jugador.getFila(), jugador.getColumna())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica si todas las monedas del nivel han sido recolectadas.
     *
     * @return true si no queda ninguna moneda sin recolectar
     */
    public boolean todasMonedasRecogidas() {
        return monedas.stream().allMatch(Moneda::estaRecolectada);
    }

    /**
     * Retorna la cantidad de monedas que el jugador ha recogido hasta el momento.
     *
     * @return número de monedas recolectadas
     */
    public int getMonedasRecogidas() {
        return (int) monedas.stream().filter(Moneda::estaRecolectada).count();
    }

    /**
     * Indica si el tiempo disponible para el nivel se ha agotado.
     *
     * @return true si el tiempo transcurrido es mayor o igual al límite
     */
    public boolean tiempoAgotado() {
        return tiempoTranscurrido >= tiempoLimite;
    }

    /**
     * Incrementa en uno el contador de segundos transcurridos en el nivel.
     */
    public void avanzarTiempo() {
        tiempoTranscurrido++;
    }

    /**
     * Retorna el tiempo restante en segundos; nunca es negativo.
     *
     * @return segundos que quedan antes de que se agote el tiempo
     */
    public int getTiempoRestante() {
        return Math.max(0, tiempoLimite - tiempoTranscurrido);
    }

    /**
     * Agrega una moneda al nivel.
     *
     * @param m moneda a agregar
     */
    public void agregarMoneda(Moneda m)   { monedas.add(m); }

    /**
     * Agrega un enemigo al nivel.
     *
     * @param e enemigo a agregar
     */
    public void agregarEnemigo(Enemigo e) { enemigos.add(e); }

    /**
     * Agrega una zona al nivel. Si es una ZonaInicial, actualiza el punto de spawn del jugador.
     *
     * @param z zona a agregar (inicial, final u otra)
     */
    public void agregarZona(Zona z) {
        zonas.add(z);
        if (z instanceof ZonaInicial zi) {
            zonaInicial  = zi;
            filaSpawn    = z.getFila()    + z.getAlto()  / 2;
            columnaSpawn = z.getColumna() + z.getAncho() / 2;
        }
    }

    /**
     * Retorna el nombre del nivel.
     *
     * @return nombre del nivel
     */
    public String getNombre()           { return nombre; }

    /**
     * Retorna el tablero de celdas del nivel.
     *
     * @return tablero del nivel
     */
    public Tablero getTablero()         { return tablero; }

    /**
     * Retorna la lista de monedas del nivel.
     *
     * @return lista de monedas
     */
    public List<Moneda> getMonedas()    { return monedas; }

    /**
     * Retorna la lista de enemigos del nivel.
     *
     * @return lista de enemigos
     */
    public List<Enemigo> getEnemigos()  { return enemigos; }

    /**
     * Retorna la lista de zonas del nivel.
     *
     * @return lista de zonas
     */
    public List<Zona> getZonas()        { return zonas; }

    /**
     * Retorna el tiempo límite del nivel en segundos.
     *
     * @return tiempo límite en segundos
     */
    public int getTiempoLimite()        { return tiempoLimite; }

    /**
     * Retorna la columna de spawn del segundo jugador, garantizando que
     * la celda esté dentro de la zona inicial. Busca la primera celda
     * distinta a la del jugador 1 que sea ZONA_INICIAL; si no encuentra
     * ninguna, devuelve columnaSpawn (ambos jugadores solapados es mejor
     * que uno fuera del mapa).
     *
     * @return columna de spawn válida para el jugador 2
     */
    public int getColumnaSpawn2() {
        if (zonaInicial == null) return columnaSpawn;
        int fila = zonaInicial.getFila();
        int filaFin = fila + zonaInicial.getAlto();
        int col = zonaInicial.getColumna();
        int colFin = col + zonaInicial.getAncho();
        for (int f = fila; f < filaFin; f++) {
            for (int c = col; c < colFin; c++) {
                if (f == filaSpawn && c == columnaSpawn) continue; // ya ocupada por J1
                Celda celda = tablero.getCelda(f, c);
                if (celda != null && celda.getTipo() == TipoCelda.ZONA_INICIAL) {
                    return c; // fila se puede ignorar: usamos la misma que J1 o la primera disponible
                }
            }
        }
        return columnaSpawn;
    }

    /**
     * Retorna la fila de spawn del segundo jugador, compañera de {@link #getColumnaSpawn2()}.
     *
     * @return fila de spawn válida para el jugador 2
     */
    public int getFilaSpawn2() {
        if (zonaInicial == null) return filaSpawn;
        int fila = zonaInicial.getFila();
        int filaFin = fila + zonaInicial.getAlto();
        int col = zonaInicial.getColumna();
        int colFin = col + zonaInicial.getAncho();
        for (int f = fila; f < filaFin; f++) {
            for (int c = col; c < colFin; c++) {
                if (f == filaSpawn && c == columnaSpawn) continue;
                Celda celda = tablero.getCelda(f, c);
                if (celda != null && celda.getTipo() == TipoCelda.ZONA_INICIAL) {
                    return f;
                }
            }
        }
        return filaSpawn;
    }



    /**
     * Retorna la fila del punto de reaparición inicial del jugador.
     *
     * @return fila de spawn
     */
    public int getFilaSpawn()           { return filaSpawn; }

    /**
     * Retorna la columna del punto de reaparición inicial del jugador.
     *
     * @return columna de spawn
     */
    public int getColumnaSpawn()        { return columnaSpawn; }

    /**
     * Restaura el tiempo transcurrido (usado al cargar partida).
     *
     * @param t segundos ya transcurridos
     */
    public void setTiempoTranscurrido(int t) { tiempoTranscurrido = t; }
}
