
package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Gestisce la visualizzazione della scena di gioco:
 * sfondo, proporzioni e livello degli hotspot.
 */
public class SceneView {

    private static final double SCENE_RATIO = 16.0 / 9.0;

    private final StackPane sceneArea;
    private final ImageView background;
    private final Pane hotspotLayer;

    public SceneView() {
        sceneArea = new StackPane();

        background = new ImageView();
        background.setPreserveRatio(false);
        background.setSmooth(true);
        background.setMouseTransparent(true);

        hotspotLayer = new Pane();
        hotspotLayer.setPickOnBounds(false);

        sceneArea.getChildren().addAll(background, hotspotLayer);

        sceneArea.widthProperty().addListener(
                (observable, oldValue, newValue) -> updateContentSize()
        );

        sceneArea.heightProperty().addListener(
                (observable, oldValue, newValue) -> updateContentSize()
        );
    }

    public StackPane getView() {
        return sceneArea;
    }

    public Pane getHotspotLayer() {
        return hotspotLayer;
    }

    public void setBackground(Image image) {
        background.setImage(image);
        updateContentSize();
    }

    public void clearHotspots() {
        hotspotLayer.getChildren().clear();
    }

    /**
     * Mantiene la scena in formato 16:9.
     * Le dimensioni vengono calcolate in base allo spazio disponibile.
     */
    public void updateSceneSize(double availableWidth, double availableHeight) {
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        double width = Math.min(
                availableWidth,
                availableHeight * SCENE_RATIO
        );

        double height = width / SCENE_RATIO;

        sceneArea.setPrefSize(width, height);
        sceneArea.setMinSize(width, height);
        sceneArea.setMaxSize(width, height);

        updateContentSize();
    }

    private void updateContentSize() {
        double width = sceneArea.getWidth();
        double height = sceneArea.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        background.setFitWidth(width);
        background.setFitHeight(height);

        hotspotLayer.setPrefSize(width, height);
        hotspotLayer.setMinSize(width, height);
        hotspotLayer.setMaxSize(width, height);

        updateBackgroundViewport();
    }

    /**
     * Ritaglia lo sfondo al centro per mantenere la visualizzazione 16:9.
     */
    private void updateBackgroundViewport() {
        Image image = background.getImage();

        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return;
        }

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double imageRatio = imageWidth / imageHeight;

        if (imageRatio > SCENE_RATIO) {
            double croppedWidth = imageHeight * SCENE_RATIO;
            double x = (imageWidth - croppedWidth) / 2.0;

            background.setViewport(
                    new Rectangle2D(x, 0, croppedWidth, imageHeight)
            );
        } else {
            double croppedHeight = imageWidth / SCENE_RATIO;
            double y = (imageHeight - croppedHeight) / 2.0;

            background.setViewport(
                    new Rectangle2D(0, y, imageWidth, croppedHeight)
            );
        }
    }
}
