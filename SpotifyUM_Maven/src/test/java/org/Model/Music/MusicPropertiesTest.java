package org.Model.Music;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import static org.junit.jupiter.api.Assertions.*;

class MusicPropertiesTest {

    @Property
    void musicaRecemCriadaDeveTerZeroReproducoes(
            @ForAll @NotEmpty String titulo,
            @ForAll @NotEmpty String artista,
            @ForAll @Positive int duracao) {
        
        Music m = new Music(titulo, artista, "Editora", "Letra", "Figuras", "Rock", "Album", duracao, false);
        
        assertEquals(0, m.getReproductions(), "Uma música nova deve ter 0 reproduções.");
    }

    @Property
    void oMetodoPlayDeveIncrementarReproducoesERetornarLetra(
            @ForAll @IntRange(min = 1, max = 50) int vezes) {
        
        String letraOriginal = "La la la";
        Music m = new Music("Song", "Art", "Pub", letraOriginal, "Fig", "Pop", "Alb", 200, false);
        
        String letraRecebida = "";
        for (int i = 0; i < vezes; i++) {
            letraRecebida = m.play();
        }
        
        assertEquals(letraOriginal, letraRecebida, "O método play() deve retornar a letra da música.");
        assertEquals(vezes, m.getReproductions(), "O contador de reproduções deve bater certo com o número de plays.");
    }
}