package br.com.fiap.chalenge.dominio;

public class Cliente {
    private String cpf;
    private String nome;
    private String dataDeNascimento;
    private String telefone;
    private String email;
    private String senha;

    // Construtor padrão
    public Cliente() {
    }

    // Construtor com parâmetros
    public Cliente(String cpf, String nome, String dataDeNascimento, String telefone, String email, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataDeNascimento = dataDeNascimento;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Método para exibir informações do cliente
    public void exibirInformacoes() {
        System.out.println("CPF: " + cpf);
        System.out.println("Nome: " + nome);
        System.out.println("Data de Nascimento: " + dataDeNascimento);
        System.out.println("Telefone: " + telefone);
        System.out.println("Email: " + email);
    }

    // Método para validar CPF
    public static boolean validarCpf(String cpf) {
        cpf = cpf.replaceAll("\\D", ""); // Remove caracteres não numéricos
        if (cpf.length() != 11) return false;

        // Elimina CPFs inválidos conhecidos
        if (cpf.matches("(\\d)\\1{10}")) return false;

        // Calcula os dígitos verificadores
        for (int i = 9; i < 11; i++) {
            int soma = 0;
            for (int j = 0; j < i; j++) {
                soma += (cpf.charAt(j) - '0') * (i + 1 - j);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10 || digito == 11) digito = 0;
            if (digito != (cpf.charAt(i) - '0')) return false;
        }
        return true;
    }
}