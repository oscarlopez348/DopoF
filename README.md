# DOPO Hardest Game — Parcial Tercer Tercio (2026-1)

**Nombre:** ________________________________________  
**Nota esperada:** _____

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
│   │   ├── objetivos/      (Moneda, MonedaAmarilla, MonedaSkin)
│   │   ├── personajes/     (Cuadrado, CuadradoRojo, CuadradoAzul, CuadradoVerde)
│   │   ├── persistencia/   (GestorPartida, LectorConfiguracion)
│   │   └── zonas/          (Zona, ZonaInicial, ZonaFinal)
│   └── presentacion/       (VentanaPrincipal, PanelMenu, PanelJuego)
├── test/                   (pruebas unitarias JUnit por paquete)
└── recursos/
    └── niveles/            (nivel1.txt, nivel2.txt, nivel3.txt)
```

---

## I. REFACTORIZACIÓN (50%) — Comportamiento seleccionado: _[indicar cuál elegiste]_

### 1. Selección y deficiencias actuales

> _[Explicar aquí cuál de los 3 comportamientos candidatos se seleccionó y por qué. Describir las deficiencias concretas del código actual: si usa `instanceof`, cadenas de `if/else`, violación de OCP, SRP, etc.]_

**Ejemplo orientativo si se elige el comportamiento de recolectar monedas (`recogerMonedasEn` en `Nivel.java`):**

El método `recogerMonedasEn(int, int, Cuadrado)` en `Nivel` identifica el tipo de moneda con `instanceof MonedaSkin` y aplica el efecto directamente. Esto viola el **Principio Abierto/Cerrado (OCP)**: agregar un nuevo tipo de moneda obliga a modificar `Nivel`. También viola el **Principio de Responsabilidad Única (SRP)**, pues `Nivel` conoce los efectos específicos de cada moneda.

---

### 2. Componentes BDD-MDD del comportamiento actual

#### Requisitos

| ID | Descripción |
|----|-------------|
| R-01 | Cuando el jugador ocupa la casilla de una `MonedaAmarilla`, esta queda marcada como recolectada. |
| R-02 | Cuando el jugador ocupa la casilla de una `MonedaSkin`, esta queda marcada como recolectada y se aplica el skin temporal al jugador. |
| R-03 | Al recolectar cualquier moneda, se limpia el skin temporal previo antes de aplicar el nuevo efecto. |
| R-04 | Todas las monedas deben estar recolectadas para completar el nivel. |

#### Diseño estructural (zona del diagrama de clases relevante)

> _[Insertar recorte del diagrama Astah mostrando Nivel → Moneda → MonedaAmarilla / MonedaSkin]_

Clases involucradas: `Nivel`, `Moneda` (abstracta), `MonedaAmarilla`, `MonedaSkin`, `Cuadrado`.

#### Diseño de comportamiento (diagrama de secuencia)

> _[Insertar diagrama de secuencia: jugador mueve → Nivel.recogerMonedasEn → m instanceof MonedaSkin → jugador.aplicarSkinTemporal]_

#### Código — capa presentación

```java
// PanelJuego.java (fragmento relevante — ciclo de juego que llama a recogerMonedasEn)
// [pegar aquí el método de PanelJuego que invoca nivel.recogerMonedasEn]
```

#### Código — capa aplicación

```java
// Nivel.java
public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
    for (Moneda m : monedas) {
        if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
            jugador.limpiarSkinTemporal();
            m.recolectar(jugador);
            if (m instanceof MonedaSkin ms) {          // ← acoplamiento problemático
                jugador.aplicarSkinTemporal(ms.getSkinAsociado());
            }
        }
    }
}
```

#### Pruebas de unidad (actuales)

```java
// MonedaTest.java — [pegar aquí los tests relevantes y captura de ejecución]
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa casilla de MonedaAmarilla | Moneda queda `recolectada = true`; sin cambio de skin |
| Jugador pisa casilla de MonedaSkin (Azul) | Moneda recolectada y `getSkinTemporal() == "Azul"` |

> _[Insertar dos pantallas del juego mostrando el comportamiento]_

---

### 3. Patrón de diseño propuesto

**Patrón:** Strategy (o Visitor / Template Method — justificar la elección concreta)

**Justificación:**

Aplicar **Strategy** permite encapsular el efecto de cada tipo de moneda en una clase separada (`EfectoMoneda`). `Nivel` delega la aplicación del efecto a la moneda misma sin necesidad de conocer su tipo concreto, cumpliendo OCP (nuevas monedas solo agregan nuevas clases) y SRP (cada clase de efecto tiene una única responsabilidad).

---

### 4. Refactorización aplicada

#### Cambios en diseño estructural

> _[Insertar diagrama de clases actualizado: Moneda ahora tiene método `aplicarEfecto(Cuadrado)` o se introduce interfaz `EfectoMoneda`]_

#### Cambios en código

```java
// Moneda.java — nuevo método abstracto
public abstract void aplicarEfecto(Cuadrado jugador);

// MonedaAmarilla.java
@Override
public void aplicarEfecto(Cuadrado jugador) {
    // sin efecto adicional
}

// MonedaSkin.java
@Override
public void aplicarEfecto(Cuadrado jugador) {
    jugador.aplicarSkinTemporal(skinAsociado);
}

// Nivel.java — refactorizado
public void recogerMonedasEn(int fila, int columna, Cuadrado jugador) {
    for (Moneda m : monedas) {
        if (!m.estaRecolectada() && m.getFila() == fila && m.getColumna() == columna) {
            jugador.limpiarSkinTemporal();
            m.recolectar(jugador);
            m.aplicarEfecto(jugador);   // ← sin instanceof
        }
    }
}
```

#### Pruebas siguen pasando

> _[Insertar captura de ejecución de tests después de la refactorización — verde en JUnit]_

---

## II. EXTENSIÓN (50%) — Moneda Pulso

### 1. Componentes BDD-MDD

#### Requisitos

| ID | Descripción |
|----|-------------|
| R-P01 | Al recolectar una `MonedaPulso`, todos los enemigos quedan congelados (no se mueven, no matan) durante 3 segundos. |
| R-P02 | Mientras el pulso está activo, el tiempo del nivel no avanza. |
| R-P03 | Al recolectar una `MonedaPulso` con pulso ya activo, el cronómetro se reinicia a 3 segundos. |
| R-P04 | La skin del/los jugador(es) cambia visualmente a la skin de inmunidad mientras dure el pulso. |
| R-P05 | Al terminar el pulso, cada enemigo retoma su movimiento desde la posición donde quedó congelado. |
| R-P06 | En modos PvsP y PvsM, el efecto sobre los enemigos es global; si los dos jugadores se tocan durante el pulso, no pasa nada. |
| R-P07 | La `MonedaPulso` debe recolectarse para completar el nivel (igual que `MonedaAmarilla`). |
| R-P08 | La `MonedaPulso` se declara en el `.txt` de configuración con el identificador `PU`. |
| R-P09 | Al guardar con pulso activo, se persiste el tiempo restante del pulso. Al cargar, se restaura correctamente. |

#### Diseño estructural

> _[Insertar diagrama de clases mostrando: `MonedaPulso extends Moneda`, `Nivel` con atributos `pulsoActivo: boolean` y `tiempoRestantePulso: double`, cambios en `Enemigo.mover()`, cambios en `GestorPartida`]_

Clases nuevas/modificadas:
- **`MonedaPulso`** (nueva) — extiende `Moneda`, sobreescribe `aplicarEfecto` y `getColor`
- **`Nivel`** — agrega `pulsoActivo`, `tiempoRestantePulso`, métodos `activarPulso()`, `tick pulso`
- **`Enemigo`** — respeta el estado `congelado` antes de mover
- **`GestorPartida`** — persiste y restaura `pulso_tiempo`
- **`LectorConfiguracion`** — reconoce el token `PU` en el `.txt`

#### Diseño de comportamiento (diagramas de secuencia)

> _[Secuencia 1: jugador pisa MonedaPulso → Nivel.recogerMonedasEn → MonedaPulso.aplicarEfecto → nivel.activarPulso → congela enemigos]_

> _[Secuencia 2: tick del juego con pulso activo → nivel.tickPulso → si termina, descongela enemigos y restaura skin]_

#### Código — capa presentación

```java
// PanelJuego.java — en el timer del juego
// Si pulso activo: no llamar nivel.avanzarTiempo(); pintar moneda con color distinto;
// pintar jugador con color de inmunidad.

// [Pegar aquí el fragmento real del timer/paintComponent modificado]
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
        // El efecto real (congelar enemigos) lo activa Nivel al delegar
        // Se puede dejar vacío si Nivel detecta el tipo, o usar el patrón
        // con referencia al nivel — documentar la decisión de diseño tomada.
    }

    @Override
    public String getColor() {
        return "#00CFFF"; // azul cyan — diferente a amarillo y skins
    }
}

// Nivel.java — nuevos atributos y métodos
private boolean pulsoActivo = false;
private double tiempoRestantePulso = 0.0;

public void activarPulso(double duracion) {
    pulsoActivo = true;
    tiempoRestantePulso = duracion;
    // congelar enemigos
    for (Enemigo e : enemigos) e.setCongelado(true);
}

public void tickPulso(double delta) {
    if (!pulsoActivo) return;
    tiempoRestantePulso -= delta;
    if (tiempoRestantePulso <= 0) {
        pulsoActivo = false;
        tiempoRestantePulso = 0;
        for (Enemigo e : enemigos) e.setCongelado(false);
    }
}

public boolean isPulsoActivo() { return pulsoActivo; }
public double getTiempoRestantePulso() { return tiempoRestantePulso; }
public void setTiempoRestantePulso(double t) {
    tiempoRestantePulso = t;
    pulsoActivo = t > 0;
    for (Enemigo e : enemigos) e.setCongelado(pulsoActivo);
}

// Enemigo.java — nuevo campo
private boolean congelado = false;
public void setCongelado(boolean c) { this.congelado = c; }
public boolean isCongelado() { return congelado; }

// En cada subclase de Enemigo, en mover():
// if (congelado) return;
```

#### Pruebas de unidad

```java
// MonedaPulsoTest.java
@Test
public void alRecolectarMonedaPulso_enemigosQuedan Congelados() {
    // setup: nivel con un enemigo y una MonedaPulso en (2,3)
    // mover jugador a (2,3)
    // verificar nivel.isPulsoActivo() == true
    // verificar enemigo.isCongelado() == true
}

@Test
public void pulsoSeReiniciaSiSeRecolectaOtraMonedaPulso() {
    // activar pulso, avanzar 1.5s, recolectar segunda MonedaPulso
    // verificar tiempoRestantePulso ≈ 3.0
}

@Test
public void enemigosSeDescongelanAlTerminarPulso() {
    // activar pulso con 0.1s
    // tickPulso(0.2)
    // verificar congelado == false en todos los enemigos
}

@Test
public void tiempoDelNivelNOAvanzaMientrasPulsoActivo() {
    // activar pulso; llamar lógica de tick del juego
    // verificar que getTiempoRestante() no cambió
}

// [Insertar captura JUnit en verde]
```

#### Prueba de aceptación

| Escenario | Resultado esperado |
|-----------|-------------------|
| Jugador pisa `MonedaPulso` en nivel 1 | Moneda desaparece, todos los enemigos se detienen, skin del jugador cambia, timer del nivel se pausa |
| 3 segundos después | Enemigos reanudan movimiento, skin vuelve a normal, timer del nivel continúa |

> _[Pantalla 1: momento de recolección — enemigos visualmente detenidos, skin de inmunidad activa]_  
> _[Pantalla 2: 3 s después — enemigos en movimiento, skin normal restaurada]_

---

### 2. Estrategia de extensibilidad

| Capa | Cambio |
|------|--------|
| **Dominio** | `MonedaPulso` extiende `Moneda` y sobreescribe `aplicarEfecto`/`getColor`. `Nivel` incorpora el estado del pulso y su tick. `Enemigo` tiene el flag `congelado`. |
| **Presentación** | `PanelJuego` consulta `nivel.isPulsoActivo()` para pausar el timer visual, pintar la moneda en cyan y mostrar la skin de inmunidad. |
| **Persistencia** | `GestorPartida.guardar()` escribe `pulso_tiempo=X.X` si el pulso está activo. `cargar()` devuelve esa clave y el modo la restaura con `setTiempoRestantePulso()`. `LectorConfiguracion` reconoce `PU` en el `.txt` de nivel. |

La extensibilidad se apoya en el patrón aplicado en la refactorización (método `aplicarEfecto` polimórfico): agregar `MonedaPulso` no modifica `Nivel.recogerMonedasEn`, solo agrega una nueva clase.

---

### 3. Patrones de software utilizados

| Patrón | Dónde aplica | Justificación |
|--------|-------------|---------------|
| **Strategy / Template Method** | `Moneda.aplicarEfecto(Cuadrado)` | Encapsula el efecto variable de cada tipo de moneda; `Nivel` no usa `instanceof`. Cumple OCP y SRP. |
| **State** (implícito) | `Nivel` con `pulsoActivo` | El nivel se comporta de manera diferente (tiempo pausado, colisiones inofensivas) según el estado del pulso. Podría formalizarse con una clase `EstadoPulso`. |
| **Observer** (si se aplica) | `Enemigo` escucha evento de pulso | Alternativa para desacoplar la activación del pulso de la iteración directa sobre la lista de enemigos. |
| **Factory / LectorConfiguracion** | `LectorConfiguracion` crea monedas según token | Al agregar `PU`, solo se añade un `case "PU"` en el lector; el resto del sistema no cambia. |

---

## Herramientas

- **Lenguaje:** Java 17+
- **IDE:** Eclipse
- **Diagramas:** Astah (`.asta` incluido en la raíz del proyecto)
- **Testing:** JUnit 5

## Autores

- Francisco Gomez  
- Oscar Lopez

## Referencias

> _[Listar aquí cualquier recurso externo consultado, según lo exige el punto 4 del parcial]_
