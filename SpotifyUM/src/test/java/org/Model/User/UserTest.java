package org.Model.User;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.NoPremissionException;
import org.Exceptions.NotFoundException;
import org.Model.Music.Music;
import org.Model.Music.MusicReproduction;
import org.Model.Plan.Plan;
import org.Model.Playlist.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTest {

    private Plan mockPremiumPlan;
    private Plan mockFreePlan;
    private Playlist mockMyPlaylist;
    private Playlist mockOtherPlaylist;
    private MusicReproduction mockReproduction;
    private Music mockMusic;
    private List<Playlist> playlists;
    private List<MusicReproduction> reproductions;

    @BeforeEach
    void setUp() {
        // Mocks dos Planos
        mockPremiumPlan = mock(Plan.class);
        when(mockPremiumPlan.canAccessLibrary()).thenReturn(true);

        mockFreePlan = mock(Plan.class);
        when(mockFreePlan.canAccessLibrary()).thenReturn(false);

        // Mock de uma Playlist que me pertence
        mockMyPlaylist = mock(Playlist.class);
        when(mockMyPlaylist.getId()).thenReturn(1);
        when(mockMyPlaylist.getName()).thenReturn("My Rock");
        when(mockMyPlaylist.getAutor()).thenReturn("diogo");
        when(mockMyPlaylist.clone()).thenReturn(mockMyPlaylist); // Essencial para os Getters e Setters!
        when(mockMyPlaylist.removeMusic(any())).thenReturn(true);

        // Mock de uma Playlist de outra pessoa
        mockOtherPlaylist = mock(Playlist.class);
        when(mockOtherPlaylist.getId()).thenReturn(2);
        when(mockOtherPlaylist.getName()).thenReturn("Pop Hits");
        when(mockOtherPlaylist.getAutor()).thenReturn("joao");
        when(mockOtherPlaylist.clone()).thenReturn(mockOtherPlaylist);
        when(mockOtherPlaylist.removeMusic(any())).thenReturn(true);

        playlists = new ArrayList<>();
        playlists.add(mockMyPlaylist);
        playlists.add(mockOtherPlaylist);

        // Mock de Músicas e Reproduções para testar as Datas
        mockMusic = mock(Music.class);
        when(mockMusic.getName()).thenReturn("Song");

        mockReproduction = mock(MusicReproduction.class);
        when(mockReproduction.clone()).thenReturn(mockReproduction);
        when(mockReproduction.getDate()).thenReturn(LocalDate.of(2023, 5, 15));

        reproductions = new ArrayList<>();
        reproductions.add(mockReproduction);
    }

    // ==========================================
    // 1. TESTES DOS CONSTRUTORES
    // ==========================================
    @Test
    void testConstructors_InitializeCorrectly() {
        // Vazio
        User empty = new User();
        assertEquals("", empty.getUsername());
        assertFalse(empty.hasLibrary(), "Construtor vazio deve criar um PlanFree por omissão.");

        // Registo
        User reg = new User("diogo", "d@email.com", "Rua A", "pass");
        assertEquals("diogo", reg.getUsername());
        assertFalse(reg.hasLibrary(), "Construtor de registo deve criar um PlanFree por omissão.");

        // Completo
        User full = new User("diogo", "d@email.com", "Rua A", "pass", mockPremiumPlan, playlists, reproductions);
        assertEquals(2, full.getPlaylists().size());
        assertEquals(1, full.getMusicReproductions().size());

        // Cópia
        User copy = new User(full);
        assertEquals("diogo", copy.getUsername());
        assertEquals(2, copy.getPlaylists().size());
    }

    // ==========================================
    // 2. TESTES DE GETTERS E SETTERS
    // ==========================================
    @Test
    void testSettersAndGetters_UpdatesFields() {
        User u = new User();
        u.setUsername("admin");
        u.setEmail("admin@mail.com");
        u.setAdress("Braga");
        u.setPassword("1234");
        u.setPlan(mockPremiumPlan);
        u.setPlaylists(playlists);
        u.setMusicReproductions(reproductions);

        assertEquals("admin", u.getUsername());
        assertEquals("admin@mail.com", u.getEmail());
        assertEquals("Braga", u.getAdress());
        assertEquals("1234", u.getPassword());

        // A LINHA MÁGICA QUE MATA OS 0% DO GETPLAN:
        assertEquals(mockPremiumPlan, u.getPlan(), "Deve retornar o plano definido.");

        assertTrue(u.hasLibrary());
        assertEquals(2, u.getPlaylists().size());
        assertEquals(1, u.getMusicReproductions().size());
    }

    // ==========================================
    // 3. EQUALS, CLONE E TOSTRING
    // ==========================================
    @Test
    void testEqualsCloneAndToString() {
        User u1 = new User("diogo", "d@mail", "Braga", "123", mockPremiumPlan, playlists, reproductions);
        User u2 = new User("diogo", "d@mail", "Braga", "123", mockPremiumPlan, playlists, reproductions);

        // Mutações para cobrir todas as Branches do equals
        User diffName = new User("outro", "d@mail", "Braga", "123", mockPremiumPlan, playlists, reproductions);
        User diffEmail = new User("diogo", "x@mail", "Braga", "123", mockPremiumPlan, playlists, reproductions);
        User diffAdress = new User("diogo", "d@mail", "Porto", "123", mockPremiumPlan, playlists, reproductions);
        User diffPass = new User("diogo", "d@mail", "Braga", "456", mockPremiumPlan, playlists, reproductions);

        assertTrue(u1.equals(u1));
        assertFalse(u1.equals(null));
        assertFalse(u1.equals(new Object()));
        assertTrue(u1.equals(u2));

        assertFalse(u1.equals(diffName));
        assertFalse(u1.equals(diffEmail));
        assertFalse(u1.equals(diffAdress));
        assertFalse(u1.equals(diffPass));

        assertTrue(u1.toString().contains("diogo"));
        assertTrue(u1.toString().contains("d@mail"));
    }

    // ==========================================
    // 4. RESTRIÇÕES DE PLANO (FREE VS PREMIUM)
    // ==========================================
    @Test
    void testPlanRestrictions_FreePlan_ThrowsExceptions() {
        User u = new User("diogo", "d@email.com", "Rua A", "pass", mockFreePlan, playlists, reproductions);

        assertThrows(UnsupportedOperationException.class, u::namePlaylists);
        assertThrows(UnsupportedOperationException.class, () -> u.addPlaylist("Nova"));
        assertThrows(UnsupportedOperationException.class, () -> u.getPlaylistById(1));
    }

    @Test
    void testPlanRestrictions_PremiumPlan_WorksCorrectly() throws NotFoundException {
        User u = new User("diogo", "d@email.com", "Rua A", "pass", mockPremiumPlan, playlists, reproductions);

        assertTrue(u.namePlaylists().contains("My Rock"), "O relatório deve conter o nome da playlist.");

        u.addPlaylist("Nova Playlist"); // Adiciona nova playlist à conta
        assertEquals(3, u.getPlaylists().size(), "Deve conseguir adicionar playlists.");

        Playlist p = u.getPlaylistById(1);
        assertEquals(mockMyPlaylist, p, "Deve devolver a playlist correta.");
        assertThrows(NotFoundException.class, () -> u.getPlaylistById(99));
    }

    // ==========================================
    // 5. GESTÃO DE MÚSICAS NAS PLAYLISTS
    // ==========================================
    @Test
    void testMusicPlaylistManagement_AddAndRemove() throws Exception {
        User u = new User("diogo", "d@email.com", "Rua A", "pass", mockPremiumPlan, playlists, reproductions);

        // ADICIONAR MÚSICA
        u.addMusicPlaylist(1, mockMusic); // Sucesso: Eu sou o autor (diogo)
        verify(mockMyPlaylist, times(1)).addMusic(mockMusic);

        assertThrows(NoPremissionException.class, () -> u.addMusicPlaylist(2, mockMusic), "Não posso adicionar a playlists dos outros.");
        assertThrows(NotFoundException.class, () -> u.addMusicPlaylist(99, mockMusic), "Playlist não existe.");

        // REMOVER MÚSICA
        u.removeMusicFromPlaylist(mockMusic, 1); // Sucesso
        verify(mockMyPlaylist, times(1)).removeMusic(mockMusic);

        assertThrows(NoPremissionException.class, () -> u.removeMusicFromPlaylist(mockMusic, 2));
        assertThrows(NotFoundException.class, () -> u.removeMusicFromPlaylist(mockMusic, 99));

        // Forçar falha ao remover (música não estava na playlist)
        when(mockMyPlaylist.removeMusic(mockMusic)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> u.removeMusicFromPlaylist(mockMusic, 1));
    }

    // ==========================================
    // 6. UTILITÁRIOS DA PLAYLIST E DA CONTA
    // ==========================================
    @Test
    void testUtilities_CountAuthorAndLibrary() throws AlreadyExistsException {
        User u = new User("diogo", "d@email.com", "Rua A", "pass", mockPremiumPlan, playlists, reproductions);

        // Adicionar Pontos (Verifica se chamou o plano)
        u.addPoints();
        verify(mockPremiumPlan, times(1)).addPoints();

        // Contar Playlists do Autor (Apenas a mockMyPlaylist é do "diogo")
        assertEquals(1, u.getUserPlaylistCount(), "Só tem 1 playlist da sua autoria.");

        // Alterar Nome do Autor nas Playlists Próprias
        u.changePlaylistAutor("diogo_novo");
        verify(mockMyPlaylist, times(1)).setAutor("diogo_novo"); // Deve mudar a minha
        verify(mockOtherPlaylist, never()).setAutor(anyString()); // NÃO deve mudar a do joão

        // Adicionar Playlist à Biblioteca
        Playlist newPlay = mock(Playlist.class);
        u.addPlaylistToLibrary(newPlay);
        assertEquals(3, u.getPlaylists().size());

        // Forçar AlreadyExistsException
        assertThrows(AlreadyExistsException.class, () -> u.addPlaylistToLibrary(mockMyPlaylist));
    }

    // ==========================================
    // 7. HISTÓRICO DE REPRODUÇÕES (Mata os 0% nas Datas!)
    // ==========================================
    @Test
    void testMusicReproductions_AddAndCountByDate() {
        User u = new User("diogo", "d@email.com", "Rua A", "pass", mockPremiumPlan, playlists, reproductions);

        // Testar a Adição (O construtor na classe User cria o new MusicReproduction(m))
        u.addMusicReproduction(mockMusic);
        assertEquals(2, u.getMusicReproductions().size());

        // Testar a filtragem de Datas (A data do mockReproduction é 15/Maio/2023)
        LocalDate start = LocalDate.of(2023, 5, 1);
        LocalDate end = LocalDate.of(2023, 5, 31);
        LocalDate future = LocalDate.of(2023, 6, 1);

        // 1. Está dentro do intervalo
        assertEquals(1, u.getMusicReproductionsCount(start, end));

        // 2. Está fora do intervalo (antes)
        assertEquals(0, u.getMusicReproductionsCount(future, future.plusDays(10)));

        // 3. Está fora do intervalo (depois)
        assertEquals(0, u.getMusicReproductionsCount(start.minusDays(10), start));
    }
}