package br.larissa.cambio;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ServicoCambio {

    private static final String API_URL_BASE = "https://v6.exchangerate-api.com/v6/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String chaveApi;

    public ServicoCambio() {
        this.chaveApi = carregarChave();
    }

    private String carregarChave() {
        Properties config = new Properties();
        try (InputStream arquivo = new FileInputStream("config.properties")) {
            config.load(arquivo);
            String chave = config.getProperty("api.key");
            if (chave == null || chave.isBlank()) {
                throw new IllegalStateException("Chave 'api.key' ausente ou vazia no config.properties");
            }
            return chave;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar config.properties. Certifique-se que contém 'api.key'.", e);
        }
    }

    public CotacaoResponse obterCotacoes(String moedaBase) {
        URI endereco = URI.create(String.format("%s%s/latest/%s", API_URL_BASE, this.chaveApi, moedaBase));
        HttpRequest requisicao = HttpRequest.newBuilder().uri(endereco).build();

        try {
            HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() == 200) {
                String corpo = resposta.body();
                if (corpo.contains("conversion_rates")) {
                    return new Gson().fromJson(corpo, CotacaoResponse.class);
                } else {
                    System.out.println("Erro: API não retornou taxas. Resposta: " + corpo);
                    return null;
                }
            } else {
                System.out.println("Erro API: " + resposta.statusCode() + " - " + resposta.body());
                return null;
            }
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            System.err.println("Falha na requisição à API: " + ex.getMessage());
            return null;
        } catch (Exception ex) {
            System.err.println("Erro inesperado: " + ex.getMessage());
            return null;
        }
    }
}
