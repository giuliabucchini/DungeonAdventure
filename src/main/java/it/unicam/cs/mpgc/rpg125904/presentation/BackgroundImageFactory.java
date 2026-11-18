
package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.scene.image.Image;

import java.io.InputStream;

public class BackgroundImageFactory {

    /**
     * Carica un'immagine di sfondo dal percorso indicato.
     *
     * @param path percorso della risorsa
     * @return immagine caricata oppure null in caso di errore
     */
    public Image load(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {

            if (stream == null) {
                System.err.println("Immagine non trovata: " + path);
                return null;
            }

            Image image = new Image(stream);

            if (image.isError()) {
                System.err.println("Errore nel caricamento: " + path);
                return null;
            }

            return image;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
