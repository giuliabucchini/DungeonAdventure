package it.unicam.cs.mpgc.rpg125904.domain;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item != null && !containsItem(item.getName())) {
            items.add(item);
        }
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public boolean containsItem(String itemName) {
        if (itemName == null) return false;
        for (Item item : items) {
            if (item.getName().trim().equalsIgnoreCase(itemName.trim())) {
                return true;
            }
        }
        return false;
    }
}