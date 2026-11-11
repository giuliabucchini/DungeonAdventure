
package it.unicam.cs.mpgc.rpg125904.presentation;

import com.google.gson.JsonParseException;
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
import java.nio.file.Files;
import java.nio.file.Path;

public class GameWindow extends Application {

    private Stage primaryStage;
    private final GameManager gameManager = new GameManager();

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        Image image = new Image(
                getClass().getResourceAsStream("/images/temple.png")
        );

        ImageView background = new ImageView(image);
        background.setFitWidth(900);
        background.setFitHeight(600);
        background.setPreserveRatio(false);
        background.setMouseTransparent(true);

        Rectangle overlay = new Rectangle(900, 600);
        overlay.setFill(Color.rgb(20, 15, 10, 0.55));
        overlay.setMouseTransparent(true);

        Label title = new Label("DUNGEON ADVENTURE");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 42));
        title.setTextFill(Color.web("#E4C17A"));

        Label subtitle = new Label("Il tempio dimenticato");
        subtitle.setFont(Font.font("Serif", 24));
        subtitle.setTextFill(Color.web("#F3E5C8"));

        Label message = new Label("");
        message.setFont(Font.font("Serif", 16));
        message.setTextFill(Color.web("#E4C17A"));

        Button newGameButton = new Button("Nuova partita");
        styleButton(newGameButton);

        Button loadGameButton = new Button("Carica partita");
        styleButton(loadGameButton);

        Button exitButton = new Button("Esci");
        styleButton(exitButton);

        newGameButton.setOnAction(event -> {
            try {
                Game game = gameManager.startNewGame("Esploratore");
                showGameScreen(game);
            } catch (Exception e) {
                message.setText("Impossibile avviare una nuova partita.");
                e.printStackTrace();
            }
        });

        loadGameButton.setOnAction(event -> {

            Path savePath = Path.of("partita.json");

            if (!Files.exists(savePath)) {
                message.setText("Non esiste ancora una partita salvata.");
                return;
            }

            try {
                GameLoader gameLoader = new GameLoader();
                GameSaveData saveData = gameLoader.load("partita.json");

                if (saveData == null) {
                    message.setText("Il salvataggio non contiene dati.");
                    return;
                }

                Game game = gameManager.restoreGame(saveData);
                showGameScreen(game);

            } catch (IOException | JsonParseException | IllegalArgumentException e) {
                message.setText("Il file di salvataggio non è valido.");
                e.printStackTrace();
            }
        });

        exitButton.setOnAction(event -> primaryStage.close());

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

        StackPane root = new StackPane();
        root.getChildren().addAll(background, overlay, menu);

        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Dungeon Adventure");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

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
