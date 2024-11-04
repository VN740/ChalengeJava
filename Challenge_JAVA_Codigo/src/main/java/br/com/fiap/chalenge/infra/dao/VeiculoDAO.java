package br.com.fiap.chalenge.infra.dao;

import br.com.fiap.chalenge.dominio.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    private Connection connection;

    public VeiculoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para inserir um novo veículo no banco de dados
    public void inserirVeiculo(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculos (placa, marca, modelo, ano_fabricacao) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, veiculo.getPlaca());
            statement.setString(2, veiculo.getMarca());
            statement.setString(3, veiculo.getModelo());
            statement.setInt(4, veiculo.getAnoFabricacao());
            statement.executeUpdate();
        }
    }

    // Método para verificar se o veículo já está cadastrado
    public boolean veiculoExiste(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM veiculos WHERE placa = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, placa);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Veiculo> obterTodosVeiculos() throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                String placa = resultSet.getString("placa");
                String marca = resultSet.getString("marca");
                String modelo = resultSet.getString("modelo");
                int anoFabricacao = resultSet.getInt("ano_fabricacao");
                
                Veiculo veiculo = new Veiculo(placa, marca, modelo, anoFabricacao);
                veiculos.add(veiculo);
            }
        }
        return veiculos;
    }
}
