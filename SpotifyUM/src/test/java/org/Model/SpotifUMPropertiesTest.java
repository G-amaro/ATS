package org.Model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

import org.Exceptions.AlreadyExistsException;
import static org.junit.jupiter.api.Assertions.*;

class SpotifUMPropertiesTest {

    @Property
    void adicionarNovoUtilizadorAumentaOTamanhoDoMapa(
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String username,
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String email,
            @ForAll String rua,
            @ForAll String password) {

        SpotifUM sys = new SpotifUM();
        
        int tamanhoInicial = sys.getUsers().size();

        try {
            sys.addNewUser(username, email, rua, password);
            
            assertEquals(tamanhoInicial + 1, sys.getUsers().size(),
                "A propriedade falhou: o mapa não aumentou de tamanho!");
                
            assertTrue(sys.userExists(username),
                "A propriedade falhou: o utilizador gerado não foi encontrado!");
                
        } catch (Exception e) {

            if (!sys.userExists(username)) {
                fail("A propriedade falhou pois rebentou com uma excepção não esperada: " + e.getMessage());
            }
        }
    }
}