package org.Model.Music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MusicTest {
    
    private Music music;
    private final String NAME = "Bohemian Rhapsody";
    private final String INTERPRETER = "Queen";
    private final String PUBLISHER = "EMI";
    private final String LYRICS = "Is this the real life? Is this just fantasy?";
    private final String MUSICAL_FIGURES = "A-B-C-D";
    private final String GENRE = "Rock";
    private final String ALBUM = "A Night at the Opera";
    private final int DURATION = 355;
    private final boolean EXPLICIT = false;
    
    @BeforeEach
    public void setUp() {
        // Make sure the music object is properly initialized
        music = new Music(NAME, INTERPRETER, PUBLISHER, LYRICS, MUSICAL_FIGURES, 
                          GENRE, ALBUM, DURATION, EXPLICIT);
    }
    
    @Test
    public void testDefaultConstructor() {
        Music defaultMusic = new Music();
        
        assertEquals("", defaultMusic.getName());
        assertEquals("", defaultMusic.getInterpreter());
        assertEquals("", defaultMusic.getPublisher());
        assertEquals("", defaultMusic.getLyrics());
        assertEquals("", defaultMusic.getMusicalFigures());
        assertEquals("", defaultMusic.getGenre());
        assertEquals("", defaultMusic.getAlbum());
        assertEquals(0, defaultMusic.getDuration());
        assertEquals(0, defaultMusic.getReproductions());
        assertFalse(defaultMusic.isExplicit());
    }
    
    @Test
    public void testConstructor() {
        assertEquals(NAME, music.getName());
        assertEquals(INTERPRETER, music.getInterpreter());
        assertEquals(PUBLISHER, music.getPublisher());
        assertEquals(LYRICS, music.getLyrics());
        assertEquals(MUSICAL_FIGURES, music.getMusicalFigures());
        assertEquals(GENRE, music.getGenre());
        assertEquals(ALBUM, music.getAlbum());
        assertEquals(DURATION, music.getDuration());
        assertEquals(0, music.getReproductions());
        assertEquals(EXPLICIT, music.isExplicit());
    }
    
    @Test
    public void testCopyConstructor() {
        Music musicCopy = new Music(music);
        
        assertEquals(music.getName(), musicCopy.getName());
        assertEquals(music.getInterpreter(), musicCopy.getInterpreter());
        assertEquals(music.getPublisher(), musicCopy.getPublisher());
        assertEquals(music.getLyrics(), musicCopy.getLyrics());
        assertEquals(music.getMusicalFigures(), musicCopy.getMusicalFigures());
        assertEquals(music.getGenre(), musicCopy.getGenre());
        assertEquals(music.getAlbum(), musicCopy.getAlbum());
        assertEquals(music.getDuration(), musicCopy.getDuration());
        assertEquals(music.getReproductions(), musicCopy.getReproductions());
        assertEquals(music.isExplicit(), musicCopy.isExplicit());
    }
    
    @Test
    public void testSetters() {
        String newName = "Another One Bites the Dust";
        String newInterpreter = "Queen";
        String newPublisher = "Elektra";
        String newLyrics = "Another one bites the dust";
        String newMusicalFigures = "E-F-G";
        String newGenre = "Rock";
        String newAlbum = "The Game";
        int newDuration = 215;
        int newReproductions = 5;
        boolean newExplicit = true;
        
        music.setName(newName);
        music.setInterpreter(newInterpreter);
        music.setPublisher(newPublisher);
        music.setLyrics(newLyrics);
        music.setMusicalFigures(newMusicalFigures);
        music.setGenre(newGenre);
        music.setAlbum(newAlbum);
        music.setDuration(newDuration);
        music.setReproductions(newReproductions);
        music.setExplicit(newExplicit);
        
        assertEquals(newName, music.getName());
        assertEquals(newInterpreter, music.getInterpreter());
        assertEquals(newPublisher, music.getPublisher());
        assertEquals(newLyrics, music.getLyrics());
        assertEquals(newMusicalFigures, music.getMusicalFigures());
        assertEquals(newGenre, music.getGenre());
        assertEquals(newAlbum, music.getAlbum());
        assertEquals(newDuration, music.getDuration());
        assertEquals(newReproductions, music.getReproductions());
        assertEquals(newExplicit, music.isExplicit());
    }
    
    @Test
    public void testClone() {
        Music musicClone = music.clone();
        
        assertEquals(music.getName(), musicClone.getName());
        assertEquals(music.getInterpreter(), musicClone.getInterpreter());
        assertEquals(music.getPublisher(), musicClone.getPublisher());
        assertEquals(music.getLyrics(), musicClone.getLyrics());
        assertEquals(music.getMusicalFigures(), musicClone.getMusicalFigures());
        assertEquals(music.getGenre(), musicClone.getGenre());
        assertEquals(music.getAlbum(), musicClone.getAlbum());
        assertEquals(music.getDuration(), musicClone.getDuration());
        assertEquals(music.getReproductions(), musicClone.getReproductions());
        assertEquals(music.isExplicit(), musicClone.isExplicit());
        
        // Ensure it's a deep copy
        musicClone.setName("Different");
        assertNotEquals(music.getName(), musicClone.getName());
    }
    


    
    @Test
    public void testToString() {
        String result = music.toString();
        
        assertTrue(result.contains(NAME));
        assertTrue(result.contains(INTERPRETER));
        assertTrue(result.contains(PUBLISHER));
        assertTrue(result.contains(LYRICS));
        assertTrue(result.contains(MUSICAL_FIGURES));
        assertTrue(result.contains(GENRE));
        assertTrue(result.contains(ALBUM));
        assertTrue(result.contains(String.valueOf(DURATION)));
    }
    
    @Test
    public void testPlay() {
        int initialReproductions = music.getReproductions();
        String result = music.play();
        
        assertEquals(LYRICS, result);
        assertEquals(initialReproductions + 1, music.getReproductions());
    }

    @Test
    void testEquals_VariousConditions_ReturnsExpectedBoolean() {
        Music base = new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false);
        Music same = new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false);

        // 1. Identidade e Tipos
        assertTrue(base.equals(base), "Should equal itself.");
        assertFalse(base.equals(null), "Should handle null.");
        assertFalse(base.equals(new Object()), "Should handle different classes.");
        assertTrue(base.equals(same), "Should equal identical object.");

        // 2. Diferenças individuais em cada campo
        assertFalse(base.equals(new Music("X", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false))); // Name diff
        assertFalse(base.equals(new Music("Name", "X", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false))); // Artist diff
        assertFalse(base.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "X", "Album", 180, false))); // Genre diff
        assertFalse(base.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "X", 180, false))); // Album diff
        assertFalse(base.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 99, false))); // Duration diff
        assertFalse(base.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, true))); // Explicit diff

        // 3. O Segredo dos 100%: Mutações de Null em todos os campos protegidos!
        Music nullName = new Music(null, "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false);
        assertFalse(nullName.equals(base));
        assertFalse(base.equals(nullName));
        assertTrue(nullName.equals(new Music(null, "Artist", "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false)));

        Music nullArtist = new Music("Name", null, "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false);
        assertFalse(nullArtist.equals(base));
        assertFalse(base.equals(nullArtist));
        assertTrue(nullArtist.equals(new Music("Name", null, "Pub", "Lyrics", "Fig", "Genre", "Album", 180, false)));

        Music nullGenre = new Music("Name", "Artist", "Pub", "Lyrics", "Fig", null, "Album", 180, false);
        assertFalse(nullGenre.equals(base));
        assertFalse(base.equals(nullGenre));
        assertTrue(nullGenre.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", null, "Album", 180, false)));

        Music nullAlbum = new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", null, 180, false);
        assertFalse(nullAlbum.equals(base));
        assertFalse(base.equals(nullAlbum));
        assertTrue(nullAlbum.equals(new Music("Name", "Artist", "Pub", "Lyrics", "Fig", "Genre", null, 180, false)));
    }


    @Test
    void testToString_ValidMusic_ReturnsFormattedString() {
        // Testar o caminho do Explicita: "Nao"
        Music m1 = new Music("Bohemian Rhapsody", "Queen", "EMI", "Mama...", "Fig", "Rock", "A Night at the Opera", 354, false);
        assertTrue(m1.toString().contains("Explicita: Nao"), "Should contain Nao for non-explicit music.");

        // Testar o caminho do Explicita: "Sim" (O 2% que faltava!)
        Music m2 = new Music("WAP", "Cardi B", "Label", "Yeah", "Fig", "Rap", "Album", 200, true);
        assertTrue(m2.toString().contains("Explicita: Sim"), "Should contain Sim for explicit music.");
    }


}