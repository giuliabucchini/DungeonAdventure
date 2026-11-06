package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import javafx.scene.control.Button;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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

        // Nome del giocatore
        Label playerName = new Label(
                "Esploratore: " + game.getPlayer().getName()
        );
        playerName.setFont(Font.font("Serif", 17));
        playerName.setTextFill(Color.web("#F3E5C8"));
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

// Gestione dell'esplorazione
        GameManager gameManager = new GameManager();

        exploreButton.setOnAction(event -> {

            // Controlliamo se esiste una stanza successiva
            if (game.hasNextRoom()) {

                // Passaggio alla stanza successiva
                gameManager.moveToNextRoom(game);

                // Aggiornamento del nome della stanza
                roomName.setText(game.getCurrentRoom().getName());

                // Aggiornamento della descrizione
                description.setText(game.getCurrentRoom().getDescription());

                // Se siamo arrivati all'ultima stanza
                if (!game.hasNextRoom()) {
                    exploreButton.setText("Esplorazione completata");
                    exploreButton.setDisable(true);
                }
            }
        });

        // Organizzazione delle informazioni
        VBox information = new VBox(25);
        information.setAlignment(Pos.CENTER);
        information.setPadding(new Insets(30));

        information.getChildren().addAll(
                roomName,
                description,
                playerName,
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
}