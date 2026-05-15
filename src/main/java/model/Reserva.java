package model;

public class Reserva {
    private String nomeCliente;
    private int numeroMesa;
    private String horarioEntrada;
    private int tempoPermanencia; // hours
    private boolean necessitaTomada;

    public Reserva() {}

    public Reserva(String nomeCliente, int numeroMesa, String horarioEntrada, int tempoPermanencia, boolean necessitaTomada) {
        this.nomeCliente = nomeCliente;
        this.numeroMesa = numeroMesa;
        this.horarioEntrada = horarioEntrada;
        this.tempoPermanencia = tempoPermanencia;
        this.necessitaTomada = necessitaTomada;
    }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public int getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }
    public String getHorarioEntrada() { return horarioEntrada; }
    public void setHorarioEntrada(String horarioEntrada) { this.horarioEntrada = horarioEntrada; }
    public int getTempoPermanencia() { return tempoPermanencia; }
    public void setTempoPermanencia(int tempoPermanencia) { this.tempoPermanencia = tempoPermanencia; }
    public boolean isNecessitaTomada() { return necessitaTomada; }
    public void setNecessitaTomada(boolean necessitaTomada) { this.necessitaTomada = necessitaTomada; }
}
