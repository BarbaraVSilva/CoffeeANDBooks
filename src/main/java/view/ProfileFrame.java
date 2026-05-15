package view;

import javax.swing.*;
import java.awt.*;
import util.UIConstants;
import util.SessionManager;

public class ProfileFrame extends JFrame {
    public ProfileFrame() {
        setTitle("Coffee&Books - Perfil & Configurações");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Info
        JLabel lblUser = new JLabel("Usuário: " + SessionManager.getUsuario().getUsername());
        lblUser.setFont(UIConstants.FONT_TITLE);
        lblUser.setForeground(UIConstants.COLOR_ACCENT());
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(lblUser, gbc);

        JLabel lblRole = new JLabel("Perfil: " + SessionManager.getUsuario().getRole());
        lblRole.setFont(UIConstants.FONT_LABEL);
        lblRole.setForeground(UIConstants.TEXT_COLOR());
        gbc.gridy = 1;
        mainPanel.add(lblRole, gbc);

        // Theme Toggle
        JLabel lblTheme = new JLabel("Tema do Sistema:");
        gbc.gridy = 2;
        mainPanel.add(lblTheme, gbc);

        JRadioButton rbLight = new JRadioButton("Light Mode", !UIConstants.isDarkTheme());
        JRadioButton rbDark = new JRadioButton("Dark Mode", UIConstants.isDarkTheme());
        ButtonGroup group = new ButtonGroup();
        group.add(rbLight); group.add(rbDark);
        
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setOpaque(false);
        themePanel.add(rbLight); themePanel.add(rbDark);
        gbc.gridy = 3;
        mainPanel.add(themePanel, gbc);

        JButton btnApply = new JButton("Aplicar Mudanças");
        btnApply.setBackground(UIConstants.COLOR_ACCENT());
        btnApply.setForeground(Color.WHITE);
        gbc.gridy = 4;
        mainPanel.add(btnApply, gbc);

        add(mainPanel, BorderLayout.CENTER);

        btnApply.addActionListener(e -> {
            UIConstants.setDarkTheme(rbDark.isSelected());
            JOptionPane.showMessageDialog(this, "Tema alterado! Reinicie as janelas para aplicar totalmente.");
            dispose();
        });
    }
}
