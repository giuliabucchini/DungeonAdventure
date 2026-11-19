package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class HotspotManager {

    private final Pane hotspotLayer;
    private final Supplier<Game> gameSupplier;
    private final Function<String, Item> itemFinder;
    private final Consumer<Hotspot> hotspotAction;
    private final GameImageFactory imageFactory;

    private final List<HotspotView> activeHotspots = new ArrayList<>();

    public HotspotManager(
            Pane hotspotLayer,
            Supplier<Game> gameSupplier,
            Function<String, Item> itemFinder,
            Consumer<Hotspot> hotspotAction,
            GameImageFactory imageFactory
    ) {
        this.hotspotLayer = hotspotLayer;
        this.gameSupplier = gameSupplier;
        this.itemFinder = itemFinder;
        this.hotspotAction = hotspotAction;
        this.imageFactory = imageFactory;
    }

    public void buildHotspots(int roomIndex) {
        hotspotLayer.getChildren().clear();
        activeHotspots.clear();

        new HotspotRoomBuilder(this).build(roomIndex);

        positionHotspots(
                hotspotLayer.getWidth(),
                hotspotLayer.getHeight()
        );
    }

    void addItemHotspot(
            String itemName,
            String description,
            double x,
            double y,
            double size
    ) {
        Item item = itemFinder.apply(itemName);

        if (item == null) {
            return;
        }

        ImageView itemImage = imageFactory.createItemImage(itemName, size);

        if (itemImage == null) {
            return;
        }

        itemImage.setFitWidth(size);
        itemImage.setFitHeight(size);
        itemImage.setPreserveRatio(true);

        if (itemName.equalsIgnoreCase("Tavoletta dei geroglifici")) {
            itemImage.setRotate(+6);
        }
        if (itemName.equalsIgnoreCase("Scheggia del sigillo")) {
            itemImage.setRotate(-17);
        }
        if (itemName.equalsIgnoreCase("Pergamena antica")) {
            itemImage.setRotate(+16);
        }
        if (itemName.equalsIgnoreCase("Scarabeo dorato")) {
            itemImage.setRotate(-10);
        }
        if (itemName.equalsIgnoreCase("Chiave antica")) {
            itemImage.setRotate(+45);
        }
        if (itemName.equalsIgnoreCase("Corda")) {
            itemImage.setRotate(+10);
        }
        if (itemName.equalsIgnoreCase("Ankh")) {
            itemImage.setRotate(-18);
        }
        if (itemName.equalsIgnoreCase("Pugnale cerimoniale")) {
            itemImage.setRotate(-260);
        }

        itemImage.setMouseTransparent(true);

        javafx.scene.shape.Circle halo =
                new javafx.scene.shape.Circle(size * 0.48);

        halo.setFill(new javafx.scene.paint.RadialGradient(
                0,
                0,
                0.5,
                0.5,
                0.5,
                true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(
                        0,
                        Color.rgb(255, 220, 145, 0.65)
                ),
                new javafx.scene.paint.Stop(
                        0.35,
                        Color.rgb(230, 189, 112, 0.30)
                ),
                new javafx.scene.paint.Stop(
                        1,
                        Color.TRANSPARENT
                )
        ));

        halo.setEffect(
                new javafx.scene.effect.GaussianBlur(size * 0.16)
        );

        StackPane object = new StackPane(halo, itemImage);

        double objectSize = size * 1.25;

        object.setPrefSize(objectSize, objectSize);
        object.setMinSize(objectSize, objectSize);
        object.setMaxSize(objectSize, objectSize);
        object.setCursor(Cursor.HAND);

        javafx.animation.FadeTransition pulse =
                new javafx.animation.FadeTransition(
                        javafx.util.Duration.seconds(2),
                        halo
                );

        pulse.setFromValue(0.45);
        pulse.setToValue(1.0);
        pulse.setCycleCount(
                javafx.animation.Animation.INDEFINITE
        );
        pulse.setAutoReverse(true);
        pulse.play();

        Hotspot hotspot = new Hotspot(
                itemName,
                description,
                item,
                false,
                false,
                x,
                y
        );

        object.setOnMouseClicked(event -> {
            hotspotAction.accept(hotspot);
            event.consume();
        });

        Tooltip.install(object, new Tooltip("Esamina oggetto"));

        hotspotLayer.getChildren().add(object);

        activeHotspots.add(
                new HotspotView(object, x, y, objectSize, objectSize)
        );
    }

    void addPuzzleHotspot(
            String title,
            String description,
            double x,
            double y
    ) {
        StackPane point = createGlowingMarker(48);

        Hotspot hotspot = new Hotspot(
                title,
                description,
                null,
                true,
                false,
                x,
                y
        );

        point.setOnMouseClicked(event -> {
            hotspotAction.accept(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Esamina"));

        hotspotLayer.getChildren().add(point);

        activeHotspots.add(
                new HotspotView(point, x, y, 48, 48)
        );
    }

    void addDetailHotspot(
            String title,
            String description,
            double x,
            double y
    ) {
        StackPane point = createGlowingMarker(42);

        Hotspot hotspot = new Hotspot(
                title,
                description,
                null,
                false,
                false,
                x,
                y
        );

        point.setOnMouseClicked(event -> {
            hotspotAction.accept(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Scopri un dettaglio"));

        hotspotLayer.getChildren().add(point);

        activeHotspots.add(
                new HotspotView(point, x, y, 42, 42)
        );
    }

    void addFinaleHotspot(
            String title,
            String description,
            double x,
            double y
    ) {
        StackPane point = createGlowingMarker(54);

        Hotspot hotspot = new Hotspot(
                title,
                description,
                null,
                false,
                true,
                x,
                y
        );

        point.setOnMouseClicked(event -> {
            hotspotAction.accept(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Esamina l'altare"));

        hotspotLayer.getChildren().add(point);

        activeHotspots.add(
                new HotspotView(point, x, y, 54, 54)
        );
    }

    private StackPane createGlowingMarker(double size) {
        StackPane marker = new StackPane();

        marker.setPrefSize(size, size);
        marker.setMinSize(size, size);
        marker.setMaxSize(size, size);
        marker.setCursor(Cursor.HAND);

        javafx.scene.shape.Circle halo =
                new javafx.scene.shape.Circle(size * 0.40);

        halo.setFill(new javafx.scene.paint.RadialGradient(
                0,
                0,
                0.5,
                0.5,
                0.5,
                true,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(
                        0,
                        Color.rgb(255, 220, 145, 0.65)
                ),
                new javafx.scene.paint.Stop(
                        0.45,
                        Color.rgb(230, 189, 112, 0.25)
                ),
                new javafx.scene.paint.Stop(
                        1,
                        Color.TRANSPARENT
                )
        ));

        halo.setEffect(
                new javafx.scene.effect.GaussianBlur(size * 0.13)
        );

        javafx.scene.shape.Circle outerRing =
                new javafx.scene.shape.Circle(9);

        outerRing.setFill(Color.rgb(35, 25, 14, 0.75));
        outerRing.setStroke(Color.rgb(255, 218, 145, 0.95));
        outerRing.setStrokeWidth(1.5);

        javafx.scene.shape.Circle center =
                new javafx.scene.shape.Circle(3);

        center.setFill(Color.rgb(255, 235, 190, 1));

        marker.getChildren().addAll(halo, outerRing, center);

        javafx.animation.FadeTransition pulse =
                new javafx.animation.FadeTransition(
                        javafx.util.Duration.seconds(2.2),
                        halo
                );

        pulse.setFromValue(0.35);
        pulse.setToValue(1.0);
        pulse.setCycleCount(
                javafx.animation.Animation.INDEFINITE
        );
        pulse.setAutoReverse(true);
        pulse.play();

        return marker;
    }

    public void positionHotspots(double width, double height) {
        if (hotspotLayer == null) {
            return;
        }

        if (width <= 0 || height <= 0) {
            return;
        }

        for (HotspotView hotspotView : activeHotspots) {
            hotspotView.node.setLayoutX(
                    hotspotView.x * width - hotspotView.width / 2
            );

            hotspotView.node.setLayoutY(
                    hotspotView.y * height - hotspotView.height / 2
            );
        }
    }

    static class Hotspot {

        final String title;
        final String description;
        final Item item;
        final boolean puzzle;
        final boolean finale;
        final double x;
        final double y;

        private Hotspot(
                String title,
                String description,
                Item item,
                boolean puzzle,
                boolean finale,
                double x,
                double y
        ) {
            this.title = title;
            this.description = description;
            this.item = item;
            this.puzzle = puzzle;
            this.finale = finale;
            this.x = x;
            this.y = y;
        }
    }

    private static class HotspotView {

        private final Node node;
        private final double x;
        private final double y;
        private final double width;
        private final double height;

        private HotspotView(
                Node node,
                double x,
                double y,
                double width,
                double height
        ) {
            this.node = node;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}