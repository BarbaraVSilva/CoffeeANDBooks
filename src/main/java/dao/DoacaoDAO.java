package dao;

import model.Doacao;
import model.Livro;
import model.GeneroLivro;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoacaoDAO {
    public void salvar(Doacao d) {
        String sql = "INSERT INTO DOACAO (nome_doador, data_doacao, fk_livro) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, d.getNomeDoador());
            if (d.getDataDoacao() != null) {
                stmt.setDate(2, new java.sql.Date(d.getDataDoacao().getTime()));
            } else {
                stmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            }
            stmt.setInt(3, d.getLivro().getIdLivro());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setIdDoacao(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Doacao> listar() {
        return listar("");
    }

    public List<Doacao> listar(String filtro) {
        List<Doacao> lista = new ArrayList<>();
        String sql = "SELECT d.*, l.titulo, l.autor, l.condicao_livro, l.preco_venda, l.estoque_atual, l.image_path, l.fk_genero, g.nome_genero, g.localizacao_estante " +
                     "FROM DOACAO d " +
                     "JOIN LIVRO l ON d.fk_livro = l.id_livro " +
                     "JOIN GENERO_LIVRO g ON l.fk_genero = g.id_genero " +
                     "WHERE d.nome_doador LIKE ? OR l.titulo LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeVal = "%" + filtro + "%";
            stmt.setString(1, likeVal);
            stmt.setString(2, likeVal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GeneroLivro g = new GeneroLivro(
                        rs.getInt("fk_genero"),
                        rs.getString("nome_genero"),
                        rs.getString("localizacao_estante")
                    );
                    Livro l = new Livro(
                        rs.getInt("fk_livro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("condicao_livro"),
                        rs.getDouble("preco_venda"),
                        rs.getInt("estoque_atual"),
                        g,
                        rs.getString("image_path")
                    );
                    lista.add(new Doacao(
                        rs.getInt("id_doacao"),
                        rs.getString("nome_doador"),
                        rs.getDate("data_doacao"),
                        l
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizar(Doacao d) {
        String sql = "UPDATE DOACAO SET nome_doador = ?, data_doacao = ?, fk_livro = ? WHERE id_doacao = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, d.getNomeDoador());
            if (d.getDataDoacao() != null) {
                stmt.setDate(2, new java.sql.Date(d.getDataDoacao().getTime()));
            } else {
                stmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            }
            stmt.setInt(3, d.getLivro().getIdLivro());
            stmt.setInt(4, d.getIdDoacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM DOACAO WHERE id_doacao = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
