package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Gestisce il pannello di ispezione e le interazioni
 * con gli elementi presenti nelle stanze.
 */
public class InspectionManager {

    private final GameManager gameManager;
    private final GameImageFactory imageFactory;
    private final Supplier<Game> gameSupplier;
    private final Consumer<Game> gameSetter;
    private final StackPane sceneArea;
    private final ImageView background;
    private final Consumer<String> statusSetter;
    private final Runnable refreshScreen;
    private final Button nextButton;

    private final Color gold = Color.web("#E6BD70");
    private final Color cream = Color.web("#F5E8CF");
    private final Color blue = Color.web("#18364A");

    private VBox inspectionPanel;
    private Label inspectionTitle;
    private Label inspectionDescription;
    private ImageView closeupImage;
    private VBox interactionContent;

    public InspectionManager(
            GameManager gameManager,
            GameImageFactory imageFactory,
            Supplier<Game> gameSupplier,
            Consumer<Game> gameSetter,
            StackPane sceneArea,
            ImageView background,
            Consumer<String> statusSetter,
            Runnable refreshScreen,
            Button nextButton
    ) {
        this.gameManager = gameManager;
        this.imageFactory = imageFactory;
        this.gameSupplier = gameSupplier;
        this.gameSetter = gameSetter;
        this.sceneArea = sceneArea;
        this.background = background;
        this.statusSetter = statusSetter;
        this.refreshScreen = refreshScreen;
        this.nextButton = nextButton;
    }

    public VBox createPanel() {
        inspectionPanel = new VBox(8);
        inspectionPanel.setAlignment(Pos.CENTER);
        inspectionPanel.setPadding(new Insets(12));
        inspectionPanel.setMaxWidth(560);
        inspectionPanel.setMaxHeight(440);

        inspectionPanel.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(19, 25, 28, 0.98),
                        new CornerRadii(12),
                        Insets.EMPTY
                )
        ));

        inspectionPanel.setBorder(new Border(
                new BorderStroke(
                        gold,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(12),
                        new BorderWidths(1.2)
                )
        ));

        inspectionTitle = InspectionStyleFactory.styledLabel(
                "",
                19,
                gold,
                true
        );

        closeupImage = new ImageView();
        closeupImage.setFitWidth(210);
        closeupImage.setFitHeight(130);
        closeupImage.setPreserveRatio(true);

        inspectionDescription = InspectionStyleFactory.styledLabel(
                "",
                15,
                cream,
                false
        );

        inspectionDescription.setWrapText(true);
        inspectionDescription.setMaxWidth(500);
        inspectionDescription.setPrefWidth(500);
        inspectionDescription.setAlignment(Pos.CENTER);
        inspectionDescription.setTextAlignment(TextAlignment.CENTER);
        inspectionDescription.setBackground(Background.EMPTY);
        inspectionDescription.setBorder(Border.EMPTY);

        interactionContent = new VBox(10);
        interactionContent.setAlignment(Pos.CENTER);

        Button closeButton = InspectionStyleFactory.createButton(
                "Chiudi",
                blue,
                cream
        );

        closeButton.setOnAction(event -> closeInspection());

        inspectionPanel.getChildren().addAll(
                inspectionTitle,
                closeupImage,
                inspectionDescription,
                interactionContent,
                closeButton
        );

        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);

        return inspectionPanel;
    }

    public VBox getPanel() {
        return inspectionPanel;
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

            Button collectButton = InspectionStyleFactory.createButton(
                    "Raccogli oggetto",
                    Color.web("#79552C"),
                    cream
            );

            collectButton.setOnAction(event -> {
                if (gameManager.collectItem(currentGame, hotspot.item)) {
                    statusSetter.accept(
                            "Hai raccolto: " + hotspot.item.getName()
                    );

                    closeInspection();
                    refreshScreen.run();

                } else {
                    inspectionDescription.setText(
                            "Questo oggetto non è più disponibile."
                    );

                    collectButton.setDisable(true);
                }
            });

            interactionContent.getChildren().add(collectButton);

        } else if (hotspot.puzzle) {
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
                        InspectionStyleFactory.styledLabel(
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

                Label feedback = InspectionStyleFactory.styledLabel(
                        "",
                        12,
                        gold,
                        false
                );

                feedback.setWrapText(true);

                Button solveButton = InspectionStyleFactory.createButton(
                        "Prova a risolvere",
                        blue,
                        cream
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

        } else if (hotspot.finale) {
            showSceneZoom(hotspot);

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

                Button finaleButton = InspectionStyleFactory.createButton(
                        "Inserisci l'Ankh nell'altare",
                        Color.web("#79552C"),
                        cream
                );

                Label finaleFeedback = InspectionStyleFactory.styledLabel(
                        "L'Ankh verrà utilizzato per attivare il sigillo.",
                        13,
                        gold,
                        false
                );

                finaleFeedback.setWrapText(true);

                finaleButton.setOnAction(event -> {
                    if (gameManager.completeFinale(currentGame)) {
                        closeInspection();

                        statusSetter.accept(
                                "Il sigillo si illumina. Hai completato l'avventura!"
                        );

                        refreshScreen.run();
                        showEndingPanel();

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

        } else {
            showSceneZoom(hotspot);
        }

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
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

    public void showEndingPanel() {
        Game currentGame = gameSupplier.get();

        if (inspectionPanel == null
                || currentGame == null
                || !currentGame.isCompleted()) {
            return;
        }

        inspectionTitle.setText("AVVENTURA COMPLETATA");
        closeupImage.setManaged(true);
        closeupImage.setVisible(true);
        closeupImage.setViewport(null);
        closeupImage.setImage(
                imageFactory.loadImage("/images/rooms/ending_ankh_inserted.png")
        );

        closeupImage.setFitWidth(390);
        closeupImage.setFitHeight(220);

        inspectionPanel.setMaxWidth(620);
        inspectionPanel.setMaxHeight(
                sceneArea == null || sceneArea.getHeight() <= 0
                        ? 440
                        : Math.max(220, sceneArea.getHeight() - 16)
        );

        inspectionPanel.setPadding(new Insets(14));
        inspectionPanel.setSpacing(8);

        inspectionDescription.setFont(Font.font("Serif", 14));
        inspectionDescription.setMaxWidth(540);
        inspectionDescription.setPrefWidth(540);

        inspectionDescription.setText(
                "L'Ankh si incastra nell'altare. Una luce attraversa le pareti "
                        + "e il sigillo si risveglia, rivelando il santuario nascosto.\n\n"
                        + "Il tempio non custodiva un tesoro da conquistare, ma una memoria "
                        + "da proteggere. Hai ricomposto gli indizi, superato le prove e "
                        + "raggiunto il cuore del tempio. La tua esplorazione si conclude qui."
        );

        inspectionDescription.setMaxWidth(540);
        inspectionDescription.setPrefWidth(540);
        inspectionDescription.setTextAlignment(TextAlignment.CENTER);
        inspectionDescription.setAlignment(Pos.CENTER);

        interactionContent.getChildren().clear();

        Button restartButton = InspectionStyleFactory.createButton(
                "Ricomincia l'avventura",
                Color.web("#79552C"),
                cream
        );

        restartButton.setOnAction(event -> {
            String playerName = gameSupplier.get().getPlayer().getName();

            gameSetter.accept(gameManager.startNewGame(playerName));

            closeInspection();

            statusSetter.accept(
                    "Una nuova esplorazione sta per cominciare."
            );

            refreshScreen.run();
        });

        interactionContent.getChildren().add(restartButton);

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
    }

    public void showInventoryItem(Item item) {
        inspectionTitle.setText(item.getName());
        inspectionDescription.setText(item.getDescription());

        interactionContent.getChildren().clear();

        ImageView detail = imageFactory.createItemImage(item.getName(), 220);

        closeupImage.setViewport(null);
        closeupImage.setImage(
                detail == null ? null : detail.getImage()
        );

        closeupImage.setFitWidth(220);
        closeupImage.setFitHeight(190);

        inspectionPanel.setManaged(true);
        inspectionPanel.setVisible(true);
        inspectionPanel.toFront();
    }

    public void closeInspection() {
        if (inspectionPanel == null) {
            return;
        }

        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);
    }
}