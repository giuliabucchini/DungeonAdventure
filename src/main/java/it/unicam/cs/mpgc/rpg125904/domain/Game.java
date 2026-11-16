package it.unicam.cs.mpgc.rpg125904.domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Player player;
    private final List<Room> rooms;
    private int currentRoomIndex;
    private boolean completed;

    public Game(String playerName, List<Room> rooms) {
        this.player = new Player(playerName);
        this.rooms = new ArrayList<>(rooms);
        this.currentRoomIndex = 0;
        this.completed = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomIndex);
    }

    public Room getRoom(int index) {
        return rooms.get(index);
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public void setCurrentRoomIndex(int index) {
        if (index >= 0 && index < rooms.size()) {
            currentRoomIndex = index;
        }
    }

    public boolean hasNextRoom() {
        return currentRoomIndex < rooms.size() - 1;
    }

    public boolean hasPreviousRoom() {
        return currentRoomIndex > 0;
    }

    /**
     * La partita termina solo quando il giocatore completa l'interazione finale.
     */
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void moveToNextRoom() {
        if (hasNextRoom()) {
            currentRoomIndex++;
        }
    }

    public void moveToPreviousRoom() {
        if (hasPreviousRoom()) {
            currentRoomIndex--;
        }
    }
}
