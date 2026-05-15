package dao;

import model.VendaConsolidada;
import model.ItemVendaGeral;
import util.DatabaseUtil;
import java.sql.*;

import exception.EstoqueInsuficienteException;

public class VendaDAO {
    public void salvar(VendaConsolidada v) throws EstoqueInsuficienteException {
        String sqlVenda = "INSERT INTO VENDA_CONSOLIDADA (valor_total, forma_pagamento, numero_mesa) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO ITEM_VENDA_GERAL (quantidade, preco_applied, fk_venda, fk_livro, fk_produto) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtV = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtV.setDouble(1, v.getValorTotal());
                stmtV.setString(2, v.getFormaPagamento());
                if (v.getNumeroMesa() != null) stmtV.setInt(3, v.getNumeroMesa());
                else stmtV.setNull(3, Types.INTEGER);
                
                stmtV.executeUpdate();
                
                try (ResultSet rs = stmtV.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idVenda = rs.getInt(1);
                        try (PreparedStatement stmtI = conn.prepareStatement(sqlItem)) {
                            for (ItemVendaGeral item : v.getItens()) {
                                stmtI.setInt(1, item.getQuantidade());
                                stmtI.setDouble(2, item.getPrecoApplied());
                                stmtI.setInt(3, idVenda);
                                if (item.getLivro() != null) {
                                    stmtI.setInt(4, item.getLivro().getIdLivro());
                                    // Decrement Stock
                                    try (PreparedStatement stmtS = conn.prepareStatement("UPDATE LIVRO SET estoque_atual = estoque_atual - ? WHERE id_livro = ?")) {
                                        stmtS.setInt(1, item.getQuantidade());
                                        stmtS.setInt(2, item.getLivro().getIdLivro());
                                        stmtS.executeUpdate();
                                    }
                                } else {
                                    stmtI.setNull(4, Types.INTEGER);
                                }
                                if (item.getProduto() != null) stmtI.setInt(5, item.getProduto().getIdProduto());
                                else stmtI.setNull(5, Types.INTEGER);
                                stmtI.addBatch();
                            }
                            stmtI.executeBatch();
                        }
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
