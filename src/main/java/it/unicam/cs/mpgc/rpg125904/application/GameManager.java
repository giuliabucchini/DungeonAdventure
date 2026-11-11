
package it.unicam.cs.mpgc.rpg125904.application;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Room;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public Game startNewGame(String playerName) {

        List<Room> rooms = new ArrayList<>();

        // Prima stanza: ingresso del tempio
        Room entrance = new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne e decorazioni blu."
        );

        entrance.addItem(new Item(
                "Frammento di pietra",
                "Un piccolo frammento con inciso un simbolo antico."
        ));

        rooms.add(entrance);

        // Seconda stanza: sala delle iscrizioni
        Room inscriptions = new Room(
                "Sala delle iscrizioni",
                "Sulle pareti sono incisi antichi simboli. "
                        + "Alcuni sembrano indicare un passaggio segreto."
        );

        inscriptions.addItem(new Item(
                "Tavoletta dei geroglifici",
                "Una tavoletta di pietra con antiche iscrizioni."
        ));

        inscriptions.setPuzzle(new Puzzle(
                "L'enigma delle iscrizioni: "
                        + "sorge ogni mattina e illumina il deserto. "
                        + "Che cosa è?",
                "sole"
        ));

        rooms.add(inscriptions);

        // Terza stanza: camera degli enigmi
        Room puzzleRoom = new Room(
                "Camera degli enigmi",
                "Al centro della stanza si trova un antico altare "
                        + "con un misterioso enigma."
        );

        puzzleRoom.setPuzzle(new Puzzle(
                "Per aprire il passaggio devi pronunciare il nome "
                        + "della pietra che custodisce il tempio.",
                "frammento"
        ));

        rooms.add(puzzleRoom);

        // Quarta stanza: santuario nascosto
        Room sanctuary = new Room(
                "Santuario nascosto",
                "Una stanza segreta decorata con simboli dorati "
                        + "e antichi tesori."
        );

        sanctuary.addItem(new Item(
                "Amuleto dorato",
                "Un antico amuleto decorato con simboli egizi."
        ));

        rooms.add(sanctuary);

        return new Game(playerName, rooms);
    }

    // Spostamento nella stanza successiva
    public boolean moveToNextRoom(Game game) {

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

    // Raccolta degli oggetti presenti nella stanza
    public void collectItems(Game game) {

        Room currentRoom = game.getCurrentRoom();

        List<Item> itemsToCollect = currentRoom.getItems();

        for (Item item : itemsToCollect) {
            game.getPlayer().getInventory().addItem(item);
            currentRoom.removeItem(item);
        }
    }

    // Verifica della risposta all'enigma corrente
    public boolean solveCurrentPuzzle(Game game, String answer) {

        Room currentRoom = game.getCurrentRoom();

        Puzzle puzzle = currentRoom.getPuzzle();

        if (puzzle == null) {
            return false;
        }

        // Nella sala delle iscrizioni serve la tavoletta
        if (game.getCurrentRoomIndex() == 1) {

            if (!game.getPlayer().getInventory()
                    .containsItem("Tavoletta dei geroglifici")) {
                return false;
            }
        }

        // Nella camera degli enigmi serve il frammento
        if (game.getCurrentRoomIndex() == 2) {

            if (!game.getPlayer().getInventory()
                    .containsItem("Frammento di pietra")) {
                return false;
            }
        }

        return puzzle.checkSolution(answer);
    }

    // Creazione dei dati da salvare
    public GameSaveData createSaveData(Game game) {

        List<String> inventoryItems = new ArrayList<>();

        for (Item item : game.getPlayer().getInventory().getItems()) {
            inventoryItems.add(item.getName());
        }

        // Stato del secondo enigma
        Puzzle inscriptionsPuzzle = game.getRoom(1).getPuzzle();

        boolean inscriptionsPuzzleSolved =
                inscriptionsPuzzle != null && inscriptionsPuzzle.isSolved();

        // Stato del terzo enigma
        Puzzle puzzle = game.getRoom(2).getPuzzle();

        boolean puzzleSolved = puzzle != null && puzzle.isSolved();

        return new GameSaveData(
                game.getPlayer().getName(),
                game.getCurrentRoomIndex(),
                inventoryItems,
                puzzleSolved,
                inscriptionsPuzzleSolved
        );
    }

    // Ricostruzione di una partita salvata
    public Game restoreGame(GameSaveData saveData) {

        if (saveData == null) {
            throw new IllegalArgumentException("Salvataggio assente.");
        }

        if (saveData.getPlayerName() == null
                || saveData.getPlayerName().isBlank()) {
            throw new IllegalArgumentException("Nome del giocatore non valido.");
        }

        if (saveData.getInventoryItems() == null) {
            throw new IllegalArgumentException("Inventario non valido.");
        }

        Game game = startNewGame(saveData.getPlayerName());

        int roomIndex = saveData.getCurrentRoomIndex();

        if (roomIndex < 0 || roomIndex >= 4) {
            throw new IllegalArgumentException("Stanza non valida.");
        }

        game.setCurrentRoomIndex(roomIndex);

        // Ripristino dell'inventario
        for (String itemName : saveData.getInventoryItems()) {

            if (itemName == null || itemName.isBlank()) {
                throw new IllegalArgumentException("Nome oggetto non valido.");
            }

            boolean itemFound = false;

            for (int i = 0; i < 4; i++) {

                Room room = game.getRoom(i);

                for (Item item : room.getItems()) {

                    if (item.getName().equalsIgnoreCase(itemName)) {

                        game.getPlayer().getInventory().addItem(item);
                        room.removeItem(item);
                        itemFound = true;
                        break;
                    }
                }

                if (itemFound) {
                    break;
                }
            }

            if (!itemFound) {
                throw new IllegalArgumentException(
                        "Oggetto non riconosciuto: " + itemName
                );
            }
        }

        // Ripristino del secondo enigma
        Puzzle inscriptionsPuzzle = game.getRoom(1).getPuzzle();

        if (inscriptionsPuzzle != null) {
            inscriptionsPuzzle.setSolved(
                    saveData.isInscriptionsPuzzleSolved()
            );
        }

        // Ripristino del terzo enigma
        Puzzle puzzle = game.getRoom(2).getPuzzle();

        if (puzzle != null) {
            puzzle.setSolved(saveData.isPuzzleSolved());
        }

        return game;
    }
}
