package util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import model.Livro;
import dao.LivroDAO;

public class ReportUtil {
    public static void exportarAcervoTxt(String path) {
        List<Livro> livros = new LivroDAO().listar(null, "Todos");
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.println("============================================");
            out.println("   RELATÓRIO DE ACERVO - COFFEE&BOOKS");
            out.println("============================================");
            out.println(String.format("%-30s | %-20s | %-10s", "Título", "Autor", "Preço"));
            out.println("--------------------------------------------");
            for (Livro l : livros) {
                out.println(String.format("%-30s | %-20s | R$ %.2f", 
                    l.getTitulo(), l.getAutor(), l.getPrecoVenda()));
            }
            out.println("============================================");
            out.println("Total de obras: " + livros.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
