package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import dao.*;
import util.UIConstants;
import util.ComandaManager;

public class PDVFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable tabela;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private JTextField txtPesquisa;
    private JPanel cardPanel;
    private JPanel searchResultPanel;
    private CardLayout cardLayout;
    private double totalGeral = 0;
    private List<ItemVendaGeral> carrinho = new ArrayList<>();
    private int mesaImportadaId = -1;

    public PDVFrame() {
        setTitle("Coffee&Books - Frente de Caixa (PDV)");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // TOP SEARCH BAR on LEFT SIDE
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 10));
        searchBarPanel.setBackground(UIConstants.COLOR_SECONDARY());
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblPesquisa = new JLabel("🔍 Buscar Produto:");
        lblPesquisa.setFont(UIConstants.FONT_LABEL);
        lblPesquisa.setForeground(UIConstants.COLOR_ACCENT());
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 16));

        searchBarPanel.add(lblPesquisa, BorderLayout.WEST);
        searchBarPanel.add(txtPesquisa, BorderLayout.CENTER);
        leftPanel.add(searchBarPanel, BorderLayout.NORTH);

        // CardLayout to switch between Tabs and Search Results
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // TabbedPane for Categories
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(UIConstants.FONT_LABEL);

        // Fetch dynamic categories from the database
        List<String> categorias = new ArrayList<>();
        try {
            List<ProdutoConsumo> produtos = new dao.ProdutoConsumoDAO().listar();
            for (ProdutoConsumo prod : produtos) {
                String cat = prod.getCategoriaCardapio();
                if (cat != null && !cat.trim().isEmpty()) {
                    boolean exists = false;
                    for (String c : categorias) {
                        if (c.equalsIgnoreCase(cat)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        categorias.add(cat);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Fallback default categories if empty (e.g. database offline or unpopulated)
        if (categorias.isEmpty()) {
            categorias.add("Bebidas Quentes");
            categorias.add("Bebidas Frias");
            categorias.add("Salgados");
            categorias.add("Doces");
        }

        // Add dynamic category tabs
        for (String cat : categorias) {
            String tabTitle = cat;
            String lower = cat.toLowerCase();
            if (lower.contains("quente")) {
                tabTitle = "☕ " + cat;
            } else if (lower.contains("fria") || lower.contains("fia")) {
                tabTitle = "🥤 " + cat;
            } else if (lower.contains("salgado")) {
                tabTitle = "🥪 " + cat;
            } else if (lower.contains("doce")) {
                tabTitle = "🍰 " + cat;
            } else {
                tabTitle = "🍽️ " + cat;
            }

            JPanel catPanel = createCategoryPanel(cat);
            JScrollPane scrollPane = new JScrollPane(catPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            tabbedPane.addTab(tabTitle, scrollPane);
        }

        // Add static Books tab
        JPanel booksPanel = createBooksPanel();
        JScrollPane booksScroll = new JScrollPane(booksPanel);
        booksScroll.setBorder(null);
        booksScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("📚 Livros", booksScroll);

        cardPanel.add(tabbedPane, "CATEGORIES");

        // Search Results Panel
        searchResultPanel = new JPanel(new util.WrapLayout(FlowLayout.LEFT, 10, 10));
        searchResultPanel.setBackground(UIConstants.COLOR_PRIMARY());
        JScrollPane searchScroll = new JScrollPane(searchResultPanel);
        searchScroll.setBorder(null);
        searchScroll.getVerticalScrollBar().setUnitIncrement(16);
        cardPanel.add(searchScroll, "SEARCH_RESULTS");

        leftPanel.add(cardPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.CENTER);

        // RIGHT: Cart Panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(380, 0));
        cartPanel.setBackground(UIConstants.COLOR_SECONDARY());
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, UIConstants.COLOR_ACCENT()),
            BorderFactory.createTitledBorder("Carrinho de Compras")
        ));

        String[] cols = {"Item", "Qtd", "Total"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(24);
        cartPanel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Cart Actions (Import Comanda / Remove)
        JPanel cartActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cartActionsPanel.setOpaque(false);
        
        JButton btnImportar = new JButton("📋 Importar Mesa");
        btnImportar.setBackground(UIConstants.COLOR_ACCENT());
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setFont(UIConstants.FONT_BUTTON);
        btnImportar.addActionListener(e -> importarConsumoMesa());
        
        JButton btnRemover = new JButton("❌ Remover");
        btnRemover.setBackground(UIConstants.COLOR_ALERT);
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setFont(UIConstants.FONT_BUTTON);
        btnRemover.addActionListener(e -> removerItemSelecionado());
        
        cartActionsPanel.add(btnImportar);
        cartActionsPanel.add(btnRemover);
        cartPanel.add(cartActionsPanel, BorderLayout.NORTH);

        // Bottom Summary
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTotal = new JLabel("TOTAL: R$ 0,00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTotal.setForeground(UIConstants.COLOR_ACCENT());
        bottomPanel.add(lblTotal);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton btnFinalizar = new JButton("💰 Finalizar Venda (Cupom)");
        btnFinalizar.setFont(UIConstants.FONT_TITLE.deriveFont(16f));
        btnFinalizar.setBackground(UIConstants.COLOR_SUCCESS);
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.addActionListener(e -> finalizarVenda());
        btnPanel.add(btnFinalizar);
        bottomPanel.add(btnPanel);

        cartPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(cartPanel, BorderLayout.EAST);

        // Search text change listener
        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
    }

    private JPanel createCategoryPanel(String categoria) {
        JPanel p = new JPanel(new util.WrapLayout(FlowLayout.LEFT, 12, 12));
        p.setBackground(UIConstants.COLOR_PRIMARY());
        
        List<ProdutoConsumo> produtos = new ProdutoConsumoDAO().listar();
        for (ProdutoConsumo prod : produtos) {
            if (prod.getCategoriaCardapio().equalsIgnoreCase(categoria) && prod.isDisponivel()) {
                JButton btn = createProductButton(prod.getNomeAlimento(), prod.getPrecoUnitario(), prod.getImagePath(), getEmojiForCategory(prod.getCategoriaCardapio()), e -> adicionarAoCarrinho(prod));
                p.add(btn);
            }
        }
        return p;
    }

    private JPanel createBooksPanel() {
        JPanel p = new JPanel(new util.WrapLayout(FlowLayout.LEFT, 12, 12));
        p.setBackground(UIConstants.COLOR_PRIMARY());
        
        List<Livro> livros = new LivroDAO().listar(null, "Todos");
        for (Livro l : livros) {
            JButton btn = createProductButton(l.getTitulo(), l.getPrecoVenda(), l.getImagePath(), "📚", e -> adicionarAoCarrinho(l));
            p.add(btn);
        }
        return p;
    }

    private JButton createProductButton(String name, double price, String imagePath, String defaultEmoji, java.awt.event.ActionListener action) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setPreferredSize(new Dimension(150, 150));
        btn.setBackground(UIConstants.COLOR_SECONDARY());
        btn.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_ACCENT(), 1, true));

        // Attempt to load image
        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        ImageIcon icon = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            // 1. Try direct file path relative to root
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                icon = new ImageIcon(file.getPath());
            } else {
                // 2. Try loading from resources (standard Maven classpath)
                String resourcePath = imagePath;
                if (resourcePath.startsWith("src/main/resources")) {
                    resourcePath = resourcePath.substring("src/main/resources".length());
                }
                resourcePath = resourcePath.replace('\\', '/');
                if (!resourcePath.startsWith("/")) {
                    resourcePath = "/" + resourcePath;
                }
                
                java.net.URL imgUrl = PDVFrame.class.getResource(resourcePath);
                if (imgUrl != null) {
                    icon = new ImageIcon(imgUrl);
                } else {
                    // 3. Fallback: try relative from root or direct filename in assets
                    String filename = new java.io.File(imagePath).getName();
                    java.io.File rootAssetFile = new java.io.File("src/main/resources/assets/" + filename);
                    if (rootAssetFile.exists()) {
                        icon = new ImageIcon(rootAssetFile.getPath());
                    }
                }
            }
        }

        if (icon != null) {
            try {
                Image img = icon.getImage().getScaledInstance(140, 80, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                lblImg.setText(defaultEmoji);
                lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            }
        } else {
            lblImg.setText(defaultEmoji);
            lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }

        JLabel lblInfo = new JLabel("<html><center><b>" + name + "</b><br><font color='green'>R$ " + String.format("%.2f", price) + "</font></center></html>", SwingConstants.CENTER);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        btn.add(lblImg, BorderLayout.CENTER);
        btn.add(lblInfo, BorderLayout.SOUTH);
        btn.addActionListener(action);

        return btn;
    }

    private String getEmojiForCategory(String category) {
        if (category == null) return "🍽️";
        String lower = category.toLowerCase();
        if (lower.contains("quente")) return "☕";
        if (lower.contains("fria") || lower.contains("fia")) return "🥤";
        if (lower.contains("salgado")) return "🥪";
        if (lower.contains("doce")) return "🍰";
        return "🍽️";
    }

    private void filtrar() {
        String text = txtPesquisa.getText().trim();
        if (text.isEmpty()) {
            cardLayout.show(cardPanel, "CATEGORIES");
        } else {
            searchResultPanel.removeAll();
            
            // Search Foods & Drinks
            List<ProdutoConsumo> produtos = new ProdutoConsumoDAO().listar();
            for (ProdutoConsumo p : produtos) {
                if (p.getNomeAlimento().toLowerCase().contains(text.toLowerCase()) && p.isDisponivel()) {
                    JButton btn = createProductButton(p.getNomeAlimento(), p.getPrecoUnitario(), p.getImagePath(), getEmojiForCategory(p.getCategoriaCardapio()), e -> adicionarAoCarrinho(p));
                    searchResultPanel.add(btn);
                }
            }

            // Search Books
            List<Livro> livros = new LivroDAO().listar(text, "Todos");
            for (Livro l : livros) {
                JButton btn = createProductButton(l.getTitulo(), l.getPrecoVenda(), l.getImagePath(), "📚", e -> adicionarAoCarrinho(l));
                searchResultPanel.add(btn);
            }

            searchResultPanel.revalidate();
            searchResultPanel.repaint();
            cardLayout.show(cardPanel, "SEARCH_RESULTS");
        }
    }

    private void adicionarAoCarrinho(Object item) {
        ItemVendaGeral iv = new ItemVendaGeral();
        iv.setQuantidade(1);
        String nome = "";
        double preco = 0;

        if (item instanceof Livro) {
            Livro l = (Livro) item;
            iv.setLivro(l);
            nome = l.getTitulo();
            preco = l.getPrecoVenda();
        } else {
            ProdutoConsumo p = (ProdutoConsumo) item;
            iv.setProduto(p);
            nome = p.getNomeAlimento();
            preco = p.getPrecoUnitario();
        }

        iv.setPrecoApplied(preco);
        carrinho.add(iv);
        totalGeral += preco;
        
        model.addRow(new Object[]{nome, 1, String.format("R$ %.2f", preco)});
        lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
    }

    private void removerItemSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            ItemVendaGeral iv = carrinho.get(row);
            totalGeral -= iv.getPrecoApplied() * iv.getQuantidade();
            carrinho.remove(row);
            model.removeRow(row);
            lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item no carrinho para remover.");
        }
    }

    private void importarConsumoMesa() {
        java.util.Map<Integer, Comanda> comandas = ComandaManager.getComandasAtivas();
        if (comandas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma mesa ocupada ou comanda aberta no momento.");
            return;
        }

        List<String> options = new ArrayList<>();
        List<Integer> mesaNums = new ArrayList<>();
        for (Comanda c : comandas.values()) {
            options.add("Mesa " + c.getNumeroMesa() + " - " + c.getClienteNome() + " (R$ " + String.format("%.2f", c.getSubtotal()) + ")");
            mesaNums.add(c.getNumeroMesa());
        }

        JComboBox<String> cbOptions = new JComboBox<>(options.toArray(new String[0]));
        int result = JOptionPane.showConfirmDialog(this, cbOptions, "Importar Consumo de Mesa/Comanda", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && cbOptions.getSelectedIndex() >= 0) {
            int idx = cbOptions.getSelectedIndex();
            int mesaNum = mesaNums.get(idx);
            Comanda comanda = comandas.get(mesaNum);

            // Clear cart
            carrinho.clear();
            model.setRowCount(0);
            totalGeral = 0;

            // Import items
            for (ItemVendaGeral iv : comanda.getItens()) {
                carrinho.add(iv);
                String nome = iv.getLivro() != null ? iv.getLivro().getTitulo() : iv.getProduto().getNomeAlimento();
                double subTotal = iv.getPrecoApplied() * iv.getQuantidade();
                totalGeral += subTotal;
                model.addRow(new Object[]{nome, iv.getQuantidade(), String.format("R$ %.2f", subTotal)});
            }

            mesaImportadaId = mesaNum;
            lblTotal.setText(String.format("TOTAL: R$ %.2f", totalGeral));
            JOptionPane.showMessageDialog(this, "Consumo da Mesa " + mesaNum + " importado com sucesso!");
        }
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrinho vazio!");
            return;
        }

        // 1. Choose Payment Method
        String[] pagamentos = {"Pix", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"};
        String formaPg = (String) JOptionPane.showInputDialog(
            this,
            "Escolha a Forma de Pagamento:",
            "Meio de Pagamento",
            JOptionPane.QUESTION_MESSAGE,
            null,
            pagamentos,
            pagamentos[0]
        );

        if (formaPg == null) {
            return; // Cancelled
        }

        // 2. Ask for Customer Loyalty association
        Cliente clienteFidelidade = null;
        int optFidelidade = JOptionPane.showConfirmDialog(this, "Deseja vincular um Cliente (CPF) para acumular Pontos de Fidelidade?", "Programa de Fidelidade", JOptionPane.YES_NO_OPTION);
        if (optFidelidade == JOptionPane.YES_OPTION) {
            String cpf = JOptionPane.showInputDialog(this, "Digite o CPF do Cliente (Apenas números ou formato padrão):", "Vincular Cliente", JOptionPane.QUESTION_MESSAGE);
            if (cpf != null && !cpf.trim().isEmpty()) {
                ClienteDAO clientDao = new ClienteDAO();
                clienteFidelidade = clientDao.buscarPorCpf(cpf.trim());
                if (clienteFidelidade == null) {
                    JOptionPane.showMessageDialog(this, "Cliente com este CPF não cadastrado! A venda será finalizada sem pontos.");
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente " + clienteFidelidade.getNome() + " vinculado com sucesso!");
                }
            }
        }

        VendaConsolidada v = new VendaConsolidada();
        v.setValorTotal(totalGeral);
        v.setFormaPagamento(formaPg);
        v.setItens(new ArrayList<>(carrinho));
        v.setDataVenda(new java.util.Date());

        try {
            new VendaDAO().salvar(v);
            util.InvoiceUtil.gerarNotaFiscal(v);
            
            // 3. Loyalty and Birthday calculations if customer is linked
            if (clienteFidelidade != null) {
                // Rule 1: Points based on units consumed
                // +5 points per book, +2 points per beverage/food item
                int pontosGanhos = 0;
                for (ItemVendaGeral iv : carrinho) {
                    if (iv.getLivro() != null) {
                        pontosGanhos += 5 * iv.getQuantidade();
                    } else if (iv.getProduto() != null) {
                        pontosGanhos += 2 * iv.getQuantidade();
                    }
                }
                
                if (pontosGanhos > 0) {
                    clienteFidelidade.setPontosFidelidade(clienteFidelidade.getPontosFidelidade() + pontosGanhos);
                    new ClienteDAO().atualizar(clienteFidelidade);
                    JOptionPane.showMessageDialog(this, "Parabéns! " + clienteFidelidade.getNome() + " acumulou +" + pontosGanhos + " pontos de fidelidade na compra!");
                }

                // Rule 2: Birthday Reward (1 Coffee + 1 Bookmark)
                if (clienteFidelidade.getDataNascimento() != null) {
                    java.util.Calendar calToday = java.util.Calendar.getInstance();
                    java.util.Calendar calDob = java.util.Calendar.getInstance();
                    calDob.setTime(clienteFidelidade.getDataNascimento());
                    
                    if (calToday.get(java.util.Calendar.DAY_OF_MONTH) == calDob.get(java.util.Calendar.DAY_OF_MONTH) &&
                        calToday.get(java.util.Calendar.MONTH) == calDob.get(java.util.Calendar.MONTH)) {
                        
                        JOptionPane.showMessageDialog(this, 
                            "🎂 PARABÉNS DE ANIVERSÁRIO! 🎈\n\n" +
                            "Hoje é o aniversário de " + clienteFidelidade.getNome() + "!\n" +
                            "🎁 Como presente especial da Coffee&Books, o cliente ganhou:\n" +
                            "- ☕ 1 Café Especial Cortesia do Dia!\n" +
                            "- 🔖 1 Marca-páginas exclusivo da casa!\n\n" +
                            "Entregue os mimos ao cliente com o nosso carinho! 🎉",
                            "Aniversariante Especial!",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }

            // Clean Comanda/Mesa if imported
            if (mesaImportadaId != -1) {
                ComandaManager.fecharComanda(mesaImportadaId);
                mesaImportadaId = -1;
            }

            JOptionPane.showMessageDialog(this, "Venda consolidada! Cupom gerado.");
            carrinho.clear();
            model.setRowCount(0);
            totalGeral = 0;
            lblTotal.setText("TOTAL: R$ 0,00");
        } catch (exception.EstoqueInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Estoque Baixo", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
