package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import model.Evento;
import dao.EventoDAO;
import util.UIConstants;

public class EventoFrame extends JFrame {
    private JTextField txtId, txtNome, txtData;
    private JComboBox<String> cbTipo;
    private JTextArea txtDesc;
    private JTable tabela;
    private DefaultTableModel model;
    private EventoDAO dao;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public EventoFrame() {
        dao = new EventoDAO();
        sdf.setLenient(false);

        setTitle("Coffee&Books - Gestão de Eventos & Encontros Literários");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("🎉 Planejamento de Eventos, Oficinas e Autógrafos", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);

        // LEFT: Form Panel
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

        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Nome do Evento:"), gbc);
        txtNome = new JTextField(18); txtNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Data/Hora (dd/mm/aaaa hh:mm):"), gbc);
        txtData = new JTextField(15); txtData.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtData, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Tipo de Evento:"), gbc);
        cbTipo = new JComboBox<>(new String[]{"Troca de Livros", "Workshop Café", "Noite de Autógrafos", "Clube de Leitura"});
        cbTipo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(cbTipo, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Descrição / Notas:"), gbc);
        txtDesc = new JTextArea(4, 18);
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        gbc.gridx = 1; formPanel.add(new JScrollPane(txtDesc), gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo");
        JButton btnSalvar = createStyledButton("Salvar");
        JButton btnExcluir = createStyledButton("Excluir");
        btnExcluir.setEnabled(util.SessionManager.isAdmin());
        
        btnPanel.add(btnNovo); btnPanel.add(btnSalvar); btnPanel.add(btnExcluir);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        splitPane.setLeftComponent(formPanel);

        // RIGHT: Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        String[] cols = {"ID", "Evento", "Data e Hora", "Tipo de Evento", "Descrição"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(24);
        JScrollPane scrollTable = new JScrollPane(tabela);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Eventos Cadastrados"));
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
                txtData.setText(model.getValueAt(row, 2).toString());
                cbTipo.setSelectedItem(model.getValueAt(row, 3).toString());
                txtDesc.setText(model.getValueAt(row, 4).toString());
            }
        });

        carregarDados();
    }

    private void carregarDados() {
        model.setRowCount(0);
        List<Evento> eventos = dao.listar();
        for (Evento e : eventos) {
            model.addRow(new Object[]{
                e.getIdEvento(),
                e.getNomeEvento(),
                sdf.format(e.getDataEvento()),
                e.getTipoEvento(),
                e.getDescricao()
            });
        }
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String dataRaw = txtData.getText().trim();
            String tipo = (String) cbTipo.getSelectedItem();
            String desc = txtDesc.getText().trim();

            if (nome.isEmpty() || dataRaw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome do Evento e Data/Hora são obrigatórios.");
                return;
            }

            Date data = sdf.parse(dataRaw);

            if (txtId.getText().isEmpty()) {
                Evento e = new Evento(0, nome, data, tipo, desc);
                dao.inserir(e);
                JOptionPane.showMessageDialog(this, "Evento agendado com sucesso!");
            } else {
                int id = Integer.parseInt(txtId.getText());
                Evento e = new Evento(id, nome, data, tipo, desc);
                dao.atualizar(e);
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!");
            }
            carregarDados();
            limpar();
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido! Use o padrão: dd/mm/aaaa hh:mm", "Erro de Formatação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar evento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este evento?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                dao.deletar(id);
                JOptionPane.showMessageDialog(this, "Evento removido com sucesso!");
                carregarDados();
                limpar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir evento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtData.setText("");
        cbTipo.setSelectedIndex(0);
        txtDesc.setText("");
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
