
package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;

/**
 * Gestisce la creazione e il posizionamento degli hotspot.
 */
public class HotspotManager {

    private final List<HotspotView> activeHotspots = new ArrayList<>();

    /**
     * Crea un marcatore luminoso animato.
     */
    public StackPane createGlowingMarker(double size) {
        StackPane marker = new StackPane();

        marker.setPrefSize(size, size);
        marker.setMinSize(size, size);
        marker.setMaxSize(size, size);
        marker.setCursor(javafx.scene.Cursor.HAND);

        Circle halo = new Circle(size * 0.40);

        halo.setFill(new RadialGradient(
                0,
                0,
                0.5,
                0.5,
                0.5,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 220, 145, 0.65)),
                new Stop(0.45, Color.rgb(230, 189, 112, 0.25)),
                new Stop(1, Color.TRANSPARENT)
        ));

        halo.setEffect(new GaussianBlur(size * 0.13));

        Circle outerRing = new Circle(9);
        outerRing.setFill(Color.rgb(35, 25, 14, 0.75));
        outerRing.setStroke(Color.rgb(255, 218, 145, 0.95));
        outerRing.setStrokeWidth(1.5);

        Circle center = new Circle(3);
        center.setFill(Color.rgb(255, 235, 190, 1));

        marker.getChildren().addAll(halo, outerRing, center);

        FadeTransition pulse = new FadeTransition(
                Duration.seconds(2.2),
                halo
        );

        pulse.setFromValue(0.35);
        pulse.setToValue(1.0);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        return marker;
    }

    /**
     * Registra un nodo interattivo e le sue coordinate relative.
     */
    public void register(
            Node node,
            double x,
            double y,
            double width,
            double height
    ) {
        activeHotspots.add(new HotspotView(
                node, x, y, width, height
        ));
    }

    /**
     * Posiziona gli hotspot rispetto alle dimensioni effettive della scena.
     */
    public void positionHotspots(Pane sceneContent) {
        if (sceneContent == null) {
            return;
        }

        double width = sceneContent.getWidth();
        double height = sceneContent.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        for (HotspotView hotspot : activeHotspots) {
            hotspot.node().setLayoutX(
                    hotspot.x() * width - hotspot.width() / 2
            );

            hotspot.node().setLayoutY(
                    hotspot.y() * height - hotspot.height() / 2
            );
        }
    }

    /**
     * Svuota la registrazione degli hotspot.
     */
    public void clear() {
        activeHotspots.clear();
    }

    private record HotspotView(
            Node node,
            double x,
            double y,
            double width,
            double height
    ) {
    }

    /**
     * Crea la rappresentazione grafica di un oggetto interattivo.
     */
    public StackPane createItemObject(
            String itemName,
            ImageView itemImage,
            double size
    ) {
        if (itemImage == null) {
            return null;
        }

        itemImage.setFitWidth(size);
        itemImage.setFitHeight(size);
        itemImage.setPreserveRatio(true);

        double rotation = switch (itemName.toLowerCase()) {
            case "tavoletta dei geroglifici" -> 6;
            case "scheggia del sigillo" -> -17;
            case "pergamena antica" -> 16;
            case "scarabeo dorato" -> -10;
            case "chiave antica" -> 45;
            case "corda" -> 10;
            case "ankh" -> -18;
            case "pugnale cerimoniale" -> -260;
            default -> 0;
        };

        itemImage.setRotate(rotation);
        itemImage.setMouseTransparent(true);

        Circle halo = new Circle(size * 0.48);

        halo.setFill(new RadialGradient(
                0,
                0,
                0.5,
                0.5,
                0.5,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 220, 145, 0.65)),
                new Stop(0.35, Color.rgb(230, 189, 112, 0.30)),
                new Stop(1, Color.TRANSPARENT)
        ));

        halo.setEffect(new GaussianBlur(size * 0.16));

        StackPane object = new StackPane(halo, itemImage);

        double objectSize = size * 1.25;

        object.setPrefSize(objectSize, objectSize);
        object.setMinSize(objectSize, objectSize);
        object.setMaxSize(objectSize, objectSize);
        object.setCursor(javafx.scene.Cursor.HAND);

        FadeTransition pulse = new FadeTransition(
                Duration.seconds(2),
                halo
        );

        pulse.setFromValue(0.45);
        pulse.setToValue(1.0);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        return object;
    }

}
