package it.unicam.cs.mpgc.rpg125904.persistence;

import java.util.List;

public class GameSaveData {

    private String playerName;
    private String roleName;
    private int currentRoomIndex;
    private List<String> inventoryItems;
    private List<Boolean> solvedPuzzles;
    private boolean completed;

    private int hp;
    private int maxHp;
    private int strength;
    private int wisdom;
    private int xp;
    private int level;

    public GameSaveData() {
    }

    public GameSaveData(
            String playerName,
            int currentRoomIndex,
            List<String> inventoryItems,
            List<Boolean> solvedPuzzles
    ) {
        this(playerName, currentRoomIndex, inventoryItems, solvedPuzzles, false);
    }

    public GameSaveData(
            String playerName,
            int currentRoomIndex,
            List<String> inventoryItems,
            List<Boolean> solvedPuzzles,
            boolean completed
    ) {
        this.playerName = playerName;
        this.roleName = "ESPLORATORE";
        this.currentRoomIndex = currentRoomIndex;
        this.inventoryItems = inventoryItems;
        this.solvedPuzzles = solvedPuzzles;
        this.completed = completed;
    }

    public GameSaveData(
            String playerName,
            String roleName,
            int currentRoomIndex,
            List<String> inventoryItems,
            List<Boolean> solvedPuzzles,
            boolean completed,
            int hp,
            int maxHp,
            int strength,
            int wisdom,
            int xp,
            int level
    ) {
        this.playerName = playerName;
        this.roleName = roleName;
        this.currentRoomIndex = currentRoomIndex;
        this.inventoryItems = inventoryItems;
        this.solvedPuzzles = solvedPuzzles;
        this.completed = completed;
        this.hp = hp;
        this.maxHp = maxHp;
        this.strength = strength;
        this.wisdom = wisdom;
        this.xp = xp;
        this.level = level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRoleName() {
        return roleName;
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

    public boolean isCompleted() {
        return completed;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getStrength() {
        return strength;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }
}