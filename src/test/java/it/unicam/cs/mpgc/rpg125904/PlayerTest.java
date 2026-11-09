
package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Player;
import it.unicam.cs.mpgc.rpg125904.domain.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    class PlayerTest {

        @Test
        void shouldCreatePlayerWithCorrectName() {

            Player player = new Player("Esploratore");

            assertEquals("Esploratore", player.getName());
        }

        @Test
        void shouldCreatePlayerWithEmptyInventory() {

            Player player = new Player("Esploratore");

            assertTrue(player.getInventory().getItems().isEmpty());
        }

        @Test
        void shouldAllowPlayerToCollectItems() {

            Player player = new Player("Esploratore");

            Item item = new Item(
                    "Chiave antica",
                    "Una piccola chiave decorata."
            );

            player.getInventory().addItem(item);

            assertTrue(
                    player.getInventory().containsItem("Chiave antica")
            );
        }
    }


