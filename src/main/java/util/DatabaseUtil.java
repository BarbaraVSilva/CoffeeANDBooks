package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/coffeebooks_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Adjust as needed

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found.", e);
        }
    }
    static {
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            
            // Migrate CLIENTE
            java.sql.ResultSet rs = meta.getColumns(null, null, "CLIENTE", "data_nascimento");
            if (!rs.next()) {
                stmt.executeUpdate("ALTER TABLE CLIENTE ADD COLUMN data_nascimento DATE");
            }
            
            // Migrate INGREDIENTE
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS INGREDIENTE (" +
                               "id_ingrediente INT AUTO_INCREMENT PRIMARY KEY, " +
                               "nome_ingrediente VARCHAR(100) NOT NULL, " +
                               "quantidade_atual DOUBLE NOT NULL, " +
                               "unidade_medida VARCHAR(20) NOT NULL)");
                               
            // Insert default ingredients if empty
            try (java.sql.ResultSet rsIng = stmt.executeQuery("SELECT COUNT(*) FROM INGREDIENTE")) {
                if (rsIng.next() && rsIng.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO INGREDIENTE (nome_ingrediente, quantidade_atual, unidade_medida) VALUES " +
                                       "('Grãos de Café (Espresso)', 5000.0, 'g'), " +
                                       "('Leite Integral', 10000.0, 'ml'), " +
                                       "('Chocolate em Pó', 3000.0, 'g'), " +
                                       "('Copos Descartáveis', 500.0, 'un')");
                }
            }

            // Seed sample sales over the last 12 days if empty
            try (java.sql.ResultSet rsV = stmt.executeQuery("SELECT COUNT(*) FROM VENDA_CONSOLIDADA")) {
                if (rsV.next() && rsV.getInt(1) == 0) {
                    String[] pgMethods = {"Pix", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"};
                    java.util.Random rand = new java.util.Random();
                    
                    for (int i = 12; i >= 0; i--) {
                        int numSales = rand.nextInt(3) + 1; // 1 to 3 sales per day
                        for (int j = 0; j < numSales; j++) {
                            double totalVal = 20.0 + rand.nextDouble() * 180.0;
                            String pg = pgMethods[rand.nextInt(pgMethods.length)];
                            int mesa = rand.nextInt(20) + 1;
                            
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.add(java.util.Calendar.DAY_OF_YEAR, -i);
                            cal.add(java.util.Calendar.HOUR_OF_DAY, -rand.nextInt(8));
                            java.sql.Timestamp ts = new java.sql.Timestamp(cal.getTimeInMillis());
                            
                            try (PreparedStatement stmtV = conn.prepareStatement(
                                "INSERT INTO VENDA_CONSOLIDADA (data_venda, valor_total, forma_pagamento, numero_mesa) VALUES (?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                                stmtV.setTimestamp(1, ts);
                                stmtV.setDouble(2, totalVal);
                                stmtV.setString(3, pg);
                                stmtV.setInt(4, mesa);
                                stmtV.executeUpdate();
                                
                                int idVenda = 0;
                                try (ResultSet rsKey = stmtV.getGeneratedKeys()) {
                                    if (rsKey.next()) {
                                        idVenda = rsKey.getInt(1);
                                    }
                                }
                                
                                int numItems = rand.nextInt(2) + 1;
                                for (int k = 0; k < numItems; k++) {
                                    int qty = rand.nextInt(3) + 1;
                                    double preco = 5.0 + rand.nextDouble() * 45.0;
                                    boolean isBook = rand.nextBoolean();
                                    
                                    try (PreparedStatement stmtI = conn.prepareStatement(
                                        "INSERT INTO ITEM_VENDA_GERAL (quantidade, preco_applied, fk_venda, fk_livro, fk_produto) VALUES (?, ?, ?, ?, ?)")) {
                                        stmtI.setInt(1, qty);
                                        stmtI.setDouble(2, preco);
                                        stmtI.setInt(3, idVenda);
                                        
                                        if (isBook) {
                                            stmtI.setInt(4, rand.nextInt(5) + 1); // 1-5
                                            stmtI.setNull(5, Types.INTEGER);
                                        } else {
                                            stmtI.setNull(4, Types.INTEGER);
                                            stmtI.setInt(5, rand.nextInt(5) + 1); // 1-5
                                        }
                                        stmtI.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
