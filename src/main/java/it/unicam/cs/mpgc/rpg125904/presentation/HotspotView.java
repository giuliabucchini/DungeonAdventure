
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Item;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;
import java.util.function.Function;

public class HotspotView {

    private final Pane hotspotLayer;
    private final HotspotManager hotspotManager;
    private final ItemImageFactory itemImageFactory;
    private final Function<String, Item> itemFinder;
    private final Consumer<Hotspot> inspectionHandler;

    public HotspotView(
            Pane hotspotLayer,
            HotspotManager hotspotManager,
            ItemImageFactory itemImageFactory,
            Function<String, Item> itemFinder,
            Consumer<Hotspot> inspectionHandler
    ) {
        this.hotspotLayer = hotspotLayer;
        this.hotspotManager = hotspotManager;
        this.itemImageFactory = itemImageFactory;
        this.itemFinder = itemFinder;
        this.inspectionHandler = inspectionHandler;
    }

    public void addHotspot(
            Hotspot.Type type,
            String title,
            String description,
            double x,
            double y,
            double size
    ) {
        Item item = null;
        StackPane node;

        if (type == Hotspot.Type.ITEM) {
            item = itemFinder.apply(title);

            if (item == null) {
                return;
            }

            ImageView itemImage = itemImageFactory.create(title, size);

            if (itemImage == null) {
                return;
            }

            node = hotspotManager.createItemObject(
                    title,
                    itemImage,
                    size
            );

            if (node == null) {
                return;
            }

            size *= 1.25;

        } else {
            node = hotspotManager.createGlowingMarker(size);

            if (node == null) {
                return;
            }
        }

        Hotspot hotspot = new Hotspot(
                type,
                title,
                description,
                item,
                x,
                y,
                size
        );

        configureHotspot(node, hotspot, getTooltipText(type));

        hotspotManager.register(node, x, y, size, size);
    }

    private String getTooltipText(Hotspot.Type type) {
        return switch (type) {
            case ITEM -> "Esamina oggetto";
            case PUZZLE -> "Esamina";
            case DETAIL -> "Scopri un dettaglio";
            case FINALE -> "Esamina l'altare";
        };
    }

    private void configureHotspot(
            StackPane node,
            Hotspot hotspot,
            String tooltipText
    ) {
        node.setOnMouseClicked(event -> {
            inspectionHandler.accept(hotspot);
            event.consume();
        });

        Tooltip.install(node, new Tooltip(tooltipText));
        hotspotLayer.getChildren().add(node);
    }
}
