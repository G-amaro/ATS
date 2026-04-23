package org.Model.Music;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicMultimediaTest {

    // ==========================================
    // 1. TESTES DOS CONSTRUTORES
    // ==========================================
    @Test
    void testConstructors_InitializeCorrectly() {
        // Construtor Vazio
        MusicMultimedia empty = new MusicMultimedia();
        assertEquals("", empty.getUrl(), "Empty constructor should set URL to empty string.");
        assertEquals("", empty.getName(), "Should inherit empty fields from superclass.");

        // Construtor Completo
        MusicMultimedia full = new MusicMultimedia("Song", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "https://video.com");
        assertEquals("https://video.com", full.getUrl());
        assertEquals("Song", full.getName());

        // Construtor Cópia
        MusicMultimedia copy = new MusicMultimedia(full);
        assertEquals("https://video.com", copy.getUrl());
        assertEquals("Song", copy.getName());
    }

    // ==========================================
    // 2. TESTES DE GETTERS E SETTERS
    // ==========================================
    @Test
    void testGettersAndSetters_UpdatesUrl() {
        MusicMultimedia mm = new MusicMultimedia();
        mm.setUrl("https://new-url.com");
        assertEquals("https://new-url.com", mm.getUrl(), "Setter should update the URL.");
    }

    // ==========================================
    // 3. TESTE DE CLONE E TOSTRING
    // ==========================================
    @Test
    void testClone_ReturnsIdenticalCopy() {
        MusicMultimedia original = new MusicMultimedia("Song", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "https://url.com");
        MusicMultimedia cloned = original.clone();

        assertNotSame(original, cloned, "Clone should return a new memory instance.");
        assertEquals(original.getUrl(), cloned.getUrl());
        assertEquals(original.getName(), cloned.getName());
    }

    @Test
    void testToString_ReturnsFormattedString() {
        MusicMultimedia mm = new MusicMultimedia("Song", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "https://url.com");
        String result = mm.toString();

        assertTrue(result.contains("https://url.com"), "toString should contain the URL.");
        assertTrue(result.contains("MusicaMultimedia"), "toString should contain the class label.");
    }

    // ==========================================
    // 4. TESTE DO EQUALS (A caça aos 82% -> 100%)
    // ==========================================
    @Test
    void testEquals_VariousConditions_ReturnsExpectedBoolean() {
        MusicMultimedia base = new MusicMultimedia("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, "http://url.com");
        MusicMultimedia same = new MusicMultimedia("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, "http://url.com");

        MusicMultimedia diffSuper = new MusicMultimedia("Different", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, "http://url.com");
        MusicMultimedia diffUrl = new MusicMultimedia("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, "http://other.com");

        // Mutações de URL a null para cobrir as ramificações finais!
        MusicMultimedia nullUrl1 = new MusicMultimedia("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, null);
        MusicMultimedia nullUrl2 = new MusicMultimedia("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false, null);

        // 1. Identidade e Tipos
        assertTrue(base.equals(base), "Should equal itself.");
        assertFalse(base.equals(null), "Should handle null.");
        assertFalse(base.equals(new Object()), "Should handle different classes.");

        // 2. Igualdade Perfeita
        assertTrue(base.equals(same), "Should equal identical object.");

        // 3. Falha no super.equals() (Se um dos campos da classe pai for diferente)
        assertFalse(base.equals(diffSuper), "Should return false if superclass fields differ.");

        // 4. Falha no URL
        assertFalse(base.equals(diffUrl), "Should return false if URLs differ.");

        // 5. O Segredo dos 100%: Mutações do Null no URL!
        assertTrue(nullUrl1.equals(nullUrl2), "Should return true if both URLs are null.");
        assertFalse(nullUrl1.equals(base), "Should return false if this URL is null but the other is not.");
        assertFalse(base.equals(nullUrl1), "Should return false if this URL is not null but the other is.");
    }
}