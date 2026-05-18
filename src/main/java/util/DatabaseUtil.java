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
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CLIENTE (" +
                               "id_cliente INT AUTO_INCREMENT PRIMARY KEY, " +
                               "nome VARCHAR(100) NOT NULL, " +
                               "cpf VARCHAR(14) UNIQUE, " +
                               "email VARCHAR(100), " +
                               "telefone VARCHAR(20), " +
                               "pontos_fidelidade INT DEFAULT 0, " +
                               "data_nascimento DATE)");

            java.sql.ResultSet rs = meta.getColumns(null, null, "CLIENTE", "data_nascimento");
            if (!rs.next()) {
                java.sql.ResultSet rsLower = meta.getColumns(null, null, "cliente", "data_nascimento");
                if (!rsLower.next()) {
                    stmt.executeUpdate("ALTER TABLE CLIENTE ADD COLUMN data_nascimento DATE");
                }
            }

            // Migrate user passwords to SHA-256 if stored in plain text
            try (java.sql.ResultSet rsUser = stmt.executeQuery("SELECT id_usuario, password FROM USUARIO")) {
                java.util.List<Object[]> usersToUpdate = new java.util.ArrayList<>();
                while (rsUser.next()) {
                    int id = rsUser.getInt("id_usuario");
                    String pwd = rsUser.getString("password");
                    if (pwd == null || pwd.length() != 64 || !pwd.matches("^[0-9a-fA-F]{64}$")) {
                        usersToUpdate.add(new Object[]{id, SecurityUtil.hashPassword(pwd)});
                    }
                }
                for (Object[] u : usersToUpdate) {
                    try (java.sql.PreparedStatement stmtUp = conn.prepareStatement("UPDATE USUARIO SET password = ? WHERE id_usuario = ?")) {
                        stmtUp.setString(1, (String) u[1]);
                        stmtUp.setInt(2, (Integer) u[0]);
                        stmtUp.executeUpdate();
                    }
                }
            }

            // Seed sample clients if empty
            try (java.sql.ResultSet rsCli = stmt.executeQuery("SELECT COUNT(*) FROM CLIENTE")) {
                if (rsCli.next() && rsCli.getInt(1) == 0) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    int currentMonth = cal.get(java.util.Calendar.MONTH) + 1;
                    String monthStr = String.format("%02d", currentMonth);
                    
                    String sqlCli = "INSERT INTO CLIENTE (nome, cpf, email, telefone, pontos_fidelidade, data_nascimento) VALUES (?, ?, ?, ?, ?, ?)";
                    try (java.sql.PreparedStatement stmtCli = conn.prepareStatement(sqlCli)) {
                        // Client 1 (Birthday this month)
                        stmtCli.setString(1, "Ana Beatriz Silva");
                        stmtCli.setString(2, "111.444.777-35");
                        stmtCli.setString(3, "anabeatriz@email.com");
                        stmtCli.setString(4, "(11) 98765-4321");
                        stmtCli.setInt(5, 120);
                        stmtCli.setDate(6, java.sql.Date.valueOf("1995-" + monthStr + "-15"));
                        stmtCli.executeUpdate();
                        
                        // Client 2 (Birthday this month)
                        stmtCli.setString(1, "Carlos Henrique Souza");
                        stmtCli.setString(2, "123.456.789-02");
                        stmtCli.setString(3, "carlos.henrique@email.com");
                        stmtCli.setString(4, "(11) 97654-3210");
                        stmtCli.setInt(5, 50);
                        stmtCli.setDate(6, java.sql.Date.valueOf("1988-" + monthStr + "-20"));
                        stmtCli.executeUpdate();
                        
                        // Client 3
                        stmtCli.setString(1, "Mariana Costa Santos");
                        stmtCli.setString(2, "111.222.333-96");
                        stmtCli.setString(3, "mariana.santos@email.com");
                        stmtCli.setString(4, "(21) 99888-7777");
                        stmtCli.setInt(5, 240);
                        stmtCli.setDate(6, java.sql.Date.valueOf("2000-03-10"));
                        stmtCli.executeUpdate();
                        
                        // Client 4
                        stmtCli.setString(1, "Rodrigo de Oliveira");
                        stmtCli.setString(2, "444.555.666-19");
                        stmtCli.setString(3, "rodrigo.oliveira@email.com");
                        stmtCli.setString(4, "(31) 99123-4567");
                        stmtCli.setInt(5, 80);
                        stmtCli.setDate(6, java.sql.Date.valueOf("1992-09-25"));
                        stmtCli.executeUpdate();
                        
                        // Client 5
                        stmtCli.setString(1, "Juliana Mendes Abreu");
                        stmtCli.setString(2, "777.888.999-41");
                        stmtCli.setString(3, "juliana.abreu@email.com");
                        stmtCli.setString(4, "(11) 98888-9999");
                        stmtCli.setInt(5, 310);
                        stmtCli.setDate(6, java.sql.Date.valueOf("1985-12-05"));
                        stmtCli.executeUpdate();
                    }
                }
            }

            // Seed sample events if empty
            try (java.sql.ResultSet rsEv = stmt.executeQuery("SELECT COUNT(*) FROM EVENTO")) {
                if (rsEv.next() && rsEv.getInt(1) == 0) {
                    String sqlEv = "INSERT INTO EVENTO (nome_evento, data_evento, tipo_evento, descricao) VALUES (?, ?, ?, ?)";
                    try (java.sql.PreparedStatement stmtEv = conn.prepareStatement(sqlEv)) {
                        // Event 1
                        stmtEv.setString(1, "Clube do Livro: Ficção Científica");
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.add(java.util.Calendar.DAY_OF_YEAR, 5);
                        stmtEv.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
                        stmtEv.setString(3, "TROCA_LIVROS");
                        stmtEv.setString(4, "Discussão sobre o clássico 'Duna' de Frank Herbert, com troca de livros entre os participantes e café cortesia.");
                        stmtEv.executeUpdate();
                        
                        // Event 2
                        stmtEv.setString(1, "Workshop: Arte do Espresso e Latte Art");
                        cal = java.util.Calendar.getInstance();
                        cal.add(java.util.Calendar.DAY_OF_YEAR, 12);
                        stmtEv.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
                        stmtEv.setString(3, "WORKSHOP_CAFE");
                        stmtEv.setString(4, "Aprenda a tirar o espresso perfeito e a criar desenhos incríveis com o leite vaporizado. Inclui degustação.");
                        stmtEv.executeUpdate();
                        
                        // Event 3
                        stmtEv.setString(1, "Noite de Poesia e Sarau");
                        cal = java.util.Calendar.getInstance();
                        cal.add(java.util.Calendar.DAY_OF_YEAR, 20);
                        stmtEv.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
                        stmtEv.setString(3, "OUTROS");
                        stmtEv.setString(4, "Espaço aberto para leitura de poesias e apresentações acústicas no mezanino do Coffee&Books.");
                        stmtEv.executeUpdate();
                    }
                }
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
