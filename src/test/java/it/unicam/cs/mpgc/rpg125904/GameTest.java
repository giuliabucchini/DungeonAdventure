
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.application.GameManager;
import it.unicam.cs.mpgc.rpg125904.domain.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void shouldStartInFirstRoom() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        assertEquals(
                "Ingresso del tempio",
                game.getCurrentRoom().getName()
        );

        assertTrue(game.hasNextRoom());
        assertFalse(game.isCompleted());
    }

    @Test
    void shouldCompleteAdventureAfterLastRoom() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        game.moveToNextRoom();
        game.moveToNextRoom();
        game.moveToNextRoom();

        assertEquals(
                "Santuario nascosto",
                game.getCurrentRoom().getName()
        );

        assertFalse(game.hasNextRoom());
        assertTrue(game.isCompleted());
    }

    @Test
    void shouldNotMoveBeyondLastRoom() {

        GameManager gameManager = new GameManager();
        Game game = gameManager.startNewGame("Esploratore");

        game.moveToNextRoom();
        game.moveToNextRoom();
        game.moveToNextRoom();

        game.moveToNextRoom();

        assertEquals(
                "Santuario nascosto",
                game.getCurrentRoom().getName()
        );

        assertTrue(game.isCompleted());
    }
}
