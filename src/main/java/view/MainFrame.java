package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import util.UIConstants;
import util.SessionManager;

public class MainFrame extends JFrame {
    private JLabel lblStatus;

    public MainFrame() {
        setTitle("Coffee&Books - Sistema de Cafeteria e Sebo v1.0");
        setSize(1150, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Security Check
        if (SessionManager.getUsuario() == null) {
            JOptionPane.showMessageDialog(null, "Sessão inválida. Por favor, faça login.");
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }

        // Sidebar Panel (WEST)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.COLOR_ACCENT());
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));

        // Sidebar Header Logo/Title
        JLabel lblSidebarLogo = new JLabel(util.AssetLoader.getLogo(64, 64));
        lblSidebarLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblSidebarTitle = new JLabel("Coffee&Books");
        lblSidebarTitle.setFont(UIConstants.FONT_TITLE.deriveFont(20f));
        lblSidebarTitle.setForeground(Color.WHITE);
        lblSidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(lblSidebarLogo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblSidebarTitle);
        sidebar.add(Box.createVerticalStrut(20));

        // CATEGORY 1: Cadastros
        addSectionHeader(sidebar, "📋 CADASTROS");
        addNavButton(sidebar, "📚 Acervo de Livros", e -> new LivroForm().setVisible(true));
        
        if (SessionManager.isAdmin()) {
            addNavButton(sidebar, "🏷️ Gêneros Literários", e -> new GeneroForm().setVisible(true));
        }

        addNavButton(sidebar, "☕ Comidas e Bebidas", e -> new ProdutoConsumoForm().setVisible(true));
        addNavButton(sidebar, "📦 Insumos de Cafeteria", e -> new IngredienteForm().setVisible(true));
        addNavButton(sidebar, "👥 Clientes e Fidelidade", e -> new ClienteForm().setVisible(true));
        addNavButton(sidebar, "📥 Importação de CSV", e -> new ImportacaoDadosFrame().setVisible(true));
        
        sidebar.add(Box.createVerticalStrut(12));

        // CATEGORY 2: Operações
        addSectionHeader(sidebar, "🛋️ OPERAÇÕES");
        addNavButton(sidebar, "📅 Reservas de Poltronas", e -> new ReservaForm().setVisible(true));
        addNavButton(sidebar, "📝 Comandas e Mesas", e -> new ComandaFrame().setVisible(true));
        addNavButton(sidebar, "🛒 Frente de Caixa (PDV)", e -> new PDVFrame().setVisible(true));
        addNavButton(sidebar, "🎉 Gestão de Eventos", e -> new EventoFrame().setVisible(true));
        addNavButton(sidebar, "🔍 Busca Avançada", e -> new ConsultaAcervoFrame().setVisible(true));

        sidebar.add(Box.createVerticalStrut(12));

        // CATEGORY 3: Gestão & Financeiro
        addSectionHeader(sidebar, "📊 RELATÓRIOS & SISTEMA");
        if (SessionManager.isAdmin()) {
            addNavButton(sidebar, "📈 Painel Financeiro", e -> new FinancialDashboardFrame().setVisible(true));
        }

        sidebar.add(Box.createVerticalGlue());

        // Meu Perfil & Sair
        addNavButton(sidebar, "⚙️ Meu Perfil", e -> new ProfileFrame().setVisible(true));
        addNavButton(sidebar, "🚪 Sair do Sistema", e -> {
            SessionManager.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(sidebar, BorderLayout.WEST);

        // Main Dashboard Panel (CENTER)
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(UIConstants.COLOR_PRIMARY());
        dashboardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Query dynamic counts for a living dashboard
        int totalLivros = 0;
        int totalClientes = 0;
        int totalProdutos = 0;
        int lowStockCount = 0;
        double totalVendasHoje = 0;
        
        try {
            List<model.Livro> livrosList = new dao.LivroDAO().listar(null, "Todos");
            totalLivros = livrosList.size();
            for (model.Livro l : livrosList) {
                if (l.getEstoqueAtual() <= 2) {
                    lowStockCount++;
                }
            }
            
            totalClientes = new dao.ClienteDAO().listar().size();
            totalProdutos = new dao.ProdutoConsumoDAO().listar().size();
            
            // Query today's consolidated faturamento
            try (java.sql.Connection conn = util.DatabaseUtil.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT SUM(valor_total) FROM VENDA_CONSOLIDADA WHERE DATE(data_venda) = CURRENT_DATE")) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalVendasHoje = rs.getDouble(1);
                    }
                }
            }
        } catch (Exception ex) {
            // DB might be unitialized or offline, fallback to 0
        }

        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.setOpaque(false);
        
        JLabel lblDashboardTitle = new JLabel("☕ Visão Geral e Atalhos Rápidos");
        lblDashboardTitle.setFont(UIConstants.FONT_TITLE);
        lblDashboardTitle.setForeground(UIConstants.COLOR_ACCENT());
        northPanel.add(lblDashboardTitle, BorderLayout.WEST);
        
        if (lowStockCount > 0) {
            JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
            banner.setBackground(UIConstants.COLOR_ALERT);
            banner.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JLabel lblWarn = new JLabel("⚠️ ESTOQUE CRÍTICO: " + lowStockCount + " LIVROS COM POUCAS UNIDADES! CLIQUE AQUI.");
            lblWarn.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblWarn.setForeground(Color.WHITE);
            banner.add(lblWarn);
            
            banner.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    new ConsultaAcervoFrame().setVisible(true);
                }
            });
            northPanel.add(banner, BorderLayout.EAST);
        }
        dashboardPanel.add(northPanel, BorderLayout.NORTH);

        // Clickable Dashboard Cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(UIConstants.COLOR_PRIMARY());
        cardsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        cardsPanel.add(createDashboardCard("📚 " + totalLivros + " Livros no Acervo", "Clique para gerenciar o acervo de obras", new Color(139, 69, 19), new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LivroForm().setVisible(true);
            }
        }));
        cardsPanel.add(createDashboardCard("👥 " + totalClientes + " Clientes Cadastrados", "Programa de Fidelidade e Pontos", new Color(46, 139, 87), new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ClienteForm().setVisible(true);
            }
        }));
        cardsPanel.add(createDashboardCard("☕ " + totalProdutos + " Itens no Cardápio", "Comidas, bebidas e disponibilidade", new Color(205, 133, 63), new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ProdutoConsumoForm().setVisible(true);
            }
        }));
        cardsPanel.add(createDashboardCard("🛒 R$ " + String.format("%.2f", totalVendasHoje) + " em Vendas Hoje", "Faturamento do caixa neste dia", new Color(70, 130, 180), new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new PDVFrame().setVisible(true);
            }
        }));

        dashboardPanel.add(cardsPanel, BorderLayout.CENTER);
        add(dashboardPanel, BorderLayout.CENTER);

        // Footer (SOUTH)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UIConstants.COLOR_SECONDARY());
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.COLOR_ACCENT()));
        
        lblStatus = new JLabel();
        lblStatus.setFont(UIConstants.FONT_FOOTER);
        lblStatus.setForeground(UIConstants.TEXT_COLOR());
        footer.add(lblStatus, BorderLayout.WEST);
        
        add(footer, BorderLayout.SOUTH);

        // Timer to update session time and status
        Timer timer = new Timer(1000, e -> updateFooter());
        timer.start();
        updateFooter();
    }

    private void addSectionHeader(JPanel panel, String text) {
        JLabel lblHeader = new JLabel(text);
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblHeader.setForeground(new Color(230, 200, 180));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5));
        panel.add(lblHeader);
    }

    private void addNavButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(UIConstants.COLOR_ACCENT().darker());
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.COLOR_ACCENT());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIConstants.COLOR_ACCENT().darker());
            }
        });
        
        btn.addActionListener(action);
        panel.add(btn);
        panel.add(Box.createVerticalStrut(4));
    }

    private JPanel createDashboardCard(String title, String subtitle, Color color, java.awt.event.MouseAdapter clickAction) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.COLOR_SECONDARY());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_TITLE.deriveFont(22f));
        lblTitle.setForeground(color);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(UIConstants.FONT_LABEL);
        lblSubtitle.setForeground(UIConstants.TEXT_COLOR().darker());
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblSubtitle);
        card.add(Box.createVerticalGlue());

        // Mouse hover effects
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(UIConstants.COLOR_PRIMARY());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(UIConstants.COLOR_SECONDARY());
            }
        });
        
        card.addMouseListener(clickAction);

        return card;
    }

    private void updateFooter() {
        if (SessionManager.getUsuario() == null) return;
        String user = SessionManager.getUsuario().getUsername();
        String role = SessionManager.getUsuario().getRole();
        String time = SessionManager.getTempoSessao();
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        
        lblStatus.setText(String.format(" Usuário: %s [%s] | Sessão: %s | Data: %s", 
            user, role, time, date));
    }

    public static void main(String[] args) {
        UIConstants.initLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
