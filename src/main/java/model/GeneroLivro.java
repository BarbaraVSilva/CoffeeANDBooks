package model;

public class GeneroLivro {
    private int idGenero;
    private String nomeGenero;
    private String localizacaoEstante;

    public GeneroLivro() {}

    public GeneroLivro(int idGenero, String nomeGenero, String localizacaoEstante) {
        this.idGenero = idGenero;
        this.nomeGenero = nomeGenero;
        this.localizacaoEstante = localizacaoEstante;
    }

    public int getIdGenero() { return idGenero; }
    public void setIdGenero(int idGenero) { this.idGenero = idGenero; }

    public String getNomeGenero() { return nomeGenero; }
    public void setNomeGenero(String nomeGenero) { this.nomeGenero = nomeGenero; }

    public String getLocalizacaoEstante() { return localizacaoEstante; }
    public void setLocalizacaoEstante(String localizacaoEstante) { this.localizacaoEstante = localizacaoEstante; }

    @Override
    public String toString() {
        return nomeGenero;
    }
}
