
package it.unicam.cs.mpgc.rpg125904.domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Player player;
    private final List<Room> rooms;
    private int currentRoomIndex;

    public Game(String playerName, List<Room> rooms) {
        this.player = new Player(playerName);
        this.rooms = new ArrayList<>(rooms);
        this.currentRoomIndex = 0;
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

    public boolean isCompleted() {
        return !hasNextRoom();
    }

    public void moveToNextRoom() {
        if (hasNextRoom()) {
            currentRoomIndex++;
        }
    }
}
