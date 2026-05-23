package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import model.Usuario;
import view.*;

public class ScreenshotTaker {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO CAPTURA DE TELAS ===");
        
        // 1. Inicializar Look and Feel do FlatLaf
        UIConstants.initLookAndFeel();
        
        // 2. Mock do usuário logado admin no SessionManager para evitar redirecionamento e liberar telas restritas
        SessionManager.setUsuario(new Usuario(1, "admin", "admin123", "ADMIN", new java.util.Date()));
        
        // Criar pasta de destino para as imagens
        File outputDir = new File("telas_print");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // 3. Capturar cada uma das 17 telas
        captureFrame(new LoginFrame(), "01_LoginFrame.png");
        captureFrame(new MainFrame(), "02_MainFrame.png");
        captureFrame(new LivroForm(), "03_LivroForm.png");
        captureFrame(new ClienteForm(), "04_ClienteForm.png");
        captureFrame(new ListaEsperaFrame(), "05_ListaEsperaFrame.png");
        captureFrame(new ConsultaAcervoFrame(), "06_ConsultaAcervoFrame.png");
        captureFrame(new GeneroForm(), "07_GeneroForm.png");
        captureFrame(new ProdutoConsumoForm(), "08_ProdutoConsumoForm.png");
        captureFrame(new IngredienteForm(), "09_IngredienteForm.png");
        captureFrame(new ImportacaoDadosFrame(), "10_ImportacaoDadosFrame.png");
        captureFrame(new ReservaForm(), "11_ReservaForm.png");
        captureFrame(new ComandaFrame(), "12_ComandaFrame.png");
        captureFrame(new PDVFrame(), "13_PDVFrame.png");
        captureFrame(new EventoFrame(), "14_EventoFrame.png");
        captureFrame(new FinancialDashboardFrame(), "15_FinancialDashboardFrame.png");
        captureFrame(new ProfileFrame(), "16_ProfileFrame.png");
        captureFrame(new ChangePasswordFrame(), "17_ChangePasswordFrame.png");
        
        System.out.println("=== CAPTURA DE TELAS CONCLUIDA COM SUCESSO ===");
        System.exit(0);
    }
    
    private static void captureFrame(JFrame frame, String fileName) {
        try {
            System.out.println("Capturando: " + frame.getClass().getSimpleName() + " -> " + fileName);
            
            // Configurar propriedades essenciais do Frame
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Tornar visível e forçar a validação e renderização
            frame.addNotify();
            frame.validate();
            
            // Obter dimensões do Frame
            int width = frame.getWidth();
            int height = frame.getHeight();
            if (width <= 0) width = 800;
            if (height <= 0) height = 600;
            
            frame.setSize(width, height);
            frame.doLayout();
            
            // Criar imagem buffered e pintar o frame nela
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            
            // Habilitar anti-aliasing para prints de alta qualidade
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            frame.paint(g);
            g.dispose();
            
            // Salvar no arquivo de destino
            File fileDest = new File("telas_print", fileName);
            ImageIO.write(image, "png", fileDest);
            System.out.println("Salvo com sucesso: " + fileDest.getAbsolutePath());
            
            // Descartar o frame
            frame.dispose();
        } catch (Exception e) {
            System.err.println("Erro ao capturar o frame " + frame.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
