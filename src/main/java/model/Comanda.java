package model;

import java.util.ArrayList;
import java.util.List;

public class Comanda {
    private int numeroMesa;
    private List<ItemVendaGeral> itens;
    private boolean aberta;
    private String clienteNome;

    public Comanda(int numeroMesa) {
        this(numeroMesa, "Consumidor");
    }

    public Comanda(int numeroMesa, String clienteNome) {
        this.numeroMesa = numeroMesa;
        this.clienteNome = clienteNome;
        this.itens = new ArrayList<>();
        this.aberta = true;
    }

    public int getNumeroMesa() { return numeroMesa; }
    public List<ItemVendaGeral> getItens() { return itens; }
    public boolean isAberta() { return aberta; }
    public void setAberta(boolean aberta) { this.aberta = aberta; }
    
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public double getSubtotal() {
        double total = 0;
        for (ItemVendaGeral i : itens) total += i.getPrecoApplied() * i.getQuantidade();
        return total;
    }
}
