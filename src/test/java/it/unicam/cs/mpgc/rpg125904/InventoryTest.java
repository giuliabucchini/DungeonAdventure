package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Inventory;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void shouldAddItemToInventory() {

        Inventory inventory = new Inventory();

        Item item = new Item(
                "Chiave antica",
                "Una piccola chiave decorata."
        );

        inventory.addItem(item);

        assertTrue(inventory.containsItem("Chiave antica"));
    }

    @Test
    void shouldNotAddDuplicateItems() {

        Inventory inventory = new Inventory();

        Item firstItem = new Item(
                "Chiave antica",
                "Una piccola chiave."
        );

        Item secondItem = new Item(
                "Chiave antica",
                "Una chiave diversa."
        );

        inventory.addItem(firstItem);
        inventory.addItem(secondItem);

        assertEquals(1, inventory.getItems().size());
    }
}