package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import dao.*;
import util.UIConstants;

public class PDVFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable tabela;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private double totalGeral = 0;
    private List<ItemVendaGeral> carrinho = new ArrayList<>();

    public PDVFrame() {
        setTitle("Coffee&Books - Frente de Caixa (PDV) - Inspirado em Totvs Cheff");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // LEFT: Categories and Buttons
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_LABEL);
        
        tabbedPane.addTab("☕ Cafés", createCategoryPanel("Bebidas Quentes"));
        tabbedPane.addTab("🍰 Doces", createCategoryPanel("Doces"));
        tabbedPane.addTab("🥪 Salgados", createCategoryPanel("Salgados"));
        tabbedPane.addTab("📚 Livros", createBooksPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // RIGHT: Cart
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(350, 0));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));

        String[] cols = {"Item", "Qtd", "Total"};
        model = new DefaultTableModel(cols, 0);
        tabela = new JTable(model);
        cartPanel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Bottom Summary
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        lblTotal = new JLabel("TOTAL: R$ 0,00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTotal.setForeground(UIConstants.COLOR_ACCENT());
        bottomPanel.add(lblTotal);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFinalizar = new JButton("Finalizar Venda (Cupom)");
        btnFinalizar.setBackground(UIConstants.COLOR_SUCCESS);
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.addActionListener(e -> finalizarVenda());
        btnPanel.add(btnFinalizar);
        bottomPanel.add(btnPanel);

        cartPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(cartPanel, BorderLayout.EAST);
    }

    private JPanel createCategoryPanel(String categoria) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBackground(UIConstants.COLOR_PRIMARY());
        
        List<ProdutoConsumo> produtos = new ProdutoConsumoDAO().listar();
        for (ProdutoConsumo prod : produtos) {
            if (prod.getCategoriaCardapio().equals(categoria)) {
                JButton btn = new JButton("<html><center>" + prod.getNomeAlimento() + "<br>R$ " + prod.getPrecoUnitario() + "</center></html>");
                btn.setPreferredSize(new Dimension(120, 80));
                btn.setBackground(UIConstants.COLOR_SECONDARY());
                btn.addActionListener(e -> adicionarAoCarrinho(prod));
                p.add(btn);
            }
        }
        return p;
    }

    private JPanel createBooksPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBackground(UIConstants.COLOR_PRIMARY());
        
        List<Livro> livros = new LivroDAO().listar(null, "Todos");
        for (Livro l : livros) {
            JButton btn = new JButton("<html><center>" + l.getTitulo() + "<br>R$ " + l.getPrecoVenda() + "</center></html>");
            btn.setPreferredSize(new Dimension(150, 100));
            btn.setBackground(UIConstants.COLOR_SECONDARY());
            btn.addActionListener(e -> adicionarAoCarrinho(l));
            p.add(btn);
        }
        return p;
    }

    private void adicionarAoCarrinho(Object item) {
        ItemVendaGeral iv = new ItemVendaGeral();
        iv.setQuantidade(1);
        String nome = "";
        double preco = 0;

        if (item instanceof Livro) {
            Livro l = (Livro) item;
            iv.setLivro(l);
            nome = l.getTitulo();
            preco = l.getPrecoVenda();
        } else {
            ProdutoConsumo p = (ProdutoConsumo) item;
            iv.setProduto(p);
            nome = p.getNomeAlimento();
            preco = p.getPrecoUnitario();
        }

        iv.setPrecoApplied(preco);
        carrinho.add(iv);
        totalGeral += preco;
        
        model.addRow(new Object[]{nome, 1, String.format("R$ %.2f", preco)});
        lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) return;
        VendaConsolidada v = new VendaConsolidada();
        v.setValorTotal(totalGeral);
        v.setFormaPagamento("Crédito/Débito");
        v.setItens(new ArrayList<>(carrinho));
        v.setDataVenda(new java.util.Date());

        try {
            new VendaDAO().salvar(v);
            util.InvoiceUtil.gerarNotaFiscal(v);
            
            JOptionPane.showMessageDialog(this, "Venda consolidada! Cupom gerado.");
            carrinho.clear();
            model.setRowCount(0);
            totalGeral = 0;
            lblTotal.setText("TOTAL: R$ 0,00");
        } catch (exception.EstoqueInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Estoque Baixo", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
