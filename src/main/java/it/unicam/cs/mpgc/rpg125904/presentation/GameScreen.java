
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {

    private final GameManager gameManager = new GameManager();
    private final GameSaver gameSaver = new GameSaver();

    private final Color gold = Color.web("#E6BD70");
    private final Color cream = Color.web("#F5E8CF");
    private final Color dark = Color.rgb(22, 17, 13, 0.94);
    private final Color blue = Color.web("#18364A");

    private StackPane sceneArea;
    private StackPane sceneContent;
    private Pane hotspotLayer;

    private VBox inspectionPanel;
    private Label inspectionTitle;
    private Label inspectionDescription;
    private ImageView closeupImage;
    private VBox interactionContent;

    private Label statusLabel;
    private Label roomTitle;

    private HBox inventorySlots;
    private ScrollPane inventoryScroll;

    private Button previousButton;
    private Button nextButton;

    private ImageView background;
    private Game currentGame;

    private final List<HotspotView> activeHotspots = new ArrayList<>();

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

        createInspectionPanel();

        sceneArea.getChildren().add(inspectionPanel);
        StackPane.setAlignment(inspectionPanel, Pos.CENTER);

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

    private void createInspectionPanel() {
        inspectionPanel = new VBox(12);
        inspectionPanel.setAlignment(Pos.CENTER);
        inspectionPanel.setPadding(new Insets(20));
        inspectionPanel.setMaxWidth(760);
        inspectionPanel.setMaxHeight(580);

        inspectionPanel.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(19, 25, 28, 0.98),
                        new CornerRadii(12),
                        Insets.EMPTY
                )
        ));

        inspectionPanel.setBorder(new Border(
                new BorderStroke(
                        gold,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(12),
                        new BorderWidths(1.2)
                )
        ));

        inspectionTitle = styledLabel("", 19, gold, true);

        closeupImage = new ImageView();
        closeupImage.setFitWidth(320);
        closeupImage.setFitHeight(220);
        closeupImage.setPreserveRatio(true);

        inspectionDescription = styledLabel("", 18, cream, false);
        inspectionDescription.setWrapText(true);
        inspectionDescription.setMinHeight(Region.USE_PREF_SIZE);
        inspectionDescription.setMaxHeight(Region.USE_PREF_SIZE);
        inspectionDescription.setMaxWidth(700);
        inspectionDescription.setPrefWidth(700);
        inspectionDescription.setTextAlignment(TextAlignment.CENTER);
        inspectionDescription.setBackground(Background.EMPTY);
        inspectionDescription.setBorder(Border.EMPTY);

        interactionContent = new VBox(10);
        interactionContent.setAlignment(Pos.CENTER);

        Button closeButton = createButton("Chiudi", blue);
        closeButton.setOnAction(event -> closeInspection());

        inspectionPanel.getChildren().addAll(
                inspectionTitle,
                closeupImage,
                inspectionDescription,
                interactionContent,
                closeButton
        );

        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);
    }

    private void refreshScreen() {
        int roomIndex = currentGame.getCurrentRoomIndex();

        roomTitle.setText(currentGame.getCurrentRoom().getName());

        loadBackground(getBackgroundPath(roomIndex));
        buildHotspots(roomIndex);
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
            Platform.runLater(this::showEndingPanel);
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
     * Crea gli elementi interattivi della stanza.
     */
    private void buildHotspots(int roomIndex) {
        hotspotLayer.getChildren().clear();
        activeHotspots.clear();

        switch (roomIndex) {

            // STANZA 1: INGRESSO DEL TEMPIO.
            case 0 -> {
                addItemHotspot(
                        "Tavoletta dei geroglifici",
                        "Una tavoletta ricoperta di simboli antichi. "
                                + "Le incisioni potrebbero rivelare informazioni "
                                + "sui misteri custoditi nel tempio.",
                        0.22,
                        0.73,
                        60
                );

                addDetailHotspot(
                        "Il bassorilievo del faraone",
                        "Il bassorilievo raffigura il sovrano che fece costruire il tempio. "
                                + "Un'iscrizione racconta che il santuario fu nascosto "
                                + "per proteggere un antico segreto.",
                        0.72,
                        0.38
                );

                addDetailHotspot(
                        "Il braciere antico",
                        "Il braciere è spento da molto tempo. Sul bordo si riconosce "
                                + "un simbolo legato ai rituali di protezione del tempio.",
                        0.84,
                        0.69
                );

                addDetailHotspot(
                        "La pietra incisa",
                        "Una pietra sporge dal pavimento. Il simbolo inciso sembra "
                                + "avere la stessa origine di quello presente sulla scheggia.",
                        0.43,
                        0.78
                );

                addDetailHotspot(
                        "I simboli sulla colonna",
                        "Sulla colonna sono incisi alcuni simboli consumati dal tempo. "
                                + "Uno di essi ricorda il segno del sole, simbolo di rinascita "
                                + "e di protezione nell'antico tempio.",
                        0.30,
                        0.37
                );
            }

            // STANZA 2: SALA DELLE ISCRIZIONI.
            case 1 -> {
                addItemHotspot(
                        "Scheggia del sigillo",
                        "Una scheggia di pietra incisa con il simbolo del sigillo "
                                + "che protegge il tempio. La sua superficie emana "
                                + "una debole luce bluastra.",
                        0.13,
                        0.90,
                        43
                );

                addItemHotspot(
                        "Pergamena antica",
                        "Una pergamena con annotazioni su un antico rituale.",
                        0.48,
                        0.84,
                        82
                );

                addItemHotspot(
                        "Anfora rituale",
                        "Un'anfora decorata con simboli egizi.",
                        0.86,
                        0.90,
                        88
                );

                addItemHotspot(
                        "Scarabeo dorato",
                        "Un piccolo scarabeo d'oro, simbolo di rinascita e protezione.",
                        0.45,
                        0.65,
                        46
                );

                addPuzzleHotspot(
                        "Iscrizioni sulla parete",
                        "Le iscrizioni nascondono un indovinello. "
                                + "Osserva attentamente i simboli.",
                        0.73,
                        0.34
                );
            }

            // STANZA 3: CAMERA DEGLI ENIGMI.
            case 2 -> {
                addPuzzleHotspot(
                        "Altare centrale",
                        "Sulla superficie dell'altare è inciso un enigma. "
                                + "Sembra reagire alla Scheggia del sigillo.",
                        0.51,
                        0.57
                );
            }

            // STANZA 4: CAMERA SEGRETA BLU.
            case 3 -> {
                addItemHotspot(
                        "Chiave antica",
                        "Una chiave di metallo consumata dal tempo. Potrebbe aprire qualcosa nel tempio.",
                        0.28,
                        0.90,
                        68
                );

                addItemHotspot(
                        "Libro rituale",
                        "Un volume impolverato che descrive i rituali dei custodi. "
                                + "Alcune pagine sono consumate, ma i disegni mostrano "
                                + "un Ankh posto al centro di un altare.",
                        0.43,
                        0.68,
                        82
                );

                addItemHotspot(
                        "Corda",
                        "Una corda robusta, apparentemente ancora utilizzabile.",
                        0.96,
                        0.94,
                        70
                );

                addItemHotspot(
                        "Lanterna",
                        "Una lanterna antica che potrebbe illuminare gli angoli più bui.",
                        0.44,
                        0.52,
                        80
                );

                addDetailHotspot(
                        "Il murale del guardiano",
                        "Il dipinto raffigura un custode davanti alla porta del santuario. "
                                + "La sua mano indica un simbolo circolare: il sigillo non "
                                + "si apre con la forza, ma attraverso un'offerta rituale.",
                        0.18,
                        0.30
                );

                addDetailHotspot(
                        "La stele inclinata",
                        "La stele racconta che l'ultimo custode affidò all'Ankh "
                                + "il compito di mantenere chiuso il santuario. "
                                + "Il simbolo inciso coincide con quello del sigillo.",
                        0.78,
                        0.40
                );

                addDetailHotspot(
                        "Il tavolo degli strumenti",
                        "Restano vasi e strumenti usati durante le cerimonie. "
                                + "La polvere attorno a un piccolo spazio circolare "
                                + "sembra essere stata rimossa di recente.",
                        0.62,
                        0.57
                );
            }

            // STANZA 5: CORRIDOIO DEL SANTUARIO.
            case 4 -> {

                addItemHotspot(
                        "Ankh",
                        "Il simbolo egizio della vita eterna, scolpito in un piccolo oggetto.",
                        0.19,
                        0.89,
                        70
                );

                addItemHotspot(
                        "Pugnale cerimoniale",
                        "Un antico pugnale decorato. Sembra avere un significato rituale.",
                        0.82,
                        0.87,
                        68
                );

                addFinaleHotspot(
                        "L'altare del sigillo",
                        "Al centro dell'altare c'è un incavo a forma di Ankh. Il Libro "
                                + "rituale spiega che il simbolo della vita eterna deve essere "
                                + "collocato qui per risvegliare il sigillo.",
                        0.31,
                        0.67
                );
            }

            default -> {
            }
        }

        positionHotspots();
    }

    /**
     * Aggiunge un oggetto raccoglibile con alone luminoso.
     */
    private void addItemHotspot(
            String itemName,
            String description,
            double x,
            double y,
            double size
    ) {
        Item item = findItem(itemName);

        if (item == null) {
            return;
        }

        ImageView itemImage = createItemImage(itemName, size);

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
        }if (itemName.equalsIgnoreCase("Corda")) {
            itemImage.setRotate(+10);
        }if (itemName.equalsIgnoreCase("Ankh")) {
            itemImage.setRotate(-18);
        }if (itemName.equalsIgnoreCase("Pugnale cerimoniale")) {
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
            inspectHotspot(hotspot);
            event.consume();
        });

        Tooltip.install(object, new Tooltip("Esamina oggetto"));

        hotspotLayer.getChildren().add(object);

        activeHotspots.add(
                new HotspotView(object, x, y, objectSize, objectSize)
        );
    }

    /**
     * Aggiunge un punto cliccabile che apre un enigma.
     */
    private void addPuzzleHotspot(
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
            inspectHotspot(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Esamina"));

        hotspotLayer.getChildren().add(point);

        activeHotspots.add(
                new HotspotView(point, x, y, 48, 48)
        );
    }

    /**
     * Aggiunge un punto informativo luminoso.
     */
    private void addDetailHotspot(
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
            inspectHotspot(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Scopri un dettaglio"));

        hotspotLayer.getChildren().add(point);

        activeHotspots.add(
                new HotspotView(point, x, y, 42, 42)
        );
    }

    /**
     * Aggiunge il punto interattivo che permette di completare l'avventura.
     */
    private void addFinaleHotspot(
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
            inspectHotspot(hotspot);
            event.consume();
        });

        Tooltip.install(point, new Tooltip("Esamina l'altare"));

        hotspotLayer.getChildren().add(point);
        activeHotspots.add(new HotspotView(point, x, y, 54, 54));
    }

    /**
     * Crea il piccolo indicatore dorato pulsante.
     */
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

    /**
     * Posiziona gli hotspot rispetto alle dimensioni effettive della scena.
     */
    private void positionHotspots() {
        if (hotspotLayer == null || sceneContent == null) {
            return;
        }

        double width = sceneContent.getWidth();
        double height = sceneContent.getHeight();

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

    /**
     * Mostra un ingrandimento della zona selezionata.
     */
    private void showSceneZoom(Hotspot hotspot) {
        Image image = background.getImage();

        if (image == null) {
            closeupImage.setImage(null);
            closeupImage.setViewport(null);
            return;
        }

        Rectangle2D visibleArea = background.getViewport();

        if (visibleArea == null) {
            visibleArea = new Rectangle2D(
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight()
            );
        }

        double cropWidth = visibleArea.getWidth() * 0.42;
        double cropHeight = visibleArea.getHeight() * 0.42;

        double centerX = visibleArea.getMinX()
                + hotspot.x * visibleArea.getWidth();

        double centerY = visibleArea.getMinY()
                + hotspot.y * visibleArea.getHeight();

        double cropX = centerX - cropWidth / 2;
        double cropY = centerY - cropHeight / 2;

        cropX = Math.max(
                0,
                Math.min(cropX, image.getWidth() - cropWidth)
        );

        cropY = Math.max(
                0,
                Math.min(cropY, image.getHeight() - cropHeight)
        );

        closeupImage.setImage(image);

        closeupImage.setViewport(new Rectangle2D(
                cropX,
                cropY,
                cropWidth,
                cropHeight
        ));

        closeupImage.setFitWidth(320);
        closeupImage.setFitHeight(220);
    }

    /**
     * Apre il pannello di ispezione dell'elemento selezionato.
     */
    private void inspectHotspot(Hotspot hotspot) {
        inspectionTitle.setText(hotspot.title);
        inspectionDescription.setText(hotspot.description);

        interactionContent.getChildren().clear();

        if (hotspot.item != null) {
            ImageView itemImage = createItemImage(
                    hotspot.item.getName(),
                    220
            );

            closeupImage.setViewport(null);
            closeupImage.setImage(
                    itemImage == null ? null : itemImage.getImage()
            );

            closeupImage.setFitWidth(220);
            closeupImage.setFitHeight(190);

            Button collectButton = createButton(
                    "Raccogli oggetto",
                    Color.web("#79552C")
            );

            collectButton.setOnAction(event -> {
                if (gameManager.collectItem(currentGame, hotspot.item)) {
                    statusLabel.setText(
                            "Hai raccolto: " + hotspot.item.getName()
                    );

                    closeInspection();
                    refreshScreen();

                } else {
                    inspectionDescription.setText(
                            "Questo oggetto non è più disponibile."
                    );

                    collectButton.setDisable(true);
                }
            });

            interactionContent.getChildren().add(collectButton);

        } else if (hotspot.puzzle) {
            showSceneZoom(hotspot);

            Puzzle puzzle = currentGame.getCurrentRoom().getPuzzle();

            if (puzzle != null && puzzle.isSolved()) {
                interactionContent.getChildren().add(
                        styledLabel(
                                "Meccanismo già attivato",
                                14,
                                gold,
                                true
                        )
                );

            } else {
                TextField answerField = new TextField();
                answerField.setPromptText("La tua risposta...");
                answerField.setMaxWidth(260);

                Label feedback = styledLabel(
                        "",
                        12,
                        gold,
                        false
                );

                feedback.setWrapText(true);

                Button solveButton = createButton(
                        "Prova a risolvere",
                        blue
                );

                solveButton.setOnAction(event -> {
                    if (answerField.getText().isBlank()) {
                        feedback.setText("Inserisci una risposta.");
                        return;
                    }

                    boolean solved = gameManager.solveCurrentPuzzle(
                            currentGame,
                            answerField.getText().trim()
                    );

                    if (solved) {
                        feedback.setText(
                                "Corretto! Il meccanismo si è attivato."
                        );

                        statusLabel.setText(
                                "Hai risolto l'enigma: il passaggio è sbloccato."
                        );

                        solveButton.setDisable(true);
                        answerField.setDisable(true);

                        nextButton.setDisable(
                                currentGame.isCompleted()
                                        || !currentGame.hasNextRoom()
                        );

                    } else {
                        feedback.setText(
                                "Non è la risposta giusta. Riprova."
                        );
                    }
                });

                interactionContent.getChildren().addAll(
                        answerField,
                        solveButton,
                        feedback
                );
            }

            if (puzzle != null) {
                inspectionDescription.setText(
                        puzzle.getDescription()
                );
            }

        } else if (hotspot.finale) {
            showSceneZoom(hotspot);

            if (currentGame.isCompleted()) {
                inspectionDescription.setText(
                        "Il sigillo è già stato attivato. Il tempio è finalmente libero."
                );
            } else {
                inspectionDescription.setText(
                        "L'altare presenta un incavo a forma di Ankh. Hai già raccolto "
                                + "il simbolo della vita eterna? Premi il pulsante qui sotto "
                                + "per collocarlo al centro dell'altare e attivare il sigillo."
                );
                inspectionDescription.setWrapText(true);
                inspectionDescription.setMaxWidth(440);

                Button finaleButton = createButton(
                        "Inserisci l'Ankh nell'altare",
                        Color.web("#79552C")
                );

                Label finaleFeedback = styledLabel(
                        "L'Ankh verrà utilizzato per attivare il sigillo.",
                        13,
                        gold,
                        false
                );
                finaleFeedback.setWrapText(true);

                finaleButton.setOnAction(event -> {
                    if (gameManager.completeFinale(currentGame)) {
                        closeInspection();
                        statusLabel.setText("Il sigillo si illumina. Hai completato l'avventura!");
                        refreshScreen();
                        showEndingPanel();
                    } else {
                        finaleFeedback.setText(
                                "Non hai ancora l'Ankh. Esamina il corridoio e raccogli il simbolo della vita eterna."
                        );
                    }
                });

                interactionContent.getChildren().addAll(
                        finaleButton,
                        finaleFeedback
                );
            }

        } else {
            showSceneZoom(hotspot);
        }

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
    }

    /**
     * Mostra la conclusione narrativa dell'avventura.
     */
    private void showEndingPanel() {
        if (inspectionPanel == null || currentGame == null || !currentGame.isCompleted()) {
            return;
        }

        inspectionTitle.setText("AVVENTURA COMPLETATA");
        inspectionDescription.setText(
                "L'Ankh si incastra nell'altare. Una luce attraversa le pareti "
                        + "e il sigillo si risveglia, rivelando il santuario nascosto.\n\n"
                        + "Il tempio non custodiva un tesoro da conquistare, ma una memoria "
                        + "da proteggere. Hai ricomposto gli indizi, superato le prove e "
                        + "raggiunto il cuore del tempio. La tua esplorazione si conclude qui."
        );
        inspectionDescription.setMaxWidth(410);

        closeupImage.setImage(null);
        closeupImage.setViewport(null);

        interactionContent.getChildren().clear();

        Button restartButton = createButton(
                "Ricomincia l'avventura",
                Color.web("#79552C")
        );
        restartButton.setOnAction(event -> {
            String playerName = currentGame.getPlayer().getName();
            currentGame = gameManager.startNewGame(playerName);
            closeInspection();
            statusLabel.setText("Una nuova esplorazione sta per cominciare.");
            refreshScreen();
        });

        interactionContent.getChildren().add(restartButton);
        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
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
            ImageView image = createItemImage(item.getName(), 34);

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
            itemButton.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
            itemButton.setAlignment(Pos.CENTER);
            itemButton.setPadding(new Insets(5));
            itemButton.setMinSize(48, 48);
            itemButton.setPrefSize(48, 48);
            itemButton.setMaxSize(48, 48);

            Tooltip.install(
                    itemButton,
                    new Tooltip("Clicca per visualizzare l'oggetto")
            );

            itemButton.setOnAction(event -> showInventoryItem(item));

            inventorySlots.getChildren().add(itemButton);
        }
    }

    /**
     * Mostra nome, immagine e descrizione dell'oggetto selezionato.
     */
    private void showInventoryItem(Item item) {
        inspectionTitle.setText(item.getName());
        inspectionDescription.setText(item.getDescription());

        interactionContent.getChildren().clear();

        ImageView detail = createItemImage(item.getName(), 220);

        closeupImage.setViewport(null);
        closeupImage.setImage(
                detail == null ? null : detail.getImage()
        );

        closeupImage.setFitWidth(220);
        closeupImage.setFitHeight(190);

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
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
        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);
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

    /**
     * Carica lo sfondo della stanza.
     */
    private void loadBackground(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {

            if (stream == null) {
                background.setImage(null);

                statusLabel.setText(
                        "Immagine non trovata: " + path
                );

                return;
            }

            Image image = new Image(stream);

            if (image.isError()) {
                background.setImage(null);
                statusLabel.setText(
                        "Errore nel caricamento dell'immagine."
                );
                return;
            }

            background.setImage(image);
            background.setViewport(createWideViewport(image));

        } catch (IOException exception) {
            statusLabel.setText(
                    "Errore nel caricamento dell'ambiente."
            );

            exception.printStackTrace();
        }
    }

    /**
     * Ritaglia lo sfondo mantenendo il rapporto 16:9.
     */
    private Rectangle2D createWideViewport(Image image) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double targetRatio = 16.0 / 9.0;
        double imageRatio = imageWidth / imageHeight;

        if (imageRatio > targetRatio) {
            double croppedWidth = imageHeight * targetRatio;
            double x = (imageWidth - croppedWidth) / 2.0;

            return new Rectangle2D(
                    x,
                    0,
                    croppedWidth,
                    imageHeight
            );
        }

        double croppedHeight = imageWidth / targetRatio;
        double y = (imageHeight - croppedHeight) / 2.0;

        return new Rectangle2D(
                0,
                y,
                imageWidth,
                croppedHeight
        );
    }

    private String getBackgroundPath(int roomIndex) {
        return switch (roomIndex) {
            case 0 -> "/images/rooms/entrance.png";
            case 1 -> "/images/rooms/02_archivio_iscrizioni.png";
            case 2 -> "/images/rooms/puzzle.jpeg";
            case 3 -> "/images/rooms/04_camera_segreta_blu.png";
            case 4 -> "/images/rooms/sanctuary.jpeg";
            default -> "/images/rooms/entrance.png";
        };
    }

    /**
     * Associa ogni oggetto alla propria immagine PNG trasparente.
     */
    private ImageView createItemImage(String itemName, double size) {
        String normalized = itemName.toLowerCase().trim();
        String path = null;

        if (normalized.contains("scheggia")
                || normalized.contains("frammento")) {
            path = "/images/items/cristallo_blu senza sfondo.png";

        } else if (normalized.contains("tavoletta")) {
            path = "/images/items/tavoletta_geroglifici senza sfondo.png";

        } else if (normalized.contains("pergamena")) {
            path = "/images/items/pergamena senza sfondo.png";

        } else if (normalized.contains("anfora")) {
            path = "/images/items/anfora senza sfondo.png";

        } else if (normalized.contains("scarabeo")) {
            path = "/images/items/scarabeo_dorato senza sfondo.png";

        } else if (normalized.contains("chiave")) {
            path = "/images/items/chiave_antica senza sfondo.png";

        } else if (normalized.contains("libro")) {
            path = "/images/items/libro_rituale senza sfondo.png";

        } else if (normalized.contains("corda")) {
            path = "/images/items/corda senza sfondo.png";

        } else if (normalized.contains("lanterna")) {
            path = "/images/items/lanterna senza sfondo.png";

        } else if (normalized.contains("amuleto")) {
            path = "/images/items/amuleto_compass senza sfondo.png";

        } else if (normalized.contains("ankh")) {
            path = "/images/items/ankh senza sfondo.png";

        } else if (normalized.contains("pugnale")) {
            path = "/images/items/pugnale senza sfondo.png";
        }

        if (path == null) {
            return null;
        }

        try (InputStream stream = getClass().getResourceAsStream(path)) {

            if (stream == null) {
                System.err.println(
                        "Immagine non trovata: " + path
                );

                return null;
            }

            Image image = new Image(stream);

            if (image.isError()) {
                System.err.println(
                        "Errore nel caricamento: " + path
                );

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

    private static class Hotspot {

        private final String title;
        private final String description;
        private final Item item;
        private final boolean puzzle;
        private final boolean finale;

        private final double x;
        private final double y;

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
