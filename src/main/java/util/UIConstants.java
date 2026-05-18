package util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

public class UIConstants {
    private static boolean darkTheme = false;

    public static void initLookAndFeel() {
        try {
            if (darkTheme) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Palette Colors (Dynamic)
    public static Color COLOR_PRIMARY() { return darkTheme ? new Color(28, 28, 30) : new Color(252, 250, 248); }
    public static Color COLOR_SECONDARY() { return darkTheme ? new Color(44, 44, 46) : new Color(245, 240, 235); }
    public static Color COLOR_ACCENT() { return darkTheme ? new Color(194, 153, 115) : new Color(101, 67, 33); }
    public static Color TEXT_COLOR() { return darkTheme ? new Color(230, 230, 230) : new Color(44, 33, 24); }
    
    public static final Color COLOR_DARK_BROWN = new Color(74, 53, 37);
    public static final Color COLOR_SUCCESS = new Color(76, 175, 80);
    public static final Color COLOR_ALERT = new Color(229, 57, 53);

    // Fonts
    public static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 28);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_FOOTER = new Font("Monospaced", Font.PLAIN, 12);

    public static void setDarkTheme(boolean dark) {
        darkTheme = dark;
        try {
            if (dark) {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } else {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            }
            com.formdev.flatlaf.FlatLaf.updateUI();
            
            // Loop through all active frames and update them
            for (java.awt.Frame f : java.awt.Frame.getFrames()) {
                javax.swing.SwingUtilities.updateComponentTreeUI(f);
                
                // Refresh content pane backgrounds
                if (f instanceof javax.swing.JFrame) {
                    ((javax.swing.JFrame) f).getContentPane().setBackground(COLOR_PRIMARY());
                }
                
                // If it is the MainFrame, rebuild it to apply the dynamic design system fully
                if (f.getClass().getName().endsWith("MainFrame")) {
                    f.dispose();
                    // Open a new MainFrame dynamically to avoid compile circularities
                    javax.swing.JFrame newMain = (javax.swing.JFrame) Class.forName("view.MainFrame").getDeclaredConstructor().newInstance();
                    newMain.setVisible(true);
                } else {
                    f.repaint();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    // Borders & Styles
    public static Border getRoundedBorder(Color color) {
        return BorderFactory.createLineBorder(color, 1, true);
    }
    
    public static Border getPanelBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ACCENT(), 1),
            new EmptyBorder(15, 15, 15, 15)
        );
    }
}
