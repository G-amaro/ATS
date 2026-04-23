package org.Model;

import org.Exceptions.*;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Plan.PlanPremiumTop;
import org.Model.Playlist.Playlist;
import org.Model.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMTest {

    private SpotifUM spotifUM;
    private User admin;

    @BeforeEach
    void setUp() throws Exception {
        spotifUM = new SpotifUM();

        // 1. Criar utilizador admin manual (Sem populateDatabase para não haver lixo)
        admin = new User("admin", "a@mail.com", "Rua A", "1234");
        admin.setPlan(new PlanPremiumTop());

        // Injetar o utilizador
        spotifUM.addNewUser("admin", "a@mail.com", "Rua A", "1234");
        // Garantir que o plano é Premium para os testes passarem
        spotifUM.getUsers().get("admin").setPlan(new PlanPremiumTop());

        spotifUM.authenticateUser("admin", "1234");
    }

    @Test
    void testConstructors_AndLambdas() {
        SpotifUM copy = new SpotifUM(spotifUM);
        assertEquals(spotifUM.getUsers().size(), copy.getUsers().size());

        SpotifUM full = new SpotifUM(
                spotifUM.getMusics(),
                spotifUM.getPublicPlaylists(),
                spotifUM.getUsers(),
                spotifUM.getAlbums(),
                spotifUM.getArtistReproductions(),
                spotifUM.getGenreReproductions()
        );
        assertNotNull(full);
    }

    @Test
    void testMusicAndAlbum_Operations() throws Exception {
        // Criar um Álbum e uma Música
        spotifUM.addNewAlbum("Album 1", "Artista 1");
        spotifUM.addNewMusic("Song 1", "Artista 1", "Pub", "Lyrics", "Fig", "Pop", "Album 1", 180, false, null);

        assertTrue(spotifUM.musicExists("Song 1"));
        assertTrue(spotifUM.albumExists("Album 1"));

        assertDoesNotThrow(() -> spotifUM.playMusic("Song 1"));
        assertThrows(NotFoundException.class, () -> spotifUM.playMusic("Ghost"));

        assertNotNull(spotifUM.getMusicByName("Song 1"));
        assertNotNull(spotifUM.getAlbumByName("Album 1"));

        // Adicionar música a álbum existente
        Music m = spotifUM.getMusicByName("Song 1");
        assertDoesNotThrow(() -> spotifUM.addMusicToAlbum("Album 1", m));
    }

    @Test
    void testUser_Operations() throws Exception {
        // 1. Testar se o email muda bem
        spotifUM.setCurrentUserEmail("novo@mail.com");
        assertEquals("novo@mail.com", spotifUM.getCurrentUser().getEmail());

        // 2. Testar autenticação falhada (User que não existe)
        assertThrows(NotFoundException.class, () -> spotifUM.authenticateUser("fantasma", "123"));

        // 3. Testar senha errada num user que existe (Mata o UnsupportedOperationException)
        // Criamos um user novo e registamos no sistema para garantir que ele está no mapa
        spotifUM.addNewUser("login_test", "test@mail.com", "Rua X", "pass123");
        assertThrows(UnsupportedOperationException.class, () -> spotifUM.authenticateUser("login_test", "senha_errada"));

        // 4. Verificar se a password está correta
        spotifUM.authenticateUser("login_test", "pass123");
        assertTrue(spotifUM.isPasswordCorrect("pass123"));
        assertFalse(spotifUM.isPasswordCorrect("errada"));
    }

    @Test
    void testPlaylists_Operations() throws Exception {
        // Criar música para a playlist
        spotifUM.addNewAlbum("Alb", "Art");
        spotifUM.addNewMusic("M1", "Art", "P", "L", "F", "Pop", "Alb", 100, false, null);

        spotifUM.addToCurrentUserPlaylist("Minha Play");
        // Buscar o ID da playlist criada
        int pId = spotifUM.getCurrentUser().getPlaylists().get(0).getId();

        spotifUM.addMusicToCurrentUserPlaylist(pId, "M1");

        // Tornar pública
        assertDoesNotThrow(() -> spotifUM.setPlaylistAsPublic(pId));
        assertEquals(1, spotifUM.getPublicPlaylistSize());
        assertNotNull(spotifUM.getPublicPlaylistById(pId));

        // Remover música
        assertDoesNotThrow(() -> spotifUM.removeMusicFromPlaylist("M1", pId));
    }

    @Test
    void testStatistics_Operations() throws Exception {
        spotifUM.addNewAlbum("Alb", "Art");
        spotifUM.addNewMusic("M1", "Art", "P", "L", "F", "Pop", "Alb", 100, false, null);

        spotifUM.incrementArtistReproductions("Art");
        spotifUM.incrementGenreReproductions("Pop");
        spotifUM.addToCurrentUserMusicReproductions("M1");

        assertNotNull(spotifUM.getTopArtistName());
        assertNotNull(spotifUM.getGenreWithMostReproductions());
        assertNotNull(spotifUM.mostReproducedMusic());
        assertNotNull(spotifUM.getUserWithMostPoints());
        assertNotNull(spotifUM.getUserWithMostPlaylists());

        LocalDate now = LocalDate.now();
        assertNotNull(spotifUM.getUserWithMostReproductions(now.minusDays(1), now.plusDays(1)));
    }

    @Test
    void testEquals_Operations() {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        // Iguais por estarem vazios
        assertTrue(s1.equals(s2));

        // Diferentes por s1 ter um user
        assertDoesNotThrow(() -> s1.addNewUser("user1", "e", "a", "p"));
        assertFalse(s1.equals(s2));

        // Iguais a si próprio e nulo
        assertTrue(s1.equals(s1));
        assertFalse(s1.equals(null));
    }
}