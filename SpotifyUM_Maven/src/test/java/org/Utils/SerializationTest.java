package org.Utils;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    @Test
    void testExportarEImportarComSucesso() {
        String testFile = "teste_serializacao.dat";
        String dadosParaGravar = "SpotifUM Dados Teste";

        assertDoesNotThrow(() -> Serialization.exportar(dadosParaGravar, testFile));

        String dadosLidos = Serialization.importar(testFile);
        assertEquals(dadosParaGravar, dadosLidos);

        new File(testFile).delete();
    }

    @Test
    void testExportarObjetoNulo_AtiraExcecao() {
        assertThrows(IllegalArgumentException.class, 
            () -> Serialization.exportar(null, "ficheiro.dat"),
            "Deve atirar exceção se o objeto for nulo.");
    }

    @Test
    void testImportarFicheiroInexistente_AtiraExcecao() {
        assertThrows(RuntimeException.class, 
            () -> Serialization.importar("ficheiro_fantasma_xyz.dat"),
            "Deve atirar exceção ao ler ficheiro que não existe.");
    }

    @Test
    void testImportarFicheiroCorrompido() throws Exception {
        String path = "corrompido.ser";
        java.io.FileWriter fw = new java.io.FileWriter(path);
        fw.write("não é um objeto serializado");
        fw.close();

        assertThrows(RuntimeException.class, () -> Serialization.importar(path));
        new java.io.File(path).delete();
    }

    @Test
    void testExportar_ObjetoNulo_Branch() {
    
        assertThrows(IllegalArgumentException.class, 
            () -> Serialization.exportar(null, "f.ser"));
    }

    @Test
    void testExportarFicheiro_FalhaDeIO_AtiraRuntimeException() {
        String caminhoImpossivel = "/caminho/proibido/ficheiro_impossivel.ser"; 
        
        assertThrows(RuntimeException.class, 
            () -> Serialization.exportar("dados", caminhoImpossivel),
            "Deve atirar RuntimeException se o FileOutputStream falhar.");
    }
}