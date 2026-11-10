
package it.unicam.cs.mpgc.rpg125904.persistence;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class GameLoader {

    private final Gson gson;

    public GameLoader() {
        this.gson = new Gson();
    }

    public GameSaveData load(String fileName) throws IOException {

        Path filePath = Path.of(fileName);

        try (FileReader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, GameSaveData.class);
        }
    }
}
