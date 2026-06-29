package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*
 * PantallaFin.java
 *
 * Construye el layout de la pantalla de fin de partida.
 * Muestra el puntaje, los errores cometidos, si se superó el record,
 * y un botón para volver al inicio.
 */

public class PantallaFin {

    private final Juego juego;

    public PantallaFin(Juego juego) {
        this.juego = juego;
    }

    public VBox construir(boolean perdioPorError) {

        Label mensaje = new Label(perdioPorError
                ? "Te equivocaste — Partida terminada"
                : "Se acabo el tiempo");
        mensaje.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        mensaje.setStyle("-fx-text-fill: " + (perdioPorError ? "#c83232" : "#1e2846") + ";");

        Label puntajeFinal = new Label(String.valueOf(juego.getPuntos()));
        puntajeFinal.setFont(Font.font("SansSerif", FontWeight.BOLD, 72));
        puntajeFinal.setStyle("-fx-text-fill: " + Juego.COLOR_NIVEL[juego.getNivel()] + ";");

        Label subtextoPuntaje = new Label("palabras completadas");
        subtextoPuntaje.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 15px;");

        Label detalleErrores = new Label("Errores cometidos: " + juego.getErrores());
        detalleErrores.setStyle(
                "-fx-text-fill: " + (juego.getErrores() > 0 ? "#c83232" : "#27a05a") + ";" +
                        "-fx-font-size: 14px;"
        );

        // Comparamos con el record guardado para mostrar si se superó
        int    recordActual = juego.getRecord(juego.getNivel());
        boolean esRecord    = juego.getPuntos() >= recordActual && juego.getPuntos() > 0;
        String textoRecord  = esRecord
                ? "Nuevo record en " + Juego.NIVELES[juego.getNivel()] + "!"
                : "Record en " + Juego.NIVELES[juego.getNivel()] + ": " + recordActual + " palabras";
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
        botonVolver.setOnAction(e -> juego.mostrarInicio());

        VBox contenedor = new VBox(14,
                mensaje, puntajeFinal, subtextoPuntaje,
                detalleErrores, linea, labelRecord, botonVolver
        );
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(36));

        return contenedor;
    }
}