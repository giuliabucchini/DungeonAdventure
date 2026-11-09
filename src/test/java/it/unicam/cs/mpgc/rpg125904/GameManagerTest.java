
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    @Test
    void shouldNotMoveToNextRoomWhenPuzzleIsNotSolved() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        // Raggiunge la Camera degli enigmi
        gameManager.moveToNextRoom(game);
        gameManager.moveToNextRoom(game);

        // Prova ad avanzare senza risolvere l'enigma
        boolean moved = gameManager.moveToNextRoom(game);

        assertFalse(moved);
        assertEquals(
                "Camera degli enigmi",
                game.getCurrentRoom().getName()
        );
    }

    @Test
    void shouldMoveToNextRoomWhenPuzzleIsSolved() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        // Raccoglie il frammento nella prima stanza
        gameManager.collectItems(game);

        // Raggiunge la Camera degli enigmi
        gameManager.moveToNextRoom(game);
        gameManager.moveToNextRoom(game);

        // Risolve l'enigma
        gameManager.solveCurrentPuzzle(game, "frammento");

        // Prova a raggiungere la stanza successiva
        boolean moved = gameManager.moveToNextRoom(game);

        assertTrue(moved);
        assertEquals(
                "Santuario nascosto",
                game.getCurrentRoom().getName()
        );
    }
}
