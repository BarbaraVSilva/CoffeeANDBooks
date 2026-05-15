package view;

import javax.swing.*;
import java.awt.*;
import util.UIConstants;
import util.DatabaseUtil;
import java.sql.*;

public class FinancialDashboardFrame extends JFrame {
    private JLabel lblVendasTotais, lblFaturamento;

    public FinancialDashboardFrame() {
        setTitle("Coffee&Books - Dashboard Financeiro");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card1 = createCard("Total de Vendas Realizadas");
        lblVendasTotais = new JLabel("0", SwingConstants.CENTER);
        lblVendasTotais.setFont(new Font("SansSerif", Font.BOLD, 48));
        card1.add(lblVendasTotais, BorderLayout.CENTER);

        JPanel card2 = createCard("Faturamento Total (R$)");
        lblFaturamento = new JLabel("0.00", SwingConstants.CENTER);
        lblFaturamento.setFont(new Font("SansSerif", Font.BOLD, 48));
        lblFaturamento.setForeground(UIConstants.COLOR_ACCENT());
        card2.add(lblFaturamento, BorderLayout.CENTER);

        mainPanel.add(card1);
        mainPanel.add(card2);
        add(mainPanel, BorderLayout.CENTER);

        carregarDados();
    }

    private JPanel createCard(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.COLOR_SECONDARY());
        p.setBorder(UIConstants.getPanelBorder());
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(UIConstants.FONT_LABEL);
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private void carregarDados() {
        String sql = "SELECT COUNT(*) as total, SUM(valor_total) as soma FROM VENDA_CONSOLIDADA";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                lblVendasTotais.setText(String.valueOf(rs.getInt("total")));
                lblFaturamento.setText(String.format("%.2f", rs.getDouble("soma")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
