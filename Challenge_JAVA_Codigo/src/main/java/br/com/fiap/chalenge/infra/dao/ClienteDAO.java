package br.com.fiap.chalenge.infra.dao;

import br.com.fiap.chalenge.dominio.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    private Connection connection;

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para inserir um novo cliente no banco de dados
    public void inserirCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (cpf, nome, data_de_nascimento, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getCpf());
            statement.setString(2, cliente.getNome());
            statement.setDate(3, java.sql.Date.valueOf(cliente.getDataDeNascimento())); // Certifique-se de que o formato está correto
            statement.setString(4, cliente.getTelefone());
            statement.setString(5, cliente.getEmail());
            statement.executeUpdate();
        }
    }

    // Método para verificar se o CPF existe
    public boolean cpfExiste(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cpf = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Método para atualizar os dados do cliente
    public void atualizarCliente(String cpf, String novoNome, String novoTelefone, String novoEmail) throws SQLException {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, email = ? WHERE cpf = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, novoNome);
            statement.setString(2, novoTelefone);
            statement.setString(3, novoEmail);
            statement.setString(4, cpf);
            statement.executeUpdate();
        }
    }

    // Método para remover um cliente
    public void removerCliente(String cpf) throws SQLException {
        String sql = "DELETE FROM clientes WHERE cpf = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            statement.executeUpdate();
        }
    }

    // Método para obter um cliente com base no CPF
    public Cliente obterCliente(String cpf) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE cpf = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nome = resultSet.getString("nome");
                    String dataDeNascimento = resultSet.getString("data_de_nascimento");
                    String telefone = resultSet.getString("telefone");
                    String email = resultSet.getString("email");
                    
                    // Criar e retornar um objeto Cliente
                    return new Cliente(cpf, nome, dataDeNascimento, telefone, email);
                }
            }
        }
        return null; // Retorna null se o cliente não for encontrado
    }
}