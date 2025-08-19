package br.larissa.cambio;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class AplicativoConversor {

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        ServicoCambio servico = new ServicoCambio();
        boolean executando = true;

        System.out.println("=== Conversor de Moedas - Sistema Larissa ===");

        while (executando) {
            mostrarMenu();
            int escolha = lerOpcao(entrada);

            if (escolha == 7) {
                System.out.println("Programa encerrado. Obrigado por utilizar!");
                break;
            }

            String origem = "";
            String destino = "";

            switch (escolha) {
                case 1 -> { origem = "USD"; destino = "ARS"; }
                case 2 -> { origem = "ARS"; destino = "USD"; }
                case 3 -> { origem = "USD"; destino = "BRL"; }
                case 4 -> { origem = "BRL"; destino = "USD"; }
                case 5 -> { origem = "USD"; destino = "COP"; }
                case 6 -> { origem = "COP"; destino = "USD"; }
                default -> {
                    System.out.println("Opção inválida.");
                    continue;
                }
            }

            System.out.printf("Consultando taxas para %s...
", origem);
            CotacaoResponse cotacao = servico.obterCotacoes(origem);

            if (cotacao == null || !"success".equalsIgnoreCase(cotacao.status())) {
                System.out.println("Não foi possível obter dados. Verifique a API key ou conexão.");
                continue;
            }

            System.out.print("Digite o valor a converter: ");
            try {
                double valor = entrada.nextDouble();
                entrada.nextLine();

                if (valor < 0) {
                    System.out.println("Valor inválido. Informe um valor positivo.");
                    continue;
                }

                Map<String, Double> taxas = cotacao.taxasConversao();
                if (taxas.containsKey(destino)) {
                    double taxa = taxas.get(destino);
                    double convertido = valor * taxa;
                    System.out.printf("1 %s = %.2f %s
", origem, taxa, destino);
                    System.out.printf("%.2f %s = %.2f %s
", valor, origem, convertido, destino);
                } else {
                    System.out.printf("Moeda de destino '%s' não encontrada.
", destino);
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número.");
                entrada.nextLine();
            }
        }

        entrada.close();
    }

    private static void mostrarMenu() {
        System.out.println("
Selecione a conversão:");
        System.out.println("1 - Dólar -> Peso Argentino");
        System.out.println("2 - Peso Argentino -> Dólar");
        System.out.println("3 - Dólar -> Real Brasileiro");
        System.out.println("4 - Real Brasileiro -> Dólar");
        System.out.println("5 - Dólar -> Peso Colombiano");
        System.out.println("6 - Peso Colombiano -> Dólar");
        System.out.println("7 - Sair");
        System.out.print("Opção: ");
    }

    private static int lerOpcao(Scanner entrada) {
        while (true) {
            try {
                return entrada.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Entrada inválida. Digite um número entre 1 e 7: ");
                entrada.nextLine();
            }
        }
    }
}
