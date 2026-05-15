package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.Reserva;
import util.UIConstants;

public class ReservaForm extends JFrame {
    private static List<Reserva> listaReservas = new ArrayList<>();
    private JTextField txtCliente, txtMesa, txtHorario, txtTempo;
    private JCheckBox chkTomada;
    private JTable tabela;
    private DefaultTableModel model;

    public ReservaForm() {
        setTitle("Espaço Cultural - Gestão de Poltronas");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("🛋️ Reservas Ativas no Salão", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT: Form (Floor Plan Simulation)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.setBorder(BorderFactory.createTitledBorder(UIConstants.getRoundedBorder(UIConstants.COLOR_ACCENT()), "Nova Reserva"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome do Cliente:"), gbc);
        txtCliente = new JTextField(15); gbc.gridx = 1; formPanel.add(txtCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Número Mesa/Poltrona:"), gbc);
        txtMesa = new JTextField(5); gbc.gridx = 1; formPanel.add(txtMesa, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Horário Entrada:"), gbc);
        txtHorario = new JTextField(10); gbc.gridx = 1; formPanel.add(txtHorario, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Permanência (h):"), gbc);
        txtTempo = new JTextField(5); gbc.gridx = 1; formPanel.add(txtTempo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        chkTomada = new JCheckBox("Necessita Tomada (Kindle/Note)");
        chkTomada.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.add(chkTomada, gbc);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        JButton btnAdd = createStyledButton("Adicionar à Lista", UIConstants.COLOR_ACCENT());
        JButton btnLiberar = createStyledButton("Liberar Espaço", UIConstants.COLOR_ALERT);
        btnPanel.add(btnAdd); btnPanel.add(btnLiberar);
        
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        mainPanel.add(formPanel);

        // RIGHT: Table
        String[] cols = {"Cliente", "Mesa", "Entrada", "Tempo", "Tomada"};
        model = new DefaultTableModel(cols, 0);
        tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Mapa do Salão - Reservas"));
        mainPanel.add(scroll);

        add(mainPanel, BorderLayout.CENTER);

        // Events
        btnAdd.addActionListener(e -> adicionar());
        btnLiberar.addActionListener(e -> liberar());

        atualizarTabela();
    }

    private void adicionar() {
        try {
            listaReservas.add(new Reserva(txtCliente.getText(), Integer.parseInt(txtMesa.getText()), 
                txtHorario.getText(), Integer.parseInt(txtTempo.getText()), chkTomada.isSelected()));
            atualizarTabela();
            limpar();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Dados inválidos."); }
    }

    private void liberar() {
        int idx = tabela.getSelectedRow();
        if (idx >= 0) {
            listaReservas.remove(idx);
            atualizarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para liberar.");
        }
    }

    private void atualizarTabela() {
        model.setRowCount(0);
        for (Reserva r : listaReservas) {
            model.addRow(new Object[]{r.getNomeCliente(), r.getNumeroMesa(), r.getHorarioEntrada(), 
                r.getTempoPermanencia()+"h", r.isNecessitaTomada() ? "Sim" : "Não"});
        }
    }

    private void limpar() {
        txtCliente.setText(""); txtMesa.setText(""); txtHorario.setText(""); txtTempo.setText(""); chkTomada.setSelected(false);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        return btn;
    }
}
