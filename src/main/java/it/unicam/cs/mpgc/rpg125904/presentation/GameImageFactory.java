
package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Responsabile del caricamento delle immagini
 * utilizzate nell'interfaccia di gioco.
 */
public class GameImageFactory {

    public Image loadImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            throw new IllegalArgumentException(
                    "Immagine non trovata: " + path
            );
        }

        return new Image(stream);
    }

    public ImageView createItemImage(String itemName, double size) {
        String path = getItemImagePath(itemName);

        if (path == null) {
            return null;
        }

        ImageView imageView = new ImageView(loadImage(path));
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        return imageView;
    }

    public String getItemImagePath(String itemName) {
        if (itemName == null) {
            return null;
        }

        String name = itemName.toLowerCase().trim();

        if (name.contains("scheggia") || name.contains("frammento")) {
            return "/images/items/cristallo_blu senza sfondo.png";
        }

        if (name.contains("tavoletta")) {
            return "/images/items/tavoletta_geroglifici senza sfondo.png";
        }

        if (name.contains("pergamena")) {
            return "/images/items/pergamena senza sfondo.png";
        }

        if (name.contains("anfora")) {
            return "/images/items/anfora senza sfondo.png";
        }

        if (name.contains("scarabeo")) {
            return "/images/items/scarabeo_dorato senza sfondo.png";
        }

        if (name.contains("chiave")) {
            return "/images/items/chiave_antica senza sfondo.png";
        }

        if (name.contains("corda")) {
            return "/images/items/corda senza sfondo.png";
        }

        if (name.contains("lanterna")) {
            return "/images/items/lanterna senza sfondo.png";
        }

        if (name.contains("amuleto")) {
            return "/images/items/amuleto_compass senza sfondo.png";
        }

        if (name.contains("ankh")) {
            return "/images/items/ankh senza sfondo.png";
        }

        if (name.contains("pugnale")) {
            return "/images/items/pugnale senza sfondo.png";
        }

        return null;
    }
}
