package presentacion;

/**
 * Panel Swing que muestra el menú principal del juego.
 * Pantalla inicial: título estilo original + botón PLAY GAME.
 * Al presionar PLAY GAME se revela la selección de modo y personaje.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PanelMenu extends JPanel {

    public interface MenuListener {
        void onPlayGame(String skin, String skin2, String modo, Map<String,Boolean> config);
    }

    private MenuListener listener;
    private String skinSeleccionada       = "Rojo";
    private String skin2Seleccionada      = "Azul";
    private String modoSeleccionado       = "Player";
    private String dificultadSeleccionada = "Normal";

    private final Map<String,Boolean> config = new LinkedHashMap<>();

    private JPanel  panelSegundaPantalla;
    private JLabel  lblSub;
    private JLabel  lblTitulo;
    private JLabel  lblVersion;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PanelMenu(MenuListener listener) {
        this.listener = listener;
        config.put("moneda_amarilla",  true);
        config.put("moneda_skin",      true);
        config.put("enemigo_basico",   true);
        config.put("enemigo_patrullero", false);
        config.put("enemigo_deslizador", false);
        config.put("enemigo_acelerado",  false);
        setBackground(new Color(180, 190, 230));
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(680, 620));
        construirUI();
    }

    private void construirUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);

        lblSub = new JLabel("THE WORLD'S...");
        lblSub.setFont(new Font("Arial Black", Font.BOLD, 18));
        lblSub.setForeground(new Color(30, 30, 120));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        add(lblSub, gbc);

        lblTitulo = new JLabel("HARDEST GAME");
        lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 52));
        lblTitulo.setForeground(new Color(30, 60, 180));
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 100), 3),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        gbc.gridy = 1;
        add(lblTitulo, gbc);

        lblVersion = new JLabel("VERSION 1.0");
        lblVersion.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblVersion.setForeground(new Color(30, 30, 120));
        gbc.gridy = 2;
        add(lblVersion, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(Box.createVerticalStrut(10), gbc);
        gbc.insets = new Insets(6, 0, 6, 0);

        JButton btnPlay = crearBoton("PLAY GAME", new Color(220, 30, 30));
        gbc.gridy = 4;
        add(btnPlay, gbc);

        panelSegundaPantalla = construirSegundaPantalla(btnPlay);
        panelSegundaPantalla.setVisible(false);

        JScrollPane scroll = new JScrollPane(panelSegundaPantalla,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(660, 440));
        scroll.setVisible(false);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(scroll, gbc);

        btnPlay.addActionListener(e -> {
            if (!scroll.isVisible()) {
                lblSub.setVisible(false);
                lblTitulo.setVisible(false);
                lblVersion.setVisible(false);
                panelSegundaPantalla.setVisible(true);
                scroll.setVisible(true);
                revalidate();
                repaint();
            } else {
                String s2 = modoSeleccionado.equals("PvsP") ? skin2Seleccionada : null;
                config.put("dificultad_ia_" + dificultadSeleccionada.toLowerCase(), true);
                listener.onPlayGame(skinSeleccionada, s2, modoSeleccionado, config);
            }
        });
    }

    private JPanel construirSegundaPantalla(JButton btnPlay) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblModo = new JLabel("MODO DE JUEGO:");
        lblModo.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblModo.setForeground(new Color(30, 30, 120));
        lblModo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblModo);
        panel.add(Box.createVerticalStrut(6));

        JPanel panelModos = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelModos.setOpaque(false);
        JToggleButton btnPlayer = crearBotonModo("Player");
        JToggleButton btnPvsP   = crearBotonModo("Player vs Player");
        JToggleButton btnPvsM   = crearBotonModo("Player vs Máquina");
        ButtonGroup grupoModos  = new ButtonGroup();
        grupoModos.add(btnPlayer); grupoModos.add(btnPvsP); grupoModos.add(btnPvsM);
        btnPlayer.setSelected(true);

        JLabel lblJ2 = new JLabel("PERSONAJE JUGADOR 2:");
        lblJ2.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblJ2.setForeground(new Color(30, 30, 120));
        lblJ2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblJ2.setVisible(false);

        JPanel panelSkin2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelSkin2.setOpaque(false);
        panelSkin2.setVisible(false);

        JToggleButton btn2Rojo  = crearBotonSkin("Rojo (Blinky)",  new Color(226, 75,  74));
        JToggleButton btn2Azul  = crearBotonSkin("Azul (Inky)",    new Color(74,  144, 217));
        JToggleButton btn2Verde = crearBotonSkin("Verde (Clyde)",  new Color(76,  175, 80));
        ButtonGroup grupo2 = new ButtonGroup();
        grupo2.add(btn2Rojo); grupo2.add(btn2Azul); grupo2.add(btn2Verde);
        btn2Azul.setSelected(true);
        btn2Rojo.addActionListener(e  -> skin2Seleccionada = "Rojo");
        btn2Azul.addActionListener(e  -> skin2Seleccionada = "Azul");
        btn2Verde.addActionListener(e -> skin2Seleccionada = "Verde");
        panelSkin2.add(btn2Rojo); panelSkin2.add(btn2Azul); panelSkin2.add(btn2Verde);

        JLabel lblDificultad = new JLabel("DIFICULTAD DE LA IA:");
        lblDificultad.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblDificultad.setForeground(new Color(30, 30, 120));
        lblDificultad.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDificultad.setVisible(false);

        JPanel panelDificultad = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelDificultad.setOpaque(false);
        panelDificultad.setVisible(false);
        JToggleButton btnFacil   = crearBotonDificultad("Fácil",   new Color(60, 160, 60));
        JToggleButton btnNormal  = crearBotonDificultad("Normal",  new Color(60, 80, 180));
        JToggleButton btnDificil = crearBotonDificultad("Difícil", new Color(200, 50, 50));
        ButtonGroup grupoDif = new ButtonGroup();
        grupoDif.add(btnFacil); grupoDif.add(btnNormal); grupoDif.add(btnDificil);
        btnNormal.setSelected(true);
        btnFacil.addActionListener(e   -> dificultadSeleccionada = "Facil");
        btnNormal.addActionListener(e  -> dificultadSeleccionada = "Normal");
        btnDificil.addActionListener(e -> dificultadSeleccionada = "Dificil");
        panelDificultad.add(btnFacil); panelDificultad.add(btnNormal); panelDificultad.add(btnDificil);

        btnPlayer.addActionListener(e -> {
            modoSeleccionado = "Player";
            lblJ2.setVisible(false);
            panelSkin2.setVisible(false);
            lblDificultad.setVisible(false);
            panelDificultad.setVisible(false);
            revalidate(); repaint();
        });
        btnPvsP.addActionListener(e -> {
            modoSeleccionado = "PvsP";
            lblJ2.setVisible(true);
            panelSkin2.setVisible(true);
            lblDificultad.setVisible(false);
            panelDificultad.setVisible(false);
            revalidate(); repaint();
        });
        btnPvsM.addActionListener(e -> {
            modoSeleccionado = "PvsM";
            lblJ2.setVisible(false);
            panelSkin2.setVisible(false);
            lblDificultad.setVisible(true);
            panelDificultad.setVisible(true);
            revalidate(); repaint();
        });

        panelModos.add(btnPlayer); panelModos.add(btnPvsP); panelModos.add(btnPvsM);
        panel.add(panelModos);

        panel.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(400, 1));
        sep.setForeground(new Color(100, 110, 170));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(8));

        JLabel lblJ1 = new JLabel("ELIGE TU PERSONAJE:");
        lblJ1.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblJ1.setForeground(new Color(30, 30, 120));
        lblJ1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblJ1);
        panel.add(Box.createVerticalStrut(6));

        JPanel panelSkin1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelSkin1.setOpaque(false);
        JToggleButton btnRojo  = crearBotonSkin("Rojo (Blinky)",  new Color(226, 75,  74));
        JToggleButton btnAzul  = crearBotonSkin("Azul (Inky)",    new Color(74,  144, 217));
        JToggleButton btnVerde = crearBotonSkin("Verde (Clyde)",  new Color(76,  175, 80));
        ButtonGroup grupo1 = new ButtonGroup();
        grupo1.add(btnRojo); grupo1.add(btnAzul); grupo1.add(btnVerde);
        btnRojo.setSelected(true);
        btnRojo.addActionListener(e  -> skinSeleccionada = "Rojo");
        btnAzul.addActionListener(e  -> skinSeleccionada = "Azul");
        btnVerde.addActionListener(e -> skinSeleccionada = "Verde");
        panelSkin1.add(btnRojo); panelSkin1.add(btnAzul); panelSkin1.add(btnVerde);
        panel.add(panelSkin1);

        panel.add(Box.createVerticalStrut(6));
        panel.add(lblJ2);
        panel.add(Box.createVerticalStrut(6));
        panel.add(panelSkin2);

        panel.add(Box.createVerticalStrut(6));
        panel.add(lblDificultad);
        panel.add(Box.createVerticalStrut(6));
        panel.add(panelDificultad);

        panel.add(Box.createVerticalStrut(10));
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(400, 1));
        sep2.setForeground(new Color(100, 110, 170));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(8));

        JLabel lblConfig = new JLabel("CONFIGURAR NIVEL:");
        lblConfig.setFont(new Font("Arial Black", Font.BOLD, 14));
        lblConfig.setForeground(new Color(30, 30, 120));
        lblConfig.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblConfig);
        panel.add(Box.createVerticalStrut(6));

        JLabel lblMonedas = new JLabel("Monedas:");
        lblMonedas.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblMonedas.setForeground(new Color(60, 60, 120));
        lblMonedas.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblMonedas);
        panel.add(Box.createVerticalStrut(4));

        JPanel panelMonedas = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelMonedas.setOpaque(false);
        panelMonedas.add(crearCheckConfig("Amarilla",   "moneda_amarilla",  new Color(230, 190, 30),  true));
        panelMonedas.add(crearCheckConfig("Skin",        "moneda_skin",     new Color(74, 144, 217),  true));
        panel.add(panelMonedas);

        panel.add(Box.createVerticalStrut(6));
        JLabel lblEnemigos = new JLabel("Enemigos:");
        lblEnemigos.setFont(new Font("Arial Black", Font.BOLD, 12));
        lblEnemigos.setForeground(new Color(60, 60, 120));
        lblEnemigos.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblEnemigos);
        panel.add(Box.createVerticalStrut(4));

        JPanel panelEnemigos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelEnemigos.setOpaque(false);
        panelEnemigos.add(crearCheckConfig("Básico",      "enemigo_basico",     new Color(50, 100, 200), true));
        panelEnemigos.add(crearCheckConfig("Patrullero",  "enemigo_patrullero", new Color(30, 60, 180),  false));
        panelEnemigos.add(crearCheckConfig("Deslizador",  "enemigo_deslizador", new Color(80, 160, 255), false));
        panelEnemigos.add(crearCheckConfig("Acelerado",   "enemigo_acelerado",  new Color(220, 50, 50),  false));
        panel.add(panelEnemigos);

        return panel;
    }

    private JToggleButton crearBotonModo(String texto) {
        Color color = new Color(60, 80, 180);
        JToggleButton btn = new JToggleButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? color : color.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isSelected() ? Color.WHITE : color.darker().darker());
                g2.setStroke(new BasicStroke(isSelected() ? 3 : 1));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JToggleButton crearBotonSkin(String texto, Color color) {
        JToggleButton btn = new JToggleButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? color : color.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isSelected() ? Color.WHITE : color.darker().darker());
                g2.setStroke(new BasicStroke(isSelected() ? 3 : 1));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JToggleButton crearCheckConfig(String texto, String clave, Color color, boolean inicial) {
        JToggleButton btn = new JToggleButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? color : new Color(180, 180, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isSelected() ? color.darker() : new Color(140, 140, 160));
                g2.setStroke(new BasicStroke(isSelected() ? 2 : 1));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setSelected(inicial);
        btn.addActionListener(e -> config.put(clave, btn.isSelected()));
        return btn;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())       g2.setColor(color.darker());
                else if (getModel().isRollover()) g2.setColor(color.brighter());
                else                              g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(color.darker().darker());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 28));
        btn.setPreferredSize(new Dimension(260, 60));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JToggleButton crearBotonDificultad(String texto, Color color) {
        JToggleButton btn = new JToggleButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? color : color.darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(isSelected() ? Color.WHITE : color.darker().darker());
                g2.setStroke(new BasicStroke(isSelected() ? 3 : 1));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Arial Black", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 44));
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}