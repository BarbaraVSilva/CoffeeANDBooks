package dao;

import model.Ingrediente;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {
    public void inserir(Ingrediente ing) throws SQLException {
        String sql = "INSERT INTO INGREDIENTE (nome_ingrediente, quantidade_atual, unidade_medida) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ing.getNomeIngrediente());
            stmt.setDouble(2, ing.getQuantidadeAtual());
            stmt.setString(3, ing.getUnidadeMedida());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ing.setIdIngrediente(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Ingrediente ing) throws SQLException {
        String sql = "UPDATE INGREDIENTE SET nome_ingrediente = ?, quantidade_atual = ?, unidade_medida = ? WHERE id_ingrediente = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ing.getNomeIngrediente());
            stmt.setDouble(2, ing.getQuantidadeAtual());
            stmt.setString(3, ing.getUnidadeMedida());
            stmt.setInt(4, ing.getIdIngrediente());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idIngrediente) throws SQLException {
        String sql = "DELETE FROM INGREDIENTE WHERE id_ingrediente = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idIngrediente);
            stmt.executeUpdate();
        }
    }

    public List<Ingrediente> listar(String filter) {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT * FROM INGREDIENTE WHERE nome_ingrediente LIKE ? ORDER BY nome_ingrediente ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + (filter == null ? "" : filter) + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Ingrediente(
                        rs.getInt("id_ingrediente"),
                        rs.getString("nome_ingrediente"),
                        rs.getDouble("quantidade_atual"),
                        rs.getString("unidade_medida")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
