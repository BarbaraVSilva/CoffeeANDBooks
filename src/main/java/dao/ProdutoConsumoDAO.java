package dao;

import model.ProdutoConsumo;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoConsumoDAO {
    public List<ProdutoConsumo> listar() {
        List<ProdutoConsumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUTO_CONSUMO WHERE disponivel = TRUE";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new ProdutoConsumo(
                    rs.getInt("id_produto"),
                    rs.getString("nome_alimento"),
                    rs.getDouble("preco_unitario"),
                    rs.getString("categoria_cardapio"),
                    rs.getBoolean("disponivel"),
                    rs.getString("image_path")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<ProdutoConsumo> listarTodos(String filtro) {
        List<ProdutoConsumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM PRODUTO_CONSUMO WHERE nome_alimento LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + filtro + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ProdutoConsumo(
                        rs.getInt("id_produto"),
                        rs.getString("nome_alimento"),
                        rs.getDouble("preco_unitario"),
                        rs.getString("categoria_cardapio"),
                        rs.getBoolean("disponivel"),
                        rs.getString("image_path")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public void salvar(ProdutoConsumo p) {
        String sql = "INSERT INTO PRODUTO_CONSUMO (nome_alimento, preco_unitario, categoria_cardapio, disponivel, image_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNomeAlimento());
            stmt.setDouble(2, p.getPrecoUnitario());
            stmt.setString(3, p.getCategoriaCardapio());
            stmt.setBoolean(4, p.isDisponivel());
            stmt.setString(5, p.getImagePath());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void atualizar(ProdutoConsumo p) {
        String sql = "UPDATE PRODUTO_CONSUMO SET nome_alimento = ?, preco_unitario = ?, categoria_cardapio = ?, disponivel = ?, image_path = ? WHERE id_produto = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNomeAlimento());
            stmt.setDouble(2, p.getPrecoUnitario());
            stmt.setString(3, p.getCategoriaCardapio());
            stmt.setBoolean(4, p.isDisponivel());
            stmt.setString(5, p.getImagePath());
            stmt.setInt(6, p.getIdProduto());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM PRODUTO_CONSUMO WHERE id_produto = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
