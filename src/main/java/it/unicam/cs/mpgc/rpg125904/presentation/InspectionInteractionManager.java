
package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Gestisce le interazioni con gli hotspot.
 */
public class InspectionInteractionManager {

    private final GameManager gameManager;
    private final GameImageFactory imageFactory;
    private final Supplier<Game> gameSupplier;
    private final StackPane sceneArea;
    private final ImageView background;
    private final Consumer<String> statusSetter;
    private final Runnable refreshScreen;
    private final Runnable closeInspection;
    private final Runnable showEndingPanel;
    private final Button nextButton;

    private final VBox inspectionPanel;
    private final Label inspectionTitle;
    private final Label inspectionDescription;
    private final ImageView closeupImage;
    private final VBox interactionContent;

    private final BiFunction<String, Color, Button> buttonFactory;    private final StyledLabelFactory labelFactory;

    private final Color gold = Color.web("#E6BD70");
    private final Color blue = Color.web("#18364A");

    public InspectionInteractionManager(
            GameManager gameManager,
            GameImageFactory imageFactory,
            Supplier<Game> gameSupplier,
            StackPane sceneArea,
            ImageView background,
            Consumer<String> statusSetter,
            Runnable refreshScreen,
            Runnable closeInspection,
            Runnable showEndingPanel,
            Button nextButton,
            VBox inspectionPanel,
            Label inspectionTitle,
            Label inspectionDescription,
            ImageView closeupImage,
            VBox interactionContent,
            BiFunction<String, Color, Button> buttonFactory,            StyledLabelFactory labelFactory
    ) {
        this.gameManager = gameManager;
        this.imageFactory = imageFactory;
        this.gameSupplier = gameSupplier;
        this.sceneArea = sceneArea;
        this.background = background;
        this.statusSetter = statusSetter;
        this.refreshScreen = refreshScreen;
        this.closeInspection = closeInspection;
        this.showEndingPanel = showEndingPanel;
        this.nextButton = nextButton;
        this.inspectionPanel = inspectionPanel;
        this.inspectionTitle = inspectionTitle;
        this.inspectionDescription = inspectionDescription;
        this.closeupImage = closeupImage;
        this.interactionContent = interactionContent;
        this.buttonFactory = buttonFactory;
        this.labelFactory = labelFactory;
    }

    public void inspectHotspot(HotspotManager.Hotspot hotspot) {
        Game currentGame = gameSupplier.get();

        inspectionTitle.setText(hotspot.title);
        inspectionDescription.setText(hotspot.description);
        inspectionDescription.setFont(Font.font("Serif", 15));
        inspectionDescription.setMaxWidth(500);
        inspectionDescription.setPrefWidth(500);
        inspectionDescription.setTextAlignment(TextAlignment.CENTER);
        inspectionDescription.setAlignment(Pos.CENTER);
        closeupImage.setManaged(true);
        closeupImage.setVisible(true);
        inspectionPanel.setMaxWidth(560);
        inspectionPanel.setPadding(new Insets(12));
        inspectionPanel.setSpacing(8);

        if (sceneArea != null && sceneArea.getHeight() > 0) {
            inspectionPanel.setMaxHeight(
                    Math.max(190, sceneArea.getHeight() - 16)
            );
        }

        interactionContent.getChildren().clear();

        if (hotspot.item != null) {
            showItemInteraction(currentGame, hotspot);

        } else if (hotspot.puzzle) {
            showPuzzleInteraction(currentGame);

        } else if (hotspot.finale) {
            showSceneZoom(hotspot);
            showFinaleInteraction(currentGame);

        } else {
            showSceneZoom(hotspot);
        }

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
    }

    private void showItemInteraction(
            Game currentGame,
            HotspotManager.Hotspot hotspot
    ) {
        ImageView itemImage = imageFactory.createItemImage(
                hotspot.item.getName(),
                220
        );

        closeupImage.setViewport(null);
        closeupImage.setImage(
                itemImage == null ? null : itemImage.getImage()
        );

        closeupImage.setVisible(true);
        closeupImage.setManaged(true);
        closeupImage.setPreserveRatio(true);
        closeupImage.setSmooth(true);
        closeupImage.setFitWidth(220);
        closeupImage.setFitHeight(145);

        Button collectButton = buttonFactory.apply(
                "Raccogli oggetto",
                Color.web("#79552C")
        );

        collectButton.setOnAction(event -> {
            if (gameManager.collectItem(currentGame, hotspot.item)) {
                statusSetter.accept(
                        "Hai raccolto: " + hotspot.item.getName()
                );

                closeInspection.run();
                refreshScreen.run();

            } else {
                inspectionDescription.setText(
                        "Questo oggetto non è più disponibile."
                );

                collectButton.setDisable(true);
            }
        });

        interactionContent.getChildren().add(collectButton);
    }

    private void showPuzzleInteraction(Game currentGame) {
        closeupImage.setImage(null);
        closeupImage.setViewport(null);
        closeupImage.setManaged(false);
        closeupImage.setVisible(false);
        inspectionPanel.setMaxWidth(540);
        inspectionPanel.setPadding(new Insets(14, 18, 14, 18));
        inspectionPanel.setSpacing(9);
        inspectionDescription.setFont(Font.font("Serif", 16));
        inspectionDescription.setMaxWidth(480);
        inspectionDescription.setPrefWidth(480);

        Puzzle puzzle = currentGame.getCurrentRoom().getPuzzle();

        if (puzzle != null && puzzle.isSolved()) {
            interactionContent.getChildren().add(
                    labelFactory.create(
                            "Meccanismo già attivato",
                            14,
                            gold,
                            true
                    )
            );

        } else {
            TextField answerField = new TextField();
            answerField.setPromptText("La tua risposta...");
            answerField.setMaxWidth(260);

            Label feedback = labelFactory.create(
                    "",
                    12,
                    gold,
                    false
            );

            feedback.setWrapText(true);

            Button solveButton = buttonFactory.apply(
                    "Prova a risolvere",
                    blue
            );

            solveButton.setOnAction(event -> {
                if (answerField.getText().isBlank()) {
                    feedback.setText("Inserisci una risposta.");
                    return;
                }

                boolean solved = gameManager.solveCurrentPuzzle(
                        currentGame,
                        answerField.getText().trim()
                );

                if (solved) {
                    feedback.setText(
                            "Corretto! Il meccanismo si è attivato."
                    );

                    statusSetter.accept(
                            "Hai risolto l'enigma: il passaggio è sbloccato."
                    );

                    solveButton.setDisable(true);
                    answerField.setDisable(true);

                    nextButton.setDisable(
                            currentGame.isCompleted()
                                    || !currentGame.hasNextRoom()
                    );

                } else {
                    feedback.setText(
                            "Non è la risposta giusta. Riprova."
                    );
                }
            });

            interactionContent.getChildren().addAll(
                    answerField,
                    solveButton,
                    feedback
            );
        }

        if (puzzle != null) {
            inspectionDescription.setText(
                    puzzle.getDescription()
            );
        }
    }

    private void showFinaleInteraction(Game currentGame) {
        if (currentGame.isCompleted()) {
            inspectionDescription.setText(
                    "Il sigillo è già stato attivato. Il tempio è finalmente libero."
            );

        } else {
            inspectionDescription.setText(
                    "L'altare presenta un incavo a forma di Ankh. Hai già raccolto "
                            + "il simbolo della vita eterna? Premi il pulsante qui sotto "
                            + "per collocarlo al centro dell'altare e attivare il sigillo."
            );
            inspectionDescription.setWrapText(true);
            inspectionDescription.setMaxWidth(440);

            Button finaleButton = buttonFactory.apply(
                    "Inserisci l'Ankh nell'altare",
                    Color.web("#79552C")
            );

            Label finaleFeedback = labelFactory.create(
                    "L'Ankh verrà utilizzato per attivare il sigillo.",
                    13,
                    gold,
                    false
            );
            finaleFeedback.setWrapText(true);

            finaleButton.setOnAction(event -> {
                if (gameManager.completeFinale(currentGame)) {
                    closeInspection.run();
                    statusSetter.accept(
                            "Il sigillo si illumina. Hai completato l'avventura!"
                    );
                    refreshScreen.run();
                    showEndingPanel.run();
                } else {
                    finaleFeedback.setText(
                            "Non hai ancora l'Ankh. Esamina il corridoio e raccogli il simbolo della vita eterna."
                    );
                }
            });

            interactionContent.getChildren().addAll(
                    finaleButton,
                    finaleFeedback
            );
        }
    }

    private void showSceneZoom(HotspotManager.Hotspot hotspot) {
        Image image = background.getImage();

        if (image == null) {
            closeupImage.setImage(null);
            closeupImage.setViewport(null);
            return;
        }

        Rectangle2D visibleArea = background.getViewport();

        if (visibleArea == null) {
            visibleArea = new Rectangle2D(
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight()
            );
        }

        double cropWidth = visibleArea.getWidth() * 0.42;
        double cropHeight = visibleArea.getHeight() * 0.42;

        double centerX = visibleArea.getMinX()
                + hotspot.x * visibleArea.getWidth();

        double centerY = visibleArea.getMinY()
                + hotspot.y * visibleArea.getHeight();

        double cropX = centerX - cropWidth / 2;
        double cropY = centerY - cropHeight / 2;

        cropX = Math.max(
                0,
                Math.min(cropX, image.getWidth() - cropWidth)
        );

        cropY = Math.max(
                0,
                Math.min(cropY, image.getHeight() - cropHeight)
        );

        closeupImage.setImage(image);

        closeupImage.setViewport(new Rectangle2D(
                cropX,
                cropY,
                cropWidth,
                cropHeight
        ));

        closeupImage.setFitWidth(320);
        closeupImage.setFitHeight(220);
    }

    @FunctionalInterface
    public interface StyledLabelFactory {
        Label create(
                String text,
                double size,
                Color color,
                boolean bold
        );
    }
}
