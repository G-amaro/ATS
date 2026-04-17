package org.Controller.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicInfoTest {

    // ==========================================
    // 1. TESTES DO CONSTRUTOR PRINCIPAL
    // ==========================================

    @Test
    void testMainConstructor_ValidData_InitializesFieldsCorrectly() {
        // Setup: Dados completos
        String name = "Bohemian Rhapsody";
        String artist = "Queen";
        String lyrics = "Is this the real life";
        String url = "https://youtube.com/queen";
        boolean explicit = false;

        // Ação: Criar o DTO
        MusicInfo info = new MusicInfo(name, artist, lyrics, url, explicit);

        // Verificação: Garantir que os Getters devolvem exatamente o que metemos
        assertEquals(name, info.getMusicName(), "Music name should match the constructor input.");
        assertEquals(artist, info.getArtistName(), "Artist name should match the constructor input.");

        // A letra devia ser dividida por espaços num array de Strings
        String[] expectedLyricsArray = {"Is", "this", "the", "real", "life"};
        assertArrayEquals(expectedLyricsArray, info.getLyrics(), "Lyrics should be split into an array of words.");

        assertEquals(url, info.getUrl(), "URL should match the constructor input.");
        assertFalse(info.isExplicit(), "Explicit flag should match the constructor input.");

        // O construtor principal deve inicializar o errorMessage como String vazia
        assertEquals("", info.getErrorMessage(), "Error message should be empty by default in the main constructor.");
    }

    // ==========================================
    // 2. TESTES DO CONSTRUTOR DE ERRO
    // ==========================================

    @Test
    void testErrorConstructor_ErrorMessage_InitializesWithEmptyDefaults() {
        // Setup
        String errorMsg = "Música não encontrada no sistema.";

        // Ação
        MusicInfo errorInfo = new MusicInfo(errorMsg);

        // Verificação: Garantir que os campos ficam vazios/nulos, exceto o erro
        assertEquals("", errorInfo.getMusicName(), "Music name should be empty in error state.");
        assertEquals("", errorInfo.getArtistName(), "Artist name should be empty in error state.");
        assertNull(errorInfo.getLyrics(), "Lyrics array should be null in error state.");
        assertEquals("", errorInfo.getUrl(), "URL should be empty in error state.");
        assertFalse(errorInfo.isExplicit(), "Explicit should be false by default in error state.");
        assertEquals(errorMsg, errorInfo.getErrorMessage(), "Error message should exactly match the input.");
    }

    // ==========================================
    // 3. TESTES DOS SETTERS
    // ==========================================

    @Test
    void testSetErrorMessage_ValidString_UpdatesErrorMessage() {
        // Setup: Criar uma música normal
        MusicInfo info = new MusicInfo("Song", "Artist", "La la la", "", false);

        // Ação: Forçar uma mensagem de erro à posteriori
        info.setErrorMessage("Ocorreu um erro na reprodução");

        // Verificação
        assertEquals("Ocorreu um erro na reprodução", info.getErrorMessage(), "Setter should update the error message correctly.");
    }
}