package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class GameImageFactory {

    public void loadBackground(
            String path,
            ImageView background,
            Consumer<String> statusUpdater
    ) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                background.setImage(null);
                statusUpdater.accept("Immagine non trovata: " + path);
                return;
            }

            Image image = new Image(stream);
            if (image.isError()) {
                background.setImage(null);
                statusUpdater.accept("Errore nel caricamento dell'immagine.");
                return;
            }

            background.setImage(image);
            background.setViewport(createWideViewport(image));

        } catch (IOException exception) {
            statusUpdater.accept("Errore nel caricamento dell'ambiente.");
            exception.printStackTrace();
        }
    }

    private Rectangle2D createWideViewport(Image image) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double targetRatio = 16.0 / 9.0;
        double imageRatio = imageWidth / imageHeight;

        if (imageRatio > targetRatio) {
            double croppedWidth = imageHeight * targetRatio;
            double x = (imageWidth - croppedWidth) / 2.0;
            return new Rectangle2D(x, 0, croppedWidth, imageHeight);
        }

        double croppedHeight = imageWidth / targetRatio;
        double y = (imageHeight - croppedHeight) / 2.0;
        return new Rectangle2D(0, y, imageWidth, croppedHeight);
    }

    public Image loadImage(String resourcePath) {
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                System.err.println("Immagine non trovata: " + resourcePath);
                return null;
            }
            return new Image(stream);
        } catch (IOException exception) {
            System.err.println("Errore nel caricamento: " + resourcePath);
            return null;
        }
    }

    public String getBackgroundPath(int roomIndex) {
        return switch (roomIndex) {
            case 0 -> "/images/rooms/entrance.png";
            case 1 -> "/images/rooms/02_archivio_iscrizioni.png";
            case 2 -> "/images/rooms/puzzle.jpeg";
            case 3 -> "/images/rooms/04_camera_segreta_blu.png";
            case 4 -> "/images/rooms/sanctuary.jpeg";
            default -> "/images/rooms/entrance.png";
        };
    }

    public ImageView createItemImage(String itemName, double size) {
        String normalized = itemName.toLowerCase().trim();
        String path = null;

        if (normalized.contains("scheggia") || normalized.contains("frammento")) {
            path = "/images/items/cristallo_blu.png";
        } else if (normalized.contains("tavoletta")) {
            path = "/images/items/tavoletta_geroglifici.png";
        } else if (normalized.contains("pergamena")) {
            path = "/images/items/pergamena.png";
        } else if (normalized.contains("anfora")) {
            path = "/images/items/anfora.png";
        } else if (normalized.contains("scarabeo")) {
            path = "/images/items/scarabeo_dorato.png";
        } else if (normalized.contains("chiave")) {
            path = "/images/items/chiave_antica.png";
        } else if (normalized.contains("corda")) {
            path = "/images/items/corda.png";
        } else if (normalized.contains("lanterna")) {
            path = "/images/items/lanterna.png";
        } else if (normalized.contains("ankh")) {
            path = "/images/items/ankh.png";
        } else if (normalized.contains("pugnale")) {
            path = "/images/items/pugnale.png";
        }

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
}