package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import util.UIConstants;
import util.SessionManager;

public class ProfileFrame extends JFrame {
    public ProfileFrame() {
        setTitle("Coffee&Books - Perfil do Colaborador");
        setSize(480, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header Panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw elegant background gradient for header
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.COLOR_ACCENT(), getWidth(), getHeight(), UIConstants.COLOR_SECONDARY());
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 160));
        headerPanel.setLayout(new GridBagLayout());
        
        // Circular Avatar Component
        JPanel avatarComponent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new Ellipse2D.Double(2, 2, 86, 86));
                
                // Draw main circle gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xE0, 0xD0, 0xC0), 90, 90, new Color(0x8B, 0x5A, 0x2B));
                g2.setPaint(gp);
                g2.fill(new Ellipse2D.Double(0, 0, 88, 88));
                
                // Draw initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                String name = SessionManager.getUsuario() != null ? SessionManager.getUsuario().getUsername().toUpperCase() : "CB";
                String initial = name.length() > 0 ? String.valueOf(name.charAt(0)) : "U";
                FontMetrics fm = g2.getFontMetrics();
                int x = (88 - fm.stringWidth(initial)) / 2;
                int y = ((88 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                
                g2.dispose();
            }
        };
        avatarComponent.setPreferredSize(new Dimension(88, 88));
        avatarComponent.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        headerPanel.add(avatarComponent, gbc);
        
        add(headerPanel, BorderLayout.NORTH);

        // Body Content Panel
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints bGbc = new GridBagConstraints();
        bGbc.fill = GridBagConstraints.HORIZONTAL;
        bGbc.insets = new Insets(8, 8, 8, 8);
        bGbc.gridx = 0;
        
        // Title
        JLabel lblTitle = new JLabel("Configurações do Perfil", SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(UIConstants.COLOR_ACCENT());
        bGbc.gridy = 0;
        bodyPanel.add(lblTitle, bGbc);

        // Separator
        JSeparator sep = new JSeparator();
        bGbc.gridy = 1;
        bodyPanel.add(sep, bGbc);

        // Details Panel (Card style)
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        detailsPanel.setBackground(UIConstants.COLOR_SECONDARY());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            UIConstants.getRoundedBorder(UIConstants.COLOR_ACCENT()),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        detailsPanel.add(createLabel("Nome de Usuário:"));
        detailsPanel.add(createValLabel(SessionManager.getUsuario() != null ? SessionManager.getUsuario().getUsername() : "Convidado"));
        
        detailsPanel.add(createLabel("Nível de Acesso:"));
        detailsPanel.add(createValLabel(SessionManager.getUsuario() != null ? SessionManager.getUsuario().getRole() : "N/A"));
        
        detailsPanel.add(createLabel("Status da Sessão:"));
        JLabel lblStatus = new JLabel("🟢 Conectado", SwingConstants.LEFT);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblStatus.setForeground(UIConstants.COLOR_SUCCESS);
        detailsPanel.add(lblStatus);
        
        detailsPanel.add(createLabel("Banco de Dados:"));
        detailsPanel.add(createValLabel("Online (MySQL)"));
        
        bGbc.gridy = 2;
        bodyPanel.add(detailsPanel, bGbc);

        // System Settings Panel (Theme selection)
        JPanel settingsPanel = new JPanel(new BorderLayout(10, 10));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Preferências Visuais"));
        
        JRadioButton rbLight = new JRadioButton("Tema Claro (Sépia)", !UIConstants.isDarkTheme());
        JRadioButton rbDark = new JRadioButton("Tema Escuro (Café)", UIConstants.isDarkTheme());
        rbLight.setOpaque(false); rbDark.setOpaque(false);
        
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(rbLight); themeGroup.add(rbDark);
        
        JPanel rbPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        rbPanel.setOpaque(false);
        rbPanel.add(rbLight); rbPanel.add(rbDark);
        settingsPanel.add(rbPanel, BorderLayout.CENTER);
        
        bGbc.gridy = 3;
        bodyPanel.add(settingsPanel, bGbc);

        // Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        actionsPanel.setOpaque(false);
        
        JButton btnApply = new JButton("Aplicar Tema");
        btnApply.setBackground(UIConstants.COLOR_ACCENT());
        btnApply.setForeground(Color.WHITE);
        btnApply.setFont(UIConstants.FONT_BUTTON);
        btnApply.addActionListener(e -> {
            UIConstants.setDarkTheme(rbDark.isSelected());
            JOptionPane.showMessageDialog(this, "Tema aplicado com sucesso!");
            dispose();
        });
        
        JButton btnChangePassword = new JButton("🔒 Redefinir Senha");
        btnChangePassword.setBackground(UIConstants.COLOR_ALERT);
        btnChangePassword.setForeground(Color.WHITE);
        btnChangePassword.setFont(UIConstants.FONT_BUTTON);
        btnChangePassword.addActionListener(e -> {
            ChangePasswordFrame cpf = new ChangePasswordFrame();
            cpf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't force exit
            cpf.setVisible(true);
        });

        actionsPanel.add(btnApply);
        actionsPanel.add(btnChangePassword);
        
        bGbc.gridy = 4;
        bodyPanel.add(actionsPanel, bGbc);

        add(bodyPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(UIConstants.TEXT_COLOR().darker());
        return lbl;
    }

    private JLabel createValLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(UIConstants.COLOR_ACCENT());
        return lbl;
    }
}
