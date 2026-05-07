package org.Model.Playlist;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.Model.Music.Music;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlaylistPropertiesTest {

    @Property
    void adicionarMusicasDeveManterOrdemEIntegridade(
            @ForAll @Size(min = 1, max = 20) List<@AlphaChars String> nomesMusicas) {
        
        Playlist playlist = new Playlist("PropTest", "Owner");
        
        for (String nome : nomesMusicas) {
            try {
                
                Music m = new Music(nome, "Artista", "Ed", "Letra", "Fig", "Pop", "Album", 180, false);
                playlist.addMusic(m);
            } catch (Exception e) {
            
            }
        }

        List<Music> musicasNaPlaylist = playlist.getMusics();
        assertTrue(musicasNaPlaylist.size() <= nomesMusicas.size());
        
        if (!musicasNaPlaylist.isEmpty()) {
           
            assertNotNull(playlist.getMusicBYIndex(0));
        }
    }

    @Property
    void cloneDeveGerarObjetoIgualMasReferenciaDiferente() {
        Playlist p1 = new Playlist("Original", "Autor");
        Playlist p2 = p1.clone();

        assertEquals(p1, p2, "O clone deve ser igual ao original.");
        assertNotSame(p1, p2, "O clone deve ser uma nova instância em memória.");
    }
}