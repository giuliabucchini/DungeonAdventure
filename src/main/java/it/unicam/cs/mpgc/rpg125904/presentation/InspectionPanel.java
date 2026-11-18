
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * Gestisce il pannello di ispezione degli elementi
 * e le interazioni con il giocatore.
 */
public class InspectionPanel {

    private static final String GOLD = "#E6BD70";
    private static final String CREAM = "#F5E8CF";
    private static final String DARK = "rgba(22,17,13,0.94)";

    private final GameManager gameManager;
    private final GameImageFactory imageFactory;

    private final VBox panel;
    private final Label title;
    private final Label description;
    private final ImageView image;
    private final VBox interactionContent;

    private Consumer<Game> onGameUpdated;
    private Runnable onClose;

    public InspectionPanel(
            GameManager gameManager,
            GameImageFactory imageFactory
    ) {
        this.gameManager = gameManager;
        this.imageFactory = imageFactory;

        title = new Label();
        title.setFont(Font.font("Serif", 25));
        title.setTextFill(Color.web(GOLD));
        title.setWrapText(true);

        description = new Label();
        description.setFont(Font.font("Serif", 16));
        description.setTextFill(Color.web(CREAM));
        description.setWrapText(true);
        description.setMaxWidth(430);

        image = new ImageView();
        image.setFitWidth(180);
        image.setFitHeight(150);
        image.setPreserveRatio(true);
        image.setSmooth(true);

        interactionContent = new VBox(10);
        interactionContent.setAlignment(Pos.CENTER);

        Button closeButton = createButton("Chiudi");
        closeButton.setOnAction(event -> close());

        panel = new VBox(
                15,
                title,
                image,
                description,
                interactionContent,
                closeButton
        );

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(24));
        panel.setMaxWidth(500);
        panel.setMaxHeight(520);
        panel.setStyle(
                "-fx-background-color: " + DARK + ";" +
                        "-fx-border-color: " + GOLD + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;"
        );

        panel.setVisible(false);
        panel.setManaged(false);
    }

    public Node getView() {
        return panel;
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setOnGameUpdated(Consumer<Game> callback) {
        this.onGameUpdated = callback;
    }

    public void showItem(Game game, Item item) {
        if (game == null || item == null) {
            return;
        }

        prepare(item.getName(), item.getDescription());

        ImageView itemImage = imageFactory.createItemImage(
                item.getName(),
                180
        );

        setImage(itemImage);

        Button collectButton = createButton("Raccogli oggetto");

        collectButton.setOnAction(event -> {
            if (gameManager.collectItem(game, item)) {
                close();

                if (onGameUpdated != null) {
                    onGameUpdated.accept(game);
                }
            }
        });

        interactionContent.getChildren().add(collectButton);
        show();
    }

    public void showPuzzle(Game game) {
        if (game == null) {
            return;
        }

        Puzzle puzzle = game.getCurrentRoom().getPuzzle();

        if (puzzle == null) {
            return;
        }

        prepare("Enigma", puzzle.getDescription());
        image.setVisible(false);
        image.setManaged(false);

        if (puzzle.isSolved()) {
            description.setText(
                    puzzle.getDescription() +
                            "\n\nIl meccanismo è già stato attivato."
            );
            show();
            return;
        }

        TextField answerField = new TextField();
        answerField.setPromptText("Inserisci la risposta");
        answerField.setMaxWidth(280);

        Label feedback = createFeedbackLabel();

        Button solveButton = createButton("Conferma risposta");

        solveButton.setOnAction(event -> {
            String answer = answerField.getText();

            if (gameManager.solveCurrentPuzzle(game, answer)) {
                feedback.setText("Il meccanismo si è attivato!");
                answerField.setDisable(true);
                solveButton.setDisable(true);

                if (onGameUpdated != null) {
                    onGameUpdated.accept(game);
                }
            } else {
                feedback.setText("La risposta non è corretta.");
            }
        });

        interactionContent.getChildren().addAll(
                answerField,
                solveButton,
                feedback
        );

        show();
    }

    public void showDetail(
            String detailTitle,
            String detailDescription
    ) {
        prepare(detailTitle, detailDescription);
        image.setVisible(false);
        image.setManaged(false);
        show();
    }

    public void showFinale(Game game) {
        if (game == null) {
            return;
        }

        prepare(
                "L'altare del santuario",
                "Un antico altare attende il simbolo della vita eterna."
        );

        image.setVisible(false);
        image.setManaged(false);

        Button activateButton = createButton("Posiziona l'Ankh");
        Label feedback = createFeedbackLabel();

        activateButton.setOnAction(event -> {
            if (gameManager.completeFinale(game)) {
                feedback.setText("Il santuario si è risvegliato!");

                if (onGameUpdated != null) {
                    onGameUpdated.accept(game);
                }

                close();
            } else {
                feedback.setText(
                        "Non è ancora possibile completare il rituale."
                );
            }
        });

        interactionContent.getChildren().addAll(
                activateButton,
                feedback
        );

        show();
    }

    public void close() {
        panel.setVisible(false);
        panel.setManaged(false);

        if (onClose != null) {
            onClose.run();
        }
    }

    private void prepare(String panelTitle, String panelDescription) {
        title.setText(panelTitle);
        description.setText(panelDescription);

        image.setImage(null);
        image.setVisible(true);
        image.setManaged(true);

        interactionContent.getChildren().clear();
    }

    private void setImage(ImageView itemImage) {
        if (itemImage == null) {
            image.setImage(null);
            return;
        }

        image.setImage(itemImage.getImage());
    }

    private void show() {
        panel.setManaged(true);
        panel.setVisible(true);
    }

    private Button createButton(String text) {
        Button button = new Button(text);

        button.setFont(Font.font("Serif", 15));
        button.setTextFill(Color.web(CREAM));
        button.setPadding(new Insets(9, 20, 9, 20));
        button.setStyle(
                "-fx-background-color: #80603C;" +
                        "-fx-background-radius: 7;"
        );

        return button;
    }

    private Label createFeedbackLabel() {
        Label feedback = new Label();
        feedback.setFont(Font.font("Serif", 14));
        feedback.setTextFill(Color.web(GOLD));
        feedback.setWrapText(true);

        return feedback;
    }
}
