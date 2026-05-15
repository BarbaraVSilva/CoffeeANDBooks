package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VendaConsolidada {
    private int idVenda;
    private Date dataVenda;
    private double valorTotal;
    private String formaPagamento;
    private Integer numeroMesa;
    private List<ItemVendaGeral> itens;

    public VendaConsolidada() {
        this.itens = new ArrayList<>();
    }

    public int getIdVenda() { return idVenda; }
    public void setIdVenda(int idVenda) { this.idVenda = idVenda; }
    public Date getDataVenda() { return dataVenda; }
    public void setDataVenda(Date dataVenda) { this.dataVenda = dataVenda; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }
    public List<ItemVendaGeral> getItens() { return itens; }
    public void setItens(List<ItemVendaGeral> itens) { this.itens = itens; }
}
