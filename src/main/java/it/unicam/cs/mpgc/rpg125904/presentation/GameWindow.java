
package it.unicam.cs.mpgc.rpg125904.presentation;
import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class GameWindow extends Application {

    private final GameManager gameManager = new GameManager();
    private Game currentGame;
    @Override
    public void start(Stage primaryStage) {

        // Caricamento dello sfondo
        Image image = new Image(
                getClass().getResourceAsStream("/images/temple.png")
        );

        ImageView background = new ImageView(image);
        background.setFitWidth(900);
        background.setFitHeight(600);
        background.setPreserveRatio(false);

        // Livello scuro per rendere leggibili le scritte
        Rectangle overlay = new Rectangle(900, 600);
        overlay.setFill(Color.rgb(20, 15, 10, 0.35));

        // Titolo del gioco
        Label title = new Label("DUNGEON ADVENTURE");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#E4C17A"));

        // Sottotitolo
        Label subtitle = new Label("Il tempio dimenticato");
        subtitle.setFont(Font.font("Serif", 23));
        subtitle.setTextFill(Color.web("#F3E5C8"));

        // Pulsante nuova partita
        Button newGameButton = new Button("Nuova partita");
        newGameButton.setOnAction(event -> {
            currentGame = gameManager.startNewGame("Esploratore");

            System.out.println("Nuova partita avviata!");
            System.out.println("Giocatore: " + currentGame.getPlayer().getName());
            System.out.println("Stanza: " + currentGame.getCurrentRoom().getName());
        });

        // Pulsante esci
        Button exitButton = new Button("Esci");
        exitButton.setOnAction(event -> primaryStage.close());

        // Dimensioni e caratteri dei pulsanti
        newGameButton.setPrefSize(230, 50);
        exitButton.setPrefSize(230, 50);

        newGameButton.setFont(Font.font("Serif", 18));
        exitButton.setFont(Font.font("Serif", 18));

        newGameButton.setTextFill(Color.web("#F3E5C8"));
        exitButton.setTextFill(Color.web("#F3E5C8"));

        // Colore di sfondo dei pulsanti tramite JavaFX
        Background buttonBackground = new Background(
                new BackgroundFill(
                        Color.web("#80603C"),
                        new CornerRadii(8),
                        Insets.EMPTY
                )
        );

        newGameButton.setBackground(buttonBackground);
        exitButton.setBackground(buttonBackground);

        // Organizzazione degli elementi del menu
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);

        menu.getChildren().addAll(
                title,
                subtitle,
                newGameButton,
                exitButton
        );

        // Sovrapposizione degli elementi allo sfondo
        StackPane root = new StackPane();

        root.getChildren().addAll(
                background,
                overlay,
                menu
        );

        // Creazione della scena
        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Dungeon Adventure");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
