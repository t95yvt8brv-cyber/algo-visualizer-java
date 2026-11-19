package visualizer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.AlgorithmFactory;
import visualizer.model.ArrayModel;
import visualizer.model.Role;
import visualizer.model.Step;

/**
 * The window. It only talks to the Algorithm type, so it works with any algorithm
 * without knowing which one it is (polymorphism). All steps are recorded first, then replayed,
 * which is what makes the Previous button possible.
 */
public class VisualizerFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final transient ArrayModel model = new ArrayModel();
    private final ArrayPanel panel = new ArrayPanel();
    private final JComboBox<String> algoBox = new JComboBox<>(AlgorithmFactory.names());
    private final JComboBox<ArrayModel.Kind> kindBox = new JComboBox<>(ArrayModel.Kind.values());
    private final JSlider sizeSlider = new JSlider(5, 100, 30);
    private final JSlider speedSlider = new JSlider(1, 100, 40);
    private final JTextField targetField = new JTextField(4);
    private final JButton startButton = new JButton("Start");
    private final JLabel complexityLabel = new JLabel(" ");
    private final JLabel statsLabel = new JLabel(" ");
    private final JLabel noteLabel = new JLabel(" ");
    private final Timer timer;

    private transient List<Step> steps;    // every frame of the current run
    private int index = -1;                // frame being shown (-1 = the untouched array)
    private int[] startData;
    private Integer target;                // null for sorts

    public VisualizerFrame() {
        super("Algorithm Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        timer = new Timer(delay(), e -> next());
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

    private static JButton button(String text, ActionListener action) {
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
        // Re-record as soon as the target changes, so Start always searches for what is typed.
        targetField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { SwingUtilities.invokeLater(VisualizerFrame.this::reset); }
            @Override public void removeUpdate(DocumentEvent e)  { SwingUtilities.invokeLater(VisualizerFrame.this::reset); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
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
        target = algo.needsTarget() ? chooseTarget() : null;
        steps = algo.run(startData, target == null ? 0 : target);
        index = -1;
        complexityLabel.setText(algo.getName() + ":  " + algo.complexity());
        startButton.setText("Start");
        showStart();
    }

    private int chooseTarget() {
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
        panel.show(new Step(startData, normal, "", 0, 0));
        statsLabel.setText("Comparisons: 0    Writes: 0    Step 0 / " + steps.size());
        noteLabel.setText(target == null ? "Press Start" : "Press Start to search for " + target);
    }

    private void showStep() {
        Step s = steps.get(index);
        panel.show(s);
        statsLabel.setText("Comparisons: " + s.getComparisons() + "    Writes: " + s.getWrites()
                + "    Step " + (index + 1) + " / " + steps.size());
        noteLabel.setText(s.getNote());
    }

    // ---------- used by tools that drive the window (e.g. screenshots) ----------
    void select(String algorithmName) { algoBox.setSelectedItem(algorithmName); }
}
