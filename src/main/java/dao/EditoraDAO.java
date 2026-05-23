package dao;

import model.Editora;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditoraDAO {
    public void salvar(Editora ed) {
        String sql = "INSERT INTO EDITORA (nome_editora, cidade) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ed.getNomeEditora());
            stmt.setString(2, ed.getCidade());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ed.setIdEditora(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Editora> listar() {
        return listar("");
    }

    public List<Editora> listar(String filtro) {
        List<Editora> lista = new ArrayList<>();
        String sql = "SELECT * FROM EDITORA WHERE nome_editora LIKE ? OR cidade LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeVal = "%" + filtro + "%";
            stmt.setString(1, likeVal);
            stmt.setString(2, likeVal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Editora(
                        rs.getInt("id_editora"),
                        rs.getString("nome_editora"),
                        rs.getString("cidade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizar(Editora ed) {
        String sql = "UPDATE EDITORA SET nome_editora = ?, cidade = ? WHERE id_editora = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ed.getNomeEditora());
            stmt.setString(2, ed.getCidade());
            stmt.setInt(3, ed.getIdEditora());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM EDITORA WHERE id_editora = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
