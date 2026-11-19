import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * The window. It only talks to the Algorithm interface, so it works with any algorithm
 * without knowing which one it is (polymorphism). All steps are recorded first, then replayed,
 * which is what makes the Previous button possible.
 */
public class VisualizerFrame extends JFrame {
    private static final Map<Role, Color> COLOURS = new EnumMap<Role, Color>(Role.class);
    static {
        COLOURS.put(Role.NORMAL,  new Color(0x4c78a8));
        COLOURS.put(Role.COMPARE, new Color(0xf2cf5b));
        COLOURS.put(Role.SWAP,    new Color(0xe45756));
        COLOURS.put(Role.PIVOT,   new Color(0xb279a2));
        COLOURS.put(Role.SORTED,  new Color(0x54a24b));
        COLOURS.put(Role.FOUND,   new Color(0x54a24b));
        COLOURS.put(Role.RANGE,   new Color(0x9ecae9));
    }

    private final ArrayModel model = new ArrayModel();
    private final ArrayPanel panel = new ArrayPanel();
    final JComboBox<String> algoBox = new JComboBox<String>(AlgorithmFactory.names());
    private final JComboBox<ArrayModel.Kind> kindBox = new JComboBox<ArrayModel.Kind>(ArrayModel.Kind.values());
    private final JSlider sizeSlider = new JSlider(5, 100, 30);
    private final JSlider speedSlider = new JSlider(1, 100, 40);
    private final JTextField targetField = new JTextField(4);
    private final JButton startButton = new JButton("Start");
    private final JLabel complexityLabel = new JLabel(" ");
    private final JLabel statsLabel = new JLabel(" ");
    private final JLabel noteLabel = new JLabel(" ");
    private final javax.swing.Timer timer;

    private List<Step> steps;      // every frame of the current run
    private int index = -1;        // frame being shown (-1 = the untouched array)
    private int[] startData;

    public VisualizerFrame() {
        super("Algorithm Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        timer = new javax.swing.Timer(delay(), e -> next());
        buildLayout();
        wireEvents();
        model.generate(ArrayModel.Kind.RANDOM, sizeSlider.getValue());
        reset();
        pack();
        setLocationRelativeTo(null);
    }

    // ---------- layout ----------
    private void buildLayout() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        controls.add(algoBox);
        controls.add(kindBox);
        controls.add(new JLabel("Size"));
        controls.add(sizeSlider);
        controls.add(new JLabel("Speed"));
        controls.add(speedSlider);
        controls.add(new JLabel("Target:"));
        controls.add(targetField);
        controls.add(startButton);
        controls.add(button("Prev", e -> previous()));
        controls.add(button("Next", e -> stepForward()));
        controls.add(button("Reset", e -> reset()));
        controls.add(button("New array", e -> newArray()));

        complexityLabel.setFont(complexityLabel.getFont().deriveFont(Font.BOLD));
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(complexityLabel);
        info.add(statsLabel);
        info.add(noteLabel);

        add(controls, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);
    }

    private JButton button(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.addActionListener(action);
        return b;
    }

    private void wireEvents() {
        algoBox.addActionListener(e -> reset());
        kindBox.addActionListener(e -> newArray());
        sizeSlider.addChangeListener(e -> { if (!sizeSlider.getValueIsAdjusting()) newArray(); });
        speedSlider.addChangeListener(e -> timer.setDelay(delay()));
        startButton.addActionListener(e -> startPause());
    }

    private int delay() {
        return Math.max(1, 600 / speedSlider.getValue());
    }

    // ---------- controls ----------
    void newArray() {
        model.generate((ArrayModel.Kind) kindBox.getSelectedItem(), sizeSlider.getValue());
        reset();
    }

    /** Re-runs the chosen algorithm on the current array and shows the starting frame. */
    void reset() {
        timer.stop();
        Algorithm algo = AlgorithmFactory.create((String) algoBox.getSelectedItem());
        startData = model.copy();
        if (algo.needsSorted()) Arrays.sort(startData);
        targetField.setEnabled(algo.needsTarget());
        steps = algo.run(startData, chooseTarget(algo));
        index = -1;
        complexityLabel.setText(algo.getName() + ":  " + algo.complexity());
        startButton.setText("Start");
        showStart();
    }

    private int chooseTarget(Algorithm algo) {
        if (!algo.needsTarget()) return 0;
        try {
            return Integer.parseInt(targetField.getText().trim());
        } catch (NumberFormatException e) {
            return startData[new Random().nextInt(startData.length)];   // pick a value that is present
        }
    }

    void startPause() {
        if (timer.isRunning()) {
            timer.stop();
            startButton.setText("Resume");
            return;
        }
        if (index >= steps.size() - 1) reset();      // finished: start over
        startButton.setText("Pause");
        timer.start();
    }

    void stepForward() {
        timer.stop();
        startButton.setText("Resume");
        if (index < steps.size() - 1) {
            index++;
            showStep();
        }
    }

    void previous() {
        timer.stop();
        startButton.setText("Resume");
        if (index >= 0) index--;
        if (index < 0) showStart();
        else showStep();
    }

    /** Called by the timer. */
    private void next() {
        if (index < steps.size() - 1) {
            index++;
            showStep();
        }
        if (index >= steps.size() - 1) {
            timer.stop();
            startButton.setText("Start");
        }
    }

    // ---------- display ----------
    private void showStart() {
        Role[] normal = new Role[startData.length];
        Arrays.fill(normal, Role.NORMAL);
        panel.show(startData, normal);
        statsLabel.setText("Comparisons: 0    Writes: 0    Step 0 / " + steps.size());
        noteLabel.setText("Press Start");
    }

    private void showStep() {
        Step s = steps.get(index);
        panel.show(s.getData(), s.getRoles());
        statsLabel.setText("Comparisons: " + s.getComparisons() + "    Writes: " + s.getWrites()
                + "    Step " + (index + 1) + " / " + steps.size());
        noteLabel.setText(s.getNote());
    }

    /** Draws the bars. */
    static class ArrayPanel extends JPanel {
        private int[] data = new int[0];
        private Role[] roles = new Role[0];

        ArrayPanel() {
            setPreferredSize(new Dimension(900, 380));
            setBackground(Color.WHITE);
        }

        void show(int[] d, Role[] r) {
            data = d;
            roles = r;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int n = data.length;
            if (n == 0) return;
            int max = 1;
            for (int v : data) max = Math.max(max, v);
            double barWidth = (double) getWidth() / n;
            int usable = getHeight() - 40;
            for (int i = 0; i < n; i++) {
                int h = (int) ((double) data[i] / max * usable);
                int x = (int) (i * barWidth);
                g.setColor(COLOURS.get(roles[i]));
                g.fillRect(x + 1, getHeight() - h - 20, Math.max(1, (int) barWidth - 2), h);
                if (n <= 30) {
                    g.setColor(Color.DARK_GRAY);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    g.drawString(String.valueOf(data[i]), x + (int) barWidth / 2 - 5, getHeight() - 6);
                }
            }
        }
    }
}
