
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Tooltip;

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

        // SFONDO
        ImageView background = new ImageView();

        InputStream stream =
                getClass().getResourceAsStream("/images/temple.png");

        if (stream != null) {
            background.setImage(new Image(stream));
        }

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

        Button saveButton = createButton("💾 Salva", blue);

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

        // INVENTARIO INFERIORE
        Label inventoryTitle = new Label("🎒");
        inventoryTitle.setFont(Font.font(22));

        FlowPane inventorySlots = new FlowPane(8, 5);
        inventorySlots.setAlignment(Pos.CENTER_LEFT);

        HBox inventoryPanel = new HBox(12,
                inventoryTitle,
                inventorySlots
        );

        inventoryPanel.setAlignment(Pos.CENTER_LEFT);
        inventoryPanel.setPadding(new Insets(10, 15, 10, 15));
        inventoryPanel.setBackground(new Background(
                new BackgroundFill(dark, new CornerRadii(10), Insets.EMPTY)
        ));

        Button nextRoomButton =
                createButton("➜ Prosegui", Color.web("#79552C"));

        HBox bottom = new HBox(15,
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

            // INVENTARIO
            inventorySlots.getChildren().clear();

            for (Item item :
                    game.getPlayer().getInventory().getItems()) {

                Label icon = new Label(
                        getItemIcon(item.getName()) + " " + item.getName()
                );

                icon.setTextFill(cream);
                icon.setFont(Font.font("Serif", 12));
                icon.setPadding(new Insets(7));
                icon.setBackground(new Background(
                        new BackgroundFill(
                                Color.rgb(115, 83, 43, 0.85),
                                new CornerRadii(7),
                                Insets.EMPTY
                        )
                ));

                inventorySlots.getChildren().add(icon);
            }

            if (inventorySlots.getChildren().isEmpty()) {
                Label empty = new Label("Inventario vuoto");
                empty.setTextFill(cream);
                inventorySlots.getChildren().add(empty);
            }

            // OGGETTI CLICCABILI NELLA SCENA
            hotspotsLayer.getChildren().clear();

            int position = 0;

            for (Item item : game.getCurrentRoom().getItems()) {

                Button objectButton = createButton(
                        getItemIcon(item.getName()),
                        Color.TRANSPARENT
                );

                objectButton.setFont(Font.font("Serif", 38));
                objectButton.setPrefSize(75, 75);
                objectButton.setOpacity(0.85);

                objectButton.setTooltip(
                        new Tooltip("Esamina")
                );

                objectButton.setPrefSize(125, 85);
                objectButton.setWrapText(true);
                objectButton.setTooltip(
                        new Tooltip("Clicca per raccogliere")
                );

                objectButton.setCursor(Cursor.HAND);

                // Posizione degli oggetti nella scena
                if (item.getName().toLowerCase().contains("frammento")) {
                    objectButton.setLayoutX(140);
                    objectButton.setLayoutY(210);
                } else if (item.getName().toLowerCase().contains("tavoletta")) {
                    objectButton.setLayoutX(420);
                    objectButton.setLayoutY(150);
                } else if (item.getName().toLowerCase().contains("amuleto")) {
                    objectButton.setLayoutX(650);
                    objectButton.setLayoutY(220);
                } else {
                    objectButton.setLayoutX(180 + position * 150);
                    objectButton.setLayoutY(190);
                }

                objectButton.setOnAction(event -> {

                    gameManager.collectItems(game);

                    statusLabel.setText(
                            "Hai raccolto: " + item.getName()
                    );

                    refreshScreen[0].run();
                });

                hotspotsLayer.getChildren().add(objectButton);
                position++;
            }

            // ENIGMA
            if (game.getCurrentRoom().getPuzzle() != null) {

                puzzlePanel.setVisible(true);
                puzzlePanel.setManaged(true);

                boolean solved = game.getCurrentRoom()
                        .getPuzzle().isSolved();

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

            nextRoomButton.setDisable(game.isCompleted());

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
                puzzleFeedback.setText(
                        "Risposta errata oppure oggetto necessario mancante."
                );
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
}
