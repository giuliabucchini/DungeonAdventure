
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Consumer;

public class InventoryView {

    private final HBox inventorySlots;
    private final ItemImageFactory itemImageFactory;
    private final Consumer<Item> itemSelection;
    private final Color cream;

    public InventoryView(
            HBox inventorySlots,
            ItemImageFactory itemImageFactory,
            Color cream,
            Consumer<Item> itemSelection
    ) {
        this.inventorySlots = inventorySlots;
        this.itemImageFactory = itemImageFactory;
        this.cream = cream;
        this.itemSelection = itemSelection;
    }

    public void refresh(Game currentGame) {
        inventorySlots.getChildren().clear();

        List<Item> items = currentGame.getPlayer()
                .getInventory()
                .getItems();

        if (items.isEmpty()) {
            Label emptyLabel = new Label("Vuoto");
            emptyLabel.setFont(Font.font("Serif", 12));
            emptyLabel.setTextFill(cream);

            inventorySlots.getChildren().add(emptyLabel);
            return;
        }

        for (Item item : items) {
            ImageView image = itemImageFactory.create(item.getName(), 34);

            if (image == null) {
                continue;
            }

            image.setFitWidth(34);
            image.setFitHeight(34);
            image.setPreserveRatio(true);
            image.setMouseTransparent(true);

            Button itemButton = createItemButton(image);

            itemButton.setOnAction(event ->
                    itemSelection.accept(item)
            );

            inventorySlots.getChildren().add(itemButton);
        }
    }

    private Button createItemButton(ImageView image) {
        Button button = new Button();

        button.setGraphic(image);
        button.setContentDisplay(
                javafx.scene.control.ContentDisplay.GRAPHIC_ONLY
        );
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(5));
        button.setMinSize(48, 48);
        button.setPrefSize(48, 48);
        button.setMaxSize(48, 48);

        button.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(103, 75, 43, 0.95),
                        new CornerRadii(7),
                        Insets.EMPTY
                )
        ));

        button.setCursor(Cursor.HAND);

        Tooltip.install(
                button,
                new Tooltip("Clicca per visualizzare l'oggetto")
        );

        return button;
    }
}
