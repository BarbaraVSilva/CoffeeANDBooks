package view;

import javax.swing.*;
import java.awt.*;
import dao.UsuarioDAO;
import model.Usuario;
import util.UIConstants;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        setTitle("Coffee&Books - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.COLOR_SECONDARY());
        panel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel(" Acesso ao Sistema", SwingConstants.CENTER);
        lblTitle.setIcon(util.AssetLoader.getLogo(64, 64));
        lblTitle.setFont(UIConstants.FONT_TITLE);
        lblTitle.setForeground(UIConstants.COLOR_ACCENT());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; panel.add(new JLabel("Usuário:"), gbc);
        txtUser = new JTextField(15); gbc.gridx = 1; panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Senha:"), gbc);
        txtPass = new JPasswordField(15); gbc.gridx = 1; panel.add(txtPass, gbc);

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setBackground(UIConstants.COLOR_ACCENT());
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(UIConstants.FONT_BUTTON);
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        add(panel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario user = dao.login(txtUser.getText(), new String(txtPass.getPassword()));
            if (user != null) {
                util.SessionManager.setUsuario(user);
                if (user.isSenhaExpirada()) {
                    new ChangePasswordFrame().setVisible(true);
                } else {
                    new MainFrame().setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        UIConstants.initLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
