
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void shouldCreateItemWithCorrectName() {

        Item item = new Item(
                "Frammento di pietra",
                "Un piccolo frammento con inciso un simbolo antico."
        );

        assertEquals("Frammento di pietra", item.getName());
    }

    @Test
    void shouldReturnCorrectDescription() {

        Item item = new Item(
                "Frammento di pietra",
                "Un piccolo frammento con inciso un simbolo antico."
        );

        assertEquals(
                "Un piccolo frammento con inciso un simbolo antico.",
                item.getDescription()
        );
    }
}
