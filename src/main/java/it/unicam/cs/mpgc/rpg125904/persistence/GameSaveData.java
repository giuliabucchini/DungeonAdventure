
package it.unicam.cs.mpgc.rpg125904.persistence;

import java.util.List;

public class GameSaveData {

    private String playerName;
    private int currentRoomIndex;
    private List<String> inventoryItems;
    private boolean puzzleSolved;

    public GameSaveData() {
    }

    public GameSaveData(String playerName, int currentRoomIndex,
                        List<String> inventoryItems, boolean puzzleSolved) {
        this.playerName = playerName;
        this.currentRoomIndex = currentRoomIndex;
        this.inventoryItems = inventoryItems;
        this.puzzleSolved = puzzleSolved;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public List<String> getInventoryItems() {
        return inventoryItems;
    }

    public boolean isPuzzleSolved() {
        return puzzleSolved;
    }
}
