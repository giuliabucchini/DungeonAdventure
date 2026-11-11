
package it.unicam.cs.mpgc.rpg125904.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import com.google.gson.JsonParseException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

class GamePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadGameData() throws IOException {

        // Crea i dati della partita
        GameSaveData originalData = new GameSaveData(
                "Esploratore",
                1,
                List.of("Frammento di pietra"),
                false
        );

        // Crea il percorso del file
        String fileName = tempDir.resolve("partita.json").toString();

        // Salva i dati
        GameSaver saver = new GameSaver();
        saver.save(originalData, fileName);

        // Carica i dati
        GameLoader loader = new GameLoader();
        GameSaveData loadedData = loader.load(fileName);

        // Verifica che i dati siano stati conservati
        assertNotNull(loadedData);
        assertEquals("Esploratore", loadedData.getPlayerName());
        assertEquals(1, loadedData.getCurrentRoomIndex());
        assertEquals(
                List.of("Frammento di pietra"),
                loadedData.getInventoryItems()
        );
        assertFalse(loadedData.isPuzzleSolved());
    }
    @Test
    void shouldThrowExceptionWhenSaveFileIsInvalid() throws IOException {

        // Crea un file con contenuto JSON non valido
        Path file = tempDir.resolve("salvataggio_non_valido.json");

        Files.writeString(file, "questo non è un JSON valido");

        GameLoader gameLoader = new GameLoader();

        // Verifica che il caricamento generi un'eccezione
        assertThrows(
                JsonParseException.class,
                () -> gameLoader.load(file.toString())
        );
    }
    @Test
    void shouldThrowExceptionWhenSaveFileDoesNotExist() {

        Path file = tempDir.resolve("file_inesistente.json");

        GameLoader gameLoader = new GameLoader();

        assertThrows(
                IOException.class,
                () -> gameLoader.load(file.toString())
        );
    }
}

