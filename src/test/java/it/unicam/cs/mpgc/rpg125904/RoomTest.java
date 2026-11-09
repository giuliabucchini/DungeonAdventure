
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Room;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void shouldCreateRoomWithCorrectInformation() {

        Room room = new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne."
        );

        assertEquals("Ingresso del tempio", room.getName());
        assertEquals(
                "Una grande sala con antiche colonne.",
                room.getDescription()
        );
    }

    @Test
    void shouldAddItemToRoom() {

        Room room = new Room("Ingresso", "Una sala antica.");

        Item item = new Item(
                "Chiave antica",
                "Una piccola chiave."
        );

        room.addItem(item);

        assertEquals(1, room.getItems().size());
        assertTrue(room.getItems().contains(item));
    }

    @Test
    void shouldRemoveItemFromRoom() {

        Room room = new Room("Ingresso", "Una sala antica.");

        Item item = new Item(
                "Chiave antica",
                "Una piccola chiave."
        );

        room.addItem(item);
        room.removeItem(item);

        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void shouldStartWithoutPuzzle() {

        Room room = new Room("Ingresso", "Una sala antica.");

        assertNull(room.getPuzzle());
    }
}
