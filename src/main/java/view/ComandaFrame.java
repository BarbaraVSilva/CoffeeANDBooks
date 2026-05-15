package view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.Comanda;
import util.UIConstants;

public class ComandaFrame extends JFrame {
    private Map<Integer, Comanda> comandasAtivas = new HashMap<>();
    private JPanel gridPanel;

    public ComandaFrame() {
        setTitle("Coffee&Books - Gestor de Comandas Ativas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JLabel lblHeader = new JLabel("☕ Mapa de Mesas & Consumo", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(4, 5, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 20; i++) {
            JButton btnMesa = createMesaButton(i);
            gridPanel.add(btnMesa);
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        legendPanel.setBackground(UIConstants.COLOR_SECONDARY());
        legendPanel.add(createLegend("Livre", UIConstants.COLOR_SUCCESS));
        legendPanel.add(createLegend("Ocupada", UIConstants.COLOR_ALERT));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JButton createMesaButton(int num) {
        JButton btn = new JButton("Mesa " + num);
        btn.setFont(UIConstants.FONT_LABEL);
        btn.setBackground(UIConstants.COLOR_SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 100));

        btn.addActionListener(e -> {
            try {
                abrirDetalheMesa(num, btn);
            } catch (exception.MesaJaOcupadaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        return btn;
    }

    private void abrirDetalheMesa(int num, JButton btn) throws exception.MesaJaOcupadaException {
        Comanda c = comandasAtivas.get(num);
        if (c == null) {
            int opt = JOptionPane.showConfirmDialog(this, "Abrir comanda para a Mesa " + num + "?");
            if (opt == JOptionPane.YES_OPTION) {
                comandasAtivas.put(num, new Comanda(num));
                btn.setBackground(UIConstants.COLOR_ALERT);
                btn.setText("Mesa " + num + " (Ocup)");
            }
        } else {
            // Se já tem, poderia lançar a exceção se tentasse "Abrir" novamente, 
            // mas aqui vamos apenas mostrar o detalhe. 
            // Para demonstrar o uso, vamos lançar se o usuário clicar num botão "Nova Comanda" inexistente
            // ou se o status for inconsistente.
            // Placeholder for "Add Items" or "Close Account"
            double total = c.getSubtotal();
            int opt = JOptionPane.showOptionDialog(this, 
                "Mesa " + num + " - Consumo: R$ " + String.format("%.2f", total), 
                "Gestão de Mesa", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                new String[]{"Adicionar Café", "Fechar Conta", "Voltar"}, 
                "Voltar");
            
            if (opt == 1) { // Fechar
                JOptionPane.showMessageDialog(this, "Conta da Mesa " + num + " fechada. Total: R$ " + total);
                comandasAtivas.remove(num);
                btn.setBackground(UIConstants.COLOR_SUCCESS);
                btn.setText("Mesa " + num);
            }
        }
    }

    private JPanel createLegend(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout());
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(20, 20));
        box.setBackground(color);
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }
}
