
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

        // Prima stanza
        Room entrance = new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne e decorazioni blu."
        );

        entrance.addItem(new Item(
                "Frammento di pietra",
                "Un piccolo frammento con inciso un simbolo antico."
        ));

        rooms.add(entrance);

        // Seconda stanza
        Room inscriptions = new Room(
                "Sala delle iscrizioni",
                "Sulle pareti sono incisi antichi simboli. "
                        + "Alcuni sembrano indicare un passaggio segreto."
        );

        rooms.add(inscriptions);

        // Terza stanza
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

        // Quarta stanza
        Room sanctuary = new Room(
                "Santuario nascosto",
                "Una stanza segreta decorata con simboli dorati "
                        + "e antichi tesori."
        );

        rooms.add(sanctuary);

        return new Game(playerName, rooms);
    }

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

    public void collectItems(Game game) {

        Room currentRoom = game.getCurrentRoom();

        List<Item> itemsToCollect = currentRoom.getItems();

        for (Item item : itemsToCollect) {
            game.getPlayer().getInventory().addItem(item);
            currentRoom.removeItem(item);
        }
    }

    public boolean solveCurrentPuzzle(Game game, String answer) {

        Room currentRoom = game.getCurrentRoom();

        Puzzle puzzle = currentRoom.getPuzzle();

        if (puzzle == null) {
            return false;
        }

        if (!game.getPlayer().getInventory()
                .containsItem("Frammento di pietra")) {
            return false;
        }

        return puzzle.checkSolution(answer);
    }

        public GameSaveData createSaveData(Game game) {

            List<String> inventoryItems = new ArrayList<>();

            // Recupera gli oggetti presenti nell'inventario
            for (Item item : game.getPlayer().getInventory().getItems()) {
                inventoryItems.add(item.getName());
            }

            // Recupera lo stato dell'enigma
            Puzzle puzzle = game.getRoom(2).getPuzzle();

            boolean puzzleSolved = puzzle != null && puzzle.isSolved();

            // Crea l'oggetto contenente i dati da salvare
            return new GameSaveData(
                    game.getPlayer().getName(),
                    game.getCurrentRoomIndex(),
                    inventoryItems,
                    puzzleSolved
            );
        }
    // Ricostruisce una partita utilizzando i dati salvati
    public Game restoreGame(GameSaveData saveData) {

        Game game = startNewGame(saveData.getPlayerName());

        // Ripristina la stanza corrente
        game.setCurrentRoomIndex(saveData.getCurrentRoomIndex());

        // Ripristina gli oggetti dell'inventario
        for (String itemName : saveData.getInventoryItems()) {

            for (int i = 0; i < 4; i++) {

                Room room = game.getRoom(i);

                for (Item item : room.getItems()) {

                    if (item.getName().equals(itemName)) {
                        game.getPlayer().getInventory().addItem(item);
                        room.removeItem(item);
                    }
                }
            }
        }

        // Ripristina lo stato dell'enigma
        Puzzle puzzle = game.getRoom(2).getPuzzle();

        if (puzzle != null) {
            puzzle.setSolved(saveData.isPuzzleSolved());
        }

        return game;
    }

    }

