
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaver;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.util.List;

public class GameScreen {

    private final GameManager gameManager = new GameManager();
    private final GameSaver gameSaver = new GameSaver();
    private final GameImageFactory imageFactory = new GameImageFactory();

    private HotspotManager hotspotManager;
    private InspectionManager inspectionManager;

    private final Color gold = Color.web("#E6BD70");
    private final Color cream = Color.web("#F5E8CF");
    private final Color dark = Color.rgb(22, 17, 13, 0.94);
    private final Color blue = Color.web("#18364A");

    private StackPane sceneArea;
    private StackPane sceneContent;
    private Pane hotspotLayer;

    private Label statusLabel;
    private Label roomTitle;

    private HBox inventorySlots;
    private ScrollPane inventoryScroll;

    private Button previousButton;
    private Button nextButton;

    private ImageView background;
    private Game currentGame;

    public Parent createView(Game game) {
        currentGame = game;

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #100e0b;");

        roomTitle = styledLabel("", 20, gold, true);
        Label gameTitle = styledLabel("DUNGEON ADVENTURE", 18, gold, true);

        statusLabel = styledLabel(
                "Esplora la stanza: alcuni oggetti nascondono qualcosa.",
                12,
                cream,
                false
        );
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(190);

        Button saveButton = createButton("Salva", blue);
        saveButton.setOnAction(event -> saveGame());

        HBox header = new HBox(20, gameTitle, roomTitle, saveButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setBackground(new Background(
                new BackgroundFill(dark, new CornerRadii(8), Insets.EMPTY)
        ));

        HBox.setHgrow(roomTitle, Priority.ALWAYS);
        layout.setTop(header);

        // Scena 16:9.
        sceneArea = new StackPane();
        sceneArea.setMinSize(400, 225);
        sceneArea.setPrefSize(876, 493);

        background = new ImageView();
        background.setPreserveRatio(false);
        background.setMouseTransparent(true);

        hotspotLayer = new Pane();
        hotspotLayer.setPickOnBounds(false);

        sceneContent = new StackPane(background, hotspotLayer);
        sceneContent.setPrefSize(876, 493);
        sceneContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        background.fitWidthProperty().bind(sceneContent.widthProperty());
        background.fitHeightProperty().bind(sceneContent.heightProperty());

        hotspotLayer.prefWidthProperty().bind(sceneContent.widthProperty());
        hotspotLayer.prefHeightProperty().bind(sceneContent.heightProperty());

        sceneArea.getChildren().add(sceneContent);

        sceneArea.widthProperty().addListener((obs, oldValue, newValue) ->
                updateSceneSize()
        );

        sceneArea.heightProperty().addListener((obs, oldValue, newValue) ->
                updateSceneSize()
        );

        sceneContent.widthProperty().addListener((obs, oldValue, newValue) ->
                positionHotspots()
        );

        sceneContent.heightProperty().addListener((obs, oldValue, newValue) ->
                positionHotspots()
        );

        // Pulsante per proseguire.
        nextButton = createButton("Prosegui →", Color.web("#79552C"));
        nextButton.setOnAction(event -> {
            closeInspection();

            if (gameManager.moveToNextRoom(currentGame)) {
                statusLabel.setText(
                        "Una nuova area del tempio si apre davanti a te."
                );
                refreshScreen();
            } else {
                statusLabel.setText(
                        "Prima devi risolvere il meccanismo che blocca il passaggio."
                );
            }
        });

        inspectionManager = new InspectionManager(
                gameManager,
                imageFactory,
                () -> currentGame,
                newGame -> currentGame = newGame,
                sceneArea,
                background,
                statusLabel::setText,
                this::refreshScreen,
                nextButton
        );

        hotspotManager = new HotspotManager(
                hotspotLayer,
                () -> currentGame,
                this::findItem,
                inspectionManager::inspectHotspot,
                imageFactory
        );

        sceneArea.getChildren().add(inspectionManager.createPanel());
        StackPane.setAlignment(
                inspectionManager.getPanel(),
                Pos.CENTER
        );

        layout.setCenter(sceneArea);
        BorderPane.setMargin(sceneArea, new Insets(8, 0, 8, 0));

        // Inventario a icone, con scorrimento orizzontale.
        inventorySlots = new HBox(8);
        inventorySlots.setAlignment(Pos.CENTER_LEFT);
        inventorySlots.setPadding(new Insets(2, 4, 2, 4));

        inventoryScroll = new ScrollPane(inventorySlots);
        inventoryScroll.setFitToHeight(true);
        inventoryScroll.setFitToWidth(false);
        inventoryScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        inventoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        inventoryScroll.setPannable(true);
        inventoryScroll.setPrefViewportHeight(54);
        inventoryScroll.setPrefViewportWidth(300);
        inventoryScroll.setMinWidth(100);
        inventoryScroll.setMaxWidth(Double.MAX_VALUE);
        inventoryScroll.setStyle(
                "-fx-background: transparent;"
                        + "-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
        );

        Label inventoryTitle = styledLabel(
                "INVENTARIO",
                12,
                gold,
                true
        );

        HBox inventoryPanel = new HBox(
                10,
                inventoryTitle,
                inventoryScroll
        );

        inventoryPanel.setAlignment(Pos.CENTER_LEFT);
        inventoryPanel.setPadding(new Insets(6, 10, 6, 10));
        inventoryPanel.setMinHeight(70);
        inventoryPanel.setPrefHeight(70);
        inventoryPanel.setMaxHeight(70);
        inventoryPanel.setBackground(new Background(
                new BackgroundFill(dark, new CornerRadii(8), Insets.EMPTY)
        ));

        // Pulsante indietro.
        previousButton = createButton("← Indietro", blue);
        previousButton.setOnAction(event -> {
            closeInspection();

            if (gameManager.moveToPreviousRoom(currentGame)) {
                statusLabel.setText("Sei tornato nella stanza precedente.");
                refreshScreen();
            }
        });

        HBox footer = new HBox(
                10,
                previousButton,
                inventoryPanel,
                statusLabel,
                nextButton
        );

        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(8, 0, 0, 0));

        HBox.setHgrow(inventoryPanel, Priority.ALWAYS);
        HBox.setHgrow(statusLabel, Priority.NEVER);

        layout.setBottom(footer);

        refreshScreen();

        Platform.runLater(() -> {
            sceneArea.applyCss();
            sceneArea.layout();
            updateSceneSize();
            positionHotspots();
        });

        return layout;
    }

    /**
     * Mantiene la scena nel rapporto 16:9.
     */
    private void updateSceneSize() {
        if (sceneArea == null || sceneContent == null) {
            return;
        }

        double availableWidth = sceneArea.getWidth();
        double availableHeight = sceneArea.getHeight();

        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        double width = Math.min(
                availableWidth,
                availableHeight * 16.0 / 9.0
        );

        double height = width * 9.0 / 16.0;

        sceneContent.setPrefSize(width, height);
        sceneContent.setMinSize(width, height);
        sceneContent.setMaxSize(width, height);

        positionHotspots();
    }

    private void positionHotspots() {
        if (hotspotManager == null || sceneContent == null) {
            return;
        }

        hotspotManager.positionHotspots(
                sceneContent.getWidth(),
                sceneContent.getHeight()
        );
    }

    private void refreshScreen() {
        int roomIndex = currentGame.getCurrentRoomIndex();

        roomTitle.setText(currentGame.getCurrentRoom().getName());

        imageFactory.loadBackground(
                imageFactory.getBackgroundPath(roomIndex),
                background,
                statusLabel::setText
        );

        hotspotManager.buildHotspots(roomIndex);
        refreshInventory();

        previousButton.setDisable(
                currentGame.isCompleted() || !currentGame.hasPreviousRoom()
        );

        Puzzle puzzle = currentGame.getCurrentRoom().getPuzzle();

        nextButton.setDisable(
                currentGame.isCompleted()
                        || !currentGame.hasNextRoom()
                        || (puzzle != null && !puzzle.isSolved())
        );

        if (currentGame.isCompleted()) {
            statusLabel.setText("Il sigillo è stato attivato. Avventura completata.");
            Platform.runLater(inspectionManager::showEndingPanel);
        }

        Platform.runLater(() -> {
            if (sceneArea != null) {
                sceneArea.layout();
                updateSceneSize();
                positionHotspots();
            }
        });
    }

    /**
     * Aggiorna l'inventario mostrando solamente le immagini.
     */
    private void refreshInventory() {
        inventorySlots.getChildren().clear();

        List<Item> items = currentGame.getPlayer()
                .getInventory()
                .getItems();

        if (items.isEmpty()) {
            inventorySlots.getChildren().add(
                    styledLabel("Vuoto", 12, cream, false)
            );
            return;
        }

        for (Item item : items) {
            ImageView image = imageFactory.createItemImage(item.getName(), 34);

            if (image == null) {
                continue;
            }

            image.setFitWidth(34);
            image.setFitHeight(34);
            image.setPreserveRatio(true);
            image.setMouseTransparent(true);

            Button itemButton = createButton(
                    "",
                    Color.rgb(103, 75, 43, 0.95)
            );

            itemButton.setGraphic(image);
            itemButton.setContentDisplay(
                    javafx.scene.control.ContentDisplay.GRAPHIC_ONLY
            );
            itemButton.setAlignment(Pos.CENTER);
            itemButton.setPadding(new Insets(5));
            itemButton.setMinSize(48, 48);
            itemButton.setPrefSize(48, 48);
            itemButton.setMaxSize(48, 48);

            Tooltip.install(
                    itemButton,
                    new Tooltip("Clicca per visualizzare l'oggetto")
            );

            itemButton.setOnAction(
                    event -> inspectionManager.showInventoryItem(item)
            );

            inventorySlots.getChildren().add(itemButton);
        }
    }

    private Item findItem(String name) {
        for (Item item : currentGame.getCurrentRoom().getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }

        return null;
    }

    private void closeInspection() {
        inspectionManager.closeInspection();
    }

    /**
     * Salva la partita.
     */
    private void saveGame() {
        try {
            GameSaveData data = gameManager.createSaveData(currentGame);

            gameSaver.save(data, "partita.json");

            statusLabel.setText("Partita salvata.");

        } catch (IOException exception) {
            statusLabel.setText(
                    "Non è stato possibile salvare la partita."
            );

            exception.printStackTrace();
        }
    }

    private Button createButton(String text, Color color) {
        Button button = new Button(text);

        button.setFont(
                Font.font("Serif", FontWeight.BOLD, 13)
        );

        button.setTextFill(cream);
        button.setPadding(new Insets(9, 13, 9, 13));

        button.setBackground(new Background(
                new BackgroundFill(
                        color,
                        new CornerRadii(7),
                        Insets.EMPTY
                )
        ));

        button.setCursor(Cursor.HAND);

        return button;
    }

    private Label styledLabel(
            String text,
            double size,
            Color color,
            boolean bold
    ) {
        Label label = new Label(text);

        label.setFont(
                bold
                        ? Font.font("Serif", FontWeight.BOLD, size)
                        : Font.font("Serif", size)
        );

        label.setTextFill(color);

        return label;
    }
}
