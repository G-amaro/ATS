package org.spotifumtp37.model.playlist;

import net.jqwik.api.*;
import net.jqwik.api.constraints.DoubleRange;
import org.junit.jupiter.api.Assertions;
import org.spotifumtp37.exceptions.SubscriptionDoesNotAllowException;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;

public class PlaylistPropertiesTest {

    @Property
    void clonedSongIsEqualButNotSameReference(@ForAll("validSongs") Song song) {
        Song clone = song.clone();
        Assertions.assertEquals(song, clone);
        Assertions.assertNotSame(song, clone);
    }

    @Property
    void premiumTopAddsCorrectPoints(@ForAll @DoubleRange(min = 0.0, max = 10000.0) double initialPoints) {
        PremiumTop plan = new PremiumTop();
        double newPoints = plan.addPoints(initialPoints);
        Assertions.assertEquals(initialPoints * 1.025, newPoints, 0.001);
    }

    @Property
    void freeUserCannotAddSongsToPlaylist(
            @ForAll("freeUsers") User freeUser,
            @ForAll("validSongs") Song songToAdd
    ) {
        Playlist playlist = new Playlist(freeUser, "My Playlist", "Desc", 0, "public", new ArrayList<>());

        Assertions.assertThrows(
                SubscriptionDoesNotAllowException.class, 
                () -> playlist.addSong(songToAdd)
        );
    }

    @Provide
    Arbitrary<Song> validSongs() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(15),
                Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(15),
                Arbitraries.integers().between(30, 600)
        ).as((name, artist, duration) -> 
            new Song(name, artist, "Publisher XYZ", "La la la", "C D E", "Pop", duration)
        );
    }

    @Provide
    Arbitrary<User> freeUsers() {
        return Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(10).map(name ->
                new User(name, name + "@mail.com", "Rua X", new FreePlan(), "password123", 0.0, new ArrayList<>())
        );
    }
}