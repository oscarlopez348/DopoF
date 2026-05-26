**Nombre:** Oscar Daniel Lopez Cruz  

---

## Estructura del proyecto

```
WORLDS HARDEST GAME/
├── src/
│   ├── Main.java
│   ├── dominio/
│   │   ├── enemigos/       (Enemigo, PuntoAzulBasico, PuntoAzulPatrullero, PuntoAcelerado, DeslizadorVertical)
│   │   ├── juego/          (Tablero, Nivel, Celda, TipoCelda, Direccion)
│   │   ├── modos/          (ModoJuego, ModoPlayer, ModoPvsP, ModoPlayerVsMaquina)
│   │   ├── objetivos/      (Moneda, MonedaAmarilla, MonedaSkin, MonedaPulso)
│   │   ├── personajes/     (Cuadrado, CuadradoRojo, CuadradoAzul, CuadradoVerde)
│   │   ├── persistencia/   (GestorPartida, LectorConfiguracion)
│   │   └── zonas/          (Zona, ZonaInicial, ZonaFinal)
│   └── presentacion/       (VentanaPrincipal, PanelMenu, PanelJuego)
├── test/                   (pruebas unitarias JUnit por paquete)
└── recursos/
    └── niveles/            (nivel1.txt, nivel2.txt, nivel3.txt)
```
## Ejecución desde consola (Windows)

### Requisitos
- Java JDK instalado

### Pasos

**1. Clonar o descargar el repositorio**
```bash
git clone https://github.com/fg08416-rgb/worlds-hardest-game.git
```
O descargar el ZIP desde GitHub → botón verde <> Code → Download ZIP y descomprimir.

**2. Abrir PowerShell en la carpeta del proyecto**

Dentro de la carpeta luego de descomprimir entrar a la subcarpeta WORLDS HARDEST GAME, hacer clic en la barra de direcciones, escribir powershell y presionar Enter.

**3. Compilar**
```powershell
$sources = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
javac -d out $sources
```

**4. Ejecutar**
```powershell
java -cp out Main
```



---

## I. REFACTORIZACIÓN (50%) — Comportamiento seleccionado: _[indicar cuál elegiste]_

### 1. Selección y deficiencias actuales

> _[Explicar aquí cuál de los 3 comportamientos candidatos se seleccionó y por qué. Describir las deficiencias concretas del código actual: si usa `instanceof`, cadenas de `if/else`, violación de OCP, SRP, etc.]_

---

### 2. Componentes BDD-MDD del comportamiento actual

#### Requisitos

| ID | Descripción |
|----|-------------|
| R-01 | |
| R-02 | |

#### Diseño estructural (zona del diagrama de clases relevante)

> _[Insertar recorte del diagrama Astah]_

#### Diseño de comportamiento (diagrama de secuencia)

> _[Insertar diagrama de secuencia]_

#### Código — capa presentación

```java
// [pegar aquí el fragmento relevante]
```

#### Código — capa aplicación

```java
// [pegar aquí el código actual con el problema]
```

#### Pruebas de unidad (actuales)

```java
// [pegar aquí los tests relevantes]
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| | |

> _[Insertar pantallas del juego]_

---

### 3. Patrón de diseño propuesto

**Patrón:** _[indicar cuál]_

**Justificación:** _[explicar por qué este patrón resuelve las deficiencias]_

---

### 4. Refactorización aplicada

#### Cambios en diseño estructural

> _[Insertar diagrama de clases actualizado]_

#### Cambios en código

```java
// [pegar aquí el código refactorizado]
```

#### Pruebas siguen pasando

> _[Insertar captura de ejecución de tests — verde en JUnit]_

---

## II. EXTENSIÓN (50%) — Moneda Pulso

### 1. Componentes BDD-MDD

#### Requisitos

| ID | Descripción |
|----|-------------|
| R-P01 | Al recolectar una `MonedaPulso`, todos los enemigos quedan congelados (no se mueven, no matan) durante 3 segundos. |
| R-P02 | Mientras el pulso está activo, el tiempo del nivel no avanza. |
| R-P03 | Al recolectar una `MonedaPulso` con pulso ya activo, el cronómetro se reinicia a 3 segundos. |
| R-P04 | La skin del jugador cambia visualmente a la skin de inmunidad ("Pulso") mientras dure el pulso. |
| R-P05 | Al terminar el pulso, cada enemigo retoma su movimiento desde la posición donde quedó congelado. |
| R-P06 | En modos PvsP y PvsM, el efecto sobre los enemigos es global. |
| R-P07 | La `MonedaPulso` debe recolectarse para completar el nivel (igual que `MonedaAmarilla`). |
| R-P08 | La `MonedaPulso` se declara en el `.txt` de configuración con el identificador `PU`. |
| R-P09 | Al guardar con pulso activo, se persiste el tiempo restante del pulso. Al cargar, se restaura correctamente. |

#### Diseño estructural

<img width="399" height="257" alt="image" src="https://github.com/user-attachments/assets/8fbc5286-a3a9-4c3c-abce-2fb18d96c7aa" />


Clases nuevas/modificadas:
- **`MonedaPulso`** (nueva) — extiende `Moneda`, devuelve color `#00CFFF`
- **`Nivel`** — agrega `pulsoActivo`, `tiempoRestantePulso`, métodos `activarPulso()` y `descontarTiempoPulso()`
- **`GestorPartida`** — persiste `pulso_activo` y `pulso_tiempo` al guardar
- **`LectorConfiguracion`** — reconoce el token `PU` en el `.txt`


#### Código — capa presentación

```java
// VentanaPrincipal.java — dentro de iniciarGameLoopPlayer()

// timerLogica (cada 250 ms): mueve enemigos solo si el pulso NO está activo
timerLogica = new Timer(250, e -> {
    if (pausado) return;
    if (!nivel.isPulsoActivo()) {
        nivel.actualizarEnemigos();
        nivel.verificarColisiones(modoPlayer.getJugador());
    }
    nivel.descontarTiempoPulso(0.25); // descuenta 0.25s cada tick
    actualizarHUDPlayer();
    verificarEstadoPlayer();
    panelJuego.repaint();
});

// timerSegundo (cada 1000 ms): el tiempo del nivel NO avanza si hay pulso activo
timerSegundo = new Timer(1000, e -> {
    if (pausado) return;
    if (!nivel.isPulsoActivo()) {
        nivel.avanzarTiempo(); // tiempo congelado mientras dure el pulso
    }
    actualizarHUDPlayer();
    panelJuego.repaint();
    if (nivel.tiempoAgotado()) {
        detenerTimers();
        JOptionPane.showMessageDialog(this,
            "Tiempo agotado!\nMuertes: " + modoPlayer.getJugador().getMuertes(),
            "Game Over", JOptionPane.WARNING_MESSAGE);
        volverAlMenu();
    }
});

// PanelJuego.paintComponent — la MonedaPulso se dibuja como rombo celeste con "P"
if (m instanceof MonedaPulso) {
    g2.setColor(colorMoneda);
    int[] xPoints = {cx, cx+8, cx, cx-8};
    int[] yPoints = {cy-9, cy, cy+9, cy};
    g2.fillPolygon(xPoints, yPoints, 4);
    g2.setColor(colorMoneda.darker());
    g2.drawPolygon(xPoints, yPoints, 4);
    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial Black", Font.BOLD, 7));
    g2.drawString("P", cx-3, cy+3);
}
// Skin del jugador durante el pulso:
// En dibujarJugador(), si getSkinTemporal().equals("Pulso") → colorHex = "#00CFFF"
```

#### Código — capa aplicación

```java
// MonedaPulso.java
package dominio.objetivos;

public class MonedaPulso extends Moneda {

    public MonedaPulso(int fila, int columna) {
        super(fila, columna);
    }

    @Override
    public String getColor() {
        return "#00CFFF"; // celeste/cian, diferenciado de amarillo y skins
    }
}

// Nivel.java — atributos para el pulso
private boolean pulsoActivo = false;
private double tiempoRestantePulso = 0.0;

// Nivel.java — recogerMonedasEn detecta MonedaPulso y activa el pulso
public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
    for (Moneda m : monedas) {
        if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
            jugador.limpiarSkinTemporal();
            m.recolectar(jugador);
            if (m instanceof MonedaSkin ms) {
                jugador.aplicarSkinTemporal(ms.getSkinAsociado());
            } else if (m instanceof MonedaPulso) {
                activarPulso();
                jugador.aplicarSkinTemporal("Pulso");
            }
        }
    }
}

// Nivel.java — activarPulso() inicia o reinicia el contador a 3 segundos
public void activarPulso() {
    this.pulsoActivo = true;
    this.tiempoRestantePulso = 3.0;
}

// Nivel.java — descontarTiempoPulso() llamado cada 250 ms desde VentanaPrincipal
public void descontarTiempoPulso(double segundos) {
    if (pulsoActivo) {
        tiempoRestantePulso -= segundos;
        if (tiempoRestantePulso <= 0) {
            tiempoRestantePulso = 0;
            pulsoActivo = false;
        }
    }
}

public boolean isPulsoActivo() { return pulsoActivo; }
public double getTiempoRestantePulso() { return tiempoRestantePulso; }
public void setPulsoActivo(boolean b) { this.pulsoActivo = b; }
public void setTiempoRestantePulso(double t) { this.tiempoRestantePulso = t; }
```

#### Código — capa persistencia

```java
// GestorPartida.java — guardar() persiste el estado del pulso si está activo
if (nivel.isPulsoActivo()) {
    pw.println("pulso_activo=true");
    pw.println("pulso_tiempo=" + nivel.getTiempoRestantePulso());
}

// GestorPartida.cargar() devuelve el mapa con claves "pulso_activo" y "pulso_tiempo"
// El modo los aplica con:
//   nivel.setPulsoActivo(true);
//   nivel.setTiempoRestantePulso(Double.parseDouble(datos.get("pulso_tiempo")));

// LectorConfiguracion.java — reconoce el token "PU" en la sección MONEDAS del .txt
case "MONEDAS" -> {
    String[] p = linea.split("\\s+");
    if (p[0].equals("A"))
        nivel.agregarMoneda(new MonedaAmarilla(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
    else if (p[0].equals("S"))
        nivel.agregarMoneda(new MonedaSkin(Integer.parseInt(p[1]), Integer.parseInt(p[2]), p[3]));
    else if (p[0].equals("PU"))
        nivel.agregarMoneda(new MonedaPulso(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
}

// nivel1.txt — declaración de MonedaPulso en el archivo de nivel
// MONEDAS=
// A 2 5
// A 3 8
// A 2 11
// S 4 7 Azul
// S 3 13 Verde
// PU 5 10      ← MonedaPulso en fila 5, columna 10
```

#### Pruebas de unidad

```java
// MonedaPulsoTest.java
package dominio.objetivos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonedaPulsoTest {

    @Test
    public void testMonedaPulsoNoEstaRecolectadaInicial() {
        MonedaPulso m = new MonedaPulso(2, 3);
        assertFalse(m.estaRecolectada());
    }

    @Test
    public void testMonedaPulsoTieneColorCeleste() {
        MonedaPulso m = new MonedaPulso(1, 1);
        assertEquals("#00CFFF", m.getColor());
    }
}



<img width="1112" height="236" alt="image" src="https://github.com/user-attachments/assets/d2bb975d-33dd-47b8-aff0-15ca69393932" />


#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa `MonedaPulso` en nivel 1 (fila 5, col 10) | Moneda desaparece, `isPulsoActivo()==true`, enemigos no se mueven, skin del jugador cambia a celeste, timer del nivel se pausa |
| 3 segundos después | `isPulsoActivo()==false`, enemigos reanudan movimiento, skin del jugador vuelve a la normal, timer del nivel continúa |

> _[Pantalla 1: momento de recolección — moneda rombo celeste visible, jugador con skin celeste "Pulso"]_  
> _[Pantalla 2: 3 s después — skin normal restaurada, enemigos en movimiento]_

---

### 2. Estrategia de extensibilidad

| Capa | Cambio |
|------|--------|
| **Dominio** | `MonedaPulso` extiende `Moneda` y sobreescribe `getColor()`. `Nivel` incorpora `pulsoActivo`, `tiempoRestantePulso`, `activarPulso()` y `descontarTiempoPulso()`. El control de congelación de enemigos se delega a la presentación consultando `isPulsoActivo()`. |
| **Presentación** | `VentanaPrincipal` consulta `nivel.isPulsoActivo()` antes de llamar `actualizarEnemigos()` y antes de llamar `avanzarTiempo()`. `PanelJuego` dibuja `MonedaPulso` como rombo celeste con "P" y al jugador con skin celeste cuando `getSkinTemporal().equals("Pulso")`. |
| **Persistencia** | `GestorPartida.guardar()` escribe `pulso_activo=true` y `pulso_tiempo=X.X` si el pulso está activo. `GestorPartida.cargar()` devuelve esas claves como parte del mapa. `LectorConfiguracion` reconoce el token `PU` en la sección `MONEDAS=` del `.txt` del nivel. |

La extensibilidad se apoya en la herencia de `Moneda`: agregar `MonedaPulso` no modifica ninguna clase existente salvo añadir el `case "PU"` en `LectorConfiguracion` y el `else if (m instanceof MonedaPulso)` en `Nivel.recogerMonedasEn`.

---

### 3. Patrones de software utilizados

| Patrón | Dónde aplica | Justificación |
|--------|-------------|---------------|
| **Template Method** | `Moneda.getColor()` abstracto | Cada subclase define su color; la lógica de dibujado en `PanelJuego` no necesita `instanceof` para el color. |
| **State** (implícito) | `Nivel` con `pulsoActivo` | El nivel se comporta de manera diferente (tiempo pausado, enemigos inactivos) según el estado del pulso. |
| **Factory** | `LectorConfiguracion` | Al agregar `PU`, solo se añade un `case` en el lector; el resto del sistema no cambia. |

---

## Herramientas

- **Lenguaje:** Java 17+
- **IDE:** Eclipse
- **Diagramas:** Astah (`.asta` incluido en la raíz del proyecto)
- **Testing:** JUnit 5


