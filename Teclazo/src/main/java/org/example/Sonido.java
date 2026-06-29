package org.example;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/*
 * Sonido.java
 *
 * Genera y reproduce los efectos de sonido del juego.
 * Los tonos se crean matemáticamente con la función seno,
 * sin usar ningún archivo de audio externo.
 *
 * Los clips se pre-generan al crear el objeto para que no haya
 * delay al reproducirlos durante la partida.
 */

public class Sonido {

    private final Clip clipBien;  // sonido al completar una palabra
    private final Clip clipMal;   // sonido al errar una tecla

    public Sonido() {
        clipBien = crearClip(880, 100);  // tono agudo = acierto
        clipMal  = crearClip(180, 140);  // tono grave = error
    }

    public void bien() {
        reproducir(clipBien);
    }

    public void mal() {
        reproducir(clipMal);
    }

    // Tres tonos descendentes para cuando termina la partida
    public void fin() {
        new Thread(() -> {
            int[] tonos = {440, 330, 220};
            for (int hz : tonos) {
                Clip c = crearClip(hz, 130);
                if (c != null) {
                    c.start();
                    try { Thread.sleep(180); } catch (InterruptedException ignored) {}
                    c.close();
                }
            }
        }).start();
    }

    /*
     * Crea un clip de audio generando una onda sinusoidal en memoria.
     * La formula es: valor(t) = sin(2π × frecuencia × t)
     * El "fade" hace que el volumen baje gradualmente al final para
     * evitar el clic molesto que aparece al cortar el sonido de golpe.
     */

    private Clip crearClip(int frecuencia, int duracionMs) {
        try {
            float muestrasPorSeg = 44100f;
            AudioFormat formato  = new AudioFormat(muestrasPorSeg, 16, 1, true, false);
            int total            = (int) (muestrasPorSeg * duracionMs / 1000);
            byte[] datos         = new byte[total * 2];

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
}