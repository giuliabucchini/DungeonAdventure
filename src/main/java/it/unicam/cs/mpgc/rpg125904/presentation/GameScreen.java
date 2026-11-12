
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaver;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.io.InputStream;

public class GameScreen {

    private final GameManager gameManager = new GameManager();
    private final GameSaver gameSaver = new GameSaver();

    private final Color gold = Color.web("#E6BD70");
    private final Color cream = Color.web("#F5E8CF");
    private final Color dark = Color.rgb(22, 17, 13, 0.88);
    private final Color blue = Color.web("#172D42");

    public Parent createView(Game game) {

        // SFONDO DELLA STANZA
        ImageView background = new ImageView();
        background.setFitWidth(900);
        background.setFitHeight(600);
        background.setPreserveRatio(false);
        background.setMouseTransparent(true);

        // OSCURAMENTO LEGGERO
        Region overlay = new Region();
        overlay.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(12, 9, 6, 0.18),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        ));
        overlay.setMouseTransparent(true);

        // CONTENITORE PRINCIPALE
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(12));

        // BARRA SUPERIORE
        Label title = new Label("DUNGEON ADVENTURE");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 21));
        title.setTextFill(gold);

        Label roomTitle = new Label();
        roomTitle.setFont(Font.font("Serif", FontWeight.BOLD, 20));
        roomTitle.setTextFill(cream);

        Button saveButton = createButton("Salva", blue);

        Label statusLabel = new Label(
                "Esplora il tempio e scopri i suoi segreti."
        );
        statusLabel.setTextFill(gold);
        statusLabel.setFont(Font.font("Serif", FontWeight.BOLD, 13));
        statusLabel.setWrapText(true);

        saveButton.setOnAction(event -> {
            try {
                GameSaveData data = gameManager.createSaveData(game);
                gameSaver.save(data, "partita.json");
                statusLabel.setText("Partita salvata!");
            } catch (IOException e) {
                statusLabel.setText("Errore nel salvataggio.");
                e.printStackTrace();
            }
        });

        HBox header = new HBox(25, title, roomTitle, saveButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 18, 12, 18));
        header.setBackground(new Background(
                new BackgroundFill(dark, new CornerRadii(10), Insets.EMPTY)
        ));

        HBox.setHgrow(roomTitle, Priority.ALWAYS);
        layout.setTop(header);

        // SCENA CENTRALE
        StackPane sceneArea = new StackPane();

        Pane hotspotsLayer = new Pane();
        hotspotsLayer.setPickOnBounds(false);

        sceneArea.getChildren().add(hotspotsLayer);

        VBox.setVgrow(sceneArea, Priority.ALWAYS);
        layout.setCenter(sceneArea);

        // PANNELLO ENIGMA
        VBox puzzlePanel = new VBox(12);
        puzzlePanel.setPadding(new Insets(22));
        puzzlePanel.setMaxWidth(390);
        puzzlePanel.setAlignment(Pos.CENTER);

        puzzlePanel.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(23, 35, 43, 0.96),
                        new CornerRadii(14),
                        Insets.EMPTY
                )
        ));

        Label puzzleTitle = new Label("✧ ENIGMA DEL TEMPIO ✧");
        puzzleTitle.setFont(Font.font("Serif", FontWeight.BOLD, 19));
        puzzleTitle.setTextFill(gold);

        Label puzzleDescription = new Label();
        puzzleDescription.setWrapText(true);
        puzzleDescription.setTextFill(cream);
        puzzleDescription.setFont(Font.font("Serif", 15));

        TextField answerField = new TextField();
        answerField.setPromptText("Inserisci la risposta...");

        Button solveButton = createButton("Conferma risposta", blue);

        Label puzzleFeedback = new Label();
        puzzleFeedback.setTextFill(gold);
        puzzleFeedback.setWrapText(true);

        puzzlePanel.getChildren().addAll(
                puzzleTitle,
                puzzleDescription,
                answerField,
                solveButton,
                puzzleFeedback
        );

        puzzlePanel.setVisible(false);
        puzzlePanel.setManaged(false);

        StackPane.setAlignment(puzzlePanel, Pos.CENTER);
        sceneArea.getChildren().add(puzzlePanel);

        // INVENTARIO
        Label inventoryTitle = new Label("🎒");
        inventoryTitle.setFont(Font.font(22));

        FlowPane inventorySlots = new FlowPane(8, 5);
        inventorySlots.setAlignment(Pos.CENTER_LEFT);

        HBox inventoryPanel = new HBox(
                12,
                inventoryTitle,
                inventorySlots
        );

        inventoryPanel.setAlignment(Pos.CENTER_LEFT);
        inventoryPanel.setPadding(new Insets(10, 15, 10, 15));
        inventoryPanel.setBackground(new Background(
                new BackgroundFill(dark, new CornerRadii(10), Insets.EMPTY)
        ));

        Button previousRoomButton = createButton(
                "⬅ Indietro",
                blue
        );

        Button nextRoomButton = createButton(
                "Esplora ➜",
                Color.web("#79552C")
        );

        HBox bottom = new HBox(
                15,
                previousRoomButton,
                inventoryPanel,
                statusLabel,
                nextRoomButton
        );

        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 0, 0, 0));

        HBox.setHgrow(inventoryPanel, Priority.ALWAYS);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        layout.setBottom(bottom);

        // AGGIORNAMENTO DELLA SCENA
        Runnable[] refreshScreen = new Runnable[1];

        refreshScreen[0] = () -> {

            roomTitle.setText(game.getCurrentRoom().getName());
            System.out.println("STANZA: " + game.getCurrentRoom().getName());
            System.out.println("INDICE: " + game.getCurrentRoomIndex());
            System.out.println("OGGETTI: " + game.getCurrentRoom().getItems().size());

            for (Item item : game.getCurrentRoom().getItems()) {
                System.out.println("OGGETTO: " + item.getName());
            }

            // CAMBIO AUTOMATICO DELLO SFONDO
            String backgroundPath = getBackgroundPath(
                    game.getCurrentRoomIndex()
            );

            loadBackground(background, backgroundPath);

            // AGGIORNAMENTO INVENTARIO
            inventorySlots.getChildren().clear();

            for (Item item :
                    game.getPlayer().getInventory().getItems()) {

                HBox slot = new HBox(6);
                slot.setAlignment(Pos.CENTER_LEFT);
                slot.setPadding(new Insets(7));

                ImageView itemImage = createItemImage(
                        item.getName(),
                        30
                );

                if (itemImage != null) {
                    slot.getChildren().add(itemImage);
                } else {
                    Label fallback = new Label(
                            getItemIcon(item.getName())
                    );
                    fallback.setFont(Font.font(22));
                    slot.getChildren().add(fallback);
                }

                Label itemName = new Label(item.getName());
                itemName.setTextFill(cream);
                itemName.setFont(Font.font("Serif", 12));

                slot.getChildren().add(itemName);

                slot.setBackground(new Background(
                        new BackgroundFill(
                                Color.rgb(115, 83, 43, 0.85),
                                new CornerRadii(7),
                                Insets.EMPTY
                        )
                ));

                inventorySlots.getChildren().add(slot);
            }

            if (inventorySlots.getChildren().isEmpty()) {
                Label empty = new Label("Inventario vuoto");
                empty.setTextFill(cream);
                inventorySlots.getChildren().add(empty);
            }

            // OGGETTI CLICCABILI NELLA STANZA
            hotspotsLayer.getChildren().clear();


            int position = 0;

            for (Item item : game.getCurrentRoom().getItems()) {

                Button objectButton = createButton(
                        "",
                        Color.TRANSPARENT
                );

                ImageView itemImage = createItemImage(
                        item.getName(),
                        60
                );

                if (itemImage != null) {
                    objectButton.setGraphic(itemImage);
                } else {
                    objectButton.setText(
                            getItemIcon(item.getName())
                    );
                    objectButton.setFont(Font.font(38));
                }

                objectButton.setPrefSize(75, 75);
                objectButton.setPadding(new Insets(0));
                objectButton.setOpacity(0.95);

                objectButton.setTooltip(
                        new Tooltip("Esamina oggetto")
                );

                objectButton.setCursor(Cursor.HAND);

                // POSIZIONE DEGLI OGGETTI
                String itemName = item.getName().toLowerCase();

                if (itemName.contains("frammento")) {
                    objectButton.setLayoutX(140);
                    objectButton.setLayoutY(210);

                } else if (itemName.contains("tavoletta")) {
                    objectButton.setLayoutX(100);
                    objectButton.setLayoutY(240);

                } else if (itemName.contains("amuleto")) {
                    objectButton.setLayoutX(625);
                    objectButton.setLayoutY(400);

                } else {
                    objectButton.setLayoutX(180 + position * 150);
                    objectButton.setLayoutY(190);
                }

                // ESAME E RACCOLTA OGGETTO
                objectButton.setOnAction(event -> {

                    Alert alert = new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

                    ButtonType collectButton =
                            new ButtonType("Raccogli");

                    ButtonType closeButton =
                            new ButtonType("Chiudi");

                    alert.setTitle("Oggetto trovato");
                    alert.setHeaderText(item.getName());
                    alert.setContentText(item.getDescription());

                    alert.getButtonTypes().setAll(
                            collectButton,
                            closeButton
                    );

                    alert.showAndWait().ifPresent(response -> {

                        if (response == collectButton) {

                            boolean collected =
                                    gameManager.collectItem(game, item);

                            if (collected) {
                                statusLabel.setText(
                                        "Hai raccolto: " + item.getName()
                                );

                                refreshScreen[0].run();
                            }
                        }
                    });
                });

                hotspotsLayer.getChildren().add(objectButton);
                position++;
            }

            // ENIGMA
            // ENIGMA
            if (game.getCurrentRoom().getPuzzle() != null) {

                puzzlePanel.setVisible(true);
                puzzlePanel.setManaged(true);

                boolean solved = game.getCurrentRoom()
                        .getPuzzle()
                        .isSolved();

                puzzleDescription.setText(
                        game.getCurrentRoom()
                                .getPuzzle()
                                .getDescription()
                );

                answerField.setDisable(solved);
                solveButton.setDisable(solved);

                if (solved) {
                    puzzleFeedback.setText("✦ Enigma risolto!");
                } else {
                    puzzleFeedback.setText("");
                }

            } else {
                puzzlePanel.setVisible(false);
                puzzlePanel.setManaged(false);
            }
// ESPLORA SI ATTIVA SOLO SE L'ENIGMA È RISOLTO
            boolean canExplore = game.getCurrentRoom().getPuzzle() == null
                    || game.getCurrentRoom().getPuzzle().isSolved();

            nextRoomButton.setDisable(
                    game.isCompleted() || !canExplore
            );

// INDIETRO SI DISATTIVA NELLA PRIMA STANZA
            previousRoomButton.setDisable(!game.hasPreviousRoom());
            if (game.isCompleted()) {
                statusLabel.setText(
                        "Hai raggiunto il Santuario nascosto!"
                );
            }
        };

        // RISOLUZIONE ENIGMA
        solveButton.setOnAction(event -> {

            String answer = answerField.getText().trim();

            if (answer.isEmpty()) {
                puzzleFeedback.setText("Inserisci una risposta.");
                return;
            }

            boolean solved =
                    gameManager.solveCurrentPuzzle(game, answer);

            if (solved) {

                puzzleFeedback.setText(
                        "✦ Risposta corretta! Il passaggio è aperto."
                );

                answerField.clear();
                refreshScreen[0].run();

            } else {

                String requirement = gameManager.getPuzzleRequirementMessage(game);

                if (!requirement.isEmpty()) {
                    puzzleFeedback.setText(requirement);
                } else {
                    puzzleFeedback.setText("Risposta errata. Riprova!");
                }
            }
        });

        // RITORNO ALLA STANZA PRECEDENTE
        previousRoomButton.setOnAction(event -> {

            boolean moved = gameManager.moveToPreviousRoom(game);

            if (moved) {
                statusLabel.setText("Sei tornato nella stanza precedente.");

                answerField.clear();
                refreshScreen[0].run();
            }
        });

        // AVANZAMENTO
        nextRoomButton.setOnAction(event -> {

            boolean moved = gameManager.moveToNextRoom(game);

            if (moved) {

                statusLabel.setText(
                        "Sei entrato in una nuova area del tempio."
                );

                answerField.clear();
                refreshScreen[0].run();

            } else {

                statusLabel.setText(
                        "Devi risolvere l'enigma prima di proseguire."
                );
            }
        });

        refreshScreen[0].run();

        // COMPOSIZIONE FINALE
        StackPane root = new StackPane();

        root.getChildren().addAll(
                background,
                overlay,
                layout
        );

        return root;
    }

    // CARICAMENTO SFONDO
    private void loadBackground(
            ImageView background,
            String path
    ) {

        InputStream stream = getClass().getResourceAsStream(path);

        if (stream != null) {
            background.setImage(new Image(stream));
        } else {
            background.setImage(null);
            System.out.println("Immagine non trovata: " + path);
        }
    }

    // PERCORSO SFONDO IN BASE ALLA STANZA
    private String getBackgroundPath(int roomIndex) {

        switch (roomIndex) {

            case 0:
                return "/images/rooms/entrance.png";

            case 1:
            case 2:
                return "/images/rooms/puzzle.jpeg";

            case 3:
                return "/images/rooms/sanctuary.jpeg";

            default:
                return "/images/rooms/entrance.png";
        }
    }

    // CARICAMENTO IMMAGINI OGGETTI
    private ImageView createItemImage(
            String itemName,
            double size
    ) {

        String normalized = itemName.toLowerCase();
        String path = null;

        if (normalized.contains("frammento")) {
            path = "/images/items/fragment.png";

        } else if (normalized.contains("amuleto")) {
            path = "/images/items/amulet.png";

        } else if (normalized.contains("tavoletta")) {
            path = "/images/items/tablet.png";
        }

        if (path == null) {
            return null;
        }

        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            System.out.println("Immagine non trovata: " + path);
            return null;
        }

        ImageView imageView = new ImageView(new Image(stream));

        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    // EMOJI DI RISERVA SE MANCA UN'IMMAGINE
    private String getItemIcon(String name) {

        String normalized = name.toLowerCase();

        if (normalized.contains("frammento")) {
            return "🪨";
        }

        if (normalized.contains("tavoletta")) {
            return "📜";
        }

        if (normalized.contains("amuleto")) {
            return "🔱";
        }

        return "✦";
    }

    // CREAZIONE PULSANTI
    private Button createButton(String text, Color color) {

        Button button = new Button(text);

        button.setFont(Font.font("Serif", FontWeight.BOLD, 13));
        button.setTextFill(cream);
        button.setPadding(new Insets(12, 16, 12, 16));

        button.setBackground(new Background(
                new BackgroundFill(
                        color,
                        new CornerRadii(9),
                        Insets.EMPTY
                )
        ));

        button.setCursor(Cursor.HAND);

        return button;
    }
}
