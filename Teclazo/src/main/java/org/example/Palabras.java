package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
 * Palabras.java
 *
 * Guarda las listas de palabras separadas por nivel y devuelve
 * una al azar según el nivel que le pidan.
 *
 * Si se quiere agregar o cambiar palabras, se modifica desde este archivo.
 */

public class Palabras {

    private static final Random rng = new Random();

    private static final ArrayList<String> FACIL = new ArrayList<>(Arrays.asList(
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

    private static final ArrayList<String> MEDIO = new ArrayList<>(Arrays.asList(
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

    private static final ArrayList<String> DIFICIL = new ArrayList<>(Arrays.asList(
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

    private static final ArrayList<String> HARDCORE = new ArrayList<>(Arrays.asList(
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

    /*
     * Devuelve una palabra al azar del nivel indicado.
     * El parámetro "actual" sirve para evitar repetir la misma palabra dos veces seguidas.
     */

    public static String getRandom(int nivel, String actual) {
        ArrayList<String> lista = switch (nivel) {
            case 1  -> MEDIO;
            case 2  -> DIFICIL;
            case 3  -> HARDCORE;
            default -> FACIL;
        };

        String siguiente;
        do {
            siguiente = lista.get(rng.nextInt(lista.size()));
        } while (siguiente.equals(actual) && lista.size() > 1);

        return siguiente;
    }
}