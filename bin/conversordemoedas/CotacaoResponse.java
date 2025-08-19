package br.larissa.cambio;

import java.util.Map;

public record CotacaoResponse(String status,
                              String moedaOrigem,
                              Map<String, Double> taxasConversao) {
}
