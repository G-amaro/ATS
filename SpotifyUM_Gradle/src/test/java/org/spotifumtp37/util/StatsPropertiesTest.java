package org.spotifumtp37.util;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.user.User;

import java.util.*;

public class StatsPropertiesTest {

    // 1. Testa todas as proteções contra NullPointerExceptions de uma só vez
    @Property
    void nullOrEmptyMapsReturnGracefully() {
        Assertions.assertNull(Stats.getMostPlayedSong(null));
        Assertions.assertNull(Stats.getMostPlayedSong(Collections.emptyMap()));
        
        Assertions.assertNull(Stats.getMostListenedArtist(null));
        Assertions.assertNull(Stats.getMostListenedArtist(Collections.emptyMap()));
        
        Assertions.assertNull(Stats.getTopListener(null));
        Assertions.assertNull(Stats.getUserWithMostPoints(null));
        Assertions.assertNull(Stats.mostListenedGenre(null));
        
        Assertions.assertEquals(0L, Stats.countPublicPlaylists(null));
        Assertions.assertNull(Stats.userWithMostPlaylists(null));
    }

    // 2. Garante que a contagem de playlists públicas é matematicamente exata
    @Property
    void countPublicPlaylistsIsAccurate(@ForAll("playlistMap") Map<String, Playlist> playlists) {
        long expected = playlists.values().stream().filter(Playlist::isPublic).count();
        long actual = Stats.countPublicPlaylists(playlists);
        
        Assertions.assertEquals(expected, actual);
    }

    // 3. Garante que o utilizador retornado tem de facto os pontos mais altos do mapa
    @Property
    void userWithMostPointsIsAccurate(@ForAll("userMap") Map<String, User> users) {
        User topUser = Stats.getUserWithMostPoints(users);
        
        if (users.isEmpty()) {
            Assertions.assertNull(topUser);
        } else {
            Assertions.assertNotNull(topUser);
            // Verifica se não há ninguém no mapa com mais pontos que o topUser
            for (User u : users.values()) {
                Assertions.assertTrue(topUser.getPontos() >= u.getPontos());
            }
        }
    }

    // --- FORNECEDORES DE DADOS PARA O JQWIK ---

    @Provide
    Arbitrary<Map<String, User>> userMap() {
        Arbitrary<User> userGen = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10).map(name ->
                new User(name, name + "@mail.com", "Morada", new FreePlan(), "pass", Math.random() * 1000, new ArrayList<>())
        );
        return Arbitraries.maps(Arbitraries.strings().alpha().ofMinLength(3), userGen).ofMaxSize(10);
    }

    @Provide
    Arbitrary<Map<String, Playlist>> playlistMap() {
        Arbitrary<User> userGen = Arbitraries.strings().alpha().ofMinLength(3).map(name ->
                new User(name, name + "@m.com", "M", new FreePlan(), "p", 0, new ArrayList<>())
        );
        Arbitrary<Playlist> playlistGen = Combinators.combine(
                userGen,
                Arbitraries.strings().alpha().ofMinLength(3),
                Arbitraries.of("public", "private")
        ).as((user, name, status) -> new Playlist(user, name, "Desc", 0, status, new ArrayList<>()));

        return Arbitraries.maps(Arbitraries.strings().alpha().ofMinLength(3), playlistGen).ofMaxSize(10);
    }
}