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

## Ejecución desde consola (Windows PowerShell)

### Requisitos
- Java JDK instalado

### Pasos

**1. Clonar o descargar el repositorio**
```bash
git clone https://github.com/oscarlopez348/DopoF.git
```
O descargar el ZIP desde GitHub → botón verde `<> Code` → `Download ZIP` y descomprimir.

**2. Abrir PowerShell en la carpeta del proyecto**

Dentro de la carpeta luego de descomprimir entrar a la subcarpeta `WORLDS HARDEST GAME`, hacer clic en la barra de direcciones, escribir `powershell` y presionar Enter.

**3. Compilar**
```powershell
$sources = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
javac -d out $sources
```

**4. Ejecutar**
```powershell
java -cp out Main
```

## Ejecutar pruebas (Tests)

**1. Los JARs de JUnit ya están incluidos en la carpeta `lib/`**

**2. Compilar**
```powershell
$sources = Get-ChildItem -Recurse -Filter "*.java" src | Select-Object -ExpandProperty FullName
$tests = Get-ChildItem -Recurse -Filter "*.java" test | Select-Object -ExpandProperty FullName
javac -d out -cp "lib/*" ($sources + $tests)
```

**3. Ejecutar tests**
```powershell
java -jar "lib\junit-platform-console-standalone-1.10.2.jar" --scan-classpath --classpath out
```

---

## I. REFACTORIZACIÓN (50%) — Comportamiento seleccionado: Efecto de recolectar una moneda

### 1. Selección y deficiencias actuales

**Comportamiento seleccionado:** El efecto de recolectar una moneda.

**Deficiencias de la implementación actual:**

En `Nivel.recogerMonedasEn()` existe un bloque `instanceof` que viola el principio **Open/Closed (OCP)**:

```java
// Nivel.java — código actual con el problema
public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
    for (Moneda m : monedas) {
        if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
            jugador.limpiarSkinTemporal();
            m.recolectar(jugador);
            if (m instanceof MonedaSkin ms) {          // ← violación OCP
                jugador.aplicarSkinTemporal(ms.getSkinAsociado());
            }
        }
    }
}
```

Problemas concretos:
- `Nivel` (capa dominio) conoce los detalles internos de `MonedaSkin` — violación de **SRP**
- Cada nuevo tipo de moneda exige modificar `Nivel` con otro `else if` — violación de **OCP**
- La responsabilidad del efecto recae en `Nivel` en lugar de en la propia moneda

---

### 2. Componentes BDD-MDD del comportamiento actual

#### Requisitos

| ID | Descripción |
|----|-------------|
| R-01 | Cuando el jugador pisa una `MonedaAmarilla`, esta se marca como recolectada y suma al conteo del nivel |
| R-02 | Cuando el jugador pisa una `MonedaSkin`, esta se marca como recolectada y el jugador cambia su skin temporalmente |
| R-03 | El efecto de la moneda se aplica en `Nivel.recogerMonedasEn()` usando `instanceof` para distinguir el tipo |
#### Código — capa aplicación (actual con el problema)

```java
// Nivel.java
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
```

#### Pruebas de unidad (actuales)

```java
@Test
void accordingGLShould01MonedaSkinCambiaSkinAlRecolectar() {
    CuadradoRojo jugador = new CuadradoRojo(3, 3);
    MonedaSkin ms = new MonedaSkin(3, 3, "Azul");
    // actualmente el efecto lo aplica Nivel, no la moneda
    assertNull(jugador.getSkinTemporal());
}
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa `MonedaAmarilla` en su posición | Moneda marcada como recolectada, sin efecto visual en el jugador |
| Jugador pisa `MonedaSkin` de tipo "Azul" | Moneda recolectada, jugador cambia visualmente a skin azul |

---

### 3. Patrón de diseño propuesto

**Patrón:** Template Method (mediante herencia y polimorfismo)

**Justificación:** Se agrega el método abstracto `aplicarEfecto(Cuadrado)` en `Moneda`. Cada subclase implementa su propio efecto. `Nivel` deja de usar `instanceof` y simplemente llama `m.aplicarEfecto(jugador)`. Esto cumple:
- **OCP**: agregar nuevos tipos de moneda no modifica `Nivel`
- **SRP**: cada moneda es responsable de su propio efecto
- **DIP**: `Nivel` depende de `Moneda` (abstracción), no de `MonedaSkin` (concreto)

---

### 4. Refactorización aplicada

#### Cambios en diseño estructural

```
«abstract»
Moneda
+ recolectar(Cuadrado)
+ getColor(): String      ← abstracto
+ aplicarEfecto(Cuadrado) ← nuevo, abstracto

    ▲               ▲               ▲
    |               |               |
MonedaAmarilla   MonedaSkin      MonedaPulso
aplicarEfecto()  aplicarEfecto() aplicarEfecto()
→ sin efecto     → cambia skin   → congela enemigos

Nivel ──usa──> Moneda   (ya no conoce subclases)
```

#### Cambios en código

**`Moneda.java`** — agregar método abstracto:
```java
/**
 * Aplica el efecto especial de esta moneda al jugador que la recolecta.
 * Cada subclase define su propio comportamiento.
 *
 * @param jugador cuadrado que recoge la moneda
 */
public abstract void aplicarEfecto(Cuadrado jugador);
```

**`MonedaAmarilla.java`** — efecto vacío:
```java
@Override
public void aplicarEfecto(dominio.personajes.Cuadrado jugador) {
    // sin efecto especial — solo suma al conteo del nivel
}
```

**`MonedaSkin.java`** — efecto: cambiar skin:
```java
@Override
public void aplicarEfecto(dominio.personajes.Cuadrado jugador) {
    jugador.limpiarSkinTemporal();
    jugador.aplicarSkinTemporal(skinAsociado);
}
```

**`Nivel.recogerMonedasEn()`** — eliminar instanceof:
```java
public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
    for (Moneda m : monedas) {
        if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
            m.recolectar(jugador);
            m.aplicarEfecto(jugador);  // ← reemplaza el instanceof
        }
    }
}
```

#### Pruebas de unidad (refactorizadas)

```java
@Test
void accordingGLShould01MonedaAmarillaNoAplicaEfectoSkin() {
    CuadradoRojo jugador = new CuadradoRojo(1, 1);
    MonedaAmarilla m = new MonedaAmarilla(1, 1);
    m.aplicarEfecto(jugador);
    assertNull(jugador.getSkinTemporal());
}

@Test
void accordingGLShould02MonedaSkinAplicaSkinCorrecto() {
    CuadradoRojo jugador = new CuadradoRojo(1, 1);
    MonedaSkin m = new MonedaSkin(1, 1, "Azul");
    m.aplicarEfecto(jugador);
    assertEquals("Azul", jugador.getSkinTemporal());
}

@Test
void accordingGLShould03RecogerMonedasEnAplicaEfectoSinInstancof() {
    CuadradoRojo jugador = new CuadradoRojo(3, 3);
    MonedaSkin ms = new MonedaSkin(3, 3, "Verde");
    // verificar que el efecto se aplica sin instanceof en Nivel
    ms.recolectar(jugador);
    ms.aplicarEfecto(jugador);
    assertEquals("Verde", jugador.getSkinTemporal());
}
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa `MonedaAmarilla` | `aplicarEfecto()` ejecuta sin cambio de skin, moneda recolectada |
| Jugador pisa `MonedaSkin("Azul")` | `aplicarEfecto()` cambia skin a "Azul" sin `instanceof` en `Nivel` |
| Se agrega `MonedaPulso` nueva | `Nivel` no necesita modificarse — solo implementa `aplicarEfecto()` |

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
    nivel.descontarTiempoPulso(0.25);
    actualizarHUDPlayer();
    verificarEstadoPlayer();
    panelJuego.repaint();
});

// timerSegundo (cada 1000 ms): el tiempo del nivel NO avanza si hay pulso activo
timerSegundo = new Timer(1000, e -> {
    if (pausado) return;
    if (!nivel.isPulsoActivo()) {
        nivel.avanzarTiempo();
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
    public void aplicarEfecto(dominio.personajes.Cuadrado jugador) {
        jugador.limpiarSkinTemporal();
        jugador.aplicarSkinTemporal("Pulso");
    }

    @Override
    public String getColor() {
        return "#00CFFF";
    }
}

// Nivel.java — atributos para el pulso
private boolean pulsoActivo = false;
private double tiempoRestantePulso = 0.0;

public void activarPulso() {
    this.pulsoActivo = true;
    this.tiempoRestantePulso = 3.0;
}

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

// LectorConfiguracion.java — reconoce el token "PU"
case "MONEDAS" -> {
    String[] p = linea.split("\\s+");
    if (p[0].equals("A"))
        nivel.agregarMoneda(new MonedaAmarilla(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
    else if (p[0].equals("S"))
        nivel.agregarMoneda(new MonedaSkin(Integer.parseInt(p[1]), Integer.parseInt(p[2]), p[3]));
    else if (p[0].equals("PU"))
        nivel.agregarMoneda(new MonedaPulso(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
}

// nivel1.txt — declaración de MonedaPulso
// MONEDAS=
// A 2 5
// A 3 8
// S 4 7 Azul
// PU 5 10   ← MonedaPulso en fila 5, columna 10
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
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa `MonedaPulso` en nivel 1 (fila 5, col 10) | Moneda desaparece, `isPulsoActivo()==true`, enemigos no se mueven, skin del jugador cambia a celeste, timer del nivel se pausa |
| 3 segundos después | `isPulsoActivo()==false`, enemigos reanudan movimiento, skin del jugador vuelve a la normal, timer del nivel continúa |

---

### 2. Estrategia de extensibilidad

| Capa | Cambio |
|------|--------|
| **Dominio** | `MonedaPulso` extiende `Moneda` e implementa `aplicarEfecto()` y `getColor()`. `Nivel` incorpora `pulsoActivo`, `tiempoRestantePulso`, `activarPulso()` y `descontarTiempoPulso()`. |
| **Presentación** | `VentanaPrincipal` consulta `nivel.isPulsoActivo()` antes de llamar `actualizarEnemigos()` y `avanzarTiempo()`. `PanelJuego` dibuja `MonedaPulso` como rombo celeste con "P". |
| **Persistencia** | `GestorPartida.guardar()` escribe `pulso_activo` y `pulso_tiempo`. `LectorConfiguracion` reconoce el token `PU` en la sección `MONEDAS=`. |

La extensibilidad se apoya en la herencia de `Moneda` con el método abstracto `aplicarEfecto()`: agregar `MonedaPulso` no modifica `Nivel.recogerMonedasEn()` — cumple **Open/Closed**.

---

### 3. Patrones de software utilizados

| Patrón | Dónde aplica | Justificación |
|--------|-------------|---------------|
| **Template Method** | `Moneda.aplicarEfecto()` abstracto | Cada subclase define su efecto; `Nivel` no usa `instanceof`. |
| **State** (implícito) | `Nivel` con `pulsoActivo` | El nivel se comporta diferente según el estado del pulso (tiempo pausado, enemigos inactivos). |
| **Factory** | `LectorConfiguracion` | Al agregar `PU`, solo se añade un `case`; el resto del sistema no cambia. |

---

## Herramientas

- **Lenguaje:** Java 17+
- **IDE:** IntelliJ IDEA
- **Diagramas:** Astah (`.asta` incluido en la raíz del proyecto)
- **Testing:** JUnit 5
