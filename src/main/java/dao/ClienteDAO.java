package dao;

import model.Cliente;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTE (nome, cpf, email, telefone, pontos_fidelidade, data_nascimento) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setInt(5, cliente.getPontosFidelidade());
            if (cliente.getDataNascimento() != null) {
                stmt.setDate(6, new java.sql.Date(cliente.getDataNascimento().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE CLIENTE SET nome = ?, cpf = ?, email = ?, telefone = ?, pontos_fidelidade = ?, data_nascimento = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setInt(5, cliente.getPontosFidelidade());
            if (cliente.getDataNascimento() != null) {
                stmt.setDate(6, new java.sql.Date(cliente.getDataNascimento().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setInt(7, cliente.getIdCliente());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idCliente) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE id_cliente = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }

    public Cliente buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM CLIENTE WHERE cpf = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCliente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cliente> listar() {
        return listar("");
    }

    public List<Cliente> listar(String filtro) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE WHERE nome LIKE ? OR cpf LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeVal = "%" + filtro + "%";
            stmt.setString(1, likeVal);
            stmt.setString(2, likeVal);
            stmt.setString(3, likeVal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(extractCliente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    private Cliente extractCliente(ResultSet rs) throws SQLException {
        Date birthDate = null;
        java.sql.Date dbDate = rs.getDate("data_nascimento");
        if (dbDate != null) {
            birthDate = new Date(dbDate.getTime());
        }
        return new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("email"),
            rs.getString("telefone"),
            rs.getInt("pontos_fidelidade"),
            birthDate
        );
    }
}
