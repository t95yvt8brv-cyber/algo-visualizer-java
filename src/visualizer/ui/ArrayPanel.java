package visualizer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JPanel;
import visualizer.model.Role;
import visualizer.model.Step;

/** Draws one Step as bars. Knows nothing about algorithms, only Steps and Roles. */
public class ArrayPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
    private static final Map<Role, Color> COLOURS = new EnumMap<>(Role.class);
    static {
        COLOURS.put(Role.NORMAL,  new Color(0x4c78a8));
        COLOURS.put(Role.COMPARE, new Color(0xf2cf5b));
        COLOURS.put(Role.SWAP,    new Color(0xe45756));
        COLOURS.put(Role.PIVOT,   new Color(0xb279a2));
        COLOURS.put(Role.SORTED,  new Color(0x54a24b));
        COLOURS.put(Role.FOUND,   new Color(0x54a24b));
        COLOURS.put(Role.RANGE,   new Color(0x9ecae9));
    }

    private transient Step step;

    public ArrayPanel(int preferredHeight) {
        setPreferredSize(new Dimension(720, preferredHeight));
        setBackground(Color.WHITE);
    }

    public void show(Step s) {
        step = s;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (step == null || step.size() == 0) return;
        int n = step.size();
        int max = 1;
        for (int i = 0; i < n; i++) max = Math.max(max, step.valueAt(i));
        double barWidth = (double) getWidth() / n;
        int usable = getHeight() - 40;
        g.setFont(LABEL_FONT);
        for (int i = 0; i < n; i++) {
            int h = (int) ((double) step.valueAt(i) / max * usable);
            int x = (int) (i * barWidth);
            g.setColor(COLOURS.get(step.roleAt(i)));
            g.fillRect(x + 1, getHeight() - h - 20, Math.max(1, (int) barWidth - 2), h);
            if (n <= 30) {
                String label = String.valueOf(step.valueAt(i));
                int w = g.getFontMetrics().stringWidth(label);
                g.setColor(Color.DARK_GRAY);
                g.drawString(label, x + (int) (barWidth - w) / 2, getHeight() - 6);
            }
        }
    }
}
