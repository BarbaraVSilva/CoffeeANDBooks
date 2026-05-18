package dao;

import model.GeneroLivro;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAO {
    public void salvar(GeneroLivro g) {
        String sql = "INSERT INTO GENERO_LIVRO (nome_genero, localizacao_estante) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, g.getNomeGenero());
            stmt.setString(2, g.getLocalizacaoEstante());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<GeneroLivro> listar() {
        return listar("");
    }

    public List<GeneroLivro> listar(String filtro) {
        List<GeneroLivro> lista = new ArrayList<>();
        String sql = "SELECT * FROM GENERO_LIVRO WHERE nome_genero LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new GeneroLivro(rs.getInt("id_genero"), 
                        rs.getString("nome_genero"), rs.getString("localizacao_estante")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public void atualizar(GeneroLivro g) {
        String sql = "UPDATE GENERO_LIVRO SET nome_genero = ?, localizacao_estante = ? WHERE id_genero = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, g.getNomeGenero());
            stmt.setString(2, g.getLocalizacaoEstante());
            stmt.setInt(3, g.getIdGenero());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM GENERO_LIVRO WHERE id_genero = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
