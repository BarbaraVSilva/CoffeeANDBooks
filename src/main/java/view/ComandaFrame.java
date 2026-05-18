package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Comanda;
import model.ItemVendaGeral;
import model.Livro;
import model.ProdutoConsumo;
import dao.LivroDAO;
import dao.ProdutoConsumoDAO;
import util.UIConstants;
import util.ComandaManager;

public class ComandaFrame extends JFrame {
    private JPanel gridPanel;

    public ComandaFrame() {
        setTitle("Coffee&Books - Gestor de Comandas & Mesas");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("☕ Mapa de Mesas, Espaços & Consumo", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(4, 5, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        atualizarGrid();
        add(gridPanel, BorderLayout.CENTER);

        // Footer / Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        legendPanel.setBackground(UIConstants.COLOR_SECONDARY());
        legendPanel.add(createLegend("Livre", UIConstants.COLOR_SUCCESS));
        legendPanel.add(createLegend("Ocupada / Comanda Aberta", UIConstants.COLOR_ALERT));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private void atualizarGrid() {
        gridPanel.removeAll();
        for (int i = 1; i <= 20; i++) {
            final int numMesa = i;
            JButton btnMesa = new JButton();
            btnMesa.setFont(UIConstants.FONT_LABEL);
            btnMesa.setPreferredSize(new Dimension(100, 100));
            btnMesa.setFocusPainted(false);
            btnMesa.setLayout(new BorderLayout());

            boolean ocupada = ComandaManager.isMesaOcupada(numMesa);
            if (ocupada) {
                Comanda c = ComandaManager.getComanda(numMesa);
                btnMesa.setBackground(UIConstants.COLOR_ALERT);
                btnMesa.setForeground(Color.WHITE);
                
                JLabel lblTop = new JLabel("Mesa " + numMesa, SwingConstants.CENTER);
                lblTop.setForeground(Color.WHITE);
                lblTop.setFont(new Font("SansSerif", Font.BOLD, 12));
                
                JLabel lblMid = new JLabel("<html><center>" + c.getClienteNome() + "</center></html>", SwingConstants.CENTER);
                lblMid.setForeground(Color.WHITE);
                lblMid.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                JLabel lblBot = new JLabel(String.format("R$ %.2f", c.getSubtotal()), SwingConstants.CENTER);
                lblBot.setForeground(Color.YELLOW);
                lblBot.setFont(new Font("SansSerif", Font.BOLD, 12));
                
                btnMesa.add(lblTop, BorderLayout.NORTH);
                btnMesa.add(lblMid, BorderLayout.CENTER);
                btnMesa.add(lblBot, BorderLayout.SOUTH);
            } else {
                btnMesa.setBackground(UIConstants.COLOR_SUCCESS);
                btnMesa.setForeground(Color.WHITE);
                
                JLabel lblCenter = new JLabel("Mesa " + numMesa, SwingConstants.CENTER);
                lblCenter.setForeground(Color.WHITE);
                lblCenter.setFont(UIConstants.FONT_LABEL);
                
                JLabel lblStatus = new JLabel("Livre", SwingConstants.CENTER);
                lblStatus.setForeground(new Color(230, 255, 230));
                lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                btnMesa.add(lblCenter, BorderLayout.CENTER);
                btnMesa.add(lblStatus, BorderLayout.SOUTH);
            }

            btnMesa.addActionListener(e -> {
                try {
                    abrirDetalheMesa(numMesa);
                } catch (exception.MesaJaOcupadaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
                }
            });

            gridPanel.add(btnMesa);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void abrirDetalheMesa(int num) throws exception.MesaJaOcupadaException {
        Comanda c = ComandaManager.getComanda(num);
        if (c == null) {
            // Prompt customer registration selection or simple name
            String nomeCliente = JOptionPane.showInputDialog(this, "Mesa " + num + " está livre.\nDigite o Nome do Cliente para abrir a Comanda:", "Abrir Comanda", JOptionPane.QUESTION_MESSAGE);
            if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
                ComandaManager.abrirComanda(num, nomeCliente.trim());
                atualizarGrid();
            }
        } else {
            // Options: Add Item, Close Table, Back
            double total = c.getSubtotal();
            int opt = JOptionPane.showOptionDialog(this, 
                "Mesa " + num + " (" + c.getClienteNome() + ")\nConsumo Acumulado: R$ " + String.format("%.2f", total), 
                "Gerenciamento da Mesa", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                new String[]{"➕ Adicionar Consumo", "💰 Enviar ao Caixa (PDV)", "❌ Fechar Mesa", "Voltar"}, 
                "Voltar");
            
            if (opt == 0) { // Add Consumo
                adicionarItemMesa(c);
                atualizarGrid();
            } else if (opt == 1) { // Send to PDV
                JOptionPane.showMessageDialog(this, "Itens enviados para o Caixa! Abra o Frente de Caixa (PDV) e clique em 'Importar Mesa'.");
            } else if (opt == 2) { // Direct Close
                int conf = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja fechar esta mesa e descartar a comanda?", "Confirmar Fechamento", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    ComandaManager.fecharComanda(num);
                    atualizarGrid();
                }
            }
        }
    }

    private void adicionarItemMesa(Comanda c) {
        // Load products & books
        List<Object> allItems = new ArrayList<>();
        List<ProdutoConsumo> prods = new ProdutoConsumoDAO().listar();
        List<Livro> books = new LivroDAO().listar(null, "Todos");
        allItems.addAll(prods);
        allItems.addAll(books);
        
        if (allItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto cadastrado!");
            return;
        }
        
        JComboBox<Object> cbItems = new JComboBox<>(allItems.toArray());
        JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Produto/Livro:"));
        panel.add(cbItems);
        panel.add(new JLabel("Quantidade:"));
        panel.add(spQty);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Item à Comanda (Mesa " + c.getNumeroMesa() + ")", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Object sel = cbItems.getSelectedItem();
            int qty = (int) spQty.getValue();
            
            ItemVendaGeral iv = new ItemVendaGeral();
            iv.setQuantidade(qty);
            
            if (sel instanceof Livro) {
                Livro l = (Livro) sel;
                iv.setLivro(l);
                iv.setPrecoApplied(l.getPrecoVenda());
            } else if (sel instanceof ProdutoConsumo) {
                ProdutoConsumo p = (ProdutoConsumo) sel;
                iv.setProduto(p);
                iv.setPrecoApplied(p.getPrecoUnitario());
            }
            
            c.getItens().add(iv);
            JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
        }
    }

    private JPanel createLegend(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout());
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(20, 20));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }
}
