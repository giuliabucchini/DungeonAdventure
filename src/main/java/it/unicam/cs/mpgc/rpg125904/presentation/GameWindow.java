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
import javafx.scene.control.TextField;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameWindow extends Application {

    private Stage primaryStage;
    private final GameManager gameManager = new GameManager();
    private final GameImageFactory imageFactory = new GameImageFactory();

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Dungeon Adventure");
        primaryStage.setResizable(false);

        showMainMenu();
        primaryStage.show();
    }

    public void showMainMenu() {
        ImageView background = new ImageView();
        Image image = imageFactory.loadImage("/images/rooms/entrance.png");
        if (image != null) {
            background.setImage(image);
        }
        background.setFitWidth(900);
        background.setFitHeight(600);
        background.setPreserveRatio(false);
        background.setMouseTransparent(true);

        Rectangle overlay = new Rectangle(900, 600);
        overlay.setFill(Color.rgb(20, 15, 10, 0.55));
        overlay.setMouseTransparent(true);

        Label title = UiStyleFactory.styledLabel("DUNGEON ADVENTURE", 42, Color.web("#E4C17A"), true);
        Label subtitle = UiStyleFactory.styledLabel("Il tempio dimenticato", 24, Color.web("#F3E5C8"), false);
        Label message = UiStyleFactory.styledLabel("", 16, Color.web("#E4C17A"), false);

        TextField nameField = new TextField();
        nameField.setPromptText("Nome avventuriero (opzionale)");
        nameField.setAlignment(Pos.CENTER);
        nameField.setMaxWidth(220);
        nameField.setPrefHeight(38);
        nameField.setStyle(
                "-fx-background-color: rgba(25, 19, 13, 0.88);"
                        + "-fx-text-fill: #F3E5C8;"
                        + "-fx-prompt-text-fill: #B8A684;"
                        + "-fx-border-color: #80603C;"
                        + "-fx-border-radius: 6;"
                        + "-fx-background-radius: 6;"
                        + "-fx-font-family: 'Serif';"
                        + "-fx-font-size: 15px;"
        );

        Button newGameButton = createMenuButton("Nuova partita");
        Button loadGameButton = createMenuButton("Carica partita");
        Button exitButton = createMenuButton("Esci");

        newGameButton.setOnAction(event -> {
            try {
                String rawName = nameField.getText() == null ? "" : nameField.getText().trim();
                String playerName = rawName.isEmpty() ? "Esploratore" : rawName;
                Game game = gameManager.startNewGame(playerName);
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

        VBox menu = new VBox(16, title, subtitle, nameField, newGameButton, loadGameButton, exitButton, message);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(30));

        StackPane root = new StackPane(background, overlay, menu);
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(220, 45);
        button.setFont(Font.font("Serif", 18));
        button.setTextFill(UiStyleFactory.CREAM);
        button.setBackground(new Background(
                new BackgroundFill(Color.web("#80603C"), new CornerRadii(8), Insets.EMPTY)
        ));
        return button;
    }

    private void showGameScreen(Game game) {
        GameScreen gameScreen = new GameScreen(this::showMainMenu);
        Scene gameScene = new Scene(gameScreen.createView(game), 900, 600);
        primaryStage.setScene(gameScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}