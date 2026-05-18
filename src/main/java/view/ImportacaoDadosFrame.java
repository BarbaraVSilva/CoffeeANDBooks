package view;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import util.UIConstants;
import util.DatabaseUtil;

public class ImportacaoDadosFrame extends JFrame {
    private JComboBox<String> cbTabela;
    private JTextArea txtLogs;
    private JButton btnModelo, btnImportar;

    public ImportacaoDadosFrame() {
        setTitle("Coffee&Books - Central de Importação de Dados");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // Header
        JLabel lblHeader = new JLabel("📥 Importador de Arquivos Externos (CSV)", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE.deriveFont(18f));
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblHeader, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Form selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectionPanel.setBackground(UIConstants.COLOR_SECONDARY());
        selectionPanel.setBorder(UIConstants.getPanelBorder());

        selectionPanel.add(new JLabel("Selecionar Destino:"));
        cbTabela = new JComboBox<>(new String[]{
            "📚 Livros (Acervo)", 
            "👥 Clientes (Fidelidade)", 
            "☕ Comidas e Bebidas (Cardápio)"
        });
        cbTabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        selectionPanel.add(cbTabela);

        btnModelo = new JButton("📥 Baixar Modelo CSV");
        btnModelo.setBackground(UIConstants.COLOR_ACCENT());
        btnModelo.setForeground(Color.WHITE);
        btnModelo.setFont(UIConstants.FONT_BUTTON);
        selectionPanel.add(btnModelo);

        btnImportar = new JButton("⚡ Importar CSV...");
        btnImportar.setBackground(UIConstants.COLOR_SUCCESS);
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setFont(UIConstants.FONT_BUTTON);
        selectionPanel.add(btnImportar);

        centerPanel.add(selectionPanel, BorderLayout.NORTH);

        // Logs
        txtLogs = new JTextArea();
        txtLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLogs.setEditable(false);
        txtLogs.setBackground(Color.BLACK);
        txtLogs.setForeground(new Color(50, 255, 50)); // Matrix style
        JScrollPane scrollLogs = new JScrollPane(txtLogs);
        scrollLogs.setBorder(BorderFactory.createTitledBorder("Console de Processamento"));
        centerPanel.add(scrollLogs, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        btnModelo.addActionListener(e -> baixarModelo());
        btnImportar.addActionListener(e -> importarArquivo());
    }

    private void log(String msg) {
        txtLogs.append(msg + "\n");
        txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
    }

    private void baixarModelo() {
        int opt = cbTabela.getSelectedIndex();
        String content = "";
        String filename = "";
        
        if (opt == 0) {
            filename = "modelo_livros.csv";
            content = "titulo;autor;condicao;preco;estoque;id_genero\n" +
                      "Dom Quixote;Miguel de Cervantes;Novo;59.90;10;3\n" +
                      "1984;George Orwell;Usado;29.90;5;1";
        } else if (opt == 1) {
            filename = "modelo_clientes.csv";
            content = "nome;cpf;email;telefone;pontos;nascimento\n" +
                      "Carlos Silva;111.222.333-44;carlos@email.com;(11) 98888-7777;10;25/08/1990\n" +
                      "Ana Souza;555.665.777-88;ana@email.com;(11) 99999-8888;20;12/04/1995";
        } else {
            filename = "modelo_cardapio.csv";
            content = "nome;preco;categoria;disponivel\n" +
                      "Café Expresso Triplo;8.50;Bebidas Quentes;1\n" +
                      "Cookie Suprema de Chocolate;6.90;Doces;1";
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(filename));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write(content);
                JOptionPane.showMessageDialog(this, "Modelo CSV baixado com sucesso em:\n" + fc.getSelectedFile().getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao baixar modelo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarArquivo() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = fc.getSelectedFile();
            txtLogs.setText("");
            log("🚀 Iniciando importação de: " + selected.getName());
            
            int opt = cbTabela.getSelectedIndex();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(selected), "UTF-8"))) {
                String header = br.readLine(); // Skip header
                log("Header lido: " + header);
                
                int count = 0;
                String line;
                try (Connection conn = DatabaseUtil.getConnection()) {
                    conn.setAutoCommit(false);
                    
                    if (opt == 0) { // Books
                        String sql = "INSERT INTO LIVRO (titulo, autor, condicao_livro, preco_venda, estoque_atual, fk_genero) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            while ((line = br.readLine()) != null) {
                                String[] cols = line.split(";");
                                if (cols.length >= 6) {
                                    stmt.setString(1, cols[0].trim());
                                    stmt.setString(2, cols[1].trim());
                                    stmt.setString(3, cols[2].trim());
                                    stmt.setDouble(4, Double.parseDouble(cols[3].replace(",", ".").trim()));
                                    stmt.setInt(5, Integer.parseInt(cols[4].trim()));
                                    stmt.setInt(6, Integer.parseInt(cols[5].trim()));
                                    stmt.executeUpdate();
                                    log("✔ Livro importado: " + cols[0]);
                                    count++;
                                }
                            }
                        }
                    } else if (opt == 1) { // Clients
                        String sql = "INSERT INTO CLIENTE (nome, cpf, email, telefone, pontos_fidelidade, data_nascimento) VALUES (?, ?, ?, ?, ?, ?)";
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            while ((line = br.readLine()) != null) {
                                String[] cols = line.split(";");
                                if (cols.length >= 6) {
                                    stmt.setString(1, cols[0].trim());
                                    stmt.setString(2, cols[1].trim());
                                    stmt.setString(3, cols[2].trim());
                                    stmt.setString(4, cols[3].trim());
                                    stmt.setInt(5, Integer.parseInt(cols[4].trim()));
                                    
                                    java.util.Date d = sdf.parse(cols[5].trim());
                                    stmt.setDate(6, new java.sql.Date(d.getTime()));
                                    
                                    stmt.executeUpdate();
                                    log("✔ Cliente importado: " + cols[0]);
                                    count++;
                                }
                            }
                        }
                    } else { // Products
                        String sql = "INSERT INTO PRODUTO_CONSUMO (nome_alimento, preco_unitario, categoria_cardapio, disponivel) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            while ((line = br.readLine()) != null) {
                                String[] cols = line.split(";");
                                if (cols.length >= 4) {
                                    stmt.setString(1, cols[0].trim());
                                    stmt.setDouble(2, Double.parseDouble(cols[1].replace(",", ".").trim()));
                                    stmt.setString(3, cols[2].trim());
                                    stmt.setBoolean(4, cols[3].trim().equals("1"));
                                    stmt.executeUpdate();
                                    log("✔ Item de Cardápio importado: " + cols[0]);
                                    count++;
                                }
                            }
                        }
                    }
                    
                    conn.commit();
                    log("\n✨ SUCESSO! Processamento concluído.");
                    log("Total de registros inseridos: " + count);
                    JOptionPane.showMessageDialog(this, "Importação concluída com sucesso!\nForam inseridos " + count + " registros.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                log("\n❌ ERRO DURANTE A IMPORTAÇÃO!");
                log(e.getMessage());
                JOptionPane.showMessageDialog(this, "Erro de processamento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
