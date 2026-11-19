package it.unicam.cs.mpgc.rpg125904.domain;

public class SkillChallenge implements RoomChallenge {

    private final String title;
    private final String description;
    private final int requiredStrength;
    private final int requiredWisdom;
    private final String bypassItemName;
    private final int xpReward;
    private final int failureDamage;
    private boolean solved;

    public SkillChallenge(
            String title,
            String description,
            int requiredStrength,
            int requiredWisdom,
            String bypassItemName,
            int xpReward,
            int failureDamage
    ) {
        this.title = title;
        this.description = description;
        this.requiredStrength = requiredStrength;
        this.requiredWisdom = requiredWisdom;
        this.bypassItemName = bypassItemName;
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

    @Override
    public boolean isSolved() {
        return solved;
    }

    @Override
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    @Override
    public String getRequiredItemName() {
        return null;
    }

    @Override
    public int getXpReward() {
        return xpReward;
    }

    @Override
    public int getFailureDamage() {
        return failureDamage;
    }

    public int getRequiredStrength() {
        return requiredStrength;
    }

    public int getRequiredWisdom() {
        return requiredWisdom;
    }

    public String getBypassItemName() {
        return bypassItemName;
    }

    public boolean attemptStrength(Player player) {
        if (solved) return true;
        if (player.getStrength() >= requiredStrength) {
            solved = true;
            return true;
        }
        return false;
    }

    public boolean attemptWisdom(Player player) {
        if (solved) return true;
        if (player.getWisdom() >= requiredWisdom) {
            solved = true;
            return true;
        }
        return false;
    }

    public boolean attemptWithItem(Player player) {
        if (solved) return true;
        if (bypassItemName != null && player.getInventory().containsItem(bypassItemName)) {
            solved = true;
            return true;
        }
        return false;
    }
    @Override
    public boolean isBlocking() {
        return false;
    }
}