package it.unicam.cs.mpgc.rpg125904.application;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Room;

public class GameManager {

    public Game startNewGame(String playerName) {

        Room startingRoom = new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne e decorazioni blu."
        );

        return new Game(playerName, startingRoom);
    }
}