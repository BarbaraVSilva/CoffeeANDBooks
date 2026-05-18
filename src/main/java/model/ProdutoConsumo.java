package model;

public class ProdutoConsumo {
    private int idProduto;
    private String nomeAlimento;
    private double precoUnitario;
    private String categoriaCardapio;
    private boolean disponivel;
    private String imagePath;

    public ProdutoConsumo() {}

    public ProdutoConsumo(int idProduto, String nomeAlimento, double precoUnitario, String categoriaCardapio, boolean disponivel, String imagePath) {
        this.idProduto = idProduto;
        this.nomeAlimento = nomeAlimento;
        this.precoUnitario = precoUnitario;
        this.categoriaCardapio = categoriaCardapio;
        this.disponivel = disponivel;
        this.imagePath = imagePath;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public String getNomeAlimento() { return nomeAlimento; }
    public void setNomeAlimento(String nomeAlimento) { this.nomeAlimento = nomeAlimento; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public String getCategoriaCardapio() { return categoriaCardapio; }
    public void setCategoriaCardapio(String categoriaCardapio) { this.categoriaCardapio = categoriaCardapio; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return nomeAlimento + " - R$ " + String.format("%.2f", precoUnitario);
    }
}
