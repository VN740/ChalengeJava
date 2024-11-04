package br.com.fiap.chalenge.infra.dao;

import br.com.fiap.chalenge.dominio.Diagnostico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDAO {

    private Connection connection;

    public DiagnosticoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para inserir um novo diagnóstico no banco de dados
    public void inserirDiagnostico(Diagnostico diagnostico) throws SQLException {
        String sql = "INSERT INTO diagnosticos (placa, problema, valor, data_diagnostico) VALUES (?, ?, ?, SYSDATE)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, diagnostico.getPlaca());
            statement.setString(2, diagnostico.getProblema());
            statement.setString(3, diagnostico.getValor());
            statement.executeUpdate();
        }
    }

    public List<Diagnostico> obterDiagnosticosPorCliente(String cpf) throws SQLException {
        List<Diagnostico> diagnosticos = new ArrayList<>();
        String sql = "SELECT * FROM diagnosticos WHERE placa IN (SELECT placa FROM veiculos WHERE cpf_cliente = ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cpf);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String placa = resultSet.getString("placa");
                    String problema = resultSet.getString("problema");
                    String valor = resultSet.getString("valor");
                    LocalDateTime horaSolicitacao = resultSet.getTimestamp("data_diagnostico").toLocalDateTime();
                    
                    Diagnostico diagnostico = new Diagnostico(placa, problema, valor, horaSolicitacao);
                    diagnosticos.add(diagnostico);
                }
            }
        }
        return diagnosticos;
    }
}