package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.prefs.Preferences;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/*
 * Teclazo - Juego de mecanografía
 *
 * El jugador ve una palabra en pantalla y tiene que escribirla antes
 * de que se acabe el tiempo. Cada palabra correcta suma segundos al
 * cronómetro. El juego termina cuando el tiempo llega a cero.
 *
 * Componentes de JavaFX usados:
 *   Stage, Scene, StackPane, BorderPane, GridPane, VBox, HBox,
 *   Label, TextField, Button, RadioButton, ToggleGroup,
 *   Tooltip, ProgressBar, Separator, Timeline, KeyFrame
 */
public class Main extends Application {

    // Niveles de dificultad
    // Cada índice corresponde a: 0=Fácil, 1=Medio, 2=Difícil, 3=Hardcore

    private static final String[] NIVELES        = {"Fácil", "Medio", "Difícil", "Hardcore"};
    private static final int[]    SEGUNDOS_EXTRA  = {5, 3, 1, 1};   // tiempo que suma cada palabra según nivel
    private static final int      TIEMPO_INICIAL  = 20;             // segundos al arrancar

    // color de cada nivel para usar en los estilos CSS
    private static final String[] COLOR_NIVEL = {
            "#3498db",  // Facil - azul
            "#27ae60",  // Medio - verde
            "#e67e22",  // Dificil - naranja
            "#c0392b"   // Hardcore - rojo
    };

    // Estado del juego
    // Estas variables cambian durante la partida

    private int     nivel   = 0;     // nivel seleccionado
    private int     tiempo;          // segundos restantes
    private int     puntos;          // palabras completadas
    private int     errores;         // errores cometidos (no se resetean al borrar)
    private boolean jugando;         // indica si la partida esta activa
    private String  palabra;         // palabra que hay que escribir ahora

    private Timeline cronometro;  // el temporizador regresivo
    private Clip     clipBien;    // sonido al completar una palabra
    private Clip     clipMal;     // sonido al errar una tecla

    // Referencias a los nodos de la interfaz que se modifican en tiempo real

    private Label       lblPalabra;   // la palabra a escribir
    private TextField   txtEscribir;  // campo de entrada del usuario
    private Label       lblTiempo;    // contador de tiempo
    private Label       lblPuntos;    // contador de palabras
    private Label       lblErrores;   // contador de errores
    private ProgressBar barraTiempo;  // barra visual del tiempo restante

    private StackPane  raiz;      // contenedor principal, cambia de pantalla
    private Preferences guardado; // para guardar el record entre sesiones

    // Listas de palabras separadas por nivel de dificultad

    private final ArrayList<String> palabrasFacil = new ArrayList<>(Arrays.asList(
            "sol", "mar", "paz", "luz", "rio", "ola", "pie", "pan", "sal", "gas",
            "voz", "ley", "red", "tren", "pez", "mes", "eco", "oro", "feo", "duo",
            "casa", "mesa", "silla", "gato", "perro", "pato", "boca", "mano", "luna",
            "nube", "flor", "agua", "fuego", "libro", "cama", "dado", "lata", "bola",
            "roca", "lago", "pino", "taza", "mapa", "rama", "vela", "pozo", "hacha",
            "lobo", "oso", "toro", "vaca", "rata", "sapo", "caja", "tubo", "hilo",
            "nudo", "peso", "paso", "teja", "pala", "faro", "moto", "prado", "playa",
            "campo", "monte", "bosque", "cielo", "tierra", "arena", "plaza", "calle",
            "puerta", "suelo", "pared", "techo", "reloj", "carta"
    ));

    private final ArrayList<String> palabrasMedio = new ArrayList<>(Arrays.asList(
            "ciudad", "camino", "puente", "piedra", "sombra", "nombre", "tiempo",
            "vuelo", "sueño", "miedo", "color", "forma", "mundo", "viento", "noche",
            "árbol", "jardín", "espejo", "cuadro", "frío", "calor", "guerra", "amor",
            "plato", "vaso", "bolsa", "papel", "banco", "ventana", "lámpara", "estrella",
            "lluvia", "nieve", "montaña", "caballo", "pájaro", "código", "datos",
            "teclado", "pantalla", "lápiz", "pincel", "botón", "cable", "figura",
            "número", "música", "imagen", "viaje", "fuerza", "verdad", "espacio",
            "cuerpo", "mente", "sangre", "fuente", "colina", "aldea", "pueblo",
            "barrio", "fábrica", "escuela", "mercado", "canción", "idioma", "lengua",
            "palabra", "párrafo", "capítulo", "página", "novela", "poesía", "teatro",
            "danza", "pintura", "ciencia", "física", "química", "biología", "historia",
            "geografía", "cultura", "deporte"
    ));

    private final ArrayList<String> palabrasDificil = new ArrayList<>(Arrays.asList(
            "velocidad", "aventura", "distancia", "horizonte", "laberinto", "volcanes",
            "tormenta", "fantasmas", "libertad", "gobierno", "industria", "comercio",
            "naturaleza", "escritura", "conocimiento", "inteligencia", "tecnología",
            "educación", "sociedad", "economía", "medicina", "filosofía", "matemática",
            "experimento", "laboratorio", "microscopio", "telescopio", "universo",
            "galaxia", "planeta", "satélite", "atmósfera", "temperatura", "humedad",
            "arquitectura", "escultura", "literatura", "periodismo", "fotografía",
            "cinematografía", "informática", "programación", "algoritmo", "variable",
            "condición", "estructura", "función", "interfaz", "protocolo", "servidor",
            "montañismo", "submarino", "helicóptero", "ferrocarril", "aeropuerto",
            "institución", "organización", "constitución", "legislación", "jurisdicción",
            "complicado", "dificultad", "obstáculo", "desafío", "rendimiento"
    ));

    private final ArrayList<String> palabrasHardcore = new ArrayList<>(Arrays.asList(
            "electrodoméstico", "extraordinario", "responsabilidad", "incondicional",
            "anticonstitucional", "telecomunicación", "interoperabilidad", "infraestructura",
            "retroalimentación", "administración", "multidisciplinario", "desproporcionado",
            "experimentación", "procedimiento", "caracterización", "implementación",
            "transformación", "representación", "descentralización", "incomprensible",
            "pronunciación", "diferenciación", "profesionalismo", "cuestionamiento",
            "corresponsabilidad", "manifestación", "conmemoración", "estructuración",
            "simultaneidad", "heterogeneidad", "biodiversidad", "sustentabilidad",
            "interdependencia", "internacionalización", "desinformación", "automatización",
            "desestabilización", "contraproducente", "imprescindible", "extraordinariamente",
            "irresponsabilidad", "individualización", "sistematización", "generalización",
            "especialización", "modernización", "institucionalización", "democratización",
            "descomunal", "imperturbable", "indestructible", "incompatibilidad"
    ));

    // Inicio de la aplicación

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        guardado = Preferences.userNodeForPackage(Main.class);

        // Generamos los clips de audio al inicio para evitar delay
        // al reproducirlos durante el juego
        clipBien = crearSonido(880, 100);
        clipMal  = crearSonido(180, 140);

        // El StackPane es el contenedor raíz. Cada pantalla reemplaza
        // su contenido con setAll(), sin necesidad de abrir nuevas ventanas
        raiz = new StackPane();
        raiz.setStyle("-fx-background-color: #f5f6fa;");

        pantallaInicio();

        Scene escena = new Scene(raiz, 680, 540);

        stage.setTitle("Teclazo");
        stage.setScene(escena);
        stage.setMinWidth(520);
        stage.setMinHeight(460);
        stage.show();
    }

    // Pantalla de inicio
    // Muestra el selector de nivel, los records y el botón para jugar
    // Componentes: Label, RadioButton, ToggleGroup, Tooltip,
    //              GridPane, Separator, VBox, HBox, Button

    private void pantallaInicio() {

        Label titulo = new Label("Teclazo");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        titulo.setStyle("-fx-text-fill: #1e2846;");

        Label subtitulo = new Label("Selecciona el nivel y presiona Comenzar");
        subtitulo.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 14px;");

        // RadioButton con ToggleGroup: solo uno puede estar seleccionado a la vez
        ToggleGroup grupoNiveles = new ToggleGroup();
        VBox listaNiveles = new VBox(8);
        listaNiveles.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < NIVELES.length; i++) {
            final int indice = i;

            RadioButton opcion = new RadioButton(NIVELES[i]);
            opcion.setToggleGroup(grupoNiveles);
            opcion.setUserData(indice); // guardamos el índice para leerlo en el listener
            opcion.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
            opcion.setStyle("-fx-text-fill: " + COLOR_NIVEL[i] + ";");

            // Tooltip: cartelito que aparece al pasar el mouse encima
            Tooltip ayuda = new Tooltip(textoNivel(indice));
            ayuda.setStyle("-fx-font-size: 13px;");
            opcion.setTooltip(ayuda);

            if (i == nivel) opcion.setSelected(true);
            listaNiveles.getChildren().add(opcion);
        }

        // Label que describe el nivel seleccionado actualmente
        Label descripcion = new Label(textoNivel(nivel));
        descripcion.setStyle("-fx-text-fill: " + COLOR_NIVEL[nivel] + "; -fx-font-size: 13px;");
        descripcion.setWrapText(true);

        // Cuando cambia el RadioButton seleccionado, leemos el indice desde
        // getUserData() porque al momento de ejecutarse el listener, la variable
        // nivel todavía no fue actualizada
        grupoNiveles.selectedToggleProperty().addListener((obs, anterior, actual) -> {
            if (actual != null) {
                int indice = (int) actual.getUserData();
                nivel = indice;
                descripcion.setText(textoNivel(indice));
                descripcion.setStyle("-fx-text-fill: " + COLOR_NIVEL[indice] + "; -fx-font-size: 13px;");
            }
        });

        VBox cajaNiveles = new VBox(12, listaNiveles, descripcion);
        cajaNiveles.setPadding(new Insets(16, 24, 16, 24));
        cajaNiveles.setStyle(
                "-fx-background-color: #ebedf5;" +
                        "-fx-border-color: #bec3d2;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // Separator: línea horizontal para separar secciones visualmente
        Separator linea = new Separator();
        linea.setStyle("-fx-border-color: #c0c5d5;");

        // GridPane: tabla de records. add(nodo, columna, fila) para posicionar
        Label tituloRecords = new Label("Records");
        tituloRecords.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        tituloRecords.setStyle("-fx-text-fill: #1e2846;");

        GridPane tablaRecords = new GridPane();
        tablaRecords.setHgap(20);
        tablaRecords.setVgap(6);
        tablaRecords.setAlignment(Pos.CENTER);

        for (int i = 0; i < NIVELES.length; i++) {
            int record = guardado.getInt("record_" + NIVELES[i], 0);

            Label nombreNivel = new Label(NIVELES[i] + ":");
            nombreNivel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
            nombreNivel.setStyle("-fx-text-fill: " + COLOR_NIVEL[i] + ";");
            nombreNivel.setMinWidth(90);

            Label valorRecord = new Label(record + " palabras");
            valorRecord.setFont(Font.font("SansSerif", 13));
            valorRecord.setStyle("-fx-text-fill: " + (record > 0 ? "#3c4656" : "#828ca0") + ";");

            tablaRecords.add(nombreNivel, 0, i);
            tablaRecords.add(valorRecord, 1, i);
        }

        VBox cajaRecords = new VBox(10, tituloRecords, tablaRecords);
        cajaRecords.setAlignment(Pos.CENTER);
        cajaRecords.setPadding(new Insets(16, 24, 16, 24));
        cajaRecords.setStyle(
                "-fx-background-color: #ebedf5;" +
                        "-fx-border-color: #bec3d2;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        Button botonJugar = new Button("Comenzar");
        botonJugar.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        botonJugar.setPadding(new Insets(11, 36, 11, 36));
        botonJugar.setStyle(
                "-fx-background-color: #2980db;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        botonJugar.setOnAction(e -> empezarPartida());

        // VBox apila todo verticalmente
        VBox contenedor = new VBox(16, titulo, subtitulo, cajaNiveles, linea, cajaRecords, botonJugar);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(32));

        raiz.getChildren().setAll(contenedor);
    }

    // Pantalla de juego
    // Muestra las stats, la palabra actual y el campo de escritura
    // Componentes: BorderPane, VBox, HBox, Label, TextField, ProgressBar

    private void pantallaJuego() {

        // Stats superiores: tiempo, puntos y errores
        lblTiempo = new Label(tiempo + "s");
        lblTiempo.setFont(Font.font("SansSerif", FontWeight.BOLD, 34));
        lblTiempo.setStyle("-fx-text-fill: #2978c8;");
        Label textoTiempo = new Label("Tiempo");
        textoTiempo.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 12px;");
        VBox cajaTiempo = crearCajita(lblTiempo, textoTiempo, "#e6f2ff", "#2978c8");

        lblPuntos = new Label("0");
        lblPuntos.setFont(Font.font("SansSerif", FontWeight.BOLD, 34));
        lblPuntos.setStyle("-fx-text-fill: #27a05a;");
        Label textoPuntos = new Label("Palabras");
        textoPuntos.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 12px;");
        VBox cajaPuntos = crearCajita(lblPuntos, textoPuntos, "#e6f8ec", "#27a05a");

        lblErrores = new Label("0");
        lblErrores.setFont(Font.font("SansSerif", FontWeight.BOLD, 34));
        lblErrores.setStyle("-fx-text-fill: #c83232;");
        Label textoErrores = new Label("Errores");
        textoErrores.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 12px;");
        VBox cajaErrores = crearCajita(lblErrores, textoErrores, "#ffebeb", "#c83232");

        // Badge con el nivel actual
        Label cartelNivel = new Label(NIVELES[nivel]);
        cartelNivel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        cartelNivel.setPadding(new Insets(4, 12, 4, 12));
        cartelNivel.setStyle(
                "-fx-background-color: " + COLOR_NIVEL[nivel] + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );

        // HBox: pone las cajitas de stats en fila
        HBox filaStats = new HBox(14, cajaTiempo, cajaPuntos, cajaErrores, cartelNivel);
        filaStats.setAlignment(Pos.CENTER);
        filaStats.setPadding(new Insets(16, 24, 8, 24));

        // ProgressBar: barra del tiempo restante. Va de 1.0 a 0.0
        barraTiempo = new ProgressBar(1.0);
        barraTiempo.setMaxWidth(Double.MAX_VALUE);
        barraTiempo.setPrefHeight(10);
        barraTiempo.setStyle("-fx-accent: #2978c8;");

        VBox zonaArriba = new VBox(6, filaStats, barraTiempo);
        VBox.setMargin(barraTiempo, new Insets(0, 24, 0, 24));

        // Palabra en el centro de la pantalla
        lblPalabra = new Label(palabra);
        lblPalabra.setFont(Font.font("Monospaced", FontWeight.BOLD, 44));
        lblPalabra.setStyle("-fx-text-fill: #1e2846;");

        VBox cajaPalabra = new VBox(lblPalabra);
        cajaPalabra.setAlignment(Pos.CENTER);
        cajaPalabra.setPadding(new Insets(28, 48, 28, 48));
        cajaPalabra.setStyle(
                "-fx-background-color: #ebedf5;" +
                        "-fx-border-color: #bec3d2;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        // Campo de escritura
        txtEscribir = new TextField();
        txtEscribir.setFont(Font.font("Monospaced", 22));
        txtEscribir.setPromptText("escribi la palabra...");
        txtEscribir.setPrefHeight(48);
        pintarCampo("normal");

        // El listener se dispara cada vez que cambia el texto del campo.
        // Ahi revisamos si lo que se escribió hasta ahora es correcto o no
        txtEscribir.textProperty().addListener((obs, antes, ahora) -> {
            if (!jugando || ahora.isEmpty()) {
                pintarCampo("normal");
                return;
            }
            revisarLetras(ahora, antes);
        });

        Label indicacion = new Label("Escribi la palabra completa para continuar");
        indicacion.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 12px;");

        VBox zonaAbajo = new VBox(8, txtEscribir, indicacion);
        zonaAbajo.setPadding(new Insets(8, 24, 20, 24));

        // BorderPane organiza la pantalla en tres secciones:
        // top = stats, center = palabra, bottom = campo de texto
        BorderPane pantalla = new BorderPane();
        pantalla.setStyle("-fx-background-color: #f5f6fa;");
        pantalla.setTop(zonaArriba);
        pantalla.setCenter(cajaPalabra);
        pantalla.setBottom(zonaAbajo);
        BorderPane.setAlignment(cajaPalabra, Pos.CENTER);
        BorderPane.setMargin(cajaPalabra, new Insets(20, 48, 20, 48));

        raiz.getChildren().setAll(pantalla);
        txtEscribir.requestFocus();
    }

    // Pantalla de fin de partida
    // Muestra el puntaje, los errores y si se supero el record

    private void pantallaFin(boolean perdioPorError) {

        String  claveRecord   = "record_" + NIVELES[nivel];
        int     recordAnterior = guardado.getInt(claveRecord, 0);
        boolean esRecord      = puntos > recordAnterior;
        if (esRecord) guardado.putInt(claveRecord, puntos);

        Label mensaje = new Label(perdioPorError ? "Te equivocaste — Partida terminada" : "Se acabo el tiempo");
        mensaje.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        mensaje.setStyle("-fx-text-fill: " + (perdioPorError ? "#c83232" : "#1e2846") + ";");

        Label puntajeFinal = new Label(String.valueOf(puntos));
        puntajeFinal.setFont(Font.font("SansSerif", FontWeight.BOLD, 72));
        puntajeFinal.setStyle("-fx-text-fill: " + COLOR_NIVEL[nivel] + ";");

        Label subtextoPuntaje = new Label("palabras completadas");
        subtextoPuntaje.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 15px;");

        Label detalleErrores = new Label("Errores cometidos: " + errores);
        detalleErrores.setStyle("-fx-text-fill: " + (errores > 0 ? "#c83232" : "#27a05a") + "; -fx-font-size: 14px;");

        int    recordActual = guardado.getInt(claveRecord, 0);
        String textoRecord  = esRecord
                ? "Nuevo record en " + NIVELES[nivel] + "!"
                : "Record en " + NIVELES[nivel] + ": " + recordActual + " palabras";
        Label labelRecord = new Label(textoRecord);
        labelRecord.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        labelRecord.setStyle("-fx-text-fill: " + (esRecord ? "#d4700a" : "#828ca0") + ";");

        Separator linea = new Separator();

        Button botonVolver = new Button("Volver al inicio");
        botonVolver.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        botonVolver.setPadding(new Insets(10, 28, 10, 28));
        botonVolver.setStyle(
                "-fx-background-color: #2980db;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        botonVolver.setOnAction(e -> pantallaInicio());

        VBox contenedor = new VBox(14,
                mensaje, puntajeFinal, subtextoPuntaje,
                detalleErrores, linea, labelRecord, botonVolver
        );
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(36));

        raiz.getChildren().setAll(contenedor);
    }

    // Lógica del juego

    private void empezarPartida() {
        tiempo  = TIEMPO_INICIAL;
        puntos  = 0;
        errores = 0;
        jugando = true;
        palabra = palabraRandom();

        pantallaJuego();
        arrancarCronometro();
    }

    // Timeline con un KeyFrame de 1 segundo: baja el tiempo, actualiza
    // la barra y termina el juego cuando llega a cero
    private void arrancarCronometro() {
        if (cronometro != null) cronometro.stop();

        KeyFrame tick = new KeyFrame(Duration.seconds(1), e -> {
            tiempo--;
            lblTiempo.setText(tiempo + "s");

            // Actualizamos la barra de tiempo
            double porcentaje = (double) tiempo / TIEMPO_INICIAL;
            barraTiempo.setProgress(Math.max(0, porcentaje));

            // Cambiamos el color según la urgencia
            if (tiempo <= 2) {
                lblTiempo.setStyle("-fx-text-fill: #c83232;");
                barraTiempo.setStyle("-fx-accent: #c83232;");
            } else if (tiempo <= 5) {
                lblTiempo.setStyle("-fx-text-fill: #d4700a;");
                barraTiempo.setStyle("-fx-accent: #d4700a;");
            }

            if (tiempo <= 0) terminarPartida(false);
        });

        cronometro = new Timeline(tick);
        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
    }

    // Compara lo que se está escribiendo con la palabra actual.
    // Solo cuenta el error cuando se agrega una letra nueva, no al borrar
    private void revisarLetras(String ahora, String antes) {
        boolean escribioLetraNueva = ahora.length() > antes.length();

        if (escribioLetraNueva) {
            int pos = ahora.length() - 1;
            if (pos < palabra.length()) {
                char esperada = palabra.charAt(pos);
                char escrita  = ahora.charAt(pos);
                if (escrita != esperada) {
                    errores++;
                    lblErrores.setText(String.valueOf(errores));
                    reproducir(clipMal);

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

        // Pintamos el campo según si lo escrito hasta aca está bien o mal
        boolean hayError = false;
        for (int i = 0; i < ahora.length(); i++) {
            if (i >= palabra.length() || ahora.charAt(i) != palabra.charAt(i)) {
                hayError = true;
                break;
            }
        }
        pintarCampo(hayError ? "mal" : "bien");
    }

    private void palabraCompletada() {
        puntos++;
        lblPuntos.setText(String.valueOf(puntos));
        reproducir(clipBien);

        // Sumamos el tiempo correspondiente al nivel y reseteamos los colores
        tiempo += SEGUNDOS_EXTRA[nivel];
        lblTiempo.setText(tiempo + "s");
        lblTiempo.setStyle("-fx-text-fill: #2978c8;");
        barraTiempo.setStyle("-fx-accent: #2978c8;");
        barraTiempo.setProgress(Math.min(1.0, (double) tiempo / TIEMPO_INICIAL));

        String siguiente;
        do { siguiente = palabraRandom(); } while (siguiente.equals(palabra));
        palabra = siguiente;
        lblPalabra.setText(palabra);

        // Usamos runLater porque no podemos modificar el TextField directamente
        // desde su propio listener, eso tira una excepción en JavaFX
        Platform.runLater(() -> {
            txtEscribir.clear();
            pintarCampo("normal");
        });
    }

    private void terminarPartida(boolean porError) {
        jugando = false;
        if (cronometro != null) cronometro.stop();
        sonidoFinal();
        pantallaFin(porError);
    }

    // Métodos auxiliares

    // Arma una cajita con un número grande arriba y un texto descriptivo abajo
    private VBox crearCajita(Label numero, Label texto, String colorFondo, String colorBorde) {
        VBox caja = new VBox(3, numero, texto);
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(12, 20, 12, 20));
        caja.setStyle(
                "-fx-background-color: " + colorFondo + ";" +
                        "-fx-border-color: " + colorBorde + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        return caja;
    }

    // Cambia el color del campo de texto según el estado de escritura:
    // "normal" = blanco, "bien" = verde, "mal" = rojo
    private void pintarCampo(String estado) {
        if (txtEscribir == null) return;
        String estilo = switch (estado) {
            case "mal"  -> "-fx-background-color: #ffdada; -fx-border-color: #c83232;" +
                    "-fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;" +
                    "-fx-font-family: Monospaced; -fx-font-size: 22px;";
            case "bien" -> "-fx-background-color: #daf5e6; -fx-border-color: #27a05a;" +
                    "-fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;" +
                    "-fx-font-family: Monospaced; -fx-font-size: 22px;";
            default     -> "-fx-background-color: white; -fx-border-color: #b4b9c8;" +
                    "-fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;" +
                    "-fx-font-family: Monospaced; -fx-font-size: 22px;";
        };
        txtEscribir.setStyle(estilo);
    }

    // Devuelve una palabra al azar de la lista correspondiente al nivel
    private String palabraRandom() {
        ArrayList<String> lista = switch (nivel) {
            case 1  -> palabrasMedio;
            case 2  -> palabrasDificil;
            case 3  -> palabrasHardcore;
            default -> palabrasFacil;
        };
        return lista.get(new Random().nextInt(lista.size()));
    }

    // Descripción corta de cada nivel, para el Tooltip y la pantalla de inicio
    private String textoNivel(int i) {
        return switch (i) {
            case 0 -> "Fácil: cada palabra suma 5 segundos";
            case 1 -> "Medio: cada palabra suma 3 segundos";
            case 2 -> "Difícil: cada palabra suma 1 segundo";
            case 3 -> "Hardcore: 1 segundo por palabra, un error termina la partida";
            default -> "";
        };
    }

    // Sonido:
    // Generamos los tonos matemáticamente con la función seno,
    // sin usar archivos de audio externos

    // Crea un clip de audio con la frecuencia y duración indicadas.
    // La onda sinusoidal genera el tono, y el fade evita el "clic" al cortar
    private Clip crearSonido(int frecuencia, int duracionMs) {
        try {
            float muestrasPorSeg = 44100f;
            AudioFormat formato  = new AudioFormat(muestrasPorSeg, 16, 1, true, false);
            int total   = (int) (muestrasPorSeg * duracionMs / 1000);
            byte[] datos = new byte[total * 2];

            for (int i = 0; i < total; i++) {
                double t    = i / muestrasPorSeg;
                double onda = Math.sin(2 * Math.PI * frecuencia * t);
                double fade = 1.0 - ((double) i / total);
                short  val  = (short) (onda * fade * 28000);
                datos[i * 2]     = (byte) (val & 0xFF);
                datos[i * 2 + 1] = (byte) ((val >> 8) & 0xFF);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(formato, datos, 0, datos.length);
            return clip;
        } catch (LineUnavailableException e) {
            System.err.println("Audio no disponible: " + e.getMessage());
            return null;
        }
    }

    // Rebobina el clip y lo reproduce en un hilo separado para no
    // bloquear la interfaz mientras suena
    private void reproducir(Clip clip) {
        if (clip == null) return;
        new Thread(() -> {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }).start();
    }

    // Tres tonos descendentes al terminar la partida
    private void sonidoFinal() {
        new Thread(() -> {
            int[] tonos = {440, 330, 220};
            for (int hz : tonos) {
                Clip c = crearSonido(hz, 130);
                if (c != null) {
                    c.start();
                    try { Thread.sleep(180); } catch (InterruptedException ignored) {}
                    c.close();
                }
            }
        }).start();
    }
}