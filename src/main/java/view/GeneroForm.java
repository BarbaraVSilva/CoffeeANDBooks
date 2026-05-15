package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.UIConstants;

public class GeneroForm extends JFrame {
    private JTextField txtId, txtNome, txtEstante, txtCorEtiqueta;
    private JTextArea txtDescricao;
    private JTable tabela;
    private DefaultTableModel model;

    public GeneroForm() {
        setTitle("Gestão de Gêneros Literários");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("📝 Cadastro de Gêneros", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

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
        gbc.gridx = 1; formPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nome do Gênero:"), gbc);
        txtNome = new JTextField(15);
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Estante:"), gbc);
        txtEstante = new JTextField(15);
        gbc.gridx = 1; formPanel.add(txtEstante, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Cor Etiqueta:"), gbc);
        txtCorEtiqueta = new JTextField(15);
        gbc.gridx = 1; formPanel.add(txtCorEtiqueta, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Descrição:"), gbc);
        txtDescricao = new JTextArea(3, 15);
        txtDescricao.setLineWrap(true);
        gbc.gridx = 1; formPanel.add(new JScrollPane(txtDescricao), gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo");
        JButton btnSalvar = createStyledButton("Salvar");
        JButton btnEditar = createStyledButton("Editar");
        JButton btnExcluir = createStyledButton("Excluir");
        btnExcluir.setEnabled(util.SessionManager.isAdmin());
        btnPanel.add(btnNovo); btnPanel.add(btnSalvar);
        btnPanel.add(btnEditar); btnPanel.add(btnExcluir);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // RIGHT: Table
        String[] cols = {"ID", "Nome", "Estante", "Cor"};
        model = new DefaultTableModel(cols, 0);
        tabela = new JTable(model);
        JScrollPane scrollTable = new JScrollPane(tabela);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Gêneros Cadastrados"));

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(scrollTable);
        add(splitPane, BorderLayout.CENTER);
        
        // Mock Data
        model.addRow(new Object[]{1, "Ficção Científica", "Corredor A", "Azul"});
        model.addRow(new Object[]{2, "Suspense", "Corredor B", "Vermelho"});
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
