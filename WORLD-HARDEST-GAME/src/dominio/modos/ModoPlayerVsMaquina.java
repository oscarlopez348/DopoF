package dominio.modos;

/**
 * Modo Player vs Máquina: el jugador humano compite contra una IA
 * que busca completar el nivel usando BFS (búsqueda por anchura).
 *
 * <p>La IA calcula en cada tick el camino más corto hacia la moneda
 * más cercana aún no recogida; cuando ya las tiene todas, navega
 * hacia la zona final. La dificultad controla cada cuántos ticks
 * la IA ejecuta un paso: FACIL=3, NORMAL=2, DIFICIL=1.
 *
 * <p>Jugador humano: W/A/S/D o flechas (según configuración de la vista).
 * Gana quien complete el nivel primero (todas las monedas + zona final).
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.Direccion;
import dominio.juego.TipoCelda;
import dominio.objetivos.Moneda;
import dominio.personajes.Cuadrado;
import dominio.zonas.Zona;
import dominio.zonas.ZonaFinal;

import java.util.*;

public class ModoPlayerVsMaquina implements ModoJuego {

    /**
     * Niveles de dificultad de la IA.
     * El valor indica cada cuántos ticks del game-loop la IA avanza un paso.
     */
    public enum Dificultad {
        FACIL(3),
        NORMAL(2),
        DIFICIL(1);

        private final int ticksPorPaso;

        Dificultad(int ticksPorPaso) {
            this.ticksPorPaso = ticksPorPaso;
        }

        public int getTicksPorPaso() { return ticksPorPaso; }
    }

    private final Cuadrado jugadorHumano;
    private final Cuadrado jugadorIA;
    private final Nivel nivel;
    private final Dificultad dificultad;

    private boolean humanoGano  = false;
    private boolean maquinaGano = false;
    private boolean terminado   = false;

    /** Camino precalculado que la IA está siguiendo (lista de direcciones). */
    private Deque<Direccion> caminoIA = new ArrayDeque<>();

    /** Contador de ticks para regular la velocidad de la IA según dificultad. */
    private int ticksDesdeUltimoPaso = 0;

    /**
     * Construye el modo Player vs Máquina.
     *
     * @param jugadorHumano cuadrado controlado por el jugador humano
     * @param jugadorIA     cuadrado controlado por la IA
     * @param nivel         nivel sobre el que se juega
     * @param dificultad    nivel de dificultad de la IA
     */
    public ModoPlayerVsMaquina(Cuadrado jugadorHumano, Cuadrado jugadorIA,
                               Nivel nivel, Dificultad dificultad) {
        this.jugadorHumano = jugadorHumano;
        this.jugadorIA     = jugadorIA;
        this.nivel         = nivel;
        this.dificultad    = dificultad;
    }

    /**
     * Constructor con dificultad NORMAL por defecto.
     *
     * @param jugadorHumano cuadrado controlado por el jugador humano
     * @param jugadorIA     cuadrado controlado por la IA
     * @param nivel         nivel sobre el que se juega
     */
    public ModoPlayerVsMaquina(Cuadrado jugadorHumano, Cuadrado jugadorIA, Nivel nivel) {
        this(jugadorHumano, jugadorIA, nivel, Dificultad.NORMAL);
    }

    @Override
    public void iniciar() {
        System.out.println("Modo Player vs Máquina iniciado — Dificultad: " + dificultad.name());
        System.out.println("  Humano : " + jugadorHumano.getNombre());
        System.out.println("  Máquina: " + jugadorIA.getNombre());
        recalcularCaminoIA();
    }

    /**
     * Verifica si alguno de los dos completó el nivel.
     *
     * @return true si el modo ha terminado (alguien ganó)
     */
    @Override
    public boolean verificarVictoria() {
        if (terminado) return true;

        if (!humanoGano && nivel.estaCompleto(jugadorHumano)) {
            humanoGano = true;
            terminado  = true;
            return true;
        }
        if (!maquinaGano && nivel.estaCompleto(jugadorIA)) {
            maquinaGano = true;
            terminado   = true;
            return true;
        }
        return false;
    }

    /**
     * Actualiza el estado del modo en cada tick del game-loop:
     * verifica colisiones y monedas del humano, y avanza a la IA
     * según su cadencia de dificultad.
     */
    @Override
    public void actualizar() {
        nivel.verificarColisiones(jugadorHumano);
        nivel.recogerMonedasEn(jugadorHumano.getFila(), jugadorHumano.getColumna(), jugadorHumano);

        nivel.verificarColisiones(jugadorIA);
        nivel.recogerMonedasEn(jugadorIA.getFila(), jugadorIA.getColumna(), jugadorIA);

        ticksDesdeUltimoPaso++;
        if (ticksDesdeUltimoPaso >= dificultad.getTicksPorPaso()) {
            ticksDesdeUltimoPaso = 0;
            avanzarIA();
        }
    }

    @Override
    public String getNombre() { return "PlayerVsMaquina"; }

    /**
     * Ejecuta un paso del camino precalculado.
     * Si el camino se agotó o quedó inválido, lo recalcula antes de moverse.
     */
    private void avanzarIA() {
        if (caminoIA.isEmpty()) {
            recalcularCaminoIA();
        }
        if (!caminoIA.isEmpty()) {
            Direccion siguiente = caminoIA.poll();
            jugadorIA.mover(siguiente, nivel.getTablero());

            if (caminoIA.isEmpty()) {
                recalcularCaminoIA();
            }
        }
    }

    /**
     * Recalcula el camino de la IA usando BFS desde su posición actual.
     * Objetivo: la moneda no recogida más cercana; si no hay, la zona final.
     */
    private void recalcularCaminoIA() {
        Moneda objetivo = monedaMasCercana();
        if (objetivo != null) {
            caminoIA = bfs(
                    jugadorIA.getFila(), jugadorIA.getColumna(),
                    objetivo.getFila(),  objetivo.getColumna()
            );
        } else {
            int[] destino = centroDeLaZonaFinal();
            if (destino != null) {
                caminoIA = bfs(
                        jugadorIA.getFila(), jugadorIA.getColumna(),
                        destino[0], destino[1]
                );
            }
        }
    }

    /**
     * Devuelve la moneda no recogida más cercana (distancia Manhattan)
     * a la posición actual de la IA, o null si todas están recogidas.
     */
    private Moneda monedaMasCercana() {
        Moneda mejor = null;
        int mejorDist = Integer.MAX_VALUE;
        for (Moneda m : nivel.getMonedas()) {
            if (m.estaRecolectada()) continue;
            int dist = Math.abs(m.getFila() - jugadorIA.getFila())
                    + Math.abs(m.getColumna() - jugadorIA.getColumna());
            if (dist < mejorDist) {
                mejorDist = dist;
                mejor = m;
            }
        }
        return mejor;
    }

    /**
     * Devuelve las coordenadas del centro de la ZonaFinal del nivel,
     * o null si no existe ninguna.
     */
    private int[] centroDeLaZonaFinal() {
        for (Zona z : nivel.getZonas()) {
            if (z instanceof ZonaFinal) {
                return new int[]{
                        z.getFila()    + z.getAlto()  / 2,
                        z.getColumna() + z.getAncho() / 2
                };
            }
        }
        return null;
    }

    /**
     * BFS sobre el tablero desde (filaOrigen, colOrigen) hasta (filaDest, colDest).
     * Solo usa las 4 direcciones cardinales (la IA no se mueve en diagonal).
     * Devuelve la secuencia de direcciones del camino óptimo, o una cola vacía
     * si el destino no es alcanzable.
     *
     * @param filaOrigen  fila de inicio
     * @param colOrigen   columna de inicio
     * @param filaDest    fila de destino
     * @param colDest     columna de destino
     * @return cola con las direcciones a seguir
     */
    private Deque<Direccion> bfs(int filaOrigen, int colOrigen, int filaDest, int colDest) {
        Tablero tablero = nivel.getTablero();
        int filas    = tablero.getFilas();
        int columnas = tablero.getColumnas();

        Direccion[][] predecesor = new Direccion[filas][columnas];
        boolean[][]   visitado   = new boolean[filas][columnas];

        Queue<int[]> cola = new LinkedList<>();
        cola.add(new int[]{filaOrigen, colOrigen});
        visitado[filaOrigen][colOrigen] = true;

        int[]       dFilas  = { -1,  1,  0,  0 };
        int[]       dCols   = {  0,  0,  1, -1 };
        Direccion[] dirs    = { Direccion.NORTE, Direccion.SUR,
                Direccion.ESTE,  Direccion.OESTE };

        boolean encontrado = false;

        outer:
        while (!cola.isEmpty()) {
            int[] actual = cola.poll();
            int f = actual[0], c = actual[1];

            for (int i = 0; i < 4; i++) {
                int nf = f + dFilas[i];
                int nc = c + dCols[i];

                if (nf < 0 || nf >= filas || nc < 0 || nc >= columnas) continue;
                if (visitado[nf][nc]) continue;

                TipoCelda tipo = tablero.getCelda(nf, nc).getTipo();
                if (tipo == TipoCelda.PARED) continue;

                visitado[nf][nc]   = true;
                predecesor[nf][nc] = dirs[i];
                cola.add(new int[]{nf, nc});

                if (nf == filaDest && nc == colDest) {
                    encontrado = true;
                    break outer;
                }
            }
        }

        if (!encontrado) return new ArrayDeque<>();

        Deque<Direccion> camino = new ArrayDeque<>();
        int f = filaDest, c = colDest;
        while (f != filaOrigen || c != colOrigen) {
            Direccion d = predecesor[f][c];
            camino.addFirst(d);
            switch (d) {
                case NORTE -> f++;
                case SUR   -> f--;
                case ESTE  -> c--;
                case OESTE -> c++;
                default    -> { break; }
            }
        }
        return camino;
    }

    /** @return cuadrado del jugador humano */
    public Cuadrado getJugadorHumano() { return jugadorHumano; }

    /** @return cuadrado del jugador IA */
    public Cuadrado getJugadorIA()     { return jugadorIA; }

    /** @return dificultad configurada */
    public Dificultad getDificultad()  { return dificultad; }

    /** @return true si el jugador humano ganó */
    public boolean humanoGano()        { return humanoGano; }

    /** @return true si la máquina ganó */
    public boolean maquinaGano()       { return maquinaGano; }
}