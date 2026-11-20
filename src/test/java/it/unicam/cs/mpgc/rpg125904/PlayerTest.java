package it.unicam.cs.mpgc.rpg125904;

import it.unicam.cs.mpgc.rpg125904.domain.Item;
import it.unicam.cs.mpgc.rpg125904.domain.Player;
import it.unicam.cs.mpgc.rpg125904.domain.PlayerRole;
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

        assertTrue(player.getInventory().containsItem("Chiave antica"));
    }

    @Test
    void shouldApplyRoleDamageReductionAndHealingCap() {
        Player guardian = new Player("Guardiano", PlayerRole.GUARDIANO);
        int taken = guardian.takeDamage(10);

        assertEquals(6, taken);
        assertEquals(124, guardian.getHp());

        int healed = guardian.heal(20);
        assertEquals(6, healed);
        assertEquals(130, guardian.getHp());
    }

    @Test
    void shouldLevelUpWhenXpThresholdIsReached() {
        Player player = new Player("Esploratore");
        assertEquals(1, player.getLevel());

        boolean leveledUp = player.addXp(100);

        assertTrue(leveledUp);
        assertEquals(2, player.getLevel());
        assertEquals(115, player.getMaxHp());
    }
}