package dao;

import model.Livro;
import model.GeneroLivro;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    public void salvar(Livro l) {
        String sql = "INSERT INTO LIVRO (titulo, autor, condicao_livro, preco_venda, estoque_atual, fk_genero) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, l.getTitulo());
            stmt.setString(2, l.getAutor());
            stmt.setString(3, l.getCondicaoLivro());
            stmt.setDouble(4, l.getPrecoVenda());
            stmt.setInt(5, l.getEstoqueAtual());
            stmt.setInt(6, l.getGenero().getIdGenero());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Livro> listar(String filtro, String condicao) {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT l.*, g.nome_genero, g.localizacao_estante FROM LIVRO l " +
                     "JOIN GENERO_LIVRO g ON l.fk_genero = g.id_genero WHERE 1=1";
        
        if (filtro != null && !filtro.isEmpty()) sql += " AND (titulo LIKE ? OR autor LIKE ?)";
        if (condicao != null && !condicao.equals("Todos")) sql += " AND condicao_livro = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int idx = 1;
            if (filtro != null && !filtro.isEmpty()) {
                stmt.setString(idx++, "%" + filtro + "%");
                stmt.setString(idx++, "%" + filtro + "%");
            }
            if (condicao != null && !condicao.equals("Todos")) {
                stmt.setString(idx++, condicao);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GeneroLivro g = new GeneroLivro(rs.getInt("fk_genero"), 
                        rs.getString("nome_genero"), rs.getString("localizacao_estante"));
                    lista.add(new Livro(rs.getInt("id_livro"), rs.getString("titulo"), 
                        rs.getString("autor"), rs.getString("condicao_livro"), 
                        rs.getDouble("preco_venda"), rs.getInt("estoque_atual"), g));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}
