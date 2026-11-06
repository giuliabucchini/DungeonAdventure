package it.unicam.cs.mpgc.rpg125904.application;

import it.unicam.cs.mpgc.rpg125904.domain.Game;
import it.unicam.cs.mpgc.rpg125904.domain.Room;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    public Game startNewGame(String playerName) {

        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room(
                "Ingresso del tempio",
                "Una grande sala con antiche colonne e decorazioni blu."
        ));

        rooms.add(new Room(
                "Sala delle iscrizioni",
                "Sulle pareti sono incisi antichi simboli. "
                        + "Alcuni sembrano indicare un passaggio segreto."
        ));

        rooms.add(new Room(
                "Camera degli enigmi",
                "Al centro della stanza si trova un antico altare "
                        + "con un misterioso enigma."
        ));

        rooms.add(new Room(
                "Santuario nascosto",
                "Una stanza segreta decorata con simboli dorati "
                        + "e antichi tesori."
        ));

        return new Game(playerName, rooms);
    }

    public void moveToNextRoom(Game game) {
        game.moveToNextRoom();
    }
}