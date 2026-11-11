
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

        // Raggiunge la Sala delle iscrizioni
        gameManager.moveToNextRoom(game);

        // Raccoglie la tavoletta
        gameManager.collectItems(game);

        // Prova ad avanzare senza risolvere l'enigma
        boolean moved = gameManager.moveToNextRoom(game);

        assertFalse(moved);

        assertEquals(
                "Sala delle iscrizioni",
                game.getCurrentRoom().getName()
        );
    }

    @Test
    void shouldMoveToNextRoomWhenPuzzleIsSolved() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        // Raccoglie il frammento nell'ingresso
        gameManager.collectItems(game);

        // Raggiunge la Sala delle iscrizioni
        gameManager.moveToNextRoom(game);

        // Raccoglie la tavoletta
        gameManager.collectItems(game);

        // Risolve il primo enigma
        boolean firstPuzzleSolved =
                gameManager.solveCurrentPuzzle(game, "sole");

        assertTrue(firstPuzzleSolved);

        // Raggiunge la Camera degli enigmi
        gameManager.moveToNextRoom(game);

        // Risolve il secondo enigma
        boolean secondPuzzleSolved =
                gameManager.solveCurrentPuzzle(game, "frammento");

        assertTrue(secondPuzzleSolved);

        // Raggiunge il Santuario nascosto
        boolean moved = gameManager.moveToNextRoom(game);

        assertTrue(moved);

        assertEquals(
                "Santuario nascosto",
                game.getCurrentRoom().getName()
        );
    }

    @Test
    void shouldRestoreGameFromSaveData() {

        GameSaveData saveData = new GameSaveData(
                "Esploratore",
                2,
                List.of("Frammento di pietra"),
                true
        );

        GameManager gameManager = new GameManager();

        Game game = gameManager.restoreGame(saveData);

        assertEquals("Esploratore", game.getPlayer().getName());

        assertEquals(2, game.getCurrentRoomIndex());

        assertTrue(
                game.getPlayer().getInventory()
                        .containsItem("Frammento di pietra")
        );

        assertTrue(game.getRoom(2).getPuzzle().isSolved());

        assertTrue(game.getRoom(0).getItems().isEmpty());
    }
}
