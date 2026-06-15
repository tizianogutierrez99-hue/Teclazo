# Teclazo

## Descripción

Teclazo es un juego de mecanografía desarrollado en Java utilizando JavaFX y Maven.

El objetivo del juego es escribir correctamente las palabras mostradas en pantalla antes de que se agote el tiempo. Cada palabra completada suma segundos al cronómetro según el nivel de dificultad seleccionado.

El juego incluye diferentes niveles de dificultad, sistema de puntuación, registro de errores, almacenamiento de récords y efectos de sonido generados mediante código.

## Características

- Cuatro niveles de dificultad:
  - Fácil
  - Medio
  - Difícil
  - Hardcore

- Sistema de puntuación basado en palabras completadas.
- Contador de errores.
- Récords guardados entre sesiones mediante Preferences.
- Barra de progreso para visualizar el tiempo restante.
- Efectos de sonido generados dinámicamente.
- Interfaz gráfica desarrollada con JavaFX.

## Tecnologías utilizadas

- Java 21
- JavaFX 21
- Maven
- IntelliJ IDEA

## Componentes JavaFX utilizados

- Stage
- Scene
- StackPane
- BorderPane
- GridPane
- VBox
- HBox
- Label
- TextField
- Button
- RadioButton
- ToggleGroup
- Tooltip
- ProgressBar
- Separator
- Timeline
- KeyFrame

## Requisitos

- Java 21
- Maven

## Ejecución

Abrir el proyecto en IntelliJ IDEA y ejecutar la tarea:

Maven → Plugins → javafx → javafx:run

Alternativamente, desde una terminal ubicada en la carpeta raíz del proyecto:

```bash
mvn javafx:run
```

## Integrantes

- Tiziano Gutierrez
- Jeremias Castro
- Ian Freccero
- Gino Camilletti

## Materia

Desarrollo de Sistemas Orientados a Objetos