package it.unicam.cs.mpgc.rpg125904.application;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;
import it.unicam.cs.mpgc.rpg125904.domain.Room;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private static final String SEAL_FRAGMENT = "Scheggia del sigillo";
    private static final String HIEROGLYPH_TABLET = "Tavoletta dei geroglifici";

    public record ArchaeologistEvaluation(
            String rankTitle,
            int collectedRelics,
            int totalRelics,
            String summary
    ) {
    }

    public Game startNewGame(String playerName) {
        List<Room> rooms = new ArrayList<>();

        // STANZA 1 - INGRESSO DEL TEMPIO
        Room entrance = new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne e statue guardiane. "
                        + "Il silenzio del tempio nasconde qualcosa."
        );
        entrance.addItem(new Item(
                HIEROGLYPH_TABLET,
                "Una tavoletta ricoperta di geroglifici. Potrebbe aiutarti a interpretare le iscrizioni."
        ));
        rooms.add(entrance);

        // STANZA 2 - SALA DELLE ISCRIZIONI
        Room inscriptions = new Room(
                "Sala delle iscrizioni",
                "Le pareti sono ricoperte di simboli antichi. "
                        + "Alcune iscrizioni sembrano indicare un passaggio segreto."
        );
        inscriptions.addItem(new Item(
                SEAL_FRAGMENT,
                "Una scheggia di pietra incisa con il simbolo del sigillo che protegge il tempio."
        ));
        inscriptions.addItem(new Item(
                "Pergamena antica",
                "Una pergamena ingiallita che contiene annotazioni su un antico rituale."
        ));
        inscriptions.addItem(new Item(
                "Anfora rituale",
                "Una piccola anfora decorata con simboli egizi."
        ));
        inscriptions.addItem(new Item(
                "Scarabeo dorato",
                "Un piccolo scarabeo d'oro, simbolo di rinascita e protezione."
        ));
        inscriptions.setPuzzle(new Puzzle(
                "La tavoletta ti permette di leggere l'iscrizione completa:\n\n"
                        + "«Quando la notte si ritira, torno a nascere all'orizzonte,\n"
                        + "illumino il deserto;\n"
                        + "sono il primo custode del tempio. Chi sono?»",
                List.of("sole", "il sole"),
                HIEROGLYPH_TABLET
        ));
        rooms.add(inscriptions);

        // STANZA 3 - CAMERA DEGLI ENIGMI
        Room puzzleChamber = new Room(
                "Camera degli enigmi",
                "Al centro della stanza si trova un antico altare. "
                        + "Sulla parete sono incisi simboli che sembrano reagire alla luce."
        );
        puzzleChamber.setPuzzle(new Puzzle(
                "Sull'altare compare una seconda iscrizione:\n\n"
                        + "«Il mio riflesso blu catturerà il tuo sguardo,\n"
                        + "ma solo pronunciando il mio nome\n"
                        + "potrai aprire il passaggio.»",
                List.of(SEAL_FRAGMENT, "scheggia", "sigillo"),
                SEAL_FRAGMENT
        ));
        rooms.add(puzzleChamber);

        // STANZA 4 - CAMERA SEGRETA BLU
        Room secretChamber = new Room(
                "Camera segreta blu",
                "Una stanza nascosta, illuminata da una misteriosa luce blu. "
                        + "Vecchi bauli e oggetti dimenticati occupano l'ambiente."
        );
        secretChamber.addItem(new Item(
                "Chiave antica",
                "Una chiave di metallo consumata dal tempo. Potrebbe aprire qualcosa nel tempio."
        ));
        secretChamber.addItem(new Item(
                "Corda",
                "Una corda robusta, apparentemente ancora utilizzabile."
        ));
        secretChamber.addItem(new Item(
                "Lanterna",
                "Una lanterna antica che potrebbe illuminare gli angoli più bui."
        ));
        rooms.add(secretChamber);

        // STANZA 5 - CORRIDOIO DEL SANTUARIO
        Room sanctuaryCorridor = new Room(
                "Corridoio del santuario",
                "Un corridoio monumentale conduce verso una grande porta decorata "
                        + "con simboli dorati e figure sacre."
        );
        sanctuaryCorridor.addItem(new Item(
                "Ankh",
                "Il simbolo egizio della vita eterna, scolpito in un piccolo oggetto."
        ));
        sanctuaryCorridor.addItem(new Item(
                "Pugnale cerimoniale",
                "Un antico pugnale decorato. Sembra avere un significato rituale."
        ));
        rooms.add(sanctuaryCorridor);

        return new Game(playerName, rooms);
    }

    public ArchaeologistEvaluation evaluateEnding(Game game) {
        int collected = game.getPlayer().getInventory().getItems().size();
        int total = collected;
        for (int i = 0; i < game.getRoomCount(); i++) {
            total += game.getRoom(i).getItems().size();
        }

        String rankTitle;
        String summary;
        if (collected >= total) {
            rankTitle = "Gran Maestro Archeologo del Tempio";
            summary = "Non hai lasciato indietro nemmeno una traccia: hai recuperato tutte le "
                    + total + " reliquie custodite nelle sale del santuario.";
        } else if (collected >= 7) {
            rankTitle = "Esploratore Esperto delle Antiche Rovine";
            summary = "Hai esplorato il tempio con grande attenzione, riportando alla luce "
                    + "quasi tutti i manufatti nascosti nelle camere.";
        } else if (collected >= 4) {
            rankTitle = "Cercatore di Reliquie";
            summary = "Hai superato le prove del santuario, ma alcune sale custodiscono "
                    + "ancora oggetti e iscrizioni da scoprire.";
        } else {
            rankTitle = "Viaggiatore Frettoloso";
            summary = "Hai raggiunto subito l'altare finale, lasciando però quasi tutti "
                    + "i tesori del tempio nascosti nell'ombra.";
        }

        return new ArchaeologistEvaluation(rankTitle, collected, total, summary);
    }

    public boolean moveToNextRoom(Game game) {
        if (game == null) {
            return false;
        }
        Puzzle puzzle = game.getCurrentRoom().getPuzzle();
        if (puzzle != null && !puzzle.isSolved()) {
            return false;
        }
        if (!game.hasNextRoom()) {
            return false;
        }
        game.moveToNextRoom();
        return true;
    }

    public boolean moveToPreviousRoom(Game game) {
        if (game == null || !game.hasPreviousRoom()) {
            return false;
        }
        game.moveToPreviousRoom();
        return true;
    }

    public boolean completeFinale(Game game) {
        if (game == null
                || game.hasNextRoom()
                || game.isCompleted()
                || !hasItem(game, "Ankh")) {
            return false;
        }

        for (int i = 0; i < game.getRoomCount(); i++) {
            Puzzle puzzle = game.getRoom(i).getPuzzle();
            if (puzzle != null && !puzzle.isSolved()) {
                return false;
            }
        }

        game.setCompleted(true);
        return true;
    }

    public void collectItems(Game game) {
        if (game == null) {
            return;
        }
        for (Item item : game.getCurrentRoom().getItems()) {
            if (!hasItem(game, item.getName())) {
                game.getPlayer().getInventory().addItem(item);
            }
            game.getCurrentRoom().removeItem(item);
        }
    }

    public boolean collectItem(Game game, Item item) {
        if (game == null || item == null) {
            return false;
        }
        if (!game.getCurrentRoom().getItems().contains(item) || hasItem(game, item.getName())) {
            return false;
        }
        game.getPlayer().getInventory().addItem(item);
        game.getCurrentRoom().removeItem(item);
        return true;
    }

    private boolean hasItem(Game game, String itemName) {
        if (game == null || itemName == null) {
            return false;
        }
        for (Item item : game.getPlayer().getInventory().getItems()) {
            if (item.getName().trim().equalsIgnoreCase(itemName.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean solveCurrentPuzzle(Game game, String answer) {
        if (game == null || answer == null || answer.isBlank()) {
            return false;
        }
        Puzzle puzzle = game.getCurrentRoom().getPuzzle();
        if (puzzle == null || puzzle.isSolved()) {
            return false;
        }

        String requiredItem = puzzle.getRequiredItemName();
        if (requiredItem != null && !hasItem(game, requiredItem)) {
            return false;
        }

        return puzzle.checkSolution(answer);
    }

    public String getPuzzleRequirementMessage(Game game) {
        if (game == null) {
            return "";
        }
        Puzzle puzzle = game.getCurrentRoom().getPuzzle();
        if (puzzle == null) {
            return "";
        }
        String requiredItem = puzzle.getRequiredItemName();
        if (requiredItem != null && !hasItem(game, requiredItem)) {
            return "Ti manca un oggetto necessario: " + requiredItem + ".";
        }
        return "";
    }

    public GameSaveData createSaveData(Game game) {
        List<String> inventoryItems = new ArrayList<>();
        for (Item item : game.getPlayer().getInventory().getItems()) {
            inventoryItems.add(item.getName());
        }

        List<Boolean> solvedPuzzles = new ArrayList<>();
        for (int i = 0; i < game.getRoomCount(); i++) {
            Puzzle puzzle = game.getRoom(i).getPuzzle();
            solvedPuzzles.add(puzzle != null && puzzle.isSolved());
        }

        return new GameSaveData(
                game.getPlayer().getName(),
                game.getCurrentRoomIndex(),
                inventoryItems,
                solvedPuzzles,
                game.isCompleted()
        );
    }

    public Game restoreGame(GameSaveData saveData) {
        if (saveData == null
                || saveData.getPlayerName() == null
                || saveData.getPlayerName().isBlank()
                || saveData.getInventoryItems() == null
                || saveData.getSolvedPuzzles() == null) {
            throw new IllegalArgumentException("Dati di salvataggio non validi.");
        }

        Game game = startNewGame(saveData.getPlayerName());
        int roomCount = game.getRoomCount();

        if (saveData.getSolvedPuzzles().size() != roomCount) {
            throw new IllegalArgumentException("Dati di salvataggio non validi.");
        }

        int savedRoomIndex = saveData.getCurrentRoomIndex();
        if (savedRoomIndex < 0 || savedRoomIndex >= roomCount) {
            throw new IllegalArgumentException("La stanza salvata non è valida.");
        }

        game.setCurrentRoomIndex(savedRoomIndex);

        for (String savedItemName : saveData.getInventoryItems()) {
            if (savedItemName == null || savedItemName.isBlank()) {
                continue;
            }

            boolean found = false;
            for (int i = 0; i < roomCount && !found; i++) {
                Room room = game.getRoom(i);
                for (Item item : room.getItems()) {
                    if (item.getName().trim().equalsIgnoreCase(savedItemName.trim())) {
                        game.getPlayer().getInventory().addItem(item);
                        room.removeItem(item);
                        found = true;
                        break;
                    }
                }
            }

            if (!found && !hasItem(game, savedItemName)) {
                throw new IllegalArgumentException("Oggetto salvato non riconosciuto: " + savedItemName);
            }
        }

        List<Boolean> solvedPuzzles = saveData.getSolvedPuzzles();
        for (int i = 0; i < roomCount; i++) {
            Puzzle puzzle = game.getRoom(i).getPuzzle();
            if (puzzle != null) {
                puzzle.setSolved(Boolean.TRUE.equals(solvedPuzzles.get(i)));
            }
        }

        game.setCompleted(saveData.isCompleted());
        return game;
    }
}