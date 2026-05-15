package util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

public class AssetLoader {
    
    public static ImageIcon getLogo(int width, int height) {
        try {
            // Trying to load from resources first (standard Maven way)
            URL imgUrl = AssetLoader.class.getResource("/assets/logo.png");
            if (imgUrl == null) {
                // Fallback for local development if not in resources yet
                imgUrl = new java.io.File("src/assets/logo.png").toURI().toURL();
            }
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Erro ao carregar o logo: " + e.getMessage());
            return null;
        }
    }
}
