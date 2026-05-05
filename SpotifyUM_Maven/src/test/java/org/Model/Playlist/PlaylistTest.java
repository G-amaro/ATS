package org.Model.Playlist;

import org.Exceptions.AlreadyExistsException;
import org.Model.Music.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaylistTest {

    private Music mockMusic1;
    private Music mockMusic2;
    private List<Music> musicList;

    @BeforeEach
    void setUp() {
        mockMusic1 = mock(Music.class);
        when(mockMusic1.getName()).thenReturn("Song A");
        when(mockMusic1.clone()).thenReturn(mockMusic1);

        mockMusic2 = mock(Music.class);
        when(mockMusic2.getName()).thenReturn("Song B");
        when(mockMusic2.clone()).thenReturn(mockMusic2);

        musicList = new ArrayList<>();
        musicList.add(mockMusic1);
    }

    // ==========================================
    // 1. TESTES DOS CONSTRUTORES (A caça aos 75% -> 100%)
    // ==========================================
    @Test
    void testConstructors_InitializeCorrectly() {
        Playlist empty = new Playlist();
        assertEquals("", empty.getName());
        assertEquals("", empty.getAutor());
        assertNotNull(empty.getMusics());

        Playlist basic = new Playlist("My Vibes", "Quim");
        assertEquals("My Vibes", basic.getName());
        assertEquals("Quim", basic.getAutor());
        assertTrue(basic.getMusics().isEmpty());

        Playlist full = new Playlist("Hits", "SpotifyUM", musicList);
        assertEquals("Hits", full.getName());
        assertEquals(1, full.getMusics().size());

        // O SEGREDO DO CONSTRUTOR: Testar a passagem de lista NULL (Mata a branch dos 75%)
        Playlist nullListPlaylist = new Playlist("Null List", "SpotifyUM", null);
        assertNotNull(nullListPlaylist.getMusics(), "Should handle null list safely and initialize empty array.");
        assertTrue(nullListPlaylist.getMusics().isEmpty());

        Playlist copy = new Playlist(full);
        assertEquals("Hits", copy.getName());
    }

    // ==========================================
    // 2. TESTES DE SETTERS E GETTERS
    // ==========================================
    @Test
    void testSettersAndGetters_UpdatesFields() {
        Playlist p = new Playlist();
        p.setId(99);
        p.setName("Roadtrip");
        p.setAutor("Me");
        p.setMusics(musicList);

        assertEquals(99, p.getId());
        assertEquals("Roadtrip", p.getName());
        assertEquals("Me", p.getAutor());
        assertEquals(1, p.getMusics().size());
    }

    // ==========================================
    // 3. OPERAÇÕES DE LISTA (A caça aos 75% do getMusicBYIndex)
    // ==========================================
    @Test
    void testListOperations() throws AlreadyExistsException {
        Playlist p = new Playlist("Workout", "Me", new ArrayList<>());

        p.addMusic(mockMusic1);
        p.addMusic(mockMusic2);
        assertEquals(2, p.getMusics().size());

        assertThrows(AlreadyExistsException.class, () -> p.addMusic(mockMusic1));

        // Testar as 3 condições do getMusicBYIndex: Válido, Abaixo do limite, Acima do limite
        assertEquals(mockMusic1, p.getMusicBYIndex(0), "Should get valid index.");
        assertThrows(IndexOutOfBoundsException.class, () -> p.getMusicBYIndex(5), "Should throw on index too high.");
        // O SEGREDO DO GET INDEX: Testar o índice negativo (Mata a branch dos 75%)
        assertThrows(IndexOutOfBoundsException.class, () -> p.getMusicBYIndex(-1), "Should throw on negative index.");

        p.removeMusic(mockMusic1);
        assertEquals(1, p.getMusics().size());
    }

    // ==========================================
    // 4. TESTE DE EQUALS, CLONE, TOSTRING E HASHCODE (A caça aos 0%)
    // ==========================================
    @Test
    void testEqualsCloneToStringAndHashCode() {
        Playlist p1 = new Playlist("Rock", "Autor A", musicList);
        Playlist p2 = new Playlist("Rock", "Autor A", musicList);
        Playlist diffName = new Playlist("Pop", "Autor A", musicList);
        Playlist diffAutor = new Playlist("Rock", "Autor B", musicList);

        Playlist cloned = p1.clone();
        assertNotSame(p1, cloned);
        assertEquals(p1.getName(), cloned.getName());

        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(new Object()));

        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(diffName));
        assertFalse(p1.equals(diffAutor));

        assertTrue(p1.toString().contains("Rock"));

        // O SEGREDO DO HASHCODE: Chamar e verificar a função! (Mata os 0% -> 100%)
        assertEquals(p1.getId(), p1.hashCode(), "HashCode should be equal to the Playlist ID.");
    }
}