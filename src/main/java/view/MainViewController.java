package view;

import dao.*;
import model.*;
import util.Banco;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainViewController {

    @FXML
    private Label lblTotalBooks;

    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblTotalSuppliers;

    @FXML
    private Label lblTotalDonations;

    @FXML
    private Label lblDbName;

    @FXML
    private Label lblDbHostPort;

    @FXML
    private Label lblDbUser;

    @FXML
    private Label lblDbStatus;

    @FXML
    private ListView<String> lstLowStock;

    @FXML
    private ListView<String> lstEvents;

    @FXML
    public void initialize() {
        loadConnectionInfo();
        loadDashboardMetrics();
    }

    private void loadConnectionInfo() {
        lblDbName.setText(Banco.bancoDados);
        lblDbHostPort.setText(Banco.servidor + ":" + Banco.porta);
        lblDbUser.setText(Banco.usuario);
        
        try {
            Connection conn = Banco.obterConexao();
            if (conn != null && !conn.isClosed()) {
                lblDbStatus.setText("CONECTADO (Ativo)");
                lblDbStatus.setStyle("-fx-text-fill: #52c41a; -fx-font-weight: bold;");
            } else {
                lblDbStatus.setText("DESCONECTADO");
                lblDbStatus.setStyle("-fx-text-fill: #f5222d; -fx-font-weight: bold;");
            }
        } catch (SQLException e) {
            lblDbStatus.setText("ERRO DE CONEXÃO");
            lblDbStatus.setStyle("-fx-text-fill: #f5222d; -fx-font-weight: bold;");
        }
    }

    private void loadDashboardMetrics() {
        // Clear lists
        lstLowStock.getItems().clear();
        lstEvents.getItems().clear();

        try {
            // 1. Total Books in Stock
            LivroDAO livroDAO = new LivroDAO();
            List<Livro> livros = livroDAO.listar("", "Todos");
            int totalBooks = livros.size();
            lblTotalBooks.setText(String.valueOf(totalBooks));

            // Populate low stock warnings
            for (Livro l : livros) {
                if (l.getEstoqueAtual() == 0) {
                    lstLowStock.getItems().add("❌ [SEM ESTOQUE] " + l.getTitulo() + " (Autor: " + l.getAutor() + ")");
                } else if (l.getEstoqueAtual() <= 3) {
                    lstLowStock.getItems().add("⚠️ [ESTOQUE BAIXO] " + l.getTitulo() + " (Apenas " + l.getEstoqueAtual() + " un)");
                }
            }
            if (lstLowStock.getItems().isEmpty()) {
                lstLowStock.getItems().add("✅ Todos os livros com estoque saudável.");
            }

            // 2. Total Products
            ProdutoConsumoDAO produtoDAO = new ProdutoConsumoDAO();
            List<ProdutoConsumo> produtos = produtoDAO.listar();
            lblTotalProducts.setText(String.valueOf(produtos.size()));

            // 3. Total Suppliers
            FornecedorDAO fornecedorDAO = new FornecedorDAO();
            List<Fornecedor> fornecedores = fornecedorDAO.listar();
            lblTotalSuppliers.setText(String.valueOf(fornecedores.size()));

            // 4. Total Donations
            DoacaoDAO doacaoDAO = new DoacaoDAO();
            List<Doacao> doacoes = doacaoDAO.listar();
            lblTotalDonations.setText(String.valueOf(doacoes.size()));

            // 5. Populate Events
            EventoDAO eventoDAO = new EventoDAO();
            List<Evento> eventos = eventoDAO.listar();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            for (Evento e : eventos) {
                String dataStr = e.getDataEvento() != null ? sdf.format(e.getDataEvento()) : "--/--/----";
                lstEvents.getItems().add("📅 " + dataStr + " - " + e.getNomeEvento() + "\n   " + e.getDescricao());
            }
            if (lstEvents.getItems().isEmpty()) {
                lstEvents.getItems().add("Nenhum evento agendado recentemente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTestConnection() {
        try {
            Connection conn = Banco.obterConexao();
            if (conn != null && !conn.isClosed()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Teste de Conexão");
                alert.setHeaderText(null);
                alert.setContentText("Conexão estabelecida com sucesso usando MariaDB/MySQL!");
                alert.showAndWait();
            }
            loadConnectionInfo();
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Falha na Conexão");
            alert.setHeaderText("Erro de Infraestrutura");
            alert.setContentText("Não foi possível conectar ao banco de dados:\n" + e.getMessage());
            alert.showAndWait();
            loadConnectionInfo();
        }
    }

    @FXML
    private void handleRefreshData() {
        loadConnectionInfo();
        loadDashboardMetrics();
    }

    @FXML
    private void handleLaunchSwing() {
        // Run Swing application on the EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Swing constants L&F
                util.UIConstants.initLookAndFeel();
                // Open Swing Login Frame
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void handleViewFornecedores() {
        FornecedorDAO dao = new FornecedorDAO();
        List<Fornecedor> lista = dao.listar();
        StringBuilder sb = new StringBuilder("Lista de Fornecedores Cadastrados:\n\n");
        for (Fornecedor f : lista) {
            sb.append("• ").append(f.getNomeFantasia())
              .append(" (CNPJ: ").append(f.getCnpj()).append(")\n")
              .append("   Contato: ").append(f.getContato()).append(" | Tipo: ").append(f.getTipoProduto()).append("\n\n");
        }
        if (lista.isEmpty()) {
            sb.append("Nenhum fornecedor cadastrado.");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Fornecedores");
        alert.setHeaderText("Mapeamento Logístico");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleViewEditoras() {
        EditoraDAO dao = new EditoraDAO();
        List<Editora> lista = dao.listar();
        StringBuilder sb = new StringBuilder("Lista de Editoras Cadastradas:\n\n");
        for (Editora e : lista) {
            sb.append("• ").append(e.getNomeEditora())
              .append(" - ").append(e.getCidade()).append("\n");
        }
        if (lista.isEmpty()) {
            sb.append("Nenhuma editora cadastrada.");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Editoras");
        alert.setHeaderText("Distribuição de Obras");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleViewDoacoes() {
        DoacaoDAO dao = new DoacaoDAO();
        List<Doacao> lista = dao.listar();
        StringBuilder sb = new StringBuilder("Doações de Livros Registradas:\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Doacao d : lista) {
            String dataStr = d.getDataDoacao() != null ? sdf.format(d.getDataDoacao()) : "--/--/----";
            sb.append("• Doador: ").append(d.getNomeDoador()).append("\n")
              .append("   Livro: ").append(d.getLivro().getTitulo()).append(" (Autor: ").append(d.getLivro().getAutor()).append(")\n")
              .append("   Data: ").append(dataStr).append("\n\n");
        }
        if (lista.isEmpty()) {
            sb.append("Nenhuma doação registrada.");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Doações");
        alert.setHeaderText("Campanhas de Arrecadação");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
}
