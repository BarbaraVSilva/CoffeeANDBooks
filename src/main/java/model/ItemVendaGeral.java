package model;

public class ItemVendaGeral {
    private int idItem;
    private int quantidade;
    private double precoApplied;
    private Livro livro; // Can be null
    private ProdutoConsumo produto; // Can be null

    public ItemVendaGeral() {}

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getPrecoApplied() { return precoApplied; }
    public void setPrecoApplied(double precoApplied) { this.precoApplied = precoApplied; }
    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public ProdutoConsumo getProduto() { return produto; }
    public void setProduto(ProdutoConsumo produto) { this.produto = produto; }
}
