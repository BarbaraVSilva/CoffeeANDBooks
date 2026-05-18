package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.List;
import model.Ingrediente;
import dao.IngredienteDAO;
import util.UIConstants;

public class IngredienteForm extends JFrame {
    private JTextField txtId, txtNome, txtQuantidade, txtUnidade, txtPesquisa;
    private JTable tabela;
    private DefaultTableModel model;
    private IngredienteDAO dao;

    public IngredienteForm() {
        dao = new IngredienteDAO();

        setTitle("Coffee&Books - Controle de Insumos & Cafeteria");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("📦 Gestão de Estoque de Insumos da Cafeteria", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);

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

        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Nome do Insumo:"), gbc);
        txtNome = new JTextField(18); txtNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Quantidade Atual:"), gbc);
        txtQuantidade = new JTextField(10); txtQuantidade.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtQuantidade, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Unidade de Medida:"), gbc);
        txtUnidade = new JTextField(8); txtUnidade.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUnidade.setToolTipText("Ex: g, ml, un, kg");
        gbc.gridx = 1; formPanel.add(txtUnidade, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo");
        JButton btnSalvar = createStyledButton("Salvar");
        JButton btnExcluir = createStyledButton("Excluir");
        btnExcluir.setEnabled(util.SessionManager.isAdmin());
        btnPanel.add(btnNovo); btnPanel.add(btnSalvar); btnPanel.add(btnExcluir);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        splitPane.setLeftComponent(formPanel);

        // RIGHT: Search & Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Pesquisar Insumo:"), BorderLayout.WEST);
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(txtPesquisa, BorderLayout.CENTER);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Nome do Insumo", "Estoque Atual", "Unidade"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(24);
        JScrollPane scrollTable = new JScrollPane(tabela);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Insumos de Cafeteria"));
        rightPanel.add(scrollTable, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row >= 0) {
                txtId.setText(model.getValueAt(row, 0).toString());
                txtNome.setText(model.getValueAt(row, 1).toString());
                txtQuantidade.setText(model.getValueAt(row, 2).toString());
                txtUnidade.setText(model.getValueAt(row, 3).toString());
            }
        });

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        carregarDados();
    }

    private void carregarDados() {
        model.setRowCount(0);
        String filter = txtPesquisa != null ? txtPesquisa.getText() : "";
        List<Ingrediente> ingredientes = dao.listar(filter);
        for (Ingrediente ing : ingredientes) {
            model.addRow(new Object[]{
                ing.getIdIngrediente(),
                ing.getNomeIngrediente(),
                ing.getQuantidadeAtual(),
                ing.getUnidadeMedida()
            });
        }
    }

    private void filtrar() {
        carregarDados();
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String qtyStr = txtQuantidade.getText().trim().replace(",", ".");
            String unidade = txtUnidade.getText().trim();

            if (nome.isEmpty() || qtyStr.isEmpty() || unidade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.");
                return;
            }

            double qty = Double.parseDouble(qtyStr);

            if (txtId.getText().isEmpty()) {
                Ingrediente ing = new Ingrediente(0, nome, qty, unidade);
                dao.inserir(ing);
                JOptionPane.showMessageDialog(this, "Insumo cadastrado com sucesso!");
            } else {
                int id = Integer.parseInt(txtId.getText());
                Ingrediente ing = new Ingrediente(id, nome, qty, unidade);
                dao.atualizar(ing);
                JOptionPane.showMessageDialog(this, "Insumo atualizado com sucesso!");
            }
            carregarDados();
            limpar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser um valor numérico válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar insumo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um insumo para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este insumo?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                dao.deletar(id);
                JOptionPane.showMessageDialog(this, "Insumo removido com sucesso!");
                carregarDados();
                limpar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtQuantidade.setText("");
        txtUnidade.setText("");
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
