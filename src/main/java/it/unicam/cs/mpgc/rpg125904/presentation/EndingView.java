
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Gestisce la schermata conclusiva dell'avventura.
 */
public class EndingView {

    private static final String GOLD = "#E6BD70";
    private static final String CREAM = "#F5E8CF";
    private static final String DARK = "rgba(22,17,13,0.94)";

    private final VBox view;
    private final GameImageFactory imageFactory;

    private Consumer<Game> onRestart;

    public EndingView(GameImageFactory imageFactory) {
        this.imageFactory = imageFactory;

        Label title = new Label("AVVENTURA COMPLETATA");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 30));
        title.setTextFill(Color.web(GOLD));

        ImageView endingImage = new ImageView(
                imageFactory.loadImage(
                        "/images/rooms/ending_ankh_inserted.png"
                )
        );

        endingImage.setFitWidth(300);
        endingImage.setFitHeight(200);
        endingImage.setPreserveRatio(true);
        endingImage.setSmooth(true);

        Label narrative = new Label(
                "L'Ankh si incastra nell'altare e una luce dorata " +
                        "attraversa le pareti del tempio.\n\n" +
                        "Il passaggio si apre. Il mistero del tempio dimenticato " +
                        "è finalmente svelato."
        );

        narrative.setFont(Font.font("Serif", 17));
        narrative.setTextFill(Color.web(CREAM));
        narrative.setWrapText(true);
        narrative.setMaxWidth(480);
        narrative.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Nuova avventura");
        restartButton.setFont(Font.font("Serif", 16));
        restartButton.setTextFill(Color.web(CREAM));
        restartButton.setPadding(new Insets(10, 24, 10, 24));
        restartButton.setStyle(
                "-fx-background-color: #80603C;" +
                        "-fx-background-radius: 8;"
        );

        restartButton.setOnAction(event -> {
            if (onRestart != null) {
                onRestart.accept(null);
            }
        });

        view = new VBox(
                20,
                title,
                endingImage,
                narrative,
                restartButton
        );

        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(30));
        view.setMaxWidth(620);
        view.setStyle(
                "-fx-background-color: " + DARK + ";" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;"
        );

        view.setVisible(false);
        view.setManaged(false);
    }

    public Node getView() {
        return view;
    }

    public void setOnRestart(Consumer<Game> onRestart) {
        this.onRestart = onRestart;
    }

    public void show() {
        view.setManaged(true);
        view.setVisible(true);
    }

    public void hide() {
        view.setVisible(false);
        view.setManaged(false);
    }
}
