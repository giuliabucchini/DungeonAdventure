package it.unicam.cs.mpgc.rpg125904.domain;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String name;
    private final String description;
    private final List<Item> items;
    private RoomChallenge challenge;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.challenge = null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public RoomChallenge getChallenge() {
        return challenge;
    }

    public void setChallenge(RoomChallenge challenge) {
        this.challenge = challenge;
    }

    public Puzzle getPuzzle() {
        return challenge instanceof Puzzle puzzle ? puzzle : null;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.challenge = puzzle;
    }
}