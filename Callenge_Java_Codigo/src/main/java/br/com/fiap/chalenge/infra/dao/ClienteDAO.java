package br.com.fiap.chalenge.infra.dao;

import br.com.fiap.chalenge.dominio.Cliente;
import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    private Connection connection;

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    public void inserirCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (cpf, nome, data_de_nascimento, telefone, email, senha) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());

            // Convert the date string to java.sql.Date
            Date sqlDate = Date.valueOf(cliente.getDataDeNascimento()); // Ensure the date is in YYYY-MM-DD format
            stmt.setDate(3, sqlDate);

            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getSenha());
            stmt.executeUpdate();
        }
    }

    public boolean verificarSenha(String cpf, String senha) throws SQLException {
        String sql = "SELECT senha FROM clientes WHERE cpf = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String senhaArmazenada = resultSet.getString("senha");
                    return senhaArmazenada.equals(senha); // Compare stored password with provided password
                }
            }
        }
        return false; // Return false if the client is not found or password does not match
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
                    String senha = resultSet.getString("senha"); // Obtém a senha

                    // Criar e retornar um objeto Cliente
                    return new Cliente(cpf, nome, dataDeNascimento, telefone, email, senha);
                }
            }
        }
        return null; // Retorna null se o cliente não for encontrado
    }
}