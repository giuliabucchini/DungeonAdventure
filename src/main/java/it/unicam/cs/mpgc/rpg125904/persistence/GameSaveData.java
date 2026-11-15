
package it.unicam.cs.mpgc.rpg125904.persistence;

import java.util.List;

public class GameSaveData {

    private String playerName;
    private int currentRoomIndex;
    private List<String> inventoryItems;
    private List<Boolean> solvedPuzzles;

    public GameSaveData() {
    }

    public GameSaveData(String playerName, int currentRoomIndex,
                        List<String> inventoryItems,
                        List<Boolean> solvedPuzzles) {

        this.playerName = playerName;
        this.currentRoomIndex = currentRoomIndex;
        this.inventoryItems = inventoryItems;
        this.solvedPuzzles = solvedPuzzles;
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

    public List<Boolean> getSolvedPuzzles() {
        return solvedPuzzles;
    }
}
