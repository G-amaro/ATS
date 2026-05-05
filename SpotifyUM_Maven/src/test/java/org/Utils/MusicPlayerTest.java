package org.Utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicPlayerTest {

    @Test
    void testPlayMusic_MusicaNaoEncontrada_DevolveNull() {
        MusicPlayer player = new MusicPlayer();
        
        assertNull(player.playMusic("Musica Que Nao Existe No Sistema"),
            "Deve devolver null quando o ficheiro .wav não é encontrado na pasta Songs.");
    }
}