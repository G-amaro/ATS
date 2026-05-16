package org.spotifumtp37.model;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.spotifumtp37.exceptions.AlreadyExistsException;
import org.spotifumtp37.exceptions.DoesntExistException;
import org.spotifumtp37.model.user.User;
import org.spotifumtp37.model.subscription.FreePlan;

import java.util.ArrayList;

public class SpotifUMDataPropertiesTest {

    @Property
    void addingNewUserSavesItToTheSystem(@ForAll("validUsers") User user) throws Exception {
        SpotifUMData data = new SpotifUMData();
        
        data.addUser(user);
        
        Assertions.assertTrue(data.existsUser(user.getName()));
        Assertions.assertEquals(user, data.getUser(user.getName()));
    }

    @Property
    void addingSameUserTwiceThrowsException(@ForAll("validUsers") User user) {
        SpotifUMData data = new SpotifUMData();
        
        Assertions.assertDoesNotThrow(() -> data.addUser(user));
        
        Assertions.assertThrows(AlreadyExistsException.class, () -> data.addUser(user));
    }

    @Property
    void removingNonExistentUserThrowsException(@ForAll("validUsers") User user) {
        SpotifUMData data = new SpotifUMData();
        
        Assertions.assertThrows(DoesntExistException.class, () -> data.removeUser(user.getName()));
    }

    @Provide
    Arbitrary<User> validUsers() {
        return Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10).map(name ->
                new User(name, name + "@mail.com", "Morada", new FreePlan(), "pass", 50.0, new ArrayList<>())
        );
    }
}