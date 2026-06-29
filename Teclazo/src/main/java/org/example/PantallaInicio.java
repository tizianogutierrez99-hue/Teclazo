package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/*
 * PantallaInicio.java
 *
 * Construye y devuelve el layout de la pantalla de inicio.
 * Contiene el título, el selector de nivel, los records y el botón para arrancar.
 */

public class PantallaInicio {

    private final Juego juego;

    public PantallaInicio(Juego juego) {
        this.juego = juego;
    }

    public VBox construir() {

        Label titulo = new Label("Teclazo");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        titulo.setStyle("-fx-text-fill: #1e2846;");

        Label subtitulo = new Label("Selecciona el nivel y presiona Comenzar");
        subtitulo.setStyle("-fx-text-fill: #828ca0; -fx-font-size: 14px;");

        // RadioButton con ToggleGroup: solo uno puede estar seleccionado a la vez
        ToggleGroup grupoNiveles = new ToggleGroup();
        VBox listaNiveles = new VBox(8);
        listaNiveles.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < Juego.NIVELES.length; i++) {
            final int indice = i;

            RadioButton opcion = new RadioButton(Juego.NIVELES[i]);
            opcion.setToggleGroup(grupoNiveles);
            opcion.setUserData(indice);
            opcion.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
            opcion.setStyle("-fx-text-fill: " + Juego.COLOR_NIVEL[i] + ";");

            // Tooltip: cartelito que aparece al pasar el mouse encima
            Tooltip ayuda = new Tooltip(juego.textoNivel(indice));
            ayuda.setStyle("-fx-font-size: 13px;");
            opcion.setTooltip(ayuda);

            if (i == juego.getNivel()) opcion.setSelected(true);
            listaNiveles.getChildren().add(opcion);
        }

        // Label que describe el nivel seleccionado actualmente
        Label descripcion = new Label(juego.textoNivel(juego.getNivel()));
        descripcion.setStyle(
                "-fx-text-fill: " + Juego.COLOR_NIVEL[juego.getNivel()] + ";" +
                        "-fx-font-size: 13px;"
        );
        descripcion.setWrapText(true);

        // Cuando cambia el RadioButton, leemos el índice desde getUserData()
        // porque al momento de ejecutarse el listener, la variable nivel
        // todavía no fue actualizada

        grupoNiveles.selectedToggleProperty().addListener((obs, anterior, actual) -> {
            if (actual != null) {
                int indice = (int) actual.getUserData();
                juego.setNivel(indice);
                descripcion.setText(juego.textoNivel(indice));
                descripcion.setStyle(
                        "-fx-text-fill: " + Juego.COLOR_NIVEL[indice] + ";" +
                                "-fx-font-size: 13px;"
                );
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

        for (int i = 0; i < Juego.NIVELES.length; i++) {
            int record = juego.getRecord(i);

            Label nombreNivel = new Label(Juego.NIVELES[i] + ":");
            nombreNivel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
            nombreNivel.setStyle("-fx-text-fill: " + Juego.COLOR_NIVEL[i] + ";");
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
        botonJugar.setOnAction(e -> juego.empezarPartida());

        // VBox apila todo verticalmente
        VBox contenedor = new VBox(16, titulo, subtitulo, cajaNiveles, linea, cajaRecords, botonJugar);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(32));

        return contenedor;
    }
}