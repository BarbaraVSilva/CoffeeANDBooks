package view;

import javax.swing.*;
import java.awt.*;
import dao.UsuarioDAO;
import util.SessionManager;
import util.UIConstants;

public class ChangePasswordFrame extends JFrame {
    private JPasswordField txtNovaSenha, txtConfirmar;

    public ChangePasswordFrame() {
        setTitle("Coffee&Books - Alterar Senha Obrigatória");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Force change
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.COLOR_SECONDARY());
        panel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblMsg = new JLabel("<html><center>Sua senha expirou (90 dias).<br>Por favor, defina uma nova senha.</center></html>", SwingConstants.CENTER);
        lblMsg.setForeground(UIConstants.COLOR_ALERT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblMsg, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; panel.add(new JLabel("Nova Senha:"), gbc);
        txtNovaSenha = new JPasswordField(15); gbc.gridx = 1; panel.add(txtNovaSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Confirmar:"), gbc);
        txtConfirmar = new JPasswordField(15); gbc.gridx = 1; panel.add(txtConfirmar, gbc);

        JButton btnSalvar = new JButton("Atualizar e Sair");
        btnSalvar.setBackground(UIConstants.COLOR_ACCENT());
        btnSalvar.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnSalvar, gbc);

        add(panel, BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> {
            String senha = new String(txtNovaSenha.getPassword());
            String confirma = new String(txtConfirmar.getPassword());
            
            if (senha.isEmpty() || senha.length() < 4) {
                JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 4 caracteres.");
                return;
            }
            
            if (!senha.equals(confirma)) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem.");
                return;
            }

            UsuarioDAO dao = new UsuarioDAO();
            if (dao.alterarSenha(SessionManager.getUsuario().getId(), senha)) {
                JOptionPane.showMessageDialog(this, "Senha alterada com sucesso! Faça login novamente.");
                SessionManager.logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }
}
