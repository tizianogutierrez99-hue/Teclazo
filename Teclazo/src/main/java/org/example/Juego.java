package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.prefs.Preferences;

/*
 * Juego.java
 *
 * Es el núcleo del programa. Guarda el estado de la partida y controla
 * el flujo del juego: arrancar, revisar letras, sumar puntos, terminar.
 *
 * También decide qué pantalla mostrar en cada momento, coordinando
 * con PantallaInicio, PantallaJuego y PantallaFin.
 */
public class Juego {

    // Datos de cada nivel.
    public static final String[] NIVELES        = {"Fácil", "Medio", "Difícil", "Hardcore"};
    public static final int[]    SEGUNDOS_EXTRA  = {5, 3, 1, 1};
    public static final int      TIEMPO_INICIAL  = 20;
    public static final String[] COLOR_NIVEL     = {
            "#3498db",  // Fácil    - azul
            "#27ae60",  // Medio    - verde
            "#e67e22",  // Difícil  - naranja
            "#c0392b"   // Hardcore - rojo
    };

    // Estado de la partida.
    private int     nivel   = 0;
    private int     tiempo;
    private int     puntos;
    private int     errores;
    private boolean jugando;
    private String  palabra;

    private Timeline      cronometro;
    private PantallaJuego pantallaJuego; // referencia para actualizar los labels en tiempo real

    private final StackPane   raiz;
    private final Sonido      sonido;
    private final Preferences guardado;

    public Juego(StackPane raiz) {
        this.raiz     = raiz;
        this.sonido   = new Sonido();
        this.guardado = Preferences.userNodeForPackage(Juego.class);
    }

    // Control de pantallas

    public void mostrarInicio() {
        PantallaInicio pantalla = new PantallaInicio(this);
        raiz.getChildren().setAll(pantalla.construir());
    }

    public void mostrarJuego() {
        pantallaJuego = new PantallaJuego(this);
        raiz.getChildren().setAll(pantallaJuego.construir());
        pantallaJuego.enfocarCampo();
    }

    public void mostrarFin(boolean perdioPorError) {
        PantallaFin pantalla = new PantallaFin(this);
        raiz.getChildren().setAll(pantalla.construir(perdioPorError));
    }

    // Lógica del juego, extraída de Main.java sin cambios

    public void empezarPartida() {
        tiempo  = TIEMPO_INICIAL;
        puntos  = 0;
        errores = 0;
        jugando = true;
        palabra = Palabras.getRandom(nivel, "");

        mostrarJuego();
        arrancarCronometro();
    }

    // Timeline con un KeyFrame de 1 segundo: baja el tiempo, actualiza
    // la barra y termina el juego cuando llega a cero
    private void arrancarCronometro() {
        if (cronometro != null) cronometro.stop();

        KeyFrame tick = new KeyFrame(Duration.seconds(1), e -> {
            tiempo--;
            pantallaJuego.actualizarTiempo(tiempo);
            if (tiempo <= 0) terminarPartida(false);
        });

        cronometro = new Timeline(tick);
        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
    }

    // Compara lo que se está escribiendo con la palabra actual.
    // Solo cuenta el error cuando se agrega una letra nueva, no al borrar
    public void revisarLetras(String ahora, String antes) {
        boolean escribioLetraNueva = ahora.length() > antes.length();

        if (escribioLetraNueva) {
            int pos = ahora.length() - 1;
            if (pos < palabra.length()) {
                char esperada = palabra.charAt(pos);
                char escrita  = ahora.charAt(pos);
                if (escrita != esperada) {
                    errores++;
                    sonido.mal();
                    pantallaJuego.actualizarErrores(errores);

                    // En Hardcore, cualquier error termina la partida
                    if (nivel == 3) {
                        terminarPartida(true);
                        return;
                    }
                }
            }
        }

        if (ahora.equals(palabra)) {
            palabraCompletada();
            return;
        }

        // Pintamos el campo según si lo escrito hasta acá está bien o mal
        boolean hayError = false;
        for (int i = 0; i < ahora.length(); i++) {
            if (i >= palabra.length() || ahora.charAt(i) != palabra.charAt(i)) {
                hayError = true;
                break;
            }
        }
        pantallaJuego.pintarCampo(hayError ? "mal" : "bien");
    }

    private void palabraCompletada() {
        puntos++;
        sonido.bien();
        pantallaJuego.actualizarPuntos(puntos);

        // Sumamos el tiempo correspondiente al nivel y reseteamos los colores
        tiempo += SEGUNDOS_EXTRA[nivel];
        pantallaJuego.actualizarTiempo(tiempo);

        String siguiente;
        do { siguiente = Palabras.getRandom(nivel, palabra); } while (siguiente.equals(palabra));
        palabra = siguiente;
        pantallaJuego.actualizarPalabra(palabra);

        // Usamos runLater porque no podemos modificar el TextField directamente
        // desde su propio listener, eso tira una excepción en JavaFX
        Platform.runLater(() -> pantallaJuego.limpiarCampo());
    }

    private void terminarPartida(boolean porError) {
        jugando = false;
        if (cronometro != null) cronometro.stop();
        sonido.fin();

        // Guardamos el record si se mejoró
        String claveRecord    = "record_" + NIVELES[nivel];
        int    recordAnterior = guardado.getInt(claveRecord, 0);
        if (puntos > recordAnterior) guardado.putInt(claveRecord, puntos);

        mostrarFin(porError);
    }

    // Getters y setters que usan las pantallas para leer el estado

    public int     getNivel()   { return nivel; }
    public int     getTiempo()  { return tiempo; }
    public int     getPuntos()  { return puntos; }
    public int     getErrores() { return errores; }
    public boolean isJugando()  { return jugando; }
    public String  getPalabra() { return palabra; }
    public void    setNivel(int n) { this.nivel = n; }

    public int getRecord(int indiceNivel) {
        return guardado.getInt("record_" + NIVELES[indiceNivel], 0);
    }

    // Descripción corta de cada nivel, para el Tooltip y la pantalla de inicio
    public String textoNivel(int i) {
        return switch (i) {
            case 0 -> "Fácil: cada palabra suma 5 segundos";
            case 1 -> "Medio: cada palabra suma 3 segundos";
            case 2 -> "Difícil: cada palabra suma 1 segundo";
            case 3 -> "Hardcore: 1 segundo por palabra, un error termina la partida";
            default -> "";
        };
    }
}