package util;

import java.awt.*;

/**
 * FlowLayout successor that wraps components when they exceed the width.
 * This is particularly useful inside a JScrollPane where default FlowLayout
 * fails to wrap horizontally constrained elements vertically.
 */
public class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;

            if (targetWidth == 0) {
                Container parent = target.getParent();
                while (parent != null) {
                    if (parent.getSize().width > 0) {
                        targetWidth = parent.getSize().width;
                        break;
                    }
                    parent = parent.getParent();
                }
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }
            }

            Insets insets = target.getInsets();
            int hgap = getHgap();
            int vgap = getVgap();
            int maxwidth = targetWidth - (insets.left + insets.right + hgap * 2);

            int nmembers = target.getComponentCount();
            int x = 0;
            int y = insets.top + vgap;
            int rowHeight = 0;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    if (x == 0) {
                        x = d.width;
                        rowHeight = d.height;
                    } else {
                        if (x + hgap + d.width <= maxwidth) {
                            x += hgap + d.width;
                            rowHeight = Math.max(rowHeight, d.height);
                        } else {
                            x = d.width;
                            y += vgap + rowHeight;
                            rowHeight = d.height;
                        }
                    }
                }
            }
            y += vgap + rowHeight + insets.bottom;
            return new Dimension(targetWidth, y);
        }
    }
}
