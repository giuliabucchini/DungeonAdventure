
package it.unicam.cs.mpgc.rpg125904.persistence;

import java.util.List;

public class GameSaveData {

    private String playerName;
    private int currentRoomIndex;
    private List<String> inventoryItems;
    private boolean puzzleSolved;
    private boolean inscriptionsPuzzleSolved;

    public GameSaveData() {
    }

    // Costruttore utilizzato dal vecchio sistema di salvataggio
    public GameSaveData(String playerName, int currentRoomIndex,
                        List<String> inventoryItems, boolean puzzleSolved) {

        this(
                playerName,
                currentRoomIndex,
                inventoryItems,
                puzzleSolved,
                false
        );
    }

    // Costruttore completo con lo stato dei due enigmi
    public GameSaveData(String playerName, int currentRoomIndex,
                        List<String> inventoryItems,
                        boolean puzzleSolved,
                        boolean inscriptionsPuzzleSolved) {

        this.playerName = playerName;
        this.currentRoomIndex = currentRoomIndex;
        this.inventoryItems = inventoryItems;
        this.puzzleSolved = puzzleSolved;
        this.inscriptionsPuzzleSolved = inscriptionsPuzzleSolved;
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

    public boolean isInscriptionsPuzzleSolved() {
        return inscriptionsPuzzleSolved;
    }
}
