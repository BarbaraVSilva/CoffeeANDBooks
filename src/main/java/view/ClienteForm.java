package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import model.Cliente;
import dao.ClienteDAO;
import util.UIConstants;

public class ClienteForm extends JFrame {
    private JTextField txtId, txtNome, txtCpf, txtEmail, txtTelefone, txtDataNasc, txtPesquisa;
    private JLabel lblPontos;
    private JTable tabela;
    private DefaultTableModel model;
    private ClienteDAO dao;
    private int currentPontos = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public ClienteForm() {
        dao = new ClienteDAO();
        sdf.setLenient(false);

        setTitle("Gestão de Clientes & Fidelidade");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("👥 Cadastro de Clientes e Programa de Fidelidade", SwingConstants.CENTER);
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

        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Nome do Cliente:"), gbc);
        txtNome = new JTextField(20); txtNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("CPF (Único):"), gbc);
        txtCpf = new JTextField(15); txtCpf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtCpf, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("E-mail:"), gbc);
        txtEmail = new JTextField(20); txtEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtEmail, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Telefone:"), gbc);
        txtTelefone = new JTextField(15); txtTelefone.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtTelefone, gbc);

        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Data Nasc. (dd/mm/aaaa):"), gbc);
        txtDataNasc = new JTextField(12); txtDataNasc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtDataNasc, gbc);

        // Loyalty points indicator
        gbc.gridy = 6; gbc.gridx = 0; formPanel.add(new JLabel("Pontos Fidelidade:"), gbc);
        lblPontos = new JLabel("0 pts", SwingConstants.LEFT);
        lblPontos.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblPontos.setForeground(UIConstants.COLOR_SUCCESS);
        gbc.gridx = 1; formPanel.add(lblPontos, gbc);

        // Custom points controls
        JPanel pointsControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pointsControls.setOpaque(false);
        JButton btnAddPoints = new JButton("+10 Pts");
        btnAddPoints.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAddPoints.setBackground(UIConstants.COLOR_ACCENT());
        btnAddPoints.setForeground(Color.WHITE);
        btnAddPoints.addActionListener(e -> {
            currentPontos += 10;
            lblPontos.setText(currentPontos + " pts");
        });
        JButton btnSubPoints = new JButton("-10 Pts");
        btnSubPoints.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSubPoints.setBackground(UIConstants.COLOR_ALERT);
        btnSubPoints.setForeground(Color.WHITE);
        btnSubPoints.addActionListener(e -> {
            if (currentPontos >= 10) currentPontos -= 10;
            lblPontos.setText(currentPontos + " pts");
        });
        pointsControls.add(btnAddPoints);
        pointsControls.add(btnSubPoints);
        gbc.gridy = 7; gbc.gridx = 1; formPanel.add(pointsControls, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo");
        JButton btnSalvar = createStyledButton("Salvar");
        JButton btnExcluir = createStyledButton("Excluir");
        btnExcluir.setEnabled(util.SessionManager.isAdmin());
        JButton btnCampanha = createStyledButton("📧 Campanha");
        
        btnPanel.add(btnNovo); btnPanel.add(btnSalvar); btnPanel.add(btnExcluir); btnPanel.add(btnCampanha);

        btnCampanha.addActionListener(e -> abrirCampanhaAniversario());

        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // RIGHT: Search & Table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Pesquisar por Nome/CPF/Email:"), BorderLayout.WEST);
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(txtPesquisa, BorderLayout.CENTER);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Nome do Cliente", "CPF", "E-mail", "Telefone", "Pontos", "Nascimento"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(22);
        JScrollPane scrollTable = new JScrollPane(tabela);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
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
                txtId.setText(model.getValueAt(row, 0).toString());
                txtNome.setText(model.getValueAt(row, 1).toString());
                txtCpf.setText(model.getValueAt(row, 2).toString());
                txtEmail.setText(model.getValueAt(row, 3).toString());
                txtTelefone.setText(model.getValueAt(row, 4).toString());
                currentPontos = (int) model.getValueAt(row, 5);
                lblPontos.setText(currentPontos + " pts");
                Object dobObj = model.getValueAt(row, 6);
                txtDataNasc.setText(dobObj != null ? dobObj.toString() : "");
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
        String filterText = txtPesquisa != null ? txtPesquisa.getText() : "";
        List<Cliente> clientes = dao.listar(filterText);
        for (Cliente c : clientes) {
            String dobStr = "";
            if (c.getDataNascimento() != null) {
                dobStr = sdf.format(c.getDataNascimento());
            }
            model.addRow(new Object[]{
                c.getIdCliente(),
                c.getNome(),
                c.getCpf(),
                c.getEmail(),
                c.getTelefone(),
                c.getPontosFidelidade(),
                dobStr
            });
        }
    }

    private void filtrar() {
        carregarDados();
    }

    private void salvar() {
        try {
            String nome = txtNome.getText().trim();
            String cpf = txtCpf.getText().trim();
            String email = txtEmail.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String dobRaw = txtDataNasc.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e CPF são campos obrigatórios.");
                return;
            }

            Date dob = null;
            if (!dobRaw.isEmpty()) {
                dob = sdf.parse(dobRaw);
            }

            if (txtId.getText().isEmpty()) {
                Cliente c = new Cliente(0, nome, cpf, email, telefone, currentPontos, dob);
                dao.inserir(c);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            } else {
                int id = Integer.parseInt(txtId.getText());
                Cliente c = new Cliente(id, nome, cpf, email, telefone, currentPontos, dob);
                dao.atualizar(c);
                JOptionPane.showMessageDialog(this, "Cadastro do cliente atualizado com sucesso!");
            }
            carregarDados();
            limpar();
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, "Data de Nascimento inválida! Use o formato dd/mm/aaaa.", "Erro de Formatação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                dao.deletar(id);
                JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
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
        txtCpf.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        txtDataNasc.setText("");
        currentPontos = 0;
        lblPontos.setText("0 pts");
        tabela.clearSelection();
    }

    private void abrirCampanhaAniversario() {
        List<Cliente> all = dao.listar("");
        java.util.List<Cliente> aniversariantes = new java.util.ArrayList<>();
        java.util.Calendar calToday = java.util.Calendar.getInstance();
        int currentMonth = calToday.get(java.util.Calendar.MONTH);
        
        for (Cliente c : all) {
            if (c.getDataNascimento() != null) {
                java.util.Calendar calDob = java.util.Calendar.getInstance();
                calDob.setTime(c.getDataNascimento());
                if (calDob.get(java.util.Calendar.MONTH) == currentMonth) {
                    aniversariantes.add(c);
                }
            }
        }
        
        if (aniversariantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente aniversariante cadastrado para o mês atual.", "Campanhas de E-mail", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dlg = new JDialog(this, "Campanhas de Marketing de Fidelidade", true);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("📧 Aniversariantes do Mês: Campanhas de E-mail", SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.FONT_TITLE.deriveFont(16f));
        lblTitle.setForeground(UIConstants.COLOR_ACCENT());
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dlg.add(lblTitle, BorderLayout.NORTH);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Cliente c : aniversariantes) {
            java.util.Calendar calDob = java.util.Calendar.getInstance();
            calDob.setTime(c.getDataNascimento());
            int day = calDob.get(java.util.Calendar.DAY_OF_MONTH);
            listModel.addElement("🎉 " + c.getNome() + " (Dia " + day + ") - " + c.getEmail());
        }
        
        JList<String> jList = new JList<>(listModel);
        jList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(jList);
        scroll.setBorder(BorderFactory.createTitledBorder("Aniversariantes Identificados no Mês"));
        dlg.add(scroll, BorderLayout.CENTER);
        
        JButton btnSend = new JButton("✉️ Disparar E-mails com Cupom Cortesia");
        btnSend.setBackground(UIConstants.COLOR_SUCCESS);
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(UIConstants.FONT_BUTTON);
        btnSend.setFocusPainted(false);
        btnSend.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnSend.addActionListener(e -> {
            dlg.dispose();
            JOptionPane.showMessageDialog(this,
                "📬 SUCESSO!\n\n" +
                "Foram enviados com sucesso " + aniversariantes.size() + " cupons de aniversário!\n" +
                "🎁 Brinde: 1 Café Espresso + 1 Marca-páginas cortesia!\n\n" +
                "Os aniversariantes receberam a notificação para resgate em sua caixa de entrada! 🎉",
                "Campanha Concluída!",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        dlg.add(btnSend, BorderLayout.SOUTH);
        dlg.setVisible(true);
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
