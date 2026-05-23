package dao;

import model.Fornecedor;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {
    public void salvar(Fornecedor f) {
        String sql = "INSERT INTO FORNECEDOR (nome_fantasia, cnpj, contato, tipo_produto) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, f.getNomeFantasia());
            stmt.setString(2, f.getCnpj());
            stmt.setString(3, f.getContato());
            stmt.setString(4, f.getTipoProduto());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setIdFornecedor(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Fornecedor> listar() {
        return listar("");
    }

    public List<Fornecedor> listar(String filtro) {
        List<Fornecedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM FORNECEDOR WHERE nome_fantasia LIKE ? OR tipo_produto LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeVal = "%" + filtro + "%";
            stmt.setString(1, likeVal);
            stmt.setString(2, likeVal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Fornecedor(
                        rs.getInt("id_fornecedor"),
                        rs.getString("nome_fantasia"),
                        rs.getString("cnpj"),
                        rs.getString("contato"),
                        rs.getString("tipo_produto")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizar(Fornecedor f) {
        String sql = "UPDATE FORNECEDOR SET nome_fantasia = ?, cnpj = ?, contato = ?, tipo_produto = ? WHERE id_fornecedor = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNomeFantasia());
            stmt.setString(2, f.getCnpj());
            stmt.setString(3, f.getContato());
            stmt.setString(4, f.getTipoProduto());
            stmt.setInt(5, f.getIdFornecedor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM FORNECEDOR WHERE id_fornecedor = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
