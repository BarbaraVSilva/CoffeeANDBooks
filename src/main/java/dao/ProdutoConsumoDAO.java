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
                    rs.getBoolean("disponivel")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
