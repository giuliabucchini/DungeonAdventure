
package it.unicam.cs.mpgc.rpg125904.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GameSaver {

    private final Gson gson;

    public GameSaver() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public void save(GameSaveData saveData, String fileName)
            throws IOException {

        Path filePath = Path.of(fileName);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(saveData, writer);
        }
    }
}
