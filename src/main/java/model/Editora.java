package model;

public class Editora {
    private int idEditora;
    private String nomeEditora;
    private String cidade;

    public Editora() {}

    public Editora(int idEditora, String nomeEditora, String cidade) {
        this.idEditora = idEditora;
        this.nomeEditora = nomeEditora;
        this.cidade = cidade;
    }

    public int getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(int idEditora) {
        this.idEditora = idEditora;
    }

    public String getNomeEditora() {
        return nomeEditora;
    }

    public void setNomeEditora(String nomeEditora) {
        this.nomeEditora = nomeEditora;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return nomeEditora + " (" + cidade + ")";
    }
}
