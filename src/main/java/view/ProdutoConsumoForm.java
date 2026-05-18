package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.util.List;
import model.ProdutoConsumo;
import dao.ProdutoConsumoDAO;
import util.UIConstants;

public class ProdutoConsumoForm extends JFrame {
    private JTextField txtId, txtNome, txtPreco, txtPesquisa;
    private JComboBox<String> cbCategoria;
    private JCheckBox chkDisponivel;
    private String currentImagePath = "";
    private JLabel lblImagePreview;
    private JTable tabela;
    private DefaultTableModel model;
    private ProdutoConsumoDAO dao;

    public ProdutoConsumoForm() {
        dao = new ProdutoConsumoDAO();
        
        setTitle("Gestão de Cardápio - Comidas & Bebidas");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("☕ Novo Item do Cardápio", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // LEFT: Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID (Automático):"), gbc);
        txtId = new JTextField(); txtId.setEnabled(false);
        txtId.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtId, gbc);

        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Nome do Produto:"), gbc);
        txtNome = new JTextField(20); txtNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Categoria:"), gbc);
        cbCategoria = new JComboBox<>(new String[]{"Bebidas Quentes", "Bebidas Fias", "Salgados", "Doces", "Outros"});
        cbCategoria.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(cbCategoria, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Preço Unitário (R$):"), gbc);
        txtPreco = new JTextField(10); txtPreco.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtPreco, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Disponibilidade:"), gbc);
        chkDisponivel = new JCheckBox("Disponível para venda", true);
        chkDisponivel.setOpaque(false);
        chkDisponivel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(chkDisponivel, gbc);

        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Imagem do Produto:"), gbc);
        JButton btnUpload = new JButton("Carregar Imagem");
        btnUpload.setFont(UIConstants.FONT_BUTTON);
        btnUpload.addActionListener(e -> selecionarImagem());
        gbc.gridx = 1; formPanel.add(btnUpload, gbc);
        
        lblImagePreview = new JLabel("Nenhuma imagem");
        lblImagePreview.setPreferredSize(new Dimension(150, 150));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6; gbc.gridx = 1; formPanel.add(lblImagePreview, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo");
        JButton btnSalvar = createStyledButton("Salvar");
        JButton btnExcluir = createStyledButton("Excluir");
        btnExcluir.setEnabled(util.SessionManager.isAdmin());
        btnPanel.add(btnNovo); btnPanel.add(btnSalvar); btnPanel.add(btnExcluir);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // RIGHT: Search & Table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Pesquisar:"), BorderLayout.WEST);
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(txtPesquisa, BorderLayout.CENTER);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Nome do Produto", "Categoria", "Preço (R$)", "Disponível"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(22);
        JScrollPane scrollTable = new JScrollPane(tabela);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Cardápio Cadastrado"));
        rightPanel.add(scrollTable, BorderLayout.CENTER);

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                txtId.setText(String.valueOf(id));
                txtNome.setText(model.getValueAt(row, 1).toString());
                cbCategoria.setSelectedItem(model.getValueAt(row, 2).toString());
                txtPreco.setText(model.getValueAt(row, 3).toString().replace("R$ ", ""));
                chkDisponivel.setSelected(model.getValueAt(row, 4).toString().equals("Sim"));

                // Get Image Path from database list matching this id
                List<ProdutoConsumo> currentList = dao.listarTodos(txtPesquisa.getText());
                for (ProdutoConsumo p : currentList) {
                    if (p.getIdProduto() == id) {
                        currentImagePath = p.getImagePath();
                        exibirImagem(currentImagePath);
                        break;
                    }
                }
            }
        });

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        carregarDados();
    }
    
    private void selecionarImagem() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentImagePath = fc.getSelectedFile().getAbsolutePath();
            exibirImagem(currentImagePath);
        }
    }

    private void exibirImagem(String path) {
        if (path != null && !path.isEmpty() && new File(path).exists()) {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
            lblImagePreview.setText("");
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Sem Imagem");
        }
    }

    private void carregarDados() {
        model.setRowCount(0);
        String filterText = txtPesquisa != null ? txtPesquisa.getText() : "";
        List<ProdutoConsumo> produtos = dao.listarTodos(filterText);
        for (ProdutoConsumo p : produtos) {
            model.addRow(new Object[]{
                p.getIdProduto(),
                p.getNomeAlimento(),
                p.getCategoriaCardapio(),
                String.format("%.2f", p.getPrecoUnitario()),
                p.isDisponivel() ? "Sim" : "Não"
            });
        }
    }

    private void filtrar() {
        carregarDados();
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String categoria = (String) cbCategoria.getSelectedItem();
            String precoStr = txtPreco.getText().trim().replace(",", ".");
            boolean disponivel = chkDisponivel.isSelected();
            
            if (nome.isEmpty() || precoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
                return;
            }

            double preco = Double.parseDouble(precoStr);

            if (txtId.getText().isEmpty()) {
                ProdutoConsumo p = new ProdutoConsumo(0, nome, preco, categoria, disponivel, currentImagePath);
                dao.salvar(p);
                JOptionPane.showMessageDialog(this, "Produto '" + nome + "' adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int id = Integer.parseInt(txtId.getText());
                ProdutoConsumo p = new ProdutoConsumo(id, nome, preco, categoria, disponivel, currentImagePath);
                dao.atualizar(p);
                JOptionPane.showMessageDialog(this, "Produto '" + nome + "' atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            carregarDados();
            limpar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este produto do cardápio?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            dao.deletar(id);
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
            carregarDados();
            limpar();
        }
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtPreco.setText("");
        cbCategoria.setSelectedIndex(0);
        chkDisponivel.setSelected(true);
        currentImagePath = "";
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Nenhuma imagem");
        tabela.clearSelection();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIConstants.COLOR_ACCENT());
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
