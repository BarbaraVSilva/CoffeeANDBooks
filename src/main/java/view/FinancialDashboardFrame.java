package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.UIConstants;
import util.DatabaseUtil;

public class FinancialDashboardFrame extends JFrame {
    private JLabel lblVendasTotais, lblFaturamento;
    private BarChartPanel barChartPanel;
    private LineChartPanel lineChartPanel;

    public FinancialDashboardFrame() {
        setTitle("Coffee&Books - Painel de Controle Financeiro");
        setSize(1150, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header Label
        JLabel lblHeader = new JLabel("📊 Relatório & Performance Financeira", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE.deriveFont(22f));
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Main Panel (Vertical)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Cards Panel (Top)
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        cardsPanel.setOpaque(false);

        JPanel card1 = createCard("Total de Transações (PDV)");
        lblVendasTotais = new JLabel("0", SwingConstants.CENTER);
        lblVendasTotais.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblVendasTotais.setForeground(UIConstants.COLOR_ACCENT());
        card1.add(lblVendasTotais, BorderLayout.CENTER);

        JPanel card2 = createCard("Faturamento Consolidado");
        lblFaturamento = new JLabel("R$ 0,00", SwingConstants.CENTER);
        lblFaturamento.setFont(new Font("SansSerif", Font.BOLD, 42));
        lblFaturamento.setForeground(UIConstants.COLOR_SUCCESS);
        card2.add(lblFaturamento, BorderLayout.CENTER);

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Charts Grid Panel (Side-by-Side)
        JPanel chartsSplitPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsSplitPanel.setOpaque(false);

        // LEFT: Bar Chart
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(UIConstants.COLOR_SECONDARY());
        barContainer.setBorder(BorderFactory.createCompoundBorder(
            UIConstants.getPanelBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblBarTitle = new JLabel("Vendas por Forma de Pagamento", SwingConstants.LEFT);
        lblBarTitle.setFont(UIConstants.FONT_LABEL);
        lblBarTitle.setForeground(UIConstants.COLOR_ACCENT());
        barContainer.add(lblBarTitle, BorderLayout.NORTH);

        barChartPanel = new BarChartPanel();
        barChartPanel.setBackground(UIConstants.COLOR_SECONDARY());
        barContainer.add(barChartPanel, BorderLayout.CENTER);
        chartsSplitPanel.add(barContainer);

        // RIGHT: Line Chart (Faturamento Trend)
        JPanel lineContainer = new JPanel(new BorderLayout());
        lineContainer.setBackground(UIConstants.COLOR_SECONDARY());
        lineContainer.setBorder(BorderFactory.createCompoundBorder(
            UIConstants.getPanelBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblLineTitle = new JLabel("Evolução de Faturamento (Últimos 7 Dias)", SwingConstants.LEFT);
        lblLineTitle.setFont(UIConstants.FONT_LABEL);
        lblLineTitle.setForeground(UIConstants.COLOR_ACCENT());
        lineContainer.add(lblLineTitle, BorderLayout.NORTH);

        lineChartPanel = new LineChartPanel();
        lineChartPanel.setBackground(UIConstants.COLOR_SECONDARY());
        lineContainer.add(lineChartPanel, BorderLayout.CENTER);
        chartsSplitPanel.add(lineContainer);

        centerPanel.add(chartsSplitPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JButton btnExportar = new JButton("📥 Baixar Relatório Financeiro (.txt)");
        btnExportar.setBackground(UIConstants.COLOR_ACCENT());
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFont(UIConstants.FONT_BUTTON);
        btnExportar.setFocusPainted(false);
        btnExportar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        footerPanel.add(btnExportar);
        add(footerPanel, BorderLayout.SOUTH);

        btnExportar.addActionListener(e -> exportarRelatorioTxt());

        carregarDados();
    }

    private JPanel createCard(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.COLOR_SECONDARY());
        p.setBorder(UIConstants.getPanelBorder());
        p.setPreferredSize(new Dimension(0, 120));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(UIConstants.FONT_LABEL);
        lbl.setForeground(UIConstants.TEXT_COLOR().darker());
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private void carregarDados() {
        // 1. General Metrics
        String sqlMetrics = "SELECT COUNT(*) as total, SUM(valor_total) as soma FROM VENDA_CONSOLIDADA";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlMetrics)) {
            if (rs.next()) {
                lblVendasTotais.setText(String.valueOf(rs.getInt("total")));
                lblFaturamento.setText(String.format("R$ %.2f", rs.getDouble("soma")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Bar Chart Metrics (Sales by Payment Method)
        String sqlChart = "SELECT forma_pagamento, SUM(valor_total) as faturamento FROM VENDA_CONSOLIDADA GROUP BY forma_pagamento";
        List<String> barCategories = new ArrayList<>();
        List<Double> barValues = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlChart)) {
            while (rs.next()) {
                String pg = rs.getString("forma_pagamento");
                if (pg == null || pg.trim().isEmpty()) {
                    pg = "Outros";
                }
                barCategories.add(pg);
                barValues.add(rs.getDouble("faturamento"));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        barChartPanel.setData(barCategories, barValues);

        // 3. Line Chart Metrics (Sales Trend - Last 7 Days)
        String sqlLine = "SELECT DATE(data_venda) as dia, SUM(valor_total) as faturamento " +
                         "FROM VENDA_CONSOLIDADA " +
                         "WHERE data_venda >= DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) " +
                         "GROUP BY DATE(data_venda) " +
                         "ORDER BY DATE(data_venda) ASC";
        
        List<String> lineCategories = new ArrayList<>();
        List<Double> lineValues = new ArrayList<>();
        
        java.text.SimpleDateFormat sdfDay = new java.text.SimpleDateFormat("dd/MM");
        for (int i = 6; i >= 0; i--) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_YEAR, -i);
            lineCategories.add(sdfDay.format(cal.getTime()));
            lineValues.add(0.0);
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlLine)) {
            while (rs.next()) {
                Date d = rs.getDate("dia");
                String formatted = sdfDay.format(d);
                double faturamento = rs.getDouble("faturamento");
                
                int idx = lineCategories.indexOf(formatted);
                if (idx >= 0) {
                    lineValues.set(idx, faturamento);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        lineChartPanel.setData(lineCategories, lineValues);
    }

    private void exportarRelatorioTxt() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("relatorio_financeiro.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = fc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"))) {
                pw.println("==========================================================================");
                pw.println("                 COFFEE & BOOKS - RELATÓRIO FINANCEIRO GERENCIAL          ");
                pw.println("==========================================================================");
                pw.println("Gerado em: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
                pw.println("Faturamento Total Acumulado: " + lblFaturamento.getText());
                pw.println("Total de Vendas Realizadas: " + lblVendasTotais.getText());
                pw.println("==========================================================================");
                pw.println();
                
                pw.println("1. DETALHAMENTO DE VENDAS POR FORMA DE PAGAMENTO:");
                pw.println("--------------------------------------------------------------------------");
                String sqlChart = "SELECT forma_pagamento, SUM(valor_total) as faturamento FROM VENDA_CONSOLIDADA GROUP BY forma_pagamento";
                try (Connection conn = DatabaseUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlChart)) {
                    while (rs.next()) {
                        String pg = rs.getString("forma_pagamento");
                        double faturamento = rs.getDouble("faturamento");
                        pw.println(String.format(" - %-25s : R$ %,.2f", (pg == null ? "Outros" : pg), faturamento));
                    }
                } catch (SQLException ex) {
                    pw.println("Erro ao carregar detalhes: " + ex.getMessage());
                }
                pw.println();
                
                pw.println("2. LOG COMPLETO DE TRANSAÇÕES CONSOLIDADAS:");
                pw.println("--------------------------------------------------------------------------");
                pw.println(String.format("%-10s | %-19s | %-15s | %-20s | %-6s", "ID VENDA", "DATA DA TRANSAÇÃO", "FATURAMENTO", "PAGAMENTO", "MESA"));
                pw.println("--------------------------------------------------------------------------");
                String sqlLog = "SELECT id_venda, data_venda, valor_total, forma_pagamento, numero_mesa FROM VENDA_CONSOLIDADA ORDER BY data_venda DESC";
                try (Connection conn = DatabaseUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlLog)) {
                    java.text.SimpleDateFormat sdfSql = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    while (rs.next()) {
                        int id = rs.getInt("id_venda");
                        Timestamp ts = rs.getTimestamp("data_venda");
                        double val = rs.getDouble("valor_total");
                        String pg = rs.getString("forma_pagamento");
                        int mesa = rs.getInt("numero_mesa");
                        
                        pw.println(String.format("%-10d | %-19s | R$ %-12.2f | %-20s | %-6s", 
                            id, 
                            (ts != null ? sdfSql.format(ts) : "N/A"), 
                            val, 
                            (pg != null ? pg : "Outros"), 
                            (mesa > 0 ? String.valueOf(mesa) : "-")
                        ));
                    }
                } catch (SQLException ex) {
                    pw.println("Erro ao carregar histórico: " + ex.getMessage());
                }
                pw.println("==========================================================================");
                pw.println("                     FIM DO RELATÓRIO - COFFEE & BOOKS                    ");
                pw.println("==========================================================================");
                
                JOptionPane.showMessageDialog(this, "Relatório financeiro exportado com sucesso em:\n" + dest.getAbsolutePath(), "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom rendering of the Bar Chart
    private class BarChartPanel extends JPanel {
        private List<String> categories = new ArrayList<>();
        private List<Double> values = new ArrayList<>();

        public void setData(List<String> cats, List<Double> vals) {
            this.categories = cats;
            this.values = vals;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 50;
            int chartW = w - (2 * padding);
            int chartH = h - (2 * padding) - 20;

            if (categories.isEmpty() || chartW <= 0 || chartH <= 0) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.setColor(Color.GRAY);
                FontMetrics fm = g2.getFontMetrics();
                String msg = "Sem dados de pagamentos.";
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                g2.dispose();
                return;
            }

            double maxVal = 0;
            for (double v : values) {
                if (v > maxVal) maxVal = v;
            }
            if (maxVal == 0) maxVal = 1.0;

            g2.setColor(new Color(0, 0, 0, 15));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                int gridY = padding + (chartH * i / 4);
                g2.drawLine(padding, gridY, padding + chartW, gridY);
                double valAtGrid = maxVal * (4 - i) / 4;
                g2.setColor(Color.GRAY);
                g2.drawString(String.format("R$ %.0f", valAtGrid), padding - 45, gridY + 4);
            }

            g2.setColor(UIConstants.COLOR_ACCENT());
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(padding, padding + chartH, padding + chartW, padding + chartH);
            g2.drawLine(padding, padding, padding, padding + chartH);

            int numBars = categories.size();
            int spacePerBar = chartW / numBars;
            int barW = spacePerBar / 2;
            int gap = spacePerBar / 4;

            for (int i = 0; i < numBars; i++) {
                String cat = categories.get(i);
                double val = values.get(i);

                int barX = padding + gap + (i * spacePerBar);
                double pct = val / maxVal;
                int barH = (int) (chartH * pct);
                int barY = padding + chartH - barH;

                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(barX + 3, barY + 3, barW, barH, 6, 6);

                GradientPaint gp = new GradientPaint(
                    barX, barY, UIConstants.COLOR_ACCENT(),
                    barX + barW, barY + barH, UIConstants.COLOR_SUCCESS
                );
                g2.setPaint(gp);
                g2.fillRoundRect(barX, barY, barW, barH, 6, 6);

                g2.setColor(UIConstants.COLOR_ACCENT().darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(barX, barY, barW, barH, 6, 6);

                g2.setColor(UIConstants.TEXT_COLOR());
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int labelX = barX + (barW - fm.stringWidth(cat)) / 2;
                g2.drawString(cat, labelX, padding + chartH + 18);

                String valStr = String.format("R$ %.0f", val);
                int valX = barX + (barW - fm.stringWidth(valStr)) / 2;
                g2.drawString(valStr, valX, barY - 8);
            }
            g2.dispose();
        }
    }

    // Premium Custom Line Trend Chart
    private class LineChartPanel extends JPanel {
        private List<String> categories = new ArrayList<>();
        private List<Double> values = new ArrayList<>();

        public void setData(List<String> cats, List<Double> vals) {
            this.categories = cats;
            this.values = vals;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 50;
            int chartW = w - (2 * padding);
            int chartH = h - (2 * padding) - 20;

            if (categories.isEmpty() || chartW <= 0 || chartH <= 0) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.setColor(Color.GRAY);
                FontMetrics fm = g2.getFontMetrics();
                String msg = "Sem faturamento cadastrado.";
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                g2.dispose();
                return;
            }

            double maxVal = 0;
            for (double v : values) {
                if (v > maxVal) maxVal = v;
            }
            if (maxVal == 0) maxVal = 100.0;

            g2.setColor(new Color(0, 0, 0, 15));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                int gridY = padding + (chartH * i / 4);
                g2.drawLine(padding, gridY, padding + chartW, gridY);
                double valAtGrid = maxVal * (4 - i) / 4;
                g2.setColor(Color.GRAY);
                g2.drawString(String.format("R$ %.0f", valAtGrid), padding - 45, gridY + 4);
            }

            g2.setColor(UIConstants.COLOR_ACCENT());
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(padding, padding + chartH, padding + chartW, padding + chartH);
            g2.drawLine(padding, padding, padding, padding + chartH);

            int numPoints = categories.size();
            int spaceBetween = chartW / (numPoints - 1);

            int[] xPoints = new int[numPoints];
            int[] yPoints = new int[numPoints];

            for (int i = 0; i < numPoints; i++) {
                xPoints[i] = padding + (i * spaceBetween);
                double pct = values.get(i) / maxVal;
                yPoints[i] = padding + chartH - (int) (chartH * pct);
            }

            // Area Gradient under the trend line
            int[] xFill = new int[numPoints + 2];
            int[] yFill = new int[numPoints + 2];
            System.arraycopy(xPoints, 0, xFill, 0, numPoints);
            System.arraycopy(yPoints, 0, yFill, 0, numPoints);
            xFill[numPoints] = xPoints[numPoints - 1];
            yFill[numPoints] = padding + chartH;
            xFill[numPoints + 1] = xPoints[0];
            yFill[numPoints + 1] = padding + chartH;

            GradientPaint areaGp = new GradientPaint(
                0, padding, new Color(46, 139, 87, 85),
                0, padding + chartH, new Color(46, 139, 87, 0)
            );
            g2.setPaint(areaGp);
            g2.fillPolygon(xFill, yFill, numPoints + 2);

            // Connect Points with line
            g2.setColor(UIConstants.COLOR_SUCCESS);
            g2.setStroke(new BasicStroke(3f));
            for (int i = 0; i < numPoints - 1; i++) {
                g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }

            // Draw point rings and values
            for (int i = 0; i < numPoints; i++) {
                int px = xPoints[i];
                int py = yPoints[i];
                double val = values.get(i);
                String cat = categories.get(i);

                g2.setColor(Color.WHITE);
                g2.fillOval(px - 5, py - 5, 10, 10);
                g2.setColor(UIConstants.COLOR_SUCCESS);
                g2.fillOval(px - 3, py - 3, 6, 6);

                g2.setColor(UIConstants.TEXT_COLOR());
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(cat, px - fm.stringWidth(cat) / 2, padding + chartH + 18);

                if (val > 0) {
                    String valStr = String.format("R$ %.0f", val);
                    g2.drawString(valStr, px - fm.stringWidth(valStr) / 2, py - 8);
                }
            }
            g2.dispose();
        }
    }
}
