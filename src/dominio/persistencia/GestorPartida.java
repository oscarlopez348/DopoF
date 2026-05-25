package dominio.persistencia;

/**
 * Guarda y carga el estado de una partida en un archivo .txt legible.
 * Formato:
 *   nivel_ruta=recursos/niveles/nivel1.txt
 *   modo=Player|PvsP
 *   skin1=Rojo|Azul|Verde
 *   skin2=Rojo|Azul|Verde        (solo PvsP)
 *   skin_temporal1=Rojo|Azul|Verde  (opcional, si hay skin activo en j1)
 *   skin_temporal2=Rojo|Azul|Verde  (opcional, solo PvsP)
 *   j1_fila=N
 *   j1_columna=N
 *   j1_muertes=N
 *   j2_fila=N                   (solo PvsP)
 *   j2_columna=N                (solo PvsP)
 *   j2_muertes=N                (solo PvsP)
 *   tiempo=N
 *   monedas=fila,col;fila,col;...  (solo las YA recogidas)
 *
 * @authors Francisco Gomez, Oscar Lopez
 */

import dominio.juego.Nivel;
import dominio.objetivos.Moneda;
import dominio.personajes.Cuadrado;

import java.io.*;
import java.util.*;

public class GestorPartida {

    /**
     * Guarda el estado actual de la partida en el archivo indicado.
     *
     * @param archivo   ruta del archivo destino
     * @param modo      "Player" o "PvsP"
     * @param skin1     skin del jugador 1
     * @param skin2     skin del jugador 2 (null en modo Player)
     * @param j1        jugador 1
     * @param j2        jugador 2 (null en modo Player)
     * @param nivel     nivel activo
     */
    public void guardar(String archivo, String modo, String skin1, String skin2,
                        Cuadrado j1, Cuadrado j2, Nivel nivel) throws IOException {
        guardar(archivo, modo, skin1, skin2, j1, j2, nivel, "recursos/niveles/nivel1.txt");
    }

    /**
     * Guarda el estado actual de la partida en el archivo indicado.
     *
     * @param archivo    ruta del archivo destino
     * @param modo       "Player" o "PvsP"
     * @param skin1      skin del jugador 1
     * @param skin2      skin del jugador 2 (null en modo Player)
     * @param j1         jugador 1
     * @param j2         jugador 2 (null en modo Player)
     * @param nivel      nivel activo
     * @param nivelRuta  ruta del archivo de configuración del nivel
     */
    public void guardar(String archivo, String modo, String skin1, String skin2,
                        Cuadrado j1, Cuadrado j2, Nivel nivel, String nivelRuta) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("nivel_ruta=" + nivelRuta);
            pw.println("modo=" + modo);
            pw.println("skin1=" + skin1);
            if (skin2 != null) pw.println("skin2=" + skin2);
            if (j1.getSkinTemporal() != null) pw.println("skin_temporal1=" + j1.getSkinTemporal());
            if (j2 != null && j2.getSkinTemporal() != null) pw.println("skin_temporal2=" + j2.getSkinTemporal());
            pw.println("j1_fila="    + j1.getFila());
            pw.println("j1_columna=" + j1.getColumna());
            pw.println("j1_muertes=" + j1.getMuertes());
            if (j2 != null) {
                pw.println("j2_fila="    + j2.getFila());
                pw.println("j2_columna=" + j2.getColumna());
                pw.println("j2_muertes=" + j2.getMuertes());
            }
            pw.println("tiempo=" + (nivel.getTiempoLimite() - nivel.getTiempoRestante()));

            StringBuilder sb = new StringBuilder("monedas=");
            for (Moneda m : nivel.getMonedas()) {
                if (m.estaRecolectada()) {
                    sb.append(m.getFila()).append(",").append(m.getColumna()).append(";");
                }
            }
            pw.println(sb);
        }
    }

    /**
     * Lee un archivo de partida guardada y devuelve sus datos como mapa clave→valor.
     *
     * @param archivo ruta del archivo a leer
     * @return mapa con las claves del archivo
     */
    public Map<String, String> cargar(String archivo) throws IOException {
        Map<String, String> datos = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                int idx = linea.indexOf('=');
                if (idx > 0) {
                    datos.put(linea.substring(0, idx).trim(),
                            linea.substring(idx + 1).trim());
                }
            }
        }
        return datos;
    }
}