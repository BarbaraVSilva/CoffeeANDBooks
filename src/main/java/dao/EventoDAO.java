package dao;

import model.Evento;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    public void inserir(Evento e) throws SQLException {
        String sql = "INSERT INTO EVENTO (nome_evento, data_evento, tipo_evento, descricao) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, e.getNomeEvento());
            stmt.setTimestamp(2, new Timestamp(e.getDataEvento().getTime()));
            stmt.setString(3, e.getTipoEvento());
            stmt.setString(4, e.getDescricao());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEvento(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Evento e) throws SQLException {
        String sql = "UPDATE EVENTO SET nome_evento = ?, data_evento = ?, tipo_evento = ?, descricao = ? WHERE id_evento = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNomeEvento());
            stmt.setTimestamp(2, new Timestamp(e.getDataEvento().getTime()));
            stmt.setString(3, e.getTipoEvento());
            stmt.setString(4, e.getDescricao());
            stmt.setInt(5, e.getIdEvento());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idEvento) throws SQLException {
        String sql = "DELETE FROM EVENTO WHERE id_evento = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            stmt.executeUpdate();
        }
    }

    public List<Evento> listar() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM EVENTO ORDER BY data_evento ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                eventos.add(new Evento(
                    rs.getInt("id_evento"),
                    rs.getString("nome_evento"),
                    rs.getTimestamp("data_evento"),
                    rs.getString("tipo_evento"),
                    rs.getString("descricao")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return eventos;
    }
}
