
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.application.GameManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameScreen {

    public Parent createView(Game game) {

        // Caricamento dello sfondo
        Image image = new Image(
                getClass().getResourceAsStream("/images/temple.png")
        );

        ImageView background = new ImageView(image);
        background.setFitWidth(900);
        background.setFitHeight(600);
        background.setPreserveRatio(false);

        // Livello scuro sopra lo sfondo
        Rectangle overlay = new Rectangle(900, 600);
        overlay.setFill(Color.rgb(20, 15, 10, 0.55));

        // Nome della stanza
        Label roomName = new Label(
                game.getCurrentRoom().getName()
        );
        roomName.setFont(Font.font("Serif", FontWeight.BOLD, 36));
        roomName.setTextFill(Color.web("#E4C17A"));

        // Descrizione della stanza
        Label description = new Label(
                game.getCurrentRoom().getDescription()
        );
        description.setFont(Font.font("Serif", 20));
        description.setTextFill(Color.web("#F3E5C8"));
        description.setWrapText(true);
        description.setMaxWidth(600);

        // Oggetti presenti nella stanza
        Label roomItemsLabel = new Label(
                getRoomItemsText(game)
        );
        roomItemsLabel.setFont(Font.font("Serif", 17));
        roomItemsLabel.setTextFill(Color.web("#E4C17A"));

        // Nome del giocatore
        Label playerName = new Label(
                "Esploratore: " + game.getPlayer().getName()
        );
        playerName.setFont(Font.font("Serif", 17));
        playerName.setTextFill(Color.web("#F3E5C8"));

        // Messaggio per il giocatore
        Label message = new Label("");
        message.setFont(Font.font("Serif", 17));
        message.setTextFill(Color.web("#E4C17A"));

        // Informazioni sull'inventario
        Label inventoryLabel = new Label("Inventario: vuoto");
        inventoryLabel.setFont(Font.font("Serif", 17));
        inventoryLabel.setTextFill(Color.web("#F3E5C8"));

        // Descrizione dell'enigma
        Label puzzleLabel = new Label();
        puzzleLabel.setFont(Font.font("Serif", 19));
        puzzleLabel.setTextFill(Color.web("#E4C17A"));
        puzzleLabel.setWrapText(true);
        puzzleLabel.setMaxWidth(600);

        // Campo per inserire la risposta
        TextField answerField = new TextField();
        answerField.setPromptText("Inserisci la tua risposta");
        answerField.setMaxWidth(300);

        // Pulsante per verificare la risposta
        Button solveButton = new Button("Verifica risposta");
        solveButton.setPrefSize(200, 45);
        solveButton.setFont(Font.font("Serif", 18));
        solveButton.setTextFill(Color.web("#F3E5C8"));
        solveButton.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(
                        Color.web("#80603C"),
                        new javafx.scene.layout.CornerRadii(8),
                        Insets.EMPTY
                )
        ));

        // L'enigma inizialmente non è visibile
        puzzleLabel.setVisible(false);
        puzzleLabel.setManaged(false);

        answerField.setVisible(false);
        answerField.setManaged(false);

        solveButton.setVisible(false);
        solveButton.setManaged(false);

        // Pulsante per raccogliere gli oggetti
        Button collectButton = new Button("Raccogli oggetti");

        collectButton.setPrefSize(200, 45);
        collectButton.setFont(Font.font("Serif", 18));
        collectButton.setTextFill(Color.web("#F3E5C8"));
        collectButton.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(
                        Color.web("#80603C"),
                        new javafx.scene.layout.CornerRadii(8),
                        Insets.EMPTY
                )
        ));

        // Pulsante per esplorare
        Button exploreButton = new Button("Esplora");

        exploreButton.setPrefSize(200, 45);
        exploreButton.setFont(Font.font("Serif", 18));
        exploreButton.setTextFill(Color.web("#F3E5C8"));
        exploreButton.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(
                        Color.web("#80603C"),
                        new javafx.scene.layout.CornerRadii(8),
                        Insets.EMPTY
                )
        ));

        // Gestione delle operazioni di gioco
        GameManager gameManager = new GameManager();

        // Raccolta degli oggetti
        collectButton.setOnAction(event -> {

            if (game.getCurrentRoom().getItems().isEmpty()) {

                message.setText("Non ci sono oggetti da raccogliere.");

            } else {

                gameManager.collectItems(game);

                message.setText("Hai raccolto un oggetto!");

                StringBuilder inventoryText = new StringBuilder(
                        "Inventario: "
                );

                for (Item item : game.getPlayer().getInventory().getItems()) {
                    inventoryText.append(item.getName()).append(", ");
                }

                inventoryText.setLength(inventoryText.length() - 2);

                inventoryLabel.setText(inventoryText.toString());

                roomItemsLabel.setText(getRoomItemsText(game));

                collectButton.setDisable(true);
            }
        });

        // Verifica della risposta all'enigma
        solveButton.setOnAction(event -> {

            boolean solved = gameManager.solveCurrentPuzzle(
                    game,
                    answerField.getText().trim()
            );

            if (solved) {

                message.setText("Hai risolto l'enigma!");

                answerField.setDisable(true);
                solveButton.setDisable(true);

            } else {

                message.setText("L'enigma non reagisce.");
            }
        });

        // Gestione dell'esplorazione
        exploreButton.setOnAction(event -> {

            boolean moved = gameManager.moveToNextRoom(game);

            if (moved) {

                roomName.setText(game.getCurrentRoom().getName());

                description.setText(game.getCurrentRoom().getDescription());

                roomItemsLabel.setText(getRoomItemsText(game));

                message.setText("");

                answerField.clear();
                answerField.setDisable(false);
                solveButton.setDisable(false);

                collectButton.setDisable(
                        game.getCurrentRoom().getItems().isEmpty()
                );

                if (game.getCurrentRoom().getPuzzle() != null) {

                    puzzleLabel.setText(
                            game.getCurrentRoom().getPuzzle().getDescription()
                    );

                    puzzleLabel.setVisible(true);
                    puzzleLabel.setManaged(true);

                    answerField.setVisible(true);
                    answerField.setManaged(true);

                    solveButton.setVisible(true);
                    solveButton.setManaged(true);

                } else {

                    puzzleLabel.setVisible(false);
                    puzzleLabel.setManaged(false);

                    answerField.setVisible(false);
                    answerField.setManaged(false);

                    solveButton.setVisible(false);
                    solveButton.setManaged(false);
                }

                if (!game.isCompleted()) {

                    exploreButton.setText("Avventura completata");
                    exploreButton.setDisable(true);

                    message.setText(
                            "Complimenti! Hai raggiunto il Santuario nascosto "
                                    + "e completato l'esplorazione del tempio!"
                    );
                }

            } else {

                message.setText(
                        "Devi risolvere l'enigma prima di proseguire!"
                );
            }
        });

        // Organizzazione delle informazioni
        VBox information = new VBox(20);
        information.setAlignment(Pos.CENTER);
        information.setPadding(new Insets(30));

        information.getChildren().addAll(
                roomName,
                description,
                roomItemsLabel,
                playerName,
                inventoryLabel,
                message,
                puzzleLabel,
                answerField,
                solveButton,
                collectButton,
                exploreButton
        );

        // Composizione della schermata
        StackPane root = new StackPane();

        root.getChildren().addAll(
                background,
                overlay,
                information
        );

        return root;
    }

    // Recupera il testo degli oggetti presenti nella stanza
    private String getRoomItemsText(Game game) {

        if (game.getCurrentRoom().getItems().isEmpty()) {
            return "Oggetti nella stanza: nessuno";
        }

        StringBuilder text = new StringBuilder(
                "Oggetti nella stanza: "
        );

        for (Item item : game.getCurrentRoom().getItems()) {
            text.append(item.getName()).append(", ");
        }

        text.setLength(text.length() - 2);

        return text.toString();
    }
}
