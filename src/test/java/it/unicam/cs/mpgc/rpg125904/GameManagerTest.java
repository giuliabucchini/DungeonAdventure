
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.persistence.GameSaveData;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    @Test
    void shouldNotMoveToNextRoomWhenPuzzleIsNotSolved() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        gameManager.collectItems(game);

        assertTrue(gameManager.moveToNextRoom(game));

        gameManager.collectItems(game);

        boolean moved = gameManager.moveToNextRoom(game);

        assertFalse(moved);

        assertEquals(
                "Sala delle iscrizioni",
                game.getCurrentRoom().getName()
        );
    }

    @Test
    void shouldMoveThroughAllFiveRoomsWhenPuzzlesAreSolved() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        assertEquals("Ingresso del tempio", game.getRoom(0).getName());
        assertEquals("Sala delle iscrizioni", game.getRoom(1).getName());
        assertEquals("Camera degli enigmi", game.getRoom(2).getName());
        assertEquals("Camera segreta blu", game.getRoom(3).getName());
        assertEquals("Corridoio del santuario", game.getRoom(4).getName());

        // Raccoglie la tavoletta nella prima stanza
        gameManager.collectItems(game);

        // Raggiunge la Sala delle iscrizioni
        assertTrue(gameManager.moveToNextRoom(game));

        // Raccoglie la scheggia e gli altri oggetti
        gameManager.collectItems(game);

        // Risolve il primo enigma
        assertTrue(gameManager.solveCurrentPuzzle(game, "sole"));

        // Raggiunge la Camera degli enigmi
        assertTrue(gameManager.moveToNextRoom(game));

        // Risolve il secondo enigma
        assertTrue(
                gameManager.solveCurrentPuzzle(
                        game,
                        "Scheggia del sigillo"
                )
        );

        // Raggiunge la Camera segreta blu
        assertTrue(gameManager.moveToNextRoom(game));

        assertEquals(
                "Camera segreta blu",
                game.getCurrentRoom().getName()
        );

        // Raggiunge il Corridoio del santuario
        assertTrue(gameManager.moveToNextRoom(game));

        assertEquals(
                "Corridoio del santuario",
                game.getCurrentRoom().getName()
        );

        // Non esistono altre stanze
        assertFalse(gameManager.moveToNextRoom(game));
    }

    @Test
    void shouldRestoreGameFromCurrentSaveData() {

        GameSaveData saveData = new GameSaveData(
                "Esploratore",
                2,
                List.of("Scheggia del sigillo"),
                List.of(false, false, true, false, false)
        );

        GameManager gameManager = new GameManager();

        Game game = gameManager.restoreGame(saveData);

        assertEquals("Esploratore", game.getPlayer().getName());

        assertEquals(2, game.getCurrentRoomIndex());

        assertTrue(
                game.getPlayer().getInventory()
                        .containsItem("Scheggia del sigillo")
        );

        assertTrue(game.getRoom(2).getPuzzle().isSolved());

        // La tavoletta iniziale è stata raccolta nella partita salvata
        assertFalse(game.getRoom(0).getItems().isEmpty());
    }

    @Test
    void shouldRestoreGameInFifthRoom() {

        GameSaveData saveData = new GameSaveData(
                "Esploratore",
                4,
                List.of("Scheggia del sigillo"),
                List.of(false, true, true, false, false)
        );

        GameManager gameManager = new GameManager();

        Game game = gameManager.restoreGame(saveData);

        assertEquals(4, game.getCurrentRoomIndex());

        assertEquals(
                "Corridoio del santuario",
                game.getCurrentRoom().getName()
        );

        assertTrue(
                game.getPlayer().getInventory()
                        .containsItem("Scheggia del sigillo")
        );

        assertTrue(game.getRoom(1).getPuzzle().isSolved());
        assertTrue(game.getRoom(2).getPuzzle().isSolved());
    }

    @Test
    void shouldRestoreNewSaveWithFiveRooms() {

        GameManager gameManager = new GameManager();

        Game originalGame = gameManager.startNewGame("Esploratore");

        gameManager.collectItems(originalGame);

        assertTrue(gameManager.moveToNextRoom(originalGame));

        gameManager.collectItems(originalGame);

        assertTrue(
                gameManager.solveCurrentPuzzle(originalGame, "sole")
        );

        GameSaveData saveData =
                gameManager.createSaveData(originalGame);

        Game restoredGame =
                gameManager.restoreGame(saveData);

        assertEquals(1, restoredGame.getCurrentRoomIndex());

        assertTrue(
                restoredGame.getPlayer().getInventory()
                        .containsItem("Scheggia del sigillo")
        );

        assertTrue(
                restoredGame.getPlayer().getInventory()
                        .containsItem("Tavoletta dei geroglifici")
        );

        assertTrue(restoredGame.getRoom(1).getPuzzle().isSolved());

        assertEquals(
                "Corridoio del santuario",
                restoredGame.getRoom(4).getName()
        );
    }
}
