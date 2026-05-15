package util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class VisualDetails {
    
    public static Icon getCoffeeIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                
                // Cup
                g2.fill(new RoundRectangle2D.Double(x + size*0.2, y + size*0.4, size*0.5, size*0.4, 5, 5));
                // Handle
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Arc2D.Double(x + size*0.6, y + size*0.45, size*0.2, size*0.2, 270, 180, Arc2D.OPEN));
                // Steam
                g2.draw(new CubicCurve2D.Double(x+size*0.3, y+size*0.3, x+size*0.35, y+size*0.2, x+size*0.25, y+size*0.1, x+size*0.3, y+size*0.05));
                g2.draw(new CubicCurve2D.Double(x+size*0.5, y+size*0.3, x+size*0.55, y+size*0.2, x+size*0.45, y+size*0.1, x+size*0.5, y+size*0.05));
                
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon getBookIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                
                // Book shape
                g2.drawRect(x + 2, y + 2, size - 4, size - 4);
                g2.drawLine(x + size/2, y + 2, x + size/2, y + size - 2);
                // Lines for text
                g2.drawLine(x + 5, y + 6, x + size/2 - 3, y + 6);
                g2.drawLine(x + 5, y + 10, x + size/2 - 3, y + 10);
                g2.drawLine(x + size/2 + 3, y + 6, x + size - 5, y + 6);
                
                g2.dispose();
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
