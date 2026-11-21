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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class GameScreen {

    private static final int INVENTORY_SLOT_COUNT = 10;
    private static final double SLOT_SIZE = 34;

    private final GameManager gameManager = new GameManager();
    private final GameSaver gameSaver = new GameSaver();
    private final GameImageFactory imageFactory = new GameImageFactory();
    private final Runnable onReturnToMenu;

    private HotspotManager hotspotManager;
    private InspectionManager inspectionManager;

    private StackPane sceneArea;
    private StackPane sceneContent;
    private Pane hotspotLayer;
    private StackPane menuOverlay;

    private Label statusLabel;
    private Label roomTitle;

    private HBox inventorySlots;
    private Button previousButton;
    private Button nextButton;

    private ImageView background;
    private Game currentGame;

    public GameScreen() {
        this(null);
    }

    public GameScreen(Runnable onReturnToMenu) {
        this.onReturnToMenu = onReturnToMenu;
    }

    public Parent createView(Game game) {
        currentGame = game;

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #100e0b;");

        Label gameTitle = UiStyleFactory.styledLabel("DUNGEON ADVENTURE:", 18, UiStyleFactory.GOLD, true);
        roomTitle = UiStyleFactory.styledLabel("", 18, UiStyleFactory.CREAM, true);

        HBox titleBox = new HBox(8, gameTitle, roomTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        statusLabel = UiStyleFactory.styledLabel(
                "Benvenuto, " + game.getPlayer().getName() + "! Esplora la stanza.",
                11.5,
                UiStyleFactory.CREAM,
                false
        );
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER_LEFT);
        statusLabel.setMinSize(165, 48);
        statusLabel.setPrefSize(165, 48);
        statusLabel.setMaxSize(165, 48);

        Label playerLabel = UiStyleFactory.styledLabel(
                "Avventuriero: " + game.getPlayer().getName(),
                14,
                UiStyleFactory.CREAM,
                true
        );
        playerLabel.setMaxWidth(180);

        Button menuButton = UiStyleFactory.createButton("Menu", UiStyleFactory.BLUE);
        menuButton.setOnAction(event -> toggleMenuOverlay());

        HBox header = new HBox(16, titleBox, playerLabel, menuButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setBackground(new Background(
                new BackgroundFill(UiStyleFactory.DARK, new CornerRadii(8), Insets.EMPTY)
        ));
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        layout.setTop(header);

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
        sceneArea.widthProperty().addListener((obs, oldVal, newVal) -> updateSceneSize());
        sceneArea.heightProperty().addListener((obs, oldVal, newVal) -> updateSceneSize());
        sceneContent.widthProperty().addListener((obs, oldVal, newVal) -> positionHotspots());
        sceneContent.heightProperty().addListener((obs, oldVal, newVal) -> positionHotspots());

        nextButton = UiStyleFactory.createButton("Prosegui →", UiStyleFactory.BRONZE);
        nextButton.setPadding(new Insets(9, 10, 9, 10));
        nextButton.setMinWidth(105);
        nextButton.setPrefWidth(105);
        nextButton.setMaxWidth(105);
        nextButton.setOnAction(event -> {
            closeMenuOverlay();
            inspectionManager.closeInspection();
            if (gameManager.moveToNextRoom(currentGame)) {
                statusLabel.setText("Una nuova area del tempio si apre davanti a te.");
                refreshScreen();
            } else {
                statusLabel.setText("Prima devi risolvere il meccanismo che blocca il passaggio.");
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
                this::findItem,
                inspectionManager::inspectHotspot,
                imageFactory
        );

        sceneArea.getChildren().add(inspectionManager.createPanel());
        StackPane.setAlignment(inspectionManager.getPanel(), Pos.CENTER);

        menuOverlay = createMenuOverlay();
        sceneArea.getChildren().add(menuOverlay);
        StackPane.setAlignment(menuOverlay, Pos.CENTER);

        layout.setCenter(sceneArea);
        BorderPane.setMargin(sceneArea, new Insets(8, 0, 8, 0));

        inventorySlots = new HBox(4);
        inventorySlots.setAlignment(Pos.CENTER);

        Label inventoryTitle = UiStyleFactory.styledLabel("INVENTARIO", 11, UiStyleFactory.GOLD, true);

        HBox inventoryPanel = new HBox(8, inventoryTitle, inventorySlots);
        inventoryPanel.setAlignment(Pos.CENTER);
        inventoryPanel.setPadding(new Insets(6, 10, 6, 10));
        inventoryPanel.setMinHeight(58);
        inventoryPanel.setPrefHeight(58);
        inventoryPanel.setMaxHeight(58);
        inventoryPanel.setBackground(new Background(
                new BackgroundFill(UiStyleFactory.DARK, new CornerRadii(8), Insets.EMPTY)
        ));
        inventoryPanel.setBorder(new Border(
                new BorderStroke(Color.rgb(121, 85, 44, 0.45), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))
        ));

        previousButton = UiStyleFactory.createButton("← Indietro", UiStyleFactory.BLUE);
        previousButton.setPadding(new Insets(9, 10, 9, 10));
        previousButton.setMinWidth(105);
        previousButton.setPrefWidth(105);
        previousButton.setMaxWidth(105);
        previousButton.setOnAction(event -> {
            closeMenuOverlay();
            inspectionManager.closeInspection();
            if (gameManager.moveToPreviousRoom(currentGame)) {
                statusLabel.setText("Sei tornato nella stanza precedente.");
                refreshScreen();
            }
        });

        HBox footer = new HBox(8, previousButton, inventoryPanel, statusLabel, nextButton);
        footer.setAlignment(Pos.CENTER);
        footer.setMinHeight(62);
        footer.setPrefHeight(62);
        footer.setMaxHeight(62);
        footer.setPadding(new Insets(4, 0, 0, 0));
        HBox.setHgrow(inventoryPanel, Priority.ALWAYS);

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

    private StackPane createMenuOverlay() {
        StackPane overlay = new StackPane();
        overlay.setBackground(new Background(
                new BackgroundFill(Color.rgb(16, 14, 11, 0.75), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        overlay.setVisible(false);

        Label title = UiStyleFactory.styledLabel("MENU", 18, UiStyleFactory.GOLD, true);

        Button saveBtn = UiStyleFactory.createButton("Salva partita", UiStyleFactory.BLUE);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(event -> {
            saveGame();
            closeMenuOverlay();
        });

        Button homeBtn = UiStyleFactory.createButton("Schermata iniziale", UiStyleFactory.BRONZE);
        homeBtn.setMaxWidth(Double.MAX_VALUE);
        homeBtn.setOnAction(event -> {
            closeMenuOverlay();
            if (onReturnToMenu != null) {
                onReturnToMenu.run();
            }
        });

        Button resumeBtn = UiStyleFactory.createButton("Riprendi", Color.web("#4A3B2C"));
        resumeBtn.setMaxWidth(Double.MAX_VALUE);
        resumeBtn.setOnAction(event -> closeMenuOverlay());

        VBox box = new VBox(12, title, saveBtn, homeBtn, resumeBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(22, 26, 22, 26));
        box.setMaxWidth(260);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setBackground(new Background(
                new BackgroundFill(UiStyleFactory.DARK, new CornerRadii(10), Insets.EMPTY)
        ));
        box.setBorder(new Border(
                new BorderStroke(UiStyleFactory.GOLD, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1.5))
        ));

        overlay.getChildren().add(box);
        overlay.setOnMouseClicked(event -> {
            if (event.getTarget() == overlay) {
                closeMenuOverlay();
            }
        });

        return overlay;
    }

    private void toggleMenuOverlay() {
        if (menuOverlay != null) {
            boolean nextState = !menuOverlay.isVisible();
            menuOverlay.setVisible(nextState);
            if (nextState) {
                menuOverlay.toFront();
            }
        }
    }

    private void closeMenuOverlay() {
        if (menuOverlay != null) {
            menuOverlay.setVisible(false);
        }
    }

    private void updateSceneSize() {
        if (sceneArea == null || sceneContent == null) {
            return;
        }
        double availableWidth = sceneArea.getWidth();
        double availableHeight = sceneArea.getHeight();
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        double width = Math.min(availableWidth, availableHeight * 16.0 / 9.0);
        double height = width * 9.0 / 16.0;

        sceneContent.setPrefSize(width, height);
        sceneContent.setMinSize(width, height);
        sceneContent.setMaxSize(width, height);
        positionHotspots();
    }

    private void positionHotspots() {
        if (hotspotManager != null && sceneContent != null) {
            hotspotManager.positionHotspots(sceneContent.getWidth(), sceneContent.getHeight());
        }
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

        previousButton.setDisable(currentGame.isCompleted() || !currentGame.hasPreviousRoom());

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

    private void refreshInventory() {
        inventorySlots.getChildren().clear();
        List<Item> items = currentGame.getPlayer().getInventory().getItems();
        int totalSlots = Math.max(INVENTORY_SLOT_COUNT, items.size());

        for (int i = 0; i < totalSlots; i++) {
            if (i < items.size()) {
                Item item = items.get(i);
                inventorySlots.getChildren().add(createOccupiedSlot(item));
            } else {
                inventorySlots.getChildren().add(createEmptySlot());
            }
        }
    }

    private Button createOccupiedSlot(Item item) {
        Button slotButton = new Button();
        slotButton.setMinSize(SLOT_SIZE, SLOT_SIZE);
        slotButton.setPrefSize(SLOT_SIZE, SLOT_SIZE);
        slotButton.setMaxSize(SLOT_SIZE, SLOT_SIZE);
        slotButton.setPadding(new Insets(4));
        slotButton.setAlignment(Pos.CENTER);
        slotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        slotButton.setCursor(Cursor.HAND);

        Background normalBg = new Background(
                new BackgroundFill(Color.rgb(103, 75, 43, 0.95), new CornerRadii(6), Insets.EMPTY)
        );
        Background hoverBg = new Background(
                new BackgroundFill(Color.rgb(138, 102, 58, 0.98), new CornerRadii(6), Insets.EMPTY)
        );

        slotButton.setBackground(normalBg);
        slotButton.setBorder(new Border(
                new BorderStroke(UiStyleFactory.GOLD, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1.2))
        ));

        ImageView image = imageFactory.createItemImage(item.getName(), 24);
        if (image != null) {
            image.setMouseTransparent(true);
            slotButton.setGraphic(image);
        }

        slotButton.setOnMouseEntered(e -> slotButton.setBackground(hoverBg));
        slotButton.setOnMouseExited(e -> slotButton.setBackground(normalBg));

        Tooltip.install(slotButton, new Tooltip(item.getName()));
        slotButton.setOnAction(event -> inspectionManager.showInventoryItem(item));

        return slotButton;
    }

    private StackPane createEmptySlot() {
        StackPane emptySlot = new StackPane();
        emptySlot.setMinSize(SLOT_SIZE, SLOT_SIZE);
        emptySlot.setPrefSize(SLOT_SIZE, SLOT_SIZE);
        emptySlot.setMaxSize(SLOT_SIZE, SLOT_SIZE);
        emptySlot.setBackground(new Background(
                new BackgroundFill(Color.rgb(34, 26, 19, 0.9), new CornerRadii(6), Insets.EMPTY)
        ));
        emptySlot.setBorder(new Border(
                new BorderStroke(Color.rgb(121, 85, 44, 0.55), BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))
        ));
        return emptySlot;
    }

    private Item findItem(String name) {
        for (Item item : currentGame.getCurrentRoom().getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    private void saveGame() {
        try {
            GameSaveData data = gameManager.createSaveData(currentGame);
            gameSaver.save(data, "partita.json");
            statusLabel.setText("Partita salvata.");
        } catch (IOException exception) {
            statusLabel.setText("Non è stato possibile salvare la partita.");
            exception.printStackTrace();
        }
    }
}