package model;

import exception.PrecoInvalidoSeboException;

public class Livro {
    private int idLivro;
    private String titulo;
    private String autor;
    private String condicaoLivro;
    private double precoVenda;
    private int estoqueAtual;
    private GeneroLivro genero;

    public Livro() {}

    public Livro(int idLivro, String titulo, String autor, String condicaoLivro, double precoVenda, int estoqueAtual, GeneroLivro genero) throws PrecoInvalidoSeboException {
        validarPreco(condicaoLivro, precoVenda);
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.autor = autor;
        this.condicaoLivro = condicaoLivro;
        this.precoVenda = precoVenda;
        this.estoqueAtual = estoqueAtual;
        this.genero = genero;
    }

    public static void validarPreco(String condicao, double preco) throws PrecoInvalidoSeboException {
        if ("Usado (Marcas de Tempo)".equals(condicao) && preco > 50.00) {
            throw new PrecoInvalidoSeboException("Livros com marcas de tempo não podem custar mais de R$ 50,00.");
        }
    }

    // Getters and Setters
    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getCondicaoLivro() { return condicaoLivro; }
    public void setCondicaoLivro(String condicaoLivro) { this.condicaoLivro = condicaoLivro; }
    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    public int getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(int estoqueAtual) { this.estoqueAtual = estoqueAtual; }
    public GeneroLivro getGenero() { return genero; }
    public void setGenero(GeneroLivro genero) { this.genero = genero; }
}
