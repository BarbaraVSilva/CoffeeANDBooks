package view;

import javax.swing.*;
import java.awt.*;
import model.GeneroLivro;
import model.Livro;
import exception.PrecoInvalidoSeboException;
import util.UIConstants;

public class LivroForm extends JFrame {
    private JTextField txtTitulo, txtAutor, txtPreco;
    private JComboBox<GeneroLivro> cbGenero;
    private JComboBox<String> cbCondicao;
    private JSpinner spEstoque;
    private String currentImagePath = "";
    private JLabel lblImagePreview;

    public LivroForm() {
        setTitle("Gestão de Acervo - Livros & Relíquias");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.COLOR_PRIMARY());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.COLOR_SECONDARY());
        mainPanel.setBorder(UIConstants.getPanelBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel lblHeader = new JLabel("📚 Cadastro de Obras", SwingConstants.CENTER);
        lblHeader.setFont(UIConstants.FONT_TITLE);
        lblHeader.setForeground(UIConstants.COLOR_ACCENT());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblHeader, gbc);
        gbc.gridwidth = 1;

        // Fields
        gbc.gridy = 1; gbc.gridx = 0; mainPanel.add(new JLabel("Título do Livro:"), gbc);
        txtTitulo = new JTextField(20); gbc.gridx = 1; mainPanel.add(txtTitulo, gbc);

        gbc.gridy = 2; gbc.gridx = 0; mainPanel.add(new JLabel("Autor / Escritor:"), gbc);
        txtAutor = new JTextField(20); gbc.gridx = 1; mainPanel.add(txtAutor, gbc);

        gbc.gridy = 3; gbc.gridx = 0; mainPanel.add(new JLabel("Gênero Literário:"), gbc);
        cbGenero = new JComboBox<>();
        carregarGeneros();
        gbc.gridx = 1; mainPanel.add(cbGenero, gbc);

        gbc.gridy = 4; gbc.gridx = 0; mainPanel.add(new JLabel("Condição:"), gbc);
        cbCondicao = new JComboBox<>(new String[]{"Novo", "Usado (Excelente)", "Usado (Marcas de Tempo)"});
        gbc.gridx = 1; mainPanel.add(cbCondicao, gbc);

        gbc.gridy = 5; gbc.gridx = 0; mainPanel.add(new JLabel("Preço de Venda (R$):"), gbc);
        txtPreco = new JTextField(10); gbc.gridx = 1; mainPanel.add(txtPreco, gbc);

        gbc.gridy = 6; gbc.gridx = 0; mainPanel.add(new JLabel("Estoque Atual:"), gbc);
        spEstoque = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        gbc.gridx = 1; mainPanel.add(spEstoque, gbc);

        gbc.gridy = 7; gbc.gridx = 0; mainPanel.add(new JLabel("Foto da Capa:"), gbc);
        JButton btnUpload = new JButton("Carregar Foto");
        btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentImagePath = fc.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this, "Imagem selecionada: " + fc.getSelectedFile().getName());
            }
        });
        gbc.gridx = 1; mainPanel.add(btnUpload, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton btnSalvar = createStyledButton("Salvar Obra");
        btnSalvar.addActionListener(e -> salvar());
        btnPanel.add(btnSalvar);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void carregarGeneros() {
        cbGenero.addItem(new GeneroLivro(1, "Suspense", "Estante B"));
        cbGenero.addItem(new GeneroLivro(2, "Ficção Científica", "Estante A"));
    }

    private void salvar() {
        try {
            String titulo = txtTitulo.getText();
            String condicao = (String) cbCondicao.getSelectedItem();
            double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
            
            Livro.validarPreco(condicao, preco);
            
            JOptionPane.showMessageDialog(this, "Obra '" + titulo + "' salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (PrecoInvalidoSeboException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Regra de Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UIConstants.COLOR_ACCENT());
        btn.setForeground(Color.WHITE);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
