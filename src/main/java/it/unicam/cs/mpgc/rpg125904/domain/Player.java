package it.unicam.cs.mpgc.rpg125904.domain;

public class Player {

    private final String name;
    private final Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }
}