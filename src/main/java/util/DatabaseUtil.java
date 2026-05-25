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
        return Banco.obterConexao();
    }

    private static void initializeDatabaseSchema(Connection conn) {
        try {
            java.sql.DatabaseMetaData dbm = conn.getMetaData();
            boolean tableExists = false;
            try (ResultSet tables = dbm.getTables(null, null, "USUARIO", null)) {
                if (tables.next()) {
                    tableExists = true;
                }
            }
            if (!tableExists) {
                try (ResultSet tables = dbm.getTables(null, null, "usuario", null)) {
                    if (tables.next()) {
                        tableExists = true;
                    }
                }
            }
            
            if (tableExists) {
                return; // Schema already created
            }

            System.out.println("Tabelas principais nao encontradas. Inicializando banco de dados...");
            
            try (java.io.InputStream is = DatabaseUtil.class.getResourceAsStream("/database.sql")) {
                if (is == null) {
                    System.err.println("Erro: Arquivo '/database.sql' nao encontrado nos recursos!");
                    return;
                }
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                    
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                            continue;
                        }
                        sb.append(line).append("\n");
                    }
                    
                    String[] statements = sb.toString().split(";");
                    try (Statement stmt = conn.createStatement()) {
                        for (String sql : statements) {
                            String trimmedSql = sql.trim();
                            if (!trimmedSql.isEmpty()) {
                                stmt.execute(trimmedSql);
                            }
                        }
                    }
                    System.out.println("Banco de dados criado e populado com sucesso!");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar esquema do banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static {
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            initializeDatabaseSchema(conn);
            
            // Auto-update missing image paths in the database if they are null
            try (PreparedStatement stmtL = conn.prepareStatement("UPDATE LIVRO SET image_path = ? WHERE titulo = ? AND image_path IS NULL")) {
                String[][] books = {
                    {"src/main/resources/assets/duna.jpg", "Duna"},
                    {"src/main/resources/assets/fundacao.jpg", "Fundação"},
                    {"src/main/resources/assets/iluminado.jpg", "O Iluminado"},
                    {"src/main/resources/assets/domcasmurro.jpg", "Dom Casmurro"},
                    {"src/main/resources/assets/republica.jpg", "A República"},
                    {"src/main/resources/assets/pequenoprincipe.jpg", "O Pequeno Príncipe"},
                    {"src/main/resources/assets/1984.jpg", "1984"},
                    {"src/main/resources/assets/cemanos.jpg", "Cem Anos de Solidão"},
                    {"src/main/resources/assets/sherlock.jpg", "Sherlock Holmes: Estudo em Vermelho"},
                    {"src/main/resources/assets/hobbit.jpg", "O Hobbit"},
                    {"src/main/resources/assets/cortico.jpg", "O Cortiço"}
                };
                for (String[] b : books) {
                    stmtL.setString(1, b[0]);
                    stmtL.setString(2, b[1]);
                    stmtL.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("Aviso: Falha ao atualizar caminhos das imagens dos livros: " + e.getMessage());
            }

            try (PreparedStatement stmtP = conn.prepareStatement("UPDATE PRODUTO_CONSUMO SET image_path = ? WHERE nome_alimento = ? AND image_path IS NULL")) {
                String[][] prods = {
                    {"src/main/resources/assets/mocha.jpg", "Shake Shake Shakespeare (Mocha)"},
                    {"src/main/resources/assets/capuccino.jpg", "Capuccino Hemingway"},
                    {"src/main/resources/assets/pao_queijo.jpg", "Pão de Queijo da Vila"},
                    {"src/main/resources/assets/torta_duna.jpg", "Torta Red Velvet \"Duna\""},
                    {"src/main/resources/assets/suco_laranja.jpg", "Suco Natural \"Laranja Mecânica\""},
                    {"src/main/resources/assets/sanduiche.jpg", "Sanduíche \"Metamorfose\""},
                    {"src/main/resources/assets/croissant.jpg", "Croissant Baudelaire"},
                    {"src/main/resources/assets/empada.jpg", "Empada Edgar Allan Poe"},
                    {"src/main/resources/assets/cold_brew.jpg", "Cold Brew Orwell"},
                    {"src/main/resources/assets/soda_italiana.jpg", "Soda Italiana Poe"},
                    {"src/main/resources/assets/torta_limao.jpg", "Torta de Limão Virginia Woolf"},
                    {"src/main/resources/assets/brownie.jpg", "Brownie Bukowski"}
                };
                for (String[] p : prods) {
                    stmtP.setString(1, p[0]);
                    stmtP.setString(2, p[1]);
                    stmtP.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("Aviso: Falha ao atualizar caminhos das imagens do cardápio: " + e.getMessage());
            }

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
                                       "('Copos Descartáveis', 500.0, 'un'), " +
                                       "('Canela em Pó', 1000.0, 'g'), " +
                                       "('Essência de Baunilha', 500.0, 'ml'), " +
                                       "('Chá Verde Folhas', 1000.0, 'g'), " +
                                       "('Limão (Suco)', 2000.0, 'ml')");
                }
            }
            
            // Migrate LIVRO to add fk_editora
            java.sql.ResultSet rsLivro = meta.getColumns(null, null, "LIVRO", "fk_editora");
            if (!rsLivro.next()) {
                java.sql.ResultSet rsLivroLower = meta.getColumns(null, null, "livro", "fk_editora");
                if (!rsLivroLower.next()) {
                    try {
                        stmt.executeUpdate("ALTER TABLE LIVRO ADD COLUMN fk_editora INT NULL, ADD FOREIGN KEY (fk_editora) REFERENCES EDITORA(id_editora)");
                    } catch (Exception ex) {
                        System.err.println("Aviso ao adicionar coluna fk_editora: " + ex.getMessage());
                    }
                }
            }
            try {
                stmt.executeUpdate("UPDATE LIVRO SET fk_editora = 1 WHERE id_livro % 2 = 0 AND fk_editora IS NULL");
                stmt.executeUpdate("UPDATE LIVRO SET fk_editora = 2 WHERE id_livro % 2 = 1 AND fk_editora IS NULL");
            } catch (Exception ex) {
                System.err.println("Erro ao atualizar fk_editora em LIVRO: " + ex.getMessage());
            }

            // Migrate INGREDIENTE to add fk_fornecedor
            java.sql.ResultSet rsIngF = meta.getColumns(null, null, "INGREDIENTE", "fk_fornecedor");
            if (!rsIngF.next()) {
                java.sql.ResultSet rsIngFLower = meta.getColumns(null, null, "ingrediente", "fk_fornecedor");
                if (!rsIngFLower.next()) {
                    try {
                        stmt.executeUpdate("ALTER TABLE INGREDIENTE ADD COLUMN fk_fornecedor INT NULL, ADD FOREIGN KEY (fk_fornecedor) REFERENCES FORNECEDOR(id_fornecedor)");
                    } catch (Exception ex) {
                        System.err.println("Aviso ao adicionar coluna fk_fornecedor em INGREDIENTE: " + ex.getMessage());
                    }
                }
            }
            try {
                stmt.executeUpdate("UPDATE INGREDIENTE SET fk_fornecedor = 1 WHERE (nome_ingrediente LIKE '%Café%' OR nome_ingrediente LIKE '%Chá%') AND fk_fornecedor IS NULL");
                stmt.executeUpdate("UPDATE INGREDIENTE SET fk_fornecedor = 2 WHERE (nome_ingrediente LIKE '%Leite%' OR nome_ingrediente LIKE '%Chocolate%' OR nome_ingrediente LIKE '%Limão%' OR nome_ingrediente LIKE '%Canela%' OR nome_ingrediente LIKE '%Baunilha%' OR nome_ingrediente LIKE '%Copo%') AND fk_fornecedor IS NULL");
            } catch (Exception ex) {
                System.err.println("Erro ao atualizar fk_fornecedor em INGREDIENTE: " + ex.getMessage());
            }

            // Migrate PRODUTO_CONSUMO to add fk_fornecedor
            java.sql.ResultSet rsPC = meta.getColumns(null, null, "PRODUTO_CONSUMO", "fk_fornecedor");
            if (!rsPC.next()) {
                java.sql.ResultSet rsPCLower = meta.getColumns(null, null, "produto_consumo", "fk_fornecedor");
                if (!rsPCLower.next()) {
                    try {
                        stmt.executeUpdate("ALTER TABLE PRODUTO_CONSUMO ADD COLUMN fk_fornecedor INT NULL, ADD FOREIGN KEY (fk_fornecedor) REFERENCES FORNECEDOR(id_fornecedor)");
                    } catch (Exception ex) {
                        System.err.println("Aviso ao adicionar coluna fk_fornecedor em PRODUTO_CONSUMO: " + ex.getMessage());
                    }
                }
            }
            try {
                stmt.executeUpdate("UPDATE PRODUTO_CONSUMO SET fk_fornecedor = 1 WHERE (categoria_cardapio = 'Bebidas Quentes' OR categoria_cardapio = 'Bebidas Frias') AND fk_fornecedor IS NULL");
                stmt.executeUpdate("UPDATE PRODUTO_CONSUMO SET fk_fornecedor = 2 WHERE (categoria_cardapio = 'Salgados' OR categoria_cardapio = 'Doces') AND fk_fornecedor IS NULL");
            } catch (Exception ex) {
                System.err.println("Erro ao atualizar fk_fornecedor em PRODUTO_CONSUMO: " + ex.getMessage());
            }

            // Migrate VENDA_CONSOLIDADA to add fk_cliente
            java.sql.ResultSet rsVC = meta.getColumns(null, null, "VENDA_CONSOLIDADA", "fk_cliente");
            if (!rsVC.next()) {
                java.sql.ResultSet rsVCLower = meta.getColumns(null, null, "venda_consolidada", "fk_cliente");
                if (!rsVCLower.next()) {
                    try {
                        stmt.executeUpdate("ALTER TABLE VENDA_CONSOLIDADA ADD COLUMN fk_cliente INT NULL, ADD FOREIGN KEY (fk_cliente) REFERENCES CLIENTE(id_cliente)");
                    } catch (Exception ex) {
                        System.err.println("Aviso ao adicionar coluna fk_cliente em VENDA_CONSOLIDADA: " + ex.getMessage());
                    }
                }
            }
            try {
                stmt.executeUpdate("UPDATE VENDA_CONSOLIDADA SET fk_cliente = 1 WHERE id_venda % 3 = 1 AND fk_cliente IS NULL");
                stmt.executeUpdate("UPDATE VENDA_CONSOLIDADA SET fk_cliente = 2 WHERE id_venda % 3 = 2 AND fk_cliente IS NULL");
            } catch (Exception ex) {
                System.err.println("Erro ao atualizar fk_cliente em VENDA_CONSOLIDADA: " + ex.getMessage());
            }

            // Migrate and Create FICHA_TECNICA
            boolean tableFichaExists = false;
            try (java.sql.ResultSet tables = meta.getTables(null, null, "FICHA_TECNICA", null)) {
                if (tables.next()) tableFichaExists = true;
            }
            if (!tableFichaExists) {
                try (java.sql.ResultSet tables = meta.getTables(null, null, "ficha_tecnica", null)) {
                    if (tables.next()) tableFichaExists = true;
                }
            }
            if (!tableFichaExists) {
                try {
                    stmt.executeUpdate("CREATE TABLE FICHA_TECNICA (" +
                                       "id_ficha INT AUTO_INCREMENT PRIMARY KEY, " +
                                       "fk_produto INT NOT NULL, " +
                                       "fk_ingrediente INT NOT NULL, " +
                                       "quantidade_necessaria DOUBLE NOT NULL, " +
                                       "FOREIGN KEY (fk_produto) REFERENCES PRODUTO_CONSUMO(id_produto) ON DELETE CASCADE, " +
                                       "FOREIGN KEY (fk_ingrediente) REFERENCES INGREDIENTE(id_ingrediente))");
                                       
                    stmt.executeUpdate("INSERT INTO FICHA_TECNICA (fk_produto, fk_ingrediente, quantidade_necessaria) VALUES " +
                                       "(1, 1, 15.0), " +
                                       "(1, 2, 150.0), " +
                                       "(1, 3, 30.0), " +
                                       "(1, 4, 1.0), " +
                                       "(2, 1, 15.0), " +
                                       "(2, 2, 200.0), " +
                                       "(2, 4, 1.0)");
                } catch (Exception ex) {
                    System.err.println("Erro ao criar FICHA_TECNICA: " + ex.getMessage());
                }
            }

            // Migrate and Create PARTICIPACAO_EVENTO
            boolean tablePartExists = false;
            try (java.sql.ResultSet tables = meta.getTables(null, null, "PARTICIPACAO_EVENTO", null)) {
                if (tables.next()) tablePartExists = true;
            }
            if (!tablePartExists) {
                try (java.sql.ResultSet tables = meta.getTables(null, null, "participacao_evento", null)) {
                    if (tables.next()) tablePartExists = true;
                }
            }
            if (!tablePartExists) {
                try {
                    stmt.executeUpdate("CREATE TABLE PARTICIPACAO_EVENTO (" +
                                       "id_participacao INT AUTO_INCREMENT PRIMARY KEY, " +
                                       "fk_evento INT NOT NULL, " +
                                       "fk_cliente INT NOT NULL, " +
                                       "data_inscricao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                       "FOREIGN KEY (fk_evento) REFERENCES EVENTO(id_evento) ON DELETE CASCADE, " +
                                       "FOREIGN KEY (fk_cliente) REFERENCES CLIENTE(id_cliente) ON DELETE CASCADE)");
                                       
                    stmt.executeUpdate("INSERT INTO PARTICIPACAO_EVENTO (fk_evento, fk_cliente) VALUES " +
                                       "(1, 1), (1, 2), (2, 3), (2, 4), (3, 1), (3, 5)");
                } catch (Exception ex) {
                    System.err.println("Erro ao criar PARTICIPACAO_EVENTO: " + ex.getMessage());
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
