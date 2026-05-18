package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Reserva;
import util.UIConstants;
import util.TimeParser;

public class ReservaForm extends JFrame {
    private static List<Reserva> listaReservas = new ArrayList<>();
    private JTextField txtCliente, txtMesa, txtHorario, txtTempo, txtPesquisa;
    private JCheckBox chkTomada;
    private JTable tabela;
    private DefaultTableModel model;
    private int selectedRowIndex = -1;

    static {
        // Mock data to start with some active reservations
        listaReservas.add(new Reserva("Amanda Costa", 3, "14:30", 2, true));
        listaReservas.add(new Reserva("Bernardo M.", 8, "16:00", 1, false));
    }

    public ReservaForm() {
        setTitle("Espaço Cultural - Gestão de Poltronas");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("🛋️ Reservas Ativas no Salão", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // LEFT: Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.setBorder(BorderFactory.createTitledBorder(UIConstants.getRoundedBorder(UIConstants.COLOR_ACCENT()), "Nova Reserva / Edição"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome do Cliente:"), gbc);
        txtCliente = new JTextField(15); 
        txtCliente.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Mesa / Poltrona Nº:"), gbc);
        txtMesa = new JTextField(5); 
        txtMesa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtMesa, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Horário Entrada:"), gbc);
        txtHorario = new JTextField(10); 
        txtHorario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtHorario.setToolTipText("Ex: 12:30, 12h30, 12h");
        gbc.gridx = 1; formPanel.add(txtHorario, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Permanência (h):"), gbc);
        txtTempo = new JTextField(5); 
        txtTempo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtTempo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        chkTomada = new JCheckBox("Necessita Tomada (Kindle/Note)");
        chkTomada.setBackground(UIConstants.COLOR_SECONDARY());
        chkTomada.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(chkTomada, gbc);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);
        JButton btnNovo = createStyledButton("Novo", UIConstants.COLOR_ACCENT());
        JButton btnSalvar = createStyledButton("Salvar", UIConstants.COLOR_SUCCESS);
        JButton btnLiberar = createStyledButton("Liberar Espaço", UIConstants.COLOR_ALERT);
        btnPanel.add(btnNovo); 
        btnPanel.add(btnSalvar); 
        btnPanel.add(btnLiberar);
        
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // RIGHT: Search & Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Pesquisar por Cliente/Mesa:"), BorderLayout.WEST);
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(txtPesquisa, BorderLayout.CENTER);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID Interno", "Cliente", "Mesa", "Entrada", "Tempo", "Tomada"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(22);
        
        // Hide first column (ID Interno / Raw index)
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Mapa do Salão - Reservas"));
        rightPanel.add(scroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Events
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnLiberar.addActionListener(e -> liberar());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row >= 0) {
                selectedRowIndex = (int) model.getValueAt(row, 0);
                Reserva r = listaReservas.get(selectedRowIndex);
                txtCliente.setText(r.getNomeCliente());
                txtMesa.setText(String.valueOf(r.getNumeroMesa()));
                txtHorario.setText(r.getHorarioEntrada());
                txtTempo.setText(String.valueOf(r.getTempoPermanencia()));
                chkTomada.setSelected(r.isNecessitaTomada());
            }
        });

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        atualizarTabela();
    }

    private void salvar() {
        try {
            String cliente = txtCliente.getText().trim();
            String mesaStr = txtMesa.getText().trim();
            String horarioRaw = txtHorario.getText().trim();
            String tempoStr = txtTempo.getText().trim();

            if (cliente.isEmpty() || mesaStr.isEmpty() || horarioRaw.isEmpty() || tempoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.");
                return;
            }

            int mesa = Integer.parseInt(mesaStr);
            int tempo = Integer.parseInt(tempoStr);
            
            // Normalize time flexibly using TimeParser
            String horario = TimeParser.normalizeTime(horarioRaw);

            Reserva r = new Reserva(cliente, mesa, horario, tempo, chkTomada.isSelected());

            if (selectedRowIndex < 0) {
                listaReservas.add(r);
                util.ComandaManager.abrirComanda(mesa, cliente);
                JOptionPane.showMessageDialog(this, "Reserva adicionada e Comanda aberta com sucesso!");
            } else {
                Reserva oldRes = listaReservas.get(selectedRowIndex);
                util.ComandaManager.fecharComanda(oldRes.getNumeroMesa());
                
                listaReservas.set(selectedRowIndex, r);
                util.ComandaManager.abrirComanda(mesa, cliente);
                JOptionPane.showMessageDialog(this, "Reserva e Comanda atualizadas com sucesso!");
            }
            
            atualizarTabela();
            limpar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de Mesa e Permanência devem ser valores inteiros.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dados inválidos: " + ex.getMessage());
        }
    }

    private void liberar() {
        if (selectedRowIndex >= 0) {
            Reserva r = listaReservas.get(selectedRowIndex);
            util.ComandaManager.fecharComanda(r.getNumeroMesa());
            
            listaReservas.remove(selectedRowIndex);
            JOptionPane.showMessageDialog(this, "Poltrona/Mesa liberada e comanda finalizada!");
            atualizarTabela();
            limpar();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para liberar.");
        }
    }

    private void filtrar() {
        atualizarTabela();
    }

    private void atualizarTabela() {
        model.setRowCount(0);
        String searchVal = txtPesquisa != null ? txtPesquisa.getText().toLowerCase().trim() : "";

        for (int i = 0; i < listaReservas.size(); i++) {
            Reserva r = listaReservas.get(i);
            boolean matches = r.getNomeCliente().toLowerCase().contains(searchVal) || 
                              String.valueOf(r.getNumeroMesa()).contains(searchVal);
            
            if (matches) {
                model.addRow(new Object[]{
                    i, // Raw index stored in hidden cell for selection retrieval
                    r.getNomeCliente(), 
                    r.getNumeroMesa(), 
                    r.getHorarioEntrada(), 
                    r.getTempoPermanencia() + "h", 
                    r.isNecessitaTomada() ? "Sim" : "Não"
                });
            }
        }
    }

    private void limpar() {
        txtCliente.setText(""); 
        txtMesa.setText(""); 
        txtHorario.setText(""); 
        txtTempo.setText(""); 
        chkTomada.setSelected(false);
        selectedRowIndex = -1;
        tabela.clearSelection();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
