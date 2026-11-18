
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
                        + "«Quando la notte si ritira, torno a nascere all'orizzonte.\n"
                        + "illumino il deserto;\n"
                        + "Sono il primo custode del tempio. Chi sono?»",
                "sole"
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
                        + "potrai aprire il passaggio."
                        ,
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

    public boolean moveToNextRoom(Game game) {

        if (game == null) return false;

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

    /**
     * Completa l'avventura usando l'Ankh sull'altare del santuario.
     */
    public boolean completeFinale(Game game) {

        if (game == null
                || game.getCurrentRoomIndex() != 4
                || game.isCompleted()
                || !hasItem(game, "Ankh")) {
            return false;
        }

        Puzzle inscriptionsPuzzle = game.getRoom(1).getPuzzle();
        Puzzle altarPuzzle = game.getRoom(2).getPuzzle();

        if (inscriptionsPuzzle == null
                || altarPuzzle == null
                || !inscriptionsPuzzle.isSolved()
                || !altarPuzzle.isSolved()) {
            return false;
        }

        game.setCompleted(true);
        return true;
    }

    public void collectItems(Game game) {

        if (game == null) return;

        for (Item item : game.getCurrentRoom().getItems()) {

            if (!hasItem(game, item.getName())) {
                game.getPlayer().getInventory().addItem(item);
            }

            game.getCurrentRoom().removeItem(item);
        }
    }

    public boolean collectItem(Game game, Item item) {

        if (game == null || item == null) return false;

        if (!game.getCurrentRoom().getItems().contains(item)) {
            return false;
        }

        if (hasItem(game, item.getName())) {
            return false;
        }

        game.getPlayer().getInventory().addItem(item);
        game.getCurrentRoom().removeItem(item);

        return true;
    }

    private boolean hasItem(Game game, String itemName) {

        if (game == null || itemName == null) return false;

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

        int roomIndex = game.getCurrentRoomIndex();

        // L'enigma delle iscrizioni richiede la tavoletta.
        if (roomIndex == 1 && !hasItem(game, HIEROGLYPH_TABLET)) {
            return false;
        }

        // L'enigma dell'altare richiede la scheggia.
        if (roomIndex == 2 && !hasItem(game, SEAL_FRAGMENT)) {
            return false;
        }

        return puzzle.checkSolution(answer);
    }

    public String getPuzzleRequirementMessage(Game game) {

        if (game == null) return "";

        int roomIndex = game.getCurrentRoomIndex();

        if (roomIndex == 1 && !hasItem(game, HIEROGLYPH_TABLET)) {
            return "Prima devi trovare la Tavoletta dei geroglifici.";
        }

        if (roomIndex == 2 && !hasItem(game, SEAL_FRAGMENT)) {
            return "Prima devi recuperare la Scheggia del sigillo nella Sala delle iscrizioni.";
        }

        return "";
    }

    public GameSaveData createSaveData(Game game) {

        List<String> inventoryItems = new ArrayList<>();

        for (Item item : game.getPlayer().getInventory().getItems()) {
            inventoryItems.add(item.getName());
        }

        List<Boolean> solvedPuzzles = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

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
                || saveData.getSolvedPuzzles() == null
                || saveData.getSolvedPuzzles().size() != 5) {

            throw new IllegalArgumentException("Dati di salvataggio non validi.");
        }

        int savedRoomIndex = saveData.getCurrentRoomIndex();

        if (savedRoomIndex < 0 || savedRoomIndex > 4) {
            throw new IllegalArgumentException("La stanza salvata non è valida.");
        }

        Game game = startNewGame(saveData.getPlayerName());

        game.setCurrentRoomIndex(savedRoomIndex);

        for (String savedItemName : saveData.getInventoryItems()) {

            if (savedItemName == null || savedItemName.isBlank()) {
                continue;
            }

            boolean found = false;

            for (int i = 0; i < 5 && !found; i++) {

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
                throw new IllegalArgumentException(
                        "Oggetto salvato non riconosciuto: " + savedItemName
                );
            }
        }

        List<Boolean> solvedPuzzles = saveData.getSolvedPuzzles();

        for (int i = 0; i < 5; i++) {

            Puzzle puzzle = game.getRoom(i).getPuzzle();

            if (puzzle != null) {
                puzzle.setSolved(Boolean.TRUE.equals(solvedPuzzles.get(i)));
            }
        }

        game.setCompleted(saveData.isCompleted());

        return game;
    }
}
