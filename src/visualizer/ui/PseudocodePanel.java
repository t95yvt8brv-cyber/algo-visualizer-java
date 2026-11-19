package visualizer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Shows an algorithm's pseudocode and highlights the line the current frame belongs to. */
public class PseudocodePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Color HIGHLIGHT = new Color(0xfff1b8);

    private final DefaultListModel<String> lines = new DefaultListModel<>();
    private final JList<String> list = new JList<>(lines);
    private final JLabel cell = new JLabel();       // one label reused to draw every line
    private int current;                            // highlighted line, 1-based (0 = none)

    public PseudocodePanel() {
        super(new BorderLayout());
        list.setFocusable(false);
        list.setCellRenderer(this::render);
        cell.setOpaque(true);
        cell.setFont(MONO);
        cell.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(430, 10));
        add(new JLabel(" Pseudocode"), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private Component render(JList<? extends String> l, String value, int index, boolean selected, boolean focus) {
        boolean on = index + 1 == current;
        cell.setText(String.format("%2d  %s", index + 1, value));
        cell.setBackground(on ? HIGHLIGHT : Color.WHITE);
        cell.setForeground(on ? Color.BLACK : Color.DARK_GRAY);
        return cell;
    }

    public void setCode(List<String> code) {
        lines.clear();
        for (String line : code) lines.addElement(line);
        current = 0;
    }

    public void highlight(int line) {
        if (line == current) return;
        current = line;
        list.repaint();
        if (line > 0) list.ensureIndexIsVisible(line - 1);
    }
}
