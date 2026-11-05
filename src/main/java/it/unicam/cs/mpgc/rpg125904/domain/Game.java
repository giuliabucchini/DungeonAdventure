package it.unicam.cs.mpgc.rpg125904.domain;

public class Game {

    private final Player player;
    private Room currentRoom;

    public Game(String playerName, Room startingRoom) {
        this.player = new Player(playerName);
        this.currentRoom = startingRoom;
    }

    public Player getPlayer() {
        return player;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }
}