package dominio.personajes;

/**
 * Personaje jugable de color verde, conocido como Clyde.
 * Es resistente: al primer contacto con un enemigo no muere,
 * sino que reduce su velocidad a 0.7x y queda en su posición actual. Al segundo contacto sí muere.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
public class CuadradoVerde extends Cuadrado {

    private boolean golpeado;
    private boolean invulnerable;

    /**
     * Construye un CuadradoVerde en la posición de inicio indicada
     * con velocidad y tamaño normales, y sin golpes recibidos.
     *
     * @param filaInicio    fila de aparición inicial
     * @param columnaInicio columna de aparición inicial
     */
    public CuadradoVerde(int filaInicio, int columnaInicio) {
        super(filaInicio, columnaInicio, 1.0, 1.0);
        this.golpeado = false;
        this.invulnerable = false;
    }

    /**
     * Gestiona la lógica de resistencia al daño.
     * Si es el primer golpe, el personaje sobrevive en su posición actual, baja su velocidad a 0.7x
     * y queda invulnerable brevemente para evitar golpes múltiples en el mismo tick.
     * Si ya estaba golpeado, muere, vuelve al spawn y recupera la velocidad normal.
     */
    @Override
    public void morir() {
        if (invulnerable) return;
        if (!golpeado) {
            golpeado = true;
            invulnerable = true;
            velocidad = 0.7;
            skinTemporal = null;
        } else {
            golpeado = false;
            invulnerable = true;
            velocidad = 1.0;
            muertes++;
            fila = filaInicio;
            columna = columnaInicio;
            skinTemporal = null;
        }
    }

    /**
     * Desactiva la invulnerabilidad temporal tras haber recibido un golpe.
     * Debe llamarse desde el game loop luego de cada tick de movimiento de enemigos.
     */
    public void quitarInvulnerabilidad() {
        invulnerable = false;
    }

    /**
     * Indica si el personaje ha recibido el primer golpe y está en estado debilitado.
     *
     * @return true si el personaje está golpeado, false en caso contrario
     */
    public boolean estaGolpeado() { return golpeado; }

    /**
     * Restablece el estado del personaje a su condición inicial:
     * sin golpe recibido, sin invulnerabilidad y con velocidad normal.
     */
    public void reiniciarEstado() {
        golpeado = false;
        invulnerable = false;
        velocidad = 1.0;
    }

    /**
     * Retorna el nombre del personaje.
     *
     * @return "Clyde"
     */
    @Override
    public String getNombre() { return "Clyde"; }

    /**
     * Retorna el color del personaje en formato hexadecimal.
     * Verde oscuro cuando está en estado normal, verde claro cuando está golpeado.
     *
     * @return "#4CAF50" si está sano, "#A8D8A8" si está golpeado
     */
    @Override
    public String getColor() { return golpeado ? "#A8D8A8" : "#4CAF50"; }
}