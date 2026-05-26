package presentacion;

/**
 * Ventana principal del juego que gestiona la navegación entre menú y pantalla de juego,
 * así como el game loop basado en temporizadores Swing.
 * Soporta el modo Player (un jugador) y el modo Player vs Player (dos jugadores).
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import dominio.juego.Nivel;
import dominio.juego.Direccion;
import dominio.modos.ModoPlayer;
import dominio.modos.ModoPvsP;
import dominio.modos.ModoPlayerVsMaquina;
import dominio.persistencia.LectorConfiguracion;
import dominio.persistencia.GestorPartida;
import dominio.personajes.CuadradoRojo;
import dominio.personajes.CuadradoAzul;
import dominio.personajes.CuadradoVerde;
import dominio.personajes.Cuadrado;
import dominio.excepciones.JuegoException;
import dominio.enemigos.PuntoAcelerado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaPrincipal extends JFrame {

    private static final Color HUD_BG   = new Color(30, 30, 30);
    private static final Color HUD_FG   = Color.WHITE;
    private static final Color BAR_BG   = new Color(20, 20, 20);
    private static final Font  HUD_FONT = new Font("Arial Black", Font.BOLD, 13);

    private CardLayout cardLayout;
    private JPanel contenedor;

    private Nivel      nivel;
    private ModoPlayer modoPlayer;
    private ModoPvsP   modoPvsP;
    private ModoPlayerVsMaquina modoPvsM;

    private PanelJuego panelJuego;
    private JLabel     lblMuertes;
    private JLabel     lblMonedas;
    private JLabel     lblMuertes2;
    private Timer      timerLogica;
    private Timer      timerSegundo;
    private Timer      timerMovimiento;
    private Timer      timerMovimiento2;

    private final java.util.Set<Integer> teclasPresionadas = new java.util.HashSet<>();
    private boolean pausado = false;

    private String skinActual1 = "Rojo";
    private String skinActual2 = null;
    private String modoActual  = "Player";
    private String nivelRutaActual = "recursos/niveles/nivel1.txt";
    private final GestorPartida gestorPartida = new GestorPartida();
    private KeyEventDispatcher keyDispatcher  = null;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public VentanaPrincipal() {
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        PanelMenu menu = new PanelMenu(this::iniciarJuego);
        contenedor.add(menu, "MENU");
        add(contenedor);
        pack();
        setMinimumSize(new Dimension(640, 400));
        setLocationRelativeTo(null);
        cardLayout.show(contenedor, "MENU");
    }

    /**
     * @param skin  skin del jugador 1
     * @param skin2 skin del jugador 2 (null en modo Player)
     * @param modo  "Player" o "PvsP"
     * @param config mapa de elementos del nivel habilitados
     */
    private void iniciarJuego(String skin, String skin2, String modo, java.util.Map<String,Boolean> config) {
        if ("PvsP".equals(modo)) {
            iniciarJuegoPvsP(skin, skin2, config);
        } else if ("PvsM".equals(modo)) {
            iniciarJuegoPvsM(skin, config);
        } else {
            iniciarJuegoPlayer(skin, config);
        }
    }

    /**
     * @param skin   skin seleccionada ("Rojo", "Azul" o "Verde")
     * @param config mapa de elementos habilitados
     */
    private void iniciarJuegoPlayer(String skin, java.util.Map<String,Boolean> config) {
        skinActual1 = skin;
        skinActual2 = null;
        modoActual  = "Player";
        try {
            LectorConfiguracion lector = new LectorConfiguracion();
            nivelRutaActual = "recursos/niveles/nivel1.txt";
            nivel = lector.cargar(nivelRutaActual);
            aplicarConfig(config);

            Cuadrado jugador = crearCuadrado(skin, nivel.getFilaSpawn(), nivel.getColumnaSpawn());
            modoPlayer = new ModoPlayer(jugador, nivel);
            modoPvsP   = null;
            modoPvsM   = null;
            modoPlayer.iniciar();

            construirPantallaJuego(false);
            ajustarVentana();
            cardLayout.show(contenedor, "JUEGO");
            panelJuego.requestFocusInWindow();
            iniciarGameLoopPlayer();
        } catch (JuegoException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarGameLoopPlayer() {
        int intervalo = (int)(150 / modoPlayer.getJugador().getVelocidad());
        timerMovimiento = new Timer(intervalo, e -> {
            if (pausado) return;
            Direccion dir = getDireccionJ1();
            if (dir != Direccion.NINGUNA) {
                modoPlayer.getJugador().mover(dir, nivel.getTablero());
                modoPlayer.actualizar();
                actualizarHUDPlayer();
                verificarEstadoPlayer();
                panelJuego.repaint();
            }
        });
        timerMovimiento.start();

        timerLogica = new Timer(250, e -> {
            if (pausado) return;
            nivel.actualizarEnemigos();
            nivel.verificarColisiones(modoPlayer.getJugador());
            if (modoPlayer.getJugador() instanceof dominio.personajes.CuadradoVerde cv)
                cv.quitarInvulnerabilidad();
            actualizarHUDPlayer();
            verificarEstadoPlayer();
            panelJuego.repaint();
        });
        timerLogica.start();

        timerSegundo = new Timer(1000, e -> {
            if (pausado) return;
            nivel.avanzarTiempo();
            if (nivel.tiempoAgotado()) {
                detenerTimers();
                JOptionPane.showMessageDialog(this,
                        "Tiempo agotado!\nMuertes: " + modoPlayer.getJugador().getMuertes(),
                        "Game Over", JOptionPane.WARNING_MESSAGE);
                volverAlMenu();
            }
        });
        timerSegundo.start();
    }

    private void actualizarHUDPlayer() {
        lblMuertes.setText("DEATHS: " + modoPlayer.getJugador().getMuertes());
        lblMonedas.setText("COINS: " + nivel.getMonedasRecogidas() + "/" + nivel.getMonedas().size());
    }

    private void verificarEstadoPlayer() {
        if (modoPlayer.verificarVictoria()) {
            detenerTimers();
            JOptionPane.showMessageDialog(this,
                    "¡NIVEL COMPLETADO!\nMuertes: " + modoPlayer.getJugador().getMuertes(),
                    "Victoria!", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
        }
    }

    /**
     * @param skin  skin del jugador 1
     * @param skin2 skin del jugador 2
     * @param config mapa de elementos habilitados
     */
    private void iniciarJuegoPvsP(String skin, String skin2, java.util.Map<String,Boolean> config) {
        skinActual1 = skin;
        skinActual2 = skin2;
        modoActual  = "PvsP";
        try {
            LectorConfiguracion lector = new LectorConfiguracion();
            nivelRutaActual = "recursos/niveles/nivel1.txt";
            nivel = lector.cargar(nivelRutaActual);
            aplicarConfig(config);

            Cuadrado j1 = crearCuadrado(skin,  nivel.getFilaSpawn(),  nivel.getColumnaSpawn());
            Cuadrado j2 = crearCuadrado(skin2, nivel.getFilaSpawn2(), nivel.getColumnaSpawn2());

            modoPvsP   = new ModoPvsP(j1, j2, nivel);
            modoPlayer = null;
            modoPvsM   = null;
            modoPvsP.iniciar();

            construirPantallaJuego(true);
            ajustarVentana();
            cardLayout.show(contenedor, "JUEGO");
            panelJuego.requestFocusInWindow();
            iniciarGameLoopPvsP();
        } catch (JuegoException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarGameLoopPvsP() {
        timerMovimiento = new Timer(150, e -> {
            if (pausado) return;
            Direccion dir = getDireccionJ1();
            if (dir != Direccion.NINGUNA) {
                modoPvsP.getJugador1().mover(dir, nivel.getTablero());
                nivel.recogerMonedasEn(modoPvsP.getJugador1().getFila(), modoPvsP.getJugador1().getColumna(), modoPvsP.getJugador1());
                actualizarHUDPvsP();
                if (verificarEstadoPvsP()) return;
                panelJuego.repaint();
            }
        });
        timerMovimiento.start();

        timerMovimiento2 = new Timer(150, e -> {
            if (pausado) return;
            Direccion dir = getDireccionJ2();
            if (dir != Direccion.NINGUNA) {
                modoPvsP.getJugador2().mover(dir, nivel.getTablero());
                nivel.recogerMonedasEn(modoPvsP.getJugador2().getFila(), modoPvsP.getJugador2().getColumna(), modoPvsP.getJugador2());
                actualizarHUDPvsP();
                if (verificarEstadoPvsP()) return;
                panelJuego.repaint();
            }
        });
        timerMovimiento2.start();

        timerLogica = new Timer(250, e -> {
            if (pausado) return;
            nivel.actualizarEnemigos();
            nivel.verificarColisiones(modoPvsP.getJugador1());
            nivel.verificarColisiones(modoPvsP.getJugador2());
            if (modoPvsP.getJugador1() instanceof dominio.personajes.CuadradoVerde cv1)
                cv1.quitarInvulnerabilidad();
            if (modoPvsP.getJugador2() instanceof dominio.personajes.CuadradoVerde cv2)
                cv2.quitarInvulnerabilidad();
            actualizarHUDPvsP();
            verificarEstadoPvsP();
            panelJuego.repaint();
        });
        timerLogica.start();

        timerSegundo = new Timer(1000, e -> {
            if (pausado) return;
            nivel.avanzarTiempo();
            if (nivel.tiempoAgotado()) {
                detenerTimers();
                JOptionPane.showMessageDialog(this,
                        "¡Tiempo agotado! Nadie llegó a tiempo.",
                        "Game Over", JOptionPane.WARNING_MESSAGE);
                volverAlMenu();
            }
        });
        timerSegundo.start();
    }

    private void actualizarHUDPvsP() {
        if (lblMuertes  != null) lblMuertes.setText("J1 MUERTES: " + modoPvsP.getJugador1().getMuertes());
        if (lblMuertes2 != null) lblMuertes2.setText("J2 MUERTES: " + modoPvsP.getJugador2().getMuertes());
        if (lblMonedas  != null) lblMonedas.setText("COINS: " + nivel.getMonedasRecogidas() + "/" + nivel.getMonedas().size());
    }

    /**
     * @return true si el juego terminó
     */
    private boolean verificarEstadoPvsP() {
        if (modoPvsP.verificarVictoria()) {
            detenerTimers();
            String ganador = modoPvsP.jugador1Gano()
                    ? "¡JUGADOR 1 (" + modoPvsP.getJugador1().getNombre() + ") GANÓ!"
                    : "¡JUGADOR 2 (" + modoPvsP.getJugador2().getNombre() + ") GANÓ!";
            JOptionPane.showMessageDialog(this, ganador + "\n¡Nivel completado!",
                    "Victoria PvsP", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
            return true;
        }
        return false;
    }

    /**
     * @param skin   skin del jugador humano
     * @param config mapa de elementos habilitados (incluye dificultad_ia_*)
     */
    private void iniciarJuegoPvsM(String skin, java.util.Map<String,Boolean> config) {
        skinActual1 = skin;
        skinActual2 = null;
        modoActual  = "PvsM";
        try {
            LectorConfiguracion lector = new LectorConfiguracion();
            nivelRutaActual = "recursos/niveles/nivel1.txt";
            nivel = lector.cargar(nivelRutaActual);
            aplicarConfig(config);

            Cuadrado humano  = crearCuadrado(skin, nivel.getFilaSpawn(), nivel.getColumnaSpawn());
            String skinIA = skin.equals("Rojo") ? "Azul" : "Rojo";
            Cuadrado ia = crearCuadrado(skinIA, nivel.getFilaSpawn2(), nivel.getColumnaSpawn2());

            ModoPlayerVsMaquina.Dificultad dif = leerDificultad(config);
            modoPvsM   = new ModoPlayerVsMaquina(humano, ia, nivel, dif);
            modoPlayer = null;
            modoPvsP   = null;
            modoPvsM.iniciar();

            construirPantallaJuego(true);
            ajustarVentana();
            cardLayout.show(contenedor, "JUEGO");
            panelJuego.requestFocusInWindow();
            iniciarGameLoopPvsM();
        } catch (JuegoException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ModoPlayerVsMaquina.Dificultad leerDificultad(java.util.Map<String,Boolean> config) {
        if (Boolean.TRUE.equals(config.get("dificultad_ia_dificil"))) return ModoPlayerVsMaquina.Dificultad.DIFICIL;
        if (Boolean.TRUE.equals(config.get("dificultad_ia_facil")))   return ModoPlayerVsMaquina.Dificultad.FACIL;
        return ModoPlayerVsMaquina.Dificultad.NORMAL;
    }

    private void iniciarGameLoopPvsM() {
        timerMovimiento = new Timer(150, e -> {
            if (pausado) return;
            Direccion dir = getDireccionJ1();
            if (dir != Direccion.NINGUNA) {
                modoPvsM.getJugadorHumano().mover(dir, nivel.getTablero());
                nivel.recogerMonedasEn(modoPvsM.getJugadorHumano().getFila(),
                        modoPvsM.getJugadorHumano().getColumna(),
                        modoPvsM.getJugadorHumano());
                actualizarHUDPvsM();
                if (verificarEstadoPvsM()) return;
                panelJuego.repaint();
            }
        });
        timerMovimiento.start();

        timerLogica = new Timer(150, e -> {
            if (pausado) return;
            nivel.actualizarEnemigos();
            modoPvsM.actualizar();
            if (modoPvsM.getJugadorHumano() instanceof dominio.personajes.CuadradoVerde cv)
                cv.quitarInvulnerabilidad();
            actualizarHUDPvsM();
            if (verificarEstadoPvsM()) return;
            panelJuego.repaint();
        });
        timerLogica.start();

        timerSegundo = new Timer(1000, e -> {
            if (pausado) return;
            nivel.avanzarTiempo();
            if (nivel.tiempoAgotado()) {
                detenerTimers();
                JOptionPane.showMessageDialog(this,
                        "¡Tiempo agotado! Nadie completó el nivel.",
                        "Game Over", JOptionPane.WARNING_MESSAGE);
                volverAlMenu();
            }
        });
        timerSegundo.start();
    }

    private void actualizarHUDPvsM() {
        if (lblMuertes  != null) lblMuertes.setText("TÚ MUERTES: "  + modoPvsM.getJugadorHumano().getMuertes());
        if (lblMuertes2 != null) lblMuertes2.setText("IA MUERTES: " + modoPvsM.getJugadorIA().getMuertes());
        if (lblMonedas  != null) lblMonedas.setText("COINS: " + nivel.getMonedasRecogidas() + "/" + nivel.getMonedas().size());
    }

    /**
     * @return true si el modo PvsM terminó
     */
    private boolean verificarEstadoPvsM() {
        if (modoPvsM.verificarVictoria()) {
            detenerTimers();
            String resultado = modoPvsM.humanoGano()
                    ? "¡GANASTE! Completaste el nivel antes que la IA."
                    : "¡La IA ganó! Fue más rápida esta vez.";
            JOptionPane.showMessageDialog(this, resultado, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            volverAlMenu();
            return true;
        }
        return false;
    }

    /**
     * @param pvsp true si el modo es PvsP (HUD con dos columnas de stats)
     */
    private void construirPantallaJuego(boolean pvsp) {
        JPanel pantalla = new JPanel(new BorderLayout());

        JPanel hud = new JPanel(new BorderLayout());
        hud.setBackground(HUD_BG);
        hud.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        JButton btnMenu = new JButton("MENU");
        btnMenu.setFont(HUD_FONT); btnMenu.setForeground(HUD_FG);
        btnMenu.setBackground(HUD_BG); btnMenu.setBorderPainted(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenu.addActionListener(e -> volverAlMenu());

        boolean esPvsM = modoPvsM != null;

        JButton btnGuardar = new JButton("GUARDAR");
        btnGuardar.setFont(HUD_FONT);
        btnGuardar.setForeground(esPvsM ? new Color(100, 100, 100) : new Color(100, 220, 100));
        btnGuardar.setBackground(HUD_BG); btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setEnabled(!esPvsM);
        btnGuardar.setCursor(esPvsM ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarPartida());

        JButton btnCargar = new JButton("CARGAR");
        btnCargar.setFont(HUD_FONT);
        btnCargar.setForeground(esPvsM ? new Color(100, 100, 100) : new Color(100, 180, 255));
        btnCargar.setBackground(HUD_BG); btnCargar.setBorderPainted(false);
        btnCargar.setFocusPainted(false);
        btnCargar.setEnabled(!esPvsM);
        btnCargar.setCursor(esPvsM ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCargar.addActionListener(e -> cargarPartida());

        JPanel hudIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        hudIzq.setBackground(HUD_BG);
        hudIzq.add(btnMenu);
        hudIzq.add(btnGuardar);
        hudIzq.add(btnCargar);

        JLabel lblNivel = new JLabel("NIVEL 1", SwingConstants.CENTER);
        lblNivel.setForeground(HUD_FG); lblNivel.setFont(HUD_FONT);

        lblMonedas = new JLabel("COINS: 0/0", SwingConstants.CENTER);
        lblMonedas.setForeground(HUD_FG); lblMonedas.setFont(HUD_FONT);

        lblMuertes = new JLabel(pvsp ? "J1 MUERTES: 0" : "DEATHS: 0", SwingConstants.RIGHT);
        lblMuertes.setForeground(HUD_FG); lblMuertes.setFont(HUD_FONT);

        JPanel hudDer;
        if (pvsp) {
            lblMuertes2 = new JLabel("J2 MUERTES: 0", SwingConstants.RIGHT);
            lblMuertes2.setForeground(new Color(200, 230, 255));
            lblMuertes2.setFont(HUD_FONT);
            hudDer = new JPanel(new GridLayout(1, 3, 8, 0));
            hudDer.setBackground(HUD_BG);
            hudDer.add(lblMonedas);
            hudDer.add(lblMuertes);
            hudDer.add(lblMuertes2);
        } else {
            lblMuertes2 = null;
            hudDer = new JPanel(new GridLayout(1, 2, 10, 0));
            hudDer.setBackground(HUD_BG);
            hudDer.add(lblMonedas);
            hudDer.add(lblMuertes);
        }

        hud.add(hudIzq,   BorderLayout.WEST);
        hud.add(lblNivel, BorderLayout.CENTER);
        hud.add(hudDer,   BorderLayout.EAST);

        if (pvsp) {
            if (modoPvsM != null) {
                panelJuego = new PanelJuego(nivel, modoPvsM);
            } else {
                panelJuego = new PanelJuego(nivel, modoPvsP);
            }
        } else {
            panelJuego = new PanelJuego(nivel, modoPlayer);
        }
        JScrollPane scroll = new JScrollPane(panelJuego,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(panelJuego.getBackground());

        JPanel barraInf = new JPanel(new BorderLayout());
        barraInf.setBackground(BAR_BG);
        barraInf.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        String controles = pvsp
                ? (modoPvsM != null
                   ? "TÚ: W/A/S/D o flechas     IA: automática     Pausa: ESC"
                   : "J1: W/A/S/D     J2: ↑/↓/←/→     Pausa: ESC")
                : "Mover: W/A/S/D o flechas   Pausa: ESC";
        JLabel lblControles = new JLabel(controles, SwingConstants.CENTER);
        lblControles.setForeground(Color.LIGHT_GRAY);
        lblControles.setFont(new Font("Arial", Font.BOLD, 11));
        barraInf.add(lblControles, BorderLayout.CENTER);

        pantalla.add(hud,      BorderLayout.NORTH);
        pantalla.add(scroll,   BorderLayout.CENTER);
        pantalla.add(barraInf, BorderLayout.SOUTH);

        contenedor.add(pantalla, "JUEGO");
        configurarTeclado();
    }

    private void configurarTeclado() {
        panelJuego.setFocusable(true);

        if (keyDispatcher != null)
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);

        keyDispatcher = e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (modoPlayer != null || modoPvsP != null)) {
                    togglePausa();
                    return true;
                }
                teclasPresionadas.add(e.getKeyCode());
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                teclasPresionadas.remove(e.getKeyCode());
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
    }

    /**
     * @return dirección resultante
     */
    private Direccion getDireccionJ1() {
        boolean norte = teclasPresionadas.contains(KeyEvent.VK_W);
        boolean sur   = teclasPresionadas.contains(KeyEvent.VK_S);
        boolean este  = teclasPresionadas.contains(KeyEvent.VK_D);
        boolean oeste = teclasPresionadas.contains(KeyEvent.VK_A);
        if (modoPlayer != null) {
            norte |= teclasPresionadas.contains(KeyEvent.VK_UP);
            sur   |= teclasPresionadas.contains(KeyEvent.VK_DOWN);
            este  |= teclasPresionadas.contains(KeyEvent.VK_RIGHT);
            oeste |= teclasPresionadas.contains(KeyEvent.VK_LEFT);
        }
        return calcularDireccion(norte, sur, este, oeste);
    }

    /**
     * @return dirección resultante
     */
    private Direccion getDireccionJ2() {
        boolean norte = teclasPresionadas.contains(KeyEvent.VK_UP);
        boolean sur   = teclasPresionadas.contains(KeyEvent.VK_DOWN);
        boolean este  = teclasPresionadas.contains(KeyEvent.VK_RIGHT);
        boolean oeste = teclasPresionadas.contains(KeyEvent.VK_LEFT);
        return calcularDireccion(norte, sur, este, oeste);
    }

    private Direccion calcularDireccion(boolean norte, boolean sur, boolean este, boolean oeste) {
        if (norte && este)  return Direccion.NORESTE;
        if (norte && oeste) return Direccion.NOROESTE;
        if (sur   && este)  return Direccion.SURESTE;
        if (sur   && oeste) return Direccion.SUROESTE;
        if (norte)          return Direccion.NORTE;
        if (sur)            return Direccion.SUR;
        if (este)           return Direccion.ESTE;
        if (oeste)          return Direccion.OESTE;
        return Direccion.NINGUNA;
    }

    /**
     * @param skin   nombre del personaje ("Rojo", "Azul" o "Verde")
     * @param fila   fila de spawn
     * @param columna columna de spawn
     * @return instancia concreta de Cuadrado
     */
    private Cuadrado crearCuadrado(String skin, int fila, int columna) {
        return switch (skin) {
            case "Azul"  -> new CuadradoAzul(fila, columna);
            case "Verde" -> new CuadradoVerde(fila, columna);
            default      -> new CuadradoRojo(fila, columna);
        };
    }

    private void aplicarConfig(java.util.Map<String,Boolean> config) {
        nivel.getMonedas().removeIf(m -> {
            if (m instanceof dominio.objetivos.MonedaSkin)
                return !config.getOrDefault("moneda_skin", true);
            return !config.getOrDefault("moneda_amarilla", true);
        });
        nivel.getEnemigos().removeIf(e -> {
            if (e instanceof dominio.enemigos.PuntoAzulPatrullero)
                return !config.getOrDefault("enemigo_patrullero", true);
            if (e instanceof dominio.enemigos.DeslizadorVertical)
                return !config.getOrDefault("enemigo_deslizador", true);
            if (e instanceof dominio.enemigos.PuntoAcelerado)
                return !config.getOrDefault("enemigo_acelerado", true);
            return !config.getOrDefault("enemigo_basico", true);
        });
    }

    private void ajustarVentana() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int maxW = pantalla.width  - 100;
        int maxH = pantalla.height - 100;
        pack();
        if (getWidth() > maxW || getHeight() > maxH)
            setSize(Math.min(getWidth(), maxW), Math.min(getHeight(), maxH));
        setLocationRelativeTo(null);
    }

    private void detenerTimers() {
        if (timerLogica      != null) timerLogica.stop();
        if (timerSegundo     != null) timerSegundo.stop();
        if (timerMovimiento  != null) timerMovimiento.stop();
        if (timerMovimiento2 != null) timerMovimiento2.stop();
    }

    private void togglePausa() {
        pausado = true;
        JOptionPane.showMessageDialog(this, "PAUSA — presiona OK para continuar",
                "Pausado", JOptionPane.INFORMATION_MESSAGE);
        pausado = false;
    }

    private void volverAlMenu() {
        detenerTimers();
        if (keyDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
            keyDispatcher = null;
        }
        teclasPresionadas.clear();
        contenedor.remove(contenedor.getComponentCount() - 1);
        cardLayout.show(contenedor, "MENU");
        pack();
        setLocationRelativeTo(null);
    }

    private void guardarPartida() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar partida");
        chooser.setSelectedFile(new java.io.File("partida.sav"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        pausado = true;
        try {
            Cuadrado j1 = (modoPlayer != null) ? modoPlayer.getJugador()
                    : (modoPvsM  != null) ? modoPvsM.getJugadorHumano()
                      : modoPvsP.getJugador1();
            Cuadrado j2 = (modoPvsP != null) ? modoPvsP.getJugador2() : null;
            gestorPartida.guardar(chooser.getSelectedFile().getAbsolutePath(),
                    modoActual, skinActual1, skinActual2, j1, j2, nivel, nivelRutaActual);
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente.",
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            pausado = false;
            panelJuego.requestFocusInWindow();
        }
    }

    private void cargarPartida() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar partida");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        pausado = true;
        detenerTimers();

        try {
            java.util.Map<String, String> datos =
                    gestorPartida.cargar(chooser.getSelectedFile().getAbsolutePath());

            String modo  = datos.getOrDefault("modo",  "Player");
            String skin1 = datos.getOrDefault("skin1", "Rojo");
            String skin2 = datos.get("skin2");
            String skinTemporal1 = datos.get("skin_temporal1");
            String skinTemporal2 = datos.get("skin_temporal2");

            LectorConfiguracion lector = new LectorConfiguracion();
            String nivelRuta = datos.getOrDefault("nivel_ruta", "recursos/niveles/nivel1.txt");
            nivelRutaActual = nivelRuta;
            nivel = lector.cargar(nivelRuta);

            int tiempoGuardado = Integer.parseInt(datos.getOrDefault("tiempo", "0"));
            nivel.setTiempoTranscurrido(tiempoGuardado);

            String monedasStr = datos.getOrDefault("monedas", "");
            java.util.Set<String> recogidas = new java.util.HashSet<>();
            for (String par : monedasStr.split(";")) {
                if (!par.isBlank()) recogidas.add(par.trim());
            }
            for (dominio.objetivos.Moneda m : nivel.getMonedas()) {
                if (recogidas.contains(m.getFila() + "," + m.getColumna())) {
                    m.recolectar();
                }
            }

            int j1f = Integer.parseInt(datos.getOrDefault("j1_fila",    String.valueOf(nivel.getFilaSpawn())));
            int j1c = Integer.parseInt(datos.getOrDefault("j1_columna", String.valueOf(nivel.getColumnaSpawn())));
            int j1m = Integer.parseInt(datos.getOrDefault("j1_muertes", "0"));

            Cuadrado j1 = crearCuadrado(skin1, j1f, j1c);
            j1.setPosicion(j1f, j1c);
            j1.setMuertes(j1m);
            if (skinTemporal1 != null) j1.aplicarSkinTemporal(skinTemporal1);

            if ("PvsP".equals(modo) && skin2 != null) {
                int j2f = Integer.parseInt(datos.getOrDefault("j2_fila",    String.valueOf(nivel.getFilaSpawn2())));
                int j2c = Integer.parseInt(datos.getOrDefault("j2_columna", String.valueOf(nivel.getColumnaSpawn2())));
                int j2m = Integer.parseInt(datos.getOrDefault("j2_muertes", "0"));
                Cuadrado j2 = crearCuadrado(skin2, j2f, j2c);
                j2.setPosicion(j2f, j2c);
                j2.setMuertes(j2m);
                if (skinTemporal2 != null) j2.aplicarSkinTemporal(skinTemporal2);
                modoPvsP   = new dominio.modos.ModoPvsP(j1, j2, nivel);
                modoPlayer = null;
                modoPvsP.iniciar();
                skinActual1 = skin1; skinActual2 = skin2; modoActual = "PvsP";
                construirPantallaJuego(true);
            } else {
                modoPlayer = new dominio.modos.ModoPlayer(j1, nivel);
                modoPvsP   = null;
                modoPlayer.iniciar();
                skinActual1 = skin1; skinActual2 = null; modoActual = "Player";
                construirPantallaJuego(false);
            }

            ajustarVentana();
            cardLayout.show(contenedor, "JUEGO");
            panelJuego.requestFocusInWindow();
            pausado = false;

            if ("PvsP".equals(modo)) {
                iniciarGameLoopPvsP();
            } else {
                iniciarGameLoopPlayer();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            pausado = false;
        }
    }
}