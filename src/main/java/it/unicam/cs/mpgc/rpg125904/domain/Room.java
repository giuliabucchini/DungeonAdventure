package it.unicam.cs.mpgc.rpg125904.domain;

public class Room {

    private final String name;
    private final String description;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}