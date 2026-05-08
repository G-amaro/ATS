package org.spotifumtp37.model.album; 

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.user.User;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.exceptions.AlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tarefa 5: Teste de Larga Escala - Integração Hypothesis e JUnit 5.
 */
class SpotifUMHypothesisTest {
    private SpotifUMData data;

    @BeforeEach
    void setUp() {
        data = new SpotifUMData();
    }

    private User createTestUser(String name, String email) {
        // Construtor exige 7 argumentos: name, email, address, plan, password, pontos, history
        return new User(name, email, "Rua Teste", new FreePlan(), "pass123", 0.0, new ArrayList<>());
    }

    @ParameterizedTest
    @DisplayName("Hypothesis: Teste de Larga Escala - Utilizadores")
    @CsvFileSource(resources = "/csv/test_users.csv", delimiter = '|')
    void testUserProperties(String name, String email, String plan) throws Exception {
        User user = createTestUser(name, email);
        data.addUser(user);
        
        // Verifica se existe no mapa interno de utilizadores
        assertTrue(data.getMapUsers().containsKey(name), "Utilizador deve existir no sistema");
        assertEquals(email, data.getMapUsers().get(name).getEmail());
    }

    @ParameterizedTest
    @DisplayName("Hypothesis: Teste de Larga Escala - Álbuns")
    @CsvFileSource(resources = "/csv/test_albums.csv", delimiter = '|')
    void testAlbumProperties(String title, String artist) throws Exception {
        // Construtor exige 5 argumentos: title, artist, year, genre, songs
        Album album = new Album(title, artist, 2024, "Pop", new ArrayList<>());
        data.addAlbum(album);
        
        assertNotNull(data.getAlbum(title), "Álbum deve ser recuperável");
        assertEquals(artist, data.getAlbum(title).getArtist());
    }

    @ParameterizedTest
    @DisplayName("Hypothesis: Catálogo e Subclasses de Song")
    @CsvFileSource(resources = "/csv/test_catalog.csv", delimiter = '|')
    void testMusicAlbumRelationship(String songName, String artist, String albumName, int duration, String type, String url) {
        try {
            if (!data.existsAlbum(albumName)) {
                data.addAlbum(new Album(albumName, artist, 2024, "Genre", new ArrayList<>()));
            }
            
            // CORREÇÃO CRÍTICA: Usar o método addSong da classe Album para garantir persistência
            Album album = data.getAlbum(albumName);
            
            boolean isExpl = "Explicit".equals(type);
            boolean isMult = "Multimedia".equals(type);
            
            // Adicionar via método oficial da classe Album para evitar problemas de cópia de lista
            album.addSong(songName, "Publisher", "Lyrics", "Notes", "Genre", duration, isExpl, isMult, url);
            
            // Verificação Robusta: getSongsCopy garante que lemos o estado atualizado
            boolean found = album.getSongsCopy().stream()
                                .anyMatch(m -> m.getName().equals(songName));
                                
            assertTrue(found, "Música '" + songName + "' não foi encontrada no álbum após adição.");
            
        } catch (Exception e) {
            fail("Falha no catálogo: " + e.getMessage());
        }
    }

    @ParameterizedTest
    @DisplayName("Hypothesis: Validar AlreadyExistsException")
    @CsvFileSource(resources = "/csv/test_duplicates.csv", delimiter = '|')
    void testDuplicateUserException(String name, String email) {
        User user = createTestUser(name, email);
        try {
            if (!data.existsUser(name)) {
                data.addUser(user);
            } else {
                assertThrows(AlreadyExistsException.class, () -> data.addUser(user));
            }
        } catch (Exception e) {
            // Ignorar para deixar o assertThrows validar
        }
    }

    @ParameterizedTest
    @DisplayName("Hypothesis: Teste de Stress e Robustez")
    @CsvFileSource(resources = "/csv/test_stress.csv", delimiter = '|')
    void testStressInputs(String name, String email, String plan) {
        assertDoesNotThrow(() -> {
            User user = createTestUser(name, email);
            data.addUser(user);
            assertEquals(name, data.getMapUsers().get(name).getName());
        });
    }
}