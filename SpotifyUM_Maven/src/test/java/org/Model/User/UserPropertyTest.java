package org.Model.User;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.Model.SpotifUM; 

import static org.junit.jupiter.api.Assertions.*;

class UserPropertiesTest {

    @Property
    void adicionarNovoUtilizadorDeveAumentarTamanhoDoSistema(
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String username,
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String email,
            @ForAll String adress,
            @ForAll String password) {

        SpotifUM sys = new SpotifUM();
        int initialSize = sys.getUsers().size();

        try {
            sys.addNewUser(username, email, adress, password);
            assertEquals(initialSize + 1, sys.getUsers().size(), "O mapa de utilizadores devia ter crescido.");
            assertTrue(sys.userExists(username), "O utilizador devia existir no sistema.");
        } catch (Exception e) {
            if (!e.getMessage().contains("already exists")) {
                fail("Erro inesperado ao adicionar utilizador: " + e.getMessage());
            }
        }
    }

    @Property
    void loginDeveFalharComPasswordIncorreta(
            @ForAll @AlphaChars @StringLength(min = 3) String user,
            @ForAll @AlphaChars @StringLength(min = 3) String pwd,
            @ForAll @AlphaChars @StringLength(min = 3) String wrongPwd) {
        
        Assume.that(!pwd.equals(wrongPwd));
        SpotifUM sys = new SpotifUM();
        
        try {
            sys.addNewUser(user, user + "@email.com", "Morada Teste", pwd);
            
            assertThrows(Exception.class, () -> {
                sys.authenticateUser(user, wrongPwd);
            }, "Deveria ter lançado uma excepção de password incorreta");
            
        } catch (Exception e) {
            
        }
    }
}