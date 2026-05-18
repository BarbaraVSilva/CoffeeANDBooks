package model;

import java.util.Date;

public class Evento {
    private int idEvento;
    private String nomeEvento;
    private Date dataEvento;
    private String tipoEvento;
    private String descricao;

    public Evento() {}

    public Evento(int idEvento, String nomeEvento, Date dataEvento, String tipoEvento, String descricao) {
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
        this.dataEvento = dataEvento;
        this.tipoEvento = tipoEvento;
        this.descricao = descricao;
    }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public String getNomeEvento() { return nomeEvento; }
    public void setNomeEvento(String nomeEvento) { this.nomeEvento = nomeEvento; }

    public Date getDataEvento() { return dataEvento; }
    public void setDataEvento(Date dataEvento) { this.dataEvento = dataEvento; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
