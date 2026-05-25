package dominio.personajes;

/**
 * Clase base abstracta para los personajes jugables del juego DOPO.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.juego.Direccion;
import dominio.juego.Tablero;

public abstract class Cuadrado {
    protected int fila;
    protected int columna;
    protected double velocidad;
    protected double tamaño;
    protected int muertes;
    protected int filaInicio;
    protected int columnaInicio;
    protected String skinTemporal = null;

    /**
     * Construye un cuadrado en la posición de inicio con velocidad y tamaño dados.
     *
     * @param filaInicio    fila de aparición inicial del personaje
     * @param columnaInicio columna de aparición inicial del personaje
     * @param velocidad     multiplicador de velocidad (1.0 = normal)
     * @param tamaño        multiplicador de tamaño (1.0 = normal)
     */
    public Cuadrado(int filaInicio, int columnaInicio, double velocidad, double tamaño) {
        this.fila = filaInicio;
        this.columna = columnaInicio;
        this.filaInicio = filaInicio;
        this.columnaInicio = columnaInicio;
        this.velocidad = velocidad;
        this.tamaño = tamaño;
        this.muertes = 0;
    }

    /**
     * Intenta mover el personaje en la dirección indicada sobre el tablero dado.
     *
     * @param dir     dirección en la que se intenta mover
     * @param tablero tablero del nivel usado para validar si la celda destino es transitable
     * @return true si el movimiento fue válido y se realizó, false en caso contrario
     */
    public boolean mover(Direccion dir, Tablero tablero) {
        int nuevaFila = fila;
        int nuevaColumna = columna;

        switch (dir) {
            case NORTE     -> nuevaFila--;
            case SUR       -> nuevaFila++;
            case ESTE      -> nuevaColumna++;
            case OESTE     -> nuevaColumna--;
            case NORESTE   -> { nuevaFila--; nuevaColumna++; }
            case NOROESTE  -> { nuevaFila--; nuevaColumna--; }
            case SURESTE   -> { nuevaFila++; nuevaColumna++; }
            case SUROESTE  -> { nuevaFila++; nuevaColumna--; }
            default        -> { return false; }
        }

        if (tablero.esTransitable(nuevaFila, nuevaColumna)) {
            fila = nuevaFila;
            columna = nuevaColumna;
            return true;
        }
        return false;
    }

    /**
     * Registra la muerte del personaje y lo devuelve al punto de reaparición actual.
     */
    public void morir() {
        muertes++;
        fila = filaInicio;
        columna = columnaInicio;
        skinTemporal = null;
    }

    /**
     * Aplica un skin temporal al jugador al recolectar una MonedaSkin.
     * Se pierde al morir o al recolectar otra moneda.
     *
     * @param skin nombre del skin a aplicar ("Rojo", "Azul" o "Verde")
     */
    public void aplicarSkinTemporal(String skin) {
        this.skinTemporal = skin;
    }

    /**
     * Limpia el skin temporal, restaurando la apariencia original del jugador.
     */
    public void limpiarSkinTemporal() {
        this.skinTemporal = null;
    }

    /**
     * Retorna el skin temporal activo, o null si no hay ninguno.
     *
     * @return nombre del skin temporal, o null
     */
    public String getSkinTemporal() { return skinTemporal; }

    /**
     * Cambia el punto de reaparición del personaje a las coordenadas indicadas.
     *
     * @param f nueva fila de reaparición
     * @param c nueva columna de reaparición
     */
    public void actualizarPuntoReaparicion(int f, int c) {
        filaInicio = f;
        columnaInicio = c;
    }

    /**
     * Retorna el nombre del personaje.
     *
     * @return nombre del personaje
     */
    public abstract String getNombre();

    /**
     * Retorna el color del personaje en formato hexadecimal para la vista Swing.
     *
     * @return cadena de color hexadecimal
     */
    public abstract String getColor();

    /**
     * Retorna la fila actual del personaje.
     *
     * @return fila del personaje
     */
    public int getFila()            { return fila; }

    /**
     * Retorna la columna actual del personaje.
     *
     * @return columna del personaje
     */
    public int getColumna()         { return columna; }

    /**
     * Retorna el multiplicador de velocidad del personaje.
     *
     * @return velocidad del personaje
     */
    public double getVelocidad()    { return velocidad; }

    /**
     * Retorna el multiplicador de tamaño del personaje.
     *
     * @return tamaño del personaje
     */
    public double getTamaño()       { return tamaño; }

    /**
     * Retorna la cantidad de muertes acumuladas del personaje.
     *
     * @return número de muertes
     */
    public int getMuertes()         { return muertes; }

    /**
     * Restaura la posición y punto de reaparición del personaje (usado al cargar partida).
     *
     * @param f nueva fila
     * @param c nueva columna
     */
    public void setPosicion(int f, int c) {
        fila = f;
        columna = c;
        filaInicio = f;
        columnaInicio = c;
    }

    /**
     * Establece directamente el contador de muertes (usado al cargar partida).
     *
     * @param m número de muertes a restaurar
     */
    public void setMuertes(int m) {
        muertes = m;
    }
}