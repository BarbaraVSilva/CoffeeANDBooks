package model;

import java.util.Date;

public class Doacao {
    private int idDoacao;
    private String nomeDoador;
    private Date dataDoacao;
    private Livro livro;

    public Doacao() {}

    public Doacao(int idDoacao, String nomeDoador, Date dataDoacao, Livro livro) {
        this.idDoacao = idDoacao;
        this.nomeDoador = nomeDoador;
        this.dataDoacao = dataDoacao;
        this.livro = livro;
    }

    public int getIdDoacao() {
        return idDoacao;
    }

    public void setIdDoacao(int idDoacao) {
        this.idDoacao = idDoacao;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public Date getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(Date dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    @Override
    public String toString() {
        return "Doação por " + nomeDoador + " do livro: " + (livro != null ? livro.getTitulo() : "Desconhecido");
    }
}
