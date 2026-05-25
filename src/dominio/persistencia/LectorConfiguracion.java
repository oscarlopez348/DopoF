package dominio.persistencia;

/**
 * Lee y parsea archivos de configuración de niveles en formato .txt.
 *
 * <p>Formato esperado del archivo:
 * <pre>
 * NOMBRE=Nivel 1
 * TIEMPO=60
 * FILAS=10
 * COLUMNAS=15
 * TABLERO=
 * . . . . . . . . . . . . . . .
 * . Z . . . . . . . . . . . F .
 * ...
 * MONEDAS=
 * A 2 3
 * A 2 7
 * ENEMIGOS=
 * B H 3 5    (B=básico, H=horizontal, fila, col)
 * B V 4 8    (V=vertical)
 * </pre>
 * Celdas del tablero: '.' libre, '#' pared, 'Z' zona inicio, 'F' zona fin.
 *
 * @authors Francisco Gomez, Oscar Lopez
 */
import dominio.enemigos.PuntoAzulBasico;
import dominio.enemigos.PuntoAzulPatrullero;
import dominio.enemigos.DeslizadorVertical;
import dominio.enemigos.PuntoAcelerado;
import dominio.excepciones.JuegoException;
import dominio.juego.Nivel;
import dominio.juego.Tablero;
import dominio.juego.TipoCelda;
import dominio.objetivos.MonedaAmarilla;
import dominio.objetivos.MonedaSkin;
import dominio.zonas.ZonaFinal;
import dominio.zonas.ZonaInicial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorConfiguracion {

    /**
     * Carga y construye un {@link Nivel} a partir del archivo en la ruta indicada.
     *
     * @param ruta ruta al archivo .txt de configuración del nivel
     * @return nivel construido con todos sus elementos (tablero, monedas, enemigos, zonas)
     * @throws JuegoException si el archivo no existe, no puede leerse o tiene formato inválido
     */
    public Nivel cargar(String ruta) throws JuegoException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String nombreNivel = "Sin nombre";
            int tiempo = 60;
            int filas = 0, columnas = 0;
            Tablero tablero = null;
            Nivel nivel = null;

            List<int[]> celdasZ = new ArrayList<>();
            List<int[]> celdasF = new ArrayList<>();

            String seccion = "";
            int filaTablero = 0;
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("//")) continue;

                if (linea.startsWith("NOMBRE="))    { nombreNivel = linea.substring(7); continue; }
                if (linea.startsWith("TIEMPO="))    { tiempo = Integer.parseInt(linea.substring(7)); continue; }
                if (linea.startsWith("FILAS="))     { filas = Integer.parseInt(linea.substring(6)); continue; }
                if (linea.startsWith("COLUMNAS="))  { columnas = Integer.parseInt(linea.substring(9)); continue; }

                if (linea.equals("TABLERO=")) {
                    tablero = new Tablero(filas, columnas);
                    nivel = new Nivel(nombreNivel, tablero, tiempo);
                    seccion = "TABLERO"; filaTablero = 0; continue;
                }
                if (linea.equals("MONEDAS="))  { seccion = "MONEDAS"; continue; }
                if (linea.equals("ENEMIGOS=")) { seccion = "ENEMIGOS"; continue; }

                switch (seccion) {
                    case "TABLERO" -> {
                        String[] celdas = linea.split("\\s+");
                        for (int col = 0; col < celdas.length; col++) {
                            switch (celdas[col]) {
                                case "#" -> tablero.getCelda(filaTablero, col).setTipo(TipoCelda.PARED);
                                case "Z" -> {
                                    tablero.getCelda(filaTablero, col).setTipo(TipoCelda.ZONA_INICIAL);
                                    celdasZ.add(new int[]{filaTablero, col});
                                }
                                case "F" -> {
                                    tablero.getCelda(filaTablero, col).setTipo(TipoCelda.ZONA_FINAL);
                                    celdasF.add(new int[]{filaTablero, col});
                                }
                            }
                        }
                        filaTablero++;
                    }
                    case "MONEDAS" -> {
                        String[] p = linea.split("\\s+");
                        if (p[0].equals("A"))
                            nivel.agregarMoneda(new MonedaAmarilla(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                        else if (p[0].equals("S"))
                            nivel.agregarMoneda(new MonedaSkin(Integer.parseInt(p[1]), Integer.parseInt(p[2]), p[3]));
                    }
                    case "ENEMIGOS" -> {
                        String[] p = linea.split("\\s+");
                        if (p[0].equals("B")) {
                            boolean horiz = p[1].equals("H");
                            nivel.agregarEnemigo(new PuntoAzulBasico(
                                    Integer.parseInt(p[2]), Integer.parseInt(p[3]), horiz));
                        } else if (p[0].equals("P")) {
                            nivel.agregarEnemigo(new PuntoAzulPatrullero(
                                    Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                                    Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                        } else if (p[0].equals("DV")) {
                            nivel.agregarEnemigo(new DeslizadorVertical(
                                    Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                        } else if (p[0].equals("PA")) {
                            boolean horiz = p[1].equals("H");
                            nivel.agregarEnemigo(new PuntoAcelerado(
                                    Integer.parseInt(p[2]), Integer.parseInt(p[3]), horiz));
                        }
                    }
                }
            }

            if (nivel == null)
                throw new JuegoException(JuegoException.CONFIG_FORMATO_INVALIDO + ": El archivo no tiene seccion TABLERO: " + ruta);

            if (!celdasZ.isEmpty()) {
                int[] bbox = getBoundingBox(celdasZ);
                nivel.agregarZona(new ZonaInicial(bbox[0], bbox[1], bbox[3] - bbox[1] + 1, bbox[2] - bbox[0] + 1));
            }

            if (!celdasF.isEmpty()) {
                int[] bbox = getBoundingBox(celdasF);
                nivel.agregarZona(new ZonaFinal(bbox[0], bbox[1], bbox[3] - bbox[1] + 1, bbox[2] - bbox[0] + 1));
            }

            return nivel;

        } catch (IOException e) {
            throw new JuegoException(JuegoException.CONFIG_NO_ENCONTRADA + ": " + ruta, e);
        } catch (NumberFormatException e) {
            throw new JuegoException(JuegoException.CONFIG_FORMATO_INVALIDO + ": numero invalido en " + ruta, e);
        }
    }

    /**
     * Calcula el bounding box (rectángulo mínimo) que encierra un conjunto de celdas.
     *
     * @param celdas lista de coordenadas [fila, columna] de las celdas
     * @return arreglo con [filaMin, colMin, filaMax, colMax]
     */
    private int[] getBoundingBox(List<int[]> celdas) {
        int filaMin = Integer.MAX_VALUE, colMin = Integer.MAX_VALUE;
        int filaMax = Integer.MIN_VALUE, colMax = Integer.MIN_VALUE;
        for (int[] c : celdas) {
            filaMin = Math.min(filaMin, c[0]);
            colMin  = Math.min(colMin,  c[1]);
            filaMax = Math.max(filaMax, c[0]);
            colMax  = Math.max(colMax,  c[1]);
        }
        return new int[]{filaMin, colMin, filaMax, colMax};
    }
}