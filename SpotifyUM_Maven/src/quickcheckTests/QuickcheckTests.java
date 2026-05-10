package org.quickcheckTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

import org.Model.SpotifUM;
import org.Controller.Controller;
import org.Model.Music.Music;
import org.Model.User.User;
import org.Model.Plan.*;


class QuickcheckTests {

    private SpotifUM spotifUM;
    private Controller controller;

    @BeforeEach
    void setUp() {
        spotifUM = new SpotifUM();
        controller = new Controller(spotifUM);
    }

    @ParameterizedTest
    @DisplayName("Propriedade: Registo de Utilizadores em Larga Escala")
    @CsvFileSource(resources = "/test_users.csv", delimiter = '|')
    void testRegistoUtilizadoresQuickCheck(String nome, String email, String password, String plano) {
        // Executa a lógica de registo (agrupando morada fixa para cumprir a assinatura de 4 args)
        String resultado = controller.addNewUser(nome, email, "Rua de Teste QuickCheck", password);
        
        assertNotNull(resultado, "O sistema deve devolver sempre uma resposta");
        
        if (resultado.contains("sucesso")) {
            assertTrue(spotifUM.userExists(nome), "O utilizador deve estar no sistema");
        }
    }

    @ParameterizedTest
    @DisplayName("Propriedade: Integridade do Catálogo de Músicas")
    @CsvFileSource(resources = "/test_musics.csv", delimiter = '|')
    void testAdicaoMusicasQuickCheck(String nome, String artista, int duracao, boolean explicit, String url) {
        controller.addMusic(nome, artista, "Editora", "Letra", "Figuras", "Pop", "Album Teste", duracao, explicit, url);
        
        try {
            Music m = spotifUM.getMusicByName(nome);
            assertNotNull(m, "A música deve existir no catálogo");
            assertEquals(duracao, m.getDuration(), "A duração deve coincidir");
            
            if (url != null && !url.equals("null")) {
                assertTrue(m.getClass().getSimpleName().contains("Multimedia"), "Deve ser do tipo Multimedia");
            }
        } catch (Exception e) {
            fail("A música deveria ter sido encontrada: " + e.getMessage());
        }
    }

    @ParameterizedTest
    @DisplayName("Propriedade: Criação de Álbuns")
    @CsvFileSource(resources = "/test_albums.csv", delimiter = '|')
    void testCriacaoAlbunsQuickCheck(String titulo, String artista, String listaMusicas) {
        spotifUM.addNewAlbum(titulo, artista); 
        
        try {
            assertNotNull(spotifUM.getAlbumByName(titulo));
            assertEquals(artista, spotifUM.getAlbumByName(titulo).getArtist());
        } catch (Exception e) {
            fail("O álbum deveria existir");
        }
    }

    @ParameterizedTest
    @DisplayName("Propriedade: Integridade de Playlists")
    @CsvFileSource(resources = "/test_playlists.csv", delimiter = '|')
    void testPlaylistsQuickCheck(String nome, String criador, boolean isPublic, String musicas) {
        // O controller não tem createPlaylist direto, usamos o modelo
        spotifUM.addPlaylist(nome, criador);
        
        // As playlists no SpotifUM são geridas por ID no mapa publicPlaylists
        boolean encontrada = spotifUM.getPublicPlaylists().values().stream()
                .anyMatch(p -> p.getName().equals(nome) && p.getAutor().equals(criador));
        
        assertTrue(encontrada, "A playlist deve ter sido criada no sistema");
    }
    
    @ParameterizedTest
    @DisplayName("Propriedade: Acumulação de Pontos por Plano (Incluindo 2.5% PremiumTop)")
    @CsvFileSource(resources = "/test_plans.csv", delimiter = '|')
    void testPontosPorPlanoQuickCheck(String email, String planoInicial, int numReproducoes) {
        controller.addNewUser(email, email, "Morada", "pass");
        User u = spotifUM.getUsers().get(email);
        
        // Aplicação do plano conforme o enunciado
        if (planoInicial.equals("PremiumBase")) {
            u.setPlan(new PlanPremiumBase());
        } else if (planoInicial.equals("PremiumTop")) {
            // Usamos o construtor que recebe o plano atual (ex: PlanFree) 
            // e soma 100 pontos ao acumulado existente
            u.setPlan(new PlanPremiumTop(u.getPlan())); 
            
            assertEquals(100, u.getPlan().getPoints(), "PremiumTop deve iniciar com 100 pontos (base 0 + bónus)");
        } else {
            u.setPlan(new PlanFree());
        }

        int pontosAntes = u.getPlan().getPoints();
        
        for(int i = 0; i < numReproducoes; i++) {
            int pontosNoMomento = u.getPlan().getPoints();
            u.addPoints(); 
            
            if (planoInicial.equals("PremiumTop")) {
                int ganhoEsperado = (int) (pontosNoMomento * 0.025);
                assertTrue(u.getPlan().getPoints() >= pontosNoMomento + ganhoEsperado);
            }
        }
        
        assertTrue(u.getPlan().getPoints() >= pontosAntes, "Os pontos nunca devem diminuir");
    }

    @ParameterizedTest
    @DisplayName("Propriedade: Consistência de Estatísticas")
    @CsvFileSource(resources = "/test_stats.csv", delimiter = '|')
    void testEstatisticasQuickCheck(String listaReproducoes) {
        String[] reps = listaReproducoes.split(";");
        
        for (String rep : reps) {
            String[] partes = rep.split(":");
            spotifUM.incrementArtistReproductions(partes[1]); // partes[1] é a música/artista no generator
        }

        if (reps.length > 0) {
            String artistTop = controller.getMostReproducedArtist();
            assertNotNull(artistTop);
            assertFalse(artistTop.contains("Erro"));
        }
    }

    

    @ParameterizedTest
    @DisplayName("Propriedade: Restrição de Playlists para Utilizadores Free")
    @CsvFileSource(resources = "/test_users.csv", delimiter = '|')
    void testRestricaoPlaylistsFree(String nome, String email, String password, String plano) {
        // Se o utilizador for Free, ele só tem acesso a músicas aleatórias 
        if (plano.equals("Free")) {
            controller.addNewUser(nome, email, "Morada", password);
            
            // Correção: Aceder ao utilizador através do mapa de users do SpotifUM
            User u = spotifUM.getUsers().get(nome);
            
            if (u != null) {
                // Utilizador Free não deve ter biblioteca ativa (hasLibrary() == false) 
                assertFalse(u.hasLibrary(), "Utilizador Free não deve ter biblioteca ativa por omissão");
            }
        }
    }
}