package org.spotifumtp37.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.spotifumtp37.exceptions.AlreadyExistsException;
import org.spotifumtp37.exceptions.DoesntExistException;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpotifUMDataTest {

    private SpotifUMData spotifUMData;
    private Album mockAlbum;
    private User mockUser;
    private Playlist mockPlaylist;
    private Song mockSong;

    @BeforeEach
    void setUp() {
        spotifUMData = new SpotifUMData();

        // Setup Mock Album
        mockAlbum = mock(Album.class);
        when(mockAlbum.getTitle()).thenReturn("Test Album");
        when(mockAlbum.clone()).thenReturn(mockAlbum);

        // Setup Mock User
        mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("TestUser");
        when(mockUser.clone()).thenReturn(mockUser);

        // Setup Mock Playlist
        mockPlaylist = mock(Playlist.class);
        when(mockPlaylist.getPlaylistName()).thenReturn("Test Playlist");
        when(mockPlaylist.clone()).thenReturn(mockPlaylist);
        when(mockPlaylist.getCreator()).thenReturn(mockUser);

        // Setup Mock Song
        mockSong = mock(Song.class);
        when(mockSong.getName()).thenReturn("Test Song");

        List<Song> songsList = new ArrayList<>();
        songsList.add(mockSong);
        when(mockAlbum.getSongsCopy()).thenReturn(songsList);
    }

    @Test
    void testConstructorsAndClone() throws AlreadyExistsException {
        spotifUMData.addAlbum(mockAlbum);
        spotifUMData.addUser(mockUser);
        spotifUMData.addPlaylist(mockPlaylist);

        SpotifUMData copyData = new SpotifUMData(spotifUMData);
        assertEquals(spotifUMData, copyData);

        SpotifUMData clonedData = spotifUMData.clone();
        assertEquals(spotifUMData, clonedData);
    }

    @Test
    void testEqualsAndToString() throws AlreadyExistsException {
        assertEquals(spotifUMData, spotifUMData);
        assertNotEquals(spotifUMData, null);
        assertNotEquals(spotifUMData, new Object());

        SpotifUMData other = new SpotifUMData();
        assertEquals(spotifUMData, other);

        spotifUMData.addAlbum(mockAlbum);
        assertNotEquals(spotifUMData, other);

        assertNotNull(spotifUMData.toString());
        assertTrue(spotifUMData.toString().contains("Albums:"));
        assertTrue(spotifUMData.toString().contains("Users:"));
        assertTrue(spotifUMData.toString().contains("Playlists:"));
    }

    @Test
    void testEqualsThoroughly() throws AlreadyExistsException {
        SpotifUMData data1 = new SpotifUMData();
        SpotifUMData data2 = new SpotifUMData();

        // 1. Mesmo objeto na memória (this == o)
        assertTrue(data1.equals(data1));

        // 2. Objeto é nulo
        assertFalse(data1.equals(null));

        // 3. Classes diferentes
        assertFalse(data1.equals(new Object()));
        assertFalse(data1.equals("Uma String Aleatória"));

        // 4. Albums diferentes
        Album a1 = mock(Album.class);
        when(a1.getTitle()).thenReturn("Album Equals");
        when(a1.clone()).thenReturn(a1);
        data1.addAlbum(a1);
        assertFalse(data1.equals(data2));
        data2.addAlbum(a1); // Agora ficam iguais

        // 5. Albums iguais, mas Users diferentes
        User u1 = mock(User.class);
        when(u1.getName()).thenReturn("User Equals");
        when(u1.clone()).thenReturn(u1);
        data1.addUser(u1);
        assertFalse(data1.equals(data2));
        data2.addUser(u1); // Agora ficam iguais

        // 6. Albums e Users iguais, mas Playlists diferentes
        Playlist p1 = mock(Playlist.class);
        when(p1.getPlaylistName()).thenReturn("Playlist Equals");
        when(p1.clone()).thenReturn(p1);
        data1.addPlaylist(p1);
        assertFalse(data1.equals(data2));
        data2.addPlaylist(p1); // Agora ficam iguais

        // 7. Tudo igual e de instâncias diferentes
        assertTrue(data1.equals(data2));
    }

    @Test
    void testGetAndSetMapAlbums() throws AlreadyExistsException {
        spotifUMData.addAlbum(mockAlbum);

        Map<String, Album> copiedMap = spotifUMData.getMapAlbumsCopy();
        assertEquals(1, copiedMap.size());
        assertTrue(copiedMap.containsKey("Test Album"));

        Map<String, Album> simpleMap = spotifUMData.getMapAlbums();
        assertEquals(1, simpleMap.size());

        Map<String, Album> newMap = new HashMap<>();
        newMap.put("Test Album", mockAlbum);
        SpotifUMData newData = new SpotifUMData();
        newData.setMapAlbums(newMap);
        assertTrue(newData.existsAlbum("Test Album"));
    }

    @Test
    void testGetAndSetMapUsers() throws AlreadyExistsException {
        spotifUMData.addUser(mockUser);

        Map<String, User> copiedMap = spotifUMData.getMapUsers();
        assertEquals(1, copiedMap.size());

        Map<String, User> newMap = new HashMap<>();
        newMap.put("TestUser", mockUser);
        SpotifUMData newData = new SpotifUMData();
        newData.setMapUsers(newMap);
        assertTrue(newData.existsUser("TestUser"));
    }

    @Test
    void testGetAndSetMapPlaylists() throws AlreadyExistsException {
        spotifUMData.addPlaylist(mockPlaylist);

        Map<String, Playlist> copiedMap = spotifUMData.getMapPlaylists();
        assertEquals(1, copiedMap.size());

        Map<String, Playlist> newMap = new HashMap<>();
        newMap.put("Test Playlist", mockPlaylist);
        SpotifUMData newData = new SpotifUMData();
        newData.setMapPlaylists(newMap);
        assertTrue(newData.existsPlaylist("Test Playlist"));
    }

    @Test
    void testExistsMethods() throws AlreadyExistsException {
        assertFalse(spotifUMData.existsAlbum("Test Album"));
        assertFalse(spotifUMData.existsUser("TestUser"));
        assertFalse(spotifUMData.existsPlaylist("Test Playlist"));

        spotifUMData.addAlbum(mockAlbum);
        spotifUMData.addUser(mockUser);
        spotifUMData.addPlaylist(mockPlaylist);

        assertTrue(spotifUMData.existsAlbum("Test Album"));
        assertTrue(spotifUMData.existsUser("TestUser"));
        assertTrue(spotifUMData.existsPlaylist("Test Playlist"));
    }

    @Test
    void testExistsSong() throws AlreadyExistsException {
        assertFalse(spotifUMData.existsSong("Test Song", "Test Album"));

        spotifUMData.addAlbum(mockAlbum);
        assertTrue(spotifUMData.existsSong("Test Song", "Test Album"));
        assertFalse(spotifUMData.existsSong("NonExistent Song", "Test Album"));
    }

    @Test
    void testGetCurrentUserPointer() throws AlreadyExistsException {
        assertNull(spotifUMData.getCurrentUserPointer("TestUser"));
        spotifUMData.addUser(mockUser);
        assertNotNull(spotifUMData.getCurrentUserPointer("TestUser"));
    }

    @Test
    void testAddMethodsExceptions() throws AlreadyExistsException {
        spotifUMData.addAlbum(mockAlbum);
        assertThrows(AlreadyExistsException.class, () -> spotifUMData.addAlbum(mockAlbum));

        spotifUMData.addUser(mockUser);
        assertThrows(AlreadyExistsException.class, () -> spotifUMData.addUser(mockUser));

        spotifUMData.addPlaylist(mockPlaylist);
        assertThrows(AlreadyExistsException.class, () -> spotifUMData.addPlaylist(mockPlaylist));
    }

    @Test
    void testRemoveMethods() throws AlreadyExistsException, DoesntExistException {
        spotifUMData.addAlbum(mockAlbum);
        spotifUMData.addUser(mockUser);
        spotifUMData.addPlaylist(mockPlaylist);

        spotifUMData.removeAlbum("Test Album");
        assertFalse(spotifUMData.existsAlbum("Test Album"));

        spotifUMData.removeUser("TestUser");
        assertFalse(spotifUMData.existsUser("TestUser"));

        spotifUMData.removePlaylist("Test Playlist");
        assertFalse(spotifUMData.existsPlaylist("Test Playlist"));
    }

    @Test
    void testRemoveMethodsExceptions() {
        assertThrows(DoesntExistException.class, () -> spotifUMData.removeAlbum("Test Album"));
        assertThrows(DoesntExistException.class, () -> spotifUMData.removeUser("TestUser"));
        assertThrows(DoesntExistException.class, () -> spotifUMData.removePlaylist("Test Playlist"));
    }

    @Test
    void testGetMethods() throws AlreadyExistsException, DoesntExistException {
        spotifUMData.addAlbum(mockAlbum);
        spotifUMData.addUser(mockUser);
        spotifUMData.addPlaylist(mockPlaylist);

        assertEquals(mockAlbum, spotifUMData.getAlbum("Test Album"));
        assertEquals(mockUser, spotifUMData.getUser("TestUser"));
        assertEquals(mockPlaylist, spotifUMData.getPlaylist("Test Playlist"));
        assertEquals(mockSong, spotifUMData.getSong("Test Song", "Test Album"));
    }

    @Test
    void testGetMethodsExceptions() {
        assertThrows(DoesntExistException.class, () -> spotifUMData.getAlbum("Test Album"));
        assertThrows(DoesntExistException.class, () -> spotifUMData.getUser("TestUser"));
        assertThrows(DoesntExistException.class, () -> spotifUMData.getPlaylist("Test Playlist"));
        assertThrows(DoesntExistException.class, () -> spotifUMData.getSong("Test Song", "Test Album"));
    }

    @Test
    void testGetAnyPlaylistAsCreator() throws AlreadyExistsException, DoesntExistException {
        spotifUMData.addPlaylist(mockPlaylist);
        Playlist result = spotifUMData.getAnyPlaylist("Test Playlist", mockUser);
        assertEquals(mockPlaylist, result);
    }

    @Test
    void testGetAnyPlaylistAsPublic() throws AlreadyExistsException, DoesntExistException {
        User otherUser = mock(User.class);
        when(mockPlaylist.isPublic()).thenReturn(true);
        spotifUMData.addPlaylist(mockPlaylist);

        Playlist result = spotifUMData.getAnyPlaylist("Test Playlist", otherUser);
        assertEquals(mockPlaylist, result);
    }

    @Test
    void testGetAnyPlaylistException() throws AlreadyExistsException {
        User otherUser = mock(User.class);
        when(mockPlaylist.isPublic()).thenReturn(false);
        spotifUMData.addPlaylist(mockPlaylist);

        assertThrows(DoesntExistException.class, () -> spotifUMData.getAnyPlaylist("Test Playlist", otherUser));
        assertThrows(DoesntExistException.class, () -> spotifUMData.getAnyPlaylist("NonExistent", mockUser));
    }

    @Test
    void testGetPlaylistMapByCreator() throws AlreadyExistsException {
        spotifUMData.addPlaylist(mockPlaylist);

        Map<String, Playlist> creatorMap = spotifUMData.getPlaylistMapByCreator(mockUser);
        assertEquals(1, creatorMap.size());
        assertTrue(creatorMap.containsKey("Test Playlist"));

        User otherUser = mock(User.class);
        Map<String, Playlist> emptyMap = spotifUMData.getPlaylistMapByCreator(otherUser);
        assertTrue(emptyMap.isEmpty());
    }

    @Test
    void testGetSongUnreachableBranch() throws AlreadyExistsException, DoesntExistException {
        SpotifUMData data = new SpotifUMData();

        Album mockAlb = mock(Album.class);
        when(mockAlb.getTitle()).thenReturn("Album Magico");
        when(mockAlb.clone()).thenReturn(mockAlb);

        Song mockSng = mock(Song.class);
        when(mockSng.getName()).thenReturn("Musica Magica");

        List<Song> listaComMusica = new ArrayList<>();
        listaComMusica.add(mockSng);

        List<Song> listaVazia = new ArrayList<>();

        when(mockAlb.getSongsCopy()).thenReturn(listaComMusica).thenReturn(listaVazia);

        data.addAlbum(mockAlb);

        // Como o for vai iterar numa lista vazia, ele salta para o 'return null;' final!
        assertNull(data.getSong("Musica Magica", "Album Magico"));
    }
    @Test
    void testGetSongIfFalseBranch() throws AlreadyExistsException, DoesntExistException {
        SpotifUMData data = new SpotifUMData();

        Album mockAlb = mock(Album.class);
        when(mockAlb.getTitle()).thenReturn("Album Multiplo");
        when(mockAlb.clone()).thenReturn(mockAlb);

        // 1. Música Errada (Vai forçar o 'if' a dar FALSE)
        Song mockSongErrada = mock(Song.class);
        when(mockSongErrada.getName()).thenReturn("Musica Ignorada");

        // 2. Música Certa (Vai forçar o 'if' a dar TRUE e terminar)
        Song mockSongCerta = mock(Song.class);
        when(mockSongCerta.getName()).thenReturn("Musica Alvo");

        // Criamos a lista com a ordem: Primeiro a errada, depois a certa.
        List<Song> listaComDuasMusicas = new ArrayList<>();
        listaComDuasMusicas.add(mockSongErrada);
        listaComDuasMusicas.add(mockSongCerta);

        // Tanto o existsSong como o getSong vão ler esta lista
        when(mockAlb.getSongsCopy()).thenReturn(listaComDuasMusicas);

        data.addAlbum(mockAlb);

        // Chama o método. O ciclo for vai iterar na "Musica Ignorada" primeiro,
        // cobrindo o caminho falso do if!
        Song result = data.getSong("Musica Alvo", "Album Multiplo");

        assertEquals(mockSongCerta, result);
    }
}