package model;

public class Ingrediente {
    private int idIngrediente;
    private String nomeIngrediente;
    private double quantidadeAtual;
    private String unidadeMedida;

    public Ingrediente() {}

    public Ingrediente(int idIngrediente, String nomeIngrediente, double quantidadeAtual, String unidadeMedida) {
        this.idIngrediente = idIngrediente;
        this.nomeIngrediente = nomeIngrediente;
        this.quantidadeAtual = quantidadeAtual;
        this.unidadeMedida = unidadeMedida;
    }

    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }

    public String getNomeIngrediente() { return nomeIngrediente; }
    public void setNomeIngrediente(String nomeIngrediente) { this.nomeIngrediente = nomeIngrediente; }

    public double getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(double quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    @Override
    public String toString() {
        return nomeIngrediente + " (" + quantidadeAtual + " " + unidadeMedida + ")";
    }
}
