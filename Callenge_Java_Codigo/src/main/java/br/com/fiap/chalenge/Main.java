package br.com.fiap.chalenge;

import br.com.fiap.chalenge.dominio.Cliente;
import br.com.fiap.chalenge.dominio.Veiculo;
import br.com.fiap.chalenge.dominio.Diagnostico;
import br.com.fiap.chalenge.infra.dao.ClienteDAO;
import br.com.fiap.chalenge.infra.dao.ConnectionFactory;
import br.com.fiap.chalenge.infra.dao.VeiculoDAO;
import br.com.fiap.chalenge.infra.dao.DiagnosticoDAO;
import br.com.fiap.chalenge.controller.ClienteController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDateTime;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try (Connection connection = connectionFactory.getConnection()) {
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            VeiculoDAO veiculoDAO = new VeiculoDAO(connection);
            DiagnosticoDAO diagnosticoDAO = new DiagnosticoDAO(connection);
            ClienteController clienteController = new ClienteController(connection);
            Scanner scanner = new Scanner(System.in);
            String cpfClienteLogado = null;

            while (true) {
                System.out.println("\nOpções:");
                System.out.println("1. Fazer login");
                System.out.println("2. Não tenho login");
                System.out.println("3. Sair");

                String opcao = scanner.nextLine();

                if (opcao.equals("3")) {
                    System.out.println("Encerrando o programa. Até logo!");
                    break;
                }

                if (opcao.equals("1")) {
                    System.out.print("Digite seu CPF: ");
                    String cpfLogin = scanner.nextLine();
                    if (Cliente.validarCpf(cpfLogin) && clienteDAO.cpfExiste(cpfLogin)) {
                        System.out.println("Login bem-sucedido!");
                        cpfClienteLogado = cpfLogin;

                        // Menu do Cliente
                        while (true) {
                            System.out.println("\nMenu do Cliente:");
                            System.out.println("1. Cadastrar Problema");
                            System.out.println("2. Exportar Dados para JSON");
                            System.out.println("3. Alterar meus dados");
                            System.out.println("4. Deletar meu cadastro");
                            System.out.println("5. Sair");

                            String opcaoCliente = scanner.nextLine();

                            if (opcaoCliente.equals("5")) {
                                System.out.println("Saindo do menu do cliente.");
                                break;
                            }

                            switch (opcaoCliente) {
                                case "1":
                                    try {
                                        cadastrarProblema(scanner, veiculoDAO, diagnosticoDAO, clienteController, cpfClienteLogado);
                                    } catch (SQLException e) {
                                        System.out.println("Erro ao executar a operação: " + e.getMessage());
                                    }
                                    break;
                                case "2":
                                    exportarDados(clienteDAO, veiculoDAO, diagnosticoDAO, cpfClienteLogado);
                                    break;
                                case "3":
                                    alterarDados(clienteDAO, cpfClienteLogado);
                                    break;
                                case "4":
                                    deletarCadastro(scanner, clienteDAO, cpfClienteLogado);
                                    break;
                                default:
                                    System.out.println("Opção inválida. Por favor, escolha entre '1', '2', '3', '4' ou '5'.");
                            }
                        }
                    } else {
                        System.out.println("CPF inválido ou não encontrado.");
                    }
                } else if (opcao.equals("2")) {
                    // Implementar cadastro de novo cliente
                    cadastrarNovoCliente(scanner, clienteDAO);
                } else {
                    System.out.println("Opção inválida. Por favor, escolha entre '1', '2' ou '3'.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private static void cadastrarNovoCliente(Scanner scanner, ClienteDAO clienteDAO) {
        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a data de nascimento (DDMMYYYY): ");
        String dataNascimento = scanner.nextLine();

        // Validar o formato da data
        if (!dataNascimento.matches("\\d{8}")) {
            System.out.println("Data de nascimento deve estar no formato DDMMYYYY.");
            return;
        }

        // Extrair partes da data
        String dia = dataNascimento.substring(0, 2);
        String mes = dataNascimento.substring(2, 4);
        String ano = dataNascimento.substring(4, 8);

        // Converter a data para o formato YYYY-MM-DD
        String dataNascimentoFormatada = ano + "-" + mes + "-" + dia;

        System.out.print("Digite o telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Digite o email: ");
        String email = scanner.nextLine();
        System.out.print("Digite a senha: "); // Prompt for password
        String senha = scanner.nextLine(); // Capture the password

        // Criar um novo cliente
        Cliente novoCliente = new Cliente(cpf, nome, dataNascimentoFormatada, telefone, email, senha);
        try {
            clienteDAO.inserirCliente(novoCliente);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private static void exportarDados(ClienteDAO clienteDAO, VeiculoDAO veiculoDAO, DiagnosticoDAO diagnosticoDAO, String cpfClienteLogado) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha o tipo de dado para exportar:");
        System.out.println("1. Exportar Dados dos Veículos Cadastrados");
        System.out.println("2. Exportar Diagnósticos dos Veículos Cadastrados");
        System.out.println("3. Exportar Apenas Meus Dados (Clientes)");

        String tipoOpcao = scanner.nextLine();

        switch (tipoOpcao) {
            case "1":
                exportarDadosVeiculos(veiculoDAO); // Função para exportar dados dos veículos cadastrados
                break;
            case "2":
                exportarDiagnosticosVeiculos(diagnosticoDAO, cpfClienteLogado); // Função para exportar diagnósticos dos veículos cadastrados
                break;
            case "3":
                exportarDadosParaJson(clienteDAO, cpfClienteLogado, "clientes"); // Exporta apenas os dados do cliente
                break;
            default:
                System.out.println("Opção inválida. Por favor, escolha entre '1', '2' ou '3'.");
        }
    }

    private static void exportarDadosVeiculos(VeiculoDAO veiculoDAO) {
        try {
            List<Veiculo> veiculos = veiculoDAO.obterTodosVeiculos(); // Método que deve retornar todos os veículos cadastrados
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(veiculos);

            // Salvar o JSON em um arquivo
            try (FileWriter fileWriter = new FileWriter("veiculos_cadastrados.json")) {
                fileWriter.write(json);
                System.out.println("Dados dos veículos cadastrados exportados com sucesso para veiculos_cadastrados.json");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Erro ao exportar dados dos veículos: " + e.getMessage());
        }
    }

    private static void exportarDiagnosticosVeiculos(DiagnosticoDAO diagnosticoDAO, String cpfClienteLogado) {
        try {
            List<Diagnostico> diagnosticos = diagnosticoDAO.obterDiagnosticosPorCliente(cpfClienteLogado); // Método que deve retornar diagnósticos do cliente
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(diagnosticos);

            // Salvar o JSON em um arquivo
            try (FileWriter fileWriter = new FileWriter("diagnosticos_veiculos.json")) {
                fileWriter.write(json);
                System.out.println("Diagnósticos dos veículos cadastrados exportados com sucesso para diagnosticos_veiculos.json");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Erro ao exportar diagnósticos dos veículos: " + e.getMessage());
        }
    }

    private static void exportarDadosParaJson(ClienteDAO clienteDAO, String cpfClienteLogado, String tipo) {
        try {
            Cliente cliente = clienteDAO.obterCliente(cpfClienteLogado); // Método que deve retornar os dados do cliente
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(cliente);

            // Salvar o JSON em um arquivo
            try (FileWriter fileWriter = new FileWriter("dados_cliente.json")) {
                fileWriter.write(json);
                System.out.println("Dados do cliente exportados com sucesso para dados_cliente.json");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Erro ao exportar dados do cliente: " + e.getMessage());
        }
    }

    private static void alterarDados(ClienteDAO clienteDAO, String cpfClienteLogado) {
        try {
            // Obter dados atuais do cliente
            Cliente dadosCliente = clienteDAO.obterCliente(cpfClienteLogado);

            if (dadosCliente != null) {
                String nomeAtual = dadosCliente.getNome();
                String telefoneAtual = dadosCliente.getTelefone();
                String emailAtual = dadosCliente.getEmail();

                System.out.println("\nDados atuais:");
                System.out.println("Nome: " + nomeAtual);
                System.out.println("Telefone: " + mascararTelefone(telefoneAtual)); // Implementar a função mascararTelefone
                System.out.println("Email: " + mascararEmail(emailAtual)); // Implementar a função mascararEmail

                // Solicitar novos dados
                Scanner scanner = new Scanner(System.in);
                System.out.print("Digite o novo nome (ou pressione Enter para manter o atual): ");
                String novoNome = scanner.nextLine();
                if (novoNome.isEmpty()) {
                    novoNome = nomeAtual; // Manter o nome atual se não for fornecido
                }

                System.out.print("Digite o novo telefone (ou pressione Enter para manter o atual): ");
                String novoTelefone = scanner.nextLine();
                if (novoTelefone.isEmpty()) {
                    novoTelefone = telefoneAtual; // Manter o telefone atual se não for fornecido
                }

                System.out.print("Digite o novo email (ou pressione Enter para manter o atual): ");
                String novoEmail = scanner.nextLine();
                if (novoEmail.isEmpty()) {
                    novoEmail = emailAtual; // Manter o email atual se não for fornecido
                }

                // Atualizar os dados do cliente
                clienteDAO.atualizarCliente(cpfClienteLogado, novoNome, novoTelefone, novoEmail);
                System.out.println("Dados do cliente atualizados com sucesso.");
            } else {
                System.out.println("Erro ao obter dados do cliente.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar os dados do cliente: " + e.getMessage());
        }
    }

    // Funções para mascarar telefone e email
    private static String mascararTelefone(String telefone) {
        // Implementar lógica para mascarar telefone
        return telefone; // Retornar telefone mascarado
    }

    private static String mascararEmail(String email) {
        // Implementar lógica para mascarar email
        return email; // Retornar email mascarado
    }

    private static void deletarCadastro(Scanner scanner, ClienteDAO clienteDAO, String cpfClienteLogado) {
        System.out.print("Tem certeza que deseja deletar seu cadastro? (s/n): ");
        String confirmacao = scanner.nextLine();
        if (confirmacao.equalsIgnoreCase("s")) {
            try {
                clienteDAO.removerCliente(cpfClienteLogado);
                System.out.println("Cadastro deletado com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao deletar cadastro: " + e.getMessage());
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void cadastrarProblema(Scanner scanner, VeiculoDAO veiculoDAO, DiagnosticoDAO diagnosticoDAO, ClienteController clienteController, String cpfClienteLogado) throws SQLException {
        System.out.print("Digite a placa do veículo: ");
        String placaDiagnostico = scanner.nextLine(); // Solicita a placa do veículo

        // Consultar a API para verificar se o veículo existe
        String respostaApi = clienteController.consultarVeiculoPorPlaca(placaDiagnostico);
        if (respostaApi.contains("Erro")) {
            System.out.println("Erro ao consultar a API: " + respostaApi);
            return;
        }

        // Analisar a resposta JSON
        JsonObject jsonResponse = JsonParser.parseString(respostaApi).getAsJsonObject();
        String placaRetornada = jsonResponse.get("placa").getAsString();

        // Verificar se a placa retornada corresponde à placa consultada
        if (!placaRetornada.equalsIgnoreCase(placaDiagnostico)) {
            System.out.println("Veículo não encontrado.");
            return;
        }

        // Verificar se a placa já está cadastrada no banco de dados
        if (!veiculoDAO.veiculoExiste(placaDiagnostico)) {
            System.out.println("Veículo não cadastrado no sistema. Cadastrando agora...");
            // Aqui você pode inserir o veículo no banco de dados, se necessário
            Veiculo novoVeiculo = new Veiculo(placaDiagnostico, jsonResponse.get("MARCA").getAsString(), jsonResponse.get("MODELO").getAsString(), jsonResponse.get("ano").getAsInt());
            veiculoDAO.inserirVeiculo(novoVeiculo);
        }

        System.out.println("Selecione o problema:");
        String[] problemas = {
                "Superaquecimento - R$ 60,99",
                "Pane elétrica - R$ 120,00",
                "Problemas no câmbio - R$ 150,00",
                "Bateria ruim - R$ 99,99",
                "Falta de combustível - R$ 70,00",
                "Carro trepidando - R$ 100,00",
                "Pneus furados - R$ 80,00",
                "Correia dentada com muito uso - R$ 400,00",
                "Pneus carecas - R$ 150,00",
                "Falta de revisão - R$ 200,00"
        };

        for (int i = 0; i < problemas.length; i++) {
            System.out.println((i + 1) + ". " + problemas[i]);
        }

        int escolha = scanner.nextInt();
        scanner.nextLine(); // Consumir a nova linha

        if (escolha < 1 || escolha > problemas.length) {
            System.out.println("Opção inválida.");
            return;
        }

        // Obter o valor do problema selecionado
        String problemaSelecionado = problemas[escolha - 1].split(" - ")[0]; // Pega apenas o texto antes do " - "
        String valorProblema = problemas[escolha - 1].split(" - ")[1]; // Pega o valor após o " - "

        // Aqui você pode inserir o diagnóstico no banco de dados
        Diagnostico novoDiagnostico = new Diagnostico(placaDiagnostico, problemaSelecionado, valorProblema, LocalDateTime.now());
        diagnosticoDAO.inserirDiagnostico(novoDiagnostico);
        System.out.println("Diagnóstico registrado com sucesso.");
    }
}