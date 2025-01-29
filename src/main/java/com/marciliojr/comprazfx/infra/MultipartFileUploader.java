package com.marciliojr.comprazfx.infra;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class MultipartFileUploader {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://localhost:8080/api/pdf";
    private static final String UPLOAD_URL = BASE_URL + "/upload";
    private static final String TESTE_URL = BASE_URL + "/teste";

    public static void enviarArquivo(File file, String nomeEstabelecimento, LocalDate dataCadastro) {
        if (file == null || !file.exists()) {
            System.err.println("Erro: Arquivo não encontrado ou inválido.");
            return;
        }

        try {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/pdf")))
                    .addFormDataPart("nomeEstabelecimento", nomeEstabelecimento)
                    .addFormDataPart("dataCadastro", dataCadastro.toString())
                    .build();

            Request request = new Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("Retorno " + response.body());
                processarResposta(response, "Upload de arquivo");
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar o arquivo: " + e.getMessage());
        }
    }

    public static String teste() {
        try {
            Request request = new Request.Builder()
                    .url(TESTE_URL)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return processarResposta(response, "Teste de conexão");
            }
        } catch (IOException e) {
            return "Erro ao testar a conexão: " + e.getMessage();
        }
    }

    private static String processarResposta(Response response, String contexto) {
        try {
            if (response.isSuccessful()) {
                return contexto + " bem-sucedido. Resposta: " + response.body().string();
            } else {
                return contexto + " falhou. Código: " + response.code() + ", Mensagem: " + response.message();
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar a resposta: " + e.getMessage());
        }
        return null;
    }
}
