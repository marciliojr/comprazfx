package com.marciliojr.comprazfx.infra;

import java.io.File;
import java.time.LocalDate;

public class PersistenciaPDF {

    public static void enviarArquivo(File file, String nomeEstabelecimento, LocalDate dataCadastro) {
        if (file == null || !file.exists()) {
            System.err.println("Erro: Arquivo não encontrado ou inválido.");
            return;
        }
        // Inserir Aqui a persistencia do dado.
    }


}
