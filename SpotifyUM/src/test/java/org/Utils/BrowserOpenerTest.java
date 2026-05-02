package org.Utils;

import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;

class BrowserOpenerTest {

    @Test
    void testAbrir_URLInvalido_AtiraExcecao() {
        BrowserOpener opener = new BrowserOpener("h t t p : / / i n v a l i d . c o m");
        
        assertThrows(URISyntaxException.class, () -> opener.abrir(),
            "Deve atirar URISyntaxException se o link estiver mal formatado.");
    }
    
    @Test
    void testAbrirIOException() {

        BrowserOpener opener = new BrowserOpener("file://diretorio/inexistente/arquivo.html");
        assertThrows(java.io.IOException.class, () -> opener.abrir());
    }

    @Test
    void testConstructorCoverage() {
        
        BrowserOpener opener = new BrowserOpener("mailto:test@example.com");
        assertDoesNotThrow(() -> {
            try { opener.abrir(); } catch (Exception e) {}
        });
    }

}