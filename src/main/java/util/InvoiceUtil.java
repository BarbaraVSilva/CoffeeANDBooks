package util;

import model.VendaConsolidada;
import model.ItemVendaGeral;
import java.io.*;
import java.text.SimpleDateFormat;
import java.awt.Desktop;

public class InvoiceUtil {
    public static void gerarNotaFiscal(VendaConsolidada v) {
        String filename = "nota_fiscal_" + v.getIdVenda() + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("******************************************");
            out.println("           COFFEE & BOOKS - RECIBO        ");
            out.println("******************************************");
            out.println("Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDataVenda()));
            out.println("ID Venda: " + v.getIdVenda());
            out.println("Forma Pagamento: " + v.getFormaPagamento());
            out.println("------------------------------------------");
            out.println(String.format("%-20s %-5s %-10s", "Item", "Qtd", "Subtotal"));
            for (ItemVendaGeral item : v.getItens()) {
                String nome = item.getLivro() != null ? item.getLivro().getTitulo() : item.getProduto().getNomeAlimento();
                out.println(String.format("%-20.20s %-5d R$ %-10.2f", nome, item.getQuantidade(), item.getPrecoApplied() * item.getQuantidade()));
            }
            out.println("------------------------------------------");
            out.println(String.format("TOTAL: R$ %.2f", v.getValorTotal()));
            out.println("******************************************");
            out.println("     Obrigado pela visita e boa leitura!  ");
            out.println("******************************************");
            
            // Auto-open for "printing"
            Desktop.getDesktop().open(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
