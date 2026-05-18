package dao;

import model.Usuario;
import util.DatabaseUtil;
import java.sql.*;

public class UsuarioDAO {
    public Usuario login(String username, String password) {
        String sql = "SELECT * FROM USUARIO WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, util.SecurityUtil.hashPassword(password));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getDate("data_ultima_senha")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean alterarSenha(int idUsuario, String novaSenha) {
        String sql = "UPDATE USUARIO SET password = ?, data_ultima_senha = CURRENT_DATE WHERE id_usuario = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, util.SecurityUtil.hashPassword(novaSenha));
            stmt.setInt(2, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
