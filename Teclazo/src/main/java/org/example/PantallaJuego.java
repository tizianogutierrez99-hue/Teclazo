package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*
 * PantallaJuego.java
 *
 * Construye el layout de la pantalla de juego y expone métodos
 * para que Juego.java pueda actualizar los labels en tiempo real.
 */

public class PantallaJuego {

    private final Juego juego;

    // Referencias a los nodos que se modifican durante la partida
    private Label       lblPalabra;
    private TextField   txtEscribir;
    private Label       lblTiempo;
    private Label       lblPuntos;
    private Label       lblErrores;
    private ProgressBar barraTiempo;

    public PantallaJuego(Juego juego) {
        this.juego = juego;
    }

    public BorderPane construir() {

        // Stats superiores: tiempo, puntos y errores
        lblTiempo = new Label(juego.getTiempo() + "s");
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
        Label cartelNivel = new Label(Juego.NIVELES[juego.getNivel()]);
        cartelNivel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        cartelNivel.setPadding(new Insets(4, 12, 4, 12));
        cartelNivel.setStyle(
                "-fx-background-color: " + Juego.COLOR_NIVEL[juego.getNivel()] + ";" +
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
        lblPalabra = new Label(juego.getPalabra());
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
        // Cuando hay un cambio, le avisamos a Juego para que lo evalúe
        txtEscribir.textProperty().addListener((obs, antes, ahora) -> {
            if (!juego.isJugando() || ahora.isEmpty()) {
                pintarCampo("normal");
                return;
            }
            juego.revisarLetras(ahora, antes);
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

        return pantalla;
    }

    // Métodos de actualización: Juego.java los llama para reflejar
    // los cambios de estado en la interfaz

    // Actualiza el tiempo y ajusta el color y la barra según la urgencia
    public void actualizarTiempo(int tiempo) {
        lblTiempo.setText(tiempo + "s");

        double porcentaje = (double) tiempo / Juego.TIEMPO_INICIAL;
        barraTiempo.setProgress(Math.max(0, Math.min(1.0, porcentaje)));

        if (tiempo <= 2) {
            lblTiempo.setStyle("-fx-text-fill: #c83232;");
            barraTiempo.setStyle("-fx-accent: #c83232;");
        } else if (tiempo <= 5) {
            lblTiempo.setStyle("-fx-text-fill: #d4700a;");
            barraTiempo.setStyle("-fx-accent: #d4700a;");
        } else {
            lblTiempo.setStyle("-fx-text-fill: #2978c8;");
            barraTiempo.setStyle("-fx-accent: #2978c8;");
        }
    }

    public void actualizarPuntos(int puntos) {
        lblPuntos.setText(String.valueOf(puntos));
    }

    public void actualizarErrores(int errores) {
        lblErrores.setText(String.valueOf(errores));
    }

    public void actualizarPalabra(String nuevaPalabra) {
        lblPalabra.setText(nuevaPalabra);
    }

    public void limpiarCampo() {
        txtEscribir.clear();
        pintarCampo("normal");
    }

    public void enfocarCampo() {
        if (txtEscribir != null) txtEscribir.requestFocus();
    }

    // Cambia el color del campo de texto según el estado de escritura:
    // "normal" = blanco, "bien" = verde, "mal" = rojo
    public void pintarCampo(String estado) {
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
}