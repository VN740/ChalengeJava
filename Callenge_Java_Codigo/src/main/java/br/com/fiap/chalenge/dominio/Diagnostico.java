package br.com.fiap.chalenge.dominio;

import java.time.LocalDateTime;

public class Diagnostico {
    private String placa;
    private String problema;
    private String valor;
    private LocalDateTime horaSolicitacao;

    // Construtor
    public Diagnostico(String placa, String problema, String valor, LocalDateTime horaSolicitacao) {
        this.placa = placa;
        this.problema = problema;
        this.valor = valor;
        this.horaSolicitacao = horaSolicitacao;
    }

    // Getters e Setters
    public String getPlaca() {
        return placa;
    }

    public String getProblema() {
        return problema;
    }

    public String getValor() {
        return valor;
    }

    public LocalDateTime getHoraSolicitacao() {
        return horaSolicitacao;
    }
}