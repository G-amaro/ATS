package org.Controller;

import org.Controller.dtos.MusicInfo;
import org.Model.SpotifUM;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Music.MusicMultimedia;
import org.Model.Music.MusicReproduction;
import org.Model.Plan.Plan;
import org.Model.Plan.PlanFree;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;
import org.Model.Playlist.Playlist;
import org.Model.Playlist.PlaylistFavorites;
import org.Model.Playlist.PlaylistRandom;
import org.Model.User.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTest {

    @Mock
    private SpotifUM spotifUMMock;

    private Controller controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new Controller(spotifUMMock);
    }

    // ==========================================
    // 1. CONSTRUCTORS & SETUP
    // ==========================================

    @Test
    void testControllerConstructors_InitializeCorrectly() {
        Controller defaultController = new Controller();
        assertNotNull(defaultController.getSpotifUM(), "Default constructor should initialize SpotifUM");

        Controller copyController = new Controller(defaultController);
        assertNotNull(copyController.getSpotifUM(), "Copy constructor should initialize SpotifUM");

        copyController.setSpotifUM(new SpotifUM());
        assertNotNull(copyController.getSpotifUM(), "Setter should correctly assign SpotifUM");
    }

    // ==========================================
    // 2. AUTHENTICATION & USER MANAGEMENT
    // ==========================================

    @Test
    void testLoginWithMessage_ValidCredentials_ReturnsTrue() throws Exception {
        doNothing().when(spotifUMMock).authenticateUser("admin", "pass123");
        assertTrue(controller.loginWithMessage("admin", "pass123"));
    }

    @Test
    void testLoginWithMessage_InvalidCredentials_ReturnsFalse() throws Exception {
        doThrow(new RuntimeException("Credenciais Inválidas")).when(spotifUMMock).authenticateUser("admin", "wrongpass");
        assertFalse(controller.loginWithMessage("admin", "wrongpass"));
    }

    @Test
    void testAddNewUser_ExistingUsername_ReturnsErrorMessage() {
        when(spotifUMMock.userExists("john_doe")).thenReturn(true);
        assertTrue(controller.addNewUser("john_doe", "john@mail.com", "Address", "pass").contains("Já existe"));
    }

    @Test
    void testAddNewUser_NewUsername_ReturnsSuccessMessage() {
        when(spotifUMMock.userExists("john_doe")).thenReturn(false);
        assertTrue(controller.addNewUser("john_doe", "john@mail.com", "Address", "pass").contains("sucesso"));
    }

    @Test
    void testProfileManagement_ValidActions_ReturnsSuccessMessages() throws Exception {
        assertTrue(controller.changeCurrentUserUserName("NewName").contains("sucesso"));
        assertTrue(controller.changeCurrentUserEmail("new@mail.com").contains("sucesso"));
        assertTrue(controller.changeCurrentUserPassword("newPass").contains("sucesso"));

        User mockUser = mock(User.class);
        when(mockUser.toString()).thenReturn("User Profile Info");
        when(spotifUMMock.getCurrentUser()).thenReturn(mockUser);
        assertTrue(controller.getCurrentUser().contains("User Profile Info"));
    }

    @Test
    void testProfileManagement_SpotifUMThrowsException_ReturnsErrorMessage() throws Exception {
        doThrow(new RuntimeException("Error")).when(spotifUMMock).changeCurrentUserName(anyString());
        assertTrue(controller.changeCurrentUserUserName("Name").contains("Erro"));
    }

    // ==========================================
    // 3. MEDIA ADDITION & CREATION
    // ==========================================

    @Test
    void testAddMusic_ValidData_ReturnsSuccessMessage() throws Exception {
        doNothing().when(spotifUMMock).addNewMusic(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyString());
        assertTrue(controller.addMusic("Song", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "").contains("sucesso"));
    }

    @Test
    void testAddMusic_SpotifUMThrowsException_ReturnsErrorMessage() throws Exception {
        doThrow(new RuntimeException("Database Error")).when(spotifUMMock).addNewMusic(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyBoolean(), anyString());
        assertTrue(controller.addMusic("Song", "Artist", "Pub", "Lyrics", "Fig", "Pop", "Album", 180, false, "").contains("Erro"));
    }

    @Test
    void testCreateAlbum_ValidAndInvalidStates_ReturnsExpectedMessages() throws Exception {
        doNothing().when(spotifUMMock).addNewAlbum("Album", "Artist");
        assertTrue(controller.createAlbum("Album", "Artist").contains("sucesso"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).addNewAlbum("Invalid", "Artist");
        assertTrue(controller.createAlbum("Invalid", "Artist").contains("Erro"));
    }

    // ==========================================
    // 4. MUSIC PLAYBACK (CORE FUNCTIONALITY)
    // ==========================================

    @Test
    void testPlayMusic_StandardMusic_ReturnsMusicInfo() throws Exception {
        Music musicMock = mock(Music.class);
        when(musicMock.getName()).thenReturn("Standard Song");
        when(musicMock.getInterpreter()).thenReturn("Artist");

        when(musicMock.getGenre()).thenReturn("Pop");
        when(musicMock.getLyrics()).thenReturn("Letra da musica");
        when(musicMock.isExplicit()).thenReturn(false);

        when(spotifUMMock.getMusicByName("Standard Song")).thenReturn(musicMock);

        MusicInfo result = controller.playMusic("Standard Song");
        assertNotNull(result, "MusicInfo should be created successfully");
        assertEquals("Standard Song", result.getMusicName());

        verify(spotifUMMock, times(1)).playMusic("Standard Song");
    }

    @Test
    void testPlayMusic_MultimediaMusic_ExtractsUrlSuccessfully() throws Exception {
        MusicMultimedia multimediaInstance = new MusicMultimedia("Video Song", "Artist X", "Pub", "Lyrics", "Figs", "Pop", "Album", 180, false, "https://url.com");
        when(spotifUMMock.getMusicByName("Video Song")).thenReturn(multimediaInstance);

        MusicInfo result = controller.playMusic("Video Song");
        assertNotNull(result, "MusicInfo should be created for multimedia instance");
    }

    @Test
    void testPlayMusic_MusicNotFound_ReturnsMusicInfoWithError() throws Exception {
        when(spotifUMMock.getMusicByName("Ghost Song")).thenThrow(new RuntimeException("Not Found"));
        MusicInfo result = controller.playMusic("Ghost Song");
        assertNotNull(result, "MusicInfo should encapsulate the error message");
    }

    // ==========================================
    // 5. PLAYLIST MANAGEMENT
    // ==========================================

    @Test
    void testPlaylistManagement_ValidActions_ReturnsSuccess() throws Exception {
        Playlist mockPlaylist = mock(Playlist.class);
        when(mockPlaylist.getName()).thenReturn("My Playlist");
        when(spotifUMMock.getUserPlaylistById(1)).thenReturn(mockPlaylist);
        when(spotifUMMock.getPublicPlaylistById(2)).thenReturn(mockPlaylist);

        assertTrue(controller.addMusicToCurrentUserPlaylist(1, "Song A").contains("Song A"));
        assertTrue(controller.removeMusicFromPlaylist("Song A", 1).contains("Song A"));
        assertTrue(controller.setPlaylistAsPublic(1).contains("pública"));
        assertTrue(controller.addPublicPlaylistToLibrary(2).contains("adicionada"));
        assertTrue(controller.addToCurrentUserPlaylists("New Playlist").contains("New Playlist"));
        assertTrue(controller.createGenrePlaylist("GenrePlay", "Rock", 60).contains("sucesso"));
    }

    @Test
    void testPlaylistManagement_SpotifUMThrowsException_ReturnsErrorMessages() throws Exception {
        doThrow(new RuntimeException("Error")).when(spotifUMMock).addToCurrentUserPlaylist(anyString());
        assertTrue(controller.addToCurrentUserPlaylists("P1").contains("Erro"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).addMusicToCurrentUserPlaylist(anyInt(), anyString());
        assertTrue(controller.addMusicToCurrentUserPlaylist(1, "M1").contains("Erro"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).addPlaylistToCurrentUserLibrary(anyInt());
        assertTrue(controller.addPublicPlaylistToLibrary(1).contains("Erro"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).removeMusicFromPlaylist(anyString(), anyInt());
        assertTrue(controller.removeMusicFromPlaylist("M1", 1).contains("Erro"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).setPlaylistAsPublic(anyInt());
        assertTrue(controller.setPlaylistAsPublic(1).contains("Erro"));

        doThrow(new RuntimeException("Error")).when(spotifUMMock).createGenrePlaylist(anyString(), anyString(), anyInt());
        assertTrue(controller.createGenrePlaylist("P", "Rock", 60).contains("Erro"));
    }

    // ==========================================
    // 6. LISTINGS & EXTRACTIONS
    // ==========================================

    @Test
    void testListings_WithData_ReturnsFormattedStrings() throws Exception {
        when(spotifUMMock.listAllMusics()).thenReturn("Song 1\nSong 2");
        assertTrue(controller.listAllMusics().contains("Song 1"));

        when(spotifUMMock.listAllAlbums()).thenReturn("Album A");
        assertTrue(controller.listAllAlbums().contains("Album A"));

        when(spotifUMMock.listAllGenres()).thenReturn("Pop");
        assertTrue(controller.listAllGenres().contains("Pop"));

        when(spotifUMMock.listAllMusicsInPlaylist(1)).thenReturn("Song X");
        assertTrue(controller.listPlaylistMusics(1).contains("Song X"));

        when(spotifUMMock.listCurrentUserPlaylists()).thenReturn("My Playlist");
        assertTrue(controller.listUserPlaylists().contains("My Playlist"));

        when(spotifUMMock.listPublicPlaylists()).thenReturn("Public Play");
        assertTrue(controller.listPublicPlaylists().contains("Public Play"));
    }

    @Test
    void testListings_EmptyData_ReturnsEmptyMessages() throws Exception {
        when(spotifUMMock.listAllMusics()).thenReturn("");
        assertTrue(controller.listAllMusics().contains("Não existem"));

        when(spotifUMMock.listAllAlbums()).thenReturn("");
        assertTrue(controller.listAllAlbums().contains("Não existem"));

        when(spotifUMMock.listAllGenres()).thenReturn("");
        assertTrue(controller.listAllGenres().contains("Não existem"));

        when(spotifUMMock.listCurrentUserPlaylists()).thenReturn("");
        assertTrue(controller.listUserPlaylists().contains("Não existem"));

        when(spotifUMMock.listPublicPlaylists()).thenReturn("");
        assertTrue(controller.listPublicPlaylists().contains("Não existem"));

        when(spotifUMMock.listAllMusicsInPlaylist(1)).thenReturn("");
        assertEquals("", controller.listPlaylistMusics(1));
    }

    @Test
    void testListings_ExceptionsThrown_ReturnsErrorMessages() throws Exception {
        when(spotifUMMock.listAllMusics()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listAllMusics().contains("Erro"));

        when(spotifUMMock.listAllAlbums()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listAllAlbums().contains("Erro"));

        when(spotifUMMock.listAllGenres()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listAllGenres().contains("Erro"));

        when(spotifUMMock.listCurrentUserPlaylists()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listUserPlaylists().contains("Erro"));

        when(spotifUMMock.listPublicPlaylists()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listPublicPlaylists().contains("Erro"));

        when(spotifUMMock.listAllMusicsInPlaylist(anyInt())).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.listPlaylistMusics(1).contains("Erro"));
    }

    // ==========================================
    // 7. STATISTICS & METRICS
    // ==========================================

    @Test
    void testStatistics_ValidData_ReturnsCorrectStrings() throws Exception {
        Music musicMock = mock(Music.class);
        when(musicMock.getName()).thenReturn("Top Song");
        when(spotifUMMock.mostReproducedMusic()).thenReturn(musicMock);
        assertTrue(controller.getMostReproducedMusic().contains("Top Song"));

        when(spotifUMMock.getTopArtistName()).thenReturn("Top Artist");
        assertTrue(controller.getMostReproducedArtist().contains("Top Artist"));

        when(spotifUMMock.getGenreWithMostReproductions()).thenReturn("Rock");
        assertTrue(controller.getMostReproducedGenre().contains("Rock"));

        User userMock = mock(User.class);
        Plan planMock = mock(Plan.class);
        when(userMock.getUsername()).thenReturn("UserMaster");
        when(planMock.getPoints()).thenReturn(500);
        when(userMock.getPlan()).thenReturn(planMock);
        when(userMock.getUserPlaylistCount()).thenReturn(5);

        List<Playlist> fakePlaylists = new ArrayList<>();
        fakePlaylists.add(mock(Playlist.class));
        when(userMock.getPlaylists()).thenReturn(fakePlaylists);

        List<MusicReproduction> reps = new ArrayList<>();
        reps.add(mock(MusicReproduction.class));
        when(userMock.getMusicReproductions()).thenReturn(reps);

        when(spotifUMMock.getUserWithMostPoints()).thenReturn(userMock);
        assertTrue(controller.getUserWithMostPoints().contains("UserMaster"));

        when(spotifUMMock.getUserWithMostPlaylists()).thenReturn(userMock);
        assertTrue(controller.getUserWithMostPlaylists().contains("UserMaster"));

        when(spotifUMMock.getUserWithMostReproductions()).thenReturn(userMock);
        assertTrue(controller.getUserWithMostReproductions().contains("UserMaster"));

        when(spotifUMMock.getPublicPlaylistSize()).thenReturn(20);
        assertTrue(controller.getPublicPlaylistCount().contains("20"));
    }

    @Test
    void testStatistics_ExceptionsThrown_ReturnsErrorMessages() throws Exception {
        when(spotifUMMock.mostReproducedMusic()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getMostReproducedMusic().contains("Erro"));

        when(spotifUMMock.getTopArtistName()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getMostReproducedArtist().contains("Erro"));

        when(spotifUMMock.getGenreWithMostReproductions()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getMostReproducedGenre().contains("Erro"));

        when(spotifUMMock.getUserWithMostPoints()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getUserWithMostPoints().contains("Erro"));

        when(spotifUMMock.getUserWithMostPlaylists()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getUserWithMostPlaylists().contains("Erro"));

        when(spotifUMMock.getUserWithMostReproductions()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getUserWithMostReproductions().contains("Erro"));

        when(spotifUMMock.getPublicPlaylistSize()).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getPublicPlaylistCount().contains("Erro"));
    }

    @Test
    void testStatisticsByDate_ValidAndEmptyStates_ReturnsExpectedStrings() throws Exception {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        User userMock = mock(User.class);
        when(userMock.getUsername()).thenReturn("DateUser");
        when(userMock.getMusicReproductionsCount(start, end)).thenReturn(10);

        List<MusicReproduction> reps = new ArrayList<>();
        reps.add(mock(MusicReproduction.class));
        when(userMock.getMusicReproductions()).thenReturn(reps);

        when(spotifUMMock.getUserWithMostReproductions(start, end)).thenReturn(userMock);
        assertTrue(controller.getUserWithMostReproductions(start, end).contains("DateUser"));

        when(userMock.getMusicReproductions()).thenReturn(new ArrayList<>());
        assertTrue(controller.getUserWithMostReproductions(start, end).contains("Não existe"));

        when(spotifUMMock.getUserWithMostReproductions(start, end)).thenThrow(new RuntimeException("Error"));
        assertTrue(controller.getUserWithMostReproductions(start, end).contains("Erro"));
    }

    @Test
    void testStatistics_EmptyUsers_ReturnsNoUserMessages() throws Exception {
        User emptyUserMock = mock(User.class);
        when(emptyUserMock.getPlaylists()).thenReturn(new ArrayList<>());
        when(emptyUserMock.getMusicReproductions()).thenReturn(new ArrayList<>());

        when(spotifUMMock.getUserWithMostPlaylists()).thenReturn(emptyUserMock);
        assertTrue(controller.getUserWithMostPlaylists().contains("Não existe"));

        when(spotifUMMock.getUserWithMostReproductions()).thenReturn(emptyUserMock);
        assertTrue(controller.getUserWithMostReproductions().contains("Não existe"));
    }

    // ==========================================
    // 8. OPTIONALS & EXTRACTIONS
    // ==========================================

    @Test
    void testGetNamesOptionals_ValidEntities_ReturnsPresentOptional() throws Exception {
        User userMock = mock(User.class);
        Playlist pMock = mock(Playlist.class);
        Music mMock = mock(Music.class);

        when(mMock.getName()).thenReturn("Song");
        List<Music> musics = new ArrayList<>();
        musics.add(mMock);

        when(pMock.getMusics()).thenReturn(musics);
        when(userMock.getPlaylistById(1)).thenReturn(pMock);
        when(spotifUMMock.getCurrentUser()).thenReturn(userMock);
        assertTrue(controller.getPlaylistMusicNames(1).isPresent());

        Album aMock = mock(Album.class);
        when(aMock.getMusics()).thenReturn(musics);
        when(spotifUMMock.getAlbumByName("A1")).thenReturn(aMock);
        assertTrue(controller.getAlbumMusicNames("A1").isPresent());

        PlaylistFavorites pfMock = mock(PlaylistFavorites.class);
        when(pfMock.getMusics()).thenReturn(musics);
        when(spotifUMMock.createFavoritesPlaylist(60, false)).thenReturn(pfMock);
        assertTrue(controller.getFavoritePlaylistMusicNames(false, 60).isPresent());

        PlaylistRandom prMock = mock(PlaylistRandom.class);
        when(prMock.getMusics()).thenReturn(musics);
        when(spotifUMMock.getRandomPlaylist()).thenReturn(prMock);
        assertTrue(controller.getRandomPlaylistMusicNames().isPresent());
    }

    @Test
    void testGetNamesOptionals_EmptyEntities_ReturnsEmptyOptionalList() throws Exception {
        List<Music> emptyMusicList = new ArrayList<>();

        User mockUser = mock(User.class);
        Playlist mockPlaylist = mock(Playlist.class);
        when(mockPlaylist.getMusics()).thenReturn(emptyMusicList);
        when(mockUser.getPlaylistById(99)).thenReturn(mockPlaylist);
        when(spotifUMMock.getCurrentUser()).thenReturn(mockUser);
        assertTrue(controller.getPlaylistMusicNames(99).get().isEmpty());

        Album mockAlbum = mock(Album.class);
        when(mockAlbum.getMusics()).thenReturn(emptyMusicList);
        when(spotifUMMock.getAlbumByName("EmptyAlbum")).thenReturn(mockAlbum);
        assertTrue(controller.getAlbumMusicNames("EmptyAlbum").get().isEmpty());

        PlaylistFavorites mockFav = mock(PlaylistFavorites.class);
        when(mockFav.getMusics()).thenReturn(emptyMusicList);
        when(spotifUMMock.createFavoritesPlaylist(10, false)).thenReturn(mockFav);
        assertTrue(controller.getFavoritePlaylistMusicNames(false, 10).get().isEmpty());

        PlaylistRandom mockRand = mock(PlaylistRandom.class);
        when(mockRand.getMusics()).thenReturn(emptyMusicList);
        when(spotifUMMock.getRandomPlaylist()).thenReturn(mockRand);
        assertTrue(controller.getRandomPlaylistMusicNames().get().isEmpty());
    }

    @Test
    void testGetNamesOptionals_ExceptionsThrown_ReturnsEmptyOptional() throws Exception {
        User mockUser = mock(User.class);
        when(mockUser.getPlaylistById(anyInt())).thenThrow(new RuntimeException("Error"));
        when(spotifUMMock.getCurrentUser()).thenReturn(mockUser);
        assertFalse(controller.getPlaylistMusicNames(1).isPresent());

        when(spotifUMMock.getAlbumByName(anyString())).thenThrow(new RuntimeException("Error"));
        assertFalse(controller.getAlbumMusicNames("A1").isPresent());

        when(spotifUMMock.createFavoritesPlaylist(anyInt(), anyBoolean())).thenThrow(new RuntimeException("Error"));
        assertFalse(controller.getFavoritePlaylistMusicNames(true, 10).isPresent());

        when(spotifUMMock.getRandomPlaylist()).thenThrow(new RuntimeException("Error"));
        assertFalse(controller.getRandomPlaylistMusicNames().isPresent());
    }

    // ==========================================
    // 9. BOOLEANS, STATES & PLANS
    // ==========================================

    @Test
    void testBooleanChecks_VariousStates_ReturnsExpectedResults() {
        when(spotifUMMock.albumExists("A1")).thenReturn(true);
        assertTrue(controller.albumExists("A1"));

        when(spotifUMMock.musicExists("M1")).thenReturn(false);
        assertFalse(controller.musicExists("M1"));

        when(spotifUMMock.canCurrentUserSkip()).thenReturn(true);
        assertTrue(controller.canCurrentUserSkip());

        when(spotifUMMock.canCurrentUserChooseWhatToPlay()).thenReturn(false);
        assertFalse(controller.canCurrentUserChooseWhatToPlay());

        when(spotifUMMock.currentUserAccessToFavorites()).thenReturn(true);
        assertTrue(controller.currentUserAccessToFavorites());

        when(spotifUMMock.isPasswordCorrect("pass123")).thenReturn(true);
        assertTrue(controller.isPasswordCorrect("pass123"));
    }

    @Test
    void testCurrentUserHasLibrary_WithAndWithoutLibrary_ReturnsBoolean() {
        User uMock = mock(User.class);
        when(spotifUMMock.getCurrentUser()).thenReturn(uMock);

        when(uMock.hasLibrary()).thenReturn(true);
        assertTrue(controller.currentUserHasLibrary());

        when(uMock.hasLibrary()).thenReturn(false);
        assertFalse(controller.currentUserHasLibrary());

        when(spotifUMMock.getCurrentUser()).thenReturn(null);
        assertFalse(controller.currentUserHasLibrary());
    }

    @Test
    void testSetPlan_VariousPlans_ReturnsSuccessMessage() {
        User userMock = mock(User.class);
        Plan planMock = mock(Plan.class);
        when(userMock.getPlan()).thenReturn(planMock);
        when(spotifUMMock.getCurrentUser()).thenReturn(userMock);

        assertTrue(controller.setFreePlan().contains("Free"));
        assertTrue(controller.setPremiumBasePlan().contains("PremiumBase"));
        assertTrue(controller.setPremiumTopPlan().contains("PremiumTop"));

        when(spotifUMMock.getCurrentUserPlanName()).thenReturn("Free");
        assertEquals("Free", controller.getCurrentUserPlan());
    }

    @Test
    void testGetPlaylistId_ValidAndInvalidId_ReturnsNameOrError() throws Exception {
        User uMock = mock(User.class);
        Playlist pMock = mock(Playlist.class);
        when(pMock.getName()).thenReturn("Target Playlist");
        when(uMock.getPlaylistById(1)).thenReturn(pMock);
        when(spotifUMMock.getCurrentUser()).thenReturn(uMock);

        assertEquals("Target Playlist", controller.getPlaylistId(1));

        when(uMock.getPlaylistById(2)).thenThrow(new RuntimeException("Not Found"));
        assertTrue(controller.getPlaylistId(2).contains("Erro"));
    }

    // ==========================================
    // 10. FILE I/O (IMPORT/EXPORT)
    // ==========================================

    @Test
    void testImportExportModel_ValidPath_ReturnsSuccessMessage() {
        Controller realController = new Controller();
        String tempFilePath = "temp_spotifum_test_db.dat";

        String exportResult = realController.exportModel(tempFilePath);
        assertTrue(exportResult.contains("com sucesso"), "Export should succeed to a valid path");

        String importResult = realController.importModel(tempFilePath);
        assertTrue(importResult.contains("com sucesso"), "Import should succeed from a valid path");

        new File(tempFilePath).delete(); // Clean up
    }

    @Test
    void testImportExportModel_InvalidPath_ReturnsErrorMessage() {
        assertTrue(controller.importModel("non_existent_file.dat").contains("Erro"));
        assertTrue(controller.exportModel("invalid_directory/file.dat").contains("Erro"));
    }
}