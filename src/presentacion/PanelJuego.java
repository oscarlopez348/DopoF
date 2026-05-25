package presentacion;

/**
 * Panel Swing que dibuja el estado visual del nivel en cada ciclo de juego.
 * Soporta tanto el modo Player (un jugador) como el modo PvsP (dos jugadores).
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import dominio.enemigos.Enemigo;
import dominio.enemigos.DeslizadorVertical;
import dominio.enemigos.PuntoAzulPatrullero;
import dominio.enemigos.PuntoAcelerado;
import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.modos.ModoPlayer;
import dominio.modos.ModoPvsP;
import dominio.modos.ModoPlayerVsMaquina;
import dominio.objetivos.Moneda;
import dominio.objetivos.MonedaSkin;
import dominio.personajes.Cuadrado;

import javax.swing.*;
import java.awt.*;

public class PanelJuego extends JPanel {

    public static final int TAM_CELDA = 40;

    private static final Color COLOR_FONDO        = new Color(175, 185, 225);
    private static final Color COLOR_ZONA_SEGURA  = new Color(150, 210, 150);
    private static final Color COLOR_TABLERO_A    = new Color(230, 230, 230);
    private static final Color COLOR_TABLERO_B    = new Color(200, 200, 215);
    private static final Color COLOR_BORDE_ZONA   = new Color(30,  30,  30);
    private static final Color COLOR_MONEDA       = new Color(230, 190,  30);
    private static final Color COLOR_ENEMIGO      = new Color(50,  100, 200);
    private static final String FUENTE            = "Arial Black";

    private Nivel nivel;
    private ModoPlayer modoPlayer;
    private ModoPvsP   modoPvsP;
    private ModoPlayerVsMaquina modoPvsM;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PanelJuego(Nivel nivel, ModoPlayer modoPlayer) {
        this.nivel      = nivel;
        this.modoPlayer = modoPlayer;
        inicializarTamaño();
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PanelJuego(Nivel nivel, ModoPvsP modoPvsP) {
        this.nivel    = nivel;
        this.modoPvsP = modoPvsP;
        inicializarTamaño();
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PanelJuego(Nivel nivel, ModoPlayerVsMaquina modoPvsM) {
        this.nivel    = nivel;
        this.modoPvsM = modoPvsM;
        inicializarTamaño();
    }

    private void inicializarTamaño() {
        Tablero t = nivel.getTablero();
        setPreferredSize(new Dimension(t.getColumnas() * TAM_CELDA, t.getFilas() * TAM_CELDA));
        setBackground(COLOR_FONDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Tablero tablero = nivel.getTablero();
        g2.setColor(COLOR_FONDO);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                TipoCelda tipo = tablero.getCelda(f, c).getTipo();
                int px = c * TAM_CELDA;
                int py = f * TAM_CELDA;
                switch (tipo) {
                    case ZONA_INICIAL, ZONA_FINAL -> {
                        g2.setColor(COLOR_ZONA_SEGURA);
                        g2.fillRect(px, py, TAM_CELDA, TAM_CELDA);
                    }
                    case LIBRE -> {
                        g2.setColor((f + c) % 2 == 0 ? COLOR_TABLERO_A : COLOR_TABLERO_B);
                        g2.fillRect(px, py, TAM_CELDA, TAM_CELDA);
                    }
                }
            }
        }

        g2.setColor(COLOR_BORDE_ZONA);
        g2.setStroke(new BasicStroke(2f));
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (tablero.getCelda(f, c).getTipo() == TipoCelda.PARED) continue;
                int px = c * TAM_CELDA, py = f * TAM_CELDA;
                if (f == 0 || tablero.getCelda(f-1,c).getTipo()==TipoCelda.PARED)
                    g2.drawLine(px, py, px+TAM_CELDA, py);
                if (f==tablero.getFilas()-1 || tablero.getCelda(f+1,c).getTipo()==TipoCelda.PARED)
                    g2.drawLine(px, py+TAM_CELDA, px+TAM_CELDA, py+TAM_CELDA);
                if (c==0 || tablero.getCelda(f,c-1).getTipo()==TipoCelda.PARED)
                    g2.drawLine(px, py, px, py+TAM_CELDA);
                if (c==tablero.getColumnas()-1 || tablero.getCelda(f,c+1).getTipo()==TipoCelda.PARED)
                    g2.drawLine(px+TAM_CELDA, py, px+TAM_CELDA, py+TAM_CELDA);
            }
        }

        for (Moneda m : nivel.getMonedas()) {
            if (!m.estaRecolectada()) {
                int cx = m.getColumna()*TAM_CELDA + TAM_CELDA/2;
                int cy = m.getFila()*TAM_CELDA + TAM_CELDA/2;
                Color colorMoneda = Color.decode(m.getColor());
                g2.setColor(colorMoneda);
                g2.fillOval(cx-7, cy-7, 14, 14);
                g2.setColor(colorMoneda.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx-7, cy-7, 14, 14);
                if (m instanceof MonedaSkin) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(FUENTE, Font.BOLD, 7));
                    g2.drawString("S", cx-3, cy+3);
                }
            }
        }

        g2.setStroke(new BasicStroke(1.5f));
        for (Enemigo e : nivel.getEnemigos()) {
            int cx = e.getColumna()*TAM_CELDA + TAM_CELDA/2;
            int cy = e.getFila()*TAM_CELDA + TAM_CELDA/2;
            if (e instanceof DeslizadorVertical) {
                g2.setColor(new Color(80, 160, 255));
                g2.fillRect(cx-8, cy-10, 16, 20);
                g2.setColor(new Color(40, 100, 200));
                g2.drawRect(cx-8, cy-10, 16, 20);
            } else if (e instanceof PuntoAzulPatrullero) {
                g2.setColor(new Color(30, 60, 180));
                g2.fillOval(cx-11, cy-11, 22, 22);
                g2.setColor(new Color(10, 30, 120));
                g2.drawOval(cx-11, cy-11, 22, 22);
            } else if (e instanceof PuntoAcelerado) {
                g2.setColor(new Color(220, 50, 50));
                g2.fillOval(cx-12, cy-12, 24, 24);
                g2.setColor(new Color(140, 20, 20));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx-12, cy-12, 24, 24);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FUENTE, Font.BOLD, 7));
                g2.drawString("A", cx-3, cy+3);
            } else {
                g2.setColor(COLOR_ENEMIGO);
                g2.fillOval(cx-10, cy-10, 20, 20);
                g2.setColor(COLOR_ENEMIGO.darker());
                g2.drawOval(cx-10, cy-10, 20, 20);
            }
        }

        if (modoPlayer != null) {
            dibujarJugador(g2, modoPlayer.getJugador(), false);
        } else if (modoPvsP != null) {
            dibujarJugador(g2, modoPvsP.getJugador1(), false);
            dibujarJugador(g2, modoPvsP.getJugador2(), true);
        } else if (modoPvsM != null) {
            dibujarJugador(g2, modoPvsM.getJugadorHumano(), false);
            dibujarJugadorIA(g2, modoPvsM.getJugadorIA());
        }
    }

    private void dibujarJugador(Graphics2D g2, Cuadrado jugador, boolean esJ2) {
        int tamBase = (int)(TAM_CELDA * jugador.getTamaño());
        int margen  = (TAM_CELDA - tamBase) / 2;
        int jx = jugador.getColumna()*TAM_CELDA + margen;
        int jy = jugador.getFila()*TAM_CELDA + margen;

        String colorHex = jugador.getColor();
        if (jugador.getSkinTemporal() != null) {
            colorHex = switch (jugador.getSkinTemporal()) {
                case "Azul"  -> "#4A90D9";
                case "Verde" -> "#4CAF50";
                default      -> "#E24B4A";
            };
        }

        g2.setColor(Color.decode(colorHex));
        g2.fillRect(jx, jy, tamBase, tamBase);
        g2.setColor(Color.decode(colorHex).darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(jx, jy, tamBase, tamBase);

        if (jugador.getSkinTemporal() != null) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(FUENTE, Font.BOLD, 7));
            g2.drawString("S", jx + tamBase - 9, jy + 9);
        }

        if (modoPvsP != null) {
            String label = esJ2 ? "J2" : "J1";
            g2.setFont(new Font(FUENTE, Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int lx = jx + (tamBase - fm.stringWidth(label)) / 2;
            int ly = jy + (tamBase + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(label, lx, ly);
        } else if (modoPvsM != null) {
            g2.setFont(new Font(FUENTE, Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int lx = jx + (tamBase - fm.stringWidth("TÚ")) / 2;
            int ly = jy + (tamBase + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString("TÚ", lx, ly);
        }
    }

    private void dibujarJugadorIA(Graphics2D g2, Cuadrado ia) {
        int tamBase = (int)(TAM_CELDA * ia.getTamaño());
        int margen  = (TAM_CELDA - tamBase) / 2;
        int jx = ia.getColumna()*TAM_CELDA + margen;
        int jy = ia.getFila()*TAM_CELDA + margen;

        String colorHex = ia.getColor();
        if (ia.getSkinTemporal() != null) {
            colorHex = switch (ia.getSkinTemporal()) {
                case "Azul"  -> "#4A90D9";
                case "Verde" -> "#4CAF50";
                default      -> "#E24B4A";
            };
        }

        g2.setColor(Color.decode(colorHex));
        g2.fillRect(jx, jy, tamBase, tamBase);
        g2.setColor(Color.decode(colorHex).darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(jx, jy, tamBase, tamBase);

        g2.setFont(new Font(FUENTE, Font.BOLD, 9));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int lx = jx + (tamBase - fm.stringWidth("IA")) / 2;
        int ly = jy + (tamBase + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString("IA", lx, ly);
    }
}