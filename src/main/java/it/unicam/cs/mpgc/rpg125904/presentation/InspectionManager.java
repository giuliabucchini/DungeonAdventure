
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

/**
 * Gestisce i pannelli di ispezione e le interazioni con gli elementi
 * presenti nelle stanze.
 */
public class InspectionManager {

    private final GameManager gameManager;
    private final ItemImageFactory itemImageFactory;
    private final BackgroundImageFactory backgroundImageFactory;

    private final Color gold;
    private final Color cream;
    private final Color blue;

    private final Button nextButton;

    private final Consumer<String> statusUpdater;
    private final Runnable refreshScreen;
    private final Consumer<Game> restartGame;

    private final VBox inspectionPanel;
    private final Label inspectionTitle;
    private final Label inspectionDescription;
    private final ImageView closeupImage;
    private final VBox interactionContent;

    public InspectionManager(
            GameManager gameManager,
            ItemImageFactory itemImageFactory,
            BackgroundImageFactory backgroundImageFactory,
            Color gold,
            Color cream,
            Color blue,
            Button nextButton,
            Consumer<String> statusUpdater,
            Runnable refreshScreen,
            Consumer<Game> restartGame) {

        this.gameManager = gameManager;
        this.itemImageFactory = itemImageFactory;
        this.backgroundImageFactory = backgroundImageFactory;

        this.gold = gold;
        this.cream = cream;
        this.blue = blue;

        this.nextButton = nextButton;

        this.statusUpdater = statusUpdater;
        this.refreshScreen = refreshScreen;
        this.restartGame = restartGame;

        inspectionPanel = new VBox(12);
        inspectionPanel.setAlignment(Pos.CENTER);
        inspectionPanel.setPadding(new Insets(18));
        inspectionPanel.setMaxWidth(440);
        inspectionPanel.setStyle(
                "-fx-background-color: rgba(22,17,13,0.94);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #E6BD70;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1.5;"
        );

        inspectionTitle = new Label();
        inspectionTitle.setTextFill(gold);
        inspectionTitle.setFont(
                Font.font("Serif", FontWeight.BOLD, 20)
        );
        inspectionTitle.setWrapText(true);
        inspectionTitle.setTextAlignment(TextAlignment.CENTER);

        closeupImage = new ImageView();
        closeupImage.setPreserveRatio(true);
        closeupImage.setSmooth(true);

        inspectionDescription = new Label();
        inspectionDescription.setTextFill(cream);
        inspectionDescription.setFont(
                Font.font("Serif", 14)
        );
        inspectionDescription.setWrapText(true);
        inspectionDescription.setTextAlignment(TextAlignment.CENTER);
        inspectionDescription.setMaxWidth(390);

        interactionContent = new VBox(10);
        interactionContent.setAlignment(Pos.CENTER);

        Button closeButton = createButton("Chiudi", blue);
        closeButton.setOnAction(event -> close());

        inspectionPanel.getChildren().addAll(
                inspectionTitle,
                closeupImage,
                inspectionDescription,
                interactionContent,
                closeButton
        );

        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);
    }

    /**
     * Restituisce il pannello da inserire nella schermata di gioco.
     */
    public VBox getPanel() {
        return inspectionPanel;
    }

    /**
     * Mostra le informazioni e le interazioni relative a un hotspot.
     */
    public void inspectHotspot(
            Hotspot hotspot,
            Game currentGame,
            ImageView background,
            StackPane sceneArea) {

        inspectionTitle.setText(hotspot.title);
        inspectionTitle.setFont(
                Font.font("Serif", FontWeight.BOLD, 20)
        );

        inspectionDescription.setFont(
                Font.font("Serif", 14)
        );
        inspectionDescription.setMaxWidth(390);

        interactionContent.getChildren().clear();

        inspectionPanel.setMaxWidth(440);
        inspectionPanel.setPadding(new Insets(18));
        inspectionPanel.setSpacing(12);

        closeupImage.setVisible(true);
        closeupImage.setManaged(true);
        closeupImage.setViewport(null);
        closeupImage.setFitWidth(320);
        closeupImage.setFitHeight(220);

        switch (hotspot.type) {
            case ITEM ->
                    showItemInspection(hotspot, currentGame);

            case PUZZLE ->
                    showPuzzleInspection(hotspot, currentGame);

            case FINALE ->
                    showFinaleInspection(
                            hotspot,
                            currentGame,
                            background,
                            sceneArea
                    );

            case DETAIL ->
                    showSceneZoom(hotspot, background);
        }

        inspectionPanel.setVisible(true);
        inspectionPanel.setManaged(true);
        inspectionPanel.toFront();
    }

    /**
     * Gestisce l'ispezione e la raccolta di un oggetto.
     */
    private void showItemInspection(
            Hotspot hotspot,
            Game currentGame) {

        ImageView itemImage = itemImageFactory.create(
                hotspot.item.getName(), 220
        );

        closeupImage.setViewport(null);
        closeupImage.setImage(itemImage.getImage());
        closeupImage.setFitWidth(220);
        closeupImage.setFitHeight(145);

        Button collectButton = createButton(
                "Raccogli oggetto",
                Color.web("#79552C")
        );

        collectButton.setOnAction(event -> {
            if (gameManager.collectItem(currentGame, hotspot.item)) {
                statusUpdater.accept(
                        "Hai raccolto: " + hotspot.item.getName()
                );

                close();
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

    /**
     * Gestisce la visualizzazione e la risoluzione di un enigma.
     */
    private void showPuzzleInspection(
            Hotspot hotspot,
            Game currentGame) {

        closeupImage.setVisible(false);
        closeupImage.setManaged(false);

        inspectionPanel.setMaxWidth(540);
        inspectionPanel.setPadding(
                new Insets(14, 18, 14, 18)
        );
        inspectionPanel.setSpacing(9);

        inspectionDescription.setFont(
                Font.font("Serif", 16)
        );
        inspectionDescription.setMaxWidth(480);
        inspectionDescription.setPrefWidth(480);

        Puzzle puzzle = currentGame.getCurrentRoom().getPuzzle();

        if (puzzle != null) {
            inspectionDescription.setText(
                    puzzle.getDescription()
            );
        }

        if (puzzle != null && puzzle.isSolved()) {
            Label solvedLabel = styledLabel(
                    "Meccanismo già attivato",
                    14,
                    gold,
                    true
            );

            interactionContent.getChildren().add(solvedLabel);
            return;
        }

        TextField answerField = new TextField();
        answerField.setPromptText("La tua risposta...");
        answerField.setMaxWidth(260);

        Label feedback = styledLabel(
                "",
                12,
                gold,
                false
        );
        feedback.setWrapText(true);

        Button solveButton = createButton(
                "Prova a risolvere",
                blue
        );

        solveButton.setOnAction(event -> {
            String answer = answerField.getText();

            if (answer == null || answer.isBlank()) {
                feedback.setText("Inserisci una risposta.");
                return;
            }

            if (gameManager.solveCurrentPuzzle(
                    currentGame,
                    answer.trim())) {

                feedback.setText(
                        "Corretto! Il meccanismo si è attivato."
                );

                statusUpdater.accept(
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

    /**
     * Gestisce l'ispezione dell'altare finale e l'inserimento dell'Ankh.
     */
    private void showFinaleInspection(
            Hotspot hotspot,
            Game currentGame,
            ImageView background,
            StackPane sceneArea) {

        showSceneZoom(hotspot, background);

        if (currentGame.isCompleted()) {
            inspectionDescription.setText(
                    "Il sigillo è già stato attivato. " +
                            "Il tempio è finalmente libero."
            );
            return;
        }

        inspectionDescription.setText(
                "L'altare presenta un incavo a forma di Ankh. " +
                        "Hai già raccolto il simbolo della vita eterna? " +
                        "Premi il pulsante qui sotto per collocarlo al centro " +
                        "dell'altare e attivare il sigillo."
        );

        inspectionDescription.setWrapText(true);
        inspectionDescription.setMaxWidth(440);

        Button insertButton = createButton(
                "Inserisci l'Ankh nell'altare",
                Color.web("#79552C")
        );

        Label feedback = styledLabel(
                "L'Ankh verrà utilizzato per attivare il sigillo.",
                13,
                gold,
                false
        );
        feedback.setWrapText(true);

        insertButton.setOnAction(event -> {
            if (gameManager.completeFinale(currentGame)) {
                close();

                statusUpdater.accept(
                        "Il sigillo si illumina. Hai completato l'avventura!"
                );

                refreshScreen.run();
                showEndingPanel(currentGame, sceneArea);

            } else {
                feedback.setText(
                        "Non hai ancora l'Ankh. Esamina il corridoio " +
                                "e raccogli il simbolo della vita eterna."
                );
            }
        });

        interactionContent.getChildren().addAll(
                insertButton,
                feedback
        );
    }

    /**
     * Mostra un dettaglio ingrandito dello sfondo della stanza.
     */
    private void showSceneZoom(
            Hotspot hotspot,
            ImageView background) {

        Image image = background.getImage();

        if (image == null) {
            closeupImage.setImage(null);
            return;
        }

        Rectangle2D viewport = background.getViewport();

        if (viewport == null) {
            viewport = new Rectangle2D(
                    0, 0, image.getWidth(), image.getHeight()
            );
        }

        double cropWidth = viewport.getWidth() * 0.42;
        double cropHeight = viewport.getHeight() * 0.42;

        double centerX = viewport.getMinX()
                + hotspot.x * viewport.getWidth();

        double centerY = viewport.getMinY()
                + hotspot.y * viewport.getHeight();

        double cropX = centerX - cropWidth / 2;
        double cropY = centerY - cropHeight / 2;

        cropX = Math.max(
                viewport.getMinX(),
                Math.min(cropX, viewport.getMaxX() - cropWidth)
        );

        cropY = Math.max(
                viewport.getMinY(),
                Math.min(cropY, viewport.getMaxY() - cropHeight)
        );

        closeupImage.setImage(image);
        closeupImage.setViewport(
                new Rectangle2D(
                        cropX,
                        cropY,
                        cropWidth,
                        cropHeight
                )
        );

        closeupImage.setFitWidth(320);
        closeupImage.setFitHeight(220);
    }

    /**
     * Mostra un oggetto selezionato dall'inventario.
     */
    public void showInventoryItem(Item item) {
        inspectionTitle.setText(item.getName());

        inspectionDescription.setText(
                item.getDescription()
        );

        interactionContent.getChildren().clear();

        ImageView itemImage = itemImageFactory.create(
                item.getName(), 220
        );

        closeupImage.setImage(itemImage.getImage());
        closeupImage.setViewport(null);
        closeupImage.setFitWidth(220);
        closeupImage.setFitHeight(190);
        closeupImage.setVisible(true);
        closeupImage.setManaged(true);

        inspectionPanel.setVisible(true);
        inspectionPanel.setManaged(true);
        inspectionPanel.toFront();
    }

    /**
     * Mostra il pannello conclusivo dell'avventura.
     */
    public void showEndingPanel(
            Game currentGame,
            StackPane sceneArea) {

        if (currentGame == null || !currentGame.isCompleted()) {
            return;
        }

        inspectionTitle.setText("AVVENTURA COMPLETATA");
        inspectionTitle.setFont(
                Font.font("Serif", FontWeight.BOLD, 22)
        );

        Image endingImage = backgroundImageFactory.load(
                "/images/rooms/ending_ankh_inserted.png"
        );

        closeupImage.setImage(endingImage);
        closeupImage.setViewport(null);
        closeupImage.setFitWidth(390);
        closeupImage.setFitHeight(220);
        closeupImage.setVisible(true);
        closeupImage.setManaged(true);

        inspectionPanel.setMaxWidth(620);
        inspectionPanel.setMaxHeight(
                sceneArea.getHeight() > 0
                        ? sceneArea.getHeight()
                        : 440
        );
        inspectionPanel.setPadding(new Insets(14));
        inspectionPanel.setSpacing(8);

        inspectionDescription.setFont(
                Font.font("Serif", 14)
        );
        inspectionDescription.setMaxWidth(540);
        inspectionDescription.setPrefWidth(540);
        inspectionDescription.setText(
                "L'Ankh si incastra nell'altare. Una luce attraversa " +
                        "le pareti e il sigillo si risveglia, rivelando il " +
                        "santuario nascosto.\n\n" +
                        "Il tempio non custodiva un tesoro da conquistare, " +
                        "ma una memoria da proteggere. Hai ricomposto gli " +
                        "indizi, superato le prove e raggiunto il cuore del " +
                        "tempio. La tua esplorazione si conclude qui."
        );

        interactionContent.getChildren().clear();

        Button restartButton = createButton(
                "Ricomincia l'avventura",
                Color.web("#79552C")
        );

        restartButton.setOnAction(event -> {
            String playerName = currentGame.getPlayer().getName();

            Game newGame = gameManager.startNewGame(playerName);

            close();
            restartGame.accept(newGame);

            statusUpdater.accept(
                    "Una nuova esplorazione sta per cominciare."
            );

            refreshScreen.run();
        });

        interactionContent.getChildren().add(restartButton);

        inspectionPanel.setVisible(true);
        inspectionPanel.setManaged(true);
        inspectionPanel.toFront();
    }

    /**
     * Chiude il pannello di ispezione.
     */
    public void close() {
        inspectionPanel.setVisible(false);
        inspectionPanel.setManaged(false);
    }

    private Button createButton(String text, Color color) {
        Button button = new Button(text);

        button.setFont(
                Font.font("Serif", FontWeight.BOLD, 13)
        );
        button.setTextFill(cream);
        button.setPadding(
                new Insets(9, 13, 9, 13)
        );
        button.setStyle(
                "-fx-background-color: " + toHex(color) + ";" +
                        "-fx-background-radius: 8;"
        );
        button.setCursor(Cursor.HAND);

        return button;
    }

    private Label styledLabel(
            String text,
            double size,
            Color color,
            boolean bold) {

        Label label = new Label(text);

        label.setFont(
                bold
                        ? Font.font("Serif", FontWeight.BOLD, size)
                        : Font.font("Serif", size)
        );

        label.setTextFill(color);

        return label;
    }

    private String toHex(Color color) {
        return String.format(
                "#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }
}
