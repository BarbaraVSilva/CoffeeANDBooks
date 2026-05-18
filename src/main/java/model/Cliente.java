package model;

import java.util.Date;

public class Cliente {
    private int idCliente;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private int pontosFidelidade;
    private Date dataNascimento;

    public Cliente() {}

    public Cliente(int idCliente, String nome, String cpf, String email, String telefone, int pontosFidelidade) {
        this(idCliente, nome, cpf, email, telefone, pontosFidelidade, null);
    }

    public Cliente(int idCliente, String nome, String cpf, String email, String telefone, int pontosFidelidade, Date dataNascimento) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.pontosFidelidade = pontosFidelidade;
        this.dataNascimento = dataNascimento;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public int getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(int pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public void adicionarPontos(int pontos) {
        this.pontosFidelidade += pontos;
    }

    public boolean usarPontos(int pontos) {
        if (this.pontosFidelidade >= pontos) {
            this.pontosFidelidade -= pontos;
            return true;
        }
        return false;
    }
}
