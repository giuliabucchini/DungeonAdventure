
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.persistence.GameLoader;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public class GameWindow extends Application {

    private Stage primaryStage;

    private final GameManager gameManager = new GameManager();

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

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

        // Titolo del gioco
        Label title = new Label("DUNGEON ADVENTURE");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#E4C17A"));

        // Sottotitolo
        Label subtitle = new Label("Il tempio dimenticato");
        subtitle.setFont(Font.font("Serif", 24));
        subtitle.setTextFill(Color.web("#F3E5C8"));

        // Messaggio per eventuali errori
        Label message = new Label("");
        message.setFont(Font.font("Serif", 16));
        message.setTextFill(Color.web("#E4C17A"));

        // Pulsante nuova partita
        Button newGameButton = new Button("Nuova partita");
        styleButton(newGameButton);

        // Pulsante carica partita
        Button loadGameButton = new Button("Carica partita");
        styleButton(loadGameButton);

        // Pulsante esci
        Button exitButton = new Button("Esci");
        styleButton(exitButton);

        // Avvio di una nuova partita
        newGameButton.setOnAction(event -> {

            Game game = gameManager.startNewGame("Esploratore");

            showGameScreen(game);
        });

        // Caricamento di una partita salvata
        loadGameButton.setOnAction(event -> {

            try {

                GameLoader gameLoader = new GameLoader();

                GameSaveData saveData = gameLoader.load("partita.json");

                if (saveData == null) {
                    message.setText("Il salvataggio non contiene dati.");
                    return;
                }

                Game game = gameManager.restoreGame(saveData);

                showGameScreen(game);

            } catch (IOException e) {

                message.setText("Nessun salvataggio trovato.");
                e.printStackTrace();
            }
        });

        // Chiusura del gioco
        exitButton.setOnAction(event -> primaryStage.close());

        // Organizzazione degli elementi
        VBox menu = new VBox(20);

        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(30));

        menu.getChildren().addAll(
                title,
                subtitle,
                newGameButton,
                loadGameButton,
                exitButton,
                message
        );

        // Composizione della schermata
        StackPane root = new StackPane();

        root.getChildren().addAll(
                background,
                overlay,
                menu
        );

        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Dungeon Adventure");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Impostazioni grafiche dei pulsanti
    private void styleButton(Button button) {

        button.setPrefSize(220, 45);
        button.setFont(Font.font("Serif", 18));
        button.setTextFill(Color.web("#F3E5C8"));

        button.setBackground(new Background(
                new BackgroundFill(
                        Color.web("#80603C"),
                        new CornerRadii(8),
                        Insets.EMPTY
                )
        ));
    }

    // Apertura della schermata di gioco
    private void showGameScreen(Game game) {

        GameScreen gameScreen = new GameScreen();

        Scene gameScene = new Scene(
                gameScreen.createView(game),
                900,
                600
        );

        primaryStage.setScene(gameScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
