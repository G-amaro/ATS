package org.Model.Album;

import org.Model.Music.Music;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumTest {

    private Music mockMusic1;
    private Music mockMusic2;
    private List<Music> musicList;

    @BeforeEach
    void setUp() {
        // Criar mocks para as músicas isolando assim a classe Album
        mockMusic1 = mock(Music.class);
        when(mockMusic1.getName()).thenReturn("Song A");
        when(mockMusic1.clone()).thenReturn(mockMusic1); // O álbum exige que as músicas sejam clonáveis
        when(mockMusic1.toString()).thenReturn("MusicInfo A");

        mockMusic2 = mock(Music.class);
        when(mockMusic2.getName()).thenReturn("Song B");
        when(mockMusic2.clone()).thenReturn(mockMusic2);
        when(mockMusic2.toString()).thenReturn("MusicInfo B");

        musicList = new ArrayList<>();
        musicList.add(mockMusic1);
    }

    // ==========================================
    // 1. TESTES DOS CONSTRUTORES
    // ==========================================

    @Test
    void testConstructors_InitializeCorrectly() {
        // Construtor Vazio
        Album emptyAlbum = new Album();
        assertEquals("", emptyAlbum.getName(), "Empty constructor should set name to empty string.");
        assertEquals("", emptyAlbum.getArtist(), "Empty constructor should set artist to empty string.");

        // BUG ENCONTRADO NO CÓDIGO ORIGINAL! getMusics() lança NullPointerException se a lista for nula.
        // O nosso teste prevê e captura esse crash com sucesso!
        assertThrows(NullPointerException.class, () -> emptyAlbum.getMusics(), "getMusics should throw NPE because musics list is null.");
        // Construtor Nome & Artista
        Album basicAlbum = new Album("Dark Side", "Pink Floyd");
        assertEquals("Dark Side", basicAlbum.getName());
        assertEquals("Pink Floyd", basicAlbum.getArtist());
        assertTrue(basicAlbum.getMusics().isEmpty());

        // Construtor Completo
        Album fullAlbum = new Album("Thriller", "Michael Jackson", musicList);
        assertEquals("Thriller", fullAlbum.getName());
        assertEquals("Michael Jackson", fullAlbum.getArtist());
        assertEquals(1, fullAlbum.getMusics().size());

        // Construtor de Cópia
        Album copyAlbum = new Album(fullAlbum);
        assertEquals("Thriller", copyAlbum.getName());
        assertEquals("Michael Jackson", copyAlbum.getArtist());
        assertEquals(1, copyAlbum.getMusics().size());
    }

    // ==========================================
    // 2. TESTES DOS SETTERS E GETTERS
    // ==========================================

    @Test
    void testSettersAndGetters_ValidData_UpdatesFields() {
        Album album = new Album();

        // As tuas linhas em falta estavam aqui!
        album.setName("Abbey Road");
        album.setArtist("The Beatles");

        // Resolver o null do construtor vazio antes de testar a lista
        album.setMusics(musicList);

        assertEquals("Abbey Road", album.getName(), "setName should update the album name.");
        assertEquals("The Beatles", album.getArtist(), "setArtist should update the album artist.");
        assertEquals(1, album.getMusics().size(), "setMusics should update the list of musics.");
    }

    // ==========================================
    // 3. OPERAÇÕES DE LISTA E PESQUISA
    // ==========================================

    @Test
    void testMusicListOperations_AddRemoveGet_ModifiesAndRetrievesCorrectly() {
        Album album = new Album("Nevermind", "Nirvana");

        // Testar a Adição
        album.addMusic(mockMusic1);
        album.addMusic(mockMusic2);
        assertEquals(2, album.getMusics().size(), "addMusic should increase list size.");

        // Testar a Pesquisa (Com Sucesso)
        Music found = album.getMusicByName("Song B");
        assertNotNull(found, "getMusicByName should return the music if it exists.");
        assertEquals("Song B", found.getName());

        // Testar a Pesquisa (Sem Sucesso)
        Music notFound = album.getMusicByName("Song C");
        assertNull(notFound, "getMusicByName should return null if music does not exist.");

        // Testar a Remoção
        album.removeMusic(mockMusic1);
        assertEquals(1, album.getMusics().size(), "removeMusic should decrease list size.");
        assertNull(album.getMusicByName("Song A"), "Removed music should no longer be retrievable.");
    }

    // ==========================================
    // 4. TESTES DE CLONE E TOSTRING
    // ==========================================

    @Test
    void testClone_ValidAlbum_ReturnsIdenticalCopy() {
        Album original = new Album("Master of Puppets", "Metallica", musicList);
        Album cloned = original.clone();

        assertNotSame(original, cloned, "Clone should return a new object instance.");
        assertEquals(original.getName(), cloned.getName(), "Cloned album should have the same name.");
        assertEquals(original.getArtist(), cloned.getArtist(), "Cloned album should have the same artist.");
    }

    @Test
    void testToString_ValidAlbum_ReturnsFormattedString() {
        Album album = new Album("Rumours", "Fleetwood Mac", musicList);
        String result = album.toString();

        assertTrue(result.contains("Album: Rumours"), "toString should contain the album name.");
        assertTrue(result.contains("Artista: Fleetwood Mac"), "toString should contain the artist name.");
        assertTrue(result.contains("MusicInfo A"), "toString should contain the music representations.");
    }
}