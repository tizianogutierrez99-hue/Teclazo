package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
 * Main.java
 *
 * Punto de entrada de la aplicación. Su único trabajo es armar
 * la ventana y pasarle el control a Juego.java.
 */

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // El StackPane es el contenedor raíz. Las pantallas se van
        // reemplazando dentro de él sin abrir ventanas nuevas
        StackPane raiz = new StackPane();
        raiz.setStyle("-fx-background-color: #f5f6fa;");

        // Juego es el cerebro: recibe el raiz para poder cambiar de pantalla
        Juego juego = new Juego(raiz);
        juego.mostrarInicio();

        Scene escena = new Scene(raiz, 680, 540);

        stage.setTitle("Teclazo");
        stage.setScene(escena);
        stage.setMinWidth(520);
        stage.setMinHeight(460);
        stage.show();
    }
}