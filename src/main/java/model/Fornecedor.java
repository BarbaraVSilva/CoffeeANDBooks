package model;

public class Fornecedor {
    private int idFornecedor;
    private String nomeFantasia;
    private String cnpj;
    private String contato;
    private String tipoProduto;

    public Fornecedor() {}

    public Fornecedor(int idFornecedor, String nomeFantasia, String cnpj, String contato, String tipoProduto) {
        this.idFornecedor = idFornecedor;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.contato = contato;
        this.tipoProduto = tipoProduto;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    @Override
    public String toString() {
        return nomeFantasia + " (" + tipoProduto + ")";
    }
}
