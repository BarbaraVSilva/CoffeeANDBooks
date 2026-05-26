package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import util.UIConstants;

/**
 * Tela de Lista de Espera - Gerenciada por COLLECTION (ArrayList em memória).
 * Esta tela NÃO persiste dados no banco de dados.
 * Utiliza ArrayList<Map<String,String>> como coleção principal de dados.
 * Critério: "Uma tela onde o conteúdo será armazenado apenas dentro de uma coleção,
 * sem a necessidade de se persistir os dados no banco."
 * Possui 5 campos de entrada para atender ao critério "todas as telas devem possuir pelo menos 5 campos".
 */
public class ListaEsperaFrame extends JFrame {

    // === COLLECTION: ArrayList armazena os dados em memória (sem banco de dados) ===
    private final ArrayList<Map<String, String>> listaEspera = new ArrayList<>();

    {
        // Seed 10 waitlist entries inspired by literature
        adicionarEntradaEspera("Gabriel García Márquez", "11988887771", "4", "VIP / Reserva", "Jardim das Letras");
        adicionarEntradaEspera("Julio Cortázar", "11988887772", "2", "Normal", "Balcão do Café");
        adicionarEntradaEspera("Jorge Luis Borges", "11988887773", "1", "Preferencial (Idoso/PCD)", "Mezanino Cultural");
        adicionarEntradaEspera("Ernest Hemingway", "11988887774", "5", "VIP / Reserva", "Salão Principal");
        adicionarEntradaEspera("Oscar Wilde", "11988887775", "2", "Normal", "Jardim das Letras");
        adicionarEntradaEspera("Mary Shelley", "11988887776", "3", "Normal", "Salão Principal");
        adicionarEntradaEspera("Agatha Christie", "11988887777", "2", "Normal", "Mezanino Cultural");
        adicionarEntradaEspera("Machado de Assis", "11988887778", "1", "Preferencial (Idoso/PCD)", "Balcão do Café");
        adicionarEntradaEspera("Simone de Beauvoir", "11988887779", "6", "VIP / Reserva", "Jardim das Letras");
        adicionarEntradaEspera("Arthur Conan Doyle", "11988887780", "2", "Normal", "Mezanino Cultural");
    }

    private void adicionarEntradaEspera(String nome, String tel, String pessoas, String prio, String amb) {
        Map<String, String> entrada = new java.util.LinkedHashMap<>();
        entrada.put("id", String.valueOf(contadorId++));
        entrada.put("nome", nome);
        entrada.put("telefone", tel);
        entrada.put("pessoas", pessoas);
        entrada.put("prioridade", prio);
        entrada.put("ambiente", amb);
        entrada.put("horario", java.time.LocalDateTime.now().minusMinutes((int)(Math.random() * 40)).format(FORMATTER));
        listaEspera.add(entrada);
    }

    // 5 campos de entrada de dados requisitados:
    private JTextField txtNome;           // 1. Nome
    private JTextField txtTelefone;       // 2. Telefone
    private JTextField txtMesa;           // 3. Nº de Pessoas
    private JComboBox<String> cbPrioridade; // 4. Prioridade
    private JComboBox<String> cbAmbiente;   // 5. Ambiente Preferido

    private JTextField txtPesquisa;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblTempoMedio;
    private static int contadorId = 1;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ListaEsperaFrame() {
        setTitle("Coffee&Books - Lista de Espera (Coleção em Memória)");
        setSize(1080, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        // ── HEADER ──────────────────────────────────────────────────────────────
        JLabel lblHeader = new JLabel("🕐 Lista de Espera — Dados em Coleção (ArrayList)", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(lblHeader, BorderLayout.NORTH);

        JLabel lblSub = new JLabel("⚠️  Os dados desta tela são armazenados em uma coleção Java (ArrayList) e não são persistidos no banco de dados.", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblSub.setForeground(new Color(200, 150, 80));
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(lblHeader, BorderLayout.NORTH);
        northPanel.add(lblSub, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ── SPLIT PANE ──────────────────────────────────────────────────────────
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);

        // ── FORM (LEFT) ─────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.COLOR_SECONDARY());
        formPanel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Nome do Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lNome = new JLabel("Nome do Cliente:");
        lNome.setForeground(UIConstants.TEXT_COLOR());
        formPanel.add(lNome, gbc);
        txtNome = new JTextField(18);
        txtNome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtNome, gbc);

        // 2. Telefone
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lTel = new JLabel("Telefone:");
        lTel.setForeground(UIConstants.TEXT_COLOR());
        formPanel.add(lTel, gbc);
        txtTelefone = new JTextField(14);
        txtTelefone.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtTelefone, gbc);

        // 3. Número de Pessoas / Mesa
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lMesa = new JLabel("Nº de Pessoas:");
        lMesa.setForeground(UIConstants.TEXT_COLOR());
        formPanel.add(lMesa, gbc);
        txtMesa = new JTextField(5);
        txtMesa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(txtMesa, gbc);

        // 4. Prioridade
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lPrio = new JLabel("Prioridade:");
        lPrio.setForeground(UIConstants.TEXT_COLOR());
        formPanel.add(lPrio, gbc);
        cbPrioridade = new JComboBox<>(new String[]{"Normal", "Preferencial (Idoso/PCD)", "VIP / Reserva"});
        cbPrioridade.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(cbPrioridade, gbc);

        // 5. Ambiente Preferido
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lAmb = new JLabel("Ambiente:");
        lAmb.setForeground(UIConstants.TEXT_COLOR());
        formPanel.add(lAmb, gbc);
        cbAmbiente = new JComboBox<>(new String[]{"Salão Principal", "Jardim das Letras", "Mezanino Cultural", "Balcão do Café"});
        cbAmbiente.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; formPanel.add(cbAmbiente, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnPanel.setOpaque(false);

        JButton btnAdicionar = criarBotao("➕ Adicionar", UIConstants.COLOR_SUCCESS);
        JButton btnAlterar   = criarBotao("✏️ Alterar", UIConstants.COLOR_ACCENT());
        JButton btnChamar   = criarBotao("📣 Chamar Próximo", new Color(30, 100, 200));
        JButton btnRemover  = criarBotao("🗑 Remover", UIConstants.COLOR_ALERT);
        JButton btnLimpar   = criarBotao("🔄 Limpar", UIConstants.COLOR_ACCENT().darker());

        btnPanel.add(btnAdicionar);
        btnPanel.add(btnAlterar);
        btnPanel.add(btnChamar);
        btnPanel.add(btnRemover);
        btnPanel.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Info sobre a Collection
        JTextArea txtInfo = new JTextArea(
            "📌 IMPLEMENTAÇÃO TÉCNICA:\n\n" +
            "Coleção utilizada:\n" +
            "  ArrayList<Map<String, String>>\n\n" +
            "Cada entrada da lista é um\n" +
            "LinkedHashMap contendo 5 campos:\n" +
            "  1. Nome, 2. Telefone,\n" +
            "  3. Pessoas, 4. Prioridade,\n" +
            "  5. Ambiente Preferido.\n\n" +
            "Dados em memória RAM. Ao fechar\n" +
            "a janela, são perdidos."
        );
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtInfo.setBackground(UIConstants.COLOR_PRIMARY());
        txtInfo.setForeground(new Color(160, 200, 160));
        txtInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 160, 80)),
            "Estrutura de Dados"
        ));
        gbc.gridy = 6;
        formPanel.add(txtInfo, gbc);

        splitPane.setLeftComponent(formPanel);

        // ── TABLE (RIGHT) ────────────────────────────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setOpaque(false);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 5));
        searchPanel.add(new JLabel("🔍 Pesquisar:"), BorderLayout.WEST);
        txtPesquisa = new JTextField();
        txtPesquisa.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPesquisa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        searchPanel.add(txtPesquisa, BorderLayout.CENTER);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // JTable columns
        String[] cols = {"#", "Nome", "Telefone", "Pessoas", "Prioridade", "Ambiente", "Entrada"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.setRowHeight(26);
        tabela.getTableHeader().setBackground(UIConstants.COLOR_ACCENT());
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        // Renderer: colorir por prioridade
        tabela.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String prio = t.getValueAt(row, 4) != null ? t.getValueAt(row, 4).toString() : "";
                if (!sel) {
                    if (prio.contains("VIP"))            { c.setBackground(new Color(255, 245, 200)); c.setForeground(new Color(150, 100, 0)); }
                    else if (prio.contains("Preferencial")) { c.setBackground(new Color(210, 240, 255)); c.setForeground(new Color(0, 80, 160)); }
                    else                                 { c.setBackground(t.getBackground()); c.setForeground(t.getForeground()); }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Aguardando (ArrayList em Memória)"));
        rightPanel.add(scroll, BorderLayout.CENTER);

        // Summary bar
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        summaryPanel.setBackground(UIConstants.COLOR_SECONDARY());
        lblTotal      = new JLabel("Total na fila: 0");
        lblTempoMedio = new JLabel("ArrayList.size(): 0");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTempoMedio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblTempoMedio.setForeground(new Color(80, 160, 80));
        summaryPanel.add(lblTotal);
        summaryPanel.add(lblTempoMedio);
        rightPanel.add(summaryPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // ── ACTION LISTENERS ─────────────────────────────────────────────────────
        btnAdicionar.addActionListener(e -> adicionarNaFila());
        btnAlterar.addActionListener(e -> alterarNaFila());
        btnChamar.addActionListener(e -> chamarProximo());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnLimpar.addActionListener(e -> limparFormulario());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row >= 0) {
                txtNome.setText(tableModel.getValueAt(row, 1).toString());
                txtTelefone.setText(tableModel.getValueAt(row, 2).toString());
                txtMesa.setText(tableModel.getValueAt(row, 3).toString());
                cbPrioridade.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                cbAmbiente.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            }
        });
    }

    /** Adiciona um novo cliente à ArrayList (sem nenhuma operação de banco de dados) */
    private void adicionarNaFila() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String pessoasStr = txtMesa.getText().trim();
        String prioridade = (String) cbPrioridade.getSelectedItem();
        String ambiente = (String) cbAmbiente.getSelectedItem();

        if (nome.isEmpty() || telefone.isEmpty() || pessoasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos:\n• Nome\n• Telefone\n• Nº de Pessoas",
                "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int pessoas = Integer.parseInt(pessoasStr);
            if (pessoas < 1 || pessoas > 20) {
                JOptionPane.showMessageDialog(this, "Número de pessoas deve estar entre 1 e 20.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // === Cria um LinkedHashMap e adiciona à ArrayList (sem banco) ===
            Map<String, String> entrada = new LinkedHashMap<>();
            entrada.put("id", String.valueOf(contadorId++));
            entrada.put("nome", nome);
            entrada.put("telefone", telefone);
            entrada.put("pessoas", String.valueOf(pessoas));
            entrada.put("prioridade", prioridade);
            entrada.put("ambiente", ambiente);
            entrada.put("horario", LocalDateTime.now().format(FORMATTER));

            listaEspera.add(entrada);  // <-- OPERAÇÃO NA COLLECTION

            atualizarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this,
                "✅ " + nome + " adicionado(a) à fila de espera!\n" +
                "Posição na fila: " + listaEspera.size() + "\n" +
                "ArrayList.size() = " + listaEspera.size(),
                "Adicionado à Fila", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de pessoas deve ser um valor inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Remove o primeiro da fila (operação na ArrayList) */
    private void chamarProximo() {
        if (listaEspera.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A fila de espera está vazia!", "Fila Vazia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Map<String, String> proximo = listaEspera.remove(0);  // <-- remove(0) da ArrayList
        atualizarTabela();
        JOptionPane.showMessageDialog(this,
            "📣 Chamando: " + proximo.get("nome") + "\n" +
            "Telefone: " + proximo.get("telefone") + "\n" +
            "Pessoas: " + proximo.get("pessoas") + "\n" +
            "Ambiente: " + proximo.get("ambiente") + "\n\n" +
            "Removido da ArrayList. Tamanho atual: " + listaEspera.size(),
            "Próximo da Fila!", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Remove o item selecionado na tabela da ArrayList */
    private void removerSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para remover.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Identificar o item na collection pelo ID
        String idAlvo = tableModel.getValueAt(row, 0).toString();
        boolean removido = listaEspera.removeIf(m -> m.get("id").equals(idAlvo));

        if (removido) {
            atualizarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this,
                "✅ Cliente removido da fila (ArrayList).\nTamanho atual: " + listaEspera.size(),
                "Removido", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Filtra a exibição da tabela pela pesquisa (sem tocar na ArrayList original) */
    private void filtrar() {
        String filtro = txtPesquisa.getText().toLowerCase().trim();
        tableModel.setRowCount(0);

        // Itera sobre a ArrayList e filtra
        for (Map<String, String> item : listaEspera) {
            if (filtro.isEmpty()
                    || item.get("nome").toLowerCase().contains(filtro)
                    || item.get("telefone").toLowerCase().contains(filtro)
                    || item.get("ambiente").toLowerCase().contains(filtro)) {
                tableModel.addRow(new Object[]{
                    item.get("id"),
                    item.get("nome"),
                    item.get("telefone"),
                    item.get("pessoas"),
                    item.get("prioridade"),
                    item.get("ambiente"),
                    item.get("horario")
                });
            }
        }
        atualizarSummary();
    }

    /** Repopula a tabela com todos os dados da ArrayList */
    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (Map<String, String> item : listaEspera) {
            tableModel.addRow(new Object[]{
                item.get("id"),
                item.get("nome"),
                item.get("telefone"),
                item.get("pessoas"),
                item.get("prioridade"),
                item.get("ambiente"),
                item.get("horario")
            });
        }
        atualizarSummary();
    }

    private void atualizarSummary() {
        lblTotal.setText("Total na fila: " + listaEspera.size() + " clientes");
        lblTempoMedio.setText("ArrayList.size() = " + listaEspera.size());
    }

    private void alterarNaFila() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para alterar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idAlvo = tableModel.getValueAt(row, 0).toString();
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String pessoasStr = txtMesa.getText().trim();
        String prioridade = (String) cbPrioridade.getSelectedItem();
        String ambiente = (String) cbAmbiente.getSelectedItem();

        if (nome.isEmpty() || telefone.isEmpty() || pessoasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos:\n• Nome\n• Telefone\n• Nº de Pessoas",
                "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int pessoas = Integer.parseInt(pessoasStr);
            if (pessoas < 1 || pessoas > 20) {
                JOptionPane.showMessageDialog(this, "Número de pessoas deve estar entre 1 e 20.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Achar o item na collection ArrayList e atualizar
            for (Map<String, String> item : listaEspera) {
                if (item.get("id").equals(idAlvo)) {
                    item.put("nome", nome);
                    item.put("telefone", telefone);
                    item.put("pessoas", String.valueOf(pessoas));
                    item.put("prioridade", prioridade);
                    item.put("ambiente", ambiente);
                    break;
                }
            }

            atualizarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this,
                "✅ Cadastro alterado na coleção de memória com sucesso!",
                "Alterado", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de pessoas deve ser um valor inteiro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtMesa.setText("");
        cbPrioridade.setSelectedIndex(0);
        cbAmbiente.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return btn;
    }
}
