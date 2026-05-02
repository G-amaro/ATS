package org.Model;

import org.Exceptions.*;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Plan.PlanPremiumTop;
import org.Model.Playlist.Playlist;
import org.Model.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMTest {

    private SpotifUM spotifUM;
    private User admin;

    @BeforeEach
    void setUp() throws Exception {
        spotifUM = new SpotifUM();

        admin = new User("admin", "a@mail.com", "Rua A", "1234");
        admin.setPlan(new PlanPremiumTop());

        spotifUM.addNewUser("admin", "a@mail.com", "Rua A", "1234");
        spotifUM.getUsers().get("admin").setPlan(new PlanPremiumTop());

        spotifUM.authenticateUser("admin", "1234");
    }

    @Test
    void testConstructors_AndLambdas() {
        SpotifUM copy = new SpotifUM(spotifUM);
        assertEquals(spotifUM.getUsers().size(), copy.getUsers().size());

        SpotifUM full = new SpotifUM(
                spotifUM.getMusics(),
                spotifUM.getPublicPlaylists(),
                spotifUM.getUsers(),
                spotifUM.getAlbums(),
                spotifUM.getArtistReproductions(),
                spotifUM.getGenreReproductions()
        );
        assertNotNull(full);
    }

    @Test
    void testMusicAndAlbum_Operations() throws Exception {
        spotifUM.addNewAlbum("Album 1", "Artista 1");
        spotifUM.addNewMusic("Song 1", "Artista 1", "Pub", "Lyrics", "Fig", "Pop", "Album 1", 180, false, null);

        assertTrue(spotifUM.musicExists("Song 1"));
        assertTrue(spotifUM.albumExists("Album 1"));

        assertDoesNotThrow(() -> spotifUM.playMusic("Song 1"));
        assertThrows(NotFoundException.class, () -> spotifUM.playMusic("Ghost"));

        assertNotNull(spotifUM.getMusicByName("Song 1"));
        assertNotNull(spotifUM.getAlbumByName("Album 1"));

        Music m = spotifUM.getMusicByName("Song 1");
        assertDoesNotThrow(() -> spotifUM.addMusicToAlbum("Album 1", m));
    }

    @Test
    void testUser_Operations() throws Exception {
        spotifUM.setCurrentUserEmail("novo@mail.com");
        assertEquals("novo@mail.com", spotifUM.getCurrentUser().getEmail());

        assertThrows(NotFoundException.class, () -> spotifUM.authenticateUser("fantasma", "123"));

        spotifUM.addNewUser("login_test", "test@mail.com", "Rua X", "pass123");
        assertThrows(UnsupportedOperationException.class, () -> spotifUM.authenticateUser("login_test", "senha_errada"));

        spotifUM.authenticateUser("login_test", "pass123");
        assertTrue(spotifUM.isPasswordCorrect("pass123"));
        assertFalse(spotifUM.isPasswordCorrect("errada"));
    }

    @Test
    void testPlaylists_Operations() throws Exception {
        spotifUM.addNewAlbum("Alb", "Art");
        spotifUM.addNewMusic("M1", "Art", "P", "L", "F", "Pop", "Alb", 100, false, null);

        spotifUM.addToCurrentUserPlaylist("Minha Play");
        int pId = spotifUM.getCurrentUser().getPlaylists().get(0).getId();

        spotifUM.addMusicToCurrentUserPlaylist(pId, "M1");

        assertDoesNotThrow(() -> spotifUM.setPlaylistAsPublic(pId));
        assertEquals(1, spotifUM.getPublicPlaylistSize());
        assertNotNull(spotifUM.getPublicPlaylistById(pId));

        assertDoesNotThrow(() -> spotifUM.removeMusicFromPlaylist("M1", pId));
    }

    @Test
    void testStatistics_Operations() throws Exception {
        spotifUM.addNewAlbum("Alb", "Art");
        spotifUM.addNewMusic("M1", "Art", "P", "L", "F", "Pop", "Alb", 100, false, null);

        spotifUM.incrementArtistReproductions("Art");
        spotifUM.incrementGenreReproductions("Pop");
        spotifUM.addToCurrentUserMusicReproductions("M1");

        assertNotNull(spotifUM.getTopArtistName());
        assertNotNull(spotifUM.getGenreWithMostReproductions());
        assertNotNull(spotifUM.mostReproducedMusic());
        assertNotNull(spotifUM.getUserWithMostPoints());
        assertNotNull(spotifUM.getUserWithMostPlaylists());

        LocalDate now = LocalDate.now();
        assertNotNull(spotifUM.getUserWithMostReproductions(now.minusDays(1), now.plusDays(1)));
    }

    @Test
    void testEquals_Refinado() throws Exception {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        assertTrue(s1.equals(s1));
        assertFalse(s1.equals(null));
        
        s1.addNewAlbum("Alb", "Art");
        s1.addNewMusic("M1", "Art", "P", "L", "F", "Pop", "Alb", 100, false, null);
        assertFalse(s1.equals(s2));
        
        s2 = new SpotifUM(s1);
        s1.addNewUser("admin", "e", "m", "p");
        s1.authenticateUser("admin", "p");
        assertFalse(s1.equals(s2));
        
        s2.addNewUser("user2", "e", "m", "p");
        s2.authenticateUser("user2", "p");
        assertFalse(s1.equals(s2));
    }

    @Test
    void testGlobalSettersAndGetters() {
        Map<String, User> newUsers = new HashMap<>();
        newUsers.put("testUser", new User("test", "test@mail", "rua", "pass"));
        
        Map<String, Music> newMusics = new HashMap<>();
        newMusics.put("testMusic", new Music());

        Map<String, Album> newAlbums = new HashMap<>();
        newAlbums.put("testAlbum", new Album());

        Map<Integer, Playlist> newPlaylists = new HashMap<>();
        newPlaylists.put(1, new Playlist());

        spotifUM.setUsers(newUsers);
        spotifUM.setMusics(newMusics);
        spotifUM.setAlbums(newAlbums);
        spotifUM.setPublicPlaylists(newPlaylists);

        assertEquals(1, spotifUM.getUsers().size());
        assertEquals(1, spotifUM.getMusics().size());
        assertEquals(1, spotifUM.getAlbums().size());
        assertEquals(1, spotifUM.getPublicPlaylists().size());
    }

    @Test
    void testStatistics_EmptySystem_ThrowsExceptions() {
        SpotifUM emptySystem = new SpotifUM();

        assertThrows(NoArtistsInDatabaseException.class, 
            () -> emptySystem.getTopArtistName());
            
        assertThrows(NoReproductionsInDatabaseException.class, 
            () -> emptySystem.getGenreWithMostReproductions());
            
        assertThrows(NoMusicsInDatabaseException.class, 
            () -> emptySystem.mostReproducedMusic());
            
        assertThrows(NoUsersInDatabaseException.class, 
            () -> emptySystem.getUserWithMostPoints());
            
        assertThrows(NoUsersInDatabaseException.class, 
            () -> emptySystem.getUserWithMostPlaylists());
        
        LocalDate now = LocalDate.now();
        assertThrows(NoUsersInDatabaseException.class, 
            () -> emptySystem.getUserWithMostReproductions(now.minusDays(1), now.plusDays(1)));
    }

    @Test
    void testAlreadyExistsExceptions() {
        assertDoesNotThrow(() -> spotifUM.addNewAlbum("Unico", "Art"));
        
        assertDoesNotThrow(() -> spotifUM.addNewMusic("Som Unico", "Art", "P", "L", "F", "Pop", "Unico", 100, false, null));
        
        assertThrows(AlreadyExistsException.class, 
            () -> spotifUM.addNewMusic("Som Unico", "Outro Art", "P", "L", "F", "Rock", "Unico", 200, false, null));
    }

    @Test
    void testMassiveMethods_PopulateAndLists() {
        SpotifUM sys = new SpotifUM();
        
        assertDoesNotThrow(() -> sys.populateDatabase());
        assertNotNull(sys.listAllMusics());
        assertNotNull(sys.listAllAlbums());
        assertNotNull(sys.listPublicPlaylists());
        assertNotNull(sys.listAllGenres());
        assertDoesNotThrow(() -> sys.authenticateUser("simao", "root"));
        assertNotNull(sys.listCurrentUserPlaylists());
        
        int playlistId = sys.getCurrentUser().getPlaylists().get(0).getId();
        assertDoesNotThrow(() -> sys.listAllMusicsInPlaylist(playlistId));
    }

    @Test
    void testPlaylistGenerators() {
        SpotifUM sys = new SpotifUM();
        sys.populateDatabase(); 
        
        assertDoesNotThrow(() -> sys.authenticateUser("simao", "root"));

        assertNotNull(sys.getRandomPlaylist());
        assertNotNull(sys.createFavoritesPlaylist(500, false));
    }

    @Test
    void testUserProfileAndPermissions() {
        spotifUM.setCurrentUserPassword("newpass");
        assertTrue(spotifUM.isPasswordCorrect("newpass"));

        spotifUM.setCurrentUserUsername("admin_temp");
        assertEquals("admin_temp", spotifUM.getCurrentUser().getUsername());
        
        assertThrows(IllegalArgumentException.class, 
            () -> spotifUM.changeCurrentUserName("admin_temp")); 
            
        spotifUM.addNewUser("outro", "o@m.com", "Rua", "pass");
        assertThrows(IllegalArgumentException.class, 
            () -> spotifUM.changeCurrentUserName("outro"));
        
        assertDoesNotThrow(() -> spotifUM.changeCurrentUserName("admin_novo"));
        assertTrue(spotifUM.userExists("admin_novo"));
        
        assertTrue(spotifUM.hasLibrary()); 
        spotifUM.addPointsToCurrentUser();
        assertEquals("PremiumTop", spotifUM.getCurrentUserPlanName());
        assertTrue(spotifUM.canCurrentUserSkip());
        assertTrue(spotifUM.canCurrentUserChooseWhatToPlay());
        assertTrue(spotifUM.currentUserAccessToFavorites());
    }

    @Test
    void testMorePlaylistMethodsAndCatches() throws Exception {
        spotifUM.addPlaylist("Public Mix", "DJ");
        assertEquals(1, spotifUM.getPublicPlaylistSize());

        spotifUM.addNewAlbum("Alb2", "Art2");
        spotifUM.addNewMusic("PopSong", "Art2", "P", "L", "F", "Pop", "Alb2", 100, false, null);
        assertDoesNotThrow(() -> spotifUM.createGenrePlaylist("My Pop", "Pop", 500));
        assertThrows(AlreadyExistsException.class, 
            () -> spotifUM.createGenrePlaylist("My Pop", "Pop", 500));
        
        String result = spotifUM.listAllMusicsInPlaylist(999);
        assertTrue(result.contains("999"));
        
        int pubId = spotifUM.getPublicPlaylists().keySet().iterator().next(); 
        assertDoesNotThrow(() -> spotifUM.addPlaylistToCurrentUserLibrary(pubId));
        assertThrows(AlreadyExistsException.class, 
            () -> spotifUM.addPlaylistToCurrentUserLibrary(pubId));
        assertThrows(NotFoundException.class, 
            () -> spotifUM.addPlaylistToCurrentUserLibrary(999));
    }

    @Test
    void testRemainingGettersSettersAndToString() {
        assertNotNull(spotifUM.toString());
        
        Map<String, Integer> artReps = new HashMap<>();
        artReps.put("Art", 5);
        spotifUM.setArtistReproductions(artReps);
        assertEquals(5, spotifUM.getArtistReproductions().get("Art"));
        
        Map<String, Integer> genreReps = new HashMap<>();
        genreReps.put("Pop", 10);
        spotifUM.setGenreReproductions(genreReps);
        assertEquals(10, spotifUM.getGenreReproductions().get("Pop"));
    }

    @Test
    void testDeepBranches_SpotifUM() throws Exception {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();
        s2.addNewUser("temp", "e", "m", "p");
        s2.authenticateUser("temp", "p");
        assertFalse(s1.equals(s2));

        LocalDate start = LocalDate.of(2000, 1, 1);
        LocalDate end = LocalDate.of(2000, 1, 2);
        assertThrows(NoUsersInDatabaseException.class, 
            () -> spotifUM.getUserWithMostReproductions(start, end));
            
        assertNotNull(spotifUM.getUserWithMostReproductions());
    }

    @Test
    void testEquals_Extreme() throws Exception {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        assertTrue(s1.equals(s1));

        assertFalse(s1.equals("não é um spotifum"));

        assertTrue(s1.equals(s2));

        s2.addNewUser("u1", "e", "m", "p");
        s2.authenticateUser("u1", "p");
        assertFalse(s1.equals(s2));

        assertFalse(s2.equals(s1));

        s1.addNewUser("u2", "e", "m", "p");
        s1.authenticateUser("u2", "p");
        assertFalse(s1.equals(s2));

        s2 = new SpotifUM(s1); 
        s1.addNewAlbum("A1", "Art1");
        s1.addNewMusic("M1", "Art1", "P", "L", "F", "Pop", "A1", 100, false, null);
        assertFalse(s1.equals(s2));
    }

    @Test
    void testExtremeEquals_PlaylistsAndMusics() throws Exception {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        s1.addPlaylist("Pub1", "Autor");
        assertFalse(s1.equals(s2));

        s1 = new SpotifUM();
        s1.addNewAlbum("AlbExtreme", "ArtExtreme");
        s1.addNewMusic("MusicaExtreme", "ArtExtreme", "P", "L", "F", "Pop", "AlbExtreme", 100, false, null);
        assertFalse(s1.equals(s2));
    }

    @Test
    void testToStringAndGetters_MutantKiller() throws Exception {
        SpotifUM sys = new SpotifUM();
        sys.addNewUser("admin_mutante", "email@m", "Rua", "pass");
        
        String str = sys.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());

        Map<String, User> usersMap = sys.getUsers();
        usersMap.clear();
        assertEquals(1, sys.getUsers().size());
    }

    @Test
    void testStatisticsBoundaries_MutantKiller() throws Exception {
        SpotifUM sys = new SpotifUM();
        
        sys.addNewAlbum("A1", "Art1");
        sys.addNewAlbum("A2", "Art2");
        sys.addNewMusic("M1", "Art1", "P", "L", "F", "Pop", "A1", 100, false, null);
        sys.addNewMusic("M2", "Art2", "P", "L", "F", "Rock", "A2", 100, false, null);

        sys.incrementArtistReproductions("Art1");
        sys.incrementArtistReproductions("Art2");
        sys.incrementArtistReproductions("Art2");
        
        assertTrue(sys.getTopArtistName().contains("Art2"));

        sys.incrementGenreReproductions("Pop");
        sys.incrementGenreReproductions("Pop");
        sys.incrementGenreReproductions("Pop");
        sys.incrementGenreReproductions("Rock");
        
        assertTrue(sys.getGenreWithMostReproductions().contains("Pop"));
    }

    @Test
    void testEqualsExhaustive_MutantKiller() throws Exception {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        s1.addNewAlbum("AlbX", "ArtX");
        s2.addNewAlbum("AlbY", "ArtY");
        assertFalse(s1.equals(s2));

        s1 = new SpotifUM();
        s2 = new SpotifUM();
        s1.addNewUser("U1", "E1", "M1", "P1");
        s2.addNewUser("U2", "E2", "M2", "P2");
        assertFalse(s1.equals(s2));

        s1 = new SpotifUM();
        s2 = new SpotifUM();
        s1.addPlaylist("Pub1", "Autor");
        assertFalse(s1.equals(s2));
        
        assertFalse(s1.equals(new Object()));
    }

    @Test
    void testConstructors_DeepCopy_MutantKiller() {
        SpotifUM original = new SpotifUM();
        original.addNewUser("u1", "e1", "m1", "p1");
        original.addNewAlbum("Alb1", "Art1");
        
        SpotifUM copy = new SpotifUM(original);
        assertEquals(1, copy.getUsers().size());
        assertEquals(1, copy.getAlbums().size());

        SpotifUM param = new SpotifUM(original.getMusics(), original.getPublicPlaylists(), 
                                      original.getUsers(), original.getAlbums(), 
                                      original.getArtistReproductions(), original.getGenreReproductions());
        assertEquals(1, param.getUsers().size());
        assertEquals(1, param.getAlbums().size());
    }

    @Test
    void testSetCurrentUser_Assignment_MutantKiller() {
        SpotifUM sys = new SpotifUM();
        User novoAdmin = new User("boss", "boss@mail", "rua", "123");
        
        sys.setCurrentUser(novoAdmin);
        
        assertEquals(novoAdmin, sys.getCurrentUser());
    }

    @Test
    void testCurrentUserDelegations_MutantKiller() {

        SpotifUM sys = new SpotifUM();
        try {
            sys.addNewUser("userTest", "email@m.com", "Rua X", "pass");
            sys.authenticateUser("userTest", "pass");

            sys.addToCurrentUserPlaylist("Minha Play");
            sys.changeCurrentUserName("novoUserTest");
            
            if (sys.getCurrentUser() != null && !sys.getCurrentUser().getPlaylists().isEmpty()) {
                assertEquals("novoUserTest", sys.getCurrentUser().getPlaylists().get(0).getAutor());
            }

            sys.getCurrentUser().setPlan(new PlanPremiumTop());
            assertTrue(sys.hasLibrary());

            assertDoesNotThrow(() -> sys.addPointsToCurrentUser());
        } catch (Exception e) {
        }
    }

}