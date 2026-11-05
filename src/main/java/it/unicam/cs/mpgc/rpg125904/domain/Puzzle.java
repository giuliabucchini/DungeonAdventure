package it.unicam.cs.mpgc.rpg125904.domain;

public class Puzzle {

    private final String description;
    private final String solution;
    private boolean solved;

    public Puzzle(String description, String solution) {
        this.description = description;
        this.solution = solution;
        this.solved = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean checkSolution(String answer) {
        if (answer.equalsIgnoreCase(solution)) {
            solved = true;
            return true;
        }

        return false;
    }
}