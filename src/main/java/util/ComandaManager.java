package util;

import java.util.HashMap;
import java.util.Map;
import model.Comanda;
import model.ItemVendaGeral;
import model.ProdutoConsumo;

public class ComandaManager {
    private static Map<Integer, Comanda> comandasAtivas = new HashMap<>();

    static {
        // Seed 10 active table comandas with mock items
        abrirComanda(1, "Franz Kafka");
        Comanda c1 = getComanda(1);
        ProdutoConsumo p1 = new ProdutoConsumo(2, "Capuccino Hemingway", 12.00, "Bebidas Quentes", true, "");
        ItemVendaGeral iv1 = new ItemVendaGeral();
        iv1.setQuantidade(1);
        iv1.setPrecoApplied(12.00);
        iv1.setProduto(p1);
        c1.getItens().add(iv1);
        
        abrirComanda(2, "J.R.R. Tolkien");
        Comanda c2 = getComanda(2);
        ProdutoConsumo p2 = new ProdutoConsumo(1, "Shake Shake Shakespeare (Mocha)", 14.50, "Bebidas Quentes", true, "");
        ItemVendaGeral iv2 = new ItemVendaGeral();
        iv2.setQuantidade(1);
        iv2.setPrecoApplied(14.50);
        iv2.setProduto(p2);
        c2.getItens().add(iv2);
        
        abrirComanda(3, "Amanda Costa");
        Comanda c3 = getComanda(3);
        ProdutoConsumo p3 = new ProdutoConsumo(3, "Pão de Queijo da Vila", 5.50, "Salgados", true, "");
        ItemVendaGeral iv3 = new ItemVendaGeral();
        iv3.setQuantidade(2);
        iv3.setPrecoApplied(5.50);
        iv3.setProduto(p3);
        c3.getItens().add(iv3);

        abrirComanda(4, "William Shakespeare");
        Comanda c4 = getComanda(4);
        ProdutoConsumo p4 = new ProdutoConsumo(1, "Shake Shake Shakespeare (Mocha)", 14.50, "Bebidas Quentes", true, "");
        ItemVendaGeral iv4 = new ItemVendaGeral();
        iv4.setQuantidade(2);
        iv4.setPrecoApplied(14.50);
        iv4.setProduto(p4);
        c4.getItens().add(iv4);

        abrirComanda(5, "Jane Austen");
        Comanda c5 = getComanda(5);
        ProdutoConsumo p5 = new ProdutoConsumo(4, "Torta Red Velvet \"Duna\"", 18.00, "Doces", true, "");
        ItemVendaGeral iv5 = new ItemVendaGeral();
        iv5.setQuantidade(1);
        iv5.setPrecoApplied(18.00);
        iv5.setProduto(p5);
        c5.getItens().add(iv5);

        abrirComanda(8, "Bernardo M.");
        Comanda c8 = getComanda(8);
        ProdutoConsumo p8 = new ProdutoConsumo(9, "Cold Brew Orwell", 11.50, "Bebidas Frias", true, "");
        ItemVendaGeral iv8 = new ItemVendaGeral();
        iv8.setQuantidade(1);
        iv8.setPrecoApplied(11.50);
        iv8.setProduto(p8);
        c8.getItens().add(iv8);

        abrirComanda(9, "Virginia Woolf");
        Comanda c9 = getComanda(9);
        ProdutoConsumo p9 = new ProdutoConsumo(11, "Torta de Limão Virginia Woolf", 15.00, "Doces", true, "");
        ItemVendaGeral iv9 = new ItemVendaGeral();
        iv9.setQuantidade(1);
        iv9.setPrecoApplied(15.00);
        iv9.setProduto(p9);
        c9.getItens().add(iv9);

        abrirComanda(12, "Clarice Lispector");
        Comanda c12 = getComanda(12);
        ProdutoConsumo p12 = new ProdutoConsumo(7, "Croissant Baudelaire", 9.50, "Salgados", true, "");
        ItemVendaGeral iv12 = new ItemVendaGeral();
        iv12.setQuantidade(1);
        iv12.setPrecoApplied(9.50);
        iv12.setProduto(p12);
        c12.getItens().add(iv12);

        abrirComanda(13, "Edgar Allan Poe");
        Comanda c13 = getComanda(13);
        ProdutoConsumo p13 = new ProdutoConsumo(10, "Soda Italiana Poe", 10.00, "Bebidas Frias", true, "");
        ItemVendaGeral iv13 = new ItemVendaGeral();
        iv13.setQuantidade(1);
        iv13.setPrecoApplied(10.00);
        iv13.setProduto(p13);
        c13.getItens().add(iv13);

        abrirComanda(19, "George Orwell");
        Comanda c19 = getComanda(19);
        ProdutoConsumo p19 = new ProdutoConsumo(6, "Sanduíche \"Metamorfose\"", 22.00, "Salgados", true, "");
        ItemVendaGeral iv19 = new ItemVendaGeral();
        iv19.setQuantidade(1);
        iv19.setPrecoApplied(22.00);
        iv19.setProduto(p19);
        c19.getItens().add(iv19);
    }

    public static Map<Integer, Comanda> getComandasAtivas() {
        return comandasAtivas;
    }

    public static void abrirComanda(int numeroMesa, String clienteNome) {
        if (!comandasAtivas.containsKey(numeroMesa)) {
            comandasAtivas.put(numeroMesa, new Comanda(numeroMesa, clienteNome));
        }
    }

    public static Comanda getComanda(int numeroMesa) {
        return comandasAtivas.get(numeroMesa);
    }

    public static void fecharComanda(int numeroMesa) {
        comandasAtivas.remove(numeroMesa);
    }
    
    public static boolean isMesaOcupada(int numeroMesa) {
        return comandasAtivas.containsKey(numeroMesa);
    }
}
