package org.Model.Album;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.Model.Music.Music;

import static org.junit.jupiter.api.Assertions.*;

class AlbumPropertiesTest {

    @Property
    void adicionarMusicaAumentaTamanhoEPermiteBuscaPorNome(
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String albumName,
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String artistName,
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String musicName) {

        Album album = new Album(albumName, artistName);
        Music music = new Music(musicName, artistName, "Editora", "Letra", "Figuras", "Pop", albumName, 180, false);

        int tamanhoInicial = album.getMusics().size();
        
        album.addMusic(music);

        assertEquals(tamanhoInicial + 1, album.getMusics().size(), "O tamanho do álbum não incrementou corretamente.");
        
        assertNotNull(album.getMusicByName(musicName), "A música adicionada não foi encontrada pelo nome.");
        assertEquals(musicName, album.getMusicByName(musicName).getName(), "O nome da música recuperada não coincide.");
    }

    @Property
    void removerMusicaExistenteDiminuiTamanho(
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String musicName) {

        Album album = new Album("Album Teste", "Artista Teste");
        Music music = new Music(musicName, "Artista Teste", "Ed", "L", "F", "G", "A", 200, false);

        album.addMusic(music);
        Assume.that(album.getMusics().size() == 1);

        album.removeMusic(music);

        assertEquals(0, album.getMusics().size(), "A música não foi removida do álbum.");
        
        assertNull(album.getMusicByName(musicName), "A música ainda é encontrada após ser removida.");
    }

    @Property
    void encapsulamentoECloneDevemSerIndependentes(
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String musicName) {

        Album original = new Album("Original", "Artista");
        Music m = new Music(musicName, "Artista", "Ed", "L", "F", "G", "A", 100, false);
        
        original.addMusic(m);

        Album copia = original.clone();
        
        original.removeMusic(m);

        assertEquals(1, copia.getMusics().size(), "Quebra de encapsulamento: O clone foi afetado pela modificação do original!");
        assertEquals(0, original.getMusics().size());
        assertNotNull(copia.getMusicByName(musicName));
    }
}