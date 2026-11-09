
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Puzzle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleTest {

    @Test
    void shouldSolvePuzzleWithCorrectAnswer() {

        Puzzle puzzle = new Puzzle(
                "Qual è il nome della pietra?",
                "frammento"
        );

        assertTrue(puzzle.checkSolution("frammento"));
        assertTrue(puzzle.isSolved());
    }

    @Test
    void shouldNotSolvePuzzleWithWrongAnswer() {

        Puzzle puzzle = new Puzzle(
                "Qual è il nome della pietra?",
                "frammento"
        );

        assertFalse(puzzle.checkSolution("chiave"));
        assertFalse(puzzle.isSolved());
    }

    @Test
    void shouldAcceptAnswerWithDifferentUppercase() {

        Puzzle puzzle = new Puzzle(
                "Qual è il nome della pietra?",
                "frammento"
        );

        assertTrue(puzzle.checkSolution("FRAMMENTO"));
    }
}
