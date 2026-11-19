package it.unicam.cs.mpgc.rpg125904.domain;

import java.util.List;

public class Puzzle implements RoomChallenge {

    private final String title;
    private final String description;
    private final String extraHint;
    private final List<String> validSolutions;
    private final String requiredItemName;
    private final int xpReward;
    private final int failureDamage;
    private boolean solved;

    public Puzzle(String description, String solution) {
        this("Enigma", description, "", List.of(solution), null, 60, 10);
    }

    public Puzzle(String description, List<String> validSolutions, String requiredItemName) {
        this("Enigma", description, "", validSolutions, requiredItemName, 60, 10);
    }

    public Puzzle(
            String title,
            String description,
            String extraHint,
            List<String> validSolutions,
            String requiredItemName,
            int xpReward,
            int failureDamage
    ) {
        this.title = title;
        this.description = description;
        this.extraHint = extraHint != null ? extraHint : "";
        this.validSolutions = List.copyOf(validSolutions);
        this.requiredItemName = requiredItemName;
        this.xpReward = xpReward;
        this.failureDamage = failureDamage;
        this.solved = false;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getExtraHint() {
        return extraHint;
    }

    @Override
    public String getRequiredItemName() {
        return requiredItemName;
    }

    @Override
    public int getXpReward() {
        return xpReward;
    }

    @Override
    public int getFailureDamage() {
        return failureDamage;
    }

    @Override
    public boolean isSolved() {
        return solved;
    }

    @Override
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean checkSolution(String answer) {
        if (answer == null || answer.isBlank()) {
            return false;
        }
        if (solved) {
            return true;
        }

        String normalized = answer.trim();
        for (String valid : validSolutions) {
            if (normalized.equalsIgnoreCase(valid.trim())) {
                solved = true;
                return true;
            }
        }
        return false;
    }
}