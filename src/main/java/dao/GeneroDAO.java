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
        List<GeneroLivro> lista = new ArrayList<>();
        String sql = "SELECT * FROM GENERO_LIVRO";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new GeneroLivro(rs.getInt("id_genero"), 
                    rs.getString("nome_genero"), rs.getString("localizacao_estante")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
