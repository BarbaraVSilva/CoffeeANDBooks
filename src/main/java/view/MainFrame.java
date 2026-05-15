package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.UIConstants;
import util.SessionManager;

public class MainFrame extends JFrame {
    private JLabel lblStatus;

    public MainFrame() {
        setTitle("Coffee&Books - Sistema de Cafeteria e Sebo v1.0");
        setSize(1000, 700);
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

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UIConstants.COLOR_ACCENT());
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JMenu menuHome = new JMenu(" Coffee&Books");
        menuHome.setIcon(util.AssetLoader.getLogo(24, 24));
        menuHome.setForeground(Color.WHITE);
        menuHome.setFont(UIConstants.FONT_LABEL);

        JMenu menuCadastros = new JMenu(" Cadastros");
        menuCadastros.setIcon(util.VisualDetails.getBookIcon(20, Color.WHITE));
        menuCadastros.setForeground(Color.WHITE);
        
        JMenuItem itemGenero = new JMenuItem("Gêneros Literários");
        itemGenero.setEnabled(SessionManager.isAdmin()); // ADMIN ONLY
        itemGenero.addActionListener(e -> new GeneroForm().setVisible(true));
        
        JMenuItem itemLivro = new JMenuItem("Acervo de Livros");
        itemLivro.addActionListener(e -> new LivroForm().setVisible(true));
        
        menuCadastros.add(itemGenero);
        menuCadastros.add(itemLivro);

        JMenu menuEspaco = new JMenu("🛋️ Espaço Cultural");
        menuEspaco.setForeground(Color.WHITE);
        JMenuItem itemReserva = new JMenuItem("Reservas de Poltronas");
        JMenuItem itemEventos = new JMenuItem("Gestão de Eventos");
        itemReserva.addActionListener(e -> new ReservaForm().setVisible(true));
        itemEventos.addActionListener(e -> new EventoFrame().setVisible(true));
        menuEspaco.add(itemReserva);
        menuEspaco.add(itemEventos);

        JMenu menuConsultas = new JMenu("🔍 Consultas");
        menuConsultas.setForeground(Color.WHITE);
        JMenuItem itemBusca = new JMenuItem("Busca Avançada");
        itemBusca.addActionListener(e -> new ConsultaAcervoFrame().setVisible(true));
        menuConsultas.add(itemBusca);

        JMenu menuFinanceiro = new JMenu("💰 Financeiro");
        menuFinanceiro.setForeground(Color.WHITE);
        JMenuItem itemPDV = new JMenuItem("Frente de Caixa (PDV)");
        JMenuItem itemDashboard = new JMenuItem("Dashboard Financeiro");
        itemDashboard.setEnabled(SessionManager.isAdmin()); // ADMIN ONLY
        
        JMenuItem itemComandas = new JMenuItem("Gestor de Comandas (Inovação)");
        itemComandas.addActionListener(e -> new ComandaFrame().setVisible(true));
        
        itemPDV.addActionListener(e -> new PDVFrame().setVisible(true));
        itemDashboard.addActionListener(e -> new FinancialDashboardFrame().setVisible(true));
        menuFinanceiro.add(itemPDV);
        menuFinanceiro.add(itemComandas);
        menuFinanceiro.add(itemDashboard);

        JMenu menuSair = new JMenu("🚪 Sistema");
        menuSair.setForeground(Color.WHITE);
        JMenuItem itemProfile = new JMenuItem("Meu Perfil & Tema");
        JMenuItem itemLogout = new JMenuItem("Logout");
        
        itemProfile.addActionListener(e -> new ProfileFrame().setVisible(true));
        itemLogout.addActionListener(e -> {
            SessionManager.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });
        menuSair.add(itemProfile);
        menuSair.add(itemLogout);

        menuBar.add(menuHome);
        menuBar.add(Box.createHorizontalStrut(20));
        menuBar.add(menuCadastros);
        menuBar.add(menuEspaco);
        menuBar.add(menuConsultas);
        menuBar.add(menuFinanceiro);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuSair);
        setJMenuBar(menuBar);

        // Main Content (Background Simulation)
        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Background image logic would go here if file is accessible via classpath
            }
        };
        contentPanel.setOpaque(false);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(util.AssetLoader.getLogo(250, 250));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("Coffee & Books");
        lblTitle.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 48));
        lblTitle.setForeground(UIConstants.COLOR_ACCENT());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(lblLogo);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(lblTitle);
        centerPanel.add(Box.createVerticalGlue());

        contentPanel.add(centerPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Footer
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

    private void updateFooter() {
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
