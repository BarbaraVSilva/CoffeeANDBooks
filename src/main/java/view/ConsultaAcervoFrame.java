package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.Livro;
import dao.LivroDAO;
import util.UIConstants;

public class ConsultaAcervoFrame extends JFrame {
    private JTextField txtBusca;
    private JComboBox<String> cbCondicao;
    private JTable tabela;
    private DefaultTableModel model;
    private JLabel lblTotalLivros, lblValorTotal;

    public ConsultaAcervoFrame() {
        setTitle("Catálogo de Acervo - Filtros Facetados");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // TOP: Faceted Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        filterPanel.setBackground(UIConstants.COLOR_SECONDARY());
        filterPanel.setBorder(BorderFactory.createTitledBorder(UIConstants.getRoundedBorder(UIConstants.COLOR_ACCENT()), "Busca Inteligente"));

        txtBusca = new JTextField(20);
        txtBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { carregarDados(); }
        });

        cbCondicao = new JComboBox<>(new String[]{"Todos", "Novo", "Usado (Excelente)", "Usado (Marcas de Tempo)"});
        cbCondicao.addActionListener(e -> carregarDados());

        filterPanel.add(new JLabel("Título / Autor:")); filterPanel.add(txtBusca);
        filterPanel.add(new JLabel("Condição:")); filterPanel.add(cbCondicao);

        add(filterPanel, BorderLayout.NORTH);

        // CENTER: JTable
        String[] cols = {"ID", "Título", "Autor", "Estante", "Condição", "Preço", "Estoque"};
        model = new DefaultTableModel(cols, 0);
        tabela = new JTable(model);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setBackground(UIConstants.COLOR_ACCENT());
        tabela.getTableHeader().setForeground(Color.WHITE);
        
        // Custom Renderer for Low Stock Alerts
        tabela.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int stockCol = 6; // Index of "Estoque" column
                try {
                    int stock = Integer.parseInt(table.getValueAt(row, stockCol).toString());
                    if (stock <= 0) {
                        c.setBackground(new Color(255, 200, 200)); // Crítico: Vermelho claro
                        c.setForeground(Color.RED);
                    } else if (stock < 3) {
                        c.setBackground(new Color(255, 255, 200)); // Alerta: Amarelo claro
                        c.setForeground(new Color(200, 150, 0));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    }
                } catch (Exception e) {}
                return c;
            }
        });

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // BOTTOM: Summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        summaryPanel.setBackground(UIConstants.COLOR_SECONDARY());
        
        lblTotalLivros = new JLabel("Total: 0");
        lblValorTotal = new JLabel("Valor: R$ 0,00");
        
        JButton btnExportar = new JButton("📄 Gerar Relatório de Acervo");
        btnExportar.setBackground(UIConstants.COLOR_DARK_BROWN);
        btnExportar.setForeground(Color.WHITE);
        btnExportar.addActionListener(e -> {
            util.ReportUtil.exportarAcervoTxt("relatorio_acervo.txt");
            JOptionPane.showMessageDialog(this, "Relatório exportado!");
        });

        summaryPanel.add(lblTotalLivros);
        summaryPanel.add(lblValorTotal);
        summaryPanel.add(btnExportar);
        add(summaryPanel, BorderLayout.SOUTH);

        carregarDados();
    }

    private void carregarDados() {
        model.setRowCount(0);
        String busca = txtBusca.getText();
        String condicao = (String) cbCondicao.getSelectedItem();
        
        List<Livro> livros = new LivroDAO().listar(busca, condicao);
        double valorTotal = 0;
        for (Livro l : livros) {
            model.addRow(new Object[]{
                l.getIdLivro(), l.getTitulo(), l.getAutor(), 
                l.getGenero().getLocalizacaoEstante(),
                l.getCondicaoLivro(), String.format("R$ %.2f", l.getPrecoVenda()),
                l.getEstoqueAtual()
            });
            valorTotal += l.getPrecoVenda();
        }
        lblTotalLivros.setText("Total de Obras: " + livros.size());
        lblValorTotal.setText("Valor Total: R$ " + String.format("%.2f", valorTotal));
    }
}
