package org.Model.Playlist;

import org.Model.Music.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaylistRandomTest {

    private List<Music> musicList;

    @BeforeEach
    void setUp() {
        Music mockMusic = mock(Music.class);
        when(mockMusic.getName()).thenReturn("Random Song");
        when(mockMusic.clone()).thenReturn(mockMusic);
        musicList = new ArrayList<>();
        musicList.add(mockMusic);
    }

    @Test
    void testConstructors_InitializeCorrectly() {
        PlaylistRandom empty = new PlaylistRandom();
        assertEquals("", empty.getName());

        // Construtor pede Nome e Lista
        PlaylistRandom full = new PlaylistRandom("My Random Mix", musicList);
        assertEquals("My Random Mix", full.getName());
        assertEquals("SpotifyUM", full.getAutor());
        assertEquals(1, full.getMusics().size());

        PlaylistRandom copy = new PlaylistRandom(full);
        assertEquals("My Random Mix", copy.getName());
    }

    @Test
    void testCloneAndEquals() {
        PlaylistRandom p1 = new PlaylistRandom("Mix", musicList);
        PlaylistRandom p2 = new PlaylistRandom("Mix", musicList);
        PlaylistRandom diffName = new PlaylistRandom("Diff", musicList);

        PlaylistRandom cloned = p1.clone();
        assertNotSame(p1, cloned);
        assertEquals(p1.getName(), cloned.getName());

        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(new Object()));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(diffName));

        assertTrue(p1.toString().contains("Playlist Aleatória"));
    }
}