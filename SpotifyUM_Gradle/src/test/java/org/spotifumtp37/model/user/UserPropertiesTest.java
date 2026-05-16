package org.spotifumtp37.model.user;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumTop;

import java.util.ArrayList;

public class UserPropertiesTest {

    @Property
    void updatingToPremiumTopAdds100Points(@ForAll("validUsers") User user) {
        double initialPoints = user.getPontos();
        user.updatePremiumTop(new PremiumTop());

        Assertions.assertEquals(initialPoints + 100.0, user.getPontos(), 0.001);
        Assertions.assertTrue(user.getSubscriptionPlan() instanceof PremiumTop);
    }

    @Property
    void updatingHistoryAddsSongToTheList(@ForAll("validUsers") User user, @ForAll("validSongs") Song song) {
        int initialHistorySize = user.getHistory().size();
        user.updateHistory(song);

        // O tamanho da lista tem de aumentar 1
        Assertions.assertEquals(initialHistorySize + 1, user.getHistory().size());
        
        // A última música do histórico tem de ser igual à música que passámos
        Song addedSong = user.getHistory().get(user.getHistory().size() - 1).getSong();
        Assertions.assertEquals(song, addedSong);
    }

    @Property
    void cloningUserCreatesDeepCopy(@ForAll("validUsers") User user) {
        User clone = user.clone();
        
        Assertions.assertEquals(user, clone);
        Assertions.assertNotSame(user, clone);
        Assertions.assertNotSame(user.getHistory(), clone.getHistory());
    }

    @Provide
    Arbitrary<User> validUsers() {
        return Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10).map(name ->
                new User(name, name + "@mail.com", "Morada", new FreePlan(), "pass", 50.0, new ArrayList<>())
        );
    }

    @Provide
    Arbitrary<Song> validSongs() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(15),
                Arbitraries.integers().between(30, 600)
        ).as((name, duration) ->
                new Song(name, "Artista", "Editora", "Letra", "Notas", "Rock", duration)
        );
    }
}