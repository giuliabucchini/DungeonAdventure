package it.unicam.cs.mpgc.rpg125904.domain;

public interface RoomChallenge {

    String getTitle();

    String getDescription();

    boolean isSolved();

    void setSolved(boolean solved);

    String getRequiredItemName();

    int getXpReward();

    int getFailureDamage();
    default boolean isBlocking() {
        return true;
    }
}