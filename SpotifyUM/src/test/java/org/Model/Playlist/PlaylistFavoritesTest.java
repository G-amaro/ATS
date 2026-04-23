package org.Model.Playlist;

import org.Model.Music.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaylistFavoritesTest {

    private List<Music> musicList;

    @BeforeEach
    void setUp() {
        Music mockMusic = mock(Music.class);
        when(mockMusic.getName()).thenReturn("Fav Song");
        when(mockMusic.clone()).thenReturn(mockMusic);
        musicList = new ArrayList<>();
        musicList.add(mockMusic);
    }

    @Test
    void testConstructors_InitializeCorrectly() {
        PlaylistFavorites empty = new PlaylistFavorites();
        assertEquals("", empty.getName()); // Herdado vazio

        // Usa APENAS a lista no construtor
        PlaylistFavorites full = new PlaylistFavorites(musicList);
        assertEquals("Feito para Você", full.getName());
        assertEquals("SpotifyUM", full.getAutor());
        assertEquals(1, full.getMusics().size());

        PlaylistFavorites copy = new PlaylistFavorites(full);
        assertEquals("Feito para Você", copy.getName());
    }

    @Test
    void testCloneAndEquals() {
        PlaylistFavorites p1 = new PlaylistFavorites(musicList);
        PlaylistFavorites p2 = new PlaylistFavorites(musicList);

        // A PlaylistFavorites clona para uma PlaylistRandom!
        PlaylistRandom cloned = p1.clone();
        assertNotSame(p1, cloned);
        assertEquals(p1.getName(), cloned.getName());

        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(new Object()));
        assertTrue(p1.equals(p2));

        assertTrue(p1.toString().contains("Playlist Favoritos"));
    }
}