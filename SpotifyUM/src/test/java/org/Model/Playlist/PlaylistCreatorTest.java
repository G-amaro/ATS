package org.Model.Playlist;

import org.Exceptions.EmptyPlaylistException;
import org.Model.Music.Music;
import org.Model.Music.MusicReproduction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaylistCreatorTest {

    private Map<String, Music> dbMusics;
    private Music rockMusic;
    private Music popMusic;

    @BeforeEach
    void setUp() {
        dbMusics = new HashMap<>();

        rockMusic = mock(Music.class);
        when(rockMusic.getName()).thenReturn("Rock Song");
        when(rockMusic.getGenre()).thenReturn("Rock");
        when(rockMusic.getDuration()).thenReturn(200);
        when(rockMusic.isExplicit()).thenReturn(false);

        popMusic = mock(Music.class);
        when(popMusic.getName()).thenReturn("Pop Song");
        when(popMusic.getGenre()).thenReturn("Pop");
        when(popMusic.getDuration()).thenReturn(180);
        when(popMusic.isExplicit()).thenReturn(true);

        dbMusics.put("Rock Song", rockMusic);
        dbMusics.put("Pop Song", popMusic);
    }

    // ==========================================
    // 1. TESTE DO CONSTRUTOR (Para matar os 0%)
    // ==========================================
    @Test
    void testConstructor_Coverage() {
        // Como é uma classe de métodos estáticos, ninguém a instancia na vida real.
        // Mas o JaCoCo exige que o construtor vazio seja chamado para dar 100%!
        PlaylistCreator creator = new PlaylistCreator();
        assertNotNull(creator, "Should instantiate to cover the default constructor.");
    }

    // ==========================================
    // 2. TESTE DO GENRE PLAYLIST
    // ==========================================
    @Test
    void testCreateGenrePlaylist() throws Exception {
        Map<Integer, Playlist> publicPlaylists = new HashMap<>();

        // Caminho de Sucesso
        List<Music> result = PlaylistCreator.createGenrePlaylist("User", "RockPlay", "Rock", 500, dbMusics, publicPlaylists);
        assertEquals(1, result.size());

        // Erro: Género inexistente
        assertThrows(IllegalArgumentException.class, () ->
                PlaylistCreator.createGenrePlaylist("User", "JazzPlay", "Jazz", 500, dbMusics, publicPlaylists)
        );

        // Erro: Playlist Vazia (duração não chega sequer para 1 música)
        assertThrows(EmptyPlaylistException.class, () ->
                PlaylistCreator.createGenrePlaylist("User", "RockPlay", "Rock", 10, dbMusics, publicPlaylists)
        );
    }

    // ==========================================
    // 3. TESTE DO RANDOM PLAYLIST
    // ==========================================
    @Test
    void testCreateRandomPlaylist() {
        List<Music> result = PlaylistCreator.createRandomPlaylist(dbMusics);
        assertNotNull(result);
        assertTrue(result.size() > 0 && result.size() <= dbMusics.size());
    }

    // ==========================================
    // 4. TESTE DO FAVORITES (Matar os Branches e o Lambda)
    // ==========================================
    @Test
    void testCreateFavoritesPlaylist_FullCoverage() {
        List<MusicReproduction> reps = new ArrayList<>();

        MusicReproduction repRock = mock(MusicReproduction.class);
        when(repRock.getMusic()).thenReturn(rockMusic);

        MusicReproduction repPop = mock(MusicReproduction.class);
        when(repPop.getMusic()).thenReturn(popMusic);

        // Música fantasma (está no histórico mas já foi apagada da Base de Dados - mata o "if(m != null)")
        Music ghostMusic = mock(Music.class);
        when(ghostMusic.getName()).thenReturn("Ghost Song");
        when(ghostMusic.isExplicit()).thenReturn(false);
        MusicReproduction repGhost = mock(MusicReproduction.class);
        when(repGhost.getMusic()).thenReturn(ghostMusic);

        // Adicionar ao histórico: Pop(3x), Rock(2x), Ghost(1x).
        // Isto obriga a usar a LAMBDA para ordenar: Pop(3) -> Rock(2) -> Ghost(1)
        reps.add(repPop); reps.add(repPop); reps.add(repPop);
        reps.add(repRock); reps.add(repRock);
        reps.add(repGhost);

        // Cenário 1: maxDuration = 0 (ignora duração) e explicit = false
        List<Music> res1 = PlaylistCreator.createFavoritesPlaylist(0, false, reps, dbMusics);
        assertEquals(2, res1.size(), "Should skip Ghost Song because it's not in dbMusics.");
        assertEquals("Pop Song", res1.get(0).getName(), "Pop should be first (most played).");

        // Cenário 2: explicit = true (Rock não é explicita, logo é ignorada na listagem)
        List<Music> res2 = PlaylistCreator.createFavoritesPlaylist(500, true, reps, dbMusics);
        assertEquals(1, res2.size(), "Should only include Pop Song.");

        // Cenário 3: maxDuration break! Pop=180, Rock=200. Max=200.
        // Entra o Pop (180). Quando tenta entrar o Rock (180+200 > 200), ele faz "break"!
        List<Music> res3 = PlaylistCreator.createFavoritesPlaylist(200, false, reps, dbMusics);
        assertEquals(1, res3.size(), "Should stop after Pop Song due to maxDuration limit.");

        // Cenário 4: Exceção de lista vazia
        assertThrows(IllegalArgumentException.class, () ->
                PlaylistCreator.createFavoritesPlaylist(500, false, new ArrayList<>(), dbMusics)
        );
    }
}