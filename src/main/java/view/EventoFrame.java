package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import util.UIConstants;

public class EventoFrame extends JFrame {
    private JTextField txtNome, txtData;
    private JComboBox<String> cbTipo;
    private JTextArea txtDesc;
    private JTable tabela;
    private DefaultTableModel model;

    public EventoFrame() {
        setTitle("Coffee&Books - Gestão de Eventos & Troca de Livros");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome do Evento:"), gbc);
        txtNome = new JTextField(15); gbc.gridx = 1; formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Data/Hora:"), gbc);
        txtData = new JTextField("25/05/2026 19:00", 15); gbc.gridx = 1; formPanel.add(txtData, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tipo:"), gbc);
        cbTipo = new JComboBox<>(new String[]{"Troca de Livros", "Workshop Café", "Noite de Autógrafos"});
        gbc.gridx = 1; formPanel.add(cbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Descrição:"), gbc);
        txtDesc = new JTextArea(3, 15); gbc.gridx = 1; formPanel.add(new JScrollPane(txtDesc), gbc);

        JButton btnAdd = new JButton("Agendar Evento");
        btnAdd.setBackground(UIConstants.COLOR_ACCENT());
        btnAdd.setForeground(Color.WHITE);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        formPanel.add(btnAdd, gbc);

        add(formPanel, BorderLayout.WEST);

        // Table
        String[] cols = {"Evento", "Data", "Tipo"};
        model = new DefaultTableModel(cols, 0);
        tabela = new JTable(model);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Mock
        model.addRow(new Object[]{"Clube do Livro: Duna", "20/05 18h", "Troca de Livros"});
        
        btnAdd.addActionListener(e -> {
            model.addRow(new Object[]{txtNome.getText(), txtData.getText(), cbTipo.getSelectedItem()});
            JOptionPane.showMessageDialog(this, "Evento agendado!");
        });
    }
}
