package org.spotifumtp37.model.album;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MultimediaSongTest {

    @Test
    void testVideoLinkGetterAndSetter() {
        // Inicializa com o construtor cheio
        MultimediaSong song = new MultimediaSong("Video Song", "Artista", "Editora", "Letras", "Notas", "Pop", 200, "https://youtube.com/video1");

        // Verifica se o construtor guardou bem o link
        assertEquals("https://youtube.com/video1", song.getVideoLink());

        // A Magia para cobrir o Setter!
        song.setVideoLink("https://vimeo.com/video2");

        // Verifica se o valor mudou com sucesso
        assertEquals("https://vimeo.com/video2", song.getVideoLink());
    }

    @Test
    void testConstructorsAndClone() {
        // Construtor de Cópia e Clone
        MultimediaSong original = new MultimediaSong("Original", "Artista", "Editora", "Letras", "Notas", "Pop", 200, "link.com");

        MultimediaSong copyConstructor = new MultimediaSong(original);
        assertEquals("link.com", copyConstructor.getVideoLink());

        MultimediaSong cloned = original.clone();
        assertEquals("link.com", cloned.getVideoLink());
    }
}