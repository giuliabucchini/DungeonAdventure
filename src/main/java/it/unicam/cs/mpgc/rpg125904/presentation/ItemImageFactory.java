
package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Si occupa di individuare e caricare le immagini degli oggetti.
 */
public class ItemImageFactory {

    public ImageView create(String itemName, double size) {
        String path = getImagePath(itemName);

        if (path == null) {
            return null;
        }

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

            ImageView view = new ImageView(image);
            view.setFitWidth(size);
            view.setFitHeight(size);
            view.setPreserveRatio(true);

            return view;

        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String getImagePath(String itemName) {
        String normalized = itemName.toLowerCase().trim();

        if (normalized.contains("scheggia")
                || normalized.contains("frammento")) {
            return "/images/items/cristallo_blu senza sfondo.png";

        } else if (normalized.contains("tavoletta")) {
            return "/images/items/tavoletta_geroglifici senza sfondo.png";

        } else if (normalized.contains("pergamena")) {
            return "/images/items/pergamena senza sfondo.png";

        } else if (normalized.contains("anfora")) {
            return "/images/items/anfora senza sfondo.png";

        } else if (normalized.contains("scarabeo")) {
            return "/images/items/scarabeo_dorato senza sfondo.png";

        } else if (normalized.contains("chiave")) {
            return "/images/items/chiave_antica senza sfondo.png";

        } else if (normalized.contains("corda")) {
            return "/images/items/corda senza sfondo.png";

        } else if (normalized.contains("lanterna")) {
            return "/images/items/lanterna senza sfondo.png";

        } else if (normalized.contains("amuleto")) {
            return "/images/items/amuleto_compass senza sfondo.png";

        } else if (normalized.contains("ankh")) {
            return "/images/items/ankh senza sfondo.png";

        } else if (normalized.contains("pugnale")) {
            return "/images/items/pugnale senza sfondo.png";
        }

        return null;
    }
}
