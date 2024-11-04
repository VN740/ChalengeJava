package br.com.fiap.chalenge.controller;

import br.com.fiap.chalenge.dominio.Cliente;
import br.com.fiap.chalenge.infra.dao.ClienteDAO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000") // Altere para o domínio do seu front-end
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteDAO clienteDAO;
    private final String token = "f7c5df87e73e17a75814d3aacd0627e3"; // Seu token de API

    public ClienteController(Connection connection) {
        this.clienteDAO = new ClienteDAO(connection);
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarCliente(@RequestBody Cliente cliente) throws SQLException {
        // Verifica se o CPF já existe
        if (clienteDAO.cpfExiste(cliente.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna 409 se o CPF já existir
        }
        
        clienteDAO.inserirCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Retorna 201 após a criação
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable String cpf) throws SQLException {
        Cliente cliente = clienteDAO.obterCliente(cpf);
        
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{cpf}")
    public void removerCliente(@PathVariable String cpf) throws SQLException {
        clienteDAO.removerCliente(cpf);
    }

    @GetMapping("/consulta/{placa}")
    public String consultarVeiculo(@PathVariable String placa) {
        String apiUrl = String.format("https://wdapi2.com.br/consulta/%s/%s", placa, token);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao consultar a API: " + e.getMessage();
        }
    }

    @GetMapping("/veiculo/{placa}")
    public String consultarVeiculoPorPlaca(@PathVariable String placa) {
        return consultarVeiculo(placa);
    }

    @GetMapping("/download/{cpf}")
    public ResponseEntity<byte[]> downloadDadosCliente(@PathVariable String cpf) throws SQLException, IOException {
        Cliente cliente = clienteDAO.obterCliente(cpf);
        
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Usar ObjectMapper para converter o objeto Cliente em JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonData = objectMapper.writeValueAsBytes(cliente);

        // Configurar os headers para download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=cliente_" + cpf + ".json");
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(jsonData, headers, HttpStatus.OK);
    }
} 